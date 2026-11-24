# Krishi Saathi (कृषि साथी)
*Offline-First Android Agricultural Companion for Makwanpur Farmers*

---

## 🌾 Project Overview

**Krishi Saathi** ("Farmer's Companion") is an offline-first native Android mobile application designed to empower smallholder and commercial farmers in the **Makwanpur District (Hetauda and surrounding rural municipalities), Bagmati Province, Nepal**. 

In rural Nepal, farmers encounter critical agricultural challenges: erratic cellular connectivity in hill terrains, counterfeit or inappropriately mixed agro-chemicals, untracked pest infestations (such as Fall Armyworm and Tomato Late Blight), and outdated planting schedules disrupted by microclimate variations. Krishi Saathi bridges this information gap by providing a high-reliability, zero-latency advisory directly on the farmer's smartphone.

All core agronomic guides, seasonal calendars, pest/disease diagnostic matrices, and agrochemical dosage calculations operate **100% offline** using local cached data, with seamless background synchronization to a remote **Supabase** backend whenever internet connectivity is restored.

---

## 🎯 Target Audience & Local Context

- **Primary Users:** Smallholder and commercial farmers operating in:
  - **Subtropical Lowland / Inner-Terai Valleys:** Hetauda Sub-Metropolitan City, Manahari Rural Municipality, Bakaiya Rural Municipality.
  - **Temperate Mid-Hills:** Thaha Municipality (Daman, Palung), Makwanpurgadhi Rural Municipality, Bhimphedi Rural Municipality.
- **Key Regional Crops Supported:**
  - *Staple Cereals:* Paddy (धान - Radha-4, Sabitri, Chaite), Maize (मकै - Rampur Composite, Arun-2), Wheat (गहुँ).
  - *Cash Crops & Vegetables:* Off-season Tomato (गोलभेडा), Cauliflower (काउली), Cabbage (बन्दा), Mustard (तोरी), Ginger (अदुवा), Large Cardamom (अलैंची).
- **Secondary Users:**
  - Local Agro-Vet proprietors seeking standardized pesticide registration tables.
  - Junior Technical Assistants (JTAs) and Agricultural Extension Officers conducting field audits.
  - System Administrators managing verified regional advisory content.

---

## 📱 Technology Stack

Krishi Saathi uses a modern, battle-tested, lightweight native Android tech stack configured to ensure high performance on low-resource hardware:

| Layer | Technology | Specification / Details |
|---|---|---|
| **Language** | Kotlin | v2.2.x (Modern idiomatic Kotlin, Coroutines, Flow) |
| **Minimum SDK** | Android 8.0 (API Level 26) | Ensures compatibility with ~95%+ active Android devices in rural Nepal |
| **Target SDK** | Android 15 / 16 (API Level 37) | Latest platform standards, modern permissions, edge-to-edge layout |
| **UI Framework** | Jetpack Compose + Material 3 | Declarative UI, dynamic typography, outdoor high-contrast themes |
| **Architecture** | MVVM + Repository Pattern | Clean separation of concerns, unidirectional data flow (UDF) |
| **Local Storage** | Android Room Database | SQLite-based offline cache with pre-packaged agricultural seed database |
| **Secure Storage** | Android Keystore & EncryptedSharedPreferences | Secure token handling for authentication and user preferences |
| **Backend as a Service** | Supabase (PostgreSQL 15+) | PostgREST API, Row Level Security (RLS), Supabase Auth (Phone OTP) |
| **Network & Serialization** | Ktor Client / Kotlinx Serialization | Lightweight HTTP client engine without heavy reflection overhead |
| **Build System** | Gradle Kotlin DSL (`build.gradle.kts`) | Version catalogs (`gradle/libs.versions.toml`) |

---

## 🌟 Key Features (MVP Phase 1)

1. **Offline Agronomic Library:** Detailed cultivation stages (land preparation, seed treatment, irrigation, harvesting) for Makwanpur's primary crops.
2. **Pest & Disease Diagnostic Guide:** Visual symptom identification, preventive cultural controls, and biological remedies.
3. **Verified Pesticide & Fertilizer Calculator:** 
   - Instant calculation of chemical and organic inputs tailored to traditional Nepali land units (**Ropani-Aana-Paisa-Daam** and **Bigha-Katha-Dhur**).
   - Strict Pre-Harvest Interval (PHI) indicators and WHO Toxicity Classifications (Red, Yellow, Blue, Green badges).
4. **Seasonal Makwanpur Crop Calendar:** Planting and harvesting advisories tailored for mid-hills vs. valley microclimates.
5. **Farmer Profile & Onboarding:** Low-friction Phone OTP authentication with offline fallback profile storage.

---

## 🏗️ Project Architecture Snapshot

```
                       +---------------------------------------+
                       |        Jetpack Compose (UI)           |
                       | (Screens, Navigation, Outdoor Theme)  |
                       +---------------------------------------+
                                          |
                                    State / Events
                                          v
                       +---------------------------------------+
                       |           View Layer (MVVM)           |
                       |       StateFlow<UiState> & ViewModel  |
                       +---------------------------------------+
                                          |
                                   Repository API
                                          v
                       +---------------------------------------+
                       |      Offline-First Repository         |
                       | (Single Source of Truth: Room SQLite) |
                       +---------------------------------------+
                                    /           \
                                   /             \
                                  v               v
            +------------------------+      +---------------------------+
            |  Local Room Database   |      |   Remote Supabase Backend |
            | (Pre-seeded Agro Data) |      | (PostgreSQL, RLS, Auth)   |
            +------------------------+      +---------------------------+
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio:** Ladybug (2024.2.1+) or newer.
- **JDK:** OpenJDK 17 or JDK 21.
- **Android SDK:** Platforms 26 through 37 installed.
- **Physical Device:** Android device (API 26+) recommended for field verification and offline test runs.

### Installation & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/pranish/KrishiSaathi.git
   cd KrishiSaathi
   ```

2. **Configure Local Secrets:**
   Create or update `local.properties` in the project root:
   ```properties
   sdk.dir=/path/to/your/android-sdk
   SUPABASE_URL=https://your-project-ref.supabase.co
   SUPABASE_ANON_KEY=your-supabase-anon-key
   ```

3. **Build the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit and Android Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 📜 Documentation Index

For complete architectural specifications, database design, and operational rules, consult the project documentation suite:

- [CONTEXT.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/CONTEXT.md) - Business domain, offline principles, MVP scope, and data truth rules.
- [PLAN.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/PLAN.md) - 48-Hour compressed MVP development roadmap.
- [ARCHITECTURE.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/ARCHITECTURE.md) - Deep dive into MVVM, Repository pattern, and Room-Supabase sync.
- [DATABASE.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/DATABASE.md) - Relational schema, Room entities, and field specifications.
- [SECURITY.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/SECURITY.md) - Supabase RLS policies, Keystore security, and agrochemical safety checks.
- [TASKS.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/TASKS.md) - Granular tracking checklist for all 8 roadmap phases.
- [AGENT_RULES.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/AGENT_RULES.md) - Strict AI working guidelines and engineering constraints.

---

## ⚖️ License & Attribution

Agricultural advisory datasets conform to published documentation by the **Nepal Agricultural Research Council (NARC)** and the **Ministry of Agriculture and Livestock Development (MoALD), Nepal**.
All code is maintained under the project license.

