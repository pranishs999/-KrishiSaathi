# Admin Security Architecture — Bakaiya Krishi Saathi Admin CMS

## 1. Credential & Environment Hardening
- Zero hardcoded database passwords or API secrets in `admin_web/config/settings.py`.
- Production database configuration reads `DATABASE_URL` and `SECRET_KEY` exclusively from environment variables or `.env` (gitignored).
- Password policy enforces `MinimumLengthValidator(min_length=10)` plus similarity, common-password, and numeric validators.

## 2. Session, Cookie & HTTP Header Controls
- `CmsSecurityHeadersMiddleware` sets `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: strict-origin-when-cross-origin`, and `Cache-Control: no-store` on all `/cms/` routes.
- `SESSION_COOKIE_HTTPONLY = True`, `CSRF_COOKIE_HTTPONLY = True`, `SESSION_EXPIRE_AT_BROWSER_CLOSE = True`, and `SESSION_COOKIE_SECURE = True` when `DEBUG=False`.
- Login endpoint enforces IP-based rate limiting (`8` failed attempts per `300` seconds -> HTTP `429`).

## 3. RBAC & Anti-Self-Approval Workflow Controls
- Users without an active `AdminProfile` (or `is_superuser`) receive `HTTP 403 Forbidden`.
- **Content Editor**: Can create drafts and submit entries with a verified source citation (`in_review`). Cannot approve or publish.
- **Technical Reviewer**: Can approve and publish submissions **except** records they authored (`created_by_id == actor.id` raises `PermissionDenied`).
- **Super Admin**: Full editorial, helpline management, archiving, and oversight permissions.
- Every login, logout, upload, and status transition writes an immutable record to `ContentAuditLog`.

## 4. Media Upload Security
- `cms/media_pipeline.py` inspects binary magic headers (`JPEG`, `PNG`, `WEBP`) rather than trusting HTTP `Content-Type` or filenames.
- SVG, HTML/script, polyglot payloads, and files exceeding `5 MB` are rejected with `ValidationError`.
- Valid images are re-encoded with Pillow (EXIF metadata stripped) into clean WebP derivatives.
