# Krishi Saathi: Directory Architecture (Sub-phase 1.5)

> **Date:** 2026-11-24  
> **Deliverable:** Organized source structure under `com.krishisaathi.app` aligned with [ARCHITECTURE.md](../ARCHITECTURE.md).

---

## 1. Current Package Layout (`app/src/main/java/com/krishisaathi/app/`)

```text
com.krishisaathi.app/
├── KrishiSaathiApp.kt          # Application entry point & periodic sync scheduling
├── MainActivity.kt             # Single Compose activity host
├── core/
│   ├── common/                 # DispatcherProvider, DefaultDispatcherProvider, AppResult
│   ├── database/               # KrishiSaathiDatabase (Room SSOT)
│   └── network/                # SupabaseClientProvider
├── data/
│   ├── local/
│   │   ├── dao/                # CropDao (Room DAOs)
│   │   ├── entity/             # CropEntity (Room Entities)
│   │   └── prefs/              # DataStoreManager (local preferences)
│   └── remote/
│       └── dto/                # CropRemoteDto (remote DTOs)
├── di/                         # AppContainer, DefaultAppContainer (manual DI)
├── domain/
│   ├── model/                  # Crop (pure Kotlin domain models)
│   └── repository/             # CropRepository (repository contracts)
├── ui/
│   ├── navigation/             # BottomNavItem, KrishiSaathiNavHost, MainScaffold
│   ├── screens/                # splash, home, crops, weather, profile
│   └── theme/                  # Color, Theme, Type (high-contrast Material 3)
└── worker/                     # SyncWorker (WorkManager background sync)
```

---

## 2. Planned Package Targets for Upcoming Phases

As subsequent roadmap phases are implemented, new components belong in the following designated packages:
- `core/security/` — Keystore / `EncryptedSharedPreferences` session token wrapper (Phase 3.8).
- `core/util/` — Nepali calendar & formatting helpers (Phase 4/5).
- `data/repository/` — Repository implementations (`CropRepositoryImpl`, `AuthRepositoryImpl`, etc., Phase 3/7).
- `domain/engine/` — `LandUnitConverter`, `FertilizerCalculatorEngine`, `PesticideDilutionEngine` (Phase 9).
- `ui/components/` — Reusable Compose widgets such as toxicity and PHI badges (Phase 4.4 / 6).
- `ui/screens/...` or feature packages — Auth, onboarding, crop detail, pest diagnostic, admin, and calculators (Phase 3–9).
