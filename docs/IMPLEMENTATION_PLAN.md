# Krishi Saathi: 24-Hour Phased Implementation Schedule

**Project:** Krishi Saathi (कृषि साथी)  
**Timeline Constraint:** 24-Hour Functional Prototype Target  
**Execution Strategy:** Prioritize end-to-end working features over secondary decorations.

---

## Phase Breakdown & Milestones

### Phase 1: Inspection & Documentation Foundation (Hours 00–02)
- [x] Workspace and tooling audit (JDK 25, Gradle 9.9.2, Android SDK 37, Python 3.14).
- [x] Test direct Supabase PostgreSQL connectivity via psycopg2.
- [x] Create core specifications (`REQUIREMENTS.md`, `INTERVIEW_RESULTS.md`, `ARCHITECTURE.md`, `IMPLEMENTATION_PLAN.md`, `KNOWN_LIMITATIONS.md`).
- [ ] Establish directory structure for `admin/` (Django) and clean modular packages for `app/`.

### Phase 2: Database Schema & Backend Foundation (Hours 02–06)
- [ ] Author canonical PostgreSQL schema SQL migration in `supabase/migrations/`:
  - `farmer_profiles`, `farms`, `farm_plots`
  - `crop_categories`, `crops`, `seasons`, `topics`, `regions`
  - `guides`, `guide_steps`, `guide_topics`, `guide_regions`
  - `content_sync_metadata`
- [ ] Apply migration directly to Supabase PostgreSQL.
- [ ] Configure Row Level Security (RLS) policies for farmer isolation and public advisory reads.
- [ ] Seed canonical Nepali agricultural records (Paddy, Maize, Cauliflower, Cabbage, Tomato, Mustard, Ginger) and 8-stage step-by-step guides.

### Phase 3: Django Web Administration Portal (Hours 06–10)
- [ ] Initialize Django project in `admin/` directory.
- [ ] Configure Django `settings.py` connecting directly to Supabase PostgreSQL via `psycopg2`.
- [ ] Configure Tailwind CSS styling (NO Bootstrap).
- [ ] Implement Django authentication for Super Admin and Content Admin roles.
- [ ] Create Django models mapping existing Supabase tables (`managed = False`).
- [ ] Build administrative UI:
  - Admin login & session management
  - Dashboard overview (crop count, guide count, farmer count)
  - Crop & Category CRUD
  - Step-by-step Cultivation Guide authoring & publishing toggle
  - Basic admin activity logging
- [ ] Verify content published in Django appears immediately in PostgreSQL.

### Phase 4: Android Foundation & Data Layer (Hours 10–14)
- [ ] Update Room database schema in `app/`:
  - Entities for `CropEntity`, `CategoryEntity`, `GuideEntity`, `FarmEntity`, `PlotEntity`, `ProfileEntity`.
  - DAOs with reactive Flow queries (`CropDao`, `GuideDao`, `FarmDao`, `ProfileDao`).
- [ ] Expand `AppContainer` with repository singletons.
- [ ] Implement Repository layer (`CropRepository`, `GuideRepository`, `FarmRepository`, `AuthRepository`).
- [ ] Configure Supabase PostgREST client queries for delta fetching.

### Phase 5: Core Farmer Functionality & Screens (Hours 14–19)
- [ ] **Authentication Flow:**
  - Phone number input (`+977-98/97XXXXXXXX`)
  - Development OTP verification (testing code `123456`)
  - 4-digit MPIN creation and hashing
  - Phone + MPIN login
  - Multi-account switching on shared device
  - Persistent Keystore token storage
- [ ] **Farmer Profile & Farm Management:**
  - Profile screen with GPS and manual municipality selection
  - Farm listing, creation, and plot assignment (online-enabled)
  - Explicit land unit handling (Ropani, Bigha, etc.)
- [ ] **Crop Catalogue & Search:**
  - Searchable crop list with category filter chips
  - High-speed Nepali Unicode text search
  - Crop detail view with botanical info, seasonality, and stages
- [ ] **Cultivation Guides:**
  - 8-stage cultivation guide viewer (Land Prep -> Harvesting)
  - Topic, season, and region filters

### Phase 6: Offline Read-Only Synchronization (Hours 19–21)
- [ ] WorkManager background sync routine pulling deltas into Room.
- [ ] Network status detection via `ConnectivityManager`.
- [ ] Offline status banner / indicator in Compose UI.
- [ ] Verify strict offline read-only rule: mutations block gracefully when disconnected.

### Phase 7: Verification, Build & Final Delivery (Hours 21–24)
- [ ] Execute Android unit tests (`./gradlew testDebugUnitTest`).
- [ ] Verify clean debug APK generation (`./gradlew assembleDebug`).
- [ ] Test Django admin server and CRUD workflows.
- [ ] Verify cross-system integration: Django publishes guide -> Android downloads & reads offline.
- [ ] Assemble final implementation report.

