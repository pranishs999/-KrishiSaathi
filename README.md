# बकैया कृषि साथी (Bakaiya Krishi Saathi)

*Offline-First Android Agricultural Knowledge Companion for Bakaiya Rural Municipality, Makwanpur, Nepal*

---

## Overview

**बकैया कृषि साथी (Bakaiya Krishi Saathi)** is a Nepali-first, light-mode-only native Android application built with Kotlin and Jetpack Compose for farmers in **Bakaiya Rural Municipality (बकैया गाउँपालिका), Makwanpur, Nepal**.

Core agricultural knowledge operates **100% offline** via a pre-seeded Room SQLite database (`KrishiSaathiDatabase`), entered through a one-time mandatory first-launch registration without OTP, passwords, or MPIN.

---

## Implemented Features

1. **अनिवार्य किसान दर्ता (`RegistrationScreen` — First Launch):**
   - Collects **किसानको नाम**, fixed **गाउँपालिका (`बकैया गाउँपालिका, मकवानपुर।`)**, **वडा नम्बर (`1–12`)**, and **मोबाइल नम्बर (`+977`)**.
   - Saved persistently across Room (`farmer_registration`) and DataStore (`registration_completed`) so registration opens once on fresh install and routes straight to Home on all subsequent launches.
2. **गृहपृष्ठ (Home Screen):**
   - Welcome banner, instant crop search bar, 6 crop category cards, video library & YouTube channel shortcuts, NARC seasonal highlights, and featured crops.
3. **कुन खेती कसरी गर्ने? (`CropsScreen` & `CropDetailScreen`):**
   - Live Nepali, English, and scientific-name crop search and category filtering across 8 verified crops (`धान`, `मकै`, `काउली`, `गोलभेंडा`, `अदुवा`, `तोरी`, `मसुरो`, `केरा`).
   - 22-section Crop Detail guide covering overview, varieties, planting season, altitude, environmental requirements (temperature, humidity, soil types, pH, sunlight, water requirement), 9 expandable cultivation stages, Wikimedia Commons reference images with fullscreen viewer, and topic-filtered video tutorials across 8 cultivation stages (`VideoTopic`).
4. **कृषि प्रविधि युट्यूब च्यानलहरू (`YouTubeChannelsScreen` & Video Learning):**
   - Dedicated directory of 7 verified Nepali agricultural YouTube channels opened safely via `YouTubeLauncher`.
   - Crop-level verified YouTube video links (`provider = "youtube"`, never downloaded) plus an authorized HTTPS offline downloader (`VideoDownloadManager`) and offline Media3 ExoPlayer (`VideoPlayerScreen`).
5. **किसान हेल्पलाइन (`HelplineScreen`) & मेरो विवरण (`ProfileScreen`):**
   - Dedicated bottom navigation destinations (`किसान हेल्पलाइन`, `मेरो विवरण`) currently displaying clean Under Construction screens (`निर्माणाधीन`).

---

## Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin + Coroutines + Flow |
| **SDK** | Min SDK 26 (Android 8.0) / Target SDK 37 |
| **UI** | Jetpack Compose + Material Design 3 (Light Mode Only — Natural Ivory `#F6F7EF` & Forest Green `#174D36`) |
| **Architecture** | MVVM (`StateFlow` + `SavedStateHandle`) + Manual DI (`AppContainer`) + Offline-First Repository |
| **Local Storage** | Room Database v6 (`MIGRATION_5_6`) + Preferences DataStore |
| **Media** | AndroidX Media3 ExoPlayer + Coil Compose |
| **Background Sync** | AndroidX WorkManager (`SyncWorker`) + Supabase Kotlin SDK |

---

## Build & Verification

```bash
# Clean & Build Debug APK
./gradlew clean
./gradlew assembleDebug --no-configuration-cache

# Run Unit Tests
./gradlew test

# Run Android Lint
./gradlew lint
```

---

## Documentation Suite

- [REGISTRATION_FLOW.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/REGISTRATION_FLOW.md) — Mandatory first-launch registration fields, validation, and Room + DataStore persistence
- [NAVIGATION.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/NAVIGATION.md) — Entry gate and 4-tab navigation architecture
- [DEVELOPMENT_STATUS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/DEVELOPMENT_STATUS.md) — Implemented features, Under Construction screens, and removed modules
- [PROJECT_AUDIT.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/PROJECT_AUDIT.md) — Current codebase structure and component inventory
- [ARCHITECTURE.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/ARCHITECTURE.md) — UI, Domain, Data, and Media architecture
- [REQUIREMENTS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/REQUIREMENTS.md) — Product scope and implemented capabilities
- [DATA_SOURCES.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/DATA_SOURCES.md) — Verified NARC/AITC, Wikimedia, and YouTube provenance
- [AGRICULTURAL_DATA_POLICY.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/AGRICULTURAL_DATA_POLICY.md) — Data truthfulness and zero-hallucination rules
- [API_CONTRACTS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/API_CONTRACTS.md) — Repository, Media, and Supabase PostgreSQL contracts
- [OFFLINE_SYNC.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/OFFLINE_SYNC.md) — Room SSOT and offline download lifecycle
- [AUTHENTICATION.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/AUTHENTICATION.md) — Mandatory registration without login/OTP/MPIN gates
- [UI_UX_GUIDELINES.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/UI_UX_GUIDELINES.md) — Approved 14-color palette and Devanagari typography rules
- [TESTING.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/TESTING.md) — Automated unit and instrumented test overview
- [KNOWN_LIMITATIONS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/KNOWN_LIMITATIONS.md) — Documented prototype boundaries
