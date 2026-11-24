# Krishi Saathi: AI Agent Working Guidelines & Engineering Rules

---

## 1. Prime Directive

You are working on **Krishi Saathi**, an offline-first agricultural mobile application for smallholder and commercial farmers in Makwanpur District, Nepal. 

Your code, architecture, and data management directly affect the livelihoods, crop yields, and personal safety of rural farmers. You must adhere to the following **mandatory engineering rules** at all times.

---

## 2. Mandatory Rules

### Rule 1: Offline-First Integrity (Room SSOT)
- **Offline is the default state, NOT an error state:** A farmer in a remote hill terrace with no cellular reception must be able to launch the app, browse crop stages, view pest symptoms, and calculate fertilizer dosages with zero lag and zero network errors.
- **Single Source of Truth:** All UI screens must observe local Room database queries via reactive Kotlin `Flow`. Never bypass Room to bind directly to a remote network response.
- **Zero Blocking Network Calls:** Never make synchronous or blocking network calls on the main thread or during screen initialization. Remote synchronization must run asynchronously in the background.

### Rule 2: Zero Agricultural Hallucination (Strict Data Quality)
- **No Synthesized Agrochemical Facts:** Never invent, hallucinate, estimate, or guess crop growth duration, chemical dosages, active ingredients, or Pre-Harvest Intervals (PHI).
- **Authoritative Data Sources Only:** All agronomic datasets must strictly conform to published materials from:
  - **Nepal Agricultural Research Council (NARC)**
  - **Ministry of Agriculture and Livestock Development (MoALD)**
  - **Plant Quarantine and Pesticide Management Centre (PQPMC)**
- **Expert Verification Required:** Every chemical record must include a verified registration number and have `verified_by_expert = true`. Unverified chemicals must never be displayed to farmers.

### Rule 3: Dependency Discipline (Lean Architecture)
- **No Bloated Third-Party Libraries:** Do not add heavy, unnecessary third-party libraries (e.g., avoid legacy RxJava, avoid bloated image-loading wrappers, avoid bulky logging frameworks).
- **Standard Tooling Only:**
  - Language: Modern Kotlin 2.2.x + Coroutines + Flow.
  - UI: Jetpack Compose + Material 3.
  - Storage: Android Room + SQLite.
  - Backend: Official Supabase Kotlin SDK + Ktor/Kotlinx Serialization.
  - Security: Android Keystore + Jetpack Security Crypto.
- **Version Catalog Enforcement:** All dependencies must be added to `gradle/libs.versions.toml`. Never hardcode library versions directly in `build.gradle.kts`.

### Rule 4: Physical Device & Field-Condition Testing
- **Physical Device Target:** Always optimize for physical Android devices running on low-resource hardware:
  - Minimum SDK: Android 8.0 (API Level 26).
  - Target RAM profile: Smooth operation on budget phones with 2GB to 3GB RAM (keep active PSS under 150MB).
- **Airplane Mode Test Gate:** Before declaring any UI screen or feature complete, it must be verified with **Airplane Mode enabled** (all cellular, Wi-Fi, and Bluetooth radios disabled).
- **Outdoor Field Ergonomics:**
  - High-contrast color palette readable under bright, direct sunlight.
  - Touch targets must be at least **48×48 dp** to support use by farmers with soiled hands or gloves.

### Rule 5: Bilingual Support & Touch Ergonomics
- **Bilingual Strings:** Maintain support for both Nepali (नेपाली) and English in UI strings.
- **No String Hardcoding:** Extract user-facing text to `res/values/strings.xml` and provide `res/values-ne/strings.xml` for Nepali localization.
- **Traditional Land Units:** Always support both **Ropani-Aana-Paisa-Daam** (Hills) and **Bigha-Katha-Dhur** (Terai) in addition to metric units.

### Rule 6: Code Quality, Verification & Commit Hygiene
- **Zero Compilation Regressions:** Ensure the project compiles cleanly after every step:
  ```bash
  ./gradlew compileDebugKotlin
  ```
- **Run Unit Tests:** Run test suites after modifying logic:
  ```bash
  ./gradlew testDebugUnitTest
  ```
- **Do Not Modify Unrelated Files:** Do not alter build scripts, Gradle configs, or Kotlin source files unless explicitly instructed by the current task.

