# बकैया कृषि साथी (Bakaiya Krishi Saathi) — Project Audit

**Audit Date:** 2026-11-26  
**Auditor:** Lead Android & Backend Systems Engineer  
**Repository:** `/home/pranish/AndroidStudioProjects/KrishiSaathi`

---

## 1. Audited Baseline Inventory

### 1.1 Android Application (`app/`)
- **Language / UI:** Kotlin (Gradle Kotlin DSL), Jetpack Compose BOM `2026.02.01`, Material Design 3 (Light mode only).
- **Build Toolchain:** AGP `9.3.3`, Gradle `9.9.2`, KSP `2.2.10-2.0.2`, Kotlin `2.2.10`, Java compatibility `11`, `minSdk = 26`, `targetSdk = 37`, `compileSdk = 37`.
- **Application ID & Version:** `com.krishisaathi.app`, `versionCode = 1`, `versionName = "1.0"`.
- **Release Optimization (Previously Audited):**
  - R8 minification (`isMinifyEnabled = true`), resource shrinking (`isShrinkResources = true`), custom ProGuard rules (`app/proguard-rules.pro`), `network_security_config.xml` (HTTPS-only), and restricted backup rules (`backup_rules.xml`, `data_extraction_rules.xml`, `allowBackup = false`) are configured.
  - Verified baseline release outputs: `app-release-unsigned.apk` (~3.32 MB compressed, single `classes.dex` ~5.17 MB) and `app-release.aab` (~7.93 MB).
  - Unit tests: 27/27 passing cleanly prior to this extension.
- **Database & Local Storage:**
  - Room DB `KrishiSaathiDatabase` (`version = 7`, with `MIGRATION_5_6` and `MIGRATION_6_7`).
  - Preferences DataStore (`DataStoreManager`).
- **Media & Video Playback:**
  - Coil `2.7.0` (`ImageLoaderFactory` with 15% memory cache & 25 MB bounded disk cache).
  - Media3 ExoPlayer `1.5.1` (`VideoPlayerScreen`) with path-containment check (`filesDir/authorized_videos`).
  - `VideoDownloadManager` with HTTPS redirect guard and HTTP Range resume.
  - `YouTubeLauncher` launching verified YouTube URLs/channels externally via `Intent.ACTION_VIEW`.

### 1.2 Findings & Gaps Identified Before Extension
1. **Stale / Obsolete References Check (Repaired vs Remaining):**
   - Obsolete `Farm Hisab` (`FarmRecord`, `FarmRecordsScreen`) was already removed cleanly (`grep` shows only the safe SQL `DROP TABLE IF EXISTS farm_records` inside `MIGRATION_5_6` and a UI test assertion confirming it is absent).
   - **Conflict with New Master Specification:** The prior revision introduced a **mandatory blocking first-launch registration gate** (`MainActivity` -> `RegistrationScreen`), set Bottom Navigation to `गृहपृष्ठ / बालीहरू / किसान हेल्पलाइन / मेरो विवरण`, and set `HelplineScreen` to Under Construction. The Master Specification requires:
     - **Zero registration/login gates blocking farmer access** (optional registration must remain non-blocking).
     - **Exact 4-tab Bottom Navigation:** `गृहपृष्ठ`, `बाली`, `पशुपालन`, `किसान हेल्पलाइन`.
     - **Live Livestock Education Module (`पशुपालन`)** covering cattle/oxen, buffalo, goats, sheep, pigs, chickens, ducks, and fish across 25 extensible sections.
     - **Restored Helpline (`किसान हेल्पलाइन`)** with verified official contacts and Android `Intent.ACTION_DIAL` plus dedicated **`निशुल्क कृषि प्राविधिक सेवा`** icon/entry.
2. **Broken Wikimedia Commons Image URLs Found in `BakaiyaSeedData.kt`:**
   - Live check against Wikimedia Commons API (`commons.wikimedia.org/w/api.php`) proved three crop thumbnail URLs were broken (`missing: True`: `Rice_plants_(IRRI).jpg`, `Zea_mays_crop.jpg`, `Mustard_field_in_Nepal.jpg`) and `Ginger_plant.jpg` used an incorrect hash prefix (`a/a5` instead of verified `1/1c/Ginger_plant.jpg`).
3. **Missing Central CMS Backend, Livestock Schema, Verification Workflow & Web Admin Panel:**
   - `supabase/migrations/` existed (`001_initial_schema.sql`, `002_seed_data.sql`) and `local.properties` defines `SUPABASE_URL` and `SUPABASE_ANON_KEY`, **but** lacked normalized livestock tables, media asset licensing registry, source citations per claim/section, `review_status` workflow state enforcement, RBAC (`Super Admin`, `Content Editor`, `Technical Reviewer`), and audit logs.
   - No English web admin application (`admin_web/`) existed yet.
   - `BakaiyaCropRepositoryImpl.syncRemoteContent()` currently re-seeded local Room data instead of fetching published content from the central backend.

---

## 2. Extension Plan

1. Keep **Supabase PostgreSQL + Auth + Storage + RLS** as the single authoritative content database (zero competing databases of record), extended cleanly via `003_cms_livestock_and_verification.sql` and `004_verified_crops_and_livestock_seed.sql`.
2. Build the separate **English-only Web Admin Panel (`admin_web/`)** using Django 6.1 + Pillow 12.3 + server-side RBAC, workflow enforcement, Pillow WebP/JPEG image derivative pipeline (`full`, `medium`, `thumb`), and Supabase/PostgreSQL integration + Public Published Content REST API.
3. Extend Android app (`app/`) with non-blocking open access, 4-tab navigation (`गृहपृष्ठ`, `बाली`, `पशुपालन`, `किसान हेल्पलाइन`), dedicated **`निशुल्क कृषि प्राविधिक सेवा`** icon, full extensible 25-section Livestock module, restored `ACTION_DIAL` Helpline, fixed verified Wikimedia image assets, and remote published content sync with offline Room fallback.
