# System Architecture — Bakaiya Krishi Saathi

**Package:** `com.krishisaathi.app`  
**Min SDK / Target SDK:** API 26 (Android 8.0) / API 37  
**UI & State Pattern:** Jetpack Compose + Material 3 + MVVM (`StateFlow`) + Manual DI (`AppContainer`) + Offline-First Room Repository

## 1. Layer Breakdown

```
UI Layer (Jetpack Compose)
  ├── MainScaffold + KrishiSaathiNavHost
  ├── HomeScreen (Categories, Seasonal Crops, Profile Dialog, Quick Shortcuts)
  ├── CropsScreen (Nepali/English Search, Category Filter)
  ├── CropDetailScreen (22 Sections, Gallery, Video Playlists, Offline Download)
  ├── FarmRecordsScreen (Income/Expense Ledger + Bookkeeping Guide)
  ├── YouTubeChannelsScreen (Verified Nepali Agri Channels)
  ├── HelplineScreen (Ward Filters + ACTION_DIAL)
  ├── DownloadedVideosScreen + VideoPlayerScreen (Media3 ExoPlayer)
  └── SplashScreen (Calm Light Mode Boot Transition)
         │
         ▼
Domain Layer
  ├── Models: BakaiyaCrop, CropCategory, CropImage, EducationalVideo,
  │           HelplineContact, FarmerRegistration, FarmRecord, YouTubeChannel
  └── Repository Interface: BakaiyaCropRepository
         │
         ▼
Data & Media Layer
  ├── BakaiyaCropRepositoryImpl (File verification + Validation)
  ├── Room Database v5 (KrishiSaathiDatabase, BakaiyaCropDao, 7 Entities, BakaiyaSeedData)
  ├── VideoDownloadManager (HTTPS Range Resume + StatFs check) & YouTubeLauncher
  ├── DataStoreManager (lastSyncTimestamp)
  └── SupabaseClientProvider (Remote Client) & SyncWorker (WorkManager)
```

## 2. Key Architectural Decisions
- **Zero-Gate Access:** No login/OTP/MPIN blocking screens.
- **Strict Separation of Media:** YouTube URLs launch externally via `YouTubeLauncher`; only explicitly permitted HTTPS `.mp4` streams can be downloaded and played offline in `VideoPlayerScreen`.
- **Single Source of Truth:** All UI flows collect reactively from Room via `BakaiyaCropRepository`.
