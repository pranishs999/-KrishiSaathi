# Development Status — बकैया कृषि साथी (Bakaiya Krishi Saathi)

**Version:** v1.1-revision  
**Platform:** Android (Kotlin, Jetpack Compose, Material 3 Light Mode Only)

---

## 1. Implemented & Active Features

1. **Mandatory First-Launch Farmer Registration (`RegistrationScreen`):**
   - Collects Name, fixed Municipality (`बकैया गाउँपालिका, मकवानपुर।`), Ward dropdown (1–12), and Nepal mobile number (`+977`), with no OTP, password, MPIN, or extra permissions.
   - Persists record in Room (`farmer_registration`) and completion flag in DataStore (`registration_completed`).
2. **Home Dashboard (`HomeScreen`):**
   - Welcome banner, instant crop search shortcut, 6 Nepali crop categories, quick shortcuts (`डाउनलोड भिडियो`, `कृषि युट्यूब`), NARC seasonal cultivation highlights, and featured crops.
3. **Crops Knowledge Base (`CropsScreen` & `CropDetailScreen`):**
   - Live Nepali/English/scientific search and category filters across 8 Bakaiya crops (`धान`, `मकै`, `काउली`, `गोलभेंडा`, `अदुवा`, `तोरी`, `मसुरो`, `केरा`).
   - 22-section cultivation guide, environmental requirement cards, Wikimedia Commons photo gallery, and 8-topic video tutorials.
4. **Educational Videos & Offline Media (`DownloadedVideosScreen`, `VideoPlayerScreen`, `YouTubeChannelsScreen`):**
   - 7 verified Nepali agricultural YouTube channels (`YouTubeLauncher`).
   - Strict separation of YouTube links (external launch only) vs authorized HTTPS downloads (`VideoDownloadManager` + Media3 ExoPlayer offline playback).

## 2. Under Construction Screens (`UnderConstructionScreen.kt`)

| Destination | Route | Title | Icon (`ForestGreen`) | Heading & Message |
|---|---|---|---|---|
| **Profile** | `profile` | `मेरो विवरण` | `Icons.Outlined.Construction` | `निर्माणाधीन` — *"यो सुविधा हाल निर्माणको क्रममा छ। चाँडै उपलब्ध हुनेछ।"* |
| **Farmer Helpline** | `helpline` | `किसान हेल्पलाइन` | `Icons.Outlined.SupportAgent` | `निर्माणाधीन` — *"कृषि सम्बन्धी सम्पर्क तथा सहयोग विवरण चाँडै उपलब्ध हुनेछ।"* |

Neither Under Construction page exposes fake phone numbers, fake agricultural officer profiles, call buttons, profile editing, or authentication UI. Locally stored registration records remain preserved in Room for future Profile features.

## 3. Removed Features

- **Farm Hisab (`फार्म हिसाब` / `farm_records`):** Completely removed from UI (`FarmRecordsScreen`, `FarmRecordsViewModel`, Home shortcut), bottom navigation, `NavHost`, `BakaiyaCropRepository`, `BakaiyaCropDao`, `FarmRecord`, `FarmRecordEntity`, and Room schema (`KrishiSaathiDatabase` v6 includes `MIGRATION_5_6` dropping `farm_records` cleanly without deleting unrelated tables or downloaded videos).
- **Optional Profile Dialog (`RegistrationDialog`):** Replaced by mandatory first-launch `RegistrationScreen` and removed from `HomeScreen`.
