# Product Requirements — Bakaiya Krishi Saathi (बकैया कृषि साथी)

**Target Community:** Individual farmers of Bakaiya Rural Municipality (बकैया गाउँपालिका), Makwanpur, Nepal  
**Primary Language:** Nepali (Devanagari-first) with English botanical/scientific references

## Implemented Capabilities

1. **कुन खेती कसरी गर्ने? (Crop Knowledge & 22-Section Guides):**
   - Home screen with crop categories, search bar, seasonal NARC highlights, and featured crops.
   - Crops browser with live Nepali/English/scientific-name search and 6 category filters.
   - 22-section Crop Detail view covering overview, varieties, planting season, altitude, 7 environmental requirement cards (temperature, humidity, soil types, pH, sunlight, water requirement, altitude), 9 expandable cultivation instructions, photo gallery with fullscreen inspection, and topic-filtered video tutorials across 8 cultivation stages (`VideoTopic`).
2. **फार्मको हिसाब कसरी राख्ने? (Farm Record Keeping):**
   - Interactive income/expense ledger (`FarmRecordsScreen`) with Nepali categories (बीउ बिजन, मलखाद, मजदुरी, बाली बिक्री, etc.), optional crop tagging, filter chips (सबै / आम्दानी / खर्च), net profit/loss balance card, delete confirmation, and an inline expandable Nepali bookkeeping guide.
3. **कृषि प्रविधि युट्यूब च्यानलहरू (Agricultural YouTube Channels & Videos):**
   - Crop-specific verified YouTube educational videos mapped to 8 cultivation topics.
   - Dedicated `YouTubeChannelsScreen` directory linking to 7 verified Nepali agricultural YouTube channels (AITC Nepal, Krishi Television HD, NTV NEWS NEPAL, Krishi Patrika, LAB TV NEPAL, Agro Chiran, कृषि लोकसेवा तयारी).
   - Authorized offline MP4 download manager (`VideoDownloadManager`) and offline Media3 ExoPlayer (`VideoPlayerScreen`) with clear sample-media disclosures.
4. **किसान हेल्पलाइन (Farmer Helpline Directory):**
   - Official Bakaiya Rural Municipality contacts with ward filtering (Central Agriculture Branch + Wards 1–12), verified badges, email display, safe `ACTION_DIAL`, and non-deceptive empty states.
5. **ऐच्छिक किसान विवरण (Optional Farmer Registration):**
   - Simple 3-field local registration (Name, Ward 1–12, Phone) without passwords, OTP, or login gates.
