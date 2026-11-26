# Production Readiness Baseline & Tracking — Bakaiya Krishi Saathi

**Initial Baseline Date:** 2026-11-26  
**Status Prior to Extension:** NOT READY (Android R8 crop-only core built, missing Livestock module, 4-tab Farmer navigation mismatch, unimplemented Web Admin Panel, unverified Wikimedia image URLs found, Supabase CMS/RLS/workflow schema incomplete).

## Mandatory Pre-Production Gates

| Gate Category | Current State at Audit Start | Target Verification Criteria |
|---|---|---|
| **Android 4-Tab Navigation & Non-Blocking Entry** | Had mandatory blocking Registration & Under Construction Helpline | Open farmer browsing (`गृहपृष्ठ`, `बाली`, `पशुपालन`, `किसान हेल्पलाइन`), non-blocking optional registration, dedicated `निशुल्क कृषि प्राविधिक सेवा` icon, verified `ACTION_DIAL` helpline |
| **Livestock Module (25 Sections)** | Not started | All 8 initial species categories extensible from backend, 25 data-driven sections, veterinary safety warning, breeds, gallery, videos, offline Room cache |
| **Verified Content & Workflow** | Crops had NARC/FAO metrics; livestock/backend workflow missing | Full provenance (`source_documents`, `content_citations`, `content_reviews`), strict `Draft -> Needs Source -> In Review -> Approved -> Published`, unpublished gaps flagged `Needs Source` |
| **English Web Admin CMS (`admin_web/`)** | Not started | Django CMS in English, RBAC (`Super Admin`, `Content Editor`, `Technical Reviewer`), Pillow derivative pipeline, audit logs, security controls, public published API |
| **Release Security & Optimization** | R8 + `network_security_config` active (`3.32 MB` APK / `7.93 MB` AAB) | Verify zero secrets in repo/APK, HTTPS-only, clean lint & automated Android + Django/API/RBAC/offline tests |

> Final production readiness classification will be determined honestly in [RELEASE_CHECKLIST.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/RELEASE_CHECKLIST.md) after full implementation and test execution.
