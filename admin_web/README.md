# Bakaiya Krishi Saathi Admin CMS (`admin_web`)

## 1. Project Overview

The **Bakaiya Krishi Saathi Admin CMS** is the web administration backend and public verified-content API for the Bakaiya Krishi Saathi agricultural platform. It enables editors, technical reviewers, and administrators to create, review, approve, and publish crop advisories, 25-section livestock guides, verified helpline contacts, and attributed media assets, while exposing read-only published JSON endpoints for the Android farmer application.

## 2. Technology Stack

- **Language:** Python 3.12+ (developed on Python 3.14)
- **Framework:** Django 6.1.3 (`django>=5.1,<7.0`)
- **Database:** Supabase PostgreSQL (`psycopg2-binary`) with SQLite used strictly for isolated automated tests
- **Media Processing:** Pillow (`pillow`) for magic-header validation, EXIF stripping, and WebP derivative generation
- **Static Files / WSGI:** WhiteNoise (`whitenoise`) + Gunicorn (`gunicorn`) / Vercel WSGI (`config/wsgi.py`)
- **Frontend Styling:** Tailwind CSS + centralized static CSS/JS (`core/static/core/`)

## 3. Directory Structure

```text
admin_web/
│
├── manage.py                    # Django CLI entrypoint
│
├── config/                      # Django project configuration
│   ├── __init__.py
│   ├── settings.py              # Environment-driven settings (DB, security, static, templates)
│   ├── urls.py                  # Root project URL routing (django-admin + core)
│   ├── asgi.py                  # ASGI entrypoint
│   └── wsgi.py                  # WSGI entrypoint (exposes application and app)
│
├── core/                        # Single consolidated Django application
│   ├── __init__.py
│   ├── admin.py                 # Django Admin registrations
│   ├── apps.py                  # CoreConfig app metadata
│   ├── constants.py             # Roles, workflow state machine, crop/livestock section specs
│   ├── db_diagnostics.py        # Read-only 6-step PostgreSQL/Supabase connectivity diagnostics
│   ├── forms.py                 # Form definitions and server-side validation
│   ├── models.py                # Managed CMS models (cms_*) + unmanaged Supabase tables
│   ├── services.py              # RBAC, security middleware, workflow engine, Supabase mirror, media pipeline
│   ├── tests.py                 # Unit, config, diagnostic, workflow, and end-to-end page tests
│   ├── urls.py                  # All application routes (namespaced under core:)
│   ├── views.py                 # CMS pages, public read-only APIs, and legacy portal views
│   ├── management/
│   │   └── commands/
│   │       └── check_db.py      # `python manage.py check_db` connectivity diagnostic command
│   ├── migrations/
│   │   ├── __init__.py
│   │   └── 0001_initial.py      # Consolidated initial migration (replaces legacy apps cleanly)
│   ├── templates/
│   │   └── core/
│   │       ├── base.html        # Main CMS layout
│   │       ├── legacy_base.html # Legacy portal layout
│   │       ├── dashboard.html   # Editorial overview dashboard
│   │       ├── components/      # Shared partials (navbar, messages, footer)
│   │       └── pages/           # Individual page templates
│   └── static/
│       └── core/
│           ├── css/             # Centralized stylesheets (app.css)
│           ├── js/              # Centralized scripts (app.js)
│           ├── images/          # Static image assets
│           └── icons/           # Brand icons and favicons
│
├── requirements.txt
├── .env.example
├── .gitignore
└── README.md
```

## 4. Installation Instructions

Clone the repository and change into the `admin_web/` directory:

```bash
cd admin_web
```

## 5. Virtual Environment Setup

Create and activate a Python virtual environment:

```bash
python3 -m venv venv
source venv/bin/activate
```

## 6. Dependency Installation

Install all pinned Python dependencies inside the active virtual environment:

```bash
pip install -r requirements.txt
```

## 7. Environment Configuration

Copy `.env.example` to `.env` (never commit `.env` to Git):

```bash
cp .env.example .env
```

Set the required variables in `.env`:

- `DB_ENGINE=django.db.backends.postgresql`
- `DB_NAME=postgres`
- `DB_USER=postgres.<project-ref>` *(from Supabase Dashboard → Connect → Session Pooler)*
- `DB_PASSWORD=<your-supabase-db-password>`
- `DB_HOST=<aws-0-region.pooler.supabase.com>` *(Session Pooler host for IPv4 compatibility)*
- `DB_PORT=5432`
- `DB_SSLMODE=require`
- `DJANGO_DEBUG=true` *(local development only)*
- `SECRET_KEY=<long-random-string>`
- `ALLOWED_HOSTS=localhost,127.0.0.1,testserver`

## 8. Database Configuration

- **Local Development:** Uses discrete `DB_*` variables pointing to the **Supabase Session Pooler** (IPv4-compatible, port `5432`, `CONN_MAX_AGE=60`, `CONN_HEALTH_CHECKS=True`). Direct `db.<project-ref>.supabase.co` endpoints resolve to IPv6 only and fail with `Network is unreachable` on IPv4-only local networks.
- **Production (Vercel Serverless):** Uses **Supabase Transaction Pooler** (`DB_PORT=6543`, `DB_POOL_MODE=transaction`), automatically setting `CONN_MAX_AGE=0`, `DISABLE_SERVER_SIDE_CURSORS=True`, `DEBUG=False`, and `sslmode=require`.
- **Legacy `DATABASE_URL` Fallback:** Still supported when `DB_HOST` is unset.
- **Diagnostics:** Run `python manage.py check_db` to verify environment loading, DNS resolution, TCP IPv4/IPv6 reachability, PostgreSQL authentication, SQL execution, and migration state without modifying data.

## 9. Migration Commands

Check migration status before applying anything:

```bash
python manage.py showmigrations
python manage.py migrate --check
```

Apply pending migrations (never reset or drop existing tables):

```bash
python manage.py migrate
```

When modifying models in `core/models.py`:

```bash
python manage.py makemigrations core
python manage.py migrate
```

## 10. Development Server Startup

```bash
python manage.py runserver
```

Open `http://127.0.0.1:8000/cms/` (or `http://127.0.0.1:8000/healthz` for health checks).

## 11. Creating and Managing the Admin Account

Create a Django superuser (automatically granted the `super_admin` CMS role):

```bash
python manage.py createsuperuser
```

To assign non-superuser editorial roles (`content_editor`, `technical_reviewer`, `super_admin`), create an `AdminProfile` record in `/django-admin/` or via `manage.py shell`.

## 12. How URLs Are Organized

- `config/urls.py` mounts Django Admin at `/django-admin/` and includes `core.urls` at the root `""`.
- `core/urls.py` defines `app_name = "core"` and maps all CMS views (`/cms/...`), public APIs (`/api/v1/public/...`), health checks (`/healthz`), and legacy routes (`/dashboard/`, `/crops/`, `/guides/`, `/accounts/`).
- Always reference routes in Python and templates using named URLs (e.g., `reverse("core:dashboard")` and `{% url 'core:crops_list' %}`).

## 13. Where to Add New Views

Add request handlers in `core/views.py`. Protect CMS routes with `@cms_role_required(...)`, use form classes from `core/forms.py` for input validation, and place reusable business logic in `core/services.py`.

## 14. Where to Add New Templates

- Place full-page templates in `core/templates/core/pages/` and extend `core/base.html` (`{% extends "core/base.html" %}`).
- Place reusable fragments (navigation bars, alerts, cards, footers) in `core/templates/core/components/` and include them via `{% include "core/components/..." %}`.

## 15. Where to Add CSS, JavaScript, and Images

Place all frontend assets inside `core/static/core/`:

- CSS: `core/static/core/css/`
- JavaScript: `core/static/core/js/`
- Images: `core/static/core/images/`
- Icons: `core/static/core/icons/`

Reference them in templates using `{% load static %}` and `{% static 'core/...' %}`.

## 16. Where to Add or Modify Models

Define or update models in `core/models.py`.

- Managed CMS tables should define explicit `class Meta: db_table = "cms_..."` names where preserving legacy naming, or standard `core_*` names for new tables.
- Unmanaged Supabase tables owned by external SQL migrations must keep `managed = False` in `Meta`.

## 17. Testing Commands

Run Django system checks, deployment checks, static collection dry-run, and the full test suite (tests run in isolation on SQLite so Supabase data is never modified):

```bash
python manage.py check
python manage.py check --deploy
python manage.py collectstatic --noinput --dry-run
python manage.py test -v 2
```

## 18. Production Deployment Considerations

- Set `DJANGO_DEBUG=false` (automatically enforced when `VERCEL=1` and `VERCEL_ENV=production`).
- Provide a strong `SECRET_KEY` environment variable (50+ random characters).
- Configure `ALLOWED_HOSTS` and `CSRF_TRUSTED_ORIGINS` with exact hostnames (never `*`).
- Keep `DB_SSLMODE=require` and `DB_PORT=6543` (`CONN_MAX_AGE=0`, `DISABLE_SERVER_SIDE_CURSORS=True`) for Vercel serverless deployments.
- Run `python manage.py collectstatic --noinput` during build so WhiteNoise serves `core/static/core/*` from `staticfiles/`.

## How Future Developers Should Add a New Page

1. **Create a form (if the page accepts POST input):** Add a `forms.Form` or `forms.ModelForm` class in `core/forms.py`.
2. **Add business logic (if needed):** Put reusable queries, permissions, or workflow helpers in `core/services.py`.
3. **Write the view:** Add a view function in `core/views.py` decorated with `@cms_role_required(...)` (or `@login_required`) that renders `core/pages/<your_page>.html`.
4. **Create the template:** Add `core/templates/core/pages/<your_page>.html` extending `core/base.html` and reusing partials from `core/templates/core/components/`.
5. **Register the URL:** Add a `path(...)` entry with a descriptive `name="..."` in `core/urls.py`.
6. **Link it in navigation:** Add a `{% url 'core:<name>' %}` link in `core/templates/core/components/navbar.html`.
7. **Add static assets (optional):** Place page CSS/JS/icons under `core/static/core/` and load via `{% static %}`.
8. **Write tests & verify:** Add test cases in `core/tests.py`, then run `python manage.py check` and `python manage.py test -v 2`.
