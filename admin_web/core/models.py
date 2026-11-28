"""Consolidated database models for Bakaiya Krishi Saathi CMS and Supabase public tables."""

import uuid
from django.conf import settings
from django.db import models
from django.utils import timezone

from .constants import ADMIN_ROLES, WORKFLOW_STATUSES


def _uuid_str():
    return str(uuid.uuid4())


# ---------------------------------------------------------------------------
# Managed CMS Models (tables prefixed with cms_ preserved for compatibility)
# ---------------------------------------------------------------------------


class AdminProfile(models.Model):
    """Links Django auth users to explicit CMS RBAC roles with hierarchical team management."""

    ROLE_SUPER_ADMIN = "super_admin"
    ROLE_STAFF_MANAGER = "staff_manager"
    ROLE_STAFF = "staff"

    ROLE_CHOICES = [
        (ROLE_SUPER_ADMIN, "Super Admin"),
        (ROLE_STAFF_MANAGER, "Staff Manager"),
        (ROLE_STAFF, "Staff"),
    ]

    STATUS_ACTIVE = "active"
    STATUS_SUSPENDED = "suspended"
    STATUS_INACTIVE = "inactive"
    STATUS_ARCHIVED = "archived"

    STATUS_CHOICES = [
        (STATUS_ACTIVE, "Active"),
        (STATUS_SUSPENDED, "Suspended"),
        (STATUS_INACTIVE, "Inactive"),
        (STATUS_ARCHIVED, "Archived"),
    ]

    user = models.OneToOneField(
        settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name="cms_profile"
    )
    role = models.CharField(max_length=32, choices=ROLE_CHOICES, default=ROLE_STAFF)
    full_name = models.CharField(max_length=150)
    department = models.CharField(max_length=150, blank=True, default="Bakaiya Agriculture CMS")
    mobile_number = models.CharField(max_length=32, blank=True, default="")
    email = models.EmailField(blank=True, default="")
    profile_image = models.ImageField(upload_to="cms/profile_images/", blank=True, null=True)
    status = models.CharField(max_length=16, choices=STATUS_CHOICES, default=STATUS_ACTIVE)
    is_active_editor = models.BooleanField(default=True)

    # Hierarchical relationship: Staff Manager manages Staff
    managed_by = models.ForeignKey(
        "self",
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name="managed_staff",
        help_text="Staff Manager who manages this staff member (only for Staff role)"
    )

    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name="created_admin_profiles",
    )
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = "cms_adminprofile"
        ordering = ["role", "full_name"]

    def __str__(self):
        return f"{self.full_name} ({self.get_role_display()})"

    def get_team_members(self):
        """Return staff members managed by this Staff Manager."""
        if self.role == self.ROLE_STAFF_MANAGER:
            return AdminProfile.objects.filter(managed_by=self, role=self.ROLE_STAFF)
        return AdminProfile.objects.none()

    def can_manage_user(self, target_user):
        """Check if this user can manage the target user."""
        if not self.is_active_editor or self.status != self.STATUS_ACTIVE:
            return False
        if self.role == self.ROLE_SUPER_ADMIN:
            return True
        if self.role == self.ROLE_STAFF_MANAGER:
            # Can manage their own staff
            return target_user.cms_profile.managed_by_id == self.id if hasattr(target_user, 'cms_profile') else False
        return False

    def can_access_content(self, content_user):
        """Check if this user can access content created by content_user."""
        if self.role == self.ROLE_SUPER_ADMIN:
            return True
        if self.role == self.ROLE_STAFF_MANAGER:
            # Can access content from their team
            return content_user.cms_profile.managed_by_id == self.id if hasattr(content_user, 'cms_profile') else False
        # Staff can only access their own content
        return content_user.id == self.user.id


class SourceDocument(models.Model):
    """Authoritative citation record (NARC, MoALD, AITC Nepal, Bakaiya Municipality)."""

    id = models.CharField(primary_key=True, max_length=64, default=_uuid_str)
    title = models.CharField(max_length=255)
    publisher = models.CharField(max_length=255)
    publication_year = models.PositiveIntegerField(null=True, blank=True)
    url = models.URLField(blank=True, default="")
    notes = models.TextField(blank=True, default="")
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = "cms_sourcedocument"

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

    class Meta:
        db_table = "cms_mediaasset"

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

    class Meta:
        db_table = "cms_cropentry"

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

    class Meta:
        db_table = "cms_livestockentry"

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
        db_table = "cms_livestocksectioncontent"
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

    class Meta:
        db_table = "cms_helplinecontactentry"

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
        db_table = "cms_contentauditlog"
        ordering = ["-created_at"]


# ---------------------------------------------------------------------------
# Unmanaged Supabase Public Models (managed = False; schema owned by Supabase)
# ---------------------------------------------------------------------------


class FarmerProfile(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    phone = models.CharField(max_length=20, unique=True)
    full_name = models.CharField(max_length=150)
    mpin_hash = models.CharField(max_length=255, null=True, blank=True)
    municipality = models.CharField(max_length=100, default="बकैया गाउँपालिका, मकवानपुर")
    ward_number = models.IntegerField(default=1)
    tole_village = models.CharField(max_length=200, null=True, blank=True)
    consent_status = models.BooleanField(default=False)
    registered_at = models.BigIntegerField(null=True, blank=True)
    latitude = models.FloatField(null=True, blank=True)
    longitude = models.FloatField(null=True, blank=True)
    land_unit_preference = models.CharField(max_length=20, default="ropani")
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = "farmer_profiles"
        managed = False
        verbose_name = "Farmer Profile"
        verbose_name_plural = "Farmer Profiles"

    def __str__(self):
        return f"{self.full_name} ({self.phone})"


class Farm(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    farmer = models.ForeignKey(
        FarmerProfile, on_delete=models.CASCADE, db_column="farmer_id", related_name="farms"
    )
    name = models.CharField(max_length=150)
    location = models.CharField(max_length=255)
    total_area = models.FloatField(default=0.0)
    area_unit = models.CharField(max_length=30, default="ropani")
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = "farms"
        managed = False
        verbose_name = "Farm"
        verbose_name_plural = "Farms"

    def __str__(self):
        return f"{self.name} - {self.farmer.full_name}"


class FarmPlot(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    farm = models.ForeignKey(
        Farm, on_delete=models.CASCADE, db_column="farm_id", related_name="plots"
    )
    plot_name = models.CharField(max_length=100)
    area = models.FloatField(default=0.0)
    area_unit = models.CharField(max_length=30, default="ropani")
    current_crop_id = models.CharField(max_length=100, null=True, blank=True)
    soil_type = models.CharField(max_length=100, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = "farm_plots"
        managed = False
        verbose_name = "Farm Plot"
        verbose_name_plural = "Farm Plots"

    def __str__(self):
        return f"{self.plot_name} ({self.farm.name})"


class AdminActivityLog(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    admin_email = models.CharField(max_length=150)
    action = models.CharField(max_length=100)
    target_type = models.CharField(max_length=50)
    target_id = models.CharField(max_length=100)
    details = models.TextField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = "admin_activity_logs"
        managed = False
        verbose_name = "Admin Activity Log"
        verbose_name_plural = "Admin Activity Logs"
        ordering = ["-created_at"]

    def __str__(self):
        return f"[{self.created_at}] {self.admin_email} - {self.action} on {self.target_type}"


class CropCategory(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=100)
    name_en = models.CharField(max_length=100)
    icon_name = models.CharField(max_length=50, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = "crop_categories"
        managed = False
        verbose_name = "Crop Category"
        verbose_name_plural = "Crop Categories"
        ordering = ["name_en"]

    def __str__(self):
        return f"{self.name_ne} ({self.name_en})"


class Season(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=100)
    name_en = models.CharField(max_length=100)
    months_ne = models.CharField(max_length=100)

    class Meta:
        db_table = "seasons"
        managed = False
        verbose_name = "Season"
        verbose_name_plural = "Seasons"

    def __str__(self):
        return f"{self.name_ne} ({self.months_ne})"


class Topic(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=100)
    name_en = models.CharField(max_length=100)

    class Meta:
        db_table = "topics"
        managed = False
        verbose_name = "Topic"
        verbose_name_plural = "Topics"

    def __str__(self):
        return self.name_ne


class Region(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=150)
    name_en = models.CharField(max_length=150)

    class Meta:
        db_table = "regions"
        managed = False
        verbose_name = "Region"
        verbose_name_plural = "Regions"

    def __str__(self):
        return self.name_ne


class Crop(models.Model):
    id = models.CharField(max_length=100, primary_key=True)
    category = models.ForeignKey(
        CropCategory,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        db_column="category_id",
        related_name="crops",
    )
    name_ne = models.CharField(max_length=150)
    name_en = models.CharField(max_length=150)
    scientific_name = models.CharField(max_length=150)
    description_ne = models.TextField()
    description_en = models.TextField(null=True, blank=True)
    growth_duration_days_min = models.IntegerField(default=90)
    growth_duration_days_max = models.IntegerField(default=150)
    optimal_temp_celsius = models.CharField(max_length=50, null=True, blank=True)
    soil_type = models.CharField(max_length=150, null=True, blank=True)
    thumbnail_url = models.CharField(max_length=500, null=True, blank=True)
    is_published = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = "crops"
        managed = False
        verbose_name = "Crop"
        verbose_name_plural = "Crops"
        ordering = ["name_ne"]

    def __str__(self):
        return f"{self.name_ne} ({self.name_en})"


class Guide(models.Model):
    id = models.CharField(max_length=100, primary_key=True)
    crop = models.ForeignKey(
        Crop, on_delete=models.CASCADE, db_column="crop_id", related_name="guides"
    )
    title_ne = models.CharField(max_length=255)
    title_en = models.CharField(max_length=255)
    summary_ne = models.TextField()
    topic = models.ForeignKey(
        Topic, on_delete=models.SET_NULL, null=True, blank=True, db_column="topic_id"
    )
    season = models.ForeignKey(
        Season, on_delete=models.SET_NULL, null=True, blank=True, db_column="season_id"
    )
    region = models.ForeignKey(
        Region, on_delete=models.SET_NULL, null=True, blank=True, db_column="region_id"
    )
    is_published = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = "guides"
        managed = False
        verbose_name = "Cultivation Guide"
        verbose_name_plural = "Cultivation Guides"
        ordering = ["title_ne"]

    def __str__(self):
        return f"{self.title_ne} ({self.crop.name_ne})"


class GuideStep(models.Model):
    id = models.CharField(max_length=100, primary_key=True)
    guide = models.ForeignKey(
        Guide, on_delete=models.CASCADE, db_column="guide_id", related_name="steps"
    )
    step_number = models.IntegerField()
    title_ne = models.CharField(max_length=200)
    title_en = models.CharField(max_length=200)
    content_ne = models.TextField()
    content_en = models.TextField(null=True, blank=True)
    tips_ne = models.TextField(null=True, blank=True)
    image_url = models.CharField(max_length=500, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = "guide_steps"
        managed = False
        verbose_name = "Guide Step"
        verbose_name_plural = "Guide Steps"
        ordering = ["step_number"]

    def __str__(self):
        return f"{self.guide.title_ne} - Step {self.step_number}: {self.title_ne}"
