# बकैया कृषि साथी (Bakaiya Krishi Saathi) — Project Audit

**Version:** v1.1-revision  
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
| Database | Room v6 (offline-first, `MIGRATION_5_6`) + Preferences DataStore + Supabase |
| Media Player | Media3 ExoPlayer |

## 2. Architecture

```
app/src/main/java/com/krishisaathi/app/
├── core/
│   ├── common/        # AppResult, DispatcherProvider
│   └── util/          # ValidationUtils
├── data/
│   ├── local/         # Room entities, DAO, database v6, seed data, DataStoreManager
│   ├── remote/        # SupabaseClientProvider
│   └── repository/    # BakaiyaCropRepositoryImpl
├── di/                # AppContainer (manual DI)
├── domain/
│   ├── model/         # BakaiyaCrop, CropCategory, EducationalVideo, FarmerRegistration, YouTubeChannel
│   ├── registration/  # FarmerRegistrationCoordinator
│   └── repository/    # BakaiyaCropRepository interface
├── media/             # VideoDownloadManager, YouTubeLauncher
├── ui/
│   ├── components/    # UnderConstructionScreen (shared component)
│   ├── navigation/    # BottomNavItem, NavHost, MainScaffold
│   ├── screens/
│   │   ├── channels/  # YouTubeChannelsScreen
│   │   ├── crops/     # CropsScreen, CropDetailScreen, ViewModels
│   │   ├── helpline/  # HelplineScreen (Under Construction)
│   │   ├── home/      # HomeScreen, ViewModel
│   │   ├── profile/   # ProfileScreen (Under Construction)
│   │   ├── registration/ # RegistrationScreen, RegistrationViewModel
│   │   ├── splash/    # SplashScreen
│   │   └── videos/    # DownloadedVideosScreen, VideoPlayerScreen
│   └── theme/         # Color, Theme, Type
├── worker/            # SyncWorker
├── KrishiSaathiApp.kt
├── MainViewModel.kt
└── MainActivity.kt
```

## 3. Feature Summary

### Active Features
1. **Mandatory First-Launch Farmer Registration** — Collects Name, fixed Municipality (`बकैया गाउँपालिका, मकवानपुर।`), Ward (1–12), and Mobile (`+977`) once without OTP, password, or MPIN; persists across Room + DataStore.
2. **Home Screen** — Welcome header, search pill, category grid, Downloaded Videos & YouTube Channels shortcuts, seasonal/featured crops.
3. **Crop Categories & Listings** — 6 verified categories with live Nepali, English, and scientific search.
4. **Crop Detail (22 sections)** — Overview, varieties, season, environmental metrics, 9 cultivation sections, image gallery, video learning.
5. **YouTube Educational Videos & Channel Directory** — 10 verified YouTube videos, 7 Nepali agricultural YouTube channels, 8-topic filter, YouTube app launch.
6. **Authorized Offline Downloads & Library** — Media3 ExoPlayer, download/pause/resume/delete, physical file verification.

### Under Construction Destinations
7. **Farmer Helpline (`किसान हेल्पलाइन`)** — Displays approved Under Construction state (`Icons.Outlined.SupportAgent`, `"निर्माणाधीन"`).
8. **Profile (`मेरो विवरण`)** — Displays approved Under Construction state (`Icons.Outlined.Construction`, `"निर्माणाधीन"`).

### Removed Modules
- **Farm Hisab (`फार्म हिसाब` / `farm_records`)** — Removed completely from UI, navigation, domain, DAO, Room schema (`MIGRATION_5_6`), and Supabase initial schema.
- **Optional Registration Dialog (`RegistrationDialog`)** — Replaced by mandatory first-launch `RegistrationScreen`.

## 4. Database Schema

### Room Entities (v6)
| Table | Description |
|---|---|
| `crop_categories` | 6 agricultural crop categories |
| `crops` | 8 verified crops with 22 cultivation sections |
| `crop_images` | 6 Wikimedia Commons crop images |
| `educational_videos` | 10 YouTube + 2 sample offline videos (12 total) |
| `helpline_contacts` | Verified municipality schema contacts (not displayed while Helpline UI is Under Construction) |
| `farmer_registration` | Single-row mandatory farmer registration record (`full_name`, `municipality`, `ward_number`, `phone_number`, `registered_at`) |

## 5. Navigation

4-tab bottom navigation:
1. `गृहपृष्ठ` (Home)
2. `बालीहरू` (Crops)
3. `किसान हेल्पलाइन` (Helpline — Under Construction)
4. `मेरो विवरण` (Profile — Under Construction)

## 6. Known Limitations
1. Farmer Profile (`मेरो विवरण`) and Farmer Helpline (`किसान हेल्पलाइन`) are Under Construction placeholders until official management/directory UI is released.
2. Sample offline videos use Google test bucket MP4s (`isSampleMedia = true`).
3. Supabase sync currently refreshes seeded local content until live credentials/endpoints are supplied.
4. Light mode only by design specification.
