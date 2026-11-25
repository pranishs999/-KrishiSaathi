# Krishi Saathi: Development Environment Verification Report (Sub-phase 1.2)

> **Date:** 2026-11-24  
> **Scope:** Verification of local JDK, Android SDK, Gradle distribution, CLI tools, and toolchain configuration against project requirements.

---

## 1. Summary

| Component | Expected / Configured | Observed on Host / Workspace | Status |
|---|---|---|---|
| Android SDK (`sdk.dir`) | `local.properties` `sdk.dir` | `/home/pranish/Android/Sdk` (present, readable) | Verified |
| Android Platform (API 37) | `compileSdk = 37`, `targetSdk = 37` | `platforms/android-37.0` (`Platform.Version=17`, `ApiLevel=37.0`) | Verified |
| Build Tools | `buildToolsVersion = "36.0.0"` | `build-tools/36.0.0` installed (also `35.0.0`, `37.0.0`) | Verified |
| Platform Tools / ADB | Android SDK `platform-tools` | `adb` 1.0.41 / `37.0.1-15733141`, `sqlite3` `3.50.6` | Verified |
| SDK Licenses | Accepted SDK licenses | `android-sdk-license`, `android-sdk-preview-license` present | Verified |
| NDK | `ndkVersion = "30.0.16138531 rc3"` | `ndk/30.0.16138531` (`Pkg.Revision = 30.0.16138531-beta3`) | Version Mismatch Note |
| System Java (`/usr/bin/java`) | JDK (with `javac`) | OpenJDK `25.0.5` JRE at `/usr/lib/jvm/java-25-openjdk`; `javac` not on `PATH` | Warning (No `javac` on PATH) |
| Configured Gradle JDK | `org.gradle.java.home=/home/pranish/Downloads/android-studio/jbr` | Not accessible inside sandbox (`No such file or directory`); Gradle 9.9.2 failed with `Java home supplied is invalid` | Sandbox / Host-Specific Path |
| Gradle Distribution | `gradle-9.9.2-bin.zip` | Cached in `/home/pranish/.gradle/wrapper/dists/gradle-9.9.2-bin/` | Cached (Wrapper lock blocked in read-only sandbox) |
| Git | Version control CLI | `git version 2.56.1` | Verified |

---

## 2. Detailed Toolchain Findings

### 2.1 JDK & Java Environment
- **Configured in `gradle.properties`:**
  - `org.gradle.java.home=/home/pranish/Downloads/android-studio/jbr`
  - `gradle/gradle-daemon-jvm.properties` specifies `toolchainVersion=25`.
- **Observed on `PATH` / `/usr/lib/jvm`:**
  - `/usr/bin/java` is `openjdk version "25.0.5" 2026-10-20` (Red Hat build `25.0.5+8`).
  - `javac` is not present on `PATH` (`javac: command not found`), and `/usr/lib/jvm` contains `java-25-openjdk` JRE symlinks (`jre`, `jre-25`, `jre-25-openjdk`, `jre-openjdk`).
  - `JAVA_HOME` is unset in the shell environment.
  - `/home/pranish/Downloads/android-studio/jbr` is inaccessible inside the sandboxed execution environment (`No such file or directory`), causing direct Gradle invocation (`gradle --version`) to fail with:
    `Value '/home/pranish/Downloads/android-studio/jbr' given for org.gradle.java.home Gradle property is invalid`.

### 2.2 Android SDK (`local.properties` -> `/home/pranish/Android/Sdk`)
- **Environment variables:** `ANDROID_HOME` and `ANDROID_SDK_ROOT` both point to `/home/pranish/Android/Sdk`.
- **Installed Platforms:** `android-34`, `android-35`, `android-36`, `android-37.0` (`Platform.Version=17`, `ApiLevel=37.0`, `Layoutlib.Api=15`). Matches `compileSdk` (`release(37)`) and `targetSdk` (`37`) in `app/build.gradle.kts`.
- **Installed Build Tools:** `35.0.0`, `36.0.0` (`Pkg.Revision=36.0.0`), `37.0.0`. Matches `buildToolsVersion = "36.0.0"` in `app/build.gradle.kts`.
- **Installed NDKs:** `28.2.13676358` and `30.0.16138531` (`Pkg.Revision = 30.0.16138531-beta3`, `ReleaseName = r30-beta3`).
  - **Finding:** `app/build.gradle.kts` currently declares `ndkVersion = "30.0.16138531 rc3"`, whereas the installed NDK directory reports `beta3` (`30.0.16138531-beta3`). Also, the project has no native C/C++ sources (`externalNativeBuild` is unused). This should be reviewed in **Phase 1.3 (Gradle, AGP and Kotlin compatibility)**.
- **Platform Tools & Utilities:** `platform-tools` (`Pkg.Revision=37.0.1`) includes:
  - `adb`: `Android Debug Bridge version 1.0.41` / `Version 37.0.1-15733141` (requires writable `$HOME/.android` when run; succeeded with `HOME=/tmp`).
  - `sqlite3`: `3.50.6` (available for Phase 2/7 pre-populated Room database inspection).
- **Other SDK directories present:** `cmdline-tools/latest`, `emulator`, `cmake`, `sources`, `licenses` (`android-sdk-license`, `android-sdk-preview-license`).

### 2.3 Gradle Wrapper & Cache
- **Wrapper configuration (`gradle/wrapper/gradle-wrapper.properties`):** `gradle-9.9.2-bin.zip`.
- **Local cache:** `/home/pranish/.gradle/wrapper/dists/gradle-9.9.2-bin/` contains the unpacked `gradle-9.9.2` distribution (`gradle-9.9.2-bin.zip.ok`).
- **Sandbox constraint:** `./gradlew` cannot acquire `/home/pranish/.gradle/wrapper/dists/.../gradle-9.9.2-bin.zip.lck` inside the read-only sandbox filesystem; invoking the cached `gradle` binary directly displays `Welcome to Gradle 9.9.2!` before failing on the `org.gradle.java.home` path check.

---

## 3. Risks & Recommendations for Phase 1.3

1. **Machine-specific `org.gradle.java.home` in `gradle.properties`:** Hardcoding `/home/pranish/Downloads/android-studio/jbr` in tracked `gradle.properties` breaks builds in environments where Android Studio is installed elsewhere or where a standard JDK (`JAVA_HOME` / Gradle JVM Toolchain) is used. Review in Phase 1.3.
2. **Unused/mismatched `ndkVersion`:** `ndkVersion = "30.0.16138531 rc3"` in `app/build.gradle.kts` does not match the installed `30.0.16138531-beta3` revision and is unnecessary unless native C/C++ code is introduced. Review in Phase 1.3.
3. **Host JDK Verification:** Ensure the host / external runner executing Gradle has a full JDK (including `javac`, e.g., Android Studio JBR on the host or OpenJDK devel package) since `/usr/bin/java` in the sandbox provides JRE without `javac`.
