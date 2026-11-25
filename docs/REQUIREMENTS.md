# Product Requirements — Bakaiya Krishi Saathi (बकैया कृषि साथी)

**Target Community:** Individual farmers of Bakaiya Rural Municipality (बकैया गाउँपालिका), Makwanpur, Nepal  
**Primary Language:** Nepali (Devanagari-first) with English botanical/scientific references

## Scope & Implemented Capabilities

1. **अनिवार्य पहिलो दर्ता (Mandatory First-Launch Farmer Registration):**
   - Collects **किसानको नाम**, fixed **गाउँपालिका (`बकैया गाउँपालिका, मकवानपुर।`)**, **वडा नम्बर (`1–12`)**, and **मोबाइल नम्बर (`+977`)**.
   - Requires no OTP, password, MPIN, login screen, or sensitive Android permissions.
   - Persisted across Room + DataStore so subsequent launches skip registration and open Home directly.
2. **कुन खेती कसरी गर्ने? (Crop Knowledge & 22-Section Guides):**
   - Home screen with crop categories, search bar, seasonal NARC highlights, and featured crops.
   - Crops browser with live Nepali/English/scientific-name search and 6 category filters.
   - 22-section Crop Detail view covering overview, varieties, planting season, altitude, 7 environmental requirement cards, 9 expandable cultivation instructions, photo gallery with fullscreen inspection, and topic-filtered video tutorials across 8 cultivation stages (`VideoTopic`).
3. **कृषि प्रविधि युट्यूब च्यानलहरू (Agricultural YouTube Channels & Videos):**
   - Crop-specific verified YouTube educational videos mapped to 8 cultivation topics.
   - Dedicated `YouTubeChannelsScreen` directory linking to 7 verified Nepali agricultural YouTube channels.
   - Authorized offline MP4 download manager (`VideoDownloadManager`) and offline Media3 ExoPlayer (`VideoPlayerScreen`).
4. **किसान हेल्पलाइन (`HelplineScreen` — Under Construction):**
   - Persistent bottom navigation destination displaying the Under Construction placeholder (`निर्माणाधीन`).
5. **मेरो विवरण (`ProfileScreen` — Under Construction):**
   - Persistent bottom navigation destination displaying the Under Construction placeholder (`निर्माणाधीन`) while preserving registration data safely in Room.

## Out of Scope / Excluded
- Farm Hisab / accounting/bookkeeping (`फार्म हिसाब`), weather forecasting, market prices, plot/farm management, authentication/OTP/MPIN flows, and invented contact data.
