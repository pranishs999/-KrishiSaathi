# Krishi Saathi: Existing Project Audit Report (Sub-phase 1.1)

> **Date:** 2026-11-24  
> **Scope:** Read-only inspection of repository structure, Gradle/AGP/Kotlin configuration, Android manifest, application source code, resources, tests, and documentation prior to Phase 1.2+.

---

## 1. Executive Summary

Krishi Saathi currently exists as a compiling Jetpack Compose Android skeleton (`com.krishisaathi.app`) with an initial Room entity (`CropEntity`), Preferences `DataStoreManager`, a `SupabaseClientProvider` wrapper, a stubbed `SyncWorker`, a Material 3 light/dark theme with Devanagari-friendly typography, and 5 static/preview Compose screens (`SplashScreen`, `HomeScreen`, `CropsScreen`, `WeatherScreen`, `ProfileScreen`).

While the foundational UI shell and build files exist, the core offline-first data layer, full database schema, Supabase serialization, security crypto storage, domain calculation engines, localized string resources (`values-ne`), `ViewModel`/UDF layer, and real automated tests are not yet implemented.

---

## 2. Repository & Git Status

- **Branch at inspection:** `overnight/krishi-saathi-20261124-225727` (HEAD `7add630`, aligned with `main` / `origin/main`).
- **Ignored sensitive/local files present (not tracked by Git):**
  - `local.properties` (contains `sdk.dir`, `SUPABASE_URL`, `SUPABASE_ANON_KEY`)
  - `.supabase.pass`
  - `.antigravity-overnight/` (runner state/logs)
- **Existing build output on disk:** `app/build/outputs/apk/debug/app-debug.apk` (16 MB) and `output-metadata.json`.

---

## 3. Build, Gradle & Dependency Audit

### 3.1 Toolchain & Gradle Configuration
| Item | Observed Value | File |
|---|---|---|
| Gradle Wrapper | `9.9.2` (`gradle-9.9.2-bin.zip`) | `gradle/wrapper/gradle-wrapper.properties` |
| Daemon JVM Toolchain | `25` | `gradle/gradle-daemon-jvm.properties` |
| `org.gradle.java.home` | `/home/pranish/Downloads/android-studio/jbr` | `gradle.properties` |
| Configuration Cache | `true` | `gradle.properties` |
| AGP | `9.3.3` | `gradle/libs.versions.toml` |
| Kotlin | `2.2.10` | `gradle/libs.versions.toml` |
| KSP | `2.2.10-2.0.2` | `gradle/libs.versions.toml` |
| `compileSdk` / `targetSdk` | `37` | `app/build.gradle.kts` |
| `minSdk` | `26` | `app/build.gradle.kts` |
| `buildToolsVersion` | `36.0.0` | `app/build.gradle.kts` |
| `ndkVersion` | `30.0.16138531 rc3` | `app/build.gradle.kts` |
| Java Compatibility | `VERSION_11` | `app/build.gradle.kts` |

### 3.2 Declared Dependencies (`gradle/libs.versions.toml`)
- **Compose BOM:** `2026.02.01` (`ui`, `ui-graphics`, `ui-tooling-preview`, `material3`, `material-icons-core`)
- **AndroidX Core / Lifecycle / Activity:** `core-ktx:1.10.1`, `lifecycle-runtime-ktx:2.6.1`, `activity-compose:1.8.0`
- **Navigation:** `navigation-compose:2.8.8`
- **Room:** `2.6.1` (`room-runtime`, `room-ktx`, `room-compiler` via KSP)
- **DataStore:** `datastore-preferences:1.1.3`
- **WorkManager:** `work-runtime-ktx:2.10.0`
- **Supabase & Ktor:** `supabase:bom:3.1.1` (`postgrest-kt`, `auth-kt`), `ktor-client-android:3.0.3`
- **Testing:** `junit:4.13.2`, `androidx.test.ext:junit:1.1.5`, `espresso-core:3.5.1`, `ui-test-junit4`

### 3.3 Build & Dependency Gaps
1. **Fallback secret placeholder in `app/build.gradle.kts`:** If `local.properties` omits keys, `BuildConfig.SUPABASE_URL` falls back to a hardcoded URL (`https://aheylojvwimspfuivpii.supabase.co`) and `SUPABASE_ANON_KEY` falls back to `"placeholder_anon_key"`.
2. **Release optimization disabled:** `buildTypes.release.optimization.enable = false`, and `app/src/main/keepRules/rules.keep` contains only template comments.
3. **Missing declared dependencies required by architecture/roadmap:**
   - `org.jetbrains.kotlin.plugin.serialization` and `kotlinx-serialization-json` (needed for Supabase `Postgrest` DTO decoding).
   - `androidx.security:security-crypto` (specified in `AGENT_RULES.md` / `SECURITY.md` for Keystore-backed token persistence).
   - `androidx.lifecycle:lifecycle-runtime-compose` (for `collectAsStateWithLifecycle()`).
4. **Sandbox / local CLI execution observation:** Running `./gradlew compileDebugKotlin testDebugUnitTest` inside the restricted sandbox failed with `FileNotFoundException: /home/pranish/.gradle/wrapper/dists/.../gradle-9.9.2-bin.zip.lck (Read-only file system)` and `/home/pranish/Downloads/android-studio/jbr/bin/java: No such file or directory` (while `/usr/bin/java` is OpenJDK `25.0.5`). Full toolchain and Gradle verification is scheduled in Phase 1.2 / 1.3 / 1.7 and by the external runner.

---

## 4. Application Architecture & Source Code Audit

### 4.1 Manifest & Entry Points
- `app/src/main/AndroidManifest.xml`: Requests `INTERNET` and `ACCESS_NETWORK_STATE`; configures `KrishiSaathiApp`, `allowBackup="true"`, and single launcher `MainActivity` (`windowSoftInputMode="adjustResize"`).
- `KrishiSaathiApp.kt`: Instantiates `DefaultAppContainer` and schedules `SyncWorker.enqueuePeriodicSync(this)`.
- `MainActivity.kt`: Renders `KrishiSaathiTheme` with a `Crossfade` from `SplashScreen` to `MainScaffold`.

### 4.2 Dependency Injection (`di/AppContainer.kt`)
- Manual DI (`AppContainer` / `DefaultAppContainer`) exposes `database`, `dataStoreManager`, and `supabaseClient`.
- **Gap:** No repository instances (`CropRepository`, etc.) or `NetworkObserver` are wired yet.

### 4.3 Data & Domain Layers (`data/`, `domain/`, `worker/`)
- **Room (`data/local/`):**
  - `KrishiSaathiDatabase` (`version = 1`, `exportSchema = false`, `.fallbackToDestructiveMigration()`) includes only `CropEntity`. No pre-packaged seed database (`.createFromAsset(...)`) or `TypeConverters` exist yet.
  - `CropEntity` (`tableName = "crops"`) has only 4 fields (`id`, `name_ne`, `name_en`, `image_url`), which does not yet match the full schema in `DATABASE.md`. The remaining core tables (`profiles`, `crop_guides`, `seasonal_recommendations`, `pest_diseases`, `fertilizer_guides`, `pesticide_guides`, plus supporting tables in Phase 2) are not yet implemented.
  - `CropDao` provides reactive `Flow` reads (`getAllCrops`, `getCropById`, `searchCrops`) and non-`suspend` `upsertCrops`, `upsertCrop`, `clearAllCrops`.
- **Preferences (`data/local/prefs/DataStoreManager.kt`):**
  - Stores `last_sync_timestamp`, `is_logged_in`, `user_phone`, and `user_role` in unencrypted Preferences DataStore.
- **Remote (`data/remote/`):**
  - `SupabaseClientProvider` configures `Auth` and `Postgrest`.
  - `CropRemoteDto` lacks `@Serializable` and `@SerialName` annotations.
- **Domain (`domain/`):**
  - `Crop` model and `CropRepository` interface exist, but no `CropRepositoryImpl` exists.
  - No domain calculation engines (`LandUnitConverter`, `FertilizerCalculatorEngine`, `PesticideDilutionEngine`) exist yet.
- **Background Sync (`worker/SyncWorker.kt`):**
  - Configured as a 6-hour periodic `CoroutineWorker` requiring `NetworkType.CONNECTED`, but `doWork()` is a stub that only updates `last_sync_timestamp` without querying Supabase or writing to Room.

### 4.4 Presentation & UI Layer (`ui/`)
- **Theme (`ui/theme/`):**
  - `Color.kt` and `Theme.kt`: High-contrast light/dark schemes (`dynamicColor = false`) using emerald green (`#15803D` / `#16A34A`) and harvest amber (`#D97706`) on white/dark surfaces.
  - `Type.kt`: Material 3 `Typography` with Devanagari-safe line heights (1.35x–1.5x).
- **Navigation (`ui/navigation/`):**
  - `MainScaffold.kt`, `BottomNavItem.kt`, `KrishiSaathiNavHost.kt`: 4 bottom tabs (`home`, `crops`, `weather`, `profile`).
- **Screens (`ui/screens/`):**
  - All screens (`SplashScreen`, `HomeScreen`, `CropsScreen`, `WeatherScreen`, `ProfileScreen`) currently display static hardcoded data.
  - **Rule 1 / MVVM gap:** Zero `ViewModel` classes exist; `CropsScreen` uses an inline `sampleCrops` list instead of observing `CropDao`/`CropRepository`.
  - **Rule 5 (i18n) gap:** `app/src/main/res/values/strings.xml` contains only `app_name`; `values-ne/strings.xml` does not exist; all UI copy is hardcoded in Kotlin composables. `SplashScreen.kt` also hardcodes raw hex colors instead of theme tokens.

---

## 5. Testing & Quality Audit

- **Unit tests (`app/src/test/`):** Only `ExampleUnitTest.kt` (`2 + 2 == 4`).
- **Instrumented tests (`app/src/androidTest/`):** Only `ExampleInstrumentedTest.kt` (package name check).
- **Coverage gaps:** Zero tests for Room DAOs, repositories, sync logic, auth/validation, land/fertilizer/pesticide calculators, or Compose UI.

---

## 6. Key Findings & Priorities for Upcoming Phases

1. **Phase 1.2 – 1.7:** Verify local SDK/JDK/Gradle toolchain outside sandbox constraints, add missing serialization/security-crypto/lifecycle dependencies to `libs.versions.toml` (when scheduled), align package architecture (`core/`, `data/`, `domain/`, `ui/`), and establish a clean baseline build + unit test check.
2. **Phase 2:** Design and version SQL migrations in Supabase for all user, agricultural, and supporting tables with strict RLS policies (`DATABASE.md` / `SECURITY.md`).
3. **Phase 3–7:** Build real Phone OTP auth + encrypted session storage, extract bilingual strings (`values/` + `values-ne/`), wire Room SSOT through `CropRepositoryImpl` + `ViewModel`s, and implement real `SyncWorker` ingestion.
