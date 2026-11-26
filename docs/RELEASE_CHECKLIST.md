# Production Release Checklist — Bakaiya Krishi Saathi

## Android Farmer App
- [x] **4-Tab Public Navigation**: `गृहपृष्ठ`, `बाली`, `पशुपालन`, `किसान हेल्पलाइन` configured in `BottomNavItem.kt`, `MainScaffold.kt`, and `KrishiSaathiNavHost.kt`.
- [x] **Non-Blocking Farmer Access**: Launch proceeds directly from Splash -> Home; optional farmer registration (`registration` route) never blocks crops, livestock, media, or helpline screens.
- [x] **Strict Light Mode Design System**: Strictly uses the 14 approved colors in `Color.kt`, `24.dp` borderless cards, and `RoundedCornerShape(50)` pill buttons (`>= 48.dp`).
- [x] **No Admin UI in APK**: Admin functionality is strictly isolated inside `admin_web/`.
- [x] **Livestock Safety & Data Integrity**: Quantitative drug dosages/rations omitted; veterinary referral banner displayed prominently; unverified species (`sheep`, `duck`) excluded from farmer queries.
- [x] **Verified Helpline Integration**: `ACTION_DIAL` opens the native phone dialer without requiring dangerous `CALL_PHONE` runtime permission.
- [x] **R8 Shrinking & Release Build**: Minification, resource shrinking, `allowBackup="false"`, and release ProGuard rules configured.

## Supabase Backend & English Admin CMS
- [x] **Supabase Migrations 003 & 004**: Normalized crop/livestock/media/audit tables, RLS policies, and verified seed data ready.
- [x] **English-Only Django CMS**: RBAC, state-machine approval gates, Pillow WebP media pipeline, helpline CRUD, and public read-only JSON APIs tested via `manage.py test cms`.
- [ ] **Pre-Deploy Environment Setup**: Export `SECRET_KEY`, `DATABASE_URL` (Supabase Postgres), and `DJANGO_DEBUG=false` on hosting target before starting WSGI server.
