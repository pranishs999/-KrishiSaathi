import uuid
from django.conf import settings
from django.db import models
from django.utils import timezone
from .constants import ADMIN_ROLES, WORKFLOW_STATUSES


def _uuid_str():
    return str(uuid.uuid4())


class AdminProfile(models.Model):
    """Links Django auth users to explicit CMS RBAC roles."""

    user = models.OneToOneField(
        settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name="cms_profile"
    )
    role = models.CharField(max_length=32, choices=ADMIN_ROLES, default="content_editor")
    full_name = models.CharField(max_length=150)
    department = models.CharField(max_length=150, blank=True, default="Bakaiya Agriculture CMS")
    is_active_editor = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.full_name} ({self.role})"


class SourceDocument(models.Model):
    """Authoritative citation record (NARC, MoALD, AITC Nepal, Bakaiya Municipality)."""

    id = models.CharField(primary_key=True, max_length=64, default=_uuid_str)
    title = models.CharField(max_length=255)
    publisher = models.CharField(max_length=255)
    publication_year = models.PositiveIntegerField(null=True, blank=True)
    url = models.URLField(blank=True, default="")
    notes = models.TextField(blank=True, default="")
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.title} ({self.publisher})"


class MediaAsset(models.Model):
    """Validated media asset with derivative paths and strict CC/official attribution."""

    id = models.CharField(primary_key=True, max_length=64, default=_uuid_str)
    title_en = models.CharField(max_length=200)
    caption_ne = models.CharField(max_length=255, blank=True, default="")
    target_kind = models.CharField(
        max_length=20,
        choices=[("crop", "Crop"), ("livestock", "Livestock"), ("general", "General")],
        default="general",
    )
    original_path = models.CharField(max_length=500, blank=True, default="")
    medium_path = models.CharField(max_length=500, blank=True, default="")
    thumb_path = models.CharField(max_length=500, blank=True, default="")
    remote_url = models.URLField(blank=True, default="")
    mime_type = models.CharField(max_length=64, blank=True, default="")
    width_px = models.PositiveIntegerField(null=True, blank=True)
    height_px = models.PositiveIntegerField(null=True, blank=True)
    file_size_bytes = models.PositiveIntegerField(null=True, blank=True)
    creator_name = models.CharField(max_length=200, blank=True, default="")
    license_type = models.CharField(max_length=120, blank=True, default="")
    source_url = models.URLField(blank=True, default="")
    is_verified = models.BooleanField(default=False)
    uploaded_by = models.ForeignKey(
        settings.AUTH_USER_MODEL, null=True, blank=True, on_delete=models.SET_NULL
    )
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.title_en


class CropEntry(models.Model):
    """Editorial Crop entry synchronized to farmer clients once published."""

    id = models.CharField(primary_key=True, max_length=64, default=_uuid_str)
    category_id = models.CharField(max_length=64, default="cat-cereal")
    name_ne = models.CharField(max_length=120)
    name_en = models.CharField(max_length=120)
    scientific_name = models.CharField(max_length=150, blank=True, default="")
    description_ne = models.TextField()
    varieties = models.TextField(blank=True, default="")
    planting_season_ne = models.CharField(max_length=200, blank=True, default="")
    altitude_summary_ne = models.CharField(max_length=200, blank=True, default="")
    soil_types_ne = models.CharField(max_length=255, blank=True, default="")
    source_reference = models.CharField(max_length=255, blank=True, default="")
    source_document = models.ForeignKey(
        SourceDocument, null=True, blank=True, on_delete=models.SET_NULL
    )
    thumbnail_url = models.URLField(blank=True, default="")
    review_status = models.CharField(max_length=32, choices=WORKFLOW_STATUSES, default="draft")
    is_published = models.BooleanField(default=False)
    published_at = models.DateTimeField(null=True, blank=True)
    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="created_crops",
    )
    reviewed_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="reviewed_crops",
    )
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.name_en} ({self.review_status})"


class LivestockEntry(models.Model):
    """Editorial Livestock species entry with strict veterinary verification workflow."""

    id = models.CharField(primary_key=True, max_length=64, default=_uuid_str)
    category_id = models.CharField(max_length=64, default="acat-cattle-buffalo")
    species_code = models.CharField(max_length=40, unique=True)
    name_ne = models.CharField(max_length=120)
    name_en = models.CharField(max_length=120)
    scientific_name = models.CharField(max_length=150, blank=True, default="")
    summary_ne = models.TextField()
    purpose_tags_ne = models.CharField(max_length=255, blank=True, default="")
    bakaiya_relevance_ne = models.TextField(blank=True, default="")
    climate_suitability_ne = models.TextField(blank=True, default="")
    altitude_range_ne = models.CharField(max_length=150, blank=True, default="")
    recommended_breeds_summary_ne = models.TextField(blank=True, default="")
    safety_notice_ne = models.TextField(
        default="पशु स्वास्थ्य वा औषधि मात्रा सम्बन्धी अन्तिम निर्णय नजिकको पशु सेवा प्राविधिक वा पशु चिकित्सकको प्रत्यक्ष जाँचपछि मात्र लिनुहोस्।"
    )
    source_reference = models.CharField(max_length=255, blank=True, default="")
    source_document = models.ForeignKey(
        SourceDocument, null=True, blank=True, on_delete=models.SET_NULL
    )
    thumbnail_url = models.URLField(blank=True, default="")
    review_status = models.CharField(max_length=32, choices=WORKFLOW_STATUSES, default="draft")
    is_published = models.BooleanField(default=False)
    published_at = models.DateTimeField(null=True, blank=True)
    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="created_animals",
    )
    reviewed_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="reviewed_animals",
    )
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.name_en} [{self.species_code}] ({self.review_status})"


class LivestockSectionContent(models.Model):
    """Structured 25-section veterinary-reviewed guidance per species."""

    animal = models.ForeignKey(
        LivestockEntry, on_delete=models.CASCADE, related_name="sections"
    )
    section_number = models.PositiveSmallIntegerField()
    section_key = models.CharField(max_length=64)
    title_en = models.CharField(max_length=200)
    body_ne = models.TextField(blank=True, default="")
    body_en = models.TextField(blank=True, default="")
    is_applicable = models.BooleanField(default=True)
    source_reference = models.CharField(max_length=255, blank=True, default="")
    review_status = models.CharField(max_length=32, choices=WORKFLOW_STATUSES, default="draft")
    is_published = models.BooleanField(default=False)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        unique_together = ("animal", "section_number")
        ordering = ["section_number"]

    def __str__(self):
        return f"{self.animal.species_code} §{self.section_number}: {self.title_en}"


class HelplineContactEntry(models.Model):
    """Official agriculture/veterinary helpline contacts (verified only)."""

    id = models.CharField(primary_key=True, max_length=64, default=_uuid_str)
    name_ne = models.CharField(max_length=150)
    designation_ne = models.CharField(max_length=200)
    department_en = models.CharField(max_length=200)
    ward_ne = models.CharField(max_length=200)
    mobile_number = models.CharField(max_length=32)
    email = models.EmailField(blank=True, default="")
    source_reference = models.CharField(max_length=255)
    is_verified = models.BooleanField(default=False)
    is_published = models.BooleanField(default=False)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.department_en} ({self.mobile_number})"


class ContentAuditLog(models.Model):
    """Immutable workflow & security audit trail."""

    actor = models.ForeignKey(
        settings.AUTH_USER_MODEL, null=True, blank=True, on_delete=models.SET_NULL
    )
    action = models.CharField(max_length=80)
    target_type = models.CharField(max_length=64)
    target_id = models.CharField(max_length=64)
    from_status = models.CharField(max_length=32, blank=True, default="")
    to_status = models.CharField(max_length=32, blank=True, default="")
    notes = models.TextField(blank=True, default="")
    ip_address = models.GenericIPAddressField(null=True, blank=True)
    created_at = models.DateTimeField(default=timezone.now)

    class Meta:
        ordering = ["-created_at"]
