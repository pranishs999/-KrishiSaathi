# KRISHI SAATHI
## Master Project Specification & Antigravity Implementation Prompt

**Project:** Krishi Saathi  
**Product:** Farmer-focused agricultural assistance platform  
**Initial target market:** Selected municipalities in Nepal  
**Long-term target:** Nationwide coverage across Nepal  
**Primary language:** Nepali  
**Development deadline:** 24 hours for the prototype  
**Development workflow:** Antigravity for coding; Android Studio for Gradle sync, build, run and test  
**Status:** Prototype development

---

# 1. MASTER INSTRUCTIONS FOR ANTIGRAVITY

You are the lead software architect, senior Android developer, backend developer, database engineer, UI/UX designer, security engineer and QA engineer responsible for implementing Krishi Saathi.

Your task is to build a functioning, integrated prototype. Do not stop after writing a plan, generating mockups or creating documentation.

## 1.1 First actions

Before modifying files:

1. Inspect the entire existing project workspace.
2. Identify existing source files, applications, configurations, dependencies, documentation and assets.
3. Determine what already works and what is missing.
4. Preserve working code and existing decisions.
5. Identify genuine blockers, especially missing credentials and external services.
6. Create or update the project documentation listed in Section 18.
7. Create a prioritized implementation plan and immediately proceed with implementation.

Do not create a second unrelated project if a working project already exists. Adapt the existing repository carefully.

The requirements in this document reflect a 61-question requirements interview. Treat confirmed answers and later scope clarifications as the approved project baseline. Do not ask the user to reconfirm requirements already recorded here.

When a detail is not specified, document it as undecided or configurable. Do not invent credentials, APIs, data sources, official brand colours, municipalities or verified agricultural advice.

## 1.2 Delivery objective

Deliver the best genuinely working prototype possible within the 24-hour development window. Prioritize an end-to-end farmer workflow, agricultural content administration and offline reading over secondary features.

Never claim a feature, test, integration or deployment works unless it has actually been implemented and verified.

---

# 2. PRODUCT OVERVIEW

Krishi Saathi is a Nepali-language agricultural assistance application for individual farmers. It is intended to help farmers manage basic farm information, discover crops suitable for their location and season, access cultivation information, search agricultural guides and read previously synchronized information without an internet connection.

The initial rollout is intended for selected municipalities in Nepal. The long-term goal is nationwide availability. The initial municipalities have not yet been specified, so geographical coverage must be configurable rather than hard-coded.

## 2.1 Target users

The confirmed primary audience is **individual farmers**.

The system has two main interfaces:

1. Farmer-facing Android application.
2. Administrator-facing web dashboard.

Do not add unapproved audiences, roles, marketplaces, community forums or business features.

## 2.2 Launch language

The launch language is Nepali only. Support Nepali Unicode correctly throughout the application, dashboard content and search experience. Keep the implementation extensible for additional languages later, but do not build an unrequested multilingual interface in this prototype.

## 2.3 Agriculture scope

The confirmed initial product domain is **crop farming only**. Do not add livestock management or unrelated agricultural modules.

---

# 3. COMPLETE REQUIREMENTS INTERVIEW RECORD

The following records the decisions made during the 61-question interview. Where a later answer narrowed or overrode an earlier selection, the later clarification is explicitly identified.

## 3.1 Product, audience and geography

| Question | Confirmed answer |
|---|---|
| Q1 | Individual farmers only. |
| Q2 | Initial rollout in selected municipalities; long-term goal is all of Nepal. Exact municipalities are not yet specified. |
| Q3 | Nepali only at launch. |
| Q4 | Both GPS-based location detection and manual location selection. |
| Q5 | One farmer account can manage multiple farms, and a farm can contain multiple plots. |
| Q6 | Collect farm name, location and total land area. |
| Q7 | Crop farming only. |

Location selection must permit manual correction when GPS is unavailable or inaccurate. Do not require GPS permission just to use the application.

For land area, retain the unit explicitly. Do not assume all farmers use the same unit. The user has not selected a canonical land-area unit or conversion policy.

## 3.2 Crops, guides and agricultural assistance

| Question | Confirmed answer |
|---|---|
| Q8 | Provide a predefined crop catalogue, manual crop entry and recommendations based on location and season. |
| Q9 | The intended product content includes short text guides, detailed step-by-step guides with images and complete cultivation lifecycle guides. |
| Q10 | The intended product includes a searchable pest/disease information library, treatment recommendations and prevention instructions. |
| Q11 | An earlier selection included photo-based identification. This was later explicitly excluded. |
| Q12 | Intended weather capability: current weather, daily/weekly forecasts, rainfall and extreme-weather alerts. |
| Q13 | Farmer-configurable reminders and automatically scheduled crop-specific reminders based on planting dates and cultivation stages. |
| Q14 | Intended fertilizer information: types, quantities, application methods, crop-specific schedules, precautions, organic/chemical comparison and soil-specific recommendations. |
| Q15 | Intended pesticide information: recommended pesticides, dosage, application methods, safety precautions, protective equipment and pre-harvest waiting periods. |
| Q16 | Daily prices for major agricultural crops. |
| Q17 | No farm financial-management feature. Do not build expense, income or profit/loss tracking. |
| Q18 | Farmers may submit questions using a contact form. No expert chat or community forum was selected. |
| Q19 | Administrators can publish content directly. There is no separate expert-approval workflow. |

### Scope clarifications that override earlier selections

- **No image detector:** A later explicit answer confirmed that there must be no image detector in the prototype. Do not implement image-based pest or disease detection, crop-image recognition or an AI image-identification API.
- **No video module:** A later explicit answer excluded video content from the application. Do not implement video streaming, video lessons or video downloads.
- The searchable, text-based pest and disease library remains part of the broader product concept, but it is secondary to the core prototype.
- Agricultural recommendations, fertilizer quantities, pesticide dosages and safety periods must not be fabricated. Use verified source material or clearly labelled demonstration content that cannot be mistaken for verified advice.

## 3.3 Weather, notifications, offline use and content updates

| Question | Confirmed answer |
|---|---|
| Q20 | Automatically download content updates whenever an internet connection is available. |
| Q21 | Android push notifications only. |
| Q22 | Multiple farmer accounts may use one device, with separate login sessions. |
| Q23 | Previously downloaded information must remain available offline, with automatic synchronization when connectivity returns. |
| Q24 | Crop recommendations use geographical location and current season. |
| Q25 | Weather sources should include Nepal government meteorological data and third-party weather APIs. No exact provider has been selected. |
| Q26 | A local reference-image database was considered, but offline image identification is not required for the first release. |
| Q27 | Final clarification: no image detector. |
| Q28 | Market prices should come from government-published data. No exact dataset/API has been selected. |
| Q29 | Administration must use a dedicated web dashboard. |
| Q30 | Administrator roles: Super Admin and Content Admin. |

### Mandatory offline rule

Offline mode is **read-only**.

A farmer can read previously synchronized and downloaded information offline. The farmer must not be able to perform profile edits, farm changes, submissions, favorites/collection changes or any other write operation until connectivity is restored.

Do not implement offline mutation queues that contradict this rule. Background synchronization may refresh and download permitted content. When the internet returns, the application can enable online actions again.

## 3.4 Content organization, search and personalization

| Question | Confirmed answer |
|---|---|
| Q31 | Hybrid storage: cloud database plus local database for offline access. |
| Q32 | Supabase. |
| Q33 | Administrator authentication with email/password. |
| Q34 | Organize content by crop categories, agricultural topics, seasons and geographical regions. |
| Q35 | Search with filters, crop/topic categories, relevant suggestions and Nepali text support. |
| Q36 | Favorite crops and guides, plus personal collections/folders. |
| Q37 | No video content. |

Favorites and personal collections are intended product features. Their modifications require connectivity under the read-only offline rule. Previously synchronized favorites and collections may be viewed offline.

## 3.5 Development technologies and workflow

| Question | Confirmed answer |
|---|---|
| Q38 | Native Android development using Kotlin and Jetpack Compose. |
| Q39 | The prototype should be an end-to-end working application, not just a UI demonstration. |
| Q40 | Official logo and brand colours already exist. |
| Q41 | The brand assets are currently inaccessible to the developer/agent. |
| Q42 | Use Antigravity for coding and Android Studio for Gradle synchronization, builds and testing. |
| Q43 | Build a web administration dashboard immediately so content can be managed. |
| Q44 | Django for the administration application. |
| Q45 | Django connects directly to the same Supabase PostgreSQL database used by Android. |
| Q46 | Django templates + Tailwind CSS. Do not use Bootstrap. |
| Q47 | Deploy online using a free hosting option where practical. |
| Q48 | Test APK on a physical Android device and emulator; Google Play internal testing is also desired. |
| Q49 | No Google Play Console developer account currently exists. |

Do not invent the official logo or colours. Search the workspace for assets first. If unavailable, use a restrained, replaceable provisional theme and clearly mark it as temporary.

A working APK and local/test deployment take priority over Play Console setup. Do not claim Play internal testing is complete without a registered and verified Play Console account and a successful release workflow.

## 3.6 Authentication decisions

| Question | Confirmed answer |
|---|---|
| Q50 | Phone number and MPIN, similar to a phone-number-plus-PIN login experience, with OTP verification. |
| Q51 | Registration: phone number → OTP → set MPIN. Normal login: phone + MPIN. New device and account recovery require OTP. |
| Q52 | No SMS or WhatsApp OTP provider has been configured. Use a development/testing OTP mechanism for the prototype. |
| Q53 | MPIN is exactly four digits. |

### Required authentication behavior

**Registration**

1. Farmer enters a phone number.
2. The system initiates OTP verification.
3. SMS is the intended primary delivery channel.
4. WhatsApp is the intended fallback where supported and configured.
5. Farmer verifies the OTP.
6. Farmer creates a four-digit MPIN.
7. Farmer profile/account is provisioned.
8. A secure authenticated session is established.

**Normal login**

1. Farmer enters phone number and four-digit MPIN.
2. The server verifies the MPIN using a secure authentication mechanism.
3. A valid session is established or the existing session is unlocked according to the chosen architecture.
4. OTP must not be requested on every ordinary app launch.

**New device**

Require additional OTP verification before trusting a new device/session.

**Account recovery / MPIN reset**

Require OTP verification before allowing the MPIN to be reset.

**Sessions**

- Persist sessions securely.
- Use Android Keystore-backed secure token storage.
- Refresh tokens when online.
- Support separate account sessions on shared devices without leaking one farmer's data to another.
- Logout must clear the selected account's sensitive local session material.

### Authentication architecture warning

Supabase phone OTP and a custom four-digit MPIN login are not automatically the same authentication mechanism. Do not pretend that storing a PIN in Android preferences implements secure MPIN authentication.

Choose and document one safe architecture before implementation. Options to evaluate include:

- Using the MPIN as a secure local unlock for an already-established Supabase session, while OTP is used for enrollment, new-device verification and recovery; or
- Implementing server-side MPIN verification with modern password hashing, rate limiting and a carefully designed token/session issuance mechanism.

The user expects normal phone + MPIN login. The selected implementation must satisfy that experience without client-only PIN verification or plaintext PIN storage. Explain any prototype limitation honestly.

Never log MPINs, OTPs, access tokens or refresh tokens.

### OTP provider restriction

No production SMS or WhatsApp provider credentials exist. Build an isolated development-only OTP/testing mechanism. Do not claim it sends real SMS or WhatsApp. Make it impossible for a release build to silently use a predictable test OTP. Keep the production SMS-primary/WhatsApp-fallback adapter configurable for later integration.

## 3.7 Local database, architecture and network stack

| Question | Confirmed answer |
|---|---|
| Q54 | Android Room database, optimized for quick search and retrieval. |
| Q55 | Automatically use cached data and synchronize when online, while respecting the offline read-only restriction. |
| Q56 | Clean Architecture + MVVM + Repository pattern. |
| Q57 | Hilt for dependency injection. |
| Q58 | Jetpack Navigation Compose with type-safe routes. |
| Q59 | UI design-system selection was not decided. |
| Q60 | Supabase Kotlin SDK + Ktor. |
| Q61 | Minimum SDK 26 (Android 8.0); target the latest stable Android SDK supported by the development environment. |

Use Material 3 as a **provisional** Compose design foundation only. Keep typography, spacing, colour and component tokens centralized so the official branding can replace it later.

---

# 4. PROTOTYPE SCOPE AND PRIORITIES

The interview describes the intended broader product. The 24-hour prototype must implement the essential end-to-end path first. Do not treat every long-term feature as a blocker for the initial APK.

## 4.1 Priority 0: Required prototype functionality

### A. Authentication

Implement:

- Phone number registration.
- Clearly labelled development OTP flow.
- Four-digit MPIN setup.
- Phone + MPIN normal login experience.
- New-device verification flow or a clearly documented prototype implementation of it.
- MPIN recovery flow or a clearly documented prototype implementation of it.
- Persistent secure sessions.
- Logout.
- Separate sessions/data isolation for multiple farmer accounts on one device.

No production OTP delivery is possible until credentials and an approved provider are supplied.

### B. Farmer profile and location

Implement:

- Farmer profile creation and display.
- Phone number association with authenticated account.
- GPS location detection with permission handling.
- Manual location selection and correction.
- Sensible behavior if location permission is denied or GPS is unavailable.

Do not add extra mandatory demographic fields that were never approved.

### C. Farm and plot management

Implement:

- Create and list multiple farms.
- View farm details.
- Edit farm details when online.
- Add multiple plots within a farm.
- Farm name, location and total land area.
- Explicit land-area unit.
- Correct farmer ownership relationships.

All create/update/delete actions require connectivity. Previously synchronized farm data can be viewed offline.

### D. Crop catalogue

Implement:

- Crop list and details.
- Crop categories.
- Nepali crop names and descriptions.
- Search and filters.
- Predefined catalogue selection.
- Manual crop entry where online.
- Basic association between a crop and a farm/plot.

### E. Agricultural guides

Implement:

- Guide list and details.
- Text-based, step-by-step instructions.
- Supporting images where available.
- Crop, topic, season and region filters.
- Lifecycle-oriented content structure.

The guide content model should be able to represent these stages:

1. Land preparation.
2. Seed selection.
3. Sowing or planting.
4. Irrigation.
5. Fertilization.
6. Crop care.
7. Pest and disease management.
8. Harvesting.

Do not invent agricultural facts. If seed data is required to demonstrate the interface, label it visibly as demonstration content and do not present it as verified advice.

### F. Nepali search

Implement search across:

- Crop names.
- Guide titles.
- Guide content.
- Categories and topics.

Support Nepali Unicode. Investigate SQLite FTS5 behavior with Nepali text before relying on it. If it does not meet requirements, build and test an indexed fallback. Use pagination and avoid loading the full content catalogue into memory.

### G. Offline reading

Implement:

- Room database.
- Cached crop catalogue.
- Cached agricultural guides.
- Cached images where practical.
- Connectivity awareness.
- Visible offline status.
- Read-only browsing of synchronized data.
- Automatic content refresh when connectivity returns.
- Retry/backoff for refresh failures.

A failed network request must not crash the UI or erase valid cached content.

### H. Django administration dashboard

Required in the prototype:

- Administrator email/password login.
- Logout.
- Super Admin role.
- Content Admin role.
- Dashboard overview.
- Crop CRUD.
- Crop category management.
- Guide CRUD.
- Topic management.
- Season management.
- Region management.
- Publish/unpublish controls.
- Role-based permissions enforced on the server.
- Basic administrator activity logging.

Administrators can publish directly. No expert review queue is needed.

## 4.2 Priority 1: Add after Priority 0 works

- Favorite crops and guides.
- Personal collections/folders.
- Rule-based crop recommendations from location and current season, using verified/configured rules.
- Search suggestions and more comprehensive filters.
- Better image caching and content versioning.

Favorites and collection modifications require connectivity. Cached items may be viewed offline.

## 4.3 Priority 2: Broader product features, not prototype blockers

### Weather

Long-term desired functionality:

- Current weather.
- Daily and weekly forecast.
- Rainfall information.
- Extreme-weather alerts.
- Nepal government meteorological data and third-party weather APIs.

No exact provider, API or credentials have been selected. Do not fabricate current weather or forecasts.

### Market prices

Long-term desired functionality:

- Daily market prices for major crops.
- Government-published data source.

No exact endpoint or dataset has been selected. Do not invent prices or label sample prices as current government data.

### Fertilizer and pesticide information

Future content should support:

- Fertilizer types, quantities, methods, schedules and precautions.
- Organic/chemical comparisons.
- Soil-specific recommendations where evidence exists.
- Pesticide recommendations, dosage, application methods and protective equipment.
- Safety precautions and pre-harvest intervals.

Use reliable, attributable agricultural sources. Do not generate unverified chemical dosage or safety guidance.

### Notifications and reminders

Future functionality:

- Android push notifications only.
- Farmer-configured reminders.
- Crop-stage reminders calculated from planting dates and cultivation stages.
- Relevant weather or agricultural warnings.

### Farmer contact form

Future functionality:

- Submit an agricultural question online.
- Admin access to submitted questions.
- No offline submissions.
- No expert chat or community forum.

### Pest and disease library

A searchable text-based information library is in the broader product requirements. It can follow the core guide/content workflow. Photo-based identification is explicitly excluded.

## 4.4 Explicit prototype exclusions

Do not implement:

- Image-based pest/disease detection.
- AI crop image recognition.
- Video streaming or video lessons.
- Farm financial tracking.
- Expense or income management.
- Profit/loss calculation.
- Livestock management.
- Marketplace or e-commerce.
- Social/community forum.
- Expert chat.
- Unapproved roles or user categories.
- Offline write queues.

---

# 5. TECHNOLOGY STACK

## 5.1 Android

- Language: Kotlin.
- UI: Jetpack Compose.
- Design foundation: Material 3, provisional.
- Architecture: Clean Architecture + MVVM + Repository.
- Async: Kotlin Coroutines and Flow.
- Dependency injection: Hilt.
- Navigation: Jetpack Navigation Compose, type-safe routes.
- Local persistence: Room / SQLite.
- Network: Supabase Kotlin SDK + Ktor.
- Background content refresh: WorkManager, constrained to network availability.
- Secure token storage: Android Keystore-backed solution.
- Minimum SDK: 26.
- Target SDK: latest stable available in the installed toolchain.

Verify compatibility between Android Gradle Plugin, Gradle, Kotlin, Compose, Hilt, Room, Supabase SDK and Ktor. Pin mutually compatible versions. Do not blindly upgrade a working project to preview or incompatible versions.

## 5.2 Django web administration

- Python + Django.
- Django templates.
- Tailwind CSS.
- No Bootstrap.
- Shared Supabase PostgreSQL database.
- Django email/password authentication.
- Server-side role checks.

Do not replace Django with FastAPI, Node.js, Firebase Functions or another backend without explicit approval.

## 5.3 Database and cloud

- Supabase PostgreSQL is the shared cloud database.
- Android uses the intended Supabase client interfaces with restricted privileges and RLS.
- Django connects directly to the same PostgreSQL database using a dedicated least-privilege database role and TLS/SSL.
- Room is the Android local read cache.

Keep one canonical migration history for the shared database. Do not let Django and Supabase migrations independently modify the same tables without a documented coordinated strategy.

---

# 6. DATABASE DESIGN REQUIREMENTS

Design a normalized and documented schema. At minimum, evaluate the entities below. Add tables only when they support a confirmed requirement.

## 6.1 Farmer-owned data

- `farmer_profiles`
- `farms`
- `farm_plots`
- `farmer_crops` or an equivalent crop-to-farm/plot association

## 6.2 Agricultural content

- `crop_categories`
- `crops`
- `seasons`
- `crop_seasons` or equivalent mapping
- `topics`
- `regions`
- `guides`
- `guide_crops`
- `guide_topics`
- `guide_regions`

## 6.3 Personalization

- `favorites`
- `collections`
- `collection_items`

## 6.4 Synchronization metadata

- `content_versions` or equivalent content revision tracking
- `content_sync_metadata` where needed

## 6.5 Administration

- Administrator account/role mapping, if not adequately represented by Django's auth tables.
- Administrator activity log.

## 6.6 Relationship expectations

- One farmer can own multiple farms.
- One farm can contain multiple plots.
- A crop can be associated with multiple farms/plots.
- One crop can have multiple guides.
- One guide can cover multiple topics, seasons and regions.
- One farmer can have multiple favorites.
- One farmer can have multiple collections.
- One collection can contain multiple saved items.

Use UUID primary keys where appropriate, explicit foreign keys, `created_at` and `updated_at` timestamps, appropriate delete behavior and indexes. Store Nepali text with PostgreSQL UTF-8 support.

Use an explicit land-area unit field. The exact preferred unit is not yet decided.

Document the schema and relationships before creating migrations. Ensure Django models accurately reflect the shared schema.

---

# 7. DATABASE SECURITY AND AUTHORIZATION

Security must be built in from the beginning.

## 7.1 Farmer data

Enable and test Row Level Security (RLS) for farmer-owned records.

- A farmer can access only their own profile.
- A farmer can access only their own farms and plots.
- A farmer can access only their own crop associations, favorites and collections.
- A farmer cannot query or modify another farmer's data by changing an identifier.
- Published global agricultural content is readable according to publication status.
- Unpublished content must not be exposed to ordinary farmer accounts unless explicitly intended for an authorized admin.

## 7.2 Administration

- Super Admin and Content Admin permissions must be enforced server-side.
- Content Admin must not be able to perform Super Admin-only actions.
- Do not rely on hiding buttons in HTML as an authorization control.
- Protect administrator routes and mutations.
- Log meaningful administrator actions.

## 7.3 Credentials

- Never commit secrets.
- Never place database passwords or privileged Supabase keys in Android.
- Never place privileged credentials in browser JavaScript.
- Use environment variables for server-side secrets.
- Use a dedicated, restricted PostgreSQL role for Django.
- Do not connect Django with an unnecessarily privileged owner/superuser role.
- Use HTTPS in deployed environments.
- Validate all server-side inputs.

---

# 8. OFFLINE-FIRST READING AND SYNCHRONIZATION

The local Room database is the source for farmer-facing cached read operations.

## 8.1 Read flow

1. Farmer opens a screen.
2. The application reads available local Room data immediately.
3. The UI displays cached information without waiting for the network.
4. The application checks connectivity.
5. If online, it requests permitted updates from Supabase.
6. Valid updates are written to Room transactionally.
7. The UI observes Room updates through Flow.
8. If synchronization fails, keep existing cached data and show a non-blocking status.

## 8.2 Synchronization

Use WorkManager for appropriate background refreshes. Apply network constraints, bounded retries and backoff. Consider pagination, `updated_at` timestamps, content versioning, duplicate prevention and database transactions.

Do not download the same guide images repeatedly. Use cache-aware image handling and sensible size limits.

## 8.3 Strict read-only offline behavior

When offline:

- Allow reading cached crops, guides, farm details and other previously synchronized information.
- Disable or block profile modifications.
- Disable or block farm/plot changes.
- Disable or block favorites and collection modifications.
- Disable or block form submissions.
- Disable every other write action.
- Explain that the action requires an internet connection.

Do not queue offline writes for later execution. This is an explicit product rule, not an implementation detail to be optimized away.

---

# 9. ANDROID APPLICATION STRUCTURE

Inspect the existing project before restructuring it. A reasonable target structure is:

```text
android/
├── app/
├── core/
│   ├── common/
│   ├── database/
│   ├── network/
│   ├── security/
│   └── designsystem/
├── data/
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── feature/
    ├── auth/
    ├── home/
    ├── profile/
    ├── farm/
    ├── crop/
    ├── guide/
    └── search/
```

Use a practical architecture. Avoid excessive abstraction, over-modularization or unnecessary dependencies that consume the 24-hour schedule.

All critical screens must have real state, repository connections and meaningful loading, empty, error and offline states. Do not leave primary buttons inert.

---

# 10. DJANGO ADMIN DASHBOARD

Create a responsive, usable web administration dashboard with Django templates and Tailwind CSS.

## 10.1 Roles

**Super Admin**

- Manage administrator access and roles.
- Manage all agricultural content.
- Publish and unpublish content.
- Access administrative overview and activity logs.

**Content Admin**

- Manage permitted crop and agricultural guide content.
- Publish and unpublish content if granted that permission.
- Cannot perform Super Admin-only user/role administration.

If the exact permission split is not fully specified, use least privilege and document the provisional policy rather than granting every administrator unrestricted access.

## 10.2 Required content administration

- Crop categories.
- Crops.
- Topics.
- Seasons.
- Regions.
- Guides.
- Guide-to-crop/topic/region relationships.
- Draft/published status.

Content may be published directly by an authorized administrator. Do not create an expert approval queue.

## 10.3 User experience

- Nepali content entry must work.
- Forms must validate input.
- Display useful success and error messages.
- Protect delete and publish actions.
- Add pagination and search to long lists.
- Do not use Bootstrap.
- Do not place secrets in templates or client-side scripts.

---

# 11. UI/UX AND BRANDING

The official logo and brand colours exist but are not currently accessible.

First inspect the workspace and project assets for the official files. If not found:

- Use a temporary, replaceable visual theme.
- Clearly label the theme as provisional in documentation.
- Do not claim provisional colours are official brand colours.
- Keep tokens centralized for easy replacement.

Question 59 of the interview did not select a final UI design system. Material 3 is a provisional implementation choice, not a confirmed branding decision.

Design principles:

- Nepali-first interface.
- Simple navigation and understandable agricultural language.
- Readable typography and correct Nepali font rendering.
- Adequate contrast and touch target sizes.
- Accessible controls.
- Clear empty, loading, error and offline states.
- Responsive layouts.
- No unnecessary animation or decorative complexity.
- No fake content presented as real data.

---

# 12. SEARCH AND DATA PERFORMANCE

Search must support Nepali Unicode. Test actual Nepali queries, including normalization and common spacing/character variations where relevant.

Evaluate Room/SQLite FTS5 for the actual Nepali search requirements. Do not assume it works perfectly without testing. If necessary, implement an indexed search strategy and document its limitations.

Use:

- Indexes for common filtering and foreign-key relationships.
- Pagination for long lists.
- Bounded network requests.
- Room as the read source for cached content.
- Efficient image caching.
- Avoidance of unnecessary full-table scans.

Do not fetch every record every time a user opens the application.

---

# 13. DEVELOPMENT WORKFLOW AND ENVIRONMENT

The chosen workflow is:

- **Antigravity:** primary coding agent and repository work.
- **Android Studio:** Gradle synchronization, build/run, emulator and physical-device testing.

Avoid making Android Studio the only required editor workflow. Keep command-line Gradle tasks usable where practical.

Avoid unnecessary Docker requirements. Prefer a straightforward local Django setup and documented environment configuration.

Provide `.env.example` files containing variable names and safe placeholders only. Never include real secrets.

Suggested environment variables should be documented, for example:

- Supabase project URL.
- Supabase publishable/anon key where appropriate for the client.
- Server-side database host, port, database name, username and password.
- Django secret key.
- Allowed hosts and CSRF trusted origins.
- OTP development-mode configuration.
- Production OTP provider credentials, when eventually provisioned.
- External weather/market API credentials, when selected.

Do not invent values for these variables.

---

# 14. TESTING AND QUALITY ASSURANCE

Tests are mandatory. Do not claim tests passed unless they were run.

## 14.1 Android tests

Test as much as the project structure reasonably supports:

- Phone number validation.
- MPIN validation and authentication behavior.
- Session persistence and logout.
- Farmer data isolation.
- Farm repository behavior.
- Multiple farms per farmer.
- Multiple plots per farm.
- Crop retrieval and search.
- Nepali Unicode search.
- Guide retrieval.
- Room database reads/writes.
- Offline cached data availability.
- Offline write blocking.
- Content synchronization and failure handling.

## 14.2 Django tests

Test:

- Administrator login and logout.
- Super Admin access.
- Content Admin access restrictions.
- Crop create/read/update/delete.
- Guide create/read/update/delete.
- Publish/unpublish behavior.
- Database connectivity.
- Input validation.
- Authentication protection.
- Authorization failures.

## 14.3 Security tests

Verify:

- Farmer A cannot access Farmer B's profile, farms or plots.
- Farmer A cannot modify Farmer B's data.
- Content Admin cannot perform Super Admin-only actions.
- Unauthenticated requests cannot modify protected data.
- Privileged credentials do not appear in Android assets or browser bundles.
- Incorrect MPIN attempts are rate-limited or otherwise safely constrained.
- OTP testing mode cannot silently be used as production authentication.

## 14.4 End-to-end acceptance workflow

Verify this workflow:

1. Register a farmer using the development OTP mode.
2. Verify the test OTP.
3. Set a four-digit MPIN.
4. Log in using phone number and MPIN.
5. Create or complete the farmer profile.
6. Select a location using GPS or manual selection.
7. Create a farm.
8. Add a plot to that farm.
9. Browse the crop catalogue.
10. Open an agricultural guide.
11. Search using Nepali text.
12. Synchronize content.
13. Disable internet connectivity.
14. Reopen the application.
15. Confirm previously synchronized content remains accessible.
16. Confirm all write actions are blocked while offline.
17. Restore internet connectivity and confirm permitted content refresh works.

Also verify that a second farmer account cannot access the first farmer's private information on the same device.

---

# 15. DEPLOYMENT AND DISTRIBUTION

## 15.1 Android distribution

Required prototype deliverables:

- Debug APK.
- Successful installation on a physical Android device.
- Successful run on an Android emulator.
- Verification of minimum SDK 26.
- Offline reading verification.

Google Play internal testing is desired, but the user does not currently have a Play Console developer account. Do not allow registration/verification delays to block APK delivery. Document the remaining Play Console setup and publishing tasks. Never claim a Play release has occurred unless verified.

## 15.2 Django hosting

The intended deployment preference is a free online hosting option.

Before choosing a provider, verify its current:

- Free-tier terms and limits.
- Persistent storage policy.
- PostgreSQL connectivity.
- Environment-variable support.
- Python/Django support.
- Idle/sleep behavior.
- TLS/HTTPS support.

Do not assume a service has a permanent free plan. If online deployment is blocked, provide a functioning local deployment and document the exact limitation.

## 15.3 Database deployment

Use Supabase for the shared PostgreSQL database. Apply schema changes using the documented migration workflow. Verify RLS and restricted Django database access against the deployed project.

---

# 16. 24-HOUR IMPLEMENTATION PLAN

Use this order. Adjust based on actual repository state, but preserve the priority order.

## Phase 1: Inspection and foundation

- Inspect workspace and repository.
- Identify existing working code and assets.
- Create/update requirements and architecture documents.
- Establish Android and Django project foundations.
- Configure Supabase environment and migration strategy.

## Phase 2: Database and administration backend

- Design and document PostgreSQL schema.
- Create migrations.
- Configure RLS and grants.
- Configure Django database connection.
- Implement admin login and roles.
- Implement crop and guide management.
- Build a usable dashboard.

## Phase 3: Android foundation

- Configure Kotlin and Jetpack Compose.
- Verify Gradle/dependency compatibility.
- Configure Hilt.
- Configure typed navigation.
- Configure Room.
- Configure Supabase SDK and Ktor.
- Establish practical Clean Architecture/MVVM boundaries.

## Phase 4: Core farmer workflow

- Authentication and MPIN flow.
- Farmer profile and location selection.
- Multiple farms and plots.
- Crop catalogue and details.
- Agricultural guides.
- Nepali search.

## Phase 5: Offline reading and synchronization

- Room cache.
- Network monitoring.
- Offline read-only UI behavior.
- Background content refresh.
- Sync failure and retry handling.

## Phase 6: Integration and verification

- Connect Android and Django to the shared cloud database.
- Verify farmer data isolation and administrator roles.
- Verify publication visibility.
- Run core end-to-end workflow.
- Fix critical failures.

## Phase 7: Build and handoff

- Gradle sync.
- Debug APK build.
- Physical-device test.
- Emulator test.
- Run available automated tests.
- Complete documentation.
- Provide a factual implementation report.

**Priority rule:** A functioning authenticated farmer workflow, content administration and offline reading are more important than weather, market data, advanced notifications or other secondary features.

---

# 17. ACCEPTANCE CRITERIA

The prototype is not complete just because the Android project compiles.

## 17.1 Farmer application

- App launches.
- Nepali Unicode text displays correctly.
- Development registration/OTP flow works and is clearly labelled non-production.
- MPIN setup works.
- Normal login uses phone + MPIN according to the documented secure architecture.
- Sessions persist securely.
- Logout works.
- Separate farmer accounts remain isolated.
- Profile and location functions work online.
- Multiple farms and plots work.
- Crop catalogue is connected to the cloud data source.
- Guides are connected to the cloud data source.
- Nepali search works.
- Room caches agricultural content.
- Cached information remains readable offline.
- Offline write actions are blocked.

## 17.2 Administration dashboard

- Django dashboard loads.
- Administrator can log in and out.
- Super Admin and Content Admin authorization works.
- Authorized administrator can manage crops and guides.
- Publication status is enforced.
- Published content becomes visible to the farmer application after permitted synchronization.
- Administrator actions are appropriately logged.

## 17.3 Infrastructure and delivery

- Supabase migrations exist and apply successfully.
- RLS policies and permissions are documented and tested.
- Django and Android use the same cloud database.
- Secrets are not committed or exposed to clients.
- APK builds successfully.
- Physical-device and emulator tests are documented.
- Setup instructions are complete enough for another developer to reproduce the environment.

---

# 18. REQUIRED DOCUMENTATION AND DELIVERABLES

Create or update these files in the repository:

```text
docs/
├── MASTER_IMPLEMENTATION_PROMPT.md
├── REQUIREMENTS.md
├── INTERVIEW_RESULTS.md
├── ARCHITECTURE.md
├── DATABASE_SCHEMA.md
├── AUTHENTICATION.md
├── OFFLINE_SYNC.md
├── API_CONTRACTS.md
├── SECURITY.md
├── UI_DESIGN.md
├── TEST_PLAN.md
├── DEPLOYMENT.md
├── IMPLEMENTATION_PLAN.md
└── KNOWN_LIMITATIONS.md
```

Also provide:

- Android project source.
- Django project source.
- Supabase SQL migrations.
- Safe seed/demo data where needed.
- RLS and database permission policies.
- `.env.example` files.
- README setup instructions.
- Debug APK.
- Test results.
- Deployment instructions.
- A clear list of incomplete features and blockers.

Do not create documentation that contradicts the actual implementation. Update the documents as implementation decisions change.

---

# 19. FINAL REPORT FORMAT

At the end of the work, produce a factual report with these sections:

1. **Implemented and tested** — features that were actually run and verified.
2. **Implemented but not fully tested** — code exists, but verification is incomplete.
3. **Partially implemented** — describe the missing pieces.
4. **Not implemented** — list deferred features.
5. **External blockers** — missing provider credentials, hosting restrictions, Play Console registration or missing assets.
6. **Build artifacts** — exact APK path and build variant, only if generated.
7. **Test results** — actual commands executed and pass/fail counts.
8. **How to run** — commands and setup steps for Android and Django.
9. **Security notes** — known risks and mitigations.
10. **Next recommended work** — the highest-priority remaining tasks.

Use precise status language. Never describe a simulated OTP as a delivered OTP, demo data as verified agricultural data, or an untested deployment as live.

---

# 20. NON-NEGOTIABLE DEVELOPMENT RULES

1. Do not fabricate functionality.
2. Do not use static mock data where live database integration is required, except clearly labelled demonstration seed content.
3. Do not claim an API is connected before testing it.
4. Do not invent Supabase credentials or OTP providers.
5. Do not fabricate agricultural advice, pesticide dosage, weather or market prices.
6. Do not replace Kotlin with Flutter or another framework.
7. Do not replace Django with another backend.
8. Do not replace Supabase with Firebase.
9. Do not use Bootstrap.
10. Do not add video features.
11. Do not implement image detection.
12. Do not remove offline read-only behavior.
13. Do not add offline mutation queues.
14. Do not add unapproved product features.
15. Do not restructure working project code without inspecting it first.
16. Do not leave primary buttons nonfunctional.
17. Do not hide errors or silently swallow failures.
18. Do not commit secrets.
19. Do not claim tests passed unless they were executed.
20. Do not claim deployment succeeded unless it was verified.
21. Use stable, mutually compatible dependencies and pin versions.
22. Avoid unnecessary dependencies and excessive abstractions.
23. Prefer a practical implementation that can be built, tested and maintained.
24. Keep the 24-hour prototype priorities visible throughout development.

---

# 21. START NOW

Inspect the complete current project workspace.

Then create or update the required documentation, especially:

- `docs/REQUIREMENTS.md`
- `docs/INTERVIEW_RESULTS.md`
- `docs/ARCHITECTURE.md`
- `docs/IMPLEMENTATION_PLAN.md`
- `docs/KNOWN_LIMITATIONS.md`

Record all 61 interview decisions, including later clarifications that exclude image detection and video content. Record Question 59 (the final UI design system) as undecided.

After documenting the current state and plan, continue directly into implementation. Do not stop after planning.

When a real external dependency is missing, implement a safe development alternative, record the limitation and continue with the remaining tasks.

**FINAL OBJECTIVE: Build a real, integrated and testable Krishi Saathi prototype within the 24-hour development window, with a secure farmer workflow, Django content administration, shared Supabase data and reliable offline read-only access.**
