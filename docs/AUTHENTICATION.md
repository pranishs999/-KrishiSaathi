# Authentication & Access Policy

**Policy:** Zero Authentication Gates (Frictionless Open Access)

## Design Decision
Bakaiya Krishi Saathi removes all login, phone OTP, password, and MPIN barriers from app launch and core workflows (`MainActivity` transitions directly from `SplashScreen` to `MainScaffold`).

## Optional Farmer Registration
Farmers may optionally record their local identity details via the Home screen profile card (`RegistrationDialog`):
- **Fields:** Full name (`full_name`), Ward number 1–12 (`ward_number`), Nepal mobile/landline number (`phone_number`, validated by `ValidationUtils.isValidNepalPhoneNumber`).
- **Storage:** Single local row in Room (`farmer_registration`, primary key `"primary_farmer"`).
- **Controls:** Editable and clearable anytime (`clearFarmerRegistration()`).
- **Non-blocking:** All crop guides, environmental data, farm records, YouTube channels, helpline contacts, and offline video playback work fully without registration.

## Legacy Auth Removal
Prior prototype session keys (`is_logged_in`, `user_id`, `mpin`) were removed from `DataStoreManager` (which now retains only `LAST_SYNC_TIMESTAMP`).
