# Krishi Saathi: System Architecture & Technical Design

---

## 1. Architectural Philosophy

Krishi Saathi adheres to **Clean Architecture** principles and the official **Android Modern App Architecture** recommendations. It uses:
- **Model-View-ViewModel (MVVM)** for the presentation layer.
- **Unidirectional Data Flow (UDF)** for predictable state management.
- **Repository Pattern** serving as an abstraction over local and remote data sources.
- **Offline-First Reactive Pipeline** where the local **Room SQLite Database** is the single source of truth (SSOT).

```
+-----------------------------------------------------------------------------------+
|                                PRESENTATION LAYER                                 |
|                                                                                   |
|   +--------------------------+                 +------------------------------+   |
|   |   Jetpack Compose UI     | <--(UiState)--- |          ViewModel           |   |
|   | (Stateless Composables)  | ---(UiEvent)--> | (StateFlow, CoroutineScope)  |   |
|   +--------------------------+                 +------------------------------+   |
+-----------------------------------------------------------------------------------+
                                         |
                                (Repository Calls)
                                         v
+-----------------------------------------------------------------------------------+
|                                   DATA LAYER                                      |
|                                                                                   |
|                        +------------------------------+                           |
|                        |   Offline-First Repository   |                           |
|                        +------------------------------+                           |
|                               /                \                                  |
|                    (Flow / Local DB)      (Sync / Cloud API)                      |
|                             v                    v                                |
|                 +--------------------+  +--------------------+                    |
|                 |    Room Database   |  |   Supabase Client  |                    |
|                 | (SQLite Cache SSOT)|  | (PostgreSQL & Auth)|                    |
|                 +--------------------+  +--------------------+                    |
+-----------------------------------------------------------------------------------+
```

---

## 2. Layer Specifications

### 2.1 Presentation Layer (Jetpack Compose + MVVM)
- **Declarative UI:** Built entirely using **Jetpack Compose** with **Material 3**. No legacy XML view bindings or fragments.
- **Unidirectional Data Flow (UDF):**
  - **State Down:** ViewModels expose a single, immutable `StateFlow<UiState>` to Composables.
  - **Events Up:** Composables dispatch user intentions via typed events (e.g., `CropListEvent.SearchQueryChanged`, `CalculatorEvent.CalculateDose`).
- **Lifecycle Awareness:** Screens collect state using `collectAsStateWithLifecycle()` to prevent unnecessary computations and memory leaks during configuration changes or backgrounding.
- **Outdoor Accessibility:** High-contrast color palette, scalable typography (`sp`), and minimum 48×48 dp interactive touch targets for field utility.

```kotlin
// Example UDF Pattern for Crop Detail
data class CropDetailUiState(
    val isLoading: Boolean = false,
    val crop: Crop? = null,
    val stages: List<CropStage> = emptyList(),
    val errorMessage: String? = null
)

sealed interface CropDetailEvent {
    data class LoadCrop(val cropId: String) : CropDetailEvent
    data class StageSelected(val stageId: String) : CropDetailEvent
}
```

### 2.2 Domain Layer (Calculation & Business Logic)
To maintain strict data integrity and testability, core mathematical calculations are isolated in pure Kotlin domain engines with **zero Android framework dependencies**:
- `LandUnitConverter`: Bi-directional conversion between Nepali traditional units:
  - Hills: $1\text{ Ropani} = 16\text{ Aana} = 64\text{ Paisa} = 256\text{ Daam} \approx 508.72\text{ m}^2$
  - Terai: $1\text{ Bigha} = 20\text{ Katha} = 400\text{ Dhur} \approx 6772.63\text{ m}^2$
- `FertilizerCalculatorEngine`: Translates standard MoALD recommendation rates (kg/ha) into exact quantities of commercial fertilizers (Urea, DAP, Muriate of Potash, Organic Farmyard Manure) for the farmer's specific land size.
- `PesticideDilutionEngine`: Computes liquid chemical volume ($mL$) or wettable powder ($g$) per 16-liter knapsack sprayer tank, enforcing maximum safe concentrations.

### 2.3 Data Layer (Repository Pattern & Offline Cache)
The Data Layer orchestrates local storage and remote networking:
- **Repository Interface:** Defines contracts (e.g., `CropRepository`, `PestDiseaseRepository`, `AuthRepository`).
- **Single Source of Truth (SSOT):** Repositories always expose local Room database queries as reactive `Flow<T>`. UI observers receive updates immediately from local disk storage.
- **Remote Synchronization:** A background sync routine queries Supabase for updated rows since the last recorded timestamp (`last_synced_at`), updates Room inside an atomic transaction, and Room automatically pushes new data downstream.

---

## 3. Data Synchronization Flow

```
[Farmer Device]                     [Local Room SQLite]                  [Supabase PostgreSQL]
       |                                     |                                     |
       | 1. User opens Crop Guide Screen     |                                     |
       |------------------------------------>|                                     |
       | 2. Emit cached data immediately     |                                     |
       |<------------------------------------|                                     |
       |    (Render UI in ~5ms, zero network)|                                     |
       |                                     |                                     |
       | 3. Check connectivity & trigger sync in background                        |
       |-------------------------------------------------------------------------->|
       |                                     | 4. Fetch delta (updated_at > t)     |
       |                                     |<------------------------------------|
       |                                     | 5. Upsert delta in transaction      |
       |                                     |--+                                  |
       |                                     |  |                                  |
       |                                     |<-+                                  |
       | 6. Flow triggers reactive UI update |                                     |
       |<------------------------------------|                                     |
```

### 3.1 Conflict Resolution Strategy
- **Agronomic Content (Crops, Guides, Pests, Pesticides, Fertilizers):**
  - **Remote Wins.** Agricultural authorities/admins retain ultimate authority over technical guidelines. Incoming remote records overwrite local records matching primary keys.
- **Farmer Profile & Personal Land Data:**
  - **Local Client Wins.** If the farmer modifies their local municipality, ward, or cultivated crops while offline, the local timestamp is preserved and merged upward to Supabase once connectivity is established.

---

## 4. Supabase Integration Architecture

- **Supabase Auth:**
  - Phone OTP authentication provider.
  - Generates signed JWTs stored in Android Keystore / `EncryptedSharedPreferences`.
- **PostgREST Client:**
  - Lightweight HTTPS REST requests to Supabase tables.
  - Queries filtered by Row Level Security (RLS) policies.
- **Real-Time Subscriptions (Future Phase):**
  - Supabase Realtime channels for high-priority emergency agricultural warnings.

---

## 5. Security & Secure Storage Architecture

1. **Android Keystore Integration:** Sensitive cryptographic keys and JWT tokens are stored using the Android Keystore system with AES-256 GCM encryption.
2. **EncryptedSharedPreferences:** User session state and last sync timestamps reside in encrypted preferences preventing access on rooted or compromised devices.
3. **Network Security Config:** Cleartext HTTP traffic is strictly prohibited (`android:usesCleartextTraffic="false"`). All remote communication utilizes TLS 1.3 with certificate validation.

---

## 6. Directory Structure & Modular Packaging

The project follows a feature-driven, clean-layered package layout under `com.krishisaathi.app`:

```
com.krishisaathi.app/
│
├── core/
│   ├── common/         # Dispatcher providers, Resource wrapper, Result types
│   ├── database/       # Room Database class, TypeConverters, Migration scripts
│   ├── network/        # Supabase client singleton, network monitor
│   ├── security/       # Keystore helper, EncryptedPrefs wrapper
│   └── util/           # Date formatters, Nepali calendar helpers
│
├── data/
│   ├── local/
│   │   ├── dao/        # Room DAOs (CropDao, PestDao, ProfileDao, etc.)
│   │   └── entity/     # Room Entities (CropEntity, PestEntity, etc.)
│   ├── remote/
│   │   └── dto/        # Supabase DTO models (Kotlinx Serialization)
│   └── repository/     # Repository implementations (CropRepositoryImpl, etc.)
│
├── domain/
│   ├── engine/         # LandUnitConverter, FertilizerCalculatorEngine
│   ├── model/          # Clean domain models (Crop, Pest, FertilizerPlan)
│   └── repository/     # Repository interfaces
│
└── ui/
    ├── theme/          # Color, Type, Shape, Theme (Outdoor high-contrast)
    ├── navigation/     # NavHost, AppDestinations, BottomNavigation
    ├── components/     # Reusable Compose widgets (ToxicityBadge, PHIBadge)
    ├── auth/           # PhoneLoginScreen, OtpVerificationScreen, AuthViewModel
    ├── onboarding/     # FarmerProfileSetupScreen, OnboardingViewModel
    ├── crop/           # CropListScreen, CropDetailScreen, CropViewModel
    ├── pest/           # PestDiagnosticScreen, PestDetailScreen, PestViewModel
    ├── calendar/       # SeasonalCalendarScreen, CalendarViewModel
    └── calculator/     # FertilizerCalcScreen, PesticideCalcScreen, CalcViewModel
```

