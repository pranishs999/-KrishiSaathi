# UI / UX Guidelines — Bakaiya Krishi Saathi

**Mode:** Material Design 3 — **Light Mode Only** (`darkTheme = false`, `dynamicColor = false`)

## 1. Approved 14-Color Palette (`ui/theme/Color.kt`)
| Token | Hex | Role |
|---|---|---|
| `ForestGreen` | `#174D36` | Primary brand, primary CTA buttons, active chips |
| `NaturalIvory` | `#F6F7EF` | Screen canvas background, primary button text |
| `LightGreen` | `#DCE9DF` | Content card surfaces, secondary containers |
| `SoftSage` | `#E8EDE5` | Search bars, inactive filter chips, guides |
| `DeepCharcoal` | `#26352D` | Primary body text, selected navigation items |
| `MutedGreenGrey` | `#69776D` | Subtitles, metadata, unselected nav items |
| `LeafGreen` | `#6E9877` | Supporting agricultural icon accents |
| `DividerSage` | `#C8D8C9` | Structural 1dp hairlines |
| `PlaceholderText` | `#8A9B8D` | Input field placeholders |
| `SuccessGreen` | `#27834A` | Verified badges, income amounts |
| `WarningOrange` | `#C56B2A` | Seasonal notices, expense indicators |
| `ErrorMaroon` | `#8F3030` | Error messages, destructive actions |
| `DisabledGreen` | `#A8BDAA` | Disabled control states |
| `PureWhite` | `#FFFFFF` | Highlights and contrast surfaces |

## 2. Devanagari-First Typography (`ui/theme/Type.kt`)
Line heights are scaled to ~1.35x–1.5x font size (e.g., `16.sp` body with `25.sp` line height, `21.sp` headline with `30.sp` line height) so upper and lower Devanagari matras (`े, ै, ो, ु, ू, ्र`) never clip.

## 3. Shape, Elevation & Layout Rules
- **Cards:** `RoundedCornerShape(24.dp)`, `elevation = 0.dp`, tonal separation (`LightGreen` / `SoftSage` over `NaturalIvory`). No drop shadows, gradients, or neon colors.
- **Pills & Buttons:** `RoundedCornerShape(50)` with minimum `48.dp` height on primary interactive targets.
- **Navigation (`MainScaffold.kt`):**
  - 4 Bottom Navigation Tabs: `गृहपृष्ठ` (Home), `बालीहरू` (Crops), `फार्म हिसाब` (Farm Records), `हेल्पलाइन` (Helpline).
  - Top bar shortcut icon (`Icons.Outlined.DownloadDone`) opens the Offline Video Library (`downloaded_videos`).
