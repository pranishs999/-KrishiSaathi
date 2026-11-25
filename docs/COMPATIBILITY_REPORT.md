# Krishi Saathi: Gradle, AGP & Kotlin Compatibility Verification (Sub-phase 1.3)

> **Date:** 2026-11-24  
> **Deliverable:** Verified build configuration (`build.gradle.kts`, `app/build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`).

---

## 1. Compatibility Matrix

| Layer | Configured Version | Verification / Compatibility Notes |
|---|---|---|
| Gradle Wrapper | `9.9.2` (`gradle-9.9.2-bin.zip`) | Compatible with AGP `9.3.3` and configuration cache (`org.gradle.configuration-cache=true`). |
| Android Gradle Plugin (AGP) | `9.3.3` | Uses AGP 9.x DSL (`compileSdk { version = release(37) }`, `release { optimization { enable = false } }`) and AGP 9 built-in Kotlin (`built_in_kotlinc`). |
| Kotlin & Compose Compiler Plugin | `2.2.10` (`org.jetbrains.kotlin.plugin.compose`) | Matches `AGENT_RULES.md` (`Kotlin 2.2.x`). `org.jetbrains.kotlin.android` is intentionally omitted since AGP 9.x provides built-in Kotlin support. |
| KSP | `2.2.10-2.0.2` (`com.google.devtools.ksp`) | Aligned to Kotlin `2.2.10`; verified existing KSP output in `app/build/generated/ksp/debug/java/` (`CropDao_Impl.java`, `KrishiSaathiDatabase_Impl.java`). |
| Room | `2.6.1` (`room-runtime`, `room-ktx`, `room-compiler`) | Wired via `ksp(libs.androidx.room.compiler)` in `app/build.gradle.kts`. |
| Compose BOM | `2026.02.01` | Compatible with Kotlin `2.2.10` Compose compiler plugin. |
| Supabase Kotlin SDK & Ktor | `supabase` `3.1.1`, `ktor` `3.0.3` | Version-catalog managed (`postgrest-kt`, `auth-kt`, `ktor-client-android`). |
| Java Bytecode Compatibility | `JavaVersion.VERSION_11` | Configured in `app/build.gradle.kts` `compileOptions`. |
| Daemon JVM Toolchain | `toolchainVersion=25` | Declared in `gradle/gradle-daemon-jvm.properties`; `gradle.properties` sets `org.gradle.java.home=/home/pranish/Downloads/android-studio/jbr` for the host JDK. |

---

## 2. Build Configuration Checks & Fix Applied

1. **Version Catalog Discipline (`AGENT_RULES.md` Rule 3):**
   - Verified `build.gradle.kts` and `app/build.gradle.kts` reference all plugins and libraries via `libs.*` aliases in `gradle/libs.versions.toml` with no hardcoded dependency versions.
2. **SDK & Build Tools Alignment:**
   - `compileSdk` (`release(37)`), `targetSdk = 37`, and `buildToolsVersion = "36.0.0"` match installed SDK directories (`platforms/android-37.0`, `build-tools/36.0.0`).
   - `minSdk = 26` complies with `AGENT_RULES.md` Rule 4 (Android 8.0+).
3. **Removed Mismatched Unused `ndkVersion`:**
   - Removed `ndkVersion = "30.0.16138531 rc3"` from `app/build.gradle.kts` because the project contains no native C/C++ code and the installed NDK revision on disk is `30.0.16138531-beta3`.
4. **Preserved Host `org.gradle.java.home`:**
   - Kept `org.gradle.java.home=/home/pranish/Downloads/android-studio/jbr` in `gradle.properties` because the host system `/usr/bin/java` provides only OpenJDK 25.0.5 JRE (`javac` missing on `PATH`), while Android Studio's JBR provides the host JDK.
5. **Prior Build Artifacts Inspected:**
   - Confirmed existing build outputs from the configured AGP 9.3.3 / Kotlin 2.2.10 / KSP 2.2.10-2.0.2 toolchain: `app/build/outputs/apk/debug/app-debug.apk`, `app/build/generated/ksp/debug/java/...`, `app/build/generated/source/buildConfig/...`, and `app/build/reports/tests/testDebugUnitTest/index.html` (`ExampleUnitTest`: 1 test, 0 failures).

---

## 3. Known Limitations & Upcoming Build Work

- **Sandboxed Gradle execution:** `./gradlew` cannot execute inside the restricted sandbox because `/home/pranish/.gradle/wrapper/dists/` is read-only and `/home/pranish/Downloads/android-studio/jbr` is outside the sandbox mount. Live Gradle build verification (`compileDebugKotlin` / `testDebugUnitTest`) is performed by the external runner on the host.
- **Future phase dependencies (not added prematurely):** `kotlinx-serialization` (for Supabase DTOs) and `androidx.security:security-crypto` (for Keystore session storage) remain deferred to their respective implementation phases so `libs.versions.toml` stays lean until needed.
