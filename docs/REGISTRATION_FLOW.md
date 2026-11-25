# Farmer Registration Flow — बकैया कृषि साथी (Bakaiya Krishi Saathi)

**Policy:** Mandatory First-Launch Registration (Zero OTP / Password / MPIN Authentication)

---

## 1. Startup & First-Launch Navigation Flow

```text
Application Launch (MainActivity)
       |
       v
SplashScreen + MainViewModel (Persistent Check)
       |
       +---- Not Registered (DataStore flag false or Room record missing)
       |          |
       |          v
       |    RegistrationScreen (Mandatory Entry Gate; Back cannot bypass to Home)
       |          |
       |          v
       |    Validate Fields (FarmerRegistrationCoordinator)
       |          |
       |          v
       |    Commit Room Record (`farmer_registration`)
       |          |
       |          +-- On Failure: Show error in Nepali; do NOT set DataStore completed
       |          +-- On Success: Set DataStore `registration_completed = true`
       |                            |
       |                            v
       |                        MainScaffold -> HomeScreen (`गृहपृष्ठ`)
       |
       +---- Already Registered (DataStore `registration_completed = true` AND Room record exists)
                  |
                  v
            MainScaffold -> HomeScreen (`गृहपृष्ठ`) directly
```

## 2. Registration Form Fields & Validation

| Field | Label (Nepali) | Component / Input | Validation Rule |
|---|---|---|---|
| **Farmer Name** | `किसानको नाम` | Required single-line text input (`आफ्नो पूरा नाम लेख्नुहोस्।`) | Non-blank trimmed text |
| **Municipality** | `गाउँपालिका` | Fixed read-only field (`बकैया गाउँपालिका, मकवानपुर।`) | Fixed to Bakaiya Rural Municipality |
| **Ward Number** | `वडा नम्बर` | Required `ExposedDropdownMenuBox` selector | Wards `1..12` (`BakaiyaMunicipality.WARDS`) |
| **Mobile Number** | `मोबाइल नम्बर` | Required numeric input (`KeyboardType.Number`) with `+977` prefix | 10-digit Nepali mobile starting with `98`, `97`, or `96` (supports Devanagari numerals ०-९) |

- **Primary Submit Button:** `दर्ता गर्नुहोस्` (Forest Green `#174D36` container, Natural Ivory `#F6F7EF` label, pill shape `RoundedCornerShape(50)`, minimum height `48.dp`).
- **Privacy Notice:** Clearly explains that collected fields are stored locally on-device solely to organize agricultural information services for Bakaiya farmers, with **no** Location, Camera, Contacts, or Microphone permissions requested.

## 3. Dual-Layer Persistence Strategy

1. **Room SQLite (`KrishiSaathiDatabase` v6 — `farmer_registration` table):**
   - Stores `id = "primary_farmer"`, `full_name`, `municipality`, `ward_number`, `phone_number`, and `registered_at` timestamp.
   - `BakaiyaCropRepositoryImpl.saveFarmerRegistration` validates fields, inserts via `BakaiyaCropDao.saveFarmerRegistration`, and verifies persistence with `findFarmerRegistrationSync()` before returning `Result.success`.
2. **Preferences DataStore (`DataStoreManager` / `RegistrationPreferences`):**
   - Stores boolean key `registration_completed` in `bakaiya_krishi_saathi_prefs`.
   - Set to `true` **only** inside `FarmerRegistrationCoordinator.registerFarmer(...)` after Room persistence succeeds. If either Room or DataStore fails, `isCompleted` remains `false` and the farmer stays on `RegistrationScreen` with an error notice.
3. **Process Recreation & Rotation Resilience:**
   - Unsubmitted form text and selected ward are retained across device rotation and process death using `SavedStateHandle` inside `RegistrationViewModel`.
   - Once registered, `MainViewModel.entryState` emits `AppEntryState.Registered` from persistent Room + DataStore storage on every restart, background/foreground switch, rotation, and process recreation so `RegistrationScreen` never reappears on ordinary launches.
