# Krishi Saathi: Master Development Roadmap

> 10 Phases · 95 Sub-phases · One phase at a time

This is the **authoritative development sequence** for Krishi Saathi. Each sub-phase has specific deliverables, tests, and completion criteria. No phase may be skipped.

See [PHASE_STATUS.md](PHASE_STATUS.md) for live progress tracking.

---

## Phase 1: Project Foundation (7 sub-phases)
Establish a clean, maintainable Android development environment.

| ID  | Sub-phase                             | Deliverable                   |
| --- | ------------------------------------- | ----------------------------- |
| 1.1 | Existing project inspection           | Project audit report          |
| 1.2 | Development environment verification  | SDK, JDK and toolchain report |
| 1.3 | Gradle, AGP and Kotlin compatibility  | Verified build configuration  |
| 1.4 | Package and application configuration | `com.krishisaathi.app`        |
| 1.5 | Project directory architecture        | Organized source structure    |
| 1.6 | Git initialization and conventions    | Version-controlled project    |
| 1.7 | Baseline build and test               | Successful initial build      |

## Phase 2: Backend & Database Foundation (10 sub-phases)
Establish the Supabase backend and secure database structure.

| ID   | Sub-phase                             | Deliverable                                |
| ---- | ------------------------------------- | ------------------------------------------ |
| 2.1  | Supabase project configuration        | Connected backend                          |
| 2.2  | Database architecture design          | Reviewed schema                            |
| 2.3  | Database migration setup              | Versioned SQL migrations                   |
| 2.4  | User and profile tables               | Farmer profiles                            |
| 2.5  | Agricultural content tables           | Crop and guide tables                      |
| 2.6  | Supporting tables                     | Favorites, notifications, sources and logs |
| 2.7  | Foreign keys, constraints and indexes | Validated relational structure             |
| 2.8  | RLS policy implementation             | Backend-enforced permissions               |
| 2.9  | Supabase Storage configuration        | Secure image storage                       |
| 2.10 | Backend integration testing           | Verified database connectivity             |

## Phase 3: Authentication & Farmer Profiles (11 sub-phases)
Implement secure user registration and account management.

| ID   | Sub-phase                         | Deliverable                  |
| ---- | --------------------------------- | ---------------------------- |
| 3.1  | Authentication architecture       | Defined authentication flow  |
| 3.2  | Phone number input and validation | Validated phone input        |
| 3.3  | SMS OTP integration               | SMS verification             |
| 3.4  | WhatsApp fallback integration     | Supported fallback delivery  |
| 3.5  | OTP verification screen           | Working verification UI      |
| 3.6  | Farmer registration               | Registration workflow        |
| 3.7  | Farmer profile creation           | Supabase profile integration |
| 3.8  | Session persistence               | Secure persistent sessions   |
| 3.9  | Login and logout                  | Complete account lifecycle   |
| 3.10 | Authentication error handling     | Reliable failure states      |
| 3.11 | Profile editing                   | Online profile management    |

## Phase 4: UI Foundation & Navigation (9 sub-phases)
Establish the complete visual and interaction system.

| ID  | Sub-phase                            | Deliverable                  |
| --- | ------------------------------------ | ---------------------------- |
| 4.1 | UI design system                     | Design tokens and guidelines |
| 4.2 | Nepali typography                    | Devanagari rendering         |
| 4.3 | Material 3 theme                     | Light and dark themes        |
| 4.4 | Reusable Compose components          | Shared UI library            |
| 4.5 | Application navigation               | Navigation architecture      |
| 4.6 | Bottom navigation                    | Four primary destinations    |
| 4.7 | Loading, empty and error states      | Consistent UI states         |
| 4.8 | Accessibility and responsive layouts | Farmer-friendly interfaces   |
| 4.9 | Device rendering verification        | Physical-device UI checks    |

## Phase 5: Core Agricultural Features (10 sub-phases)
Build the primary farmer-facing agricultural information system.

| ID   | Sub-phase                     | Deliverable                       |
| ---- | ----------------------------- | --------------------------------- |
| 5.1  | Home dashboard                | Personalized dashboard            |
| 5.2  | Crop directory                | Searchable crop catalogue         |
| 5.3  | Crop categories and filtering | Crop discovery                    |
| 5.4  | Crop details                  | Individual crop information       |
| 5.5  | Cultivation guide             | Growing instructions              |
| 5.6  | Soil and climate suitability  | Location-aware suitability        |
| 5.7  | Seasonal crop calendar        | Planting and harvesting calendar  |
| 5.8  | Local recommendation logic    | Hetauda/Makwanpur recommendations |
| 5.9  | Crop search                   | Search and filtering              |
| 5.10 | Favorites                     | Saved agricultural guides         |

## Phase 6: Agricultural Knowledge System (11 sub-phases)
Build verified pest, disease, fertilizer and pesticide information modules.

| ID   | Sub-phase                       | Deliverable                               |
| ---- | ------------------------------- | ----------------------------------------- |
| 6.1  | Agricultural source registry    | Trusted source database                   |
| 6.2  | Data verification workflow      | Verified/unverified status                |
| 6.3  | Pest information                | Pest identification guides                |
| 6.4  | Disease information             | Disease identification guides             |
| 6.5  | Symptoms and image presentation | Visual identification interface           |
| 6.6  | Prevention and management       | Management instructions                   |
| 6.7  | Fertilizer database             | Verified fertilizer guidance              |
| 6.8  | Fertilizer application guidance | Stage-specific instructions               |
| 6.9  | Pesticide product database      | Product-specific information              |
| 6.10 | Pesticide safety information    | PPE, re-entry and pre-harvest information |
| 6.11 | Agricultural content validation | Verified content publication              |

## Phase 7: Offline-First System (11 sub-phases)
Enable reliable offline access to previously downloaded agricultural information.

| ID   | Sub-phase                        | Deliverable                 |
| ---- | -------------------------------- | --------------------------- |
| 7.1  | Room database architecture       | Local database              |
| 7.2  | Entity and DAO implementation    | Local data access           |
| 7.3  | Repository integration           | Unified data access         |
| 7.4  | Content caching                  | Cached agricultural content |
| 7.5  | Synchronization architecture     | Supabase-to-Room sync       |
| 7.6  | WorkManager implementation       | Background refresh          |
| 7.7  | Version and deletion handling    | Reliable content updates    |
| 7.8  | Last-sync indicator              | Sync status UI              |
| 7.9  | Offline read-only enforcement    | Block offline writes        |
| 7.10 | Logout and local data cleanup    | Account-specific cleanup    |
| 7.11 | Offline and reconnection testing | Verified offline behavior   |

## Phase 8: Admin & Management System (11 sub-phases)
Give administrators the tools to manage farmers and agricultural content.

| ID   | Sub-phase                       | Deliverable                   |
| ---- | ------------------------------- | ----------------------------- |
| 8.1  | Admin authorization             | Protected admin access        |
| 8.2  | Admin dashboard                 | Administrative overview       |
| 8.3  | Farmer statistics               | Registration analytics        |
| 8.4  | Farmer listing                  | Searchable farmer records     |
| 8.5  | Farmer filters                  | Municipality and ward filters |
| 8.6  | Farmer profile management       | View and edit records         |
| 8.7  | Account deactivation            | Account status management     |
| 8.8  | Agricultural content management | Content CRUD                  |
| 8.9  | Notice management               | Publish and manage notices    |
| 8.10 | CSV/XLSX exports                | Farmer data exports           |
| 8.11 | Administrative audit logs       | Recorded admin activities     |

## Phase 9: Financial Tools & Application Integration (10 sub-phases)
Complete the financial estimation features and integrate the application.

| ID   | Sub-phase                           | Deliverable                     |
| ---- | ----------------------------------- | ------------------------------- |
| 9.1  | Land measurement and unit handling  | Standardized area calculations  |
| 9.2  | Cultivation cost inputs             | Cost entry interface            |
| 9.3  | Seed and fertilizer costs           | Input cost calculations         |
| 9.4  | Labor, irrigation and other costs   | Complete cost estimation        |
| 9.5  | Expected yield estimation           | Yield calculations              |
| 9.6  | Selling price inputs                | Market price entry              |
| 9.7  | Revenue calculation                 | Estimated revenue               |
| 9.8  | Profit/loss calculation             | Estimated financial result      |
| 9.9  | Assumptions and source presentation | Transparent estimates           |
| 9.10 | Feature integration                 | Connected application workflows |

## Phase 10: Testing, Security & Release (12 sub-phases)
Validate the entire application and prepare a release candidate.

| ID    | Sub-phase                           | Deliverable             |
| ----- | ----------------------------------- | ----------------------- |
| 10.1  | Kotlin code quality checks          | Static analysis results |
| 10.2  | Unit testing                        | Business logic tests    |
| 10.3  | Repository and database testing     | Data layer tests        |
| 10.4  | Authentication testing              | OTP and session tests   |
| 10.5  | RLS and authorization audit         | Security verification   |
| 10.6  | Offline testing                     | Offline behavior report |
| 10.7  | Network failure testing             | Resilience report       |
| 10.8  | Nepali UI and accessibility testing | UI quality report       |
| 10.9  | Performance and memory testing      | Performance report      |
| 10.10 | Physical-device testing             | Device test report      |
| 10.11 | Release configuration               | Signed release build    |
| 10.12 | Documentation and final APK         | Release candidate       |

