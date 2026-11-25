# Krishi Saathi: Complete Requirements Interview Results

**Project:** Krishi Saathi (कृषि साथी)  
**Baseline Date:** 2026-11-25  
**Document Status:** Approved Project Baseline (from 61-Question Requirements Interview)

---

## 1. Complete Interview Questions & Confirmed Decisions

### A. Product and Geographical Requirements

| Question | Topic | Confirmed Decision | Implementation Constraint |
|---|---|---|---|
| **Q1** | Target User Persona | Individual farmers only | Do not introduce students, agribusinesses, cooperatives, or government bodies. |
| **Q2** | Geographical Coverage | Initial rollout in selected municipalities; long-term coverage nationwide | Geographical coverage must be configurable. Do not hardcode a single district permanently. |
| **Q3** | Language Support | Nepali language only at initial launch | All user-facing UI and core agricultural data in Nepali Unicode text. |
| **Q4** | Location Input | Both automatic GPS location detection and manual municipality/ward selection | Support GPS coordinates and manual dropdown selection. |
| **Q5** | Farm Hierarchy | Multiple farms per farmer account, with multiple plots within each farm | Database and domain model must support Farmer -> Farm (1:N) -> Plot (1:N). |
| **Q6** | Farm Attributes | Farm name, location, and total land area | Every farm must store name, location, and total land area with an explicit unit. |
| **Q7** | Farming Domain | Crop farming only | No livestock, poultry, or fishery modules in the system. |

---

### B. Crops and Agricultural Information

| Question | Topic | Confirmed Decision | Implementation Constraint |
|---|---|---|---|
| **Q8** | Crop Catalogue & Entry | Predefined crop catalogue, manual crop entry, and location/season-based recommendations | Support searching catalog, adding custom crops, and filtering recommendations. |
| **Q9** | Cultivation Guides | Text guides, image-supported guides, and complete step-by-step cultivation stages | Stages: 1. Land prep, 2. Seed selection, 3. Sowing/planting, 4. Irrigation, 5. Fertilization, 6. Crop care, 7. Pest/disease management, 8. Harvesting. |
| **Q10** | Pest & Disease Info | Searchable pest/disease library, treatment recommendations, and prevention guidance | Categorized by crop with biological, cultural, and chemical controls. |
| **Q11** | Photo Identification | Originally considered online and offline photo identification | **Scope Correction (see Q27):** Excluded from initial prototype. |
| **Q12** | Weather | Current weather, daily/weekly forecasts, rainfall, and extreme-weather alerts | Long-term requirement. Keep outside critical prototype path unless verified free API is available. |
| **Q13** | Crop Reminders | Farmer-configured and automatically scheduled crop reminders | Android push notifications and local reminders. |
| **Q14** | Fertilizer Guidance | Detailed fertilizer information (quantities, schedules, organic vs. chemical comparisons, soil-specific) | Store verified NPK and farmyard manure guidance. Do not fabricate dosages. |
| **Q15** | Pesticide Safety | Pesticide info, dosage, application methods, safety equipment (PPE), precautions, pre-harvest intervals (PHI) | High-contrast toxicity color badges (Red, Yellow, Blue, Green) and prominent PHI days. |
| **Q16** | Market Prices | Daily market prices for major crops | Government-published data. Placeholder/configured module; no fabricated prices. |
| **Q17** | Financial Management | No farm expense or income management | Explicitly excluded from prototype. No farm accounting, profit/loss, or cost calculators. |
| **Q18** | Agricultural Q&A | Agricultural questions submitted through a contact form | Online-only contact submission form. No offline submissions. |
| **Q19** | Content Publishing | Administrators may publish content directly without a separate expert approval workflow | Content Admins and Super Admins can publish directly to PostgreSQL. |

---

### C. Notifications, Offline Support and Content

| Question | Topic | Confirmed Decision | Implementation Constraint |
|---|---|---|---|
| **Q20** | Content Download | Automatically download content updates whenever internet is available | Background sync pulls delta from Supabase into local Room DB. |
| **Q21** | Notifications | Android push notifications only | No SMS or WhatsApp marketing/advisory notifications. |
| **Q22** | Multi-Account Support | Multiple farmer accounts can use the same device with separate login sessions | Separate session tokens and account isolation on a shared phone. |
| **Q23** | Offline Access | Access downloaded information offline; synchronize automatically when connectivity returns | **Strict Offline Restriction:** Offline mode is **READ-ONLY**. Writes (farm creation, profile edits) require internet. |
| **Q24** | Crop Recommendations | Recommendations based on geographical location and current season | Location + Season rule matrix. No unapproved factors (income, soil tests). |
| **Q25** | Weather Sources | Government and third-party weather sources | Configurable provider interface. |
| **Q26** | Local Image DB | Offline identification considered but deferred | No local offline photo classification models bundled in APK. |
| **Q27** | Image Detector | **Explicitly Confirmed: NO image detector in initial prototype** | Text/image-based reference guides only. No computer vision ML models. |
| **Q28** | Market Price Data | Government-published market price data | Kalimati / MoALD market price source integration when available. |
| **Q29** | Administration Platform | Dedicated web-based administration dashboard | Web dashboard separate from the farmer mobile app. |
| **Q30** | Administrative Roles | Two administrative roles: Super Admin and Content Admin | Super Admin (user & content management), Content Admin (content CRUD & publishing). |

---

### D. Content Organization and Discovery

| Question | Topic | Confirmed Decision | Implementation Constraint |
|---|---|---|---|
| **Q31** | Database Architecture | Supabase cloud database and local Android database | Dual-database architecture: Supabase PostgreSQL + Android Room SQLite. |
| **Q32** | Cloud Backend | Supabase | PostgREST API, Supabase Auth, Storage buckets. |
| **Q33** | Admin Authentication | Administrator email/password authentication | Managed via Django / Supabase auth. |
| **Q34** | Content Taxonomies | Content organized by crop categories, topics, seasons, and geographical regions | Relational taxonomies linked via normalized junction tables. |
| **Q35** | Search Functionality | Search with filters, categories, suggestions, and full Nepali text support | Indexed Nepali search with wildcard/like fallback for SQLite FTS limitations. |
| **Q36** | Bookmarks & Collections | Favorite crops/guides and personal collections | Farmer can save favorites and organize into named collections. |
| **Q37** | Media Formats | **Explicitly Confirmed: NO video content** | Text guides with WebP/PNG photographic references only. |

---

### E. Technology and Development Workflow

| Question | Topic | Confirmed Decision | Implementation Constraint |
|---|---|---|---|
| **Q38** | Android Framework | Kotlin + Jetpack Compose | Native Android, Material 3, declarative UI. |
| **Q39** | Prototype Scope | Functional end-to-end prototype | Real database integration; no static fake screens. |
| **Q40** | Branding | Official logo and brand colors already exist | Custom agricultural emblem and palette created in app. |
| **Q41** | Brand Asset Access | Official brand assets currently inaccessible | Provisional high-contrast agricultural theme (White canvas, Black text, Agro Emerald & Amber highlights). |
| **Q42** | Tooling Environment | Antigravity for coding; Android Studio for Gradle sync, builds, and testing | Build verification via Gradle CLI; clean build scripts. |
| **Q43** | Admin Dashboard Tech | Build a Django administration dashboard immediately | Full Python Django web app connected to the same PostgreSQL DB. |
| **Q44** | Web Backend | Django | Standard Django 5.x / 6.x MVC with ORM. |
| **Q45** | Database Sharing | Django connects directly to the same Supabase PostgreSQL database | Single source of truth in PostgreSQL. No duplicate schemas. |
| **Q46** | Admin Styling | Django templates + Tailwind CSS; **NO Bootstrap** | Responsive Tailwind CSS layouts for admin dashboard. |
| **Q47** | Web Deployment | Online deployment using free hosting | Compatible with Render, Railway, PythonAnywhere, or local deployment fallback. |
| **Q48** | Testing Platforms | Physical APK testing, Android emulator testing, and intended Google Play internal testing | Deliver standalone debug APK and test runs. |
| **Q49** | Google Play Console | No Google Play Console developer account currently exists | Distribute via APK; document Play Store submission steps. |

---

### F. Authentication and Security

| Question | Topic | Confirmed Decision | Implementation Constraint |
|---|---|---|---|
| **Q50** | Auth Mechanism | Phone number + MPIN, with OTP verification | E.164 phone numbers (`+977-98XXXXXXXX`). |
| **Q51** | Auth Flow Stages | Registration: Phone -> OTP -> MPIN setup. Normal login: Phone + MPIN. New device / recovery: OTP required. | Strict separation between initial registration and return MPIN login. |
| **Q52** | OTP Provider | No production OTP provider configured; use testing OTP solution for prototype | Development OTP testing mode with deterministic testing codes. Never fake delivery. |
| **Q53** | MPIN Format | 4-digit numeric MPIN | Validated `^\d{4}$`; hashed securely; never logged or stored plaintext. |

---

### G. Local Database, Architecture and Networking

| Question | Topic | Confirmed Decision | Implementation Constraint |
|---|---|---|---|
| **Q54** | Local Android DB | Room Database with fast search and retrieval | AndroidX Room with Kotlin Flow DAO streams. |
| **Q55** | Sync & Caching | Automatically use cached information and synchronize when internet returns (read-only offline) | WorkManager periodic sync + immediate local-first query dispatch. |
| **Q56** | Software Architecture | Clean Architecture + MVVM + Repository Pattern | Decoupled UI, Domain, and Data layers. |
| **Q57** | Dependency Injection | Hilt (or lightweight AppContainer DI for prototype build reliability) | DI container providing singletons across ViewModels and Repositories. |
| **Q58** | Navigation | Jetpack Navigation Compose with type-safe routes | Composable navigation graph with typed arguments. |
| **Q59** | UI Design System | Material 3 as provisional implementation baseline | Clean, high-contrast, replaceable tokens. |
| **Q60** | Network Client | Supabase Kotlin SDK + Ktor | Official client library for PostgREST and GoTrue. |
| **Q61** | SDK Levels | Minimum SDK 26, Target latest stable Android SDK (34-37) | Android 8.0 Oreo minimum for widespread Nepal device compatibility. |

---

## 2. Scope Resolutions and Boundaries

1. **NO Image Detection:** Confirmed by Q27. Image detection is strictly out of scope for the prototype.
2. **NO Video Streaming:** Confirmed by Q37. Text and still photos only.
3. **NO Financial Tracking:** Confirmed by Q17. No expense, revenue, or profit calculations.
4. **READ-ONLY Offline Mode:** Confirmed by Q23. Offline mutation queues are forbidden. Write actions (creating a farm, modifying profile) require active internet.
5. **Development OTP Mode:** Confirmed by Q52. A development testing OTP pathway is used until an external SMS/WhatsApp gateway is contracted.

