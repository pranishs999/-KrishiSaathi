# बकैया कृषि साथी (Bakaiya Krishi Saathi)

*Offline-First Android Agricultural Knowledge & Farm Record Companion for Bakaiya Rural Municipality, Makwanpur, Nepal*

---

## Overview

**बकैया कृषि साथी (Bakaiya Krishi Saathi)** is a Nepali-first, light-mode-only native Android application built with Kotlin and Jetpack Compose for farmers in **Bakaiya Rural Municipality (बकैया गाउँपालिका), Makwanpur, Nepal**.

Core agricultural knowledge operates **100% offline** via a pre-seeded Room SQLite database (`KrishiSaathiDatabase`), with zero login, OTP, or MPIN gates.

---

## Implemented Features

1. **गृहपृष्ठ (Home Screen):** Welcome banner, instant crop search bar, optional local farmer profile card (Name, Ward 1–12, Phone), 6 crop category cards, quick shortcuts, NARC seasonal highlights, and featured crops.
2. **कुन खेती कसरी गर्ने? (`CropsScreen` & `CropDetailScreen`):**
   - Live Nepali, English, and scientific-name crop search and category filtering across 8 verified crops (`धान`, `मकै`, `काउली`, `गोलभेंडा`, `अदुवा`, `तोरी`, `मसुरो`, `केरा`).
   - 22-section Crop Detail guide covering overview, varieties, planting season, altitude, environmental requirements (temperature, humidity, soil types, pH, sunlight, water requirement), 9 expandable cultivation stages, Wikimedia Commons reference images with fullscreen viewer, and topic-filtered video tutorials across 8 cultivation stages (`VideoTopic`).
3. **फार्मको हिसाब कसरी राख्ने? (`FarmRecordsScreen`):**
   - Local-first income and expense ledger (`farm_records`) with Nepali categories, optional crop tagging, net balance summary, type filters (`सबै`, `आम्दानी`, `खर्च`), delete confirmation, and an expandable Nepali bookkeeping guide.
4. **कृषि प्रविधि युट्यूब च्यानलहरू (`YouTubeChannelsScreen` & Video Learning):**
   - Dedicated directory of 7 verified Nepali agricultural YouTube channels (`AITC Nepal`, `Krishi Television HD`, `NTV NEWS NEPAL`, `Krishi Patrika`, `LAB TV NEPAL`, `Agro Chiran`, `कृषि लोकसेवा तयारी`) opened safely via `YouTubeLauncher`.
   - Crop-level verified YouTube video links (`provider = "youtube"`, never downloaded) plus an authorized HTTPS offline downloader (`VideoDownloadManager`) and offline Media3 ExoPlayer (`VideoPlayerScreen`) demonstrated with explicitly labeled sample MP4 media (`isSampleMedia = true`).
5. **किसान हेल्पलाइन (`HelplineScreen`):**
   - Verified public contacts from `bakaiyakrishi.gov.np`, ward filter chips (Central Agriculture Branch + Wards 1–12), empty-state fallback guidance, and direct `Intent.ACTION_DIAL` support.

---

## Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin + Coroutines + Flow |
| **SDK** | Min SDK 26 (Android 8.0) / Target SDK 37 |
| **UI** | Jetpack Compose + Material Design 3 (Light Mode Only — Natural Ivory `#F6F7EF` & Forest Green `#174D36`) |
| **Architecture** | MVVM (`StateFlow`) + Manual DI (`AppContainer`) + Offline-First Repository |
| **Local Storage** | Room Database v5 + DataStore Preferences |
| **Media** | AndroidX Media3 ExoPlayer + Coil Compose |
| **Background Sync** | AndroidX WorkManager (`SyncWorker`) + Supabase Kotlin SDK |

---

## Build & Verification

```bash
# Build Debug APK
./gradlew assembleDebug

# Build Android Instrumentation Test APK
./gradlew assembleDebugAndroidTest

# Run JVM Unit Tests
./gradlew testDebugUnitTest

# Run Android Lint
./gradlew lintDebug
```

Optional Supabase credentials can be supplied in root `local.properties`:
```properties
sdk.dir=/path/to/android-sdk
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

---

## Documentation Suite

- [PROJECT_AUDIT.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/PROJECT_AUDIT.md) — Current codebase structure and component inventory
- [ARCHITECTURE.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/ARCHITECTURE.md) — UI, Domain, Data, and Media architecture
- [REQUIREMENTS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/REQUIREMENTS.md) — Product scope and implemented capabilities
- [DATA_SOURCES.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/DATA_SOURCES.md) — Verified NARC/AITC, Wikimedia, YouTube, and Bakaiya contact provenance
- [AGRICULTURAL_DATA_POLICY.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/AGRICULTURAL_DATA_POLICY.md) — Data truthfulness and zero-hallucination rules
- [API_CONTRACTS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/API_CONTRACTS.md) — Repository, Media, and Supabase PostgreSQL contracts
- [OFFLINE_SYNC.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/OFFLINE_SYNC.md) — Room SSOT and offline download lifecycle
- [AUTHENTICATION.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/AUTHENTICATION.md) — Zero-login access policy and optional registration
- [UI_UX_GUIDELINES.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/UI_UX_GUIDELINES.md) — Approved 14-color palette and Devanagari typography rules
- [TESTING.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/TESTING.md) — Automated unit and instrumented test overview
- [KNOWN_LIMITATIONS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/KNOWN_LIMITATIONS.md) — Documented prototype boundaries
