# Bug Tracker — Bakaiya Krishi Saathi Admin CMS

| Bug ID | Page/Module | Description | Severity | Status | Fix Applied |
|--------|-------------|-------------|----------|--------|-------------|
| BUG-001 | Legacy Dashboard (`/dashboard/`) | `legacy_dashboard` view crashed with `ProgrammingError: relation "crops" does not exist` because it queried unmanaged Supabase tables without error handling. Local dev DB (SQLite) does not have Supabase tables. | Critical | **Fixed** | Wrapped all Supabase queries in try/except, return empty lists with `db_error` message instead of 500. |
| BUG-002 | Legacy Crops List (`/crops/`) | `legacy_crop_list` crashed: `relation "crop_categories" does not exist`. Same root cause as BUG-001. | Critical | **Fixed** | Wrapped all Supabase queries in try/except, return empty lists with `db_error` message. |
| BUG-003 | Legacy Guides List (`/guides/`) | `legacy_guide_list` crashed: `relation "guides" does not exist`. Same root cause as BUG-001. | Critical | **Fixed** | Wrapped all Supabase queries in try/except, return empty lists with `db_error` message. |
| BUG-004 | Legacy Farmers (`/accounts/farmers/`) | `legacy_farmer_list` crashed: `relation "farmer_profiles" does not exist`. Same root cause as BUG-001. | Critical | **Fixed** | Wrapped all Supabase queries in try/except, return empty lists with `db_error` message (matching pattern from `farmers_list`). |
| BUG-005 | CropEntry Detail Missing | No detail page existed for CropEntry — administrators could not view all fields (source_document, published_at, created_by, reviewed_by, updated_at, etc.) | High | **Fixed** | Added `crop_detail` view (`/cms/crops/<id>/detail/`) with complete field display, source document link, delete button. |
| BUG-006 | LivestockEntry Main Fields Hidden | The main LivestockEntry fields (scientific_name, summary_ne, climate_suitability_ne, altitude_range_ne, safety_notice_ne, etc.) were completely hidden — only 25 sections were visible. | High | **Fixed** | Added `livestock_entry_detail` view (`/cms/livestock/<id>/entry-detail/`) showing all main fields plus section summary. |
| BUG-007 | MediaAsset Detail Missing | No detail page for MediaAsset — couldn't view all fields (derivative paths, creator, license, dimensions, verification status, upload metadata). | Medium | **Fixed** | Added `media_asset_detail` view (`/cms/media/<id>/detail/`) with complete field display including image preview and derivative paths. |
| BUG-008 | HelplineContactEntry Detail Missing | No detail page — `designation_ne`, `ward_ne`, `email`, `is_verified`, `is_published`, `updated_at` not displayed. | Medium | **Fixed** | Added `helpline_contact_detail` view (`/cms/helpline/<id>/detail/`) with complete field display. |
| BUG-009 | No Delete for CropEntry | Could not delete CropEntry records from CMS. | Medium | **Fixed** | Added `crop_delete` view (`/cms/crops/<id>/delete/`) with confirmation page and audit logging. |
| BUG-010 | No Delete for MediaAsset | Could not delete MediaAsset records. | Medium | **Fixed** | Added `media_asset_delete` view (`/cms/media/<id>/delete/`) with confirmation page and audit logging. |
| BUG-011 | No Delete for HelplineContactEntry | Could not delete HelplineContactEntry records. | Medium | **Fixed** | Added `helpline_contact_delete` view (`/cms/helpline/<id>/delete/`) with confirmation page and audit logging. |
| BUG-012 | Detail Links Missing in Listings | List pages (crops, livestock, media, helpline) had no links to detail pages. | Medium | **Fixed** | Added "Detail" links to all four listing tables. |
| BUG-013 | Audit Log Field Name | Delete views used `details` field which doesn't exist on `ContentAuditLog` model (field is `notes`). | Medium | **Fixed** | Changed all `details=` to `notes=` in ContentAuditLog.create() calls. |

---

## Verification Summary

All 19 automated tests pass:
- ✅ `CmsWorkflowAndSecurityTests` (6 tests)
- ✅ `CoreAllPagesEndToEndTests` (7 tests)
- ✅ `DatabaseEnvironmentConfigTests` (3 tests)
- ✅ `DatabaseDiagnosticsTests` (3 tests)

Manual QA verification completed for all phases:
- ✅ Authentication & Authorization
- ✅ All CMS pages load (200)
- ✅ All detail pages load (200)
- ✅ All delete confirm pages load (200)
- ✅ Workflow transitions work correctly
- ✅ Public APIs return correct data
- ✅ Legacy portal pages load without 500 errors
- ✅ Static assets serve correctly
- ✅ Django admin accessible
- ✅ Delete functionality works with audit logging
- ✅ Authorization enforced (outsiders get 403, role-based permissions work)

---

## Files Modified

| File | Changes |
|------|---------|
| `core/views.py` | Fixed 4 legacy views with graceful degradation; added 7 new views (3 detail, 3 delete, 1 livestock entry detail); fixed ContentAuditLog field name |
| `core/urls.py` | Added 6 new URL routes for detail and delete endpoints |
| `core/templates/core/pages/crop_detail.html` | New template - complete CropEntry detail view |
| `core/templates/core/pages/crop_delete_confirm.html` | New template - crop delete confirmation |
| `core/templates/core/pages/livestock_entry_detail.html` | New template - complete LivestockEntry detail view |
| `core/templates/core/pages/media_asset_detail.html` | New template - complete MediaAsset detail view |
| `core/templates/core/pages/media_asset_delete_confirm.html` | New template - media asset delete confirmation |
| `core/templates/core/pages/helpline_contact_detail.html` | New template - complete HelplineContactEntry detail view |
| `core/templates/core/pages/helpline_contact_delete_confirm.html` | New template - helpline contact delete confirmation |
| `core/templates/core/pages/crops_list.html` | Added "Detail" link |
| `core/templates/core/pages/livestock_list.html` | Added "Detail" link |
| `core/templates/core/pages/media_list.html` | Added "Detail" link and Actions column |
| `core/templates/core/pages/helpline_list.html` | Added "Detail" link and Actions column |

---

## Files Created (Documentation)

| File | Purpose |
|------|---------|
| `DATABASE_UI_COMPLETENESS.md` | Complete database-to-UI mapping for all 18 models and 100+ fields |
| `BUG_TRACKER.md` | This file - bug tracking and resolution |
| `QA_REPORT.md` | Final QA report (to be created) |