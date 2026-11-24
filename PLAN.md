# Krishi Saathi: 48-Hour Compressed MVP Development Plan

---

## ⏱️ Executive Roadmap Overview

This roadmap defines the compressed, disciplined 48-hour development schedule to bring **Krishi Saathi** from empty Android scaffolding to a hardened, offline-ready MVP tested on physical devices.

```
+-----------------------------------------------------------------------------------------------+
|                                48-HOUR DEVELOPMENT TIMELINE                                  |
+-----------------------------------------------------------------------------------------------+
| [00-04h] Phase 1: Foundation, Tooling & Documentation Setup                                   |
| [04-08h] Phase 2: Room Database Architecture & Seed Dataset Formulation                      |
| [08-14h] Phase 3: Supabase Cloud Infrastructure, PostgreSQL Schema & RLS Setup                 |
| [14-20h] Phase 4: Offline-First Repository Pattern & Background Sync Engine                   |
| [20-28h] Phase 5: Authentication (Phone OTP) & Farmer Onboarding UI                           |
| [28-36h] Phase 6: Core Agricultural Knowledge Base UI (Crops, Pests, Seasonal)                |
| [36-42h] Phase 7: Land Unit Converter & Agrochemical Safety Calculators                       |
| [42-48h] Phase 8: Hardening, Physical Device Verification & Release Packaging                  |
+-----------------------------------------------------------------------------------------------+
```

---

## 📅 Detailed Phase Breakdown

### Phase 1: Foundation, Tooling & Documentation Setup
- **Timeline:** Hours 00:00 – 04:00 (4 Hours)
- **Primary Objectives:**
  - Establish complete project documentation and engineering standards.
  - Configure Gradle dependency catalog (`gradle/libs.versions.toml`) with verified AndroidX, Room, Compose, and Supabase dependencies.
  - Set up package structure following clean MVVM architecture (`com.krishisaathi.app.*`).
  - Configure Android linting, git hooks, and build variants.
- **Key Deliverables:**
  - Foundational documentation files (`README.md`, `CONTEXT.md`, `PLAN.md`, `ARCHITECTURE.md`, `DATABASE.md`, `SECURITY.md`, `TASKS.md`, `AGENT_RULES.md`).
  - Baseline project compilation verified (`./gradlew compileDebugKotlin` passing).
  - Version catalog populated with tested library versions.

### Phase 2: Room Database Architecture & Seed Dataset Formulation
- **Timeline:** Hours 04:00 – 08:00 (4 Hours)
- **Primary Objectives:**
  - Define local Room entities matching the core data schema: `ProfileEntity`, `CropEntity`, `CropGuideEntity`, `SeasonalRecommendationEntity`, `PestDiseaseEntity`, `FertilizerGuideEntity`, `PesticideGuideEntity`.
  - Implement Room DAOs with reactive `kotlinx.coroutines.flow.Flow` return types.
  - Formulate verified seed database (`krishi_saathi_seed.db` or pre-packaged JSON seed assets) covering Makwanpur's priority crops (Paddy, Maize, Cauliflower, Cabbage, Off-season Tomato, Ginger) and prevalent pests (Fall Armyworm, Late Blight, Aphids).
  - Configure Room database builder with `.createFromAsset("database/krishi_saathi_seed.db")`.
- **Key Deliverables:**
  - Room database classes, DAOs, and TypeConverters implemented and compiled.
  - Unit tests verifying DAO CRUD operations and asset pre-population.

### Phase 3: Supabase Cloud Infrastructure, PostgreSQL Schema & RLS Setup
- **Timeline:** Hours 08:00 – 14:00 (6 Hours)
- **Primary Objectives:**
  - Create Supabase project tables with PostgreSQL 15+ DDL scripts matching Room entities.
  - Configure strict Row Level Security (RLS) policies:
    - Public read for verified published agricultural advisory tables.
    - User-bound read/write for farmer profiles (`auth.uid() = id`).
    - Admin-only write/update/delete on advisory tables.
  - Set up Phone Authentication provider in Supabase Auth (SMS OTP).
  - Seed remote PostgreSQL database with verified NARC/MoALD agricultural records.
  - Write SQL migration scripts and verify via Supabase CLI or SQL Editor.
- **Key Deliverables:**
  - Cloud database fully operational with active RLS policies.
  - Supabase client integration initialized in Android app.

### Phase 4: Offline-First Repository Pattern & Background Sync Engine
- **Timeline:** Hours 14:00 – 20:00 (6 Hours)
- **Primary Objectives:**
  - Implement Repository interfaces and implementations: `CropRepository`, `PestRepository`, `AdvisoryRepository`, `ProfileRepository`, `AuthRepository`.
  - Design the `NetworkBoundResource` strategy: always emit from Room first; fetch updates from Supabase asynchronously if network is available; upsert into Room; let Room push updates to observers.
  - Implement network connectivity monitor (`NetworkObserver`) via `ConnectivityManager`.
  - Configure local conflict resolution policy (Remote Advisory Wins / Local Profile Wins).
- **Key Deliverables:**
  - Repositories exposing reactive StateFlows.
  - Automated tests validating offline query execution with zero network latency.

### Phase 5: Authentication (Phone OTP) & Farmer Onboarding UI
- **Timeline:** Hours 20:00 – 28:00 (8 Hours)
- **Primary Objectives:**
  - Implement Android Keystore-backed `EncryptedSharedPreferences` for secure session token persistence.
  - Build Jetpack Compose Phone Authentication screen (mobile number input, 6-digit OTP verification, retry timer).
  - Build Farmer Onboarding screen:
    - Name entry.
    - Makwanpur Municipality & Ward selector (Hetauda, Thaha, Manahari, etc.).
    - Primary crop multi-selector.
  - Provide an "Offline Guest / Skip for Now" path allowing immediate advisory browsing without mandatory initial cellular network access.
- **Key Deliverables:**
  - Polished, responsive onboarding and login screens in Compose.
  - Session management working across app restarts.

### Phase 6: Core Agricultural Knowledge Base UI
- **Timeline:** Hours 28:00 – 36:00 (8 Hours)
- **Primary Objectives:**
  - Build **Crop Library Screen**: searchable grid/list with bilingual filtering (Nepali/English), growth duration, and seasonal tags.
  - Build **Crop Detail & Growth Stage Screen**: interactive timeline showing stages (Land Prep, Sowing, Weeding, Irrigation, Harvest) with cultural guidelines.
  - Build **Pest & Disease Diagnostic Screen**: visual symptom cards, pest filtering by crop, non-chemical and biological remedies highlighted first.
  - Build **Seasonal Makwanpur Crop Calendar**: tabular/timeline view displaying planting and harvesting windows tailored for mid-hills vs. valley areas.
- **Key Deliverables:**
  - Complete browsing and diagnostic UI operational with smooth Compose animations.
  - Instantaneous screen transitions powered by local Room cache.

### Phase 7: Land Unit Converter & Agrochemical Safety Calculators
- **Timeline:** Hours 36:00 – 42:00 (6 Hours)
- **Primary Objectives:**
  - Implement Domain Calculation Engines:
    - `LandUnitConverter`: Accurate mathematical mapping between Ropani-Aana-Paisa-Daam, Bigha-Katha-Dhur, square meters ($m^2$), and hectares ($ha$).
    - `FertilizerCalculatorEngine`: Calculates required Urea, DAP, MOP, and compost for a user-specified land parcel.
    - `PesticideDilutionCalculator`: Calculates required chemical milliliters/grams per standard 16-liter knapsack sprayer tank.
  - Implement Compose UI for Calculators with high-visibility safety warnings:
    - Prominent Pre-Harvest Interval (PHI) count badge.
    - Toxicity color badges (Red/Yellow/Blue/Green).
    - Mandatory Personal Protective Equipment (PPE) checklists.
- **Key Deliverables:**
  - Unit tests verifying 100% calculation accuracy against NARC standards.
  - Interactive, easy-to-use calculator screens in Compose.

### Phase 8: Hardening, Physical Device Verification & Release Packaging
- **Timeline:** Hours 42:00 – 48:00 (6 Hours)
- **Primary Objectives:**
  - **Airplane Mode Stress Test:** Verify 100% feature availability with all radios disabled.
  - **Network Throttling Test:** Verify app behavior under simulated 2G/EDGE (high latency, packet loss).
  - **Physical Device Field Verification:** Test on physical Android devices (API 26+) for touch target ergonomics (>= 48dp), font readability under direct sunlight, and low-RAM memory usage (<150MB active PSS).
  - **Security & RLS Audit:** Verify that unauthorized clients cannot write to advisory tables.
  - Assemble signed release APK / bundle (`./gradlew assembleRelease`).
- **Key Deliverables:**
  - Zero critical crashes or unhandled exceptions.
  - Production-ready APK artifact generated.

---

## 🛡️ Risk Management & Mitigation Matrix

| Risk Factor | Probability | Impact | Mitigation Strategy |
|---|---|---|---|
| **Rural Network Dropout during Onboarding** | High | High | Allow full guest access with local Room profile; sync account in background when connected. |
| **Pesticide Advisory Inaccuracy** | Low | Critical | Enforce strict `verified_by_expert` boolean and double-audit seed database against MoALD records. |
| **Complex Math Errors in Land Conversion** | Medium | High | Pure Kotlin unit test suite with parametrized test vectors covering all traditional Nepali land units. |
| **Room Migration Failures during App Update** | Low | High | Use destructive fallback for MVP seed database or strict, tested incremental migrations. |
| **UI Lag on Low-End Devices (2GB RAM)** | Medium | Medium | Avoid large raster images; use compressed WebP assets; keep Compose recomposition scopes narrow. |

---

## 🏁 48-Hour Milestone Checklist

- [ ] **Milestone 1 (Hour 04):** Documentation complete, Gradle configured, build passing.
- [ ] **Milestone 2 (Hour 08):** Room entities, DAOs, and Makwanpur seed database built.
- [ ] **Milestone 3 (Hour 14):** Supabase tables created with verified RLS policies.
- [ ] **Milestone 4 (Hour 20):** Repository pattern and background sync layer functioning.
- [ ] **Milestone 5 (Hour 28):** Phone OTP login and farmer onboarding screen completed.
- [ ] **Milestone 6 (Hour 36):** Crop library, growth stages, pest catalog, and seasonal calendar live.
- [ ] **Milestone 7 (Hour 42):** Land converter, NPK calculator, and pesticide safety warnings active.
- [ ] **Milestone 8 (Hour 48):** Physical device verification in Airplane mode passed; Release APK built.

