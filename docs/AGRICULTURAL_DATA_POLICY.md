# Agricultural Data Policy

**Project:** Bakaiya Krishi Saathi (बकैया कृषि साथी)  
**Target Region:** Bakaiya Rural Municipality, Makwanpur, Nepal

## Core Integrity Rules

1. **Verified Sources Only:** All agronomic packages (varieties, temperature/soil pH bounds, cultivation stages) align with published guidance from the **Nepal Agricultural Research Council (NARC)**, **Agriculture Information and Training Center (AITC Nepal)**, and **Ministry of Agriculture and Livestock Development (MoALD)**.
2. **Never Invent Missing Data:** Optional environmental fields (`tempMin`, `tempMax`, `humidityMin`, `humidityMax`, `soilPhMin`, `soilPhMax`) are declared nullable in both `BakaiyaCropEntity` and `BakaiyaCrop`. Where cultivar-standardized percentages or ranges are unavailable (for instance `humidityMin`/`humidityMax`, or lentil temperature/pH bounds in the current dataset), values remain `null` and UI renders honest availability text rather than synthetic numbers.
3. **Strict Fertilizer & Chemical Responsibility:** Fertilizer descriptions reference balanced organic compost + NPK guidance alongside explicit advice to determine exact doses from soil testing (`माटो परीक्षण प्रतिवेदन अनुसार मात्रा निर्धारण गर्नु उत्तम हुन्छ`) and consult local agriculture technicians for chemical pesticides.
4. **Source Attribution:** Every crop record carries a mandatory `source_reference` and `is_verified_content` flag displayed in `CropDetailScreen`.
5. **Helpline Contact Verification:** `helpline_contacts` contains only publicly verified official contacts from `bakaiyakrishi.gov.np`. Individual ward technician mobile numbers are NOT invented; selecting a ward without an entry renders an informative empty state directing farmers to the central agriculture branch.
6. **Sample Media Transparency:** Offline downloadable test videos (`ForBiggerBlazes.mp4`, `ForBiggerEscapes.mp4`) carry `isSampleMedia = true` and clear Nepali labels explaining they are sample test videos demonstrating Media3 ExoPlayer offline playback.
