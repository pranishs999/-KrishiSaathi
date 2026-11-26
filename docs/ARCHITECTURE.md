# System Architecture — बकैया कृषि साथी (Bakaiya Krishi Saathi)

## 1. End-to-End Platform Architecture

```
+-------------------------------------------------------------------+
|            Bakaiya Krishi Saathi Admin (admin_web/)               |
|     English-Only Responsive Web CMS (Django 6.1 + Pillow 12.3)    |
|  - Roles: Super Admin | Content Editor | Technical Reviewer       |
|  - Workflow: Draft -> Needs Source -> In Review -> Approved -> Pub|
|  - Pillow Asset Pipeline (full / medium / thumb + license gate)   |
+-------------------------------+-----------------------------------+
                                | Server-Only Credentials / RLS / Triggers
                                v
+-------------------------------------------------------------------+
|      Authoritative Database & Media Store (Supabase Postgres)     |
|  - Normalized Crop, Livestock, Media, Source, Review & Admin Schema|
|  - Strict RLS: Public/Anon READ ONLY `review_status = 'published'`|
|  - Content Version History & Audit Logs                           |
+-------------------------------+-----------------------------------+
                                | HTTPS Published Content REST / PostgREST
                                v
+-------------------------------------------------------------------+
|        Farmer Android App (com.krishisaathi.app - Kotlin/Compose) |
|  - 4-Tab Nav: गृहपृष्ठ | बाली | पशुपालन | किसान हेल्पलाइन          |
|  - Non-blocking open access + "निशुल्क कृषि प्राविधिक सेवा" Icon   |
|  - Local Offline Cache: Room DB (v8) + Bounded Coil + ExoPlayer   |
+-------------------------------------------------------------------+
```

## 2. Single Authoritative Content Rule
- There is **one published-content state**: records must reach `review_status = 'published'` after technical review and source linkage before appearing in any public API response or syncing down to the Android app.
- Draft, `needs_source`, `in_review`, `rejected`, `needs_re_review`, and `archived` records are blocked by PostgreSQL RLS policies and server API query filters.
- Android persists synchronized published datasets into local Room SQLite (`KrishiSaathiDatabase`) so farmers retain complete offline access without internet connectivity, pruning archived items cleanly on subsequent syncs without removing valid local offline video files.

## 3. Android Layers (`app/src/main/java/com/krishisaathi/app/`)
- **UI Layer (Jetpack Compose, M3 Light Mode Only):**
  - `MainActivity` -> `SplashScreen` -> `MainScaffold` (zero login/registration gates).
  - 4 Bottom Navigation tabs (`BottomNavItem`: `Home`, `Crops`, `Livestock`, `Helpline`).
  - `HomeScreen` featuring search, crop & livestock category previews, optional non-blocking farmer profile card, **`निशुल्क कृषि प्राविधिक सेवा`** free technical service icon card, seasonal highlights, and video/channel shortcuts.
  - `CropsScreen` & `CropDetailScreen` (22 sections + Growing Conditions + Variety selector + Photo Gallery + Topic Video Learning).
  - `LivestockScreen` & `AnimalDetailScreen` (extensible species/categories + Breeds + 25 dynamic sections + Veterinary Safety Notice + Image Gallery + Educational Videos).
  - `HelplineScreen` (verified agricultural & veterinary contacts + `Intent.ACTION_DIAL`).
  - `YouTubeChannelsScreen`, `DownloadedVideosScreen`, `VideoPlayerScreen`.
- **Domain & Data Layer:**
  - `BakaiyaCropRepository` (crops, varieties, crop images, crop/animal videos, playlists, helpline, optional registration).
  - `LivestockRepository` / extended repository API (animal categories, animals, breeds, sections, housing, feeding, breeding, health, diseases, production, animal images, sync).
  - `RemoteContentSyncClient` (HTTPS PostgREST / Public CMS API client).
  - Room Database (`KrishiSaathiDatabase` v8 with incremental migrations `5->6`, `6->7`, `7->8`).
