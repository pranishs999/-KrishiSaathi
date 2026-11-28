# Database-to-UI Completeness Audit

Bakaiya Krishi Saathi Admin CMS — complete mapping of every Django model field to its display and edit status in the web administration panel.

## How to Read This Document

| Column | Meaning |
|--------|---------|
| **Model** | Django model class name |
| **Database Field** | Model field name (as defined in `core/models.py`) |
| **Displayed** | Yes = field value is visible to an authorized administrator in the web panel; No = not shown |
| **Editable** | Yes = administrator can modify the field through a working form; No = read-only, hidden, or protected |
| **Validation** | Pass = server-side validation exists and was tested; Fail = no validation; N/A = not applicable (read-only/system field) |
| **Test Status** | Pass = verified via live page load / form submission; Fail = not yet verified |

**Protected fields** (primary keys, auto timestamps, password hashes, computed fields) are intentionally not editable and are marked accordingly.

---

## 1. Managed CMS Models

Tables exist in the local development database (`cms_*` prefix). These are the core editorial models.

### 1.1 AdminProfile (`cms_adminprofile`)

Links Django auth users to CMS RBAC roles. Managed through Django Admin only (no custom web panel page).

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| user (OneToOne→User) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | Displayed via Django Admin at `/django-admin/` |
| role (Char, choices) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | CMS role: super_admin / technical_reviewer / content_editor |
| full_name (Char 150) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | — |
| department (Char 150) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | — |
| is_active_editor (Bool) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | — |
| created_at (DateTime) | Yes (Django Admin) | No (auto) | N/A | N/A | Auto-generated, read-only |

**Assessment:** Complete via Django Admin. No custom panel page needed.

---

### 1.2 SourceDocument (`cms_sourcedocument`)

Authoritative citation record. Managed through Django Admin only.

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 64) | Yes (Django Admin) | No (auto UUID) | N/A | N/A | Auto-generated primary key |
| title (Char 255) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | — |
| publisher (Char 255) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | — |
| publication_year (PositiveInteger) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | Optional |
| url (URLField) | Yes (Django Admin) | Yes (Django Admin) | URL format | N/A | Optional |
| notes (Text) | Yes (Django Admin) | Yes (Django Admin) | N/A | N/A | Optional |
| created_at (DateTime) | Yes (Django Admin) | No (auto) | N/A | N/A | Auto-generated |

**Assessment:** Complete via Django Admin. Not exposed in custom CMS pages (referenced indirectly via `source_document` FK on CropEntry/LivestockEntry).

---

### 1.3 MediaAsset (`cms_mediaasset`)

Validated media asset with image derivatives. Displayed in Media Library (`/cms/media/`).

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 64) | No | No (auto UUID) | N/A | N/A | Internal identifier |
| title_en (Char 200) | Yes | Yes | Required | Pass | Shown in listing table and upload form |
| caption_ne (Char 255) | Yes | Yes | Optional | Pass | Shown in upload form |
| target_kind (Char choices) | Yes | Yes | Choices: crop/livestock/general | Pass | Shown in upload form dropdown |
| original_path (Char 500) | No | No (internal) | N/A | N/A | Storage path, set by media pipeline |
| medium_path (Char 500) | Yes (as derivative path) | No | N/A | N/A | Shown in listing as derivative path |
| thumb_path (Char 500) | Yes (as derivative path) | No | N/A | N/A | Shown in listing as derivative path |
| remote_url (URLField) | No | No | N/A | N/A | Not exposed in custom UI |
| mime_type (Char 64) | Yes (in dimensions text) | No (detected) | Magic-header detection | Pass | Detected at upload, not editable |
| width_px (PositiveInteger) | Yes (in dimensions text) | No (detected) | N/A | Pass | Detected at upload |
| height_px (PositiveInteger) | Yes (in dimensions text) | No (detected) | N/A | Pass | Detected at upload |
| file_size_bytes (PositiveInteger) | No | No | 5 MB limit enforced | Pass | Enforced at upload, not displayed |
| creator_name (Char 200) | Yes | Yes | Required | Pass | Shown in upload form and listing |
| license_type (Char 120) | Yes | Yes | Required | Pass | Shown in upload form and listing |
| source_url (URLField) | Yes | Yes | URL format (optional) | Pass | Shown in upload form |
| is_verified (Bool) | No (computed at upload) | No | N/A | N/A | Set by upload logic based on creator+license |
| uploaded_by (FK→User) | No | No (set by view) | N/A | N/A | Set automatically from request user |
| created_at (DateTime) | No | No (auto) | N/A | N/A | Not displayed |

**Missing/Improvements needed:**
- `original_path`, `remote_url`, `file_size_bytes`, `is_verified`, `uploaded_by`, `created_at` are not displayed in the Media Library listing.
- No detail page for individual media assets.
- No delete functionality for media assets.

---

### 1.4 CropEntry (`cms_cropentry`)

Editorial crop entry with workflow. Displayed in Crops List (`/cms/crops/`) and Crop Form (`/cms/crops/create/`, `/cms/crops/<id>/edit/`).

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 64) | No | No (auto UUID) | N/A | N/A | Internal identifier |
| category_id (Char 64) | Yes | Yes | Required, default "cat-cereal" | Pass | Shown in list and form |
| name_ne (Char 120) | Yes | Yes | Required | Pass | Shown in list and form |
| name_en (Char 120) | Yes | Yes | Required | Pass | Shown in list and form |
| scientific_name (Char 150) | Yes | Yes | Optional | Pass | Shown in form |
| description_ne (Text) | Yes | Yes | Required | Pass | Shown in form |
| varieties (Text) | Yes | Yes | Optional | Pass | Shown in form |
| planting_season_ne (Char 200) | Yes | Yes | Optional | Pass | Shown in form |
| altitude_summary_ne (Char 200) | Yes | Yes | Optional | Pass | Shown in form |
| soil_types_ne (Char 255) | Yes | Yes | Optional | Pass | Shown in form |
| source_reference (Char 255) | Yes | Yes | Required for review/publish | Pass | Shown in list and form |
| source_document (FK→SourceDocument) | No | No | N/A | N/A | Not exposed in custom UI |
| thumbnail_url (URLField) | Yes | Yes | URL format (optional) | Pass | Shown in form |
| review_status (Char choices) | Yes | Yes (via workflow) | Workflow state machine | Pass | Shown in list; changed via transition |
| is_published (Bool) | Yes | No (set by workflow) | N/A | Pass | Shown in list badge |
| published_at (DateTime null) | No | No (set by workflow) | N/A | N/A | Not displayed |
| created_by (FK→User) | No | No (set by view) | N/A | N/A | Not displayed |
| reviewed_by (FK→User) | No | No (set by workflow) | N/A | N/A | Not displayed |
| updated_at (DateTime) | No | No (auto) | N/A | N/A | Not displayed |

**Missing/Improvements needed:**
- `source_document`, `published_at`, `created_by`, `reviewed_by`, `updated_at` not displayed.
- No dedicated detail page showing all CropEntry fields.
- Edit form preserves optional metadata on partial POST (good).

---

### 1.5 LivestockEntry (`cms_livestockentry`)

Editorial livestock species entry with 25-section verification. Displayed in Livestock List (`/cms/livestock/`) and Livestock Detail (`/cms/livestock/<id>/`).

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 64) | No | No (auto UUID) | N/A | N/A | Internal identifier |
| category_id (Char 64) | No | No | N/A | N/A | Not exposed in custom UI |
| species_code (Char 40 unique) | Yes | No | Unique | Pass | Shown in list |
| name_ne (Char 120) | Yes | No | Required | Pass | Shown in list and detail header |
| name_en (Char 120) | Yes | No | Required | Pass | Shown in list and detail header |
| scientific_name (Char 150) | No | No | Optional | N/A | NOT displayed anywhere |
| summary_ne (Text) | No | No | Required | N/A | NOT displayed anywhere |
| purpose_tags_ne (Char 255) | No | No | Optional | N/A | NOT displayed anywhere |
| bakaiya_relevance_ne (Text) | No | No | Optional | N/A | NOT displayed anywhere |
| climate_suitability_ne (Text) | No | No | Optional | N/A | NOT displayed anywhere |
| altitude_range_ne (Char 150) | No | No | Optional | N/A | NOT displayed anywhere |
| recommended_breeds_summary_ne (Text) | No | No | Optional | N/A | NOT displayed anywhere |
| safety_notice_ne (Text) | No | No | Has default Nepali warning | N/A | NOT displayed anywhere |
| source_reference (Char 255) | Yes | No | Required | Pass | Shown in list |
| source_document (FK→SourceDocument) | No | No | N/A | N/A | NOT displayed |
| thumbnail_url (URLField) | No | No | URL format | N/A | NOT displayed |
| review_status (Char choices) | Yes | No (via workflow) | Workflow state machine | Pass | Shown in list and detail |
| is_published (Bool) | Yes | No (set by workflow) | N/A | Pass | Shown in list badge |
| published_at (DateTime null) | No | No (set by workflow) | N/A | N/A | Not displayed |
| created_by (FK→User) | No | No (set by view) | N/A | N/A | Not displayed |
| reviewed_by (FK→User) | No | No (set by workflow) | N/A | N/A | Not displayed |
| updated_at (DateTime) | No | No (auto) | N/A | N/A | Not displayed |

**CRITICAL GAPS:** The Livestock Detail page (`/cms/livestock/<id>/`) only displays the 25 section forms. The main LivestockEntry fields (scientific_name, summary_ne, purpose_tags_ne, climate_suitability_ne, altitude_range_ne, safety_notice_ne, etc.) are NOT displayed anywhere in the custom UI. No dedicated detail page shows all livestock fields. No edit form for the main LivestockEntry record (only sections can be edited).

---

### 1.6 LivestockSectionContent (`cms_livestocksectioncontent`)

One section per livestock species (25 sections). Displayed and edited in Livestock Detail (`/cms/livestock/<id>/`).

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| animal (FK→LivestockEntry) | Yes (implicit) | No | Required | Pass | Each section form is scoped to an animal |
| section_number (PositiveSmallInteger) | Yes | No (1-25 fixed) | 1-25 range | Pass | Section number displayed as heading |
| section_key (Char 64) | No | No | N/A | N/A | Not displayed |
| title_en (Char 200) | Yes | No | Required | Pass | Section title shown as heading |
| body_ne (Text) | Yes | Yes | Optional | Pass | Nepali body textarea |
| body_en (Text) | Yes | Yes | Optional | Pass | English body textarea |
| is_applicable (Bool) | Yes | Yes | Default True | Pass | Checkbox |
| source_reference (Char 255) | Yes | Yes | Optional | Pass | Text input |
| review_status (Char choices) | Yes | No (set on save) | Workflow choices | Pass | Shown as badge |
| is_published (Bool) | Yes | No (set on publish) | N/A | Pass | Shown as badge |
| updated_at (DateTime) | No | No (auto) | N/A | N/A | Not displayed |

**Assessment:** Section editing is complete. `section_key` and `updated_at` not displayed (minor).

---

### 1.7 HelplineContactEntry (`cms_helplinecontactentry`)

Verified helpline contacts. Displayed in Helpline List (`/cms/helpline/`).

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 64) | No | No (auto UUID) | N/A | N/A | Internal identifier |
| name_ne (Char 150) | Yes | Yes | Required | Pass | Shown in form and table |
| designation_ne (Char 200) | No | Yes | Required | Pass | Shown in form, NOT in table |
| department_en (Char 200) | Yes | Yes | Required | Pass | Shown in form and table |
| ward_ne (Char 200) | No | Yes | Required | Pass | Shown in form, NOT in table |
| mobile_number (Char 32) | Yes | Yes | Required | Pass | Shown in form and table |
| email (EmailField) | No | Yes | Email format (optional) | Pass | Shown in form, NOT in table |
| source_reference (Char 255) | Yes | Yes | Required | Pass | Shown in form and table |
| is_verified (Bool) | No | No (set on create) | N/A | N/A | Set to True on creation |
| is_published (Bool) | No | No (set on create) | N/A | N/A | Set to True on creation |
| updated_at (DateTime) | No | No (auto) | N/A | N/A | Not displayed |

**Missing/Improvements needed:**
- `designation_ne`, `ward_ne`, `email` not shown in listing table.
- No detail page for individual helpline contacts.
- No delete functionality for helpline contacts.
- `is_verified`, `is_published`, `updated_at` not displayed.

---

### 1.8 ContentAuditLog (`cms_contentauditlog`)

Immutable audit trail. Displayed in CMS Dashboard (`/cms/`) recent activity.

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| actor (FK→User) | Yes | No | N/A | Pass | Shown in recent_logs |
| action (Char 80) | Yes | No | N/A | Pass | Shown in recent_logs |
| target_type (Char 64) | Yes | No | N/A | Pass | Shown in recent_logs |
| target_id (Char 64) | Yes | No | N/A | Pass | Shown in recent_logs |
| from_status (Char 32) | Yes | No | N/A | Pass | Shown in recent_logs |
| to_status (Char 32) | Yes | No | N/A | Pass | Shown in recent_logs |
| notes (Text) | No | No | N/A | N/A | Not displayed |
| ip_address (GenericIPAddressField) | No | No | N/A | N/A | Not displayed |
| created_at (DateTime) | Yes | No (auto) | N/A | Pass | Shown in recent_logs |

**Missing/Improvements needed:**
- Only last 12 entries shown on dashboard. No dedicated audit log listing page.
- `notes`, `ip_address` not displayed.

---

## 2. Unmanaged Supabase Models

Tables owned by Supabase (`managed = False`). May not exist in local SQLite. Views degrade gracefully with `db_error` message when tables unavailable.

### 2.1 FarmerProfile (`farmer_profiles`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (UUID PK) | No | No | N/A | N/A | Internal identifier |
| phone (Char 20 unique) | Yes | No | Required, unique | Pass | Shown as "+977 {phone}" |
| full_name (Char 150) | Yes | No | Required | Pass | Shown in table |
| mpin_hash (Char 255 null) | No | No (protected) | N/A | N/A | Security field — NOT displayed (protected) |
| municipality (Char 100) | Yes | No | Required | Pass | Shown in table |
| ward_number (Integer) | Yes | No | Required | Pass | Shown as "Ward N" |
| tole_village (Char 200 null) | Yes | No | Optional | Pass | Shown or "—" |
| consent_status (Bool) | Yes | No | Default False | Pass | Shown as Yes/Legacy badge |
| registered_at (BigInteger null) | No | No | N/A | N/A | Not displayed |
| latitude (Float null) | No | No | N/A | N/A | Not displayed |
| longitude (Float null) | No | No | N/A | N/A | Not displayed |
| land_unit_preference (Char 20) | No | No | Default "ropani" | N/A | Not displayed |
| created_at (DateTime) | Yes | No (auto) | N/A | Pass | Shown as "Y-m-d H:i" |
| updated_at (DateTime) | No | No (auto) | N/A | N/A | Not displayed |

**Assessment:** Read-only farmer directory. `mpin_hash` correctly protected (not displayed). Missing fields: `registered_at`, `latitude`, `longitude`, `land_unit_preference`, `updated_at`. No detail page for individual farmers. No edit/delete (read-only directory).

---

### 2.2 Farm (`farms`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (UUID PK) | No | No | N/A | N/A | — |
| farmer (FK→FarmerProfile) | Yes (count only) | No | Required | Pass | Shown as farm count in legacy farmers table |
| name (Char 150) | No | No | N/A | N/A | Not displayed |
| location (Char 255) | No | No | N/A | N/A | Not displayed |
| total_area (Float) | No | No | N/A | N/A | Not displayed |
| area_unit (Char 30) | No | No | N/A | N/A | Not displayed |
| created_at/updated_at | No | No | N/A | N/A | Not displayed |

**CRITICAL GAP:** No dedicated Farm management page. Only farm count is shown in legacy_farmers table.

---

### 2.3 FarmPlot (`farm_plots`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (UUID PK) | No | No | N/A | N/A | — |
| farm (FK→Farm) | No | No | N/A | N/A | — |
| plot_name (Char 100) | No | No | N/A | N/A | — |
| area (Float) | No | No | N/A | N/A | — |
| area_unit (Char 30) | No | No | N/A | N/A | — |
| current_crop_id (Char 100 null) | No | No | N/A | N/A | — |
| soil_type (Char 100 null) | No | No | N/A | N/A | — |
| created_at/updated_at | No | No | N/A | N/A | — |

**CRITICAL GAP:** No UI at all for FarmPlot.

---

### 2.4 AdminActivityLog (`admin_activity_logs`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (UUID PK) | No | No | N/A | N/A | — |
| admin_email (Char 150) | Yes | No | Required | Pass | Shown in legacy_dashboard recent_logs |
| action (Char 100) | Yes | No | Required | Pass | Shown in legacy_dashboard recent_logs |
| target_type (Char 50) | Yes | No | Required | Pass | Shown in legacy_dashboard recent_logs |
| target_id (Char 100) | Yes | No | Required | Pass | Shown in legacy_dashboard recent_logs |
| details (Text null) | No | No | N/A | N/A | Not displayed |
| created_at (DateTime) | Yes | No (auto) | N/A | Pass | Shown in legacy_dashboard recent_logs |

**Missing/Improvements needed:** Only last 8 entries shown on legacy dashboard. No dedicated listing page. `details` not displayed.

---

### 2.5 CropCategory (`crop_categories`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 50) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option in legacy crops list |
| name_ne (Char 100) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |
| name_en (Char 100) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |
| icon_name (Char 50 null) | No | No | N/A | N/A | Not displayed |
| created_at (DateTime) | No | No | N/A | N/A | Not displayed |

**Missing/Improvements needed:** No dedicated category management page. `icon_name`, `created_at` not displayed.

---

### 2.6 Season (`seasons`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 50) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option in legacy guide form |
| name_ne (Char 100) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |
| name_en (Char 100) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |
| months_ne (Char 100) | No | No | N/A | N/A | Not displayed |

**Missing/Improvements needed:** No dedicated season management page. `months_ne` not displayed.

---

### 2.7 Topic (`topics`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 50) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option in legacy guide form |
| name_ne (Char 100) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |
| name_en (Char 100) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |

**Missing/Improvements needed:** No dedicated topic management page.

---

### 2.8 Region (`regions`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 50) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option in legacy guide form |
| name_ne (Char 150) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |
| name_en (Char 150) | Yes (filter dropdown) | No | Required | Pass | Shown as filter option |

**Missing/Improvements needed:** No dedicated region management page.

---

### 2.9 Crop (`crops`) — unmanaged Supabase

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 100) | No | No | Required | N/A | Not displayed |
| category (FK→CropCategory) | Yes | Yes | Required | Pass | Shown as filter and form select |
| name_ne (Char 150) | Yes | Yes | Required | Pass | Shown in table and form |
| name_en (Char 150) | Yes | Yes | Required | Pass | Shown in table and form |
| scientific_name (Char 150) | Yes | Yes | Required | Pass | Shown in table and form |
| description_ne (Text) | No | Yes | Required | Pass | Shown in form, NOT in table |
| description_en (Text null) | No | Yes | Optional | Pass | Shown in form, NOT in table |
| growth_duration_days_min (Int) | Yes | Yes | Default 90 | Pass | Shown in table and form |
| growth_duration_days_max (Int) | Yes | Yes | Default 150 | Pass | Shown in table and form |
| optimal_temp_celsius (Char 50 null) | No | Yes | Optional | Pass | Shown in form, NOT in table |
| soil_type (Char 150 null) | No | Yes | Optional | Pass | Shown in form, NOT in table |
| thumbnail_url (Char 500 null) | No | Yes | Optional | Pass | Shown in form, NOT in table |
| is_published (Bool) | Yes | Yes | Default True | Pass | Shown as toggle button in table |
| created_at/updated_at | No | No (auto) | N/A | N/A | Not displayed |

**Missing/Improvements needed:**
- `description_ne`, `description_en`, `optimal_temp_celsius`, `soil_type`, `thumbnail_url` not shown in listing table.
- No dedicated detail page for individual crops.
- Delete form uses inline JS confirm (good) but no server-side confirmation page.

---

### 2.10 Guide (`guides`) — unmanaged Supabase

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 100) | No | No | Required | N/A | Not displayed |
| crop (FK→Crop) | Yes | Yes | Required | Pass | Shown as badge and form select |
| title_ne (Char 255) | Yes | Yes | Required | Pass | Shown in card and form |
| title_en (Char 255) | No | Yes | Required | Pass | Shown in form, NOT in card |
| summary_ne (Text) | Yes | Yes | Required | Pass | Shown in card and form |
| topic (FK→Topic) | No | Yes | Optional | Pass | Shown in form, NOT in card |
| season (FK→Season) | Yes | Yes | Optional | Pass | Shown in card and form |
| region (FK→Region) | No | Yes | Optional | Pass | Shown in form, NOT in card |
| is_published (Bool) | Yes | Yes | Default True | Pass | Shown as toggle button |
| created_at/updated_at | No | No (auto) | N/A | N/A | Not displayed |

**Missing/Improvements needed:**
- `title_en`, `topic`, `region` not shown in guide cards.
- No dedicated detail page showing all guide fields (the detail page shows steps, not guide metadata).
- No delete functionality for guides.

---

### 2.11 GuideStep (`guide_steps`)

| Database Field | Displayed | Editable | Validation | Test Status | Notes |
|----------------|-----------|----------|------------|-------------|-------|
| id (Char PK 100) | No | No | Required | N/A | Not displayed |
| guide (FK→Guide) | Yes (implicit) | No | Required | Pass | Each step scoped to a guide |
| step_number (Integer) | Yes | Yes | 1-50 range | Pass | Shown as step badge |
| title_ne (Char 200) | Yes | Yes | Required | Pass | Shown in step card |
| title_en (Char 200) | Yes | Yes | Required | Pass | Shown as subtitle |
| content_ne (Text) | Yes | Yes | Required | Pass | Shown in step card |
| content_en (Text null) | No | Yes | Optional | Pass | NOT displayed |
| tips_ne (Text null) | Yes | Yes | Optional | Pass | Shown in farmer tips box |
| image_url (Char 500 null) | No | Yes | Optional | Pass | NOT displayed |
| created_at (DateTime) | No | No (auto) | N/A | N/A | Not displayed |

**Missing/Improvements needed:**
- `content_en`, `image_url`, `created_at` not displayed.
- No edit page for existing steps (only create/update via step form).
- No delete functionality for steps.

---

## 3. Summary of Critical Gaps

| Gap | Models Affected | Severity |
|-----|----------------|----------|
| No detail page for CropEntry | CropEntry | High |
| No detail page for LivestockEntry (main fields hidden) | LivestockEntry | High |
| No detail page for MediaAsset | MediaAsset | Medium |
| No detail page for HelplineContactEntry | HelplineContactEntry | Medium |
| No detail page for Crop (Supabase) | Crop | High |
| No detail page for Guide (metadata hidden) | Guide | High |
| No delete for MediaAsset | MediaAsset | Medium |
| No delete for HelplineContactEntry | HelplineContactEntry | Medium |
| No delete for Guide | Guide | Medium |
| No delete for GuideStep | GuideStep | Medium |
| No dedicated page for SourceDocument | SourceDocument | Low |
| No dedicated page for AdminProfile | AdminProfile | Low (Django Admin covers) |
| No dedicated page for ContentAuditLog | ContentAuditLog | Low |
| No dedicated page for Farm | Farm | High |
| No dedicated page for FarmPlot | FarmPlot | High |
| No dedicated page for CropCategory | CropCategory | Low |
| No dedicated page for Season/Topic/Region | Season/Topic/Region | Low |
| Listing tables missing several fields | Multiple | Medium |

---

## 4. Fields Correctly Protected (Read-Only)

| Model | Field | Reason |
|-------|-------|--------|
| AdminProfile | created_at | Auto-generated timestamp |
| SourceDocument | id | Auto-generated UUID |
| MediaAsset | id, original_path, mime_type, width_px, height_px, file_size_bytes, is_verified, uploaded_by, created_at | Set by system/upload pipeline |
| CropEntry | id, published_at, created_by, reviewed_by, updated_at | Set by system/workflow |
| LivestockEntry | id, published_at, created_by, reviewed_by, updated_at | Set by system/workflow |
| LivestockSectionContent | updated_at | Auto-generated timestamp |
| HelplineContactEntry | id, is_verified, is_published, updated_at | Set by system |
| ContentAuditLog | All fields | Immutable audit trail |
| FarmerProfile | id, mpin_hash | Security field protected |
| Farm | id | Auto-generated UUID |
| FarmPlot | id | Auto-generated UUID |
| AdminActivityLog | All fields | Immutable audit trail |
| CropCategory | id | Auto-generated |
| Season | id | Auto-generated |
| Topic | id | Auto-generated |
| Region | id | Auto-generated |
| Crop | id, created_at, updated_at | Auto-generated |
| Guide | id, created_at, updated_at | Auto-generated |
| GuideStep | id, created_at | Auto-generated |
