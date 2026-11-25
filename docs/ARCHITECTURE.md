# System Architecture — Bakaiya Krishi Saathi

**Package:** `com.krishisaathi.app`  
**Min SDK / Target SDK:** API 26 (Android 8.0) / API 37  
**UI & State Pattern:** Jetpack Compose + Material 3 + MVVM (`StateFlow`) + Manual DI (`AppContainer`) + Offline-First Room Repository

## 1. Layer Breakdown

```
UI Layer (Jetpack Compose)
  ├── MainActivity + MainViewModel (First-launch registration gate)
  ├── RegistrationScreen + RegistrationViewModel (SavedStateHandle form)
  ├── MainScaffold + KrishiSaathiNavHost (4-tab navigation)
  ├── HomeScreen (Categories, Seasonal Crops, Video Shortcuts)
  ├── CropsScreen (Nepali/English Search, Category Filter)
  ├── CropDetailScreen (22 Sections, Gallery, Video Playlists, Offline Download)
  ├── HelplineScreen -> UnderConstructionScreen ("निर्माणाधीन")
  ├── ProfileScreen -> UnderConstructionScreen ("निर्माणाधीन")
  ├── YouTubeChannelsScreen (Verified Nepali Agri Channels)
  ├── DownloadedVideosScreen + VideoPlayerScreen (Media3 ExoPlayer)
  └── SplashScreen (Calm Light Mode Boot Transition)
         │
         ▼
Domain Layer
  ├── Coordinator: FarmerRegistrationCoordinator (Validation + atomic Room/DataStore save)
  ├── Models: BakaiyaCrop, CropCategory, CropImage, EducationalVideo,
  │           HelplineContact, FarmerRegistration, BakaiyaMunicipality, YouTubeChannel
  └── Repository Interface: BakaiyaCropRepository
         │
         ▼
Data & Media Layer
  ├── BakaiyaCropRepositoryImpl (File verification + Registration validation)
  ├── Room Database v6 (KrishiSaathiDatabase, MIGRATION_5_6, BakaiyaCropDao, 6 Entities, BakaiyaSeedData)
  ├── VideoDownloadManager (HTTPS Range Resume + StatFs check) & YouTubeLauncher
  ├── DataStoreManager : RegistrationPreferences (registration_completed, last_sync_timestamp)
  └── SupabaseClientProvider (Remote Client) & SyncWorker (WorkManager)
```

## 2. Key Architectural Decisions
- **Mandatory First-Launch Registration Without Auth:** Collects Name, fixed Municipality, Ward (1–12), and Mobile (`+977`) once without passwords, OTP, or MPIN.
- **Strict Separation of Media:** YouTube URLs launch externally via `YouTubeLauncher`; only explicitly permitted HTTPS `.mp4` streams can be downloaded and played offline in `VideoPlayerScreen`.
- **Single Source of Truth:** All agricultural flows collect reactively from Room via `BakaiyaCropRepository`.
