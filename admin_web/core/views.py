"""Application views for Bakaiya Krishi Saathi CMS, Public APIs, and Legacy Portal."""

from pathlib import Path
import uuid

from django.conf import settings
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth import get_user_model
from django.core.exceptions import PermissionDenied, ValidationError
from django.db import DatabaseError, connection
from django.db.models import Q, Count
from django.http import JsonResponse, HttpResponseForbidden
from django.shortcuts import get_object_or_404, redirect, render
from django.views.decorators.http import require_GET, require_POST

from .constants import LIVESTOCK_SECTIONS, WORKFLOW_STATUSES
from .forms import (
    CropEntryForm,
    HelplineContactForm,
    LegacyCropForm,
    LegacyGuideForm,
    LegacyGuideStepForm,
    LivestockSectionForm,
    LoginForm,
    MediaAssetUploadForm,
    StaffManagerCreationForm,
    StaffCreationForm,
    AdminProfileForm,
    StaffProfileForm,
)
from .models import (
    AdminActivityLog,
    AdminProfile,
    ContentAuditLog,
    Crop,
    CropCategory,
    CropEntry,
    Farm,
    FarmerProfile,
    Guide,
    GuideStep,
    HelplineContactEntry,
    LivestockEntry,
    LivestockSectionContent,
    MediaAsset,
    Region,
    Season,
    Topic,
)
from .services import (
    check_login_rate_limit,
    clear_failed_logins,
    cms_role_required,
    get_client_ip,
    get_user_role,
    has_cms_permission,
    mirror_helpline,
    mirror_published_entry,
    process_uploaded_image,
    rate_limit_exceeded_response,
    record_failed_login,
    transition_entry_status,
    can_manage_user,
    can_access_content,
    get_user_team,
    is_super_admin,
    is_staff_manager,
    is_staff,
)


# ---------------------------------------------------------------------------
# Health & Entry Redirects
# ---------------------------------------------------------------------------


def healthz(_request):
    try:
        with connection.cursor() as cursor:
            cursor.execute("SELECT 1")
            cursor.fetchone()
    except DatabaseError:
        return JsonResponse({"status": "error"}, status=503)
    return JsonResponse({"status": "ok"})


def root_redirect(_request):
    return redirect("core:dashboard")


# ---------------------------------------------------------------------------
# Primary English Admin CMS Views
# ---------------------------------------------------------------------------


def cms_login(request):
    if request.user.is_authenticated and get_user_role(request.user):
        return redirect("core:dashboard")

    error_message = None
    if request.method == "POST":
        if not check_login_rate_limit(request):
            return rate_limit_exceeded_response()

        form = LoginForm(request.POST)
        if form.is_valid():
            user = authenticate(
                request,
                username=form.cleaned_data["username"],
                password=form.cleaned_data["password"],
            )
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
                return redirect("core:dashboard")

        record_failed_login(request)
        error_message = "Invalid credentials or account lacks an active CMS role."

    return render(request, "core/pages/login.html", {"error_message": error_message})


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
    return redirect("core:login")


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
    return render(request, "core/dashboard.html", context)


# ============================================================
# Role-Specific Dashboards
# ============================================================

@cms_role_required()
def super_admin_dashboard(request):
    """Super Admin Dashboard - complete system overview."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    # User stats
    staff_managers = AdminProfile.objects.filter(role="staff_manager", is_active_editor=True)
    staff_members = AdminProfile.objects.filter(role="staff", is_active_editor=True)
    active_managers = staff_managers.filter(status="active").count()
    suspended_managers = staff_managers.filter(status="suspended").count()
    active_staff = staff_members.filter(status="active").count()
    suspended_staff = staff_members.filter(status="suspended").count()

    # Content stats
    context = {
        "role": "super_admin",
        "total_staff_managers": staff_managers.count(),
        "active_staff_managers": active_managers,
        "suspended_staff_managers": suspended_managers,
        "total_staff": staff_members.count(),
        "active_staff": active_staff,
        "suspended_staff": suspended_staff,
        "total_farmers": FarmerProfile.objects.count(),
        "total_crops": CropEntry.objects.count(),
        "published_crops": CropEntry.objects.filter(is_published=True).count(),
        "crop_review": CropEntry.objects.filter(review_status="in_review").count(),
        "total_livestock": LivestockEntry.objects.count(),
        "published_livestock": LivestockEntry.objects.filter(is_published=True).count(),
        "livestock_review": LivestockEntry.objects.filter(review_status="in_review").count(),
        "total_guides": Guide.objects.count(),
        "published_guides": Guide.objects.filter(is_published=True).count(),
        "total_media": MediaAsset.objects.count(),
        "total_helpline": HelplineContactEntry.objects.count(),
        "recent_logs": ContentAuditLog.objects.select_related("actor")[:15],
        "recent_staff_managers": staff_managers.select_related("user")[:10],
    }
    return render(request, "core/dashboards/super_admin_dashboard.html", context)


@cms_role_required()
def staff_manager_dashboard(request):
    """Staff Manager Dashboard - team and content overview."""
    if not is_staff_manager(request.user):
        return HttpResponseForbidden("Forbidden: Staff Manager access required.")

    profile = request.user.cms_profile
    team_members = profile.get_team_members()

    # Team stats
    active_team = team_members.filter(status="active").count()
    suspended_team = team_members.filter(status="suspended").count()

    # Team content stats
    team_user_ids = list(team_members.values_list("user_id", flat=True))
    team_user_ids.append(request.user.id)  # Include manager's own content

    context = {
        "role": "staff_manager",
        "total_team_members": team_members.count(),
        "active_team_members": active_team,
        "suspended_team_members": suspended_team,
        "team_crops": CropEntry.objects.filter(created_by_id__in=team_user_ids).count(),
        "team_published_crops": CropEntry.objects.filter(created_by_id__in=team_user_ids, is_published=True).count(),
        "team_livestock": LivestockEntry.objects.filter(created_by_id__in=team_user_ids).count(),
        "team_published_livestock": LivestockEntry.objects.filter(created_by_id__in=team_user_ids, is_published=True).count(),
        "team_guides": Guide.objects.filter(created_by_id__in=team_user_ids).count(),
        "team_published_guides": Guide.objects.filter(created_by_id__in=team_user_ids, is_published=True).count(),
        "team_media": MediaAsset.objects.filter(uploaded_by_id__in=team_user_ids).count(),
        "recent_team_logs": ContentAuditLog.objects.filter(actor_id__in=team_user_ids).select_related("actor")[:15],
        "team_members": team_members.select_related("user")[:10],
    }
    return render(request, "core/dashboards/staff_manager_dashboard.html", context)


@cms_role_required()
def staff_dashboard(request):
    """Staff Dashboard - personal content overview."""
    if not is_staff(request.user):
        return HttpResponseForbidden("Forbidden: Staff access required.")

    user_id = request.user.id

    context = {
        "role": "staff",
        "my_crops": CropEntry.objects.filter(created_by_id=user_id).count(),
        "my_published_crops": CropEntry.objects.filter(created_by_id=user_id, is_published=True).count(),
        "my_crops_in_review": CropEntry.objects.filter(created_by_id=user_id, review_status="in_review").count(),
        "my_livestock": LivestockEntry.objects.filter(created_by_id=user_id).count(),
        "my_published_livestock": LivestockEntry.objects.filter(created_by_id=user_id, is_published=True).count(),
        "my_guides": Guide.objects.filter(created_by_id=user_id).count(),
        "my_published_guides": Guide.objects.filter(created_by_id=user_id, is_published=True).count(),
        "my_media": MediaAsset.objects.filter(uploaded_by_id=user_id).count(),
        "recent_my_logs": ContentAuditLog.objects.filter(actor_id=user_id).select_related("actor")[:15],
        "recent_crops": CropEntry.objects.filter(created_by_id=user_id).order_by("-updated_at")[:5],
    }
    return render(request, "core/dashboards/staff_dashboard.html", context)


# ============================================================
# User Management Views
# ============================================================

@cms_role_required("create_staff_manager")
def staff_manager_list(request):
    """Super Admin: List all Staff Managers."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    status_filter = request.GET.get("status", "")
    managers = AdminProfile.objects.filter(role="staff_manager").select_related("user").order_by("-created_at")

    if status_filter:
        managers = managers.filter(status=status_filter)

    return render(request, "core/user_management/staff_manager_list.html", {
        "managers": managers,
        "status_filter": status_filter,
        "status_choices": AdminProfile.STATUS_CHOICES,
    })


@cms_role_required("create_staff_manager")
def staff_manager_create(request):
    """Super Admin: Create new Staff Manager."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    if request.method == "POST":
        form = StaffManagerCreationForm(request.POST)
        if form.is_valid():
            user = User.objects.create_user(
                username=form.cleaned_data["username"],
                email=form.cleaned_data["email"],
                password=form.cleaned_data["password"],
            )
            AdminProfile.objects.create(
                user=user,
                role=AdminProfile.ROLE_STAFF_MANAGER,
                full_name=form.cleaned_data["full_name"],
                mobile_number=form.cleaned_data["mobile_number"],
                email=form.cleaned_data["email"],
                status=AdminProfile.STATUS_ACTIVE,
                is_active_editor=True,
                created_by=request.user,
            )
            ContentAuditLog.objects.create(
                actor=request.user,
                action="create_staff_manager",
                target_type="admin_profile",
                target_id=str(user.id),
                notes=f"Created Staff Manager: {user.username}",
                ip_address=get_client_ip(request),
            )
            messages.success(request, f"Staff Manager '{user.username}' created successfully.")
            return redirect("core:staff_manager_list")
    else:
        form = StaffManagerCreationForm()

    return render(request, "core/user_management/staff_manager_form.html", {
        "form": form,
        "is_create": True,
    })


@cms_role_required("edit_staff_manager")
def staff_manager_edit(request, manager_id: int):
    """Super Admin: Edit Staff Manager."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    manager = get_object_or_404(AdminProfile, id=manager_id, role="staff_manager")
    form = AdminProfileForm(request.POST or None, request.FILES or None, instance=manager)

    if request.method == "POST" and form.is_valid():
        form.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="edit_staff_manager",
            target_type="admin_profile",
            target_id=str(manager.user.id),
            notes=f"Updated Staff Manager: {manager.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff Manager '{manager.user.username}' updated successfully.")
        return redirect("core:staff_manager_list")

    return render(request, "core/user_management/staff_manager_form.html", {
        "form": form,
        "manager": manager,
        "is_create": False,
    })


@cms_role_required("suspend_staff_manager")
def staff_manager_suspend(request, manager_id: int):
    """Super Admin: Suspend Staff Manager."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    manager = get_object_or_404(AdminProfile, id=manager_id, role="staff_manager")
    if request.method == "POST":
        manager.status = AdminProfile.STATUS_SUSPENDED
        manager.is_active_editor = False
        manager.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="suspend_staff_manager",
            target_type="admin_profile",
            target_id=str(manager.user.id),
            notes=f"Suspended Staff Manager: {manager.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff Manager '{manager.user.username}' suspended.")
        return redirect("core:staff_manager_list")

    return render(request, "core/user_management/staff_manager_confirm.html", {
        "manager": manager,
        "action": "suspend",
    })


@cms_role_required("reactivate_staff_manager")
def staff_manager_reactivate(request, manager_id: int):
    """Super Admin: Reactivate Staff Manager."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    manager = get_object_or_404(AdminProfile, id=manager_id, role="staff_manager")
    if request.method == "POST":
        manager.status = AdminProfile.STATUS_ACTIVE
        manager.is_active_editor = True
        manager.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="reactivate_staff_manager",
            target_type="admin_profile",
            target_id=str(manager.user.id),
            notes=f"Reactivated Staff Manager: {manager.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff Manager '{manager.user.username}' reactivated.")
        return redirect("core:staff_manager_list")

    return render(request, "core/user_management/staff_manager_confirm.html", {
        "manager": manager,
        "action": "reactivate",
    })


@cms_role_required("archive_staff_manager")
def staff_manager_archive(request, manager_id: int):
    """Super Admin: Archive Staff Manager."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    manager = get_object_or_404(AdminProfile, id=manager_id, role="staff_manager")
    if request.method == "POST":
        # Reassign their team to another manager or keep as unassigned
        reassign_to_id = request.POST.get("reassign_to")
        if reassign_to_id:
            new_manager = get_object_or_404(AdminProfile, id=reassign_to_id, role="staff_manager")
            AdminProfile.objects.filter(managed_by=manager).update(managed_by=new_manager)

        manager.status = AdminProfile.STATUS_ARCHIVED
        manager.is_active_editor = False
        manager.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="archive_staff_manager",
            target_type="admin_profile",
            target_id=str(manager.user.id),
            notes=f"Archived Staff Manager: {manager.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff Manager '{manager.user.username}' archived.")
        return redirect("core:staff_manager_list")

    other_managers = AdminProfile.objects.filter(
        role="staff_manager", status=AdminProfile.STATUS_ACTIVE
    ).exclude(id=manager_id)

    return render(request, "core/user_management/staff_manager_confirm.html", {
        "manager": manager,
        "action": "archive",
        "other_managers": other_managers,
    })


# Staff Management (Staff Manager)

@cms_role_required("create_staff")
def staff_list(request):
    """Staff Manager: List their team members."""
    if not is_staff_manager(request.user):
        return HttpResponseForbidden("Forbidden: Staff Manager access required.")

    profile = request.user.cms_profile
    team_members = profile.get_team_members().select_related("user")

    status_filter = request.GET.get("status", "")
    if status_filter:
        team_members = team_members.filter(status=status_filter)

    return render(request, "core/user_management/staff_list.html", {
        "team_members": team_members,
        "status_filter": status_filter,
        "status_choices": AdminProfile.STATUS_CHOICES,
    })


@cms_role_required("create_staff")
def staff_create(request):
    """Staff Manager: Create new Staff member."""
    if not is_staff_manager(request.user):
        return HttpResponseForbidden("Forbidden: Staff Manager access required.")

    if request.method == "POST":
        form = StaffCreationForm(request.POST, manager=request.user)
        if form.is_valid():
            user = User.objects.create_user(
                username=form.cleaned_data["username"],
                email=form.cleaned_data["email"],
                password=form.cleaned_data["password"],
            )
            AdminProfile.objects.create(
                user=user,
                role=AdminProfile.ROLE_STAFF,
                full_name=form.cleaned_data["full_name"],
                mobile_number=form.cleaned_data["mobile_number"],
                email=form.cleaned_data["email"],
                status=AdminProfile.STATUS_ACTIVE,
                is_active_editor=True,
                managed_by=request.user.cms_profile,
                created_by=request.user,
            )
            ContentAuditLog.objects.create(
                actor=request.user,
                action="create_staff",
                target_type="admin_profile",
                target_id=str(user.id),
                notes=f"Created Staff: {user.username}",
                ip_address=get_client_ip(request),
            )
            messages.success(request, f"Staff '{user.username}' created successfully.")
            return redirect("core:staff_list")
    else:
        form = StaffCreationForm(manager=request.user)

    return render(request, "core/user_management/staff_form.html", {
        "form": form,
        "is_create": True,
    })


@cms_role_required("edit_staff")
def staff_edit(request, staff_id: int):
    """Staff Manager: Edit Staff member."""
    if not is_staff_manager(request.user):
        return HttpResponseForbidden("Forbidden: Staff Manager access required.")

    staff = get_object_or_404(AdminProfile, id=staff_id, role="staff", managed_by=request.user.cms_profile)
    form = StaffProfileForm(request.POST or None, request.FILES or None, instance=staff)

    if request.method == "POST" and form.is_valid():
        form.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="edit_staff",
            target_type="admin_profile",
            target_id=str(staff.user.id),
            notes=f"Updated Staff: {staff.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff '{staff.user.username}' updated successfully.")
        return redirect("core:staff_list")

    return render(request, "core/user_management/staff_form.html", {
        "form": form,
        "staff": staff,
        "is_create": False,
    })


@cms_role_required("suspend_staff")
def staff_suspend(request, staff_id: int):
    """Staff Manager: Suspend Staff member."""
    if not is_staff_manager(request.user):
        return HttpResponseForbidden("Forbidden: Staff Manager access required.")

    staff = get_object_or_404(AdminProfile, id=staff_id, role="staff", managed_by=request.user.cms_profile)
    if request.method == "POST":
        staff.status = AdminProfile.STATUS_SUSPENDED
        staff.is_active_editor = False
        staff.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="suspend_staff",
            target_type="admin_profile",
            target_id=str(staff.user.id),
            notes=f"Suspended Staff: {staff.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff '{staff.user.username}' suspended.")
        return redirect("core:staff_list")

    return render(request, "core/user_management/staff_confirm.html", {
        "staff": staff,
        "action": "suspend",
    })


@cms_role_required("reactivate_staff")
def staff_reactivate(request, staff_id: int):
    """Staff Manager: Reactivate Staff member."""
    if not is_staff_manager(request.user):
        return HttpResponseForbidden("Forbidden: Staff Manager access required.")

    staff = get_object_or_404(AdminProfile, id=staff_id, role="staff", managed_by=request.user.cms_profile)
    if request.method == "POST":
        staff.status = AdminProfile.STATUS_ACTIVE
        staff.is_active_editor = True
        staff.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="reactivate_staff",
            target_type="admin_profile",
            target_id=str(staff.user.id),
            notes=f"Reactivated Staff: {staff.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff '{staff.user.username}' reactivated.")
        return redirect("core:staff_list")

    return render(request, "core/user_management/staff_confirm.html", {
        "staff": staff,
        "action": "reactivate",
    })


@cms_role_required("deactivate_staff")
def staff_deactivate(request, staff_id: int):
    """Staff Manager: Deactivate Staff member."""
    if not is_staff_manager(request.user):
        return HttpResponseForbidden("Forbidden: Staff Manager access required.")

    staff = get_object_or_404(AdminProfile, id=staff_id, role="staff", managed_by=request.user.cms_profile)
    if request.method == "POST":
        staff.status = AdminProfile.STATUS_INACTIVE
        staff.is_active_editor = False
        staff.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="deactivate_staff",
            target_type="admin_profile",
            target_id=str(staff.user.id),
            notes=f"Deactivated Staff: {staff.user.username}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Staff '{staff.user.username}' deactivated.")
        return redirect("core:staff_list")

    return render(request, "core/user_management/staff_confirm.html", {
        "staff": staff,
        "action": "deactivate",
    })


@cms_role_required("view_all_staff")
def all_staff_list(request):
    """Super Admin: View all staff across all teams."""
    if not is_super_admin(request.user):
        return HttpResponseForbidden("Forbidden: Super Admin access required.")

    staff = AdminProfile.objects.filter(role="staff").select_related("user", "managed_by__user").order_by("-created_at")

    manager_filter = request.GET.get("manager")
    if manager_filter:
        staff = staff.filter(managed_by_id=manager_filter)

    status_filter = request.GET.get("status", "")
    if status_filter:
        staff = staff.filter(status=status_filter)

    managers = AdminProfile.objects.filter(role="staff_manager", is_active_editor=True)

    return render(request, "core/user_management/all_staff_list.html", {
        "staff_members": staff,
        "managers": managers,
        "manager_filter": manager_filter,
        "status_filter": status_filter,
        "status_choices": AdminProfile.STATUS_CHOICES,
    })


# Profile views

@login_required
def my_profile(request):
    """Current user's profile view."""
    profile = getattr(request.user, "cms_profile", None)
    if not profile:
        messages.error(request, "Profile not found.")
        return redirect("core:dashboard")

    return render(request, "core/user_management/my_profile.html", {
        "profile": profile,
    })


@login_required
def edit_my_profile(request):
    """Current user edits their own profile."""
    profile = getattr(request.user, "cms_profile", None)
    if not profile:
        messages.error(request, "Profile not found.")
        return redirect("core:dashboard")

    form = AdminProfileForm(request.POST or None, request.FILES or None, instance=profile)

    if request.method == "POST" and form.is_valid():
        form.save()
        messages.success(request, "Profile updated successfully.")
        return redirect("core:my_profile")

    return render(request, "core/user_management/edit_my_profile.html", {
        "form": form,
        "profile": profile,
    })


# Password management
from django.contrib.auth.views import PasswordChangeView, PasswordResetView, PasswordResetDoneView, PasswordResetConfirmView, PasswordResetCompleteView

# We'll use Django's built-in views for password management
# Just need to ensure templates exist


@cms_role_required("edit_content")
def crops_list(request):
    status_filter = request.GET.get("status", "")
    crops = CropEntry.objects.all().order_by("-updated_at")
    if status_filter:
        crops = crops.filter(review_status=status_filter)
    return render(
        request,
        "core/pages/crops_list.html",
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
        form = CropEntryForm(request.POST)
        if not form.is_valid():
            messages.error(request, "English name, Nepali name, Nepali description, and valid URLs are required.")
            return render(
                request,
                "core/pages/crop_form.html",
                {"crop": request.POST, "is_create": True},
                status=400,
            )

        crop = CropEntry.objects.create(
            **form.cleaned_data,
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
        return redirect("core:crops_list")
    return render(request, "core/pages/crop_form.html", {"crop": None, "is_create": True})


@cms_role_required("edit_content")
def crop_edit(request, crop_id: str):
    crop = get_object_or_404(CropEntry, id=crop_id)
    if request.method == "POST":
        # Preserve optional metadata fields if a partial client POST omits them.
        post_data = request.POST.copy()
        for optional_field in (
            "category_id",
            "scientific_name",
            "varieties",
            "planting_season_ne",
            "altitude_summary_ne",
            "soil_types_ne",
            "source_reference",
            "thumbnail_url",
        ):
            if optional_field not in post_data:
                post_data[optional_field] = getattr(crop, optional_field) or ""

        form = CropEntryForm(post_data)
        if not form.is_valid():
            messages.error(request, "English name, Nepali name, Nepali description, and valid URLs are required.")
            return render(
                request,
                "core/pages/crop_form.html",
                {"crop": crop, "is_create": False},
                status=400,
            )

        for field_name, value in form.cleaned_data.items():
            setattr(crop, field_name, value)

        previous_status = crop.review_status
        if crop.is_published:
            crop.review_status = "needs_rereview"
            crop.is_published = False
        crop.save()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="edit_crop",
            target_type="crop",
            target_id=crop.id,
            from_status=previous_status,
            to_status=crop.review_status,
            ip_address=get_client_ip(request),
        )
        mirror_published_entry(crop, "crop")
        messages.success(request, f"Updated '{crop.name_en}'.")
        return redirect("core:crops_list")
    return render(request, "core/pages/crop_form.html", {"crop": crop, "is_create": False})


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
    return redirect("core:crops_list")


@cms_role_required("edit_content")
def livestock_list(request):
    status_filter = request.GET.get("status", "")
    animals = LivestockEntry.objects.all().order_by("name_en")
    if status_filter:
        animals = animals.filter(review_status=status_filter)
    return render(
        request,
        "core/pages/livestock_list.html",
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
        form = LivestockSectionForm(request.POST)
        if not form.is_valid():
            messages.error(request, "Invalid section number.")
            return redirect("core:livestock_detail", animal_id=animal.id)

        sec_num_int = form.cleaned_data["section_number"]
        matching_meta = next((s for s in LIVESTOCK_SECTIONS if s[0] == sec_num_int), None)
        if matching_meta:
            _, sec_key, sec_title = matching_meta
            section, _ = LivestockSectionContent.objects.get_or_create(
                animal=animal,
                section_number=sec_num_int,
                defaults={"section_key": sec_key, "title_en": sec_title},
            )
            section.body_ne = form.cleaned_data["body_ne"].strip()
            section.body_en = form.cleaned_data["body_en"].strip()
            section.source_reference = form.cleaned_data["source_reference"].strip()
            section.is_applicable = bool(form.cleaned_data["is_applicable"])
            section.review_status = "in_review" if section.body_ne else "draft"
            section.is_published = False
            section.save()
            messages.success(request, f"Saved section {sec_num_int}: {sec_title}")
        else:
            messages.error(request, f"Unknown section number '{sec_num_int}'.")
        return redirect("core:livestock_detail", animal_id=animal.id)

    existing_sections = {s.section_number: s for s in animal.sections.all()}
    section_rows = [
        {"number": num, "key": key, "title_en": title_en, "entry": existing_sections.get(num)}
        for num, key, title_en in LIVESTOCK_SECTIONS
    ]
    return render(
        request,
        "core/pages/livestock_detail.html",
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
    return redirect("core:livestock_list")


@cms_role_required("manage_media")
def media_library(request):
    if request.method == "POST":
        form = MediaAssetUploadForm(request.POST, request.FILES)
        if not form.is_valid():
            messages.error(
                request,
                "Image file, English title, creator attribution, license, and valid source URL are required.",
            )
            return redirect("core:media_library")

        data = form.cleaned_data
        asset = MediaAsset(
            title_en=data["title_en"].strip(),
            caption_ne=data["caption_ne"].strip(),
            target_kind=data["target_kind"],
            creator_name=data["creator_name"].strip(),
            license_type=data["license_type"].strip(),
            source_url=data["source_url"].strip(),
            is_verified=bool(data["creator_name"].strip() and data["license_type"].strip()),
            uploaded_by=request.user,
        )
        try:
            storage_root = Path(getattr(settings, "MEDIA_ROOT", Path(settings.BASE_DIR) / "media")) / "cms"
            meta = process_uploaded_image(data["image_file"], storage_root, asset.id)
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
            messages.success(
                request,
                f"Uploaded and processed '{asset.title_en}' ({meta['width_px']}x{meta['height_px']}).",
            )
        except ValidationError as exc:
            messages.error(request, str(exc))
        return redirect("core:media_library")

    assets = MediaAsset.objects.all().order_by("-created_at")
    return render(request, "core/pages/media_list.html", {"assets": assets})


@cms_role_required()
def helpline_list(request):
    if request.method == "POST":
        if not has_cms_permission(request.user, "manage_helpline"):
            messages.error(request, "Super Admin permission required to modify helpline contacts.")
            return redirect("core:helpline_list")

        form = HelplineContactForm(request.POST)
        if not form.is_valid():
            messages.error(request, "All helpline fields, official source, and valid email (if provided) are required.")
            return redirect("core:helpline_list")

        contact = HelplineContactEntry.objects.create(
            **form.cleaned_data,
            is_verified=True,
            is_published=True,
        )
        mirror_helpline(contact)
        messages.success(request, "Verified helpline contact added.")
        return redirect("core:helpline_list")

    contacts = HelplineContactEntry.objects.all().order_by("-updated_at")
    return render(
        request,
        "core/pages/helpline_list.html",
        {
            "contacts": contacts,
            "can_manage": has_cms_permission(request.user, "manage_helpline"),
        },
    )


@cms_role_required()
def farmers_list(request):
    """Displays registered farmers synced from Android via Supabase PostgreSQL."""
    farmers = []
    db_error = None
    try:
        farmers = list(FarmerProfile.objects.all().order_by("-created_at"))
    except Exception:
        db_error = "Farmer directory is currently unavailable on the configured database backend."

    return render(
        request,
        "core/pages/farmers_list.html",
        {
            "farmers": farmers,
            "db_error": db_error,
        },
    )


# ---------------------------------------------------------------------------
# CMS Detail & Delete Views (Complete Database Visibility & Editability)
# ---------------------------------------------------------------------------


@cms_role_required()
def crop_detail(request, crop_id: str):
    """Complete detail view for a CropEntry — every field displayed."""
    crop = get_object_or_404(CropEntry, id=crop_id)
    source_doc = None
    if crop.source_document_id:
        try:
            source_doc = SourceDocument.objects.get(id=crop.source_document_id)
        except SourceDocument.DoesNotExist:
            source_doc = None
    return render(
        request,
        "core/pages/crop_detail.html",
        {
            "crop": crop,
            "source_doc": source_doc,
        },
    )


@cms_role_required("edit_content")
def crop_delete(request, crop_id: str):
    """Delete a CropEntry record with confirmation."""
    crop = get_object_or_404(CropEntry, id=crop_id)
    if request.method == "POST":
        crop_name = crop.name_en
        crop.delete()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="delete_crop_entry",
            target_type="crop_entry",
            target_id=crop_id,
            notes=f"Deleted crop entry {crop_name}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Crop entry '{crop_name}' deleted.")
        return redirect("core:crops_list")
    return render(
        request,
        "core/pages/crop_delete_confirm.html",
        {"crop": crop},
    )


@cms_role_required()
def livestock_entry_detail(request, animal_id: str):
    """Complete detail view for a LivestockEntry — all main fields plus sections."""
    animal = get_object_or_404(LivestockEntry, id=animal_id)
    source_doc = None
    if animal.source_document_id:
        try:
            source_doc = SourceDocument.objects.get(id=animal.source_document_id)
        except SourceDocument.DoesNotExist:
            source_doc = None
    sections = animal.sections.all().order_by("section_number")
    return render(
        request,
        "core/pages/livestock_entry_detail.html",
        {
            "animal": animal,
            "source_doc": source_doc,
            "sections": sections,
        },
    )


@cms_role_required("manage_media")
def media_asset_detail(request, asset_id: str):
    """Complete detail view for a MediaAsset — all fields displayed."""
    asset = get_object_or_404(MediaAsset, id=asset_id)
    return render(
        request,
        "core/pages/media_asset_detail.html",
        {"asset": asset},
    )


@cms_role_required("manage_media")
def media_asset_delete(request, asset_id: str):
    """Delete a MediaAsset record with confirmation."""
    asset = get_object_or_404(MediaAsset, id=asset_id)
    if request.method == "POST":
        asset_title = asset.title_en
        asset.delete()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="delete_media_asset",
            target_type="media_asset",
            target_id=asset_id,
            notes=f"Deleted media asset {asset_title}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Media asset '{asset_title}' deleted.")
        return redirect("core:media_library")
    return render(
        request,
        "core/pages/media_asset_delete_confirm.html",
        {"asset": asset},
    )


@cms_role_required()
def helpline_contact_detail(request, contact_id: str):
    """Complete detail view for a HelplineContactEntry — all fields displayed."""
    contact = get_object_or_404(HelplineContactEntry, id=contact_id)
    return render(
        request,
        "core/pages/helpline_contact_detail.html",
        {"contact": contact},
    )


@cms_role_required("manage_helpline")
def helpline_contact_delete(request, contact_id: str):
    """Delete a HelplineContactEntry record with confirmation."""
    contact = get_object_or_404(HelplineContactEntry, id=contact_id)
    if request.method == "POST":
        contact_name = contact.name_ne
        contact.delete()
        ContentAuditLog.objects.create(
            actor=request.user,
            action="delete_helpline_contact",
            target_type="helpline_contact",
            target_id=contact_id,
            notes=f"Deleted helpline contact {contact_name}",
            ip_address=get_client_ip(request),
        )
        messages.success(request, f"Helpline contact '{contact_name}' deleted.")
        return redirect("core:helpline_list")
    return render(
        request,
        "core/pages/helpline_contact_delete_confirm.html",
        {"contact": contact},
    )


# ---------------------------------------------------------------------------
# Public Farmer Client Read-Only JSON APIs (Published & Verified Only)
# ---------------------------------------------------------------------------


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


# ---------------------------------------------------------------------------
# Legacy Supabase Portal Views (Preserved for Compatibility)
# ---------------------------------------------------------------------------


def legacy_login(request):
    if request.user.is_authenticated:
        return redirect("core:legacy_dashboard")

    if request.method == "POST":
        form = LoginForm(request.POST)
        if form.is_valid():
            user = authenticate(
                request,
                username=form.cleaned_data["username"],
                password=form.cleaned_data["password"],
            )
            if user is not None:
                login(request, user)
                messages.success(request, f"स्वागत छ, {user.username}!")
                return redirect("core:legacy_dashboard")
        messages.error(request, "गलत प्रयोगकर्ता नाम वा पासवर्ड। (Invalid username or password)")

    return render(request, "core/pages/legacy_login.html")


@require_POST
def legacy_logout(request):
    logout(request)
    messages.info(request, "तपाईं सफलतापूर्वक लगआउट हुनुभयो।")
    return redirect("core:legacy_login")


@login_required
def legacy_farmer_list(request):
    """Registered farmers from Supabase farmer_profiles (unmanaged table)."""
    farmers = []
    db_error = None
    try:
        farmers = list(FarmerProfile.objects.all().prefetch_related("farms").order_by("-created_at"))
    except Exception:
        db_error = "Farmer directory is currently unavailable on the configured database backend."
    return render(request, "core/pages/legacy_farmers.html", {"farmers": farmers, "db_error": db_error})


@login_required
def legacy_dashboard(request):
    """Legacy admin overview. Queries unmanaged Supabase tables; degrades gracefully if unavailable."""
    total_crops = published_crops = total_guides = published_guides = 0
    total_farmers = total_farms = 0
    recent_logs = []
    recent_crops = []
    db_error = None
    try:
        total_crops = Crop.objects.count()
        published_crops = Crop.objects.filter(is_published=True).count()
        total_guides = Guide.objects.count()
        published_guides = Guide.objects.filter(is_published=True).count()
        total_farmers = FarmerProfile.objects.count()
        total_farms = Farm.objects.count()
        recent_logs = list(AdminActivityLog.objects.all()[:8])
        recent_crops = list(Crop.objects.order_by("-created_at")[:5])
    except Exception:
        db_error = "Some legacy data is unavailable on the configured database backend."
    context = {
        "total_crops": total_crops,
        "published_crops": published_crops,
        "total_guides": total_guides,
        "published_guides": published_guides,
        "total_farmers": total_farmers,
        "total_farms": total_farms,
        "recent_logs": recent_logs,
        "recent_crops": recent_crops,
        "db_error": db_error,
    }
    return render(request, "core/pages/legacy_dashboard.html", context)


@login_required
def legacy_crop_list(request):
    """Legacy crops catalogue from unmanaged Supabase `crops` table. Degrades gracefully if unavailable."""
    category_id = request.GET.get("category")
    search_query = request.GET.get("q", "").strip()

    crops = []
    categories = []
    db_error = None
    try:
        crops_qs = Crop.objects.all()
        if category_id:
            crops_qs = crops_qs.filter(category_id=category_id)
        if search_query:
            crops_qs = crops_qs.filter(Q(name_ne__icontains=search_query) | Q(name_en__icontains=search_query))
        crops = list(crops_qs)
        categories = list(CropCategory.objects.all())
    except Exception:
        db_error = "Legacy crop catalogue is currently unavailable on the configured database backend."
    return render(
        request,
        "core/pages/legacy_crops_list.html",
        {
            "crops": crops,
            "categories": categories,
            "selected_category": category_id,
            "search_query": search_query,
            "db_error": db_error,
        },
    )


@login_required
def legacy_crop_create(request):
    """Create a new legacy crop record in unmanaged Supabase `crops` table."""
    categories = []
    db_error = None
    try:
        categories = list(CropCategory.objects.all())
    except Exception:
        db_error = "Crop categories are currently unavailable on the configured database backend."

    if request.method == "POST":
        if db_error:
            messages.error(request, db_error)
            return render(request, "core/pages/legacy_crop_form.html", {"categories": categories}, status=400)

        form = LegacyCropForm(request.POST)
        if not form.is_valid():
            messages.error(request, "नेपाली नाम, अंग्रेजी नाम, वैज्ञानिक नाम र विवरण अनिवार्य छन्।")
            return render(request, "core/pages/legacy_crop_form.html", {"categories": categories}, status=400)

        data = form.cleaned_data
        crop_id = data["id"].strip() or f"crop_{data['name_en'].lower().replace(' ', '_')}"
        try:
            if Crop.objects.filter(id=crop_id).exists():
                messages.error(request, f"बाली आइडी '{crop_id}' पहिले नै दर्ता भइसकेको छ।")
            else:
                category = CropCategory.objects.filter(id=data["category_id"]).first() if data["category_id"] else None
                crop = Crop.objects.create(
                    id=crop_id,
                    name_ne=data["name_ne"].strip(),
                    name_en=data["name_en"].strip(),
                    scientific_name=data["scientific_name"].strip(),
                    category=category,
                    description_ne=data["description_ne"].strip(),
                    growth_duration_days_min=data["growth_duration_days_min"] or 90,
                    growth_duration_days_max=data["growth_duration_days_max"] or 150,
                    optimal_temp_celsius=data["optimal_temp_celsius"].strip(),
                    soil_type=data["soil_type"].strip(),
                    is_published=bool(data["is_published"]),
                )
                AdminActivityLog.objects.create(
                    admin_email=request.user.email or request.user.username,
                    action="CREATE_CROP",
                    target_type="Crop",
                    target_id=crop.id,
                    details=f"Created crop {crop.name_ne}",
                )
                messages.success(request, f"नयाँ बाली '{crop.name_ne}' सफलतापूर्वक थपियो।")
                return redirect("core:legacy_crops_list")
        except Exception as exc:
            messages.error(request, f"बाली सिर्जना गर्न असफल भयो: {exc}")

    return render(request, "core/pages/legacy_crop_form.html", {"categories": categories})


@login_required
def legacy_crop_edit(request, crop_id: str):
    """Edit an existing legacy crop record in unmanaged Supabase `crops` table."""
    categories = []
    db_error = None
    try:
        categories = list(CropCategory.objects.all())
        crop = get_object_or_404(Crop, id=crop_id)
    except Exception as exc:
        messages.error(request, f"बाली सम्पादन गर्न सकिएन: {exc}")
        return redirect("core:legacy_crops_list")

    if request.method == "POST":
        form = LegacyCropForm(request.POST)
        if not form.is_valid():
            messages.error(request, "नेपाली नाम, अंग्रेजी नाम, वैज्ञानिक नाम र विवरण अनिवार्य छन्।")
            return render(
                request,
                "core/pages/legacy_crop_form.html",
                {"crop": crop, "categories": categories},
                status=400,
            )

        data = form.cleaned_data
        try:
            crop.name_ne = data["name_ne"].strip()
            crop.name_en = data["name_en"].strip()
            crop.scientific_name = data["scientific_name"].strip()
            crop.category = CropCategory.objects.filter(id=data["category_id"]).first() if data["category_id"] else None
            crop.description_ne = data["description_ne"].strip()
            crop.growth_duration_days_min = data["growth_duration_days_min"] or crop.growth_duration_days_min
            crop.growth_duration_days_max = data["growth_duration_days_max"] or crop.growth_duration_days_max
            crop.optimal_temp_celsius = data["optimal_temp_celsius"].strip()
            crop.soil_type = data["soil_type"].strip()
            crop.is_published = bool(data["is_published"])
            crop.save()

            AdminActivityLog.objects.create(
                admin_email=request.user.email or request.user.username,
                action="UPDATE_CROP",
                target_type="Crop",
                target_id=crop.id,
                details=f"Updated crop {crop.name_ne}",
            )
            messages.success(request, f"बाली '{crop.name_ne}' विवरण सफलतापूर्वक अद्यावधिक भयो।")
            return redirect("core:legacy_crops_list")
        except Exception as exc:
            messages.error(request, f"बाली अद्यावधिक गर्न असफल भयो: {exc}")

    return render(request, "core/pages/legacy_crop_form.html", {"crop": crop, "categories": categories})


@require_POST
@login_required
def legacy_crop_toggle_publish(request, crop_id: str):
    """Toggle publish status of a legacy crop in unmanaged Supabase `crops` table."""
    try:
        crop = get_object_or_404(Crop, id=crop_id)
        crop.is_published = not crop.is_published
        crop.save()
        status_text = "प्रकाशित (Published)" if crop.is_published else "अप्रकाशित (Draft)"
        AdminActivityLog.objects.create(
            admin_email=request.user.email or request.user.username,
            action="TOGGLE_PUBLISH_CROP",
            target_type="Crop",
            target_id=crop.id,
            details=f"Crop {crop.name_ne} set to {status_text}",
        )
        messages.success(request, f"बाली '{crop.name_ne}' को स्थिति '{status_text}' बनाइयो।")
    except Exception as exc:
        messages.error(request, f"स्थिति परिवर्तन गर्न असफल भयो: {exc}")
    return redirect("core:legacy_crops_list")


@require_POST
@login_required
def legacy_crop_delete(request, crop_id: str):
    """Delete a legacy crop record from unmanaged Supabase `crops` table."""
    try:
        crop = get_object_or_404(Crop, id=crop_id)
        name = crop.name_ne
        crop.delete()
        AdminActivityLog.objects.create(
            admin_email=request.user.email or request.user.username,
            action="DELETE_CROP",
            target_type="Crop",
            target_id=crop_id,
            details=f"Deleted crop {name}",
        )
        messages.success(request, f"बाली '{name}' हटाइयो।")
    except Exception as exc:
        messages.error(request, f"बाली हटाउन असफल भयो: {exc}")
    return redirect("core:legacy_crops_list")


@login_required
def legacy_guide_list(request):
    """Legacy cultivation guides list from unmanaged Supabase `guides` table. Degrades gracefully if unavailable."""
    crop_id = request.GET.get("crop")
    guides = []
    crops = []
    db_error = None
    try:
        guides_qs = Guide.objects.all().select_related("crop", "topic", "season", "region")
        if crop_id:
            guides_qs = guides_qs.filter(crop_id=crop_id)
        guides = list(guides_qs)
        crops = list(Crop.objects.all())
    except Exception:
        db_error = "Legacy cultivation guides are currently unavailable on the configured database backend."
    return render(
        request,
        "core/pages/legacy_guides_list.html",
        {"guides": guides, "crops": crops, "selected_crop": crop_id, "db_error": db_error},
    )


@login_required
def legacy_guide_create(request):
    """Create a new legacy cultivation guide in unmanaged Supabase `guides` table."""
    crops = []
    topics = []
    seasons = []
    regions = []
    db_error = None
    try:
        crops = list(Crop.objects.all())
        topics = list(Topic.objects.all())
        seasons = list(Season.objects.all())
        regions = list(Region.objects.all())
    except Exception:
        db_error = "Guide reference data is currently unavailable on the configured database backend."

    if request.method == "POST":
        if db_error:
            messages.error(request, db_error)
            return render(
                request,
                "core/pages/legacy_guide_form.html",
                {"crops": crops, "topics": topics, "seasons": seasons, "regions": regions},
                status=400,
            )

        form = LegacyGuideForm(request.POST)
        if not form.is_valid():
            messages.error(request, "बाली, शीर्षक र सारांश अनिवार्य छन्।")
            return render(
                request,
                "core/pages/legacy_guide_form.html",
                {"crops": crops, "topics": topics, "seasons": seasons, "regions": regions},
                status=400,
            )

        data = form.cleaned_data
        try:
            crop = get_object_or_404(Crop, id=data["crop_id"])
            guide_id = data["id"].strip()
            if not guide_id:
                base_id = f"guide_{crop.name_en.lower().replace(' ', '_')}"
                guide_id = (
                    f"{base_id}_{uuid.uuid4().hex[:6]}" if Guide.objects.filter(id=base_id).exists() else base_id
                )
            elif Guide.objects.filter(id=guide_id).exists():
                messages.error(request, f"निर्देशिका आइडी '{guide_id}' पहिले नै अवस्थित छ।")
                return render(
                    request,
                    "core/pages/legacy_guide_form.html",
                    {"crops": crops, "topics": topics, "seasons": seasons, "regions": regions},
                    status=400,
                )

            guide = Guide.objects.create(
                id=guide_id,
                crop=crop,
                title_ne=data["title_ne"].strip(),
                title_en=data["title_en"].strip(),
                summary_ne=data["summary_ne"].strip(),
                topic=Topic.objects.filter(id=data["topic_id"]).first() if data["topic_id"] else None,
                season=Season.objects.filter(id=data["season_id"]).first() if data["season_id"] else None,
                region=Region.objects.filter(id=data["region_id"]).first() if data["region_id"] else None,
                is_published=bool(data["is_published"]),
            )
            AdminActivityLog.objects.create(
                admin_email=request.user.email or request.user.username,
                action="CREATE_GUIDE",
                target_type="Guide",
                target_id=guide.id,
                details=f"Created guide {guide.title_ne}",
            )
            messages.success(request, f"नयाँ निर्देशिका '{guide.title_ne}' थपियो।")
            return redirect("core:legacy_guide_detail", guide_id=guide.id)
        except Exception as exc:
            messages.error(request, f"निर्देशिका सिर्जना गर्न असफल भयो: {exc}")

    return render(
        request,
        "core/pages/legacy_guide_form.html",
        {"crops": crops, "topics": topics, "seasons": seasons, "regions": regions},
    )


@login_required
def legacy_guide_detail(request, guide_id: str):
    """Legacy guide detail page showing all steps from unmanaged Supabase tables."""
    guide = None
    steps = []
    db_error = None
    try:
        guide = get_object_or_404(
            Guide.objects.select_related("crop", "topic", "season", "region"), id=guide_id
        )
        steps = list(guide.steps.all().order_by("step_number"))
    except Exception as exc:
        db_error = f"निर्देशिका विवरण लोड गर्न असफल भयो: {exc}"
    return render(request, "core/pages/legacy_guide_detail.html", {"guide": guide, "steps": steps, "db_error": db_error})


@login_required
def legacy_guide_step_create(request, guide_id: str):
    """Add a new step to a legacy cultivation guide in unmanaged Supabase `guide_steps` table."""
    guide = None
    db_error = None
    try:
        guide = get_object_or_404(Guide, id=guide_id)
    except Exception as exc:
        messages.error(request, f"निर्देशिका फेला परेन: {exc}")
        return redirect("core:legacy_guides_list")

    if request.method == "POST":
        form = LegacyGuideStepForm(request.POST)
        if not form.is_valid():
            messages.error(request, "चरण नम्बर, शीर्षक र कार्यविधि अनिवार्य छन्।")
            return render(
                request,
                "core/pages/legacy_guide_step_form.html",
                {"guide": guide, "next_step": guide.steps.count() + 1},
                status=400,
            )

        data = form.cleaned_data
        step_number = data["step_number"]
        try:
            GuideStep.objects.update_or_create(
                id=f"step_{guide.id}_{step_number}",
                defaults={
                    "guide": guide,
                    "step_number": step_number,
                    "title_ne": data["title_ne"].strip(),
                    "title_en": data["title_en"].strip(),
                    "content_ne": data["content_ne"].strip(),
                    "tips_ne": data["tips_ne"].strip(),
                },
            )
            messages.success(request, f"चरण {step_number} सफलतापूर्वक थपियो/अद्यावधिक भयो।")
            return redirect("core:legacy_guide_detail", guide_id=guide.id)
        except Exception as exc:
            messages.error(request, f"चरण सुरक्षित गर्न असफल भयो: {exc}")

    return render(
        request,
        "core/pages/legacy_guide_step_form.html",
        {"guide": guide, "next_step": guide.steps.count() + 1},
    )

    return render(
        request,
        "core/pages/legacy_guide_step_form.html",
        {"guide": guide, "next_step": guide.steps.count() + 1},
    )


@require_POST
@login_required
def legacy_guide_toggle_publish(request, guide_id: str):
    """Toggle publish status of a legacy guide in unmanaged Supabase `guides` table."""
    try:
        guide = get_object_or_404(Guide, id=guide_id)
        guide.is_published = not guide.is_published
        guide.save()
        status_text = "प्रकाशित (Published)" if guide.is_published else "अप्रकाशित (Draft)"
        messages.success(request, f"निर्देशिका '{guide.title_ne}' को स्थिति '{status_text}' बनाइयो।")
    except Exception as exc:
        messages.error(request, f"स्थिति परिवर्तन गर्न असफल भयो: {exc}")
    return redirect("core:legacy_guides_list")
