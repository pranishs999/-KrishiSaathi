-- Migration: 002_seed_data.sql
-- Bakaiya Krishi Saathi — Verified Agricultural Content
-- Aligned with BakaiyaSeedData.kt Room seed data
-- ============================================================================

-- 1. CROP CATEGORIES
INSERT INTO public.crop_categories (id, name_ne, name_en, icon_name) VALUES
('cat-cereal', 'खाद्यान्न बाली', 'Cereals & Food Grains', 'grain'),
('cat-vegetable', 'तरकारी बाली', 'Vegetables', 'eco'),
('cat-fruit', 'फलफूल बाली', 'Fruits', 'park'),
('cat-pulse', 'दलहन बाली', 'Pulses & Legumes', 'spa'),
('cat-oilseed', 'तेलहन बाली', 'Oilseeds', 'water_drop'),
('cat-cash', 'मसला तथा नगदे बाली', 'Spices & Cash Crops', 'storefront')
ON CONFLICT (id) DO UPDATE SET
    name_ne = EXCLUDED.name_ne,
    name_en = EXCLUDED.name_en;

-- 2. CROPS (8 verified crops matching Room seed data)
INSERT INTO public.crops (id, category_id, name_ne, name_en, scientific_name, description_ne,
    varieties, planting_season, source_reference, is_verified_content, is_published) VALUES
('crop-paddy', 'cat-cereal', 'धान', 'Paddy / Rice', 'Oryza sativa L.',
    'धान बकैया गाउँपालिकाको तल्लो उपत्यका तथा खोला किनारका फाँटहरूको प्रमुख खाद्यान्न बाली हो।',
    'सावित्री, राधा-४, रामधान, हर्दिनाथ-३, सुख्खा धान-३',
    'असार १ देखि साउन १५ सम्म (बर्खे धान)',
    'नेपाल कृषि अनुसन्धान परिषद् (NARC) / कृषि डायरी', true, true),
('crop-maize', 'cat-cereal', 'मकै', 'Maize / Corn', 'Zea mays L.',
    'मकै बकैया गाउँपालिकाको मध्य तथा उच्च पहाडी पाखो बारीको प्रमुख खाद्यान्न बाली हो।',
    'देउती, शितला, मनकामना-३, रामपुर कम्पोजिट, पोषिलो मकै-१',
    'फागुन १५ देखि वैशाख १५ (वर्षे मकै)',
    'राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC)', true, true),
('crop-cauliflower', 'cat-vegetable', 'काउली', 'Cauliflower', 'Brassica oleracea var. botrytis',
    'काउली बकैया गाउँपालिकाका कृषकहरूको प्रमुख व्यावसायिक तरकारी बाली हो।',
    'काठमाडौँ स्थानीय, स्नो मिस्टिक, सिल्भर कप',
    'भदौ देखि कात्तिक (मुख्य हिउँदे याम)',
    'राष्ट्रिय तरकारी बाली विकास केन्द्र / AITC', true, true),
('crop-tomato', 'cat-vegetable', 'गोलभेंडा', 'Tomato', 'Solanum lycopersicum L.',
    'गोलभेंडा बकैया क्षेत्रको प्रमुख नगदे तरकारी बाली हो।',
    'सिर्जना (NARC हाइब्रिड), मनप्रिया, सम्झना',
    'तल्लो क्षेत्रमा भदौ-असोज र माघ-फागुन',
    'नेपाल कृषि अनुसन्धान परिषद् (NARC) बागवानी महाशाखा', true, true),
('crop-ginger', 'cat-cash', 'अदुवा', 'Ginger', 'Zingiber officinale Rosc.',
    'अदुवा बकैयाका पहाडी पाखो तथा छहारीयुक्त बारीहरूमा किसानहरूले उत्पादन गर्ने प्रमुख मसला तथा नगदे बाली हो।',
    'कपुरकोट अदुवा-१, कपुरकोट अदुवा-२, स्थानीय बोसे र नसे',
    'चैत मध्यदेखि वैशाख अन्त्यसम्म',
    'राष्ट्रिय अदुवा बाली अनुसन्धान कार्यक्रम, कपुरकोट (NARC)', true, true),
('crop-mustard', 'cat-oilseed', 'तोरी', 'Mustard / Rapeseed', 'Brassica campestris var. toria',
    'तोरी बकैयाका धान काटेपछिका खेतबारीमा हिउँद याममा लगाइने प्रमुख तेलहन बाली हो।',
    'विकास, उन्नति, प्रगति, प्रीति, मोरङ तोरी-२',
    'असोज १५ देखि कात्तिक १५ सम्म',
    'तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC)', true, true),
('crop-lentil', 'cat-pulse', 'मसुरो', 'Lentil', 'Lens culinaris',
    'मसुरो हिउँदमा धान खेतको चिस्यानमा वा बारीमा लगाइने प्रमुख दलहन बाली हो।',
    'शिमल, शिखर, सगुन, सिन्दुर, शिशिर',
    'कात्तिक (धान काटिसकेपछि)',
    'कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC)', true, true),
('crop-banana', 'cat-fruit', 'केरा', 'Banana', 'Musa spp.',
    'केरा बकैयाका न्यानो तल्लो उपत्यका र खोला किनारका बस्तीहरूमा वर्षैभरि उत्पादन र आयआर्जन दिने प्रमुख फलफूल बाली हो।',
    'मालभोग, विलियम हाइब्रिड, जी-९, झापाली मालभोग',
    'फागुन-चैत र असार-साउन',
    'नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय', true, true)
ON CONFLICT (id) DO UPDATE SET
    name_ne = EXCLUDED.name_ne,
    name_en = EXCLUDED.name_en,
    scientific_name = EXCLUDED.scientific_name,
    description_ne = EXCLUDED.description_ne,
    is_published = EXCLUDED.is_published;

-- 3. HELPLINE CONTACTS (verified from bakaiyakrishi.gov.np)
INSERT INTO public.helpline_contacts (id, name_ne, designation_ne, ward_ne, ward_number, mobile_number, email, is_verified) VALUES
('help-bakaiya-agri-1', 'हिमश्वेता भट्ट',
    'आर्थिक विकास शाखा प्रमुख / सूचना अधिकारी (कृषि सम्पर्क)',
    'बकैया गाउँपालिका कार्यालय (हात्तीसुँडे)', 0,
    '9868445081', 'bakaiyaagri@gmail.com', true),
('help-bakaiya-cao', 'बकैया गाउँपालिका कार्यपालिकाको कार्यालय',
    'प्रमुख प्रशासकीय अधिकृत / मुख्य कार्यालय सम्पर्क',
    'बकैया-३, हात्तीसुँडे, मकवानपुर', 3,
    '9855088066', 'bakaiyagaunpalika@gmail.com', true)
ON CONFLICT (id) DO UPDATE SET
    name_ne = EXCLUDED.name_ne,
    designation_ne = EXCLUDED.designation_ne,
    mobile_number = EXCLUDED.mobile_number;

-- 4. SYNC METADATA
INSERT INTO public.content_sync_metadata (table_name, last_updated_at, version) VALUES
('crop_categories', NOW(), 1),
('crops', NOW(), 1),
('crop_images', NOW(), 1),
('educational_videos', NOW(), 1),
('helpline_contacts', NOW(), 1)
ON CONFLICT (table_name) DO UPDATE SET
    last_updated_at = NOW(),
    version = public.content_sync_metadata.version + 1;
