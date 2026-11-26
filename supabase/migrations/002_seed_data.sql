-- Migration: 002_seed_data.sql
-- Bakaiya Krishi Saathi — Verified Agricultural Content
-- Aligned with BakaiyaSeedData.kt Room v7 seed data (including Growing Conditions & Varieties)
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

-- 2. CROPS (8 verified crops with verified NARC / MoALD / FAO growing conditions)
INSERT INTO public.crops (
    id, category_id, name_ne, name_en, scientific_name, description_ne,
    varieties, planting_season, suitable_altitude, altitude_min_m, altitude_opt_min_m, altitude_opt_max_m, altitude_max_m,
    suitable_zones_csv, suitable_regions_ne,
    temp_min, temp_opt_min, temp_opt_max, temp_max,
    humidity_min, humidity_max,
    rainfall_min_mm, rainfall_max_mm, rainfall_period_label_ne,
    soil_types, soil_ph_min, soil_ph_max,
    growing_duration_ne, source_reference, last_updated_date, is_verified_content, is_published
) VALUES
('crop-paddy', 'cat-cereal', 'धान', 'Paddy / Rice', 'Oryza sativa L.',
    'धान बकैया गाउँपालिकाको तल्लो उपत्यका तथा खोला किनारका फाँटहरू (छतिवन, हात्तीसुँडे, शिखरपुर) को प्रमुख खाद्यान्न बाली हो।',
    'सावित्री, राधा-४, रामधान, हर्दिनाथ-३, सुख्खा धान-३',
    'असार १ देखि साउन १५ सम्म (बर्खे धान)', '६० देखि १५०० मिटर (बकैयाका तल्लो फाँट तथा टार)', 60, 60, 1000, 1500,
    'terai,inner_terai,mid_hill', 'तराई, भित्री मधेस तथा मध्य पहाडका नदी उपत्यका र बकैया तल्लो फाँट',
    15.0, 20.0, 32.0, 35.0, NULL, NULL, 1000, 1500, 'बाली अवधिमा (वा सो बराबर सिँचाइ)',
    'पानी अडिने चिम्टाइलो र दोमट माटो', 5.5, 6.5,
    '११० देखि १६० दिन (जात अनुसार)', 'नेपाल कृषि अनुसन्धान परिषद् (NARC) / कृषि डायरी / FAO EcoCrop', '2026-11-25', true, true),
('crop-maize', 'cat-cereal', 'मकै', 'Maize / Corn', 'Zea mays L.',
    'मकै बकैया गाउँपालिकाको मध्य तथा उच्च पहाडी पाखो बारी (धियाल, मन्थली, ठूलो धियाल) को प्रमुख खाद्यान्न बाली हो।',
    'देउती, शितला, मनकामना-३, रामपुर कम्पोजिट, पोषिलो मकै-१',
    'फागुन १५ देखि वैशाख १५ (वर्षे मकै)', 'तराईदेखि उच्च पहाडसम्म (६० देखि २५०० मिटर)', 60, 300, 1800, 2500,
    'terai,inner_terai,mid_hill,high_hill', 'तराई, भित्री मधेस, मध्य पहाड र उच्च पहाडी क्षेत्र (बकैयाको धियाल, मन्थली क्षेत्रसहित)',
    10.0, 21.0, 27.0, 35.0, NULL, NULL, 600, 1200, 'बाली अवधिमा',
    'पानीको राम्रो निकास भएको, प्राङ्गारिक पदार्थयुक्त दोमट र बलौटे दोमट माटो', 5.5, 7.0,
    '११० देखि १८० दिन (उचाइ र जात अनुसार)', 'राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC) / कृषि डायरी', '2026-11-25', true, true),
('crop-cauliflower', 'cat-vegetable', 'काउली', 'Cauliflower', 'Brassica oleracea var. botrytis',
    'काउली बकैया गाउँपालिकाका कृषकहरूको प्रमुख व्यावसायिक तरकारी बाली हो।',
    'काठमाडौँ स्थानीय, स्नो मिस्टिक, सिल्भर कप (मौसम अनुसार अगौटे/मध्यम/पछौटे जात)',
    'भदौ देखि कात्तिक (मुख्य हिउँदे याम); पहाडी भेगमा बेमौसमी खेती समेत हुने', '७० देखि २००० मिटर (तराई हिउँदेदेखि पहाडी बेमौसमीसम्म)', 70, 800, 1800, 2000,
    'terai,inner_terai,mid_hill,high_hill', 'तराई, भित्री मधेस, मध्य पहाड र उच्च पहाडी तरकारी पकेट क्षेत्र',
    10.0, 15.0, 24.0, 27.0, NULL, NULL, 500, 700, 'बाली अवधिमा (वा नियमित सिँचाइ)',
    'प्राङ्गारिक पदार्थयुक्त, पानी नजम्ने मलिलो दोमट माटो', 6.0, 6.8,
    'बेर्ना सारेपछि ५५ देखि १३० दिन (जात अनुसार)', 'राष्ट्रिय तरकारी बाली विकास केन्द्र / AITC / FAO', '2026-11-25', true, true),
('crop-tomato', 'cat-vegetable', 'गोलभेंडा', 'Tomato', 'Solanum lycopersicum L.',
    'गोलभेंडा खुला खेत तथा प्लाष्टिक टनेल प्रविधिबाट उत्पादन गरिने बकैया क्षेत्रको प्रमुख नगदे तरकारी बाली हो।',
    'सिर्जना (NARC हाइब्रिड), मनप्रिया, सम्झना',
    'तल्लो क्षेत्रमा भदौ-असोज र माघ-फागुन; टनेलमा वर्षैभरि अनुकूल व्यवस्थापन', '६० देखि २००० मिटर (तराईदेखि मध्य पहाड टनेल प्रविधि)', 60, 800, 1600, 2000,
    'terai,inner_terai,mid_hill', 'तराई, भित्री मधेस तथा मध्य पहाडी व्यावसायिक तरकारी पकेट',
    10.0, 18.0, 27.0, 35.0, NULL, NULL, 600, 800, 'बाली अवधिमा (वर्षामा टनेल सुरक्षित)',
    'पानी निकास भएको गहिरो र उर्वर बलौटे दोमट वा चिम्टाइलो दोमट माटो', 6.0, 6.8,
    'बेर्ना सारेको ६०–९० दिनमा पहिलो टिपाइ', 'नेपाल कृषि अनुसन्धान परिषद् (NARC) बागवानी महाशाखा / FAO EcoCrop', '2026-11-25', true, true),
('crop-ginger', 'cat-cash', 'अदुवा', 'Ginger', 'Zingiber officinale Rosc.',
    'अदुवा बकैयाका पहाडी पाखो तथा छहारीयुक्त बारीहरूमा किसानहरूले उत्पादन गर्ने प्रमुख मसला तथा नगदे बाली हो।',
    'कपुरकोट अदुवा-१, कपुरकोट अदुवा-२, स्थानीय बोसे र नसे',
    'चैत मध्यदेखि वैशाख अन्त्यसम्म', '१०० देखि १६०० मिटरसम्म (मध्य पहाड तथा भित्री मधेस)', 100, 600, 1500, 1600,
    'terai,inner_terai,mid_hill', 'तराई, भित्री मधेस र मध्य पहाडी पाखो क्षेत्र',
    19.0, 20.0, 30.0, 30.0, NULL, NULL, 1500, 2500, 'वार्षिक (मनसुनमा राम्रो वितरण)',
    'पानीको उत्तम निकास भएको, प्राङ्गारिक पदार्थयुक्त खुकुलो दोमट माटो', 6.0, 6.5,
    '८ देखि ९ महिना (२२५ देखि २६० दिन)', 'राष्ट्रिय अदुवा बाली अनुसन्धान कार्यक्रम, कपुरकोट (NARC) / FAO', '2026-11-25', true, true),
('crop-mustard', 'cat-oilseed', 'तोरी', 'Mustard / Rapeseed', 'Brassica campestris var. toria',
    'तोरी बकैयाका धान काटेपछिका खेतबारीमा हिउँद याममा लगाइने प्रमुख तेलहन बाली हो।',
    'विकास, उन्नति, प्रगति, प्रीति, मोरङ तोरी-२',
    'असोज १५ देखि कात्तिक १५ सम्म', 'तराईदेखि १५०० मिटर मध्य पहाडसम्म', 60, 100, 1200, 1500,
    'terai,inner_terai,mid_hill', 'तराई, भित्री मधेस तथा न्यानो मध्य पहाड',
    10.0, 12.0, 25.0, 25.0, NULL, NULL, 300, 400, 'हिउँद बाली अवधिमा',
    'हल्का दोमटदेखि चिम्टाइलो दोमट माटो', 6.0, 7.5,
    '८५ देखि १२० दिन (जात अनुसार)', 'तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC) / कृषि डायरी', '2026-11-25', true, true),
('crop-lentil', 'cat-pulse', 'मसुरो', 'Lentil', 'Lens culinaris',
    'मसुरो हिउँदमा धान खेतको चिस्यानमा वा बारीमा लगाइने प्रमुख दलहन बाली हो।',
    'शिमल, शिखर, सगुन, सिन्दुर, शिशिर, खजुरा मसुरो-१, २, ३',
    'कात्तिक (धान काटिसकेपछि वा धान काट्नु १५ दिनअघि रिले खेती)', 'तराई, भित्री मधेस तथा तल्ला मध्य पहाडी बेँसी (६० - १५०० मिटर)', 60, 60, 1000, 1500,
    'terai,inner_terai,mid_hill', 'तराई, भित्री मधेस तथा मध्य पहाडका नदी उपत्यका',
    5.0, 18.0, 29.0, 32.0, NULL, NULL, 300, 500, 'हिउँद बाली अवधिमा (अवशिष्ट चिस्यानसहित)',
    'पानी नजम्ने दोमट र हल्का चिम्टाइलो माटो', 5.5, 7.0,
    '१२० देखि १४५ दिन (जात अनुसार)', 'कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC) / FAO EcoCrop', '2026-11-25', true, true),
('crop-banana', 'cat-fruit', 'केरा', 'Banana', 'Musa spp.',
    'केरा बकैयाका न्यानो तल्लो उपत्यका र खोला किनारका बस्तीहरूमा वर्षैभरि उत्पादन र आयआर्जन दिने प्रमुख फलफूल बाली हो।',
    'मालभोग, विलियम हाइब्रिड, जी-९ (G-9), झापाली मालभोग, चिनियाँ चम्पा',
    'फागुन-चैत र असार-साउन (वर्षाको सुरुवात)', 'तराईदेखि करिब १००० मिटरसम्मका न्यानो उपत्यका', 60, 60, 600, 1000,
    'terai,inner_terai', 'तराई, भित्री मधेस तथा तुषारोरहित तल्ला उपत्यका',
    10.0, 20.0, 35.0, 38.0, 60.0, 85.0, 1200, 2500, 'वार्षिक (वा नियमित सिँचाइ)',
    'गहिरो, मलिलो, पानी नजम्ने दोमट माटो', 6.0, 7.5,
    '११ देखि १५ महिना (पहिलो घरी तयार हुन)', 'नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय / FAO', '2026-11-25', true, true)
ON CONFLICT (id) DO UPDATE SET
    name_ne = EXCLUDED.name_ne,
    name_en = EXCLUDED.name_en,
    scientific_name = EXCLUDED.scientific_name,
    description_ne = EXCLUDED.description_ne,
    altitude_min_m = EXCLUDED.altitude_min_m,
    altitude_max_m = EXCLUDED.altitude_max_m,
    temp_min = EXCLUDED.temp_min,
    temp_max = EXCLUDED.temp_max,
    last_updated_date = EXCLUDED.last_updated_date,
    is_published = EXCLUDED.is_published;

-- 3. VARIETY-SPECIFIC GROWING CONDITIONS
INSERT INTO public.crop_variety_requirements (
    id, crop_id, crop_name_ne, crop_name_en, variety_name_ne, variety_name_en,
    notes_ne, source_reference, altitude_min_m, altitude_max_m, suitable_altitude,
    suitable_zones_csv, suitable_regions_ne, planting_season, growing_duration_ne, last_updated_date
) VALUES
('var-paddy-savitri', 'crop-paddy', 'धान', 'Paddy / Rice', 'सावित्री', 'Savitri',
    'दीर्घकालीन लोकप्रिय जात; मरुवा रोगप्रति मध्यम सहनशील।', 'नेपाल कृषि अनुसन्धान परिषद् (NARC)',
    60, 700, 'तराई, भित्री मधेस तथा तल्ला नदी फाँट', 'terai,inner_terai', 'तराई, भित्री मधेस तथा तल्ला बेँसी', 'असार–साउन (बर्खे धान)', '१४० देखि १६० दिन', '2026-11-25'),
('var-paddy-hardinath3', 'crop-paddy', 'धान', 'Paddy / Rice', 'हर्दिनाथ-३', 'Hardinath-3',
    'छिटो पाक्ने (अगौटे) जात; चैते तथा सघन बाली चक्रका लागि उपयुक्त।', 'नेपाल कृषि अनुसन्धान परिषद् (NARC)',
    60, 700, 'तराई तथा भित्री मधेस', 'terai,inner_terai', 'तराई र भित्री मधेस', 'फागुन–चैत (चैते) तथा असार (बर्खे)', '११० देखि ११५ दिन', '2026-11-25'),
('var-maize-manakamana3', 'crop-maize', 'मकै', 'Maize / Corn', 'मनकामना-३', 'Manakamana-3',
    'बोट बलियो भई नढल्ने मध्य पहाडमा अत्यन्त लोकप्रिय जात।', 'राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC)',
    1000, 1700, '१००० देखि १७०० मिटर (मध्य पहाड)', 'mid_hill', 'मध्य पहाड (१०००–१७०० मिटर)', 'फागुन–जेठ', '१३५ देखि १४५ दिन', '2026-11-25'),
('var-tomato-srijana', 'crop-tomato', 'गोलभेंडा', 'Tomato', 'सिर्जना (NARC हाइब्रिड)', 'Srijana',
    'ओइलाउने रोगप्रति सहनशील नेपालको पहिलो हाइब्रिड जात; टनेल खेतीका लागि उत्तम।', 'नेपाल कृषि अनुसन्धान परिषद् (NARC) बागवानी महाशाखा',
    800, 1500, '८०० देखि १५००+ मिटर (मध्य पहाड तथा टनेल)', 'inner_terai,mid_hill', 'मध्य पहाड र पहाडी उपत्यका', 'भदौ–असोज र माघ–फागुन (टनेलमा वर्षैभरि)', 'बेर्ना सारेको ६५ देखि ८० दिनमा पहिलो टिपाइ', '2026-11-25')
ON CONFLICT (id) DO UPDATE SET
    variety_name_ne = EXCLUDED.variety_name_ne,
    growing_duration_ne = EXCLUDED.growing_duration_ne,
    last_updated_date = EXCLUDED.last_updated_date;

-- 4. SYNC METADATA
INSERT INTO public.content_sync_metadata (table_name, last_updated_at, version) VALUES
('crop_categories', NOW(), 1),
('crops', NOW(), 2),
('crop_variety_requirements', NOW(), 1),
('crop_images', NOW(), 1),
('educational_videos', NOW(), 1)
ON CONFLICT (table_name) DO UPDATE SET
    last_updated_at = NOW(),
    version = public.content_sync_metadata.version + 1;
