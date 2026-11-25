# Test & Build Verification Report

**Date:** 2026-11-25  
**Command:** `./gradlew assembleDebug assembleDebugAndroidTest testDebugUnitTest lintDebug --no-configuration-cache --no-daemon`  
**Exit Status:** `0` (`BUILD SUCCESSFUL`)

## 1. JVM Unit Tests (`testDebugUnitTest`)

| Test Suite | Tests | Passed | Failed | Skipped |
|---|---|---|---|---|
| `com.krishisaathi.app.AppConfigTest` | 2 | 2 | 0 | 0 |
| `com.krishisaathi.app.core.common.CoreArchitectureTest` | 2 | 2 | 0 | 0 |
| `com.krishisaathi.app.core.util.BakaiyaValidationTest` | 5 | 5 | 0 | 0 |
| `com.krishisaathi.app.data.local.BakaiyaSeedDataIntegrityTest` | 5 | 5 | 0 | 0 |
| `com.krishisaathi.app.data.repository.BakaiyaCropRepositoryTest` | 5 | 5 | 0 | 0 |
| `com.krishisaathi.app.ExampleUnitTest` | 1 | 1 | 0 | 0 |
| **Total** | **20** | **20** | **0** | **0** |

## 2. Build & Static Analysis

- `assembleDebug`: Passed (`app/build/outputs/apk/debug/app-debug.apk`)
- `assembleDebugAndroidTest`: Passed (`app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`)
- `lintDebug`: Passed with 0 lint errors (31 informational/dependency/resource warnings reported in `app/build/reports/lint-results-debug.html`)

## 3. Instrumented UI Tests (`androidTest`)

- Compilation of `BakaiyaUiTest.kt` succeeded via `assembleDebugAndroidTest`.
- On-device execution (`connectedDebugAndroidTest`) was **not run** because `adb` / an Android emulator or physical device is unavailable in this environment.
