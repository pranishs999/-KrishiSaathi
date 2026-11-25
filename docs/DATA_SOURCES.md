# Data Sources & Provenance Inventory

All pre-seeded records in `BakaiyaSeedData.kt`, `VerifiedYouTubeChannels.kt`, and `supabase/migrations/002_seed_data.sql` map to verifiable public sources:

## 1. Crop Knowledge Sources (8 Crops, 6 Categories)
- `crop-paddy` (धान / Oryza sativa L.) — नेपाल कृषि अनुसन्धान परिषद् (NARC) / कृषि डायरी
- `crop-maize` (मकै / Zea mays L.) — राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC)
- `crop-cauliflower` (काउली / Brassica oleracea var. botrytis) — राष्ट्रिय तरकारी बाली विकास केन्द्र / कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC)
- `crop-tomato` (गोलभेंडा / Solanum lycopersicum L.) — नेपाल कृषि अनुसन्धान परिषद् (NARC) बागवानी महाशाखा
- `crop-ginger` (अदुवा / Zingiber officinale Rosc.) — राष्ट्रिय अदुवा बाली अनुसन्धान कार्यक्रम, कपुरकोट (NARC)
- `crop-mustard` (तोरी / Brassica campestris var. toria) — तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC)
- `crop-lentil` (मसुरो / Lens culinaris) — कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC)
- `crop-banana` (केरा / Musa spp.) — नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय

## 2. Crop Reference Images (6 Wikimedia Commons Images)
- Paddy (`img-paddy-1`): IRRI / Wikimedia Commons `Rice_plants_(IRRI).jpg`
- Maize (`img-maize-1`): Wikimedia Commons `Zea_mays_crop.jpg`
- Cauliflower (`img-cauliflower-1`): Wikimedia Commons `Chou-fleur_02.jpg`
- Tomato (`img-tomato-1`): Wikimedia Commons `Tomato_je.jpg`
- Ginger (`img-ginger-1`): Wikimedia Commons `Ginger_plant.jpg`
- Mustard (`img-mustard-1`): Wikimedia Commons `Mustard_field_in_Nepal.jpg`

## 3. Verified YouTube Videos (10 Videos) & Sample Offline Media (2 MP4s)
- **YouTube Watch URLs (`provider = "youtube"`, `downloadPermission = false`):**
  - `OflsI4icQGY` (Krishi Patrika — धानको ब्याड)
  - `zIczJuB2N2M` (Krishi Loksewa — धान खेती प्रविधि)
  - `ttjs2dSpI3A` (NTV NEWS NEPAL — धान खेती र झारपात व्यवस्थापन)
  - `AMoKn2J8fFA` (LAB TV NEPAL — मकै गोड्दै)
  - `Fl8IKosrGqM` (Agro Chiran — मकैमा झारपात नियन्त्रण)
  - `NzwRsir1-ps` (AITC NEPAL — काउली खेती प्रविधि)
  - `aEtHCR-b77o` (Krishi Television HD — काउली, ब्रोकाउली, बन्दाकाउली)
  - `F0iA4iceI-0` (Krishi Television HD — गोलभेंडा बीउ र उत्पादन)
  - `zl1ampj04Es` (Krishna Budhathoki — अदुवा खेती)
  - `CB3o_HuWwQs` (AITC NEPAL — तोरी र मकै खेती)
- **Sample Authorized Offline Test Videos (`provider = "authorized_local"`, `downloadPermission = true`, `isSampleMedia = true`):**
  - `ForBiggerBlazes.mp4` (`vid-paddy-sample-offline`)
  - `ForBiggerEscapes.mp4` (`vid-maize-sample-offline`)

## 4. Verified Nepali Agricultural YouTube Channels (7 Channels)
Listed in `VerifiedYouTubeChannels.channels` and verified via HTTPS:
- कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal) — `@iiaitcnepal2768`
- कृषि टेलिभिजन एचडी (Krishi Television HD) — `@KrishiTelevisionHD1`
- NTV NEWS NEPAL — `@ntvnewsnepal`
- कृषि पत्रिका (Krishi Patrika) — `@KrishiPatrika`
- LAB TV NEPAL — `@LABTVNEPAL`
- एग्रो चिरान (Agro Chiran) — `@AgroChiran`
- कृषि लोकसेवा तयारी (Krishi Loksewa) — `@krishiloksewa`

## 5. Official Bakaiya Helpline Contacts (2 Contacts)
Publicly listed on `bakaiyakrishi.gov.np`:
- हिमश्वेता भट्ट — आर्थिक विकास शाखा प्रमुख / सूचना अधिकारी (`9868445081`, `bakaiyaagri@gmail.com`)
- बकैया गाउँपालिका कार्यपालिकाको कार्यालय — मुख्य कार्यालय (`9855088066`, `bakaiyagaunpalika@gmail.com`)
