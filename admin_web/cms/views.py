"""English-only Admin CMS views and Public Published Content Read-Only API."""

from pathlib import Path
from django.conf import settings
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.core.exceptions import PermissionDenied, ValidationError
from django.http import JsonResponse
from django.shortcuts import get_object_or_404, redirect, render
from django.views.decorators.http import require_GET, require_POST

from .auth_security import (
    check_login_rate_limit,
    clear_failed_logins,
    cms_role_required,
    get_client_ip,
    rate_limit_exceeded_response,
    record_failed_login,
)
from .constants import LIVESTOCK_SECTIONS, WORKFLOW_STATUSES
from .media_pipeline import process_uploaded_image
from .models import (
    ContentAuditLog,
    CropEntry,
    HelplineContactEntry,
    LivestockEntry,
    LivestockSectionContent,
    MediaAsset,
)
from .workflow import get_user_role, has_cms_permission, transition_entry_status


def cms_login(request):
    if request.user.is_authenticated and get_user_role(request.user):
        return redirect("cms:dashboard")

    error_message = None
    if request.method == "POST":
        if not check_login_rate_limit(request):
            return rate_limit_exceeded_response()

        username = request.POST.get("username", "").strip()
        password = request.POST.get("password", "")
        user = authenticate(request, username=username, password=password)
        if user is not None and get_user_role(user):
            clear_failed_logins(request)
            login(request, user)
            ContentAuditLog.objects.create(
                actor=user,
                action="admin_login",
                target_type="session",
                target_id=str(user.pk),
                ip_address=get_client_ip(request),
            )
            return redirect("cms:dashboard")

        record_failed_login(request)
        error_message = "Invalid credentials or account lacks an active CMS role."

    return render(request, "cms/login.html", {"error_message": error_message})


@require_POST
def cms_logout(request):
    if request.user.is_authenticated:
        ContentAuditLog.objects.create(
            actor=request.user,
            action="admin_logout",
            target_type="session",
            target_id=str(request.user.pk),
            ip_address=get_client_ip(request),
        )
    logout(request)
    return redirect("cms:login")


@cms_role_required()
def dashboard(request):
    context = {
        "role": get_user_role(request.user),
        "crop_total": CropEntry.objects.count(),
        "crop_published": CropEntry.objects.filter(is_published=True).count(),
        "crop_review": CropEntry.objects.filter(review_status="in_review").count(),
        "livestock_total": LivestockEntry.objects.count(),
        "livestock_published": LivestockEntry.objects.filter(is_published=True).count(),
        "livestock_review": LivestockEntry.objects.filter(review_status="in_review").count(),
        "media_total": MediaAsset.objects.count(),
        "helpline_verified": HelplineContactEntry.objects.filter(is_verified=True).count(),
        "recent_logs": ContentAuditLog.objects.select_related("actor")[:12],
    }
    return render(request, "cms/dashboard.html", context)


@cms_role_required("edit_content")
def crops_list(request):
    status_filter = request.GET.get("status", "")
    crops = CropEntry.objects.all().order_by("-updated_at")
    if status_filter:
        crops = crops.filter(review_status=status_filter)
    return render(
        request,
        "cms/crops_list.html",
        {
            "crops": crops,
            "statuses": WORKFLOW_STATUSES,
            "status_filter": status_filter,
            "can_approve": has_cms_permission(request.user, "approve_content"),
            "can_publish": has_cms_permission(request.user, "publish_content"),
        },
    )


@cms_role_required("create_draft")
def crop_create(request):
    if request.method == "POST":
        crop = CropEntry.objects.create(
            category_id=request.POST.get("category_id", "cat-cereal").strip(),
            name_ne=request.POST.get("name_ne", "").strip(),
            name_en=request.POST.get("name_en", "").strip(),
            scientific_name=request.POST.get("scientific_name", "").strip(),
            description_ne=request.POST.get("description_ne", "").strip(),
            varieties=request.POST.get("varieties", "").strip(),
            planting_season_ne=request.POST.get("planting_season_ne", "").strip(),
            altitude_summary_ne=request.POST.get("altitude_summary_ne", "").strip(),
            soil_types_ne=request.POST.get("soil_types_ne", "").strip(),
            source_reference=request.POST.get("source_reference", "").strip(),
            thumbnail_url=request.POST.get("thumbnail_url", "").strip(),
            review_status="draft",
            is_published=False,
            created_by=request.user,
        )
        ContentAuditLog.objects.create(
            actor=request.user,
            action="create_crop_draft",
            target_type="crop",
            target_id=crop.id,
            to_status="draft",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Created draft crop '{crop.name_en}'.")
        return redirect("cms:crops_list")
    return render(request, "cms/crop_form.html", {"crop": None})


@cms_role_required("edit_content")
def crop_edit(request, crop_id: str):
    crop = get_object_or_404(CropEntry, id=crop_id)
    if request.method == "POST":
        crop.name_ne = request.POST.get("name_ne", crop.name_ne).strip()
        crop.name_en = request.POST.get("name_en", crop.name_en).strip()
        crop.scientific_name = request.POST.get("scientific_name", crop.scientific_name).strip()
        crop.description_ne = request.POST.get("description_ne", crop.description_ne).strip()
        crop.varieties = request.POST.get("varieties", crop.varieties).strip()
        crop.planting_season_ne = request.POST.get("planting_season_ne", "").strip()
        crop.altitude_summary_ne = request.POST.get("altitude_summary_ne", "").strip()
        crop.soil_types_ne = request.POST.get("soil_types_ne", "").strip()
        crop.source_reference = request.POST.get("source_reference", "").strip()
        crop.thumbnail_url = request.POST.get("thumbnail_url", "").strip()
        if crop.is_published:
            crop.review_status = "needs_rereview"
            crop.is_published = False
        crop.save()
        messages.success(request, f"Updated '{crop.name_en}'.")
        return redirect("cms:crops_list")
    return render(request, "cms/crop_form.html", {"crop": crop})


@require_POST
@cms_role_required()
def crop_transition(request, crop_id: str):
    crop = get_object_or_404(CropEntry, id=crop_id)
    target_status = request.POST.get("target_status", "").strip()
    notes = request.POST.get("notes", "").strip()
    try:
        transition_entry_status(
            crop,
            target_status=target_status,
            actor=request.user,
            target_type="crop",
            notes=notes,
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Crop '{crop.name_en}' moved to '{target_status}'.")
    except (ValidationError, PermissionDenied) as exc:
        messages.error(request, str(exc))
    return redirect("cms:crops_list")


@cms_role_required("edit_content")
def livestock_list(request):
    status_filter = request.GET.get("status", "")
    animals = LivestockEntry.objects.all().order_by("name_en")
    if status_filter:
        animals = animals.filter(review_status=status_filter)
    return render(
        request,
        "cms/livestock_list.html",
        {
            "animals": animals,
            "statuses": WORKFLOW_STATUSES,
            "status_filter": status_filter,
            "can_approve": has_cms_permission(request.user, "approve_content"),
            "can_publish": has_cms_permission(request.user, "publish_content"),
        },
    )


@cms_role_required("edit_content")
def livestock_detail(request, animal_id: str):
    animal = get_object_or_404(LivestockEntry, id=animal_id)
    if request.method == "POST":
        sec_num = request.POST.get("section_number")
        if sec_num:
            sec_num_int = int(sec_num)
            matching_meta = next((s for s in LIVESTOCK_SECTIONS if s[0] == sec_num_int), None)
            if matching_meta:
                _, sec_key, sec_title = matching_meta
                section, _ = LivestockSectionContent.objects.get_or_create(
                    animal=animal,
                    section_number=sec_num_int,
                    defaults={"section_key": sec_key, "title_en": sec_title},
                )
                section.body_ne = request.POST.get("body_ne", "").strip()
                section.body_en = request.POST.get("body_en", "").strip()
                section.source_reference = request.POST.get("source_reference", "").strip()
                section.is_applicable = request.POST.get("is_applicable") == "on"
                section.review_status = "in_review" if section.body_ne else "draft"
                section.save()
                messages.success(request, f"Saved section {sec_num_int}: {sec_title}")
        return redirect("cms:livestock_detail", animal_id=animal.id)

    existing_sections = {s.section_number: s for s in animal.sections.all()}
    section_rows = []
    for num, key, title_en in LIVESTOCK_SECTIONS:
        section_rows.append(
            {
                "number": num,
                "key": key,
                "title_en": title_en,
                "entry": existing_sections.get(num),
            }
        )
    return render(
        request,
        "cms/livestock_detail.html",
        {
            "animal": animal,
            "section_rows": section_rows,
            "can_approve": has_cms_permission(request.user, "approve_content"),
            "can_publish": has_cms_permission(request.user, "publish_content"),
        },
    )


@require_POST
@cms_role_required()
def livestock_transition(request, animal_id: str):
    animal = get_object_or_404(LivestockEntry, id=animal_id)
    target_status = request.POST.get("target_status", "").strip()
    notes = request.POST.get("notes", "").strip()
    try:
        transition_entry_status(
            animal,
            target_status=target_status,
            actor=request.user,
            target_type="livestock",
            notes=notes,
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Livestock '{animal.name_en}' moved to '{target_status}'.")
    except (ValidationError, PermissionDenied) as exc:
        messages.error(request, str(exc))
    return redirect("cms:livestock_list")


@cms_role_required("manage_media")
def media_library(request):
    if request.method == "POST":
        uploaded = request.FILES.get("image_file")
        title_en = request.POST.get("title_en", "").strip()
        caption_ne = request.POST.get("caption_ne", "").strip()
        creator_name = request.POST.get("creator_name", "").strip()
        license_type = request.POST.get("license_type", "").strip()
        source_url = request.POST.get("source_url", "").strip()

        if not uploaded or not title_en or not creator_name or not license_type:
            messages.error(
                request, "Image file, English title, creator attribution, and license are required."
            )
            return redirect("cms:media_library")

        asset = MediaAsset(
            title_en=title_en,
            caption_ne=caption_ne,
            target_kind=request.POST.get("target_kind", "general"),
            creator_name=creator_name,
            license_type=license_type,
            source_url=source_url,
            is_verified=bool(creator_name and license_type),
            uploaded_by=request.user,
        )
        try:
            storage_root = Path(getattr(settings, "MEDIA_ROOT", Path(settings.BASE_DIR) / "media")) / "cms"
            meta = process_uploaded_image(uploaded, storage_root, asset.id)
            for k, v in meta.items():
                setattr(asset, k, v)
            asset.save()
            ContentAuditLog.objects.create(
                actor=request.user,
                action="upload_verified_media",
                target_type="media_asset",
                target_id=asset.id,
                ip_address=get_client_ip(request),
            )
            messages.success(request, f"Uploaded and processed '{title_en}' ({meta['width_px']}x{meta['height_px']}).")
        except ValidationError as exc:
            messages.error(request, str(exc))
        return redirect("cms:media_library")

    assets = MediaAsset.objects.all().order_by("-created_at")
    return render(request, "cms/media_list.html", {"assets": assets})


@cms_role_required()
def helpline_list(request):
    if request.method == "POST":
        if not has_cms_permission(request.user, "manage_helpline"):
            messages.error(request, "Super Admin permission required to modify helpline contacts.")
            return redirect("cms:helpline_list")
        HelplineContactEntry.objects.create(
            name_ne=request.POST.get("name_ne", "").strip(),
            designation_ne=request.POST.get("designation_ne", "").strip(),
            department_en=request.POST.get("department_en", "").strip(),
            ward_ne=request.POST.get("ward_ne", "").strip(),
            mobile_number=request.POST.get("mobile_number", "").strip(),
            email=request.POST.get("email", "").strip(),
            source_reference=request.POST.get("source_reference", "").strip(),
            is_verified=True,
            is_published=True,
        )
        messages.success(request, "Verified helpline contact added.")
        return redirect("cms:helpline_list")

    contacts = HelplineContactEntry.objects.all().order_by("-updated_at")
    return render(
        request,
        "cms/helpline_list.html",
        {
            "contacts": contacts,
            "can_manage": has_cms_permission(request.user, "manage_helpline"),
        },
    )


# ---- Public Farmer Client Read-Only JSON API (Published & Verified Only) ----


@require_GET
def public_crops_api(request):
    """Returns strictly published crops (never drafts or unverified submissions)."""
    crops = list(
        CropEntry.objects.filter(is_published=True, review_status="published").values(
            "id",
            "category_id",
            "name_ne",
            "name_en",
            "scientific_name",
            "description_ne",
            "varieties",
            "planting_season_ne",
            "altitude_summary_ne",
            "soil_types_ne",
            "source_reference",
            "thumbnail_url",
        )
    )
    return JsonResponse({"count": len(crops), "results": crops})


@require_GET
def public_livestock_api(request):
    """Returns strictly published livestock species and their published sections."""
    animals = LivestockEntry.objects.filter(is_published=True, review_status="published")
    payload = []
    for animal in animals:
        sections = list(
            animal.sections.filter(is_published=True, review_status="published").values(
                "section_number", "section_key", "title_en", "body_ne", "source_reference"
            )
        )
        payload.append(
            {
                "id": animal.id,
                "category_id": animal.category_id,
                "species_code": animal.species_code,
                "name_ne": animal.name_ne,
                "name_en": animal.name_en,
                "scientific_name": animal.scientific_name,
                "summary_ne": animal.summary_ne,
                "safety_notice_ne": animal.safety_notice_ne,
                "source_reference": animal.source_reference,
                "thumbnail_url": animal.thumbnail_url,
                "sections": sections,
            }
        )
    return JsonResponse({"count": len(payload), "results": payload})


@require_GET
def public_helpline_api(request):
    """Returns verified & published helpline contacts."""
    contacts = list(
        HelplineContactEntry.objects.filter(is_verified=True, is_published=True).values(
            "id",
            "name_ne",
            "designation_ne",
            "department_en",
            "ward_ne",
            "mobile_number",
            "email",
            "source_reference",
        )
    )
    return JsonResponse({"count": len(contacts), "results": contacts})
