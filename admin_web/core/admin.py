"""Django Admin registration for Bakaiya Krishi Saathi Core models."""

from django.contrib import admin

from .models import (
    AdminProfile,
    ContentAuditLog,
    CropEntry,
    HelplineContactEntry,
    LivestockEntry,
    LivestockSectionContent,
    MediaAsset,
    SourceDocument,
)


@admin.register(AdminProfile)
class AdminProfileAdmin(admin.ModelAdmin):
    list_display = ("full_name", "user", "role", "department", "is_active_editor")
    list_filter = ("role", "is_active_editor")
    search_fields = ("full_name", "user__username")


@admin.register(SourceDocument)
class SourceDocumentAdmin(admin.ModelAdmin):
    list_display = ("title", "publisher", "publication_year")
    search_fields = ("title", "publisher")


@admin.register(MediaAsset)
class MediaAssetAdmin(admin.ModelAdmin):
    list_display = ("title_en", "target_kind", "creator_name", "license_type", "is_verified")
    list_filter = ("target_kind", "is_verified")
    search_fields = ("title_en", "creator_name")


@admin.register(CropEntry)
class CropEntryAdmin(admin.ModelAdmin):
    list_display = ("name_en", "name_ne", "review_status", "is_published", "updated_at")
    list_filter = ("review_status", "is_published")
    search_fields = ("name_en", "name_ne", "scientific_name")


@admin.register(LivestockEntry)
class LivestockEntryAdmin(admin.ModelAdmin):
    list_display = ("name_en", "species_code", "review_status", "is_published", "updated_at")
    list_filter = ("review_status", "is_published")
    search_fields = ("name_en", "name_ne", "species_code")


@admin.register(LivestockSectionContent)
class LivestockSectionContentAdmin(admin.ModelAdmin):
    list_display = ("animal", "section_number", "title_en", "review_status", "is_published")
    list_filter = ("review_status", "is_published", "is_applicable")


@admin.register(HelplineContactEntry)
class HelplineContactEntryAdmin(admin.ModelAdmin):
    list_display = ("department_en", "name_ne", "mobile_number", "is_verified", "is_published")
    list_filter = ("is_verified", "is_published")
    search_fields = ("department_en", "name_ne", "mobile_number")


@admin.register(ContentAuditLog)
class ContentAuditLogAdmin(admin.ModelAdmin):
    list_display = ("created_at", "actor", "action", "target_type", "target_id", "to_status")
    list_filter = ("action", "target_type")
    readonly_fields = ("created_at",)
