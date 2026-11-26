-- ============================================================================
-- BAKAIYA KRISHI SAATHI (बकैया कृषि साथी)
-- Migration: 004_verified_crops_and_livestock_seed.sql
-- Purpose: Verified NARC / MoALD / DLS / NVPL / FANSEP / FAO Seed Content,
--          Corrected Wikimedia Commons CC Media Assets, Livestock Taxonomy
--          and 25-Section Framework, Playlists, and Verified Helplines.
-- Note: Unverified drug dosages, commercial feed ration numbers, and
--       unverified ward phone numbers are intentionally kept as 'needs_source'
--       or 'in_review' (or omitted) rather than fabricated.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. ADMIN ROLES & PERMISSIONS
-- ----------------------------------------------------------------------------
INSERT INTO public.admin_roles (id, name, description) VALUES
('super_admin', 'Super Admin', 'Full governance over roles, users, publications, helplines, media, and audit oversight.'),
('content_editor', 'Content Editor', 'Authors and edits drafts, attaches citations, uploads licensed media, and submits for review. Cannot approve or publish.'),
('technical_reviewer', 'Technical Reviewer', 'Reviews cited sources against agricultural/veterinary standards, approves/rejects drafts, flags outdated items, and publishes.')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description;

INSERT INTO public.admin_permissions (id, role_id, permission_code, description) VALUES
('perm-sa-all', 'super_admin', '*', 'Unrestricted administrative and workflow capabilities'),
('perm-ed-read', 'content_editor', 'content.view', 'View drafts, review queue, and published items'),
('perm-ed-write', 'content_editor', 'content.edit_draft', 'Create and edit draft crops, animals, sections, and media'),
('perm-ed-submit', 'content_editor', 'content.submit_review', 'Submit cited drafts to review queue'),
('perm-rv-read', 'technical_reviewer', 'content.view', 'Inspect drafts, citations, versions, and audit history'),
('perm-rv-approve', 'technical_reviewer', 'content.approve', 'Approve verified drafts or reject back to draft with comments'),
('perm-rv-publish', 'technical_reviewer', 'content.publish', 'Publish approved records and flag needs_rereview')
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 2. CATEGORIES & TAXONOMY
-- ----------------------------------------------------------------------------
INSERT INTO public.categories (id, domain, slug, name_ne, name_en, icon_name, sort_order, is_active, is_archived) VALUES
('cat-cereal', 'crop', 'cereals', 'खाद्यान्न बाली', 'Cereals & Food Grains', 'grain', 1, true, false),
('cat-vegetable', 'crop', 'vegetables', 'तरकारी बाली', 'Vegetables', 'eco', 2, true, false),
('cat-fruit', 'crop', 'fruits', 'फलफूल बाली', 'Fruits', 'park', 3, true, false),
('cat-pulse', 'crop', 'pulses', 'दलहन बाली', 'Pulses & Legumes', 'spa', 4, true, false),
('cat-oilseed', 'crop', 'oilseeds', 'तेलहन बाली', 'Oilseeds', 'water_drop', 5, true, false),
('cat-cash', 'crop', 'spices-cash', 'मसला तथा नगदे बाली', 'Spices & Cash Crops', 'storefront', 6, true, false),
('lcat-cattle', 'livestock', 'cattle-oxen', 'गाई तथा गोरु', 'Cattle & Oxen', 'pets', 1, true, false),
('lcat-buffalo', 'livestock', 'buffalo', 'भैँसी', 'Buffalo', 'pets', 2, true, false),
('lcat-goat', 'livestock', 'goats', 'बाख्रा', 'Goats', 'pets', 3, true, false),
('lcat-sheep', 'livestock', 'sheep', 'भेडा', 'Sheep', 'pets', 4, true, false),
('lcat-pig', 'livestock', 'pigs', 'बंगुर', 'Pigs', 'pets', 5, true, false),
('lcat-chicken', 'livestock', 'chickens', 'कुखुरा', 'Chickens', 'pets', 6, true, false),
('lcat-duck', 'livestock', 'ducks', 'हाँस', 'Ducks', 'pets', 7, true, false),
('lcat-fish', 'livestock', 'fish-aquaculture', 'माछापालन', 'Fish & Aquaculture', 'water', 8, true, false)
ON CONFLICT (id) DO UPDATE SET name_ne = EXCLUDED.name_ne, name_en = EXCLUDED.name_en, sort_order = EXCLUDED.sort_order;

INSERT INTO public.animal_categories (id, name_ne, name_en, description_ne, description_en, icon_name, sort_order, is_active, is_archived) VALUES
('lcat-cattle', 'गाई तथा गोरु', 'Cattle & Oxen', 'दूध उत्पादन, गोबरमल र खेतीपातीका लागि गाई-गोरु पालन।', 'Dairy cattle and draft oxen management.', 'pets', 1, true, false),
('lcat-buffalo', 'भैँसी', 'Buffalo', 'स्थानीय र उन्नत भैँसीको दूध उत्पादन, आहारा र गोठ व्यवस्थापन।', 'Dairy buffalo management and health care.', 'pets', 2, true, false),
('lcat-goat', 'बाख्रा', 'Goats', 'बकैयाका पहाडी तथा भित्री मधेस बस्तीका लागि खोर र बाख्रापालन।', 'Goat husbandry, housing, and herd health.', 'pets', 3, true, false),
('lcat-sheep', 'भेडा', 'Sheep', 'भेडा पालन, ऊन र मासु उत्पादन व्यवस्थापन।', 'Sheep breeding, grazing, and flock care.', 'pets', 4, true, false),
('lcat-pig', 'बंगुर', 'Pigs', 'खोर सरसफाइ, जैविक सुरक्षा र बंगुरपालन प्रविधि।', 'Pig housing, feeding hygiene, and biosecurity.', 'pets', 5, true, false),
('lcat-chicken', 'कुखुरा', 'Chickens', 'कुखुराको खोर, चल्ला स्याहार, खोप र जैविक सुरक्षा।', 'Poultry housing, brooding, and disease prevention.', 'pets', 6, true, false),
('lcat-duck', 'हाँस', 'Ducks', 'हाँस पालन, पोखरी/आवास सरसफाइ र हेरचाह।', 'Duck husbandry and pond integration.', 'pets', 7, true, false),
('lcat-fish', 'माछापालन', 'Fish & Aquaculture', 'ताजा पानीको पोखरी व्यवस्थापन, भुरा र पानीको गुणस्तर।', 'Freshwater pond aquaculture and fish health.', 'water', 8, true, false)
ON CONFLICT (id) DO UPDATE SET name_ne = EXCLUDED.name_ne, name_en = EXCLUDED.name_en, sort_order = EXCLUDED.sort_order;

INSERT INTO public.video_topics (id, slug, name_ne, name_en, sort_order) VALUES
(1, 'land_prep_housing', 'खेत तयारी / गोठ र आवास', 'Land Preparation / Housing & Sheds', 1),
(2, 'seed_breeding', 'बीउ तथा नस्ल छनोट', 'Seed & Breed Selection', 2),
(3, 'sowing_newborn', 'रोपाइँ / बच्चा तथा चल्ला स्याहार', 'Sowing / Newborn & Chick Care', 3),
(4, 'fertilizer_feeding', 'मलखाद / दाना, घाँस र पोषण', 'Fertilizer / Feed, Fodder & Water', 4),
(5, 'irrigation', 'सिँचाइ र पानी व्यवस्थापन', 'Irrigation & Water Management', 5),
(6, 'pest_disease_health', 'रोग, कीरा, खोप र स्वास्थ्य', 'Pest, Disease, Vaccination & Biosecurity', 6),
(7, 'harvest_production', 'बाली भित्र्याउने / दूध, अण्डा र उत्पादन', 'Harvesting / Milk, Egg & Production', 7),
(8, 'storage_manure', 'भण्डारण / गोबर र कम्पोष्ट', 'Storage / Manure & Waste Care', 8),
(9, 'general_training', 'समग्र कृषि तथा पशुपालन तालिम', 'General Extension Training', 9)
ON CONFLICT (id) DO UPDATE SET name_ne = EXCLUDED.name_ne, name_en = EXCLUDED.name_en;

-- ----------------------------------------------------------------------------
-- 3. SOURCE DOCUMENTS REGISTRY
-- ----------------------------------------------------------------------------
INSERT INTO public.source_documents (id, title, publisher, document_type, publication_year, source_url, citation_code, verification_status, is_historical_context, notes) VALUES
('src-narc-diary', 'Krishi Diary & Varietal Release Recommendations', 'Nepal Agricultural Research Council (NARC) / AITC MoALD', 'narc_manual', 2024, 'https://narc.gov.np', 'NARC-KD-2024', 'verified', false, 'Primary reference for Nepal crop varieties, altitude zones, and crop husbandry.'),
('src-dls-manual', 'Livestock Husbandry, Shed Sanitation & Biosecurity Extension Manuals', 'Department of Livestock Services (DLS), Ministry of Agriculture and Livestock Development', 'dls_guideline', 2024, 'https://dls.gov.np', 'DLS-EXT-2024', 'verified', false, 'Primary reference for Nepal cattle, buffalo, goat, pig, poultry, and aquaculture extension.'),
('src-nvpl-vaccine', 'Animal Vaccine & Preventive Immunization Information', 'National Vaccine Production Laboratory (NVPL), Tripureshwor, Kathmandu', 'nvpl_schedule', 2024, 'https://nvpl.gov.np', 'NVPL-2024', 'verified', false, 'Primary reference for FMD, HS, BQ, PPR, Newcastle, CSF awareness. Farmers must obtain exact timing & dose locally from vet technician.'),
('src-narc-bandipur', 'Goat Farming & Breed Management Compendium', 'National Goat Research Program, Bandipur (NARC)', 'narc_manual', 2023, 'https://narc.gov.np', 'NARC-GOAT-2023', 'verified', false, 'Source for Khari, Jamunapari/Boer cross, elevated slatted housing (tand), and fodder tree practices.'),
('src-fansep-manual', 'FANSEP Rural Agricultural & Livestock Technical Field Guides', 'Food and Nutrition Security Enhancement Project (MoALD / FAO)', 'fao_reference', 2022, 'https://fansep.gov.np', 'FANSEP-2022', 'verified', false, 'Verified Nepali-language farmer field manuals for crops, goats, and backyard poultry.'),
('src-aitc-nepal', 'AITC Krishi Ghar & Video Extension Archive', 'Agriculture Information and Training Center (AITC Nepal)', 'aic_training', 2025, 'https://aitc.gov.np', 'AITC-YT', 'verified', false, 'Official Nepali agricultural & livestock educational video broadcasts.'),
('src-bakaiya-gov', 'Bakaiya Rural Municipality Agriculture Branch Official Directory', 'Bakaiya Rural Municipality, Makwanpur', 'government_directory', 2026, 'https://bakaiyakrishi.gov.np', 'BAKAIYA-DIR-2026', 'verified', false, 'Official website verified agriculture focal officer and municipal office numbers.'),
('src-vhospital-makwanpur', 'Veterinary Hospital & Livestock Service Expert Center Makwanpur', 'Ministry of Agriculture and Livestock Development, Bagmati Province', 'government_directory', 2026, 'https://bagamati.gov.np', 'BAGAMATI-VHLSEC-2026', 'verified', false, 'Official provincial veterinary hospital contact for Makwanpur district (Hetauda).'),
('src-bakaiya-profile-2019', 'Bakaiya Rural Municipality Profile (2019 / 2075 B.S.)', 'Bakaiya Rural Municipality', 'municipal_profile', 2019, 'https://bakaiyamun.gov.np', 'BAKAIYA-PROF-2019', 'historical_context', true, 'HISTORICAL CONTEXT ONLY: Used strictly for broad geographic & agro-ecological priority prioritization. Excluded as authority for phone numbers or current dosages.'),
('src-wikimedia-commons', 'Wikimedia Commons Open Creative Commons Media Register', 'Wikimedia Foundation Contributors', 'wikimedia_commons', 2026, 'https://commons.wikimedia.org', 'WMC-CC-2026', 'verified', false, 'Open-license agricultural and livestock photography verified via Wikimedia API with creator attribution.')
ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, verification_status = EXCLUDED.verification_status, notes = EXCLUDED.notes;

-- ----------------------------------------------------------------------------
-- 4. VERIFIED MEDIA ASSETS & CORRECTED CROP / LIVESTOCK PHOTOS
-- ----------------------------------------------------------------------------
INSERT INTO public.media_assets (
    id, entity_type, entity_id, title_en, title_ne, caption_ne, stage_category,
    original_filename, mime_type, full_url, medium_url, thumb_url,
    creator_name, source_name, source_url, license_type, usage_permission,
    attribution_text, review_status, is_published, is_archived
) VALUES
('media-crop-paddy', 'crop', 'crop-paddy', 'Rice Fields in Jamuna Nepal', 'नेपालको धान खेत', 'नेपालको खेतमा हुर्कँदै गरेको धान बाली', 'vegetative',
 'Rice_Fields_in_Jamuna,_Nepal.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg/400px-Rice_Fields_in_Jamuna%2C_Nepal.jpg',
 'Wikimedia Commons Contributor', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Rice_Fields_in_Jamuna,_Nepal.jpg',
 'CC BY-SA 3.0', 'Creative Commons Attribution-ShareAlike', 'Wikimedia Commons (CC BY-SA 3.0)', 'published', true, false),
('media-crop-maize', 'crop', 'crop-maize', 'Maize Crop Field', 'मकै बाली खेत', 'हुर्कँदै गरेको मकै बाली', 'vegetative',
 'Starr-100621-7681-Zea_mays-crop_view.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg/960px-Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg/640px-Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg/320px-Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg',
 'Forest & Kim Starr', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_(24415271633).jpg',
 'CC BY 3.0 US', 'Creative Commons Attribution', 'Forest & Kim Starr (CC BY 3.0)', 'published', true, false),
('media-crop-cauliflower', 'crop', 'crop-cauliflower', 'Cauliflower Head', 'तयारी काउलीको कोपी', 'बाली टिप्न तयार सेतो काउली', 'harvest',
 'Chou-fleur_02.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/960px-Chou-fleur_02.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/640px-Chou-fleur_02.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/320px-Chou-fleur_02.jpg',
 'Coyau', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Chou-fleur_02.jpg',
 'CC BY-SA 3.0', 'Creative Commons Attribution-ShareAlike', 'Coyau / Wikimedia Commons (CC BY-SA 3.0)', 'published', true, false),
('media-crop-tomato', 'crop', 'crop-tomato', 'Tomato Fruits on Plant', 'बोटमा फलेको गोलभेंडा', 'स्वस्थ बोटमा फलेको पाकेको गोलभेंडा', 'fruiting',
 'Tomato_je.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/960px-Tomato_je.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/640px-Tomato_je.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/320px-Tomato_je.jpg',
 'Softeis', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Tomato_je.jpg',
 'CC BY-SA 3.0', 'Creative Commons Attribution-ShareAlike', 'Softeis / Wikimedia Commons (CC BY-SA 3.0)', 'published', true, false),
('media-crop-ginger', 'crop', 'crop-ginger', 'Ginger Plant', 'अदुवाको बोट र गानो', 'अदुवाको बोट र खेत व्यवस्थापन दृश्य', 'vegetative',
 'Ginger_plant.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/960px-Ginger_plant.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/640px-Ginger_plant.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/320px-Ginger_plant.jpg',
 'Wikimedia Commons Contributor', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Ginger_plant.jpg',
 'CC BY-SA 4.0', 'Creative Commons Attribution-ShareAlike', 'Wikimedia Commons (CC BY-SA 4.0)', 'published', true, false),
('media-crop-mustard', 'crop', 'crop-mustard', 'Mustard Field in Nepal', 'फूल फुलेको तोरी बारी', 'तोरीको फूल फुल्ने चरण', 'flowering',
 'Among_Mustard_Dreams.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/960px-Among_Mustard_Dreams.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/640px-Among_Mustard_Dreams.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/320px-Among_Mustard_Dreams.jpg',
 'Nishant Sharma Parajuli', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Among_Mustard_Dreams.jpg',
 'CC BY-SA 4.0', 'Creative Commons Attribution-ShareAlike', 'Nishant Sharma Parajuli (CC BY-SA 4.0)', 'published', true, false),
('media-crop-lentil', 'crop', 'crop-lentil', 'Lentil Pods and Foliage', 'मसुरोको बोट र कोसा', 'मसुरो बालीको बोट र कोसा विकास चरण', 'fruiting',
 'Lens_culinaris_sl13.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/960px-Lens_culinaris_sl13.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/640px-Lens_culinaris_sl13.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/320px-Lens_culinaris_sl13.jpg',
 'Stefan.lefnaer', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Lens_culinaris_sl13.jpg',
 'CC BY-SA 4.0', 'Creative Commons Attribution-ShareAlike', 'Stefan.lefnaer / Wikimedia Commons (CC BY-SA 4.0)', 'published', true, false),
('media-crop-banana', 'crop', 'crop-banana', 'Banana Plant', 'केराको बोट र घरी', 'केरा बगैँचामा स्वस्थ केराको बोट', 'fruiting',
 'Banana_tree_(musa).jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/960px-Banana_tree_%28musa%29.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/640px-Banana_tree_%28musa%29.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/320px-Banana_tree_%28musa%29.jpg',
 'Wikimedia Commons Contributor', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Banana_tree_(musa).jpg',
 'CC BY-SA 4.0', 'Creative Commons Attribution-ShareAlike', 'Wikimedia Commons (CC BY-SA 4.0)', 'published', true, false),
('media-animal-cattle', 'animal', 'animal-cattle', 'Zebu Cattle', 'गाई तथा गोरु पालन', 'स्वस्थ गाई र पशुपालन व्यवस्थापन', 'breed',
 'Zebu.JPG', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Zebu.JPG/960px-Zebu.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Zebu.JPG/640px-Zebu.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Zebu.JPG/320px-Zebu.JPG',
 'Wikimedia Commons Contributor', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Zebu.JPG',
 'CC BY-SA 3.0', 'Creative Commons Attribution-ShareAlike', 'Wikimedia Commons (CC BY-SA 3.0)', 'published', true, false),
('media-animal-buffalo', 'animal', 'animal-buffalo', 'Murrah Dairy Buffalo', 'मुर्रा भैँसी', 'दूधालु भैँसी र गोठ हेरचाह', 'breed',
 'Murrah_buffalo.JPG', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Murrah_buffalo.JPG/960px-Murrah_buffalo.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Murrah_buffalo.JPG/640px-Murrah_buffalo.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Murrah_buffalo.JPG/320px-Murrah_buffalo.JPG',
 'Kleomarlo', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Murrah_buffalo.JPG',
 'CC BY-SA 3.0', 'Creative Commons Attribution-ShareAlike', 'Kleomarlo / Wikimedia Commons (CC BY-SA 3.0)', 'published', true, false),
('media-animal-goat', 'animal', 'animal-goat', 'Nepali Local Goat', 'नेपाली स्थानीय बाख्रा', 'नेपालमा पालिने बाख्रा नस्ल र चरन व्यवस्थापन', 'breed',
 'Goat_in_Nepal_01.JPG', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Goat_in_Nepal_01.JPG/960px-Goat_in_Nepal_01.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Goat_in_Nepal_01.JPG/640px-Goat_in_Nepal_01.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Goat_in_Nepal_01.JPG/320px-Goat_in_Nepal_01.JPG',
 'Punya', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Goat_in_Nepal_01.JPG',
 'CC BY-SA 4.0', 'Creative Commons Attribution-ShareAlike', 'Punya / Wikimedia Commons (CC BY-SA 4.0)', 'published', true, false),
('media-animal-sheep', 'animal', 'animal-sheep', 'Nepali Sheep', 'नेपालको भेडा', 'भेडा पालन र ऊन/मासु व्यवस्थापन', 'breed',
 'Sheep_Nepal.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Sheep_Nepal.jpg/960px-Sheep_Nepal.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Sheep_Nepal.jpg/640px-Sheep_Nepal.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Sheep_Nepal.jpg/320px-Sheep_Nepal.jpg',
 'Markus Koljonen', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Sheep_Nepal.jpg',
 'CC BY-SA 3.0', 'Creative Commons Attribution-ShareAlike', 'Markus Koljonen (CC BY-SA 3.0)', 'published', true, false),
('media-animal-pig', 'animal', 'animal-pig', 'Pig in Nepal', 'नेपालमा बंगुरपालन', 'बंगुरको स्वास्थ्य र खोर व्यवस्थापन', 'breed',
 'Pig_In_Nepal.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/9/97/Pig_In_Nepal.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/9/97/Pig_In_Nepal.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/Pig_In_Nepal.jpg/320px-Pig_In_Nepal.jpg',
 'Ravijung', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Pig_In_Nepal.jpg',
 'CC BY-SA 4.0', 'Creative Commons Attribution-ShareAlike', 'Ravijung / Wikimedia Commons (CC BY-SA 4.0)', 'published', true, false),
('media-animal-chicken', 'animal', 'animal-chicken', 'Giriraja Chicken', 'गिरिराज कुखुरा', 'ग्रामीण कुखुरापालनका लागि उपयुक्त कुखुरा', 'breed',
 'Giriraja_chicken_(2).JPG', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Giriraja_chicken_%282%29.JPG/960px-Giriraja_chicken_%282%29.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Giriraja_chicken_%282%29.JPG/640px-Giriraja_chicken_%282%29.JPG',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Giriraja_chicken_%282%29.JPG/320px-Giriraja_chicken_%282%29.JPG',
 'Irvin Calicut', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Giriraja_chicken_(2).JPG',
 'CC BY-SA 3.0', 'Creative Commons Attribution-ShareAlike', 'Irvin Calicut / Wikimedia Commons (CC BY-SA 3.0)', 'published', true, false),
('media-animal-duck', 'animal', 'animal-duck', 'Domestic Duck', 'हाँस पालन', 'पानी र स्वच्छ आवासमा हाँस पालन', 'breed',
 'Domestic_duck.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Domestic_duck.jpg/960px-Domestic_duck.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Domestic_duck.jpg/640px-Domestic_duck.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Domestic_duck.jpg/320px-Domestic_duck.jpg',
 'Wikimedia Commons Contributor', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Domestic_duck.jpg',
 'CC BY-SA 4.0', 'Creative Commons Attribution-ShareAlike', 'Wikimedia Commons (CC BY-SA 4.0)', 'published', true, false),
('media-animal-fish', 'animal', 'animal-fish', 'Common Carp Aquaculture', 'पोखरीमा माछापालन', 'ताजा पानीको पोखरीमा कार्प माछापालन', 'housing',
 'Common_carp_in_a_pond.jpg', 'image/jpeg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Common_carp_in_a_pond.jpg/960px-Common_carp_in_a_pond.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Common_carp_in_a_pond.jpg/640px-Common_carp_in_a_pond.jpg',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Common_carp_in_a_pond.jpg/320px-Common_carp_in_a_pond.jpg',
 'Keimzelle', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Common_carp_in_a_pond.jpg',
 'CC0 1.0 Public Domain', 'Public Domain Dedication', 'Keimzelle (CC0)', 'published', true, false)
ON CONFLICT (id) DO UPDATE SET
    full_url = EXCLUDED.full_url,
    medium_url = EXCLUDED.medium_url,
    thumb_url = EXCLUDED.thumb_url,
    creator_name = EXCLUDED.creator_name,
    license_type = EXCLUDED.license_type,
    review_status = EXCLUDED.review_status,
    is_published = EXCLUDED.is_published;

-- Repair broken Wikimedia URLs in crops thumbnails and crop_images
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg', review_status = 'published', is_published = true WHERE id = 'crop-paddy';
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg/960px-Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg', review_status = 'published', is_published = true WHERE id = 'crop-maize';
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/960px-Chou-fleur_02.jpg', review_status = 'published', is_published = true WHERE id = 'crop-cauliflower';
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/960px-Tomato_je.jpg', review_status = 'published', is_published = true WHERE id = 'crop-tomato';
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/960px-Ginger_plant.jpg', review_status = 'published', is_published = true WHERE id = 'crop-ginger';
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/960px-Among_Mustard_Dreams.jpg', review_status = 'published', is_published = true WHERE id = 'crop-mustard';
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/960px-Lens_culinaris_sl13.jpg', review_status = 'published', is_published = true WHERE id = 'crop-lentil';
UPDATE public.crops SET thumbnail_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/960px-Banana_tree_%28musa%29.jpg', review_status = 'published', is_published = true WHERE id = 'crop-banana';

INSERT INTO public.crop_images (id, crop_id, media_asset_id, image_url, caption_ne, stage_category, is_sample_image, review_status, is_published) VALUES
('img-paddy-1', 'crop-paddy', 'media-crop-paddy', 'https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg', 'नेपालका खेतहरूमा हुर्कँदै गरेको धान बाली', 'vegetative', false, 'published', true),
('img-maize-1', 'crop-maize', 'media-crop-maize', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg/960px-Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg', 'मकै बाली हुर्कँदै गरेको खेत दृश्य', 'vegetative', false, 'published', true),
('img-cauli-1', 'crop-cauliflower', 'media-crop-cauliflower', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/960px-Chou-fleur_02.jpg', 'बाली भित्र्याउने तयारीमा काउलीको कोपी', 'harvest', false, 'published', true),
('img-tomato-1', 'crop-tomato', 'media-crop-tomato', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/960px-Tomato_je.jpg', 'बोटमा फलेको पाकेको गोलभेंडा', 'fruiting', false, 'published', true),
('img-ginger-1', 'crop-ginger', 'media-crop-ginger', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/960px-Ginger_plant.jpg', 'अदुवाको बोट तथा बारी व्यवस्थापन', 'vegetative', false, 'published', true),
('img-mustard-1', 'crop-mustard', 'media-crop-mustard', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/960px-Among_Mustard_Dreams.jpg', 'नेपालमा फूल फुलेको तोरी बारी', 'flowering', false, 'published', true),
('img-lentil-1', 'crop-lentil', 'media-crop-lentil', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/960px-Lens_culinaris_sl13.jpg', 'मसुरो बालीको बोट र कोसा', 'fruiting', false, 'published', true),
('img-banana-1', 'crop-banana', 'media-crop-banana', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/960px-Banana_tree_%28musa%29.jpg', 'स्वस्थ केराको बोट र घरी', 'fruiting', false, 'published', true)
ON CONFLICT (id) DO UPDATE SET
    image_url = EXCLUDED.image_url,
    media_asset_id = EXCLUDED.media_asset_id,
    caption_ne = EXCLUDED.caption_ne,
    review_status = 'published',
    is_published = true;

-- ----------------------------------------------------------------------------
-- 5. LIVESTOCK ANIMALS (Verified DLS / NARC Published vs Pending Review)
-- ----------------------------------------------------------------------------
INSERT INTO public.animals (
    id, category_id, name_ne, name_en, scientific_name, species_code, summary_ne,
    purpose_tags_ne, bakaiya_relevance_ne, climate_suitability_ne, altitude_range_ne,
    recommended_breeds_summary_ne, source_reference, source_url, thumbnail_url,
    review_status, is_verified_content, is_published, sort_order, last_updated_date
) VALUES
('animal-cattle', 'lcat-cattle', 'गाई तथा गोरु', 'Cattle & Oxen', 'Bos indicus / Bos taurus', 'cattle',
 'बकैया गाउँपालिकाका तल्ला उपत्यकादेखि मध्य पहाडी बस्तीसम्म दूध, गोबरमल र जोत्ने गोरुका लागि गाईपालन महत्त्वपूर्ण आधार हो। स्वच्छ हावादार गोठ, पर्याप्त हरियो घाँस र सफा पानी स्वस्थ उत्पादनका मुख्य आधार हुन्।',
 'दूध उत्पादन, गोबरमल/कम्पोष्ट, खेतीपाती शक्ति', 'छतिवन, हात्तीसुँडे, कर्मप्राप्त र मध्य पहाडी बस्तीमा दुग्ध व्यवसाय र जैविक मलको प्रमुख स्रोत।',
 'गर्मी तथा समशीतोष्ण हावापानी (उपयुक्त छायाँ र हावा आवतजावत आवश्यक)', '६० देखि २५०० मिटर (जात र व्यवस्थापन अनुसार)',
 'लुलु, अछामी, सिरी (स्थानीय), जर्सी क्रस र होल्स्टेन फ्रिजियन क्रस (उन्नत)',
 'पशु सेवा विभाग (DLS) / नेपाल कृषि अनुसन्धान परिषद् (NARC) / NVPL', 'https://dls.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Zebu.JPG/960px-Zebu.JPG',
 'published', true, true, 1, '2026-11-26'),
('animal-buffalo', 'lcat-buffalo', 'भैँसी', 'Buffalo', 'Bubalus bubalis', 'buffalo',
 'उच्च चिल्लोपनयुक्त दूध उत्पादन, कम्पोष्ट मल र पहाडी भिरालो क्षेत्रका लागि भैँसी बकैयाका कृषकहरूको परम्परागत तथा व्यावसायिक आधार हो। गर्मी समयमा शीतलता र बाह्रैमास सन्तुलित हरियो-पराल घाँस आवश्यक पर्छ।',
 'उच्च चिल्लोपनयुक्त दूध, कम्पोष्ट मल', 'बकैयाका तल्लो फाँटदेखि मध्य पहाडी टोलहरूमा दुग्ध संकलन र घरेलु आम्दानीको मुख्य स्रोत।',
 'न्यानो तथा समशीतोष्ण हावापानी (गर्मीमा आहाल वा शीतल पानी छर्कने व्यवस्था आवश्यक)', '६० देखि २००० मिटर',
 'लिमे, पारकोटे, गड्डी (स्थानीय पहाडी) र मुर्रा / मुर्रा क्रस (उन्नत दुधालु)',
 'पशु सेवा विभाग (DLS) / राष्ट्रिय भैँसी अनुसन्धान कार्यक्रम (NARC)', 'https://dls.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Murrah_buffalo.JPG/960px-Murrah_buffalo.JPG',
 'published', true, true, 2, '2026-11-26'),
('animal-goat', 'lcat-goat', 'बाख्रा', 'Goats', 'Capra hircus', 'goat',
 'बकैया गाउँपालिकाका पहाडी पाखो, धियाल, मन्थली र भित्री मधेस बस्तीमा कम लगानीमा छिटो आम्दानी दिने मुख्य पशुपालन बाख्रापालन हो। ओभानो टाँडयुक्त खोर, डालेघाँस र पीपीआर (PPR) तथा खोरेतविरुद्धको सचेतना अनिवार्य हुन्छ।',
 'मासु उत्पादन, प्राङ्गारिक जुतो/मल, साना किसान आयआर्जन', 'धियाल, मन्थली र शिखरपुरका पहाडी तथा वनछेउका बस्तीहरूमा डालेघाँस आधारित मुख्य उद्यम।',
 'सुक्खा तथा ओभानो हावापानीयुक्त खोर आवश्यक (चिसो र हिलोमा छिटो बिरामी पर्ने)', '६० देखि २५०० मिटर',
 'खरी (पहाडी मुख्य), तराई बाख्रा, सिन्हाल तथा जमुनापारी / बोयर क्रस (व्यावसायिक)',
 'राष्ट्रिय बाख्रा अनुसन्धान कार्यक्रम, बन्दिपुर (NARC) / FANSEP / DLS', 'https://narc.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Goat_in_Nepal_01.JPG/960px-Goat_in_Nepal_01.JPG',
 'published', true, true, 3, '2026-11-26'),
('animal-pig', 'lcat-pig', 'बंगुर', 'Pigs', 'Sus scrofa domesticus', 'pig',
 'छिटो तौल बढ्ने, सानो ठाउँमा पाल्न सकिने र उचित जैविक सुरक्षा तथा पकाएको सन्तुलित आहाराबाट राम्रो प्रतिफल दिने पशुपालन हो। खोरको निकास, सफा पिउने पानी र स्वाइन फिभरजस्ता रोगबाट जोगाउन कडा जैविक सुरक्षा आवश्यक हुन्छ।',
 'मासु उत्पादन, छिटो प्रतिफल उद्यम', 'पशुपालक समुदाय र तल्लो तथा मध्य पहाडी बस्तीहरूमा व्यावसायिक तथा पारिवारिक उद्यम।',
 'न्यानो र मध्यम हावापानी (पाठापाठीलाई चिसो र सीधै हावाबाट बचाउनुपर्ने)', '६० देखि १८०० मिटर',
 'च्वाँचे, हुर्रा, बाम्पू (स्थानीय) तथा पाख्रिबास कालो, योर्कशायर, ल्यान्डरेस, ड्युरोक क्रस',
 'पशु सेवा विभाग (DLS) / पाख्रिबास कृषि अनुसन्धान केन्द्र (NARC)', 'https://dls.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/9/97/Pig_In_Nepal.jpg',
 'published', true, true, 4, '2026-11-26'),
('animal-chicken', 'lcat-chicken', 'कुखुरा', 'Chickens', 'Gallus gallus domesticus', 'chicken',
 'स्थानीय (साकिनी/घाँटीखुइले) तथा उन्नत (गिरिराज/न्यु ह्याम्पसायर/व्यावसायिक) कुखुरापालनबाट अण्डा र मासु उत्पादन गरी घरपरिवारको पोषण र आयआर्जन बढाउन सकिन्छ। सुक्खा सोत्तर, चल्ला तातोपन (ब्रुडिङ) र रानीखेतविरुद्ध खोप सचेतना प्रमुख आधार हुन्।',
 'अण्डा, मासु र पारिवारिक पोषण', 'सबै वडामा करेसाबारी तथा व्यावसायिक खोरमार्फत उच्च माग भएको उद्यम।',
 'मध्यम तापक्रम र राम्रो हावा आवतजावत (साना चल्लालाई सुरुवाती हप्तामा न्यानो आवश्यक)', '६० देखि २०००+ मिटर',
 'साकिनी, घाँटीखुइले, प्वाँखउल्टे (स्थानीय); गिरिराज, न्यु ह्याम्पसायर, अस्ट्रोलर्प',
 'पशु सेवा विभाग (DLS) / FANSEP / नेपाल कृषि अनुसन्धान परिषद् (NARC)', 'https://fansep.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Giriraja_chicken_%282%29.JPG/960px-Giriraja_chicken_%282%29.JPG',
 'published', true, true, 5, '2026-11-26'),
('animal-fish', 'lcat-fish', 'माछापालन', 'Fish & Aquaculture', 'Cyprinus carpio / Rohu / Catla', 'fish',
 'तल्लो बकैया क्षेत्रका पानीको स्रोत पुगेका फाँटहरूमा कार्प तथा टिलापियाजस्ता माछाको बहुजातीय (Polyculture) पोखरी पालन उपयोगी हुन्छ। पानीको गुणस्तर, घुलनशील अक्सिजन र पोखरी चुन/प्राङ्गारिक व्यवस्थापन मुख्य पक्ष हुन्।',
 'मासु उत्पादन, सिँचाइ पोखरी बहुउपयोग', 'तल्लो बकैया तथा खोला किनारका न्यानो फाँटहरूमा जलस्रोत उपयुक्त क्षेत्र।',
 'न्यानो पानी (तराई तथा भित्री मधेस र न्यानो बेँसी)', '६० देखि ११०० मिटर (कार्प प्रजाति अनुसार)',
 'रोहु, नैनी (मृगल), भाकुर (कतला), कमन कार्प, सिल्भर कार्प, बिगहेड कार्प, ग्रास कार्प',
 'केन्द्रीय मत्स्य प्रवर्द्धन तथा संरक्षण केन्द्र / मत्स्य अनुसन्धान महाशाखा (NARC)', 'https://narc.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Common_carp_in_a_pond.jpg/960px-Common_carp_in_a_pond.jpg',
 'published', true, true, 6, '2026-11-26'),
('animal-sheep', 'lcat-sheep', 'भेडा', 'Sheep', 'Ovis aries', 'sheep',
 'भेडापालन सम्बन्धी बकैया स्थानीय अनुकूलता तथा प्राविधिक मार्गनिर्देशन समीक्षा क्रममा छ। उच्च तथा मध्य पहाडी भेगका लागि कागे र बरवाल प्रमुख नेपाली नस्ल हुन्।',
 'मासु तथा ऊन उत्पादन', 'माथिल्लो पहाडी चरन क्षेत्रमा सम्भावना; बकैया विशिष्ट विवरण प्राविधिक समीक्षामा।',
 'सुक्खा तथा चिसो-समशीतोष्ण हावापानी उपयुक्त', 'पहाड तथा उच्च पहाड (जात अनुसार)',
 'कागे (मध्य पहाड), बरवाल (उच्च पहाड)',
 'पशु सेवा विभाग (DLS) — प्राविधिक समीक्षामा', 'https://dls.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Sheep_Nepal.jpg/960px-Sheep_Nepal.jpg',
 'in_review', false, false, 7, '2026-11-26'),
('animal-duck', 'lcat-duck', 'हाँस', 'Ducks', 'Anas platyrhynchos domesticus', 'duck',
 'हाँसपालन र माछा-हाँस एकीकृत प्रणाली सम्बन्धी मकवानपुर प्राविधिक समीक्षा जारी छ। प्रमाणित भएपछि विस्तृत खण्डहरू प्रकाशन हुनेछन्।',
 'अण्डा तथा मासु उत्पादन', 'तल्लो फाँट र पोखरी छेउमा सम्भावना; स्थानीय पशु सेवा प्रमाणीकरण बाँकी।',
 'पानी उपलब्ध न्यानो क्षेत्र', 'तराई तथा भित्री मधेस',
 'खकी क्याम्पबेल, पेकिन, स्थानीय हाँस',
 'पशु सेवा विभाग (DLS) — स्रोत प्रमाणीकरण बाँकी', 'https://dls.gov.np',
 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Domestic_duck.jpg/960px-Domestic_duck.jpg',
 'needs_source', false, false, 8, '2026-11-26')
ON CONFLICT (id) DO UPDATE SET
    name_ne = EXCLUDED.name_ne,
    summary_ne = EXCLUDED.summary_ne,
    thumbnail_url = EXCLUDED.thumbnail_url,
    review_status = EXCLUDED.review_status,
    is_published = EXCLUDED.is_published,
    source_reference = EXCLUDED.source_reference;

-- Verified Breeds for Published Animals
INSERT INTO public.animal_breeds (id, animal_id, breed_name_ne, breed_name_en, origin_type, purpose_ne, climate_suitability_ne, characteristics_ne, source_reference, review_status, is_published, sort_order) VALUES
('breed-cattle-lulu', 'animal-cattle', 'लुलु / पहाडी स्थानीय गाई', 'Lulu / Local Hill Cattle', 'indigenous', 'स्थानीय हावापानी सहनशील दूध र मल', 'पहाडी र मिश्रित हावापानीमा सहज', 'रोग प्रतिरोधी क्षमता उच्च, थोरै दानामा पनि जीवन निर्वाह गर्न सक्ने।', 'NARC पशु आनुवंशिक स्रोत / DLS', 'published', true, 1),
('breed-cattle-jersey', 'animal-cattle', 'जर्सी क्रस गाई', 'Jersey Crossbred', 'crossbred', 'व्यावसायिक दूध उत्पादन', 'भित्री मधेस र मध्य पहाडका बकैयाजस्ता बस्तीका लागि उपयुक्त', 'मध्यम आकार, दूधमा चिल्लोपन राम्रो र नियमित हरियो घाँस तथा सन्तुलित आहारा आवश्यक।', 'DLS नेपाल पशु प्रजनन निर्देशिका', 'published', true, 2),
('breed-buffalo-murrah', 'animal-buffalo', 'मुर्रा / मुर्रा क्रस भैँसी', 'Murrah / Murrah Cross', 'crossbred', 'उच्च दूध उत्पादन', 'तल्ला फाँटदेखि तल्ला पहाडी टोलहरू (शीतल छहारी आवश्यक)', 'कालो रङ्ग, घुम्रिएको सिङ र उच्च दूधालु क्षमता।', 'DLS / राष्ट्रिय भैँसी अनुसन्धान कार्यक्रम (NARC)', 'published', true, 1),
('breed-buffalo-lime', 'animal-buffalo', 'लिमे र पारकोटे भैँसी', 'Lime & Parkote', 'indigenous', 'पहाडी चराउन र घरायसी दूध', 'मध्य पहाडी उकालो-ओरालो र पाखो क्षेत्रका लागि उपयुक्त', 'पहाडी वातावरणमा रमाउने, बलियो खुट्टा र रोग सहनशील स्थानीय नस्ल।', 'NARC राष्ट्रिय भैँसी अनुसन्धान कार्यक्रम', 'published', true, 2),
('breed-goat-khari', 'animal-goat', 'खरी बाख्रा (स्थानीय मुख्य)', 'Khari Goat', 'indigenous', 'मासु र नियमित जुम्ल्याहा पाठापाठी', 'मध्य पहाड र भित्री मधेस (बकैयामा अत्यन्त उपयुक्त)', 'नेपालको मुख्य स्थानीय जात; वर्षमा दुई बियाउँदा जुम्ल्याहा पाउने दर राम्रो, कम रोग लाग्ने।', 'राष्ट्रिय बाख्रा अनुसन्धान कार्यक्रम, बन्दिपुर (NARC)', 'published', true, 1),
('breed-goat-boercross', 'animal-goat', 'बोयर / जमुनापारी क्रस बाख्रा', 'Boer / Jamunapari Cross', 'crossbred', 'व्यावसायिक मासु उत्पादन', 'ओभानो खोर र पर्याप्त घाँस-दाना व्यवस्थापन भएको स्थान', 'स्थानीय खरी माउमा उन्नत बोकाको प्रजननबाट छिटो तौल बढ्ने पाठापाठी प्राप्त हुन्छ।', 'NARC बन्दिपुर / FANSEP', 'published', true, 2),
('breed-pig-pakhribas', 'animal-pig', 'पाख्रिबास कालो बंगुर', 'Pakhribas Black Pig', 'crossbred', 'व्यावसायिक तथा पहाडी बंगुरपालन', 'मध्य पहाड तथा भित्री मधेस', 'कालो रङ्ग, नेपाली कृषकमा लोकप्रिय, छिटो वृद्धि र राम्रो प्रजनन क्षमता।', 'NARC पाख्रिबास', 'published', true, 1),
('breed-chicken-sakini', 'animal-chicken', 'साकिनी तथा घाँटीखुइले (स्थानीय)', 'Sakini & Ghanti Khuile', 'indigenous', 'गाउँघरमा छाडा/अर्ध-खोर अण्डा र मासु', 'नेपालको तराईदेखि पहाडसम्म सबै हावापानी', 'रोग प्रतिरोधी, कम खर्च र उच्च स्थानीय बजार मूल्य।', 'NARC / FANSEP', 'published', true, 1),
('breed-chicken-giriraja', 'animal-chicken', 'गिरिराज / न्यु ह्याम्पसायर', 'Giriraja / New Hampshire', 'improved', 'अर्ध-व्यावसायिक दोहोरो प्रयोजन (अण्डा र मासु)', 'भित्री मधेस र मध्य पहाड', 'स्थानीय परिवेशमा सहज घुलमिल भई साकिनीभन्दा छिटो बढ्ने र धेरै अण्डा दिने।', 'DLS / FANSEP', 'published', true, 2),
('breed-fish-carps', 'animal-fish', 'कमन कार्प, रोहु, नैनी र भाकुर', 'Multi-Species Carp Polyculture', 'improved', 'तल्लो पोखरी बहुजातीय माछापालन', 'ताजा न्यानो पानी (तल्लो बकैया)', 'पोखरीको माथिल्लो, मध्य र तल्लो तहको प्राकृतिक आहारको सदुपयोग गरी उत्पादन बढाउँछ।', 'NARC मत्स्य अनुसन्धान महाशाखा', 'published', true, 1)
ON CONFLICT (id) DO UPDATE SET characteristics_ne = EXCLUDED.characteristics_ne, review_status = 'published', is_published = true;

-- Housing, Feeding, Breeding, Health, Production structured tables (never inventing numeric medicine dosages!)
INSERT INTO public.animal_housing_guidance (id, animal_id, shed_design_ne, floor_space_standard_ne, ventilation_light_ne, bedding_drainage_ne, sanitation_routine_ne, biosecurity_notes_ne, source_reference, review_status, is_published) VALUES
('house-cattle', 'animal-cattle', 'पूर्व-पश्चिम वा दक्षिण फर्किएको, सीधा चिसो हावा नलाग्ने तर ताजा हावा र घाम छिर्ने पक्की वा अर्ध-पक्की गोठ।', 'पशुको आकार अनुसार सहज उठबस र सुत्न पुग्ने (सटीक नापका लागि नजिकको पशु सेवा प्राविधिकसँग सल्लाह लिनुहोस्)।', 'माथिल्लो भागमा हावा आउजाउ हुने झ्याल तथा छाना उचाइ राख्ने।', 'पिसाब निकासका लागि हल्का भिरालो भुइँ र सुक्खा सोत्तर (पराल/भुस)।', 'गोबर र पिसाब बिहान-बेलुका सफा गरी गोबरग्यास वा कम्पोष्ट पिटमा जम्मा गर्ने।', 'बाहिरबाट ल्याएको नयाँ गाईलाई कम्तीमा २ हप्ता अलग (क्वारेन्टाइन) राख्ने।', 'DLS पशुपालन निर्देशिका', 'published', true),
('house-buffalo', 'animal-buffalo', 'गर्मीबाट जोगाउन अग्लो, शीतल र छायादार गोठ; नजिक सफा पानीको व्यवस्था।', 'वथान र शारीरिक बनावट अनुसार चिप्लो नहुने भुइँ।', 'गर्मीमा तातो हावा नथुनिने खुला हावा आवतजावत।', 'हल्का भिरालो खस्रो भुइँ ताकि भैँसी नचिप्लियोस्।', 'दैनिक गोबर सफाइ र सफा पानीले शरीर र थुन धुने बानी।', 'रोगी पशु र स्वस्थ पशुको गोठ तथा घाँस-पानीको भाँडा अलग राख्ने।', 'DLS / NARC भैँसी व्यवस्थापन', 'published', true),
('house-goat', 'animal-goat', 'भुइँबाट करिब २–३ फिट माथि काठ/बाँसको टाँड (Slatted Floor) बनाउने ताकि बड्कौँला र पिसाब तल झरोस्।', 'उमेर र समूह (ब्याउने, बोका, पाठापाठी) अनुसार अलग-अलग कोठा।', 'ओभानो, घाम छिर्ने तर रातको चिसो सिरेटो नछिर्ने जाली।', 'टाँडमुनि दैनिक सफाइ र सुक्खा बनाइराख्ने; चिसो र हिलो खोरमा राख्नुहुँदैन।', 'टाँड र भित्ता नियमित सफाइ तथा चुना छर्कने (चिकित्सकीय सरसफाइ सल्लाह अनुसार)।', 'नयाँ किनिएका बाख्रा सिधै बथानमा नमिसाउने; अलग निरीक्षण पछि मात्र मिसाउने।', 'NARC बन्दिपुर बाख्रापालन / FANSEP', 'published', true),
('house-pig', 'animal-pig', 'घाम पर्ने, राम्रो पानी निकास र बलियो पर्खाल वा जाली भएको पक्की वा अर्ध-पक्की खोर।', 'ब्याउने माउलाई पाठापाठी नकिचिऊन् भनी किनारा बार (Farrowing rail) सहितको अलग खोर।', 'ताजा हावा तर सानो पाठापाठीका लागि न्यानो कुना।', 'पिसाब र मल निकासका लागि नाली र ढल प्रबन्ध।', 'दिसा-पिसाब दैनिक सफा गरी दाना-पानीका डुँड स्वच्छ राख्ने।', 'बंगुरको खोरमा बाहिरी व्यक्ति, कुकुर वा चरा पस्न नदिने; प्रवेशद्वारमा खुट्टा धुने (Footbath) राख्ने।', 'DLS / NARC पाख्रिबास', 'published', true),
('house-chicken', 'animal-chicken', 'जालीदार झ्याल, शिकारी जनावर (स्याल, मुसा, बिरालो) नपस्ने सुरक्षित खोर।', 'साना चल्ला र ठूला कुखुरा अलग राख्ने; भीडभाड नगर्ने।', 'अमोनिया ग्यास बाहिर जान राम्रो हावा प्रवाह, तर चल्ला अवस्था भरि सिधा चिसो हावा रोक्ने।', 'काठको धुलो वा धानको भुसको कम्तीमा सुक्खा सोत्तर (Litter) व्यवस्थापन।', 'ब्याच सिध्दिएपछि खोर राम्रोसँग सफा गरी सुकाउने।', 'खोर बाहिर जूता चप्पल फेर्ने र रोगी कुखुरा तुरुन्त अलग राख्ने।', 'FANSEP / DLS कुखुरापालन पुस्तिका', 'published', true),
('house-fish', 'animal-fish', 'कम्तीमा १.५ मिटर गहिराइ हुने, बाढी नपस्ने र पानी भर/निकास सुविधा भएको माटो वा लाइनिङ पोखरी।', 'बहुजातीय पालनमा तल्लो, मध्य र सतह तहका माछा सन्तुलन (विस्तृत स्टकिङ घनत्व मत्स्य प्राविधिकबाट लिनुहोस्)।', 'खुला घाम लाग्ने (छायाँ रूख नजिक धेरै नभएको) स्थानमा पोखरी निर्माण।', 'पोखरीको डिल घाँस लगाएर सुरक्षित गर्ने ताकि माटो बगेर नजाओस्।', 'पोखरीको पानी धेरै हरियो वा गन्हाउने भएमा ताजा पानी थप्ने वा निकास गर्ने।', 'अन्य पोखरीका भिजेका जाल प्रयोग गर्दा सुकाएर वा सफा गरेर मात्र प्रयोग गर्ने।', 'NARC मत्स्य अनुसन्धान / DLS', 'published', true)
ON CONFLICT (id) DO UPDATE SET shed_design_ne = EXCLUDED.shed_design_ne, review_status = 'published', is_published = true;

INSERT INTO public.animal_feeding_guidance (id, animal_id, green_fodder_forage_ne, dry_roughage_ne, concentrate_feed_ne, mineral_salt_ne, clean_water_ne, ration_quantity_note_ne, feeding_frequency_ne, source_reference, review_status, is_published) VALUES
('feed-cattle', 'animal-cattle', 'नेपियर, सटारिया, बर्सिम, जै, डालेघाँस (टाँकी, कोइरालो, इपिलइपिल, किम्बु) आदि वर्षभरि हरियो घाँसको व्यवस्था।', 'पराल, नल र सुक्खा मकैको ढोड टुक्रा पारेर (चफिङ गरी) खुवाउँदा खेर जाँदैन।', 'दूध दिने तथा ब्याउने गाईलाई मकै, पिना, चोकर मिश्रित सन्तुलित दाना (सटीक मात्रा पशुको तौल र दूध उत्पादन अनुसार पशु प्राविधिकसँग तय गर्नुहोस्)।', 'खनिज ढिक्का (Mineral block) तथा आयोडिनयुक्त नुन नियमित राख्ने।', 'सफा र ताजा पिउने पानी २४ घण्टा उपलब्ध राख्ने।', 'पशुको उमेर, तौल र दुधालु चरण अनुसार सटीक केजी/ग्राम मात्रा स्थानीय पशु सेवा प्राविधिक वा DLS तालिकाबाट निर्धारण गर्नुहोस्।', 'दिनमा २–३ पटक दाना र नियमित हरियो/सुक्खा घाँस।', 'DLS / NARC पशु पोषण निर्देशिका', 'published', true),
('feed-buffalo', 'animal-buffalo', 'हरियो नेपियर, अमृसो, कोसेबाली र डालेघाँस।', 'पराल, ढोड र नल टुक्राएर खुवाउने; ढुसी परेको सुक्खा घाँस कहिल्यै नखुवाउने।', 'दूध उत्पादन र गर्भावस्था अनुसार सन्तुलित दाना र चोकर-पिना।', 'खनिज मिश्रण र नुनको व्यवस्था।', 'भैँसीलाई गाईभन्दा पनि बढी सफा पिउने पानी आवश्यकता पर्ने हुँदा स्वच्छ पानी उपलब्ध गराउने।', 'सटीक दैनिक मात्रा र खनिज अनुपात स्थानीय पशु प्राविधिकबाट लिनुहोस्।', 'दाना र घाँस निश्चित समय तालिकामा।', 'DLS भैँसी पोषण व्यवस्थापन', 'published', true),
('feed-goat', 'animal-goat', 'डालेघाँस (बडहर, खन्यु, किम्बु, इपिलइपिल, कुटमिरो) बाख्राका लागि अत्यन्त पौष्टिक र रुचिकर हुन्छ।', 'चिसो वर्षात् वा चरिचरन नभएको समयमा सुक्खा घाँस र कोसे घाँस (हें)।', 'व्याउने माउ, बोका र हुर्कँदै गरेका पाठापाठीलाई दाना (मकै/गहुँ/भटमास/चोकर) पूरक रूपमा।', 'खोरमा झुण्ड्याएको नुन र खनिज ढिक्का राख्ने।', 'स्वच्छ र ताजा पिउने पानी टाँड बाहिर नपोखिने भाँडामा राख्ने।', 'सटीक दाना अनुपात प्राविधिक सल्लाह अनुसार तय गर्नुहोस्; सडेगलेको वा प्लास्टिक मिसिएको आहारा नखुवाउनुहोस्।', 'बिहान-बेलुका डालेघाँस/सुक्खा घाँस र दिउँसो चरिचरन वा टाँडमै आहारा।', 'NARC बन्दिपुर / FANSEP', 'published', true),
('feed-pig', 'animal-pig', 'सफा हरियो सागपात, कर्कलो वा घाँस (पकाएर वा राम्रोसँग पखालेर)।', 'सुक्खा तथा रेशादार दाना पूरकहरू।', 'सन्तुलित बंगुर दाना; भान्सा वा होटलको बाँकी खानेकुरा (Swill) खुवाउँदा रोग सर्न नदिन अनिवार्य रूपमा राम्रोसँग पकाएर सेलाएपछि मात्र खुवाउने।', 'खनिज र सफा नुन सन्तुलन।', 'गर्मी तथा स्तनपान गराउने माउलाई सधैँ स्वच्छ पिउने पानी उपलब्ध राख्ने।', 'वजन चरण अनुसार सटीक दाना ग्राम/केजी प्राविधिक सल्लाह अनुसार।', 'निश्चित समयमा बिहान-बेलुका (प्याउ अवस्था अनुसार)।', 'DLS / पाख्रिबास बंगुरपालन', 'published', true),
('feed-chicken', 'animal-chicken', 'करेसाबारीमा छाडा कुखुरालाई हरियो साग र कीरा-फट्याङ्ग्रा प्राकृतिक स्रोत हुन्छन्।', 'लागू नहुने / पचाउन नसक्ने कडा सुक्खा पराल नदिने।', 'उमेर चरण (Starter, Grower, Layer/Finisher) अनुसार सन्तुलित दाना; ढुसी लागेको मकै/दाना कहिल्यै नखुवाउने।', 'क्याल्सियम (जस्तै अण्डाको बोक्रा वा चुनढुङ्गा ग्रिट — प्राविधिक सल्लाहमा) फुल पार्ने कुखुराका लागि उपयोगी।', 'सधैँ चिसो र सफा पिउने पानी घोप्टिन नसक्ने भाँडो (Drinker) मा।', 'उमेर र प्रजाति अनुसार दाना तालिका नजिकको पशु सेवा कार्यालयबाट लिनुहोस्।', 'सुक्खा नपोखिने गरी सफा फिडरमा।', 'FANSEP / DLS कुखुरापालन', 'published', true),
('feed-fish', 'animal-fish', 'ग्रास कार्पका लागि कलिलो घाँस र नेपियरका पात उपयोगी हुन्छन्।', 'लागू नहुने।', 'प्राङ्गारिक मलबाट तयार प्राकृतिक प्ल्याङ्कटन र धानको ढुटो-पिना मिश्रित पूरक दाना।', 'प्राविधिक जाँच अनुसार पोखरी चुन व्यवस्थापन।', 'माछापालनको प्रमुख माध्यम नै सफा, घुलनशील अक्सिजनयुक्त पानी हो।', 'पोखरीको माछाको कुल तौल अनुसार पूरक दाना मात्रा मत्स्य प्राविधिकबाट लिनुहोस्।', 'सफा र घमाइलो समयमा निश्चित ठाउँमा खुवाउने।', 'NARC मत्स्य अनुसन्धान', 'published', true)
ON CONFLICT (id) DO UPDATE SET clean_water_ne = EXCLUDED.clean_water_ne, review_status = 'published', is_published = true;

INSERT INTO public.animal_breeding_guidance (id, animal_id, breeding_age_weight_ne, estrus_heat_signs_ne, mating_ai_guidance_ne, pregnancy_incubation_period_ne, pregnant_care_ne, birthing_hatching_care_ne, newborn_chick_fry_care_ne, weaning_brooding_ne, source_reference, review_status, is_published) VALUES
('breedg-cattle', 'animal-cattle', 'नस्ल र शारीरिक विकास अनुसार प्रथम बाली जाने उमेर (कृपया स्थानीय पशु सेवा प्राविधिकबाट जाँच गराउनुहोस्)।', 'रमाउने, चिच्याउने, अन्य गाईमाथि चढ्ने, योनिबाट सफा लस्सादार पानी बग्ने।', 'बाली आएको १२–१८ घण्टाभित्र प्रमाणित साँढे वा कृत्रिम गर्भाधान (AI) प्राविधिकमार्फत गर्भाधान गराउने।', 'गर्भ अवधि करिब ९ महिना ९ दिन (२८०–२८५ दिन)।', 'गर्भवती गाईलाई अन्तिम २ महिनामा सन्तुलित आहारा र शान्त, चिप्लो नहुने गोठ; भारी काम र तनाबबाट जोगाउने।', 'ब्याउने समयमा सफा स्थान, नतर्साई निगरानी; कठिनाइ भएमा तुरुन्त पशु चिकित्सक बोलाउने।', 'जन्मेको १–२ घण्टाभित्र अनिवार्य रूपमा माउको खिकाउरो (Colostrum) दूध खुवाउने र नाभी सफा राख्ने।', 'क्रमिक रूपमा कलिलो हरियो घाँस र सफा पानीमा बानी बसाल्ने।', 'DLS पशु प्रजनन निर्देशिका', 'published', true),
('breedg-buffalo', 'animal-buffalo', 'नस्ल र शारीरिक तौल अनुसार परिपक्वता (कम्तीमा स्वस्थ तौल पुगेपछि मात्र प्रजनन)।', 'भैँसीमा बाली लक्षण सूक्ष्म (Silent Heat) हुने भएकाले बिहान-बेलुका सूक्ष्म निरीक्षण र भाले/प्राविधिक जाँच जरुरी हुन्छ।', 'सही समयमा गुणस्तरीय मुर्रा राँगो वा कृत्रिम गर्भाधान सेवा प्रयोग गर्ने।', 'गर्भ अवधि करिब १० महिना १० दिन (३०५–३१५ दिन)।', 'गर्भावस्थाको पछिल्लो चरणमा हरियो घाँस, खनिज र शीतल आरामदायी गोठ।', 'ब्याउने कोठा सुक्खा र सफा; ब्याएपछि माउलाई मनतातो पानी र सहज आराम।', 'पाडापाडीको नाक-मुख पुछिदिने र तुरुन्त खिकाउरो दूध पिलाउने।', 'रोग प्रतिरोधी क्षमता बढाउन सफा दूध र क्रमिक घाँसमा ल्याउने।', 'DLS / NARC भैँसी प्रजनन', 'published', true),
('breedg-goat', 'animal-goat', 'शारीरिक विकास पूरा भएपछि मात्र पहिलो पटक ब्याउन लगाउने (नस्ल अनुसार फरक पर्ने हुँदा प्राविधिक सल्लाह लिनुहोस्)।', 'पुच्छर हल्लाउने, कराउने, बेचेन देखिने र योनि हल्का सुन्निने/रातो हुने।', 'हाडनाता प्रजनन (Inbreeding) रोक्न हरेक २ वर्षमा उन्नत बोका साटफेर गर्ने।', 'गर्भ अवधि करिब ५ महिना (१४५–१५५ दिन)।', 'व्याउने बाख्रालाई ठोक्किने वा उफ्रिन नदिने, अलग सुरक्षित ठाउँ र पोषिलो डालेघाँस दिने।', 'ब्याउने ठाउँ ओभानो र सोत्तरयुक्त राख्ने; जुम्ल्याहा पाठापाठीको नाक-मुख सफा गरिदिने।', 'जन्मेपछि तुरुन्तै माउको बिगौती (खिकाउरो) दूध खुवाउने र चिसोबाट बचाउने।', 'पाठापाठीलाई करिब २–३ हप्तापछि कलिला कोसा/घाँस चाट्न बानी पार्ने।', 'NARC बन्दिपुर / FANSEP', 'published', true),
('breedg-pig', 'animal-pig', 'स्वस्थ शारीरिक वृद्धि र परिपक्वता पुगेपछि प्रजनन गराउने।', 'खानेकुरा कम खाने, कान ठाडो पार्ने, ढाडमा थिच्दा स्थिर उभिने (Standing heat)।', 'स्वस्थ र हाडनाता बाहिरको उन्नत भाले वा कृत्रिम गर्भाधान।', 'गर्भ अवधि करिब ३ महिना ३ हप्ता ३ दिन (११४ दिन)।', 'व्याउने माउलाई शान्त र नचिप्लिने कोठा तथा सन्तुलित आहारा।', 'ब्याउने कोठामा पाठापाठी नकिचिने बार (Guard rail) राख्ने र न्यानो पार्ने।', 'जन्मेपछि तुरुन्तै न्यानोपन र आमाको पहिलो दूध (खिकाउरो) सुनिश्चित गर्ने।', 'सफा दाना र पानीमा क्रमिक रूपमा छुटाउने।', 'DLS / पाख्रिबास', 'published', true),
('breedg-chicken', 'animal-chicken', 'प्रजाति अनुसार अण्डा पार्न थाल्ने र भाले व्यवस्थापन।', 'लागू नहुने।', 'ह्याचरी वा विश्वसनीय स्रोतबाट स्वस्थ चल्ला वा सेक्ने (कोरल्ने) गुणस्तरीय अण्डा छनोट।', 'ओथारो (Incubation) अवधि २१ दिन।', 'कोरल्ने पोथीलाई शान्त, सफा र जुम्रा-उपियाँमुक्त गुँड।', 'ओथारो बसेको समयमा सफा पानी र दाना नजिक राख्ने।', 'पहिलो २–३ हप्ता चल्लालाई चिसोबाट जोगाउन सुरक्षित ब्रुडिङ (तातोपन) र सफा पानी अनिवार्य हुन्छ।', 'उमेर र वृद्धि अनुसार सुरक्षित खोर र दानामा सार्ने।', 'FANSEP / DLS कुखुरापालन', 'published', true),
('breedg-fish', 'animal-fish', 'प्राविधिक ह्याचरीबाट गुणस्तरीय र स्वस्थ भुरा (Fingerlings) खरिद गर्ने।', 'लागू नहुने।', 'आधिकारिक सरकारी वा प्रमाणित निजी मत्स्य ह्याचरीबाट विभिन्न जातीय भुरा चयन।', 'लागू नहुने।', 'पोखरी तयारी (सुकाउने, चुन राख्ने, प्राकृतिक आहारा तयार गर्ने)।', 'भुरा पोखरीमा सार्दा तापक्रम मिलाउन (Acclimatization) थैलीलाई पोखरीकै पानीमा १५–२० मिनेट तैरन दिने।', 'साना भुराको आकार अनुसार शिकारी जीव (भ्यागुता/सर्प/जंगली माछा) बाट पोखरी सुरक्षित राख्ने।', 'आकार अनुसार पोखरी विभाजन।', 'NARC मत्स्य अनुसन्धान', 'published', true)
ON CONFLICT (id) DO UPDATE SET newborn_chick_fry_care_ne = EXCLUDED.newborn_chick_fry_care_ne, review_status = 'published', is_published = true;

INSERT INTO public.animal_health_guidance (id, animal_id, daily_hygiene_ne, biosecurity_isolation_ne, vaccination_awareness_ne, deworming_parasite_control_ne, warning_signs_ne, vet_referral_guidance_ne, source_reference, review_status, is_published) VALUES
('health-cattle', 'animal-cattle', 'गोठ सफा र सुक्खा राख्ने, थुन धुने र दाना-पानीका भाँडा नियमित पखाल्ने।', 'बाहिरबाट ल्याइएका पशु अलग राख्ने, बिरामी पशु छुट्टै बाँध्ने र गोठमा अनावश्यक प्रवेश रोक्ने।', 'खोरेत (FMD), भ्यागुते (HS) र चर्चरे (BQ) जस्ता संक्रामक रोगविरुद्ध समय तालिका अनुसार खोप सचेतना; सटीक खोप मिति स्थानीय पशु प्राविधिक वा NVPL कार्यतालिकाबाट लिनुहोस्।', 'आन्तरिक जुका तथा बाह्य परजीवी (किरना/उपियाँ) लक्षण देखिएमा गोठ सफाइ र पशु प्राविधिकको सल्लाहमा उचित जुका नियन्त्रण अपनाउने।', 'अचानक दाना/उग्राउन छोड्नु, उच्च ज्वरो, मुख र खुट्टामा फोका आउनु, रगत वा पीप मिसिएको दूध आउनु, सास फेर्न गाह्रो हुनु।', 'कुनै पनि औषधि वा इन्जेक्सन आफूखुसी प्रयोग नगर्नुहोस्। तुरुन्त नजिकको पशु सेवा प्राविधिक वा भेटेरिनरी अस्पताल मकवानपुरमा सम्पर्क गर्नुहोस्।', 'DLS / NVPL खोप तथा रोग सचेतना', 'published', true),
('health-buffalo', 'animal-buffalo', 'गोठ र शीतल पानी आहाल क्षेत्र सफा राख्ने; दुध दुहुनुअघि हात र थुन धुने।', 'रोग फैलिएको समयमा सामूहिक चरिचरन र बाहिरी राँगो सम्पर्क रोक्ने।', 'भ्यागुते (HS) र खोरेत (FMD) विरुद्ध स्थानीय पशु सेवा अभियान अनुसार समयमै खोप दिलाउने।', 'भैँसीमा नाम्ले जुका (Liver fluke) लगायतका परजीवीको जोखिम रहने हुँदा हिलो/सिमसार घाँस व्यवस्थापन र प्राविधिक जाँच गराउने।', 'घाँटी वा छाती सुन्निने, घरघर सास आउने, अचानक दूध घट्ने, खाना र उग्राउने बन्द हुने।', 'आकस्मिक लक्षण देखिएमा ढिला नगरी पशु चिकित्सक वा बकैया पशु सेवा शाखामा सम्पर्क गर्नुहोस्।', 'DLS / NVPL', 'published', true),
('health-goat', 'animal-goat', 'टाँडमुनिको सोत्तर सुक्खा राख्ने र दाना-पानी भाँडा सफा गर्ने।', 'बाहिरी मेला/बजारबाट ल्याइएका बाख्रालाई कम्तीमा १५ दिन अलग राख्ने।', 'पीपीआर (PPR), खोरेत र धनुष्टङ्कार जस्ता रोगविरुद्ध स्थानीय पशु प्राविधिकको तालिका अनुसार खोप सुनिश्चित गर्ने।', 'ऋतु परिवर्तनअघि र पछि जुका, किरना र लुतो जस्ता परजीवीका लागि पशु प्राविधिकको सल्लाहमा जाँच र नियन्त्रण।', 'आँखा/नाकबाट बाक्लो चिप्रा बग्नु, मुखमा घाउ, निरन्तर पखाला, झोक्राएर बस्नु वा बथानबाट छुट्नु।', 'पीपीआर वा पखाला लक्षण देखिएमा तुरुन्त बिरामी बाख्रा अलग गरी पशु प्राविधिक बोलाउनुहोस्; जथाभावी एन्टिबायोटिक नदिनुहोस्।', 'NARC बन्दिपुर / NVPL / DLS', 'published', true),
('health-pig', 'animal-pig', 'खोर र पिसाब नाली दैनिक धुने र चिसो हावा तथा फोहोर पानी जम्न नदिने।', 'अफ्रिकन स्वाइन फिभर र क्लासिकल स्वाइन फिभर रोकथामका लागि बाहिरी व्यक्ति/सवारी निषेध, खुट्टा धुने घोल र काँचो मासु/होटल अवशेष नपकाई नखुवाउने।', 'क्लासिकल स्वाइन फिभर र खोरेत लगायतका खोपका लागि स्थानीय पशु सेवा कार्यालयसँग परामर्श गर्ने।', 'जुका र छालाको लुतो नियन्त्रणका लागि नियमित सरसफाइ र प्राविधिक परामर्श।', 'खाना नखाने, उच्च ज्वरो, शरीर वा कानमा नीलो-रातो धब्बा देखिनु, बान्ता/पखाला वा अचानक मृत्यु हुनु।', 'अस्वाभाविक मृत्यु वा ज्वरो फैलिएमा मृत बंगुर नखोलीकन र नबेची तुरुन्त भेटेरिनरी अस्पताल मकवानपुरलाई खबर गर्नुहोस्।', 'DLS / NVPL', 'published', true),
('health-chicken', 'animal-chicken', 'पानी र दानाको भाँडा दिनहुँ सफा गर्ने तथा ओसिलो सोत्तर हटाउने।', 'एकैपटक विभिन्न उमेरका कुखुरा नमिसाउने र मरेका कुखुरालाई सुरक्षित खाडलमा गाड्ने।', 'रानीखेत (Newcastle), गम्बोरो र बिफर (Fowl Pox) विरुद्धको खोप बारे नजिकको पशु सेवा प्राविधिक वा ह्याचरी तालिका पालना गर्ने।', 'खोर सफाइ र चल्ला अवस्थाको कक्सिडियोसिस (रगत मासी) रोकथामका लागि सुक्खा सोत्तर।', 'पङ्ख झारेर झोक्राउनु, सास फेर्दा घुरघुर आवाज, हरियो वा रगत मिसिएको सुली, खुट्टा लुलो हुनु वा अण्डा घट्नु।', 'सामूहिक मृत्यु वा लक्षण देखिएमा तुरुन्त रोगी कुखुरा अलग गरी पशु प्राविधिकको सल्लाह लिनुहोस्।', 'FANSEP / NVPL / DLS', 'published', true),
('health-fish', 'animal-fish', 'पोखरीमा कुहिने वस्तु, प्लास्टिक वा अत्यधिक मलखाद नफाल्ने।', 'बाहिरबाट ल्याएका भुरा र उपकरण सफा नगरी पोखरीमा नराख्ने।', 'माछामा खोप सामान्यतः नहुने; रोग रोकथामका लागि स्वच्छ पानी, अक्सिजन र सही घनत्व नै मुख्य उपाय हो।', 'बाह्य परजीवी र फङ्गस रोकथामका लागि पानीको गुणस्तर र माछाको छाला नियमित अवलोकन।', 'माछा बिहान सतहमा आएर हावा खाएको देखिनु (अक्सिजन कमी), शरीरमा रातो घाउ वा सेतो ढुसी, खान छोड्नु।', 'सतहमा माछा तैरिन थालेमा तुरुन्त ताजा पानी थप्ने र मत्स्य प्राविधिकसँग परामर्श गर्ने।', 'NARC मत्स्य अनुसन्धान / DLS', 'published', true)
ON CONFLICT (id) DO UPDATE SET vaccination_awareness_ne = EXCLUDED.vaccination_awareness_ne, review_status = 'published', is_published = true;

INSERT INTO public.animal_diseases (id, animal_id, name_ne, name_en, pathogen_type, symptoms_ne, prevention_biosecurity_ne, immediate_farmer_action_ne, veterinary_caution_ne, source_reference, review_status, is_published, sort_order) VALUES
('dis-cattle-fmd', 'animal-cattle', 'खोरेत रोग (FMD)', 'Foot and Mouth Disease', 'viral', 'मुख, जिब्रो, गिजा र खुरको कापमा पानी फोका आउनु, र्‍याल काढ्नु, खोच्याउनु र दूध एक्कासी घट्नु।', 'नियमित खोप र नयाँ वा रोगी पशु अलग्गै (क्वारेन्टाइन) राख्नु।', 'रोगी पशुलाई तुरुन्त अलग बाँधी नरम आहारा र सफा पानी दिनुहोस्।', 'औषधि वा घाउ उपचारका लागि तुरुन्त पशु प्राविधिकको प्रत्यक्ष सल्लाह लिनुहोस्।', 'DLS / NVPL', 'published', true, 1),
('dis-cattle-mastitis', 'animal-cattle', 'थुनेलो (Mastitis)', 'Mastitis', 'bacterial', 'थुन सुन्निनु, रातो वा तातो हुनु, छुँदा दुख्नु र दूध फाटेर चिप्रा वा रगत आउनु।', 'गोठ सुक्खा राख्नु र दुहुनुअघि तथा पछि सफा पानीले थुन र हात धुनु।', 'थुनेलो शंका लागेको थुनको दूध अलग भाँडोमा दुहेर सुरक्षित विसर्जन गर्ने।', 'एन्टिबायोटिक वा मलहम पशु चिकित्सकको जाँचबिना प्रयोग नगर्नुहोस्।', 'DLS पशु स्वास्थ्य', 'published', true, 2),
('dis-buffalo-hs', 'animal-buffalo', 'भ्यागुते रोग (HS)', 'Haemorrhagic Septicaemia', 'bacterial', 'उच्च ज्वरो, घाँटी र छाती तल्लो भाग सुन्निनु, घुरघुर गरी सास फेर्नु।', 'वर्षात् सुरु हुनुअघि नियमित खोप (स्थानीय पशु प्राविधिकको तालिकामा) दिलाउनु।', 'तुरुन्त अन्य पशुबाट अलग गरी प्राविधिक सेवामा खबर गर्नुहोस्।', 'यो आकस्मिक संक्रामक रोग भएकाले तत्काल पशु चिकित्सकको उपचार आवश्यक हुन्छ।', 'DLS / NVPL', 'published', true, 1),
('dis-goat-ppr', 'animal-goat', 'पीपीआर / बाख्राको हैजा (PPR)', 'Peste des Petits Ruminants', 'viral', 'उच्च ज्वरो, नाक र आँखाबाट बाक्लो सिँगान-चिप्रा, गिजा/मुखमा घाउ र गनाउने पखाला।', 'PPR विरुद्धको खोप र बाहिरी बाख्रा १५ दिन अलग राख्नु।', 'रोगी बाख्रालाई बथानबाट तुरुन्त अलग गरी सफा र सुक्खा ठाउँमा राख्नुहोस्।', 'सटीक सहयोगी उपचार र बथान संरक्षणका लागि तुरुन्त पशु सेवा प्राविधिक बोलाउनुहोस्।', 'NARC बन्दिपुर / NVPL', 'published', true, 1),
('dis-chicken-ranikhet', 'animal-chicken', 'रानीखेत रोग (Newcastle Disease)', 'Newcastle Disease', 'viral', 'झोक्राउनु, सास फेर्न गाह्रो, हरियो र पानीजस्तो सुली, घाँटी बाङ्गो पारिनु वा पक्षघात।', 'उमेर अनुसार नियमित रानीखेत खोप र खोर जैविक सुरक्षा।', 'संक्रमित कुखुरा अलग राखी खोर प्रवेश नियन्त्रण गर्ने र मरेका सुरक्षित गाड्ने।', 'भाइरल रोग भएकाले खोप नै मुख्य बचावट हो; प्राविधिक जाँच गराउनुहोस्।', 'DLS / NVPL', 'published', true, 1),
('dis-fish-eus', 'animal-fish', 'रातो घाउ रोग (EUS) तथा अक्सिजन कमी', 'Epizootic Ulcerative Syndrome / Hypoxia', 'other', 'शरीरमा रातो दाग वा खाल्डो परेको घाउ देखिनु; बिहान माछा सतहमा छटपटाउनु।', 'पोखरी सफाइ, अन्य पोखरीको भिजेको जाल प्रयोग नगर्ने र उचित घनत्व।', 'तुरुन्त ताजा पानी प्रवाह बढाउने र घना माछा घटाउने।', 'पोखरी उपचार र रसायन मात्रा मत्स्य प्राविधिकको परीक्षणपछि मात्र प्रयोग गर्नुहोस्।', 'NARC मत्स्य अनुसन्धान', 'published', true, 1)
ON CONFLICT (id) DO UPDATE SET symptoms_ne = EXCLUDED.symptoms_ne, review_status = 'published', is_published = true;

INSERT INTO public.animal_production_guidance (id, animal_id, primary_products_ne, clean_production_practice_ne, post_harvest_handling_ne, manure_waste_management_ne, marketing_notes_ne, source_reference, review_status, is_published) VALUES
('prod-cattle', 'animal-cattle', 'ताजा दूध, घ्यू, गोबरमल', 'दुहुनुअघि बाल्टिन, हात र कल्चौंडो सफा पानीले धोई सुक्खा कपडाले पुछ्ने।', 'दुध दुहेपछि सफा जालीले छानी चिसो ठाउँमा राखी छिटो डेरी संकलन केन्द्र पुर्‍याउने।', 'गोबर र सोत्तर जम्मा गरी गोबरग्यास वा गुणस्तरीय कम्पोष्ट मल बनाएर बालीमा प्रयोग गर्ने।', 'बकैयाका स्थानीय दुग्ध सहकारी र हेटौँडा बजार केन्द्र।', 'DLS स्वच्छ दूध उत्पादन', 'published', true),
('prod-buffalo', 'animal-buffalo', 'उच्च चिल्लोपनयुक्त दूध, खुवा/दही, मल', 'सफा स्टिल वा आल्मुनियमको भाँडो प्रयोग गर्ने (प्लास्टिक नचोर्ने)।', 'छिटो संकलन वा सफा चिस्यान र प्रशोधन।', 'भैँसीको गोबर र पिसाब संकलनबाट खेतबारीमा उच्च प्राङ्गारिक उर्वरता बढ्छ।', 'खुवा र दूधको स्थानीय तथा क्षेत्रीय माग।', 'DLS / NARC', 'published', true),
('prod-goat', 'animal-goat', 'जिउँदो खसी/बोका, मासु तथा बहुमूल्य जुतो मल', 'स्वस्थ शारीरिक तौल, नियमित जुका नियन्त्रण र सफा चराउने/खुवाउने।', 'तौल नापेर उचित बजार मूल्य तय गर्ने अभ्यास।', 'बाख्राको जुतो तरकारी र अदुवा खेतीका लागि उत्कृष्ट प्राङ्गारिक मल हो।', 'बकैया कृषि-पशुपालन सहकारी तथा नजिकका खसी बजार।', 'NARC बन्दिपुर', 'published', true),
('prod-pig', 'animal-pig', 'मासु तथा प्राङ्गारिक मल', 'पकाएको स्वच्छ आहारा र रोगमुक्त जैविक सुरक्षित हुर्काइ।', 'सुरक्षित ढुवानी र वधशाला सरसफाइ।', 'मलमूत्रलाई कम्पोष्ट पिट वा बायोग्यास प्लान्टमा व्यवस्थापन।', 'स्थानीय तथा क्षेत्रीय बजार संकलन।', 'DLS', 'published', true),
('prod-chicken', 'animal-chicken', 'ताजा अण्डा, मासु र कुखुराको मल (सुली)', 'सफा गुँड (Nest box) राखी अण्डा फुट्न र फोहोर हुन नदिने।', 'अण्डा दिनहुँ संकलन गरी चिसो सुक्खा ठाउँमा भण्डारण गर्ने।', 'कुखुराको सुली राम्रोसँग कुहाएर (कम्पोष्ट बनाएर) मात्र तरकारी बालीमा प्रयोग गर्ने।', 'स्थानीय बजार तथा विद्यालय/परिवार पोषण।', 'FANSEP / DLS', 'published', true),
('prod-fish', 'animal-fish', 'ताजा कार्प माछा', 'अस्वस्थ वा ढुसीयुक्त माछा नराख्ने; समात्नुअघि पूरक आहारा सफा व्यवस्थापन।', 'ताजा माछा बरफ वा चिसो सफा टोकरीमा सुरक्षित बजार पठाउने।', 'पोखरी सफाइ गर्दा निस्किएको मलिलो हिलो तरकारी बारीमा मलका रूपमा उपयोगी।', 'छतिवन, हात्तीसुँडे र हेटौँडा ताजा माछा बजार।', 'NARC मत्स्य अनुसन्धान', 'published', true)
ON CONFLICT (id) DO UPDATE SET primary_products_ne = EXCLUDED.primary_products_ne, review_status = 'published', is_published = true;

-- Verified Animal Images
INSERT INTO public.animal_images (id, animal_id, media_asset_id, image_url, medium_url, thumb_url, caption_ne, stage_category, creator_name, source_url, license_type, review_status, is_published) VALUES
('aimg-cattle-1', 'animal-cattle', 'media-animal-cattle', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Zebu.JPG/960px-Zebu.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Zebu.JPG/640px-Zebu.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Zebu.JPG/320px-Zebu.JPG', 'स्वस्थ गाई पालन र पशुपालन दृश्य', 'breed', 'Wikimedia Commons Contributor', 'https://commons.wikimedia.org/wiki/File:Zebu.JPG', 'CC BY-SA 3.0', 'published', true),
('aimg-buffalo-1', 'animal-buffalo', 'media-animal-buffalo', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Murrah_buffalo.JPG/960px-Murrah_buffalo.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Murrah_buffalo.JPG/640px-Murrah_buffalo.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Murrah_buffalo.JPG/320px-Murrah_buffalo.JPG', 'उन्नत मुर्रा भैँसी नस्ल', 'breed', 'Kleomarlo', 'https://commons.wikimedia.org/wiki/File:Murrah_buffalo.JPG', 'CC BY-SA 3.0', 'published', true),
('aimg-goat-1', 'animal-goat', 'media-animal-goat', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Goat_in_Nepal_01.JPG/960px-Goat_in_Nepal_01.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Goat_in_Nepal_01.JPG/640px-Goat_in_Nepal_01.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Goat_in_Nepal_01.JPG/320px-Goat_in_Nepal_01.JPG', 'नेपालमा स्थानीय बाख्रापालन', 'breed', 'Punya', 'https://commons.wikimedia.org/wiki/File:Goat_in_Nepal_01.JPG', 'CC BY-SA 4.0', 'published', true),
('aimg-pig-1', 'animal-pig', 'media-animal-pig', 'https://upload.wikimedia.org/wikipedia/commons/9/97/Pig_In_Nepal.jpg', 'https://upload.wikimedia.org/wikipedia/commons/9/97/Pig_In_Nepal.jpg', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/Pig_In_Nepal.jpg/320px-Pig_In_Nepal.jpg', 'नेपालमा बंगुरपालन दृश्य', 'breed', 'Ravijung', 'https://commons.wikimedia.org/wiki/File:Pig_In_Nepal.jpg', 'CC BY-SA 4.0', 'published', true),
('aimg-chicken-1', 'animal-chicken', 'media-animal-chicken', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Giriraja_chicken_%282%29.JPG/960px-Giriraja_chicken_%282%29.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Giriraja_chicken_%282%29.JPG/640px-Giriraja_chicken_%282%29.JPG', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Giriraja_chicken_%282%29.JPG/320px-Giriraja_chicken_%282%29.JPG', 'गिरिराज कुखुरा पालन', 'breed', 'Irvin Calicut', 'https://commons.wikimedia.org/wiki/File:Giriraja_chicken_(2).JPG', 'CC BY-SA 3.0', 'published', true),
('aimg-fish-1', 'animal-fish', 'media-animal-fish', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Common_carp_in_a_pond.jpg/960px-Common_carp_in_a_pond.jpg', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Common_carp_in_a_pond.jpg/640px-Common_carp_in_a_pond.jpg', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Common_carp_in_a_pond.jpg/320px-Common_carp_in_a_pond.jpg', 'ताजा पानी पोखरीमा कमन कार्प माछा', 'housing', 'Keimzelle', 'https://commons.wikimedia.org/wiki/File:Common_carp_in_a_pond.jpg', 'CC0 1.0 Public Domain', 'published', true)
ON CONFLICT (id) DO UPDATE SET image_url = EXCLUDED.image_url, review_status = 'published', is_published = true;

-- ----------------------------------------------------------------------------
-- 6. VERIFIED LIVESTOCK YOUTUBE VIDEOS & TOPIC PLAYLISTS
-- ----------------------------------------------------------------------------
INSERT INTO public.educational_videos (
    id, target_type, crop_id, animal_id, topic_id, title_ne, description_ne,
    channel_name, thumbnail_url, video_url, youtube_video_id, duration, provider,
    download_permission, is_sample_media, review_status, is_published, source_url
) VALUES
('vid-goat-1', 'animal', NULL, 'animal-goat', 6, 'बाख्रा पालन, सानन बाख्रा तथा खोरेत रोगबाट बचाउने उपाय',
 'कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal) द्वारा प्रसारित बाख्रा नस्ल र खोरेत रोग रोकथाम कार्यक्रम।',
 'AITC NEPAL', 'https://i.ytimg.com/vi/SI-uU3OPuQA/hqdefault.jpg',
 'https://www.youtube.com/watch?v=SI-uU3OPuQA', 'SI-uU3OPuQA', '14:20', 'youtube', false, false, 'published', true, 'https://www.youtube.com/watch?v=SI-uU3OPuQA'),
('vid-fish-1', 'animal', NULL, 'animal-fish', 6, 'माछा पालन तथा रोग सम्वन्धी जिज्ञासाहरु समाधान',
 'कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal) को पोखरी माछापालन तथा पानी/रोग व्यवस्थापन जानकारी।',
 'AITC NEPAL', 'https://i.ytimg.com/vi/_iAf0XdVQlc/hqdefault.jpg',
 'https://www.youtube.com/watch?v=_iAf0XdVQlc', '_iAf0XdVQlc', '15:10', 'youtube', false, false, 'published', true, 'https://www.youtube.com/watch?v=_iAf0XdVQlc'),
('vid-chicken-1', 'animal', NULL, 'animal-chicken', 9, 'कुखुरा पालन कृषक शिक्षा तथा खोर व्यवस्थापन',
 'पशु चिकित्सक डा. राज कुमार यादवद्वारा कुखुरापालन, खोर सरसफाइ र जैविक सुरक्षा सम्बन्धी शिक्षा।',
 'Dr. Raj kumar Yadav', 'https://i.ytimg.com/vi/knAeTgCV050/hqdefault.jpg',
 'https://www.youtube.com/watch?v=knAeTgCV050', 'knAeTgCV050', '12:40', 'youtube', false, false, 'published', true, 'https://www.youtube.com/watch?v=knAeTgCV050'),
('vid-pig-1', 'animal', NULL, 'animal-pig', 1, 'बंगुरपालनबाट फाइदा लिन गर्नै पर्ने प्रमुख व्यवस्थापन कार्यहरू',
 'खोर निर्माण, सरसफाइ र आहारा सन्तुलनमा बंगुरपालन सफल बनाउने व्यावहारिक जानकारी।',
 'AgroDev Nepal', 'https://i.ytimg.com/vi/PFNYdE24Lh0/hqdefault.jpg',
 'https://www.youtube.com/watch?v=PFNYdE24Lh0', 'PFNYdE24Lh0', '09:45', 'youtube', false, false, 'published', true, 'https://www.youtube.com/watch?v=PFNYdE24Lh0'),
('vid-buffalo-1', 'animal', NULL, 'animal-buffalo', 9, 'नेपालमै भैँसी पालन तथा दुग्ध व्यवस्थापन',
 'व्यावसायिक भैँसी पालन, गोठ सुधार र स्वच्छ दूध उत्पादन सम्बन्धी नेपाली भिडियो सामग्री।',
 'NAYAK Krishi TV', 'https://i.ytimg.com/vi/t9xJ7npsDLw/hqdefault.jpg',
 'https://www.youtube.com/watch?v=t9xJ7npsDLw', 't9xJ7npsDLw', '11:30', 'youtube', false, false, 'published', true, 'https://www.youtube.com/watch?v=t9xJ7npsDLw'),
('vid-cattle-1', 'animal', NULL, 'animal-cattle', 7, 'गाई पालन तथा डेरी व्यवस्थापन तालिम',
 'स्वच्छ गोठ, दाना-घाँस र गाईपालन दुग्ध व्यवस्थापन सम्बन्धी नेपाली तालिम भिडियो।',
 'Desh Paradesh Media Network', 'https://i.ytimg.com/vi/1jX3jKaib8g/hqdefault.jpg',
 'https://www.youtube.com/watch?v=1jX3jKaib8g', '1jX3jKaib8g', '10:50', 'youtube', false, false, 'published', true, 'https://www.youtube.com/watch?v=1jX3jKaib8g')
ON CONFLICT (id) DO UPDATE SET
    title_ne = EXCLUDED.title_ne,
    video_url = EXCLUDED.video_url,
    review_status = 'published',
    is_published = true;

INSERT INTO public.video_playlists (id, target_type, crop_id, animal_id, title_ne, title_en, description_ne, review_status, is_published, sort_order) VALUES
('pl-livestock-goat', 'animal', NULL, 'animal-goat', 'बाख्रापालन तथा खोप/रोग सचेतना श्रृंखला', 'Goat Farming & Biosecurity Playlist', 'बाख्राको खोर, नस्ल र खोरेत/PPR सचेतना भिडियो संग्रह।', 'published', true, 1),
('pl-livestock-fish', 'animal', NULL, 'animal-fish', 'माछापालन तथा पोखरी व्यवस्थापन श्रृंखला', 'Aquaculture & Pond Health Playlist', 'ताजा पानीको माछापालन र समस्या समाधान भिडियो।', 'published', true, 2),
('pl-livestock-poultry', 'animal', NULL, 'animal-chicken', 'कुखुरापालन कृषक शिक्षा श्रृंखला', 'Poultry Management Playlist', 'खोर सफाइ, चल्ला हेरचाह र रोग रोकथाम भिडियो।', 'published', true, 3)
ON CONFLICT (id) DO UPDATE SET title_ne = EXCLUDED.title_ne, review_status = 'published', is_published = true;

INSERT INTO public.video_playlist_items (id, playlist_id, video_id, position) VALUES
('pli-goat-1', 'pl-livestock-goat', 'vid-goat-1', 1),
('pli-fish-1', 'pl-livestock-fish', 'vid-fish-1', 1),
('pli-poultry-1', 'pl-livestock-poultry', 'vid-chicken-1', 1)
ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 7. VERIFIED ACTIVE HELPLINE CONTACTS (Bakaiya & Makwanpur)
-- ----------------------------------------------------------------------------
INSERT INTO public.helpline_contacts (
    id, name_ne, designation_ne, department_ne, department_en, contact_category,
    ward_ne, ward_number, coverage_area_ne, mobile_number, email, office_hours_ne,
    source_reference, source_url, verified_at, is_verified, is_active, review_status, is_published, sort_order
) VALUES
('help-bakaiya-agri-1', 'हिमश्वेता भट्ट', 'कृषि प्राविधिक (बकैया कृषि सेवा)',
 'निशुल्क कृषि प्राविधिक सेवा — बकैया कृषि शाखा', 'Free Agriculture Technical Service — Bakaiya Agriculture Branch', 'agriculture',
 'बकैया गाउँपालिका क्षेत्र (सबै वडा समन्वय)', 0, 'बकैया गाउँपालिका, मकवानपुर', '9868445081', 'bakaiyaagri@gmail.com',
 'आइतबार–शुक्रबार (कार्यालय समय: १०:०० – १७:००)', 'बकैया गाउँपालिका आधिकारिक पोर्टल (bakaiyakrishi.gov.np)', 'https://bakaiyakrishi.gov.np', '2026-11-26', true, true, 'published', true, 1),
('help-bakaiya-office', 'बकैया गाउँपालिका कार्यपालिकाको कार्यालय', 'आधिकारिक सम्पर्क (कृषि तथा पशुपालन शाखा समन्वय)',
 'गाउँपालिका कृषि तथा पशु सेवा समन्वय', 'Bakaiya Rural Municipality Coordination Desk', 'municipality',
 'बकैया गाउँपालिका (हात्तीसुँडे मुख्यालय)', 0, 'बकैया गाउँपालिका, मकवानपुर', '9855088066', 'bakaiyagaunpalika@gmail.com',
 'आइतबार–शुक्रबार (सरकारी कार्यालय समय)', 'बकैया गाउँपालिका आधिकारिक पोर्टल (bakaiyakrishi.gov.np)', 'https://bakaiyakrishi.gov.np', '2026-11-26', true, true, 'published', true, 2),
('help-makwanpur-vet', 'भेटेरिनरी अस्पताल तथा पशु सेवा विज्ञ केन्द्र, मकवानपुर', 'जिल्ला पशु चिकित्सक तथा पशुपालन प्राविधिक सेवा',
 'निशुल्क पशु स्वास्थ्य तथा प्राविधिक परामर्श (मकवानपुर)', 'District Veterinary Hospital & Livestock Expert Center, Makwanpur', 'veterinary',
 'मकवानपुर जिल्ला (बकैयासहित सबै स्थानीय तह)', 0, 'हेटौँडा तथा मकवानपुर जिल्ला कार्यक्षेत्र', '057-412828', 'vhospitalmak@gmail.com',
 'आइतबार–शुक्रबार (आकस्मिक पशुरोग परामर्श समन्वय)', 'बागमती प्रदेश आधिकारिक निर्देशिका (bagamati.gov.np)', 'https://bagamati.gov.np', '2026-11-26', true, true, 'published', true, 3),
('help-makwanpur-vet-mob', 'भेटेरिनरी अस्पताल मोबाइल सेवा (मकवानपुर)', 'पशु रोग तथा आकस्मिक परामर्श सम्पर्क',
 'पशु चिकित्सा तथा रोग नियन्त्रण सेवा', 'Makwanpur Veterinary Hospital Mobile Desk', 'veterinary',
 'मकवानपुर जिल्ला', 0, 'मकवानपुर तथा बकैया क्षेत्र', '9855070728', 'vhospitalmak@gmail.com',
 'सरकारी कार्यालय समय', 'बागमती प्रदेश आधिकारिक निर्देशिका (bagamati.gov.np)', 'https://bagamati.gov.np', '2026-11-26', true, true, 'published', true, 4)
ON CONFLICT (id) DO UPDATE SET
    name_ne = EXCLUDED.name_ne,
    designation_ne = EXCLUDED.designation_ne,
    department_ne = EXCLUDED.department_ne,
    mobile_number = EXCLUDED.mobile_number,
    source_reference = EXCLUDED.source_reference,
    verified_at = EXCLUDED.verified_at,
    is_active = true,
    is_verified = true,
    review_status = 'published',
    is_published = true;

-- Sample Animal Content Sections demonstrating 25-section coverage:
-- Verified sections = 'published', Dosage/Quantitative commercial formulation sections = 'needs_source'
INSERT INTO public.animal_content_sections (
    id, animal_id, section_number, section_key, title_ne, title_en, body_ne, body_en,
    is_applicable, source_reference, source_document_id, review_status, is_published, sort_order
) VALUES
('asec-cattle-1', 'animal-cattle', 1, 'overview', '१. पशु परिचय र बकैया अनुकूलता', '1. Species Overview & Bakaiya Suitability',
 'बकैया गाउँपालिकाका तल्ला फाँटदेखि मध्य पहाडसम्म दूध र प्राङ्गारिक मलका लागि गाई-गोरु पालन प्रचलित छ।', 'Dairy cattle and draft oxen across Bakaiya.',
 true, 'DLS / NARC', 'src-dls-manual', 'published', true, 1),
('asec-cattle-16', 'animal-cattle', 16, 'vaccination_schedule', '१६. खोप तालिका सचेतना', '16. Vaccination Schedule Awareness',
 'खोरेत (FMD), भ्यागुते (HS) र चर्चरे (BQ) विरुद्धको खोप आवश्यक पर्दछ। सटीक खोप मिति, खोपको नाम र मात्रा स्थानीय पशु प्राविधिक वा भेटेरिनरी अस्पताल मकवानपुरबाट यकिन गर्नुहोस्।',
 'Preventive awareness for FMD, HS, BQ vaccines via NVPL/DLS technician guidance.',
 true, 'NVPL / DLS', 'src-nvpl-vaccine', 'published', true, 16),
('asec-cattle-ration-draft', 'animal-cattle', 11, 'ration_by_weight_stage', '११. तौल अनुसार दैनिक दाना-पानी मात्रा तालिका', '11. Daily Ration Weight Table',
 'पशु प्राविधिक तथा NARC बाट आधिकारिक ग्राम/केजी दाना तालिका प्रमाणीकरण बाँकी छ।',
 'Pending DLS/NARC exact kg ration table verification.',
 true, 'Pending NARC Animal Nutrition Table', 'src-dls-manual', 'needs_source', false, 11),
('asec-goat-1', 'animal-goat', 1, 'overview', '१. बाख्रापालन परिचय र बकैया अनुकूलता', '1. Goat Overview & Suitability',
 'धियाल, मन्थली र भित्री मधेसमा खरी तथा उन्नत क्रस बाख्रापालन प्रमुख आयस्रोत हो।', 'Goat husbandry across Bakaiya mid-hills and inner valley.',
 true, 'NARC Bandipur / FANSEP', 'src-narc-bandipur', 'published', true, 1),
('asec-goat-9', 'animal-goat', 9, 'housing_slatted', '९. टाँडयुक्त खोर तथा आवास प्रबन्ध', '9. Elevated Slatted Goat Housing',
 'ओभानो भुइँ, २–३ फिट अग्लो काठ वा बाँसको टाँड र चिसो सिरेटोबाट जोगाउने व्यवस्था।', 'Elevated slatted floor housing keeping goats dry.',
 true, 'NARC Bandipur / FANSEP', 'src-narc-bandipur', 'published', true, 9)
ON CONFLICT (id) DO UPDATE SET body_ne = EXCLUDED.body_ne, review_status = EXCLUDED.review_status, is_published = EXCLUDED.is_published;

-- Citations for core records
INSERT INTO public.content_citations (id, target_type, target_id, section_key, source_document_id, page_or_section, excerpt, verified_by) VALUES
('cit-crop-paddy', 'crop', 'crop-paddy', 'general', 'src-narc-diary', 'Cereal Crop Section', 'Verified NARC variety list, temperature 15-35C, altitude 60-1500m.', 'technical_reviewer'),
('cit-animal-cattle', 'animal', 'animal-cattle', 'general', 'src-dls-manual', 'Dairy Husbandry', 'Verified cattle housing, clean water, FMD/HS/BQ preventive referral.', 'technical_reviewer'),
('cit-animal-goat', 'animal', 'animal-goat', 'general', 'src-narc-bandipur', 'Goat Compendium', 'Verified Khari & crossbred management, elevated slatted sheds, PPR prevention.', 'technical_reviewer'),
('cit-help-vet', 'helpline_contact', 'help-makwanpur-vet', 'contact', 'src-vhospital-makwanpur', 'Official Contact Directory', 'Verified 057-412828 and 9855070728 from bagamati.gov.np.', 'technical_reviewer')
ON CONFLICT (id) DO NOTHING;

UPDATE public.content_sync_metadata SET last_updated_at = NOW(), version = version + 1 WHERE table_name IN ('crops', 'crop_images', 'educational_videos');
INSERT INTO public.content_sync_metadata (table_name, last_updated_at, version) VALUES
('animals', NOW(), 1),
('animal_categories', NOW(), 1),
('helpline_contacts', NOW(), 1),
('video_playlists', NOW(), 1)
ON CONFLICT (table_name) DO UPDATE SET last_updated_at = NOW(), version = public.content_sync_metadata.version + 1;
