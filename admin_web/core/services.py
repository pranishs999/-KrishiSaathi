"""Business services: RBAC, security headers, editorial workflow, Supabase mirror, and media pipeline."""

from functools import wraps
import io
import logging
from pathlib import Path

from django.core.cache import cache
from django.core.exceptions import PermissionDenied, ValidationError
from django.db import DatabaseError, connection
from django.http import HttpResponse, HttpResponseForbidden
from django.shortcuts import redirect
from django.utils import timezone
from PIL import Image, ImageOps

from .constants import ALLOWED_TRANSITIONS, ROLE_PERMISSIONS
from .models import ContentAuditLog

logger = logging.getLogger(__name__)

ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/webp"}
MAX_IMAGE_BYTES = 5 * 1024 * 1024  # 5 MB hard cap
MAGIC_HEADERS = {
    b"\xff\xd8\xff": "image/jpeg",
    b"\x89PNG\r\n\x1a\n": "image/png",
}


# ---------------------------------------------------------------------------
# Security Headers, Login Throttling & RBAC
# ---------------------------------------------------------------------------


class CmsSecurityHeadersMiddleware:
    """Adds baseline security and no-store cache headers on CMS/admin routes."""

    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        response = self.get_response(request)
        response["X-Content-Type-Options"] = "nosniff"
        response["X-Frame-Options"] = "DENY"
        response["Referrer-Policy"] = "strict-origin-when-cross-origin"
        if request.path.startswith("/cms/") or request.path.startswith("/django-admin/"):
            response["Cache-Control"] = "no-store, no-cache, must-revalidate, max-age=0"
        return response


def get_client_ip(request) -> str | None:
    forwarded = request.META.get("HTTP_X_FORWARDED_FOR")
    if forwarded:
        return forwarded.split(",")[0].strip()
    return request.META.get("REMOTE_ADDR")


def check_login_rate_limit(request, max_attempts: int = 8) -> bool:
    """Returns True if login attempt is permitted; False when throttled."""
    ip = get_client_ip(request) or "unknown"
    return cache.get(f"cms_login_attempts:{ip}", 0) < max_attempts


def record_failed_login(request, window_seconds: int = 300) -> None:
    ip = get_client_ip(request) or "unknown"
    key = f"cms_login_attempts:{ip}"
    cache.set(key, cache.get(key, 0) + 1, timeout=window_seconds)


def clear_failed_logins(request) -> None:
    ip = get_client_ip(request) or "unknown"
    cache.delete(f"cms_login_attempts:{ip}")


def rate_limit_exceeded_response():
    return HttpResponse(
        "Too many failed login attempts. Please wait 5 minutes before retrying.",
        status=429,
    )


def get_user_role(user) -> str:
    """Resolve CMS RBAC role from user's AdminProfile or superuser flag."""
    if not user or not user.is_authenticated:
        return ""
    if getattr(user, "is_superuser", False):
        return "super_admin"
    profile = getattr(user, "cms_profile", None)
    if profile and profile.is_active_editor and profile.status == "active":
        return profile.role
    return ""


def get_user_profile(user):
    """Get the user's AdminProfile if exists."""
    if not user or not user.is_authenticated:
        return None
    return getattr(user, "cms_profile", None)


def has_cms_permission(user, permission: str) -> bool:
    role = get_user_role(user)
    return permission in ROLE_PERMISSIONS.get(role, set())


def cms_role_required(permission: str | None = None):
    """Requires authenticated admin session plus an active CMS role (and optional permission)."""

    def decorator(view_func):
        @wraps(view_func)
        def _wrapped(request, *args, **kwargs):
            if not request.user.is_authenticated:
                return redirect("core:login")
            role = get_user_role(request.user)
            if not role:
                return HttpResponseForbidden("Forbidden: Active CMS role required.")
            if permission and not has_cms_permission(request.user, permission):
                return HttpResponseForbidden(f"Forbidden: Missing permission '{permission}'.")
            return view_func(request, *args, **kwargs)

        return _wrapped

    return decorator


def cms_role_required_any(permissions: list):
    """Requires at least one of the given permissions."""
    def decorator(view_func):
        @wraps(view_func)
        def _wrapped(request, *args, **kwargs):
            if not request.user.is_authenticated:
                return redirect("core:login")
            role = get_user_role(request.user)
            if not role:
                return HttpResponseForbidden("Forbidden: Active CMS role required.")
            if not any(has_cms_permission(request.user, p) for p in permissions):
                return HttpResponseForbidden(f"Forbidden: Missing required permissions.")
            return view_func(request, *args, **kwargs)
        return _wrapped
    return decorator


# Hierarchical permission checks
def can_manage_user(request_user, target_user) -> bool:
    """Check if request_user can manage target_user based on hierarchy."""
    if not request_user.is_authenticated:
        return False
    if getattr(request_user, "is_superuser", False):
        return True
    profile = getattr(request_user, "cms_profile", None)
    if not profile or not profile.is_active_editor or profile.status != "active":
        return False
    if profile.role == "super_admin":
        return True
    if profile.role == "staff_manager":
        target_profile = getattr(target_user, "cms_profile", None)
        if target_profile and target_profile.managed_by_id == profile.id:
            return True
    return False


def can_access_content(request_user, content_owner) -> bool:
    """Check if request_user can access content owned by content_owner."""
    if not request_user.is_authenticated:
        return False
    if getattr(request_user, "is_superuser", False):
        return True
    profile = getattr(request_user, "cms_profile", None)
    if not profile or not profile.is_active_editor or profile.status != "active":
        return False
    if profile.role == "super_admin":
        return True
    if profile.role == "staff_manager":
        target_profile = getattr(content_owner, "cms_profile", None)
        if target_profile and target_profile.managed_by_id == profile.id:
            return True
    # Staff can only access their own content
    return request_user.id == content_owner.id


def get_user_team(request_user):
    """Get the team members for a Staff Manager."""
    profile = get_user_profile(request_user)
    if profile and profile.role == "staff_manager":
        return profile.get_team_members()
    return AdminProfile.objects.none()


def is_super_admin(user) -> bool:
    """Check if user is Super Admin."""
    if not user or not user.is_authenticated:
        return False
    if getattr(user, "is_superuser", False):
        return True
    profile = getattr(user, "cms_profile", None)
    return profile and profile.role == "super_admin" and profile.is_active_editor and profile.status == "active"


def is_staff_manager(user) -> bool:
    """Check if user is Staff Manager."""
    if not user or not user.is_authenticated:
        return False
    profile = getattr(user, "cms_profile", None)
    return profile and profile.role == "staff_manager" and profile.is_active_editor and profile.status == "active"


def is_staff(user) -> bool:
    """Check if user is Staff."""
    if not user or not user.is_authenticated:
        return False
    profile = getattr(user, "cms_profile", None)
    return profile and profile.role == "staff" and profile.is_active_editor and profile.status == "active"


# ---------------------------------------------------------------------------
# Editorial Verification Workflow
# ---------------------------------------------------------------------------


def transition_entry_status(
    entry,
    target_status: str,
    actor,
    target_type: str,
    notes: str = "",
    ip_address: str | None = None,
):
    """
    Validates and transitions a CropEntry or LivestockEntry through the editorial workflow.

    Enforces allowed transition edges, role permissions, source citations, and
    publishes applicable livestock sections alongside the parent species.
    """
    current_status = entry.review_status
    if target_status not in ALLOWED_TRANSITIONS.get(current_status, set()):
        raise ValidationError(
            f"Invalid status transition from '{current_status}' to '{target_status}'."
        )

    role = get_user_role(actor)
    if not role:
        raise PermissionDenied("Active CMS role required.")

    if target_status in {"in_review", "needs_source"} and not has_cms_permission(
        actor, "submit_review"
    ):
        raise PermissionDenied("Submit-for-review permission required.")
    if target_status == "approved" and not has_cms_permission(actor, "approve_content"):
        raise PermissionDenied("Reviewer or Super Admin permission required to approve content.")
    if target_status == "published" and not has_cms_permission(actor, "publish_content"):
        raise PermissionDenied("Reviewer or Super Admin permission required to publish content.")
    if target_status == "archived" and not has_cms_permission(actor, "archive_content"):
        raise PermissionDenied("Super Admin permission required to archive content.")

    if target_status in {"in_review", "approved", "published"}:
        if not (entry.source_reference or "").strip() and not getattr(
            entry, "source_document_id", None
        ):
            raise ValidationError(
                "A verified source reference or source document is required before review or publication."
            )

    if (
        target_status in {"approved", "published"}
        and role != "super_admin"
        and getattr(entry, "created_by_id", None) == actor.id
    ):
        raise PermissionDenied(
            "Authors cannot approve or publish their own submission; another reviewer must sign off."
        )

    entry.review_status = target_status
    if target_status in {"approved", "published"}:
        entry.reviewed_by = actor
    if target_status == "published":
        entry.is_published = True
        entry.published_at = timezone.now()
    else:
        entry.is_published = False

    entry.save()
    if target_type == "livestock" and hasattr(entry, "sections"):
        if target_status == "published":
            entry.sections.filter(is_applicable=True).exclude(body_ne="").update(
                review_status="published", is_published=True
            )
        else:
            entry.sections.update(is_published=False)

    mirror_published_entry(entry, target_type)

    ContentAuditLog.objects.create(
        actor=actor,
        action=f"workflow_transition_{target_status}",
        target_type=target_type,
        target_id=str(entry.pk),
        from_status=current_status,
        to_status=target_status,
        notes=notes,
        ip_address=ip_address,
    )
    return entry


# ---------------------------------------------------------------------------
# Supabase Farmer Catalog Mirroring
# ---------------------------------------------------------------------------


def mirror_published_entry(entry, target_type: str) -> None:
    try:
        if target_type == "crop":
            _mirror_crop(entry)
        elif target_type == "livestock":
            _mirror_animal(entry)
    except DatabaseError:
        logger.warning("Farmer catalog mirror skipped for %s %s", target_type, getattr(entry, "pk", ""))


def mirror_helpline(contact) -> None:
    published = bool(contact.is_published and contact.is_verified)
    ward_number = _ward_number(contact.ward_ne)
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                """
                INSERT INTO public.helpline_contacts (
                    id, name_ne, designation_ne, ward_ne, ward_number,
                    mobile_number, email, is_verified, is_published
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (id) DO UPDATE SET
                    name_ne = EXCLUDED.name_ne,
                    designation_ne = EXCLUDED.designation_ne,
                    ward_ne = EXCLUDED.ward_ne,
                    ward_number = EXCLUDED.ward_number,
                    mobile_number = EXCLUDED.mobile_number,
                    email = EXCLUDED.email,
                    is_verified = EXCLUDED.is_verified,
                    is_published = EXCLUDED.is_published
                """,
                [
                    contact.id,
                    contact.name_ne,
                    contact.designation_ne,
                    contact.ward_ne,
                    ward_number,
                    contact.mobile_number,
                    contact.email or None,
                    published,
                    published,
                ],
            )
    except DatabaseError:
        logger.warning("Helpline mirror skipped for %s", contact.pk)


def _mirror_crop(entry) -> None:
    published = bool(entry.is_published and entry.review_status == "published")
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO public.crops (
                id, category_id, name_ne, name_en, scientific_name, description_ne,
                varieties, planting_season, suitable_altitude, soil_types,
                source_reference, thumbnail_url, is_published, is_verified_content, updated_at
            )
            VALUES (
                %s,
                (SELECT id FROM public.crop_categories WHERE id = %s),
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, TRUE, NOW()
            )
            ON CONFLICT (id) DO UPDATE SET
                category_id = EXCLUDED.category_id,
                name_ne = EXCLUDED.name_ne,
                name_en = EXCLUDED.name_en,
                scientific_name = EXCLUDED.scientific_name,
                description_ne = EXCLUDED.description_ne,
                varieties = EXCLUDED.varieties,
                planting_season = EXCLUDED.planting_season,
                suitable_altitude = EXCLUDED.suitable_altitude,
                soil_types = EXCLUDED.soil_types,
                source_reference = EXCLUDED.source_reference,
                thumbnail_url = EXCLUDED.thumbnail_url,
                is_published = EXCLUDED.is_published,
                updated_at = NOW()
            """,
            [
                entry.id,
                entry.category_id,
                entry.name_ne,
                entry.name_en,
                entry.scientific_name or entry.name_en,
                entry.description_ne,
                entry.varieties or None,
                entry.planting_season_ne or None,
                entry.altitude_summary_ne or None,
                entry.soil_types_ne or None,
                entry.source_reference or "",
                entry.thumbnail_url or None,
                published,
            ],
        )


def _mirror_animal(entry) -> None:
    published = bool(entry.is_published and entry.review_status == "published")
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO public.animal_categories (id, name_ne, name_en, is_published)
            VALUES (%s, %s, %s, TRUE)
            ON CONFLICT (id) DO NOTHING
            """,
            [entry.category_id, entry.category_id, entry.category_id],
        )
        cursor.execute(
            """
            INSERT INTO public.animals (
                id, category_id, name_ne, name_en, scientific_name, species_code,
                summary_ne, purpose_tags_ne, bakaiya_relevance_ne, climate_suitability_ne,
                altitude_range_ne, recommended_breeds_summary_ne, safety_notice_ne,
                source_reference, thumbnail_url, review_status, is_published, is_verified_content,
                updated_at
            )
            VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW()
            )
            ON CONFLICT (id) DO UPDATE SET
                name_ne = EXCLUDED.name_ne,
                name_en = EXCLUDED.name_en,
                scientific_name = EXCLUDED.scientific_name,
                species_code = EXCLUDED.species_code,
                summary_ne = EXCLUDED.summary_ne,
                source_reference = EXCLUDED.source_reference,
                thumbnail_url = EXCLUDED.thumbnail_url,
                review_status = EXCLUDED.review_status,
                is_published = EXCLUDED.is_published,
                updated_at = NOW()
            """,
            [
                entry.id,
                entry.category_id,
                entry.name_ne,
                entry.name_en,
                entry.scientific_name or None,
                entry.species_code,
                entry.summary_ne,
                entry.purpose_tags_ne or None,
                entry.bakaiya_relevance_ne or None,
                entry.climate_suitability_ne or None,
                entry.altitude_range_ne or None,
                entry.recommended_breeds_summary_ne or None,
                entry.safety_notice_ne,
                entry.source_reference or "",
                entry.thumbnail_url or None,
                entry.review_status,
                published,
                published,
            ],
        )


def _ward_number(ward_text: str) -> int:
    digits = "".join(ch for ch in (ward_text or "") if ch.isdigit())
    if not digits:
        return 0
    return int(digits[:2])


# ---------------------------------------------------------------------------
# Media Validation & Derivative Pipeline
# ---------------------------------------------------------------------------


def detect_image_mime(raw_bytes: bytes) -> str:
    """Inspect magic header bytes instead of trusting filename or HTTP Content-Type."""
    for sig, mime in MAGIC_HEADERS.items():
        if raw_bytes.startswith(sig):
            return mime
    if raw_bytes[:4] == b"RIFF" and raw_bytes[8:12] == b"WEBP":
        return "image/webp"
    raise ValidationError(
        "Unsupported or unsafe file signature. Only JPEG, PNG, and WebP images are accepted."
    )


def process_uploaded_image(uploaded_file, storage_root: Path, asset_id: str) -> dict:
    """Validates raw image upload, strips EXIF metadata, and writes WebP derivatives."""
    if uploaded_file.size > MAX_IMAGE_BYTES:
        raise ValidationError("Image exceeds maximum 5 MB upload size.")

    raw_bytes = uploaded_file.read()
    mime_type = detect_image_mime(raw_bytes)

    try:
        with Image.open(io.BytesIO(raw_bytes)) as img:
            img.verify()
        with Image.open(io.BytesIO(raw_bytes)) as img:
            normalized = ImageOps.exif_transpose(img).convert("RGB")
            orig_w, orig_h = normalized.size

            storage_root.mkdir(parents=True, exist_ok=True)
            orig_file = storage_root / f"{asset_id}_orig.webp"
            med_file = storage_root / f"{asset_id}_med.webp"
            thumb_file = storage_root / f"{asset_id}_thumb.webp"

            normalized.save(orig_file, format="WEBP", quality=88, method=6)

            med_copy = normalized.copy()
            med_copy.thumbnail((800, 800))
            med_copy.save(med_file, format="WEBP", quality=82, method=6)

            thumb_copy = normalized.copy()
            thumb_copy.thumbnail((320, 320))
            thumb_copy.save(thumb_file, format="WEBP", quality=78, method=6)

            return {
                "mime_type": mime_type,
                "width_px": orig_w,
                "height_px": orig_h,
                "file_size_bytes": len(raw_bytes),
                "original_path": str(orig_file.name),
                "medium_path": str(med_file.name),
                "thumb_path": str(thumb_file.name),
            }
    except ValidationError:
        raise
    except Exception as exc:
        raise ValidationError(f"Corrupted or invalid image payload: {exc}") from exc
