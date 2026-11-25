# Testing Strategy — Bakaiya Krishi Saathi

## 1. JVM Unit Tests (`app/src/test/java/com/krishisaathi/app/`)
Run via `./gradlew testDebugUnitTest`:

- **`BakaiyaValidationTest.kt`**:
  - Nepal 10-digit mobile (`98`/`97`/`96`) and landline (`0XX`) validation.
  - Strict HTTPS YouTube watch URL (`isValidYouTubeUrl`) and channel URL (`isValidYouTubeChannelUrl`) validation.
  - Download permission & HTTPS enforcement (`isAuthorizedDownloadUrl`).
  - Human-readable file size formatting (`formatFileSize`).
- **`BakaiyaSeedDataIntegrityTest.kt`**:
  - Verifies all 6 Nepali crop categories exist.
  - Ensures every crop maps to a valid category, carries non-blank source attribution, and has non-inverted temperature/pH bounds.
  - Enforces strict separation between non-downloadable YouTube videos (`downloadPermission = false`) and authorized sample offline downloads.
  - Validates helpline phone numbers and `isVerified = true`.
  - Validates `VerifiedYouTubeChannels.channels` handles (`@...`) and HTTPS channel URLs.
- **`BakaiyaCropRepositoryTest.kt`**:
  - Uses `FakeBakaiyaCropDao` and JUnit `TemporaryFolder`.
  - Verifies seeding/sync, Nepali + English crop search, category filtering.
  - Verifies offline video physical file verification and deletion cleanup.
  - Verifies optional farmer registration persistence and clearing without auth.
  - Verifies `FarmRecord` validation, income/expense totals, filtering, and deletion.
- **`AppConfigTest.kt` & `CoreArchitectureTest.kt`**:
  - Verifies root package entry points (`KrishiSaathiApp`, `MainActivity`), `DefaultDispatcherProvider`, and `AppResult` sealed states.

## 2. Instrumented UI Tests (`app/src/androidTest/java/com/krishisaathi/app/`)
Compiled via `./gradlew assembleDebugAndroidTest` (requires connected Android device/emulator to execute `./gradlew connectedDebugAndroidTest`):

- **`BakaiyaUiTest.kt`**:
  - Verifies all 4 bottom navigation tabs (`गृहपृष्ठ`, `बालीहरू`, `फार्म हिसाब`, `हेल्पलाइन`) and transitions.
  - Verifies Nepali crop search (`धान`), opening `CropDetailScreen`, scrolling to environmental metrics & video sections, and back navigation.
  - Verifies Home shortcut opens `YouTubeChannelsScreen` (`कृषि युट्यूब च्यानलहरू`) and navigates back cleanly.
