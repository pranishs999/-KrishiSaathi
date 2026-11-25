# Authentication & Access Policy

**Policy:** Mandatory First-Launch Registration Without Login Credentials

## 1. Access Flow
1. On fresh installation, `MainActivity` + `MainViewModel` check both Preferences DataStore (`registration_completed`) and Room (`farmer_registration`).
2. Unregistered installations enter `RegistrationScreen` (`किसान दर्ता`) immediately after splash and cannot navigate past it via system Back or other routes.
3. Registered installations skip registration completely and open `MainScaffold` (`गृहपृष्ठ`) directly across app restarts, activity recreation, rotation, and process termination.

## 2. Restrictions
- **NO** OTP verification.
- **NO** password, PIN, or MPIN.
- **NO** login/logout flow or email authentication.
- **NO** Location, Camera, Contacts, or Microphone permissions requested.
- **NO** Profile editing or account management UI yet (the Profile tab `मेरो विवरण` currently displays an Under Construction screen while preserving the stored registration record).

For full technical details on fields, validation, and atomic persistence, see [REGISTRATION_FLOW.md](file:///home/pranish/AndroidStudioProjects/KrishiSaathi/docs/REGISTRATION_FLOW.md).
