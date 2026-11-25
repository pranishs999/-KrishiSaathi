# बकैया कृषि साथी (Bakaiya Krishi Saathi) — Project Audit

**Version:** v1.0-prototype  
**Last Updated:** 2026-11-25  
**Auditor:** Antigravity Lead Engineer

---

## 1. Project Identity

| Attribute | Value |
|---|---|
| Package | `com.krishisaathi.app` |
| App Name | बकैया कृषि साथी |
| Platform | Android (API 26+ / Android 8.0+, targetSdk 37) |
| Language | Kotlin + Jetpack Compose |
| Design | Material Design 3 (Light only) |
| UI Language | Nepali (Devanagari-first) |
| Database | Room (offline-first) + Supabase (remote sync) |
| Media Player | Media3 ExoPlayer |

## 2. Architecture

```
app/src/main/java/com/krishisaathi/app/
├── core/
│   ├── common/        # AppResult, DispatcherProvider
│   └── util/          # ValidationUtils
├── data/
│   ├── local/         # Room entities, DAO, database, seed data, DataStoreManager
│   ├── remote/        # SupabaseClientProvider
│   └── repository/    # BakaiyaCropRepositoryImpl
├── di/                # AppContainer (manual DI)
├── domain/
│   ├── model/         # BakaiyaCrop, CropCategory, EducationalVideo, FarmRecord, YouTubeChannel
│   └── repository/    # BakaiyaCropRepository interface
├── media/             # VideoDownloadManager, YouTubeLauncher
├── ui/
│   ├── navigation/    # BottomNavItem, NavHost, MainScaffold
│   ├── screens/
│   │   ├── channels/  # YouTubeChannelsScreen
│   │   ├── crops/     # CropsScreen, CropDetailScreen, ViewModels
│   │   ├── dialogs/   # RegistrationDialog
│   │   ├── helpline/  # HelplineScreen, ViewModel
│   │   ├── home/      # HomeScreen, ViewModel
│   │   ├── records/   # FarmRecordsScreen, ViewModel
│   │   ├── splash/    # SplashScreen
│   │   └── videos/    # DownloadedVideosScreen, VideoPlayerScreen
│   └── theme/         # Color, Theme, Type
├── worker/            # SyncWorker
├── KrishiSaathiApp.kt
└── MainActivity.kt
```

## 3. Features Implemented

### Core Features
1. ✅ **Home Screen** — Welcome header, search pill, farmer profile, category grid, shortcuts, seasonal/featured crops
2. ✅ **Crop Categories** — 6 verified categories with icons
3. ✅ **Crop Listings** — Search (Nepali+English), category filter, verified badges
4. ✅ **Crop Detail (22 sections)** — Overview, varieties, season, environmental metrics, 9 cultivation sections, image gallery, video learning
5. ✅ **YouTube Educational Videos** — 10 verified YouTube videos + 2 sample offline videos (12 total), 8-topic filter, YouTube app launch
6. ✅ **Authorized Offline Downloads** — Media3 ExoPlayer, download/pause/resume/delete, .part atomic rename
7. ✅ **Downloaded Video Library** — Offline playback, file existence verification
8. ✅ **Helpline Directory** — Verified Bakaiya contacts, ward filter, ACTION_DIAL
9. ✅ **Optional Farmer Registration** — 3-field form, edit/clear, no login required

### New Features (Latest Session)
10. ✅ **Farm Record Keeping (फार्मको हिसाब)** — Income/expense tracking with Nepali categories, crop tagging, summary dashboard, net balance
11. ✅ **YouTube Channel Directory (कृषि प्रविधि युट्यूब च्यानलहरू)** — 7 verified Nepali agricultural channels with ACTION_VIEW launch

### Design System
- Natural Ivory (#F6F7EF) canvas background
- Forest Green (#174D36) primary accent
- Light Green (#DCE9DF) card backgrounds
- 24dp rounded corners, 0dp elevation
- Devanagari-optimized typography
- 14-color approved palette

## 4. Database Schema

### Room Entities (v5)
| Table | Description |
|---|---|
| `crop_categories` | 6 agricultural crop categories |
| `crops` | 8 verified crops with 22 cultivation sections |
| `crop_images` | 6 Wikimedia Commons crop images |
| `educational_videos` | 10 YouTube + 2 sample offline videos (12 total) |
| `helpline_contacts` | 2 verified Bakaiya municipality contacts |
| `farmer_registration` | Optional single-row farmer profile |
| `farm_records` | Income/expense tracking records |

### Supabase Migrations
- `001_initial_schema.sql` — Full schema aligned with Room entities
- `002_seed_data.sql` — Verified seed data for categories, crops, contacts

## 5. Data Integrity

All agricultural data sourced from verified NARC/AITC/MoALD publications:
- **8 crops** with verified cultivation data
- **10 YouTube video IDs** confirmed via oEmbed API
- **2 helpline contacts** from bakaiyakrishi.gov.np
- **6 Wikimedia Commons images** (freely licensed)
- **2 Google test bucket MP4s** flagged as `isSampleMedia = true`

No phone numbers, agricultural metrics, or URLs are invented.

## 6. Navigation

4-tab bottom navigation:
1. गृहपृष्ठ (Home)
2. बालीहरू (Crops)
3. फार्म हिसाब (Farm Records) — NEW
4. हेल्पलाइन (Helpline)

Additional screens:
- Crop Detail (22-section)
- Downloaded Videos Library
- Video Player (Media3 ExoPlayer)
- YouTube Channels Directory — NEW

## 7. Dependencies

| Library | Purpose |
|---|---|
| Jetpack Compose + Material 3 | UI framework |
| Navigation Compose | Screen navigation |
| Room + KSP | Local database |
| Media3 ExoPlayer | Offline video playback |
| Coil Compose | Image loading |
| WorkManager | Background sync |
| Supabase Kotlin | Remote backend |
| DataStore Preferences | Local preferences |

## 8. Build Status

- ✅ `assembleDebug` — Successful (`app/build/outputs/apk/debug/app-debug.apk`)
- ✅ `testDebugUnitTest` — 20/20 unit tests passing across 6 test suites
- ✅ `assembleDebugAndroidTest` — Instrumentation test APK built (`app-debug-androidTest.apk`; on-device execution requires `adb`/emulator)
- ✅ `lintDebug` — Passed with 0 lint errors (31 informational/dependency/resource warnings)

## 9. Known Limitations

1. Ward-level phone numbers not available from official sources — only central office contacts included
2. Sample offline videos use Google test bucket MP4s (not real agricultural content)
3. Supabase sync currently re-seeds from local data (no live remote content management)
4. Farm records are local-only (no Supabase sync in prototype)
5. No dark mode (by design — light only)
