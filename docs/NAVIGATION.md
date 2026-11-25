# Application Navigation Architecture — बकैया कृषि साथी

## 1. Entry Gate (`MainActivity` + `MainViewModel`)

`MainActivity` observes `MainViewModel.entryState` (`Loading` / `NeedsRegistration` / `Registered`) coordinated with `SplashScreen`:

- **Loading / Splash (`RootStage.SPLASH`):** Holds on `SplashScreen` until DataStore and Room emit initial state, preventing any premature flash of Home or Registration.
- **Unregistered (`RootStage.REGISTRATION`):** Displays `RegistrationScreen`. `MainScaffold` and main navigation tabs are not mounted, preventing bypass via system Back or deep links prior to registration.
- **Registered (`RootStage.MAIN`):** Displays `MainScaffold` directly on Home (`गृहपृष्ठ`). System Back from Home never returns to registration.

## 2. Primary Bottom Navigation (`BottomNavItem.kt` & `MainScaffold.kt`)

Exactly four bottom navigation destinations are configured with Material Symbols Outlined icons and light-mode colors (`DeepCharcoal` selected, `LightGreen` indicator, `MutedGreenGrey` unselected over `NaturalIvory`):

| Order | Route | Nepali Label | Icon (`Icons.Outlined`) | Status |
|---|---|---|---|---|
| 1 | `home` | `गृहपृष्ठ` | `Home` | Active |
| 2 | `crops` | `बालीहरू` | `Agriculture` | Active |
| 3 | `helpline` | `किसान हेल्पलाइन` | `SupportAgent` | Under Construction |
| 4 | `profile` | `मेरो विवरण` | `Person` | Under Construction |

**Top App Bar Shortcut:** The `Icons.Outlined.DownloadDone` action button opens `downloaded_videos`.

## 3. Secondary Routes (`KrishiSaathiNavHost.kt`)

| Route | Arguments | Destination | Status |
|---|---|---|---|
| `crops?categoryId={categoryId}` | Optional `categoryId: String?` | `CropsScreen` | Active |
| `crop_detail/{cropId}` | Required `cropId: String` | `CropDetailScreen` | Active |
| `downloaded_videos` | None | `DownloadedVideosScreen` | Active |
| `youtube_channels` | None | `YouTubeChannelsScreen` | Active |
| `video_player/{videoId}` | Required `videoId: String` | `VideoPlayerScreen` | Active |

## 4. Removed Navigation Destinations

- `farm_records` (`फार्म हिसाब` / Farm Hisab) has been completely removed from `BottomNavItem`, `MainScaffold`, `KrishiSaathiNavHost`, and `HomeScreen`.
