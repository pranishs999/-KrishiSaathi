# Testing Strategy — Bakaiya Krishi Saathi

## 1. JVM Unit Tests (`app/src/test/java/com/krishisaathi/app/`)
Run via `./gradlew test`:

- **`BakaiyaValidationTest.kt`**:
  - Nepal mobile (`98`/`97`/`96`, `+977` prefix stripping, Devanagari numerals ०–९) and landline validation.
  - Ward number (`1..12`) validation.
  - Strict HTTPS YouTube watch URL (`isValidYouTubeUrl`) and channel URL (`isValidYouTubeChannelUrl`) validation.
  - Download permission & HTTPS enforcement (`isAuthorizedDownloadUrl`) and file size formatting.
- **`RegistrationFlowTest.kt`**:
  - Verifies empty name, empty ward, and invalid phone validation failures without modifying Room or DataStore.
  - Verifies DataStore `registration_completed` is NEVER set when database persistence fails.
  - Verifies DataStore write failure surfaces `PersistenceFailed`.
  - Verifies valid submission persists both Room (`fullName`, `municipality`, `wardNumber`, `phoneNumber`) and DataStore and transitions `AppEntryState` from `NeedsRegistration` to `Registered`.
  - Verifies safe recovery when DataStore throws `IOException`.
- **`BakaiyaSeedDataIntegrityTest.kt`**:
  - Verifies all 6 Nepali crop categories, source attribution, environmental bounds, YouTube/download strict separation, and verified channel handles/URLs.
- **`BakaiyaCropRepositoryTest.kt`**:
  - Uses `FakeBakaiyaCropDao` and JUnit `TemporaryFolder`.
  - Verifies seeding/sync, Nepali + English crop search, category filtering, offline video physical file verification/deletion, and farmer registration validation/persistence.
- **`AppConfigTest.kt` & `CoreArchitectureTest.kt`**:
  - Verifies root package entry points, `DefaultDispatcherProvider`, and `AppResult`.

## 2. Instrumented UI Tests (`app/src/androidTest/java/com/krishisaathi/app/`)
Compiled via `./gradlew assembleDebugAndroidTest` (executed with `./gradlew connectedDebugAndroidTest` on a device/emulator):

- **`BakaiyaUiTest.kt`**:
  - Verifies first-launch displays `RegistrationScreen`, blocks Home before registration, validates empty inputs, completes registration, navigates to Home, and survives `ActivityScenario.recreate()`.
  - Verifies registered launch opens Home directly, displays 4 navigation tabs (`गृहपृष्ठ`, `बालीहरू`, `किसान हेल्पलाइन`, `मेरो विवरण`) without `फार्म हिसाब`, and opens both Under Construction screens.
  - Verifies Nepali crop search (`धान`), `CropDetailScreen` 22-section scroll, and `YouTubeChannelsScreen` navigation remain intact.
