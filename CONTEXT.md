# Krishi Saathi: Business Context & Product Principles

---

## 1. Domain & Business Context

### 1.1 Geographic & Agro-Ecological Focus
**Krishi Saathi** is customized for the **Makwanpur District** in central Nepal (Bagmati Province). Makwanpur features sharp topological and agro-ecological gradients:
- **Inner Terai & Lowland Valleys (300m - 800m):** Hetauda Sub-Metropolitan City, Manahari, and Bakaiya. Characterized by subtropical heat, alluvial riverbed soil, intensive cereal (paddy, maize) cultivation, and large-scale commercial vegetable production.
- **Mid-Hills & High-Hills (800m - 2,500m):** Thaha Municipality (Palung, Daman, Bajrabarahi), Makwanpurgadhi, and Bhimphedi. Characterized by temperate climates, terraced hill farming, high-value off-season vegetable cultivation (cabbage, cauliflower, tomato, radish), potato seed farming, and spice crops (ginger, cardamom).

### 1.2 Target Farmer Profiles & Pain Points
1. **Connectivity Isolation:** Mobile network coverage (NTC/Ncell) drops frequently when moving from highway corridors into rural wards and steep hillside terraces. Farmers cannot rely on live cloud-only apps.
2. **Critical Agro-Chemical Misinformation:** Local agro-vets frequently prescribe high-margin, broad-spectrum chemical pesticides without verifying pest identity or safety intervals. Improper chemical mixing causes crop burning, toxic residues, and environmental degradation.
3. **Unit Confusion:** Official national recommendations are published in metric units (kg/ha), while farmers measure land in **Ropani-Aana-Paisa-Daam** (in the hills) or **Bigha-Katha-Dhur** (in the valleys). Inaccurate mental conversions lead to over- or under-fertilization.
4. **Seasonal Timing Shifts:** Erratic monsoons and temperature shifts disrupt traditional planting calendars, causing seed loss during flash droughts or early frosts.

---

## 2. Core Product Principles

### Principle 1: Offline-First as an Uncompromising Requirement
- **Zero Blocking Network Calls:** The app must launch instantly and provide full access to all agricultural advice, guides, calculators, and saved profiles without waiting for a network handshake.
- **Local Room Database as Single Source of Truth:** All UI screens observe local Room database queries via reactive Kotlin `Flow`.
- **Transparent Background Synchronization:** Remote synchronization with Supabase operates asynchronously in the background. If the device is offline, pending user actions are queued locally, and advisory reads remain 100% available.
- **No Empty "Error 500 / Network Error" Screens:** A farmer in a field terrace must never see an empty screen or an error dialog saying "Check your internet connection" when requesting a crop or pesticide guide.

### Principle 2: Absolute Agricultural Data Integrity (ZERO FAKE FACTS)
- **Zero Hallucination Policy:** Agricultural advice directly impacts human livelihoods, food safety, and environmental health. No AI assistant, developer, or system administrator is permitted to synthesize, guess, or hallucinate agricultural guidelines, chemical active ingredients, dosages, or safety intervals.
- **Authoritative Data Sources Only:** All data entered into Krishi Saathi must be verified against:
  - **Nepal Agricultural Research Council (NARC)** crop cultivation technical packages.
  - **Ministry of Agriculture and Livestock Development (MoALD)** annual recommendations.
  - **Plant Quarantine and Pesticide Management Centre (PQPMC), Hariharbhawan** registered pesticide lists.
  - Department of Agriculture (DoA) official publications.
- **Traceable Verification:** Every chemical entry in the database must feature a verified registration number and boolean flag (`verified_by_expert = true`). If an advisory item is unverified, it is quarantined in draft mode and hidden from farmer devices.

### Principle 3: Safety & Toxicity Transparency
- **Mandatory Color Badging:** All chemical treatments must visually render the standard national toxicity class badge:
  - 🟥 **Red (Extremely Toxic / Class Ia & Ib)**
  - 🟨 **Yellow (Highly Toxic / Class II)**
  - 🟦 **Blue (Moderately Toxic / Class III)**
  - 🟩 **Green (Slightly Toxic / Class IV)**
- **Pre-Harvest Interval (PHI) Enforcement:** The number of days between pesticide application and safe harvest must be prominently displayed in high-contrast red font with clear warnings in Nepali.
- **Non-Chemical First:** Cultural, physical, and biological remedies (e.g., Trichoderma, neem-based formulations, pheromone traps) must always precede chemical interventions in the advisory hierarchy.

### Principle 4: Field-First Accessibility & Ergonomics
- **Visual & Linguistic Clarity:** Designed for users with varying digital literacy. Nepali language terms (`धान`, `मकै`, `काउली`) must accompany technical names.
- **Outdoor Readability:** High-contrast Material 3 color palette capable of remaining legible under bright direct sunlight in open fields.
- **Generous Touch Targets:** Minimum touch target size of 48×48 dp for all clickable elements to accommodate hands working in soil or wearing farm gloves.

---

## 3. MVP Scope (Phase 1: 48-Hour Execution)

To ensure a hardened, rock-solid release within the strict 48-hour timeline, Krishi Saathi restricts user roles and feature boundaries.

### 3.1 Strict User Roles (2 Roles Only)

```
                     +---------------------------------------+
                     |           Krishi Saathi Roles         |
                     +---------------------------------------+
                                    /         \
                                   /           \
                                  v             v
                    +-------------------+   +--------------------+
                    |   1. Farmer Role  |   |   2. Admin Role    |
                    |    (End User)     |   | (Agro Authority)   |
                    +-------------------+   +--------------------+
```

#### 1. Farmer Role (Client App User)
- **Authentication:** Phone number + SMS OTP via Supabase Auth (with offline bypass fallback for emergency local profile creation).
- **Profile:** Ward and Municipality selection in Makwanpur, selection of cultivated primary crops.
- **Read Access:** Full offline read access to all verified crops, crop stages, seasonal advisories, pest/disease guides, and pesticide/fertilizer charts.
- **Calculators:** Interactive land unit conversion and customized fertilizer/pesticide dosage calculation.
- **Permissions:** Strict read-only access to published agricultural tables; create/update permission on their own profile row.

#### 2. Admin Role (Agricultural Authority / Specialist)
- **Authentication:** Elevated role authenticated via Supabase Auth email/password or secure administrative credential.
- **Data Management:** Full CRUD capabilities over `crops`, `crop_guides`, `pest_diseases`, `fertilizer_guides`, `pesticide_guides`, and `seasonal_recommendations`.
- **Quality Control:** Responsible for marking `verified_by_expert = true` and `status = 'published'`.
- **Advisory Broadcasts:** Authority to push high-priority emergency alerts (e.g., locust/armyworm invasion notices).

### 3.2 Explicitly Out of Scope for Phase 1 MVP
- ❌ **Commercial Marketplace / E-Commerce:** No buying/selling of seeds, produce, or chemicals; no cart or shopping checkout.
- ❌ **Payment Gateway Integration:** No eSewa, Khalti, or bank integration.
- ❌ **Live Peer-to-Peer Chat / Social Forums:** No unmoderated social messaging feeds.
- ❌ **Real-Time IoT / Drone Hardware Streaming:** No direct hardware integration.
- ❌ **Cloud-Heavy Deep Learning On-Device Inference:** No multi-gigabyte neural models bundled into the APK; pest recognition in Phase 1 relies on guided visual symptom trees and photographic references.

---

## 4. Data Quality & Compliance Rules

1. **Pre-Bundled Seed Assets:** The Android APK must ship with an initial, certified Room SQLite pre-populated database containing baseline Makwanpur crop guides. A farmer installing the app with zero internet can immediately access the entire agricultural library.
2. **Conflict Resolution Policy:**
   - **Agricultural Knowledge Base:** Remote Server Wins. Verified agronomic corrections published by admins overwrite local records during sync.
   - **Farmer Profile & Calculation State:** Local Client Wins. Farmer details and personal preferences persist locally and sync upward when authenticated.
3. **Zero Deprecated Dependencies:** No bloated or unmaintained libraries. Standard Android Jetpack libraries and official Supabase Kotlin SDK only.

