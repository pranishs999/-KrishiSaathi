# Krishi Saathi: 48-Hour MVP Task Tracking Checklist

---

## 📊 Summary Progress Dashboard

- **Current Phase:** Phase 1 (Foundation & Documentation Setup)
- **Target Timeline:** 48 Hours Total
- **Completed Phases:** 0 / 8
- **Total Tasks:** 40 Sub-Tasks

---

## 🛠️ Phase 1: Foundation, Tooling & Documentation Setup (Hours 00–04)
*Goal: Establish architectural blueprints, linting rules, dependency catalogs, and project structure.*

- [x] **Task 1.1:** Create complete project documentation suite in project root:
  - [x] `README.md` (Project overview, audience, stack)
  - [x] `CONTEXT.md` (Business context, offline principles, 2 roles, zero fake facts)
  - [x] `PLAN.md` (48-hour roadmap, phases 1–8)
  - [x] `ARCHITECTURE.md` (MVVM, UDF, Repository, Room SSOT, Supabase)
  - [x] `DATABASE.md` (PostgreSQL DDL, Room entities, TypeConverters)
  - [x] `SECURITY.md` (Supabase RLS policies, Keystore, OTP rules, PQPMC verification)
  - [x] `TASKS.md` (Tracking checklist across all phases)
  - [x] `AGENT_RULES.md` (AI guidelines, offline-first, lean dependencies, device testing)
- [ ] **Task 1.2:** Update `gradle/libs.versions.toml` with certified dependencies:
  - Room (`androidx.room:room-runtime`, `androidx.room:room-ktx`, `androidx.room:room-compiler`)
  - Supabase Kotlin SDK (`io.github.jan-tennert.supabase:postgrest-kt`, `gotrue-kt`)
  - Kotlinx Serialization & Coroutines
  - Security Crypto (`androidx.security:security-crypto`)
  - Compose Navigation & Icons Extended
- [ ] **Task 1.3:** Configure clean package structure under `com.krishisaathi.app`:
  - `core/`, `data/`, `domain/`, `ui/`
- [ ] **Task 1.4:** Verify baseline clean build (`./gradlew compileDebugKotlin`).

---

## 🗄️ Phase 2: Room Database & Seed Dataset Formulation (Hours 04–08)
*Goal: Build local Room storage with pre-packaged SQLite seed database covering Makwanpur crops.*

- [ ] **Task 2.1:** Implement Room Entities matching `DATABASE.md`:
  - `ProfileEntity`, `CropEntity`, `CropGuideEntity`, `SeasonalRecommendationEntity`, `PestDiseaseEntity`, `FertilizerGuideEntity`, `PesticideGuideEntity`.
- [ ] **Task 2.2:** Implement Room DAOs with reactive `Flow` query methods:
  - `CropDao` (search, category filtering, crop detail with stages)
  - `PestDiseaseDao` (symptoms, remedies, crop association)
  - `AdvisoryDao` (seasonal calendar queries, fertilizer recommendations)
  - `ProfileDao` (local profile persistence)
- [ ] **Task 2.3:** Implement Room `TypeConverters` for `List<String>` and timestamps.
- [ ] **Task 2.4:** Build seed SQLite database (`database/krishi_saathi_seed.db`) with verified NARC data for 7 Makwanpur crops (Paddy, Maize, Cauliflower, Cabbage, Tomato, Mustard, Ginger).
- [ ] **Task 2.5:** Configure Room Database builder with `.createFromAsset()` pre-population.
- [ ] **Task 2.6:** Write Unit Tests for Room DAOs to verify query execution and offline asset loading.

---

## ☁️ Phase 3: Supabase Cloud Backend & RLS Setup (Hours 08–14)
*Goal: Deploy PostgreSQL schema to Supabase, enforce strict RLS policies, and configure Auth.*

- [ ] **Task 3.1:** Execute PostgreSQL DDL scripts from `DATABASE.md` on Supabase instance.
- [ ] **Task 3.2:** Apply Row Level Security (RLS) policies:
  - Enable RLS on all 7 tables.
  - Implement read-only public access to verified advisory tables.
  - Implement strict user-isolated read/write on `profiles` (`auth.uid() = id`).
  - Implement admin-only write permissions for agricultural tables.
- [ ] **Task 3.3:** Populate remote PostgreSQL database with certified Makwanpur seed records.
- [ ] **Task 3.4:** Configure Supabase Phone Authentication (SMS OTP provider settings).
- [ ] **Task 3.5:** Configure Supabase Kotlin Client singleton in `com.krishisaathi.app.core.network`.
- [ ] **Task 3.6:** Test RLS boundaries using test anon and authenticated tokens.

---

## 🔄 Phase 4: Offline-First Repository & Sync Engine (Hours 14–20)
*Goal: Abstract data sources behind repositories with Room as the single source of truth.*

- [ ] **Task 4.1:** Define Domain Repository interfaces (`CropRepository`, `PestRepository`, `AdvisoryRepository`, `ProfileRepository`, `AuthRepository`).
- [ ] **Task 4.2:** Implement `NetworkObserver` using Android `ConnectivityManager` to track online/offline transitions.
- [ ] **Task 4.3:** Implement `CropRepositoryImpl` with offline-first Flow emission:
  - Immediately emit cached Room data.
  - Trigger background fetch from Supabase if online.
  - Upsert remote changes into Room.
- [ ] **Task 4.4:** Implement `PestRepositoryImpl` and `AdvisoryRepositoryImpl` following the same offline pattern.
- [ ] **Task 4.5:** Implement Conflict Resolution logic (Advisory: Server Wins; Profile: Local Wins).
- [ ] **Task 4.6:** Unit test repository behavior with simulated network dropouts.

---

## 👤 Phase 5: Authentication (Phone OTP) & Farmer Onboarding UI (Hours 20–28)
*Goal: Deliver low-friction phone login and localized Makwanpur onboarding.*

- [ ] **Task 5.1:** Implement `EncryptedSharedPreferences` wrapper via Android Keystore for secure token storage.
- [ ] **Task 5.2:** Build **Phone Login Screen** in Jetpack Compose:
  - Nepali phone number validator (`+977-98/97XXXXXXXX`).
  - 6-digit OTP code input fields with auto-focus.
  - 60-second resend countdown timer.
- [ ] **Task 5.3:** Build **Offline Fallback Option** ("Skip login / Continue as Guest").
- [ ] **Task 5.4:** Build **Farmer Profile Setup Screen**:
  - Full Name input.
  - Makwanpur Municipality dropdown (Hetauda, Thaha, Manahari, Bakaiya, etc.) and Ward number.
  - Cultivated primary crops selector.
  - Land unit preference selector (Ropani vs. Bigha).
- [ ] **Task 5.5:** Connect `AuthViewModel` with `ProfileRepository` and verify state restoration on app launch.

---

## 📖 Phase 6: Core Knowledge Base & Advisory UI (Hours 28–36)
*Goal: Provide instant, responsive browsing of agricultural guides and pest diagnosis.*

- [ ] **Task 6.1:** Build **Navigation Scaffold** (Bottom Navigation: Crops, Pests, Calendar, Calculators, Profile).
- [ ] **Task 6.2:** Build **Crop Catalog Screen**:
  - Search bar supporting Nepali and English crop names.
  - Category filter chips (Cereals, Vegetables, Spices, Cash Crops).
  - Crop card grid with WebP thumbnails and growth duration badges.
- [ ] **Task 6.3:** Build **Crop Detail & Growth Stage Screen**:
  - Interactive growth stage timeline (Land Prep, Sowing, Weeding, Irrigation, Harvest).
  - Agronomic guidance, water management, and labor instructions.
- [ ] **Task 6.4:** Build **Pest & Disease Diagnostic Screen**:
  - Visual symptom cards with photographic references.
  - Biological and cultural control remedies placed before chemical interventions.
- [ ] **Task 6.5:** Build **Seasonal Crop Calendar Screen**:
  - Tabbed view: Mid-Hills (Thaha/Daman) vs. Valley Lowlands (Hetauda/Manahari).
  - Planting and harvesting windows displayed in Nepali Bikram Sambat months.

---

## 🧮 Phase 7: Land Unit Converter & Agrochemical Safety Calculators (Hours 36–42)
*Goal: Provide exact mathematical calculators with strict safety badging.*

- [ ] **Task 7.1:** Implement pure Kotlin domain engines:
  - `LandUnitConverter` (Ropani/Aana/Paisa/Daam <-> Bigha/Katha/Dhur <-> $m^2$).
  - `FertilizerCalculatorEngine` (Urea, DAP, MOP, Farmyard manure based on land parcel size).
  - `PesticideDilutionEngine` (Chemical volume/mass per 16L knapsack tank).
- [ ] **Task 7.2:** Build **Land Converter Screen** in Compose.
- [ ] **Task 7.3:** Build **Fertilizer Calculator Screen**:
  - Crop picker, land input, basal vs. split top-dressing guidance.
- [ ] **Task 7.4:** Build **Pesticide Safety Advisory Screen**:
  - Knapsack sprayer dosage breakdown.
  - High-visibility toxicity class badges (🟥 Red, 🟨 Yellow, 🟦 Blue, 🟩 Green).
  - Prominent Pre-Harvest Interval (PHI) count badge with harvest safety warnings.
  - Personal Protective Equipment (PPE) checklist.
- [ ] **Task 7.5:** Write Unit Tests validating mathematical conversion and dosage calculations.

---

## 🚀 Phase 8: Hardening, Physical Device Verification & Release Packaging (Hours 42–48)
*Goal: Verify performance under field conditions, stress test offline modes, and generate release build.*

- [ ] **Task 8.1:** **Airplane Mode Verification:** Enable Airplane mode on physical device; verify that all crop guides, calculators, and calendars open with zero latency.
- [ ] **Task 8.2:** **Network Throttling Test:** Verify app behavior under simulated 2G/EDGE network conditions; ensure zero UI freezing or ANRs.
- [ ] **Task 8.3:** **Outdoor Ergonomics & Accessibility Audit:**
  - Verify high-contrast visibility under direct sunlight.
  - Ensure all touch targets meet minimum 48×48 dp standard.
- [ ] **Task 8.4:** **Memory & Performance Audit:**
  - Verify app runs stably under 150MB active PSS on low-end test device.
  - Check for Compose recomposition loops and memory leaks.
- [ ] **Task 8.5:** **Security Verification:** Confirm RLS blocks unauthorized writes to agricultural tables.
- [ ] **Task 8.6:** Generate signed Release APK/AAB (`./gradlew assembleRelease`).

