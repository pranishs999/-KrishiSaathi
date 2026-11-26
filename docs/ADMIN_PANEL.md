# English Web Admin Panel (`admin_web/`)

**Product Name:** Bakaiya Krishi Saathi Admin  
**Language:** English Only (zero Nepali labels in the administrative UI)  
**Target Devices:** Responsive web application optimized for desktop and laptop workflows, with tablet support  
**Framework & Runtime:** Django 6.1 + Pillow 12.3 (Server-side RBAC, CSRF, Security Headers, REST Public API, optional Supabase Postgres/PostgREST bridge)

---

## 1. Administrator Roles & Access Control

| Role | Capabilities | Enforced Server Restrictions |
|---|---|---|
| **Super Admin** | Manage admin users/roles/permissions, approve & publish content, manage categories, archive/delete content, inspect audit logs, manage settings | Cannot alter immutable audit history; sensitive actions logged |
| **Content Editor** | Create/edit draft crops, varieties, crop environmental sections, animals, breeds, care sections, upload media, manage playlists, submit for review | Blocked server-side & via DB triggers from approving (`approved`), publishing (`published`), or deleting audit logs |
| **Technical Reviewer** | Inspect citations & sources, verify agronomic/livestock/veterinary claims within assigned review scope, approve or reject to Draft with comments, set `next_review_date` | Cannot manage admin accounts or publish unapproved content |

## 2. Core Management Modules
1. **Crop Management:** Add, edit, duplicate, archive, soft-delete crops; manage `crop_varieties`, environmental bounds (altitude, soil, climate, rainfall), 22 content sections, cultivation steps, pests & diseases; preview & publish after approval.
2. **Livestock Management:** Add, edit, archive, soft-delete species (`animals`), dynamic `animal_categories`, `animal_breeds`, 25 independent care sections (housing, feeding, green/dry fodder, concentrate, water/minerals, breeding, pregnancy, newborn care, health/hygiene, biosecurity, vaccinations, parasites, diseases, clinical signs, veterinary referral, production, manure/waste); attach verified images, videos & citations.
3. **Categories Management:** Crop & animal categories creation, ordering, activation/deactivation, icon/image link, safe deletion guard when in use.
4. **Media Asset Library:** Upload images with magic-byte + MIME + Pillow verification, auto-generate `full` / `medium` / `thumb` WebP/JPEG derivatives, track creator, source, license, permission status, alt text, and Nepali captions.
5. **Video & Playlist Manager:** Validate YouTube watch/channel URLs and explicitly permitted MP4 download links; maintain topic playlists and item ordering.
6. **Source Documents & Review Queue:** Register official NARC / MoALD / DLS / FANSEP / Bakaiya documents, link page/section citations to specific claims, manage review queue & scheduled re-review dates.
7. **Helpline Directory:** Manage verified agriculture and veterinary contacts with department, ward/area, phone number verification, and active toggle.
8. **Security & Audit Center:** Admin login, rate limiting, session expiration, password reset workflow, optional TOTP 2FA enrollment/challenge, and immutable `audit_logs` + `content_publication_history`.
