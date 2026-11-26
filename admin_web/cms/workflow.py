"""Editorial verification workflow engine enforcing RBAC, source citations, and state transitions."""

from django.core.exceptions import PermissionDenied, ValidationError
from django.utils import timezone
from .constants import ALLOWED_TRANSITIONS, ROLE_PERMISSIONS
from .models import ContentAuditLog


def get_user_role(user) -> str:
    """Resolve CMS RBAC role from user's AdminProfile or superuser flag."""
    if not user or not user.is_authenticated:
        return ""
    if getattr(user, "is_superuser", False):
        return "super_admin"
    profile = getattr(user, "cms_profile", None)
    if profile and profile.is_active_editor:
        return profile.role
    return ""


def has_cms_permission(user, permission: str) -> bool:
    role = get_user_role(user)
    return permission in ROLE_PERMISSIONS.get(role, set())


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

    Enforces:
    - Allowed state machine transition edges.
    - Role permissions for approve / publish / archive.
    - Non-empty source_reference before entering in_review / approved / published.
    - Published content flag sync (`is_published = (target_status == 'published')`).
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

    # Source provenance gate
    if target_status in {"in_review", "approved", "published"}:
        if not (entry.source_reference or "").strip() and not getattr(
            entry, "source_document_id", None
        ):
            raise ValidationError(
                "A verified source reference or source document is required before review or publication."
            )

    # Prevent unverified self-approval by non-super-admins
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
