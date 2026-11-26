package com.krishisaathi.app.data.local

/**
 * Agricultural Knowledge Content Store for Bakaiya Krishi Saathi.
 *
 * Data Integrity Policy (Section 6.5, Section 8, Section 10, Section 16):
 * - Environmental ranges, varieties, and cultivation details align with NARC / MoALD Nepal publications.
 * - Unverified or unavailable optional metrics are stored as null rather than invented.
 * - YouTube URLs point to verified public Nepali agricultural videos (AITC Nepal, NTV NARC, Krishi TV, etc.).
 * - Authorized offline sample MP4 files are explicitly flagged with `isSampleMedia = true` so users
 *   understand they are sample test videos demonstrating offline ExoPlayer capability.
 * - Helpline contacts contain ONLY publicly verified contacts from Bakaiya Rural Municipality (bakaiyakrishi.gov.np).
 */
object BakaiyaSeedData {

    val categories = listOf(
        CategoryEntity(id = "cat-cereal", nameNe = "खाद्यान्न बाली", nameEn = "Cereals & Food Grains", iconName = "grain"),
        CategoryEntity(id = "cat-vegetable", nameNe = "तरकारी बाली", nameEn = "Vegetables", iconName = "eco"),
        CategoryEntity(id = "cat-fruit", nameNe = "फलफूल बाली", nameEn = "Fruits", iconName = "park"),
        CategoryEntity(id = "cat-pulse", nameNe = "दलहन बाली", nameEn = "Pulses & Legumes", iconName = "spa"),
        CategoryEntity(id = "cat-oilseed", nameNe = "तेलहन बाली", nameEn = "Oilseeds", iconName = "water_drop"),
        CategoryEntity(id = "cat-cash", nameNe = "मसला तथा नगदे बाली", nameEn = "Spices & Cash Crops", iconName = "storefront")
    )

    fun categoryThumbnailUrl(categoryId: String): String = when (categoryId) {
        "cat-cereal" -> "https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg"
        "cat-vegetable" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/640px-Chou-fleur_02.jpg"
        "cat-fruit" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/640px-Banana_tree_%28musa%29.jpg"
        "cat-pulse" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/640px-Lens_culinaris_sl13.jpg"
        "cat-oilseed" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/640px-Among_Mustard_Dreams.jpg"
        "cat-cash" -> "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/640px-Ginger_plant.jpg"
        else -> "https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg"
    }

    val crops = listOf(
        BakaiyaCropEntity(
            id = "crop-paddy",
            categoryId = "cat-cereal",
            nameNe = "धान",
            nameEn = "Paddy / Rice",
            scientificName = "Oryza sativa L.",
            descriptionNe = "धान बकैया गाउँपालिकाको तल्लो उपत्यका तथा खोला किनारका फाँटहरू (छतिवन, हात्तीसुँडे, शिखरपुर) को प्रमुख खाद्यान्न बाली हो।",
            varieties = "सावित्री, राधा-४, रामधान, हर्दिनाथ-३, सुख्खा धान-३",
            climateOverview = "धान खेतीका लागि न्यानो र ओसिलो हावापानी उपयुक्त मानिन्छ। बाली वृद्धि चरणमा पर्याप्त चिस्यान र पाक्ने बेलामा घमाइलो मौसम आवश्यक हुन्छ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeOptimalMinMeters = 60,
                altitudeOptimalMaxMeters = 1000,
                altitudeMaxMeters = 1500,
                altitudeSummaryNe = "६० देखि १५०० मिटर (बकैयाका तल्लो फाँट तथा टार)",
                altitudeGrowthNotesNe = "सामान्य उन्नत जात १५०० मिटरसम्म उपयुक्त हुन्छन्; जुम्लाजस्ता उच्च हिमाली/पहाडी भेगमा चिसो सहने विशेष स्थानीय जात मात्र खेती गरिन्छ। उचाइ बढ्दा पाक्ने अवधि लम्बिन सक्छ।",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस तथा मध्य पहाडका नदी उपत्यका र बकैया तल्लो फाँट",
                soilTypesNe = "पानी अडिने चिम्टाइलो र दोमट माटो",
                soilTextureNe = "चिम्टाइलो दोमट (Clay loam देखि Silty loam)",
                soilPhMin = 5.5,
                soilPhMax = 6.5,
                soilDrainageNe = "हिल्याएको खेतमा पानी अडाउन सक्ने, तर पाक्ने बेला निकास मिल्ने",
                soilFertilityNe = "नाइट्रोजन, फस्फोरस, पोटास तथा जिङ्कयुक्त उर्वर माटो",
                soilOrganicMatterNe = "राम्ररी कुहिएको गोबर/कम्पोस्ट वा हरियो मल लाभदायक",
                tempMin = 15.0,
                tempOptimalMin = 20.0,
                tempOptimalMax = 32.0,
                tempMax = 35.0,
                humidityMin = null,
                humidityMax = null,
                rainfallMinMm = 1000,
                rainfallMaxMm = 1500,
                rainfallPeriodLabelNe = "बाली अवधिमा (वा सो बराबर सिँचाइ)",
                waterRequirementNe = "उच्च — खेतमा नियमित चिस्यान तथा हिलो व्यवस्थापन आवश्यक",
                sunlightNe = "दैनिक ६ देखि ८ घण्टा पूर्ण घाम आवश्यक",
                plantingSeasonNe = "असार १ देखि साउन १५ सम्म (बर्खे धान)",
                sowingPeriodNe = "जेठ–असार (मुख्य बर्खे ब्याड); फागुन (चैते धान)",
                transplantingPeriodNe = "२०–३० दिनको बेर्ना (असार–साउन)",
                growingDurationNe = "११० देखि १६० दिन (जात अनुसार)",
                harvestingPeriodNe = "असोज–मंसिर (बर्खे बाली)",
                irrigationGuidanceNe = "बेर्ना सारेको सुरुवाती दिनमा २-३ से.मी. पानी राख्ने। गाँज आउने र पसाउने (फूल फुल्ने) चरणमा खेत सुक्न नदिने।",
                specialConsiderationsNe = "फूल फुल्ने बेला १५°C भन्दा कम वा ३५°C भन्दा बढी तापक्रम भएमा बाला नभरिन (बाँझोपन) सक्छ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "१. खेतलाई २-३ पटक जोतेर पानी लगाई हिल्याउने।\n२. डल्ला फुटाई पाटा लगाएर खेत सम्याउने।\n३. आली मर्मत गरी झारपात सडाउने।",
            seedSelection = "१. स्वस्थ, पुष्ट र प्रमाणित बीउ छान्ने।\n२. प्रति रोपनी करिब २.५ देखि ३ केजी बीउको ब्याड राख्ने।\n३. नुनपानी घोलमा तैरिएका भुस/कमजोर बीउ हटाउने।",
            spacing = "लाइनदेखि लाइन २० से.मी. र बोटदेखि बोट १५ से.मी. दूरीमा प्रति गाँज २-३ वटा बेर्ना रोप्ने।",
            fertilizer = "नेपाल कृषि अनुसन्धान परिषद् (NARC) को सामान्य सिफारिस अनुसार प्राङ्गारिक कम्पोस्ट मल र नाइट्रोजन, फस्फोरस तथा पोटास सन्तुलित रूपमा प्रयोग गर्ने (माटो परीक्षण प्रतिवेदन अनुसार मात्रा निर्धारण गर्नु उत्तम हुन्छ)।",
            cropCare = "रोपेको २५-३० दिन र ४५-५० दिनमा गरी दुई पटक गोडमेल गर्ने। खेतका आली सफा राख्ने।",
            pestDiseaseControl = "मरुवा (Blast), डढुवा र गवारो कीरा प्रमुख समस्या हुन्। रोग प्रतिरोधी जात लगाउने र प्राविधिकको सल्लाहमा मात्र विषादी प्रयोग गर्ने।",
            harvesting = "बालाको ८५-९० प्रतिशत दाना पहेँलो भई पाकेपछि कटानी गर्ने र घाममा सुकाएर दाउनी गर्ने।",
            storage = "दानालाई राम्ररी सुकाई चिस्यान १२ प्रतिशतभन्दा कम बनाएर सफा भकारी, सुपरग्रेन ब्याग वा मेटालिक ड्रममा हावा नछिर्ने गरी राख्ने।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / कृषि डायरी / FAO EcoCrop",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-maize",
            categoryId = "cat-cereal",
            nameNe = "मकै",
            nameEn = "Maize / Corn",
            scientificName = "Zea mays L.",
            descriptionNe = "मकै बकैया गाउँपालिकाको मध्य तथा उच्च पहाडी पाखो बारी (धियाल, मन्थली, ठूलो धियाल) को प्रमुख खाद्यान्न बाली हो।",
            varieties = "देउती, शितला, मनकामना-३, रामपुर कम्पोजिट, पोषिलो मकै-१",
            climateOverview = "मकै न्यानो हावापानी मन पराउने तर पानी जमेको नसहने बाली हो। बकैयाका पहाडी बारीहरूमा वसन्त तथा वर्षे याममा सफलतापूर्वक खेती गरिन्छ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeOptimalMinMeters = 300,
                altitudeOptimalMaxMeters = 1800,
                altitudeMaxMeters = 2500,
                altitudeSummaryNe = "तराईदेखि उच्च पहाडसम्म (६० देखि २५०० मिटर)",
                altitudeGrowthNotesNe = "उचाइ बढेसँगै तापक्रम घट्ने भएकाले मध्य तथा उच्च पहाडमा पाक्ने अवधि लामो हुन्छ (१४०–१८० दिन)। बकैयाका पाखो बारी ३००–१८०० मिटर क्षेत्र मकैका लागि अत्यन्त उपयुक्त छ।",
                suitableZonesCsv = "terai,inner_terai,mid_hill,high_hill",
                suitableRegionsNe = "तराई, भित्री मधेस, मध्य पहाड र उच्च पहाडी क्षेत्र (बकैयाको धियाल, मन्थली क्षेत्रसहित)",
                soilTypesNe = "पानीको राम्रो निकास भएको, प्राङ्गारिक पदार्थयुक्त दोमट र बलौटे दोमट माटो",
                soilTextureNe = "दोमट, पाँगो दोमट वा बलौटे दोमट",
                soilPhMin = 5.5,
                soilPhMax = 7.0,
                soilDrainageNe = "अत्यन्त राम्रो निकास (जलजमाव नसहने)",
                soilFertilityNe = "उच्च नाइट्रोजन र सन्तुलित NPK आवश्यक",
                soilOrganicMatterNe = "प्रशस्त कुहिएको गोबर/कम्पोस्ट मल आवश्यक",
                tempMin = 10.0,
                tempOptimalMin = 21.0,
                tempOptimalMax = 27.0,
                tempMax = 35.0,
                humidityMin = null,
                humidityMax = null,
                rainfallMinMm = 600,
                rainfallMaxMm = 1200,
                rainfallPeriodLabelNe = "बाली अवधिमा",
                waterRequirementNe = "मध्यम — पानी जम्न दिनु हुँदैन, जुँगा निस्कने र दाना भरिने बेला चिस्यान अनिवार्य",
                sunlightNe = "पूर्ण घाम आवश्यक",
                plantingSeasonNe = "फागुन १५ देखि वैशाख १५ (वर्षे मकै)",
                sowingPeriodNe = "वसन्त: फागुन–चैत; वर्षे पहाड: वैशाख–जेठ; हिउँदे तराई: असोज",
                transplantingPeriodNe = null,
                growingDurationNe = "११० देखि १८० दिन (उचाइ र जात अनुसार)",
                harvestingPeriodNe = "साउन–भदौ (वर्षे पहाड) / असार (वसन्त)",
                irrigationGuidanceNe = "जरा फैलाउने, घुँडा आउने, चमरा तथा जुँगा निस्कने र दूध पस्ने अवस्थामा सिँचाइ दिनु महत्वपूर्ण हुन्छ।",
                specialConsiderationsNe = "बीउ उम्रन माटोको तापक्रम कम्तीमा १०°C आवश्यक हुन्छ र बिरुवा तुषारोप्रति संवेदनशील हुन्छ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "१. हिउँदको वर्षापछि जमिनलाई २-३ पटक गहिरो जोत्ने।\n२. माटो बुर्बुराउँदो बनाउने र पानी निकासको कुलेसो मिलाउने।",
            seedSelection = "१. प्रमाणित तथा उपचारित बीउ छान्ने।\n२. प्रति रोपनी करिब १ देखि १.२ केजी बीउ कुलेसोमा रोप्ने।",
            spacing = "लाइनदेखि लाइन ६०-७५ से.मी. र बोटदेखि बोट २५ से.मी. दूरी कायम गर्ने।",
            fertilizer = "गोबर/कम्पोस्ट मल र आधारभूत मल (डीएपी, पोटास तथा आधा युरिया) रोप्ने बेलामा प्रयोग गरी बाँकी युरिया घुँडा-घुँडा भएपछि र चमरा आउनुअघि टपड्रेस गर्ने (माटो परीक्षण अनुसार मात्रा यकिन गर्ने)।",
            cropCare = "रोपेको २५-३० दिनमा पहिलो गोडमेल गरी फेदमा उकेरा (माटो चढाउने) लगाउने र बाक्लो बिरुवा छाँट्ने।",
            pestDiseaseControl = "अमेरिकन फौजी कीरा (Fall Armyworm) को गुभोमा नियमित निगरानी गर्ने। फेरोमोन ट्र्याप, निमजन्य जैविक विषादी वा कृषि प्राविधिकको सिफारिस अनुसार नियन्त्रण अपनाउने।",
            harvesting = "घोगाको खोस्टा सुकेर खैरो भएपछि र दानाको फेदमा कालो दाग (Black layer) बनेपछि भाँच्ने।",
            storage = "घोगा वा छोडाएको दानालाई घाममा राम्ररी सुकाई चिस्यान १२ प्रतिशतभन्दा कम भएपछि सुक्खा स्थान वा हर्मेटिक ब्यागमा भण्डारण गर्ने।",
            sourceReference = "राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC) / कृषि डायरी",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg/640px-Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-cauliflower",
            categoryId = "cat-vegetable",
            nameNe = "काउली",
            nameEn = "Cauliflower",
            scientificName = "Brassica oleracea var. botrytis",
            descriptionNe = "काउली बकैया गाउँपालिकाका कृषकहरूको प्रमुख व्यावसायिक तरकारी बाली हो। हिउँदे तथा बेमौसमी दुवै याममा खेती गरी राम्रो आम्दानी लिन सकिन्छ।",
            varieties = "काठमाडौँ स्थानीय, स्नो मिस्टिक, सिल्भर कप (मौसम अनुसार अगौटे/मध्यम/पछौटे जात)",
            climateOverview = "चिसो तथा समशीतोष्ण हावापानी काउलीको गुँद (Curd) विकासका लागि उत्तम हुन्छ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 70,
                altitudeOptimalMinMeters = 800,
                altitudeOptimalMaxMeters = 1800,
                altitudeMaxMeters = 2000,
                altitudeSummaryNe = "७० देखि २००० मिटर (तराई हिउँदेदेखि पहाडी बेमौसमीसम्म)",
                altitudeGrowthNotesNe = "तराई र बेँसीमा हिउँदे बालीको रूपमा तथा मध्य–उच्च पहाड (८००–२००० मिटर) मा कसिलो गुँद विकास हुने व्यावसायिक तथा बेमौसमी खेती सफल हुन्छ।",
                suitableZonesCsv = "terai,inner_terai,mid_hill,high_hill",
                suitableRegionsNe = "तराई, भित्री मधेस, मध्य पहाड र उच्च पहाडी तरकारी पकेट क्षेत्र (काभ्रे, धनकुटा, मकवानपुर)",
                soilTypesNe = "प्राङ्गारिक पदार्थयुक्त, पानी नजम्ने मलिलो दोमट माटो",
                soilTextureNe = "मलिलो दोमट वा बलौटे दोमट",
                soilPhMin = 6.0,
                soilPhMax = 6.8,
                soilDrainageNe = "उत्तम निकास भएको उँचो ब्याड आवश्यक",
                soilFertilityNe = "उच्च पोषक तत्वका साथै सूक्ष्म तत्व बोरोन र मोलिब्डेनम आवश्यक",
                soilOrganicMatterNe = "प्रशस्त कुहिएको गोबर/कम्पोस्ट मल आवश्यक",
                tempMin = 10.0,
                tempOptimalMin = 15.0,
                tempOptimalMax = 24.0,
                tempMax = 27.0,
                humidityMin = null,
                humidityMax = null,
                rainfallMinMm = 500,
                rainfallMaxMm = 700,
                rainfallPeriodLabelNe = "बाली अवधिमा (वा नियमित सिँचाइ)",
                waterRequirementNe = "नियमित हल्का चिस्यान आवश्यक, तर जलजमाव हुन नहुने",
                sunlightNe = "दैनिक खुला घाम आवश्यक",
                plantingSeasonNe = "भदौ देखि कात्तिक (मुख्य हिउँदे याम); पहाडी भेगमा बेमौसमी खेती समेत हुने",
                sowingPeriodNe = "असार–असोज (अगौटे/मध्यम/पछौटे जात अनुसार नर्सरी)",
                transplantingPeriodNe = "बीउ छरेको ३–४ हप्तापछि (साउन–कात्तिक)",
                growingDurationNe = "बेर्ना सारेपछि ५५ देखि १३० दिन (जात अनुसार)",
                harvestingPeriodNe = "कात्तिक–फागुन (पहाडमा बेमौसमी समेत)",
                irrigationGuidanceNe = "बेर्ना सारेपछि तुरुन्त सिँचाइ गरी माटोको चिस्यान हेरी साप्ताहिक वा आवश्यकता अनुसार पानी दिने।",
                specialConsiderationsNe = "अत्याधिक गर्मीमा फूल खुकुलो (Ricey) हुन्छ र ५.५ भन्दा कम अम्लीय माटोमा गाँठे रोग (Clubroot) लाग्ने जोखिम उच्च हुन्छ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "१. जमिनलाई ३-४ पटक जोती डल्ला फुटाउने।\n२. पानी निकासका लागि उँचो ब्याड र कुलेसो बनाउने।",
            seedSelection = "नर्सरी ब्याडमा स्वस्थ बेर्ना तयार पारी ४-५ पात आएपछि मुख्य खेतमा सार्ने।",
            spacing = "जात अनुसार लाइनदेखि लाइन ५० से.मी. र बोटदेखि बोट ४५ से.मी.।",
            fertilizer = "प्रशस्त कुहिएको गोबरमलका साथै सिफारिस अनुसार सन्तुलित रासायनिक मल तथा सूक्ष्म तत्व (बोरोन/मोलिब्डेनम) प्रयोग गर्ने।",
            cropCare = "काउलीको फूल पहेँलो हुन नदिन नजिकैको पातले फूललाई हल्का ढाकिदिने (Blanching) र गोडमेल गर्ने।",
            pestDiseaseControl = "गाँठे रोग (Clubroot) बाट बच्न माटोको pH सन्तुलन र बाली चक्र अपनाउने। पुतली लार्भा र लाही कीराको जैविक तथा सुरक्षित व्यवस्थापन गर्ने।",
            harvesting = "फूल कसिलो र सफा सेतो छँदै, कल्ली फाट्नुअघि नै धारिलो चक्कुले पातसहित काट्ने।",
            storage = "काटेपछि छायादार र चिसो स्थानमा राखी छिटो ताजा अवस्था मै बजार पठाउने।",
            sourceReference = "राष्ट्रिय तरकारी बाली विकास केन्द्र / कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC) / FAO",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/640px-Chou-fleur_02.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-tomato",
            categoryId = "cat-vegetable",
            nameNe = "गोलभेंडा",
            nameEn = "Tomato",
            scientificName = "Solanum lycopersicum L.",
            descriptionNe = "गोलभेंडा खुला खेत तथा प्लाष्टिक टनेल प्रविधिबाट उत्पादन गरिने बकैया क्षेत्रको प्रमुख नगदे तरकारी बाली हो।",
            varieties = "सिर्जना (NARC हाइब्रिड), मनप्रिया, सम्झना",
            climateOverview = "न्यानो र घमाइलो हावापानी गोलभेंडाका लागि उपयुक्त हुन्छ। तुषारो र अत्यधिक वर्षाले बोट तथा फलमा असर गर्दछ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeOptimalMinMeters = 800,
                altitudeOptimalMaxMeters = 1600,
                altitudeMaxMeters = 2000,
                altitudeSummaryNe = "६० देखि २००० मिटर (तराईदेखि मध्य पहाड टनेल प्रविधि)",
                altitudeGrowthNotesNe = "तराईमा हिउँद/वसन्तमा खुला खेत तथा ८००–२००० मिटर मध्य पहाडी क्षेत्रमा प्लाष्टिक टनेल प्रविधिबाट वर्षे र बेमौसमी गुणस्तरीय उत्पादन हुन्छ।",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस तथा मध्य पहाडी व्यावसायिक तरकारी पकेट (मकवानपुर, काभ्रे, ललितपुर)",
                soilTypesNe = "पानी निकास भएको गहिरो र उर्वर बलौटे दोमट वा चिम्टाइलो दोमट माटो",
                soilTextureNe = "बलौटे दोमट वा हल्का चिम्टाइलो दोमट",
                soilPhMin = 6.0,
                soilPhMax = 6.8,
                soilDrainageNe = "अत्यन्त राम्रो निकास (उँचो ड्याङ)",
                soilFertilityNe = "सन्तुलित NPK र क्याल्सियम पोषण आवश्यक",
                soilOrganicMatterNe = "प्रशस्त पाकेको गोबर/कम्पोस्ट मल आवश्यक",
                tempMin = 10.0,
                tempOptimalMin = 18.0,
                tempOptimalMax = 27.0,
                tempMax = 35.0,
                humidityMin = null,
                humidityMax = null,
                rainfallMinMm = 600,
                rainfallMaxMm = 800,
                rainfallPeriodLabelNe = "बाली अवधिमा (वर्षामा टनेल सुरक्षित)",
                waterRequirementNe = "नियमित सन्तुलित चिस्यान; थोपा सिँचाइ उपयोगी",
                sunlightNe = "दैनिक ६-८ घण्टा घाम आवश्यक",
                plantingSeasonNe = "तल्लो क्षेत्रमा भदौ-असोज र माघ-फागुन; टनेलमा वर्षैभरि अनुकूल व्यवस्थापन",
                sowingPeriodNe = "साउन–भदौ (हिउँदे), मंसिर–पुस (वसन्त), चैत (पहाड बेमौसमी)",
                transplantingPeriodNe = "ब्याड राखेको २५–३० दिनपछि (४–५ पातमा)",
                growingDurationNe = "बेर्ना सारेको ६०–९० दिनमा पहिलो टिपाइ",
                harvestingPeriodNe = "जात अनुसार २–४ महिनासम्म नियमित टिपाइ",
                irrigationGuidanceNe = "फल लाग्ने र बढ्ने बेलामा एकनासको चिस्यान राख्ने (अचानक धेरै पानी वा सुक्खाले फल फुट्ने समस्या आउँछ)।",
                specialConsiderationsNe = "राति १५–२०°C तापक्रममा फल राम्रो लाग्छ। १०°C मुनि वा ३५°C माथि परागकण तथा रङ विकासमा असर पर्छ र भारी वर्षामा डढुवा रोक्न प्लाष्टिक टनेल लाभदायक हुन्छ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "माटो राम्रोसँग खनजोत गरी उँचो ड्याङ (Raised Bed) निर्माण गर्ने।",
            seedSelection = "रोगमुक्त नर्सरीमा बेर्ना उत्पादन गरी करिब २५-३० दिनमा मुख्य जमिनमा सार्ने।",
            spacing = "सामान्यतया लाइनदेखि लाइन ६०-७५ से.मी. र बोटदेखि बोट ४५-५० से.मी.।",
            fertilizer = "प्रशस्त पाकेको गोबरमल र सिफारिस अनुसार नाइट्रोजन, फस्फोरस, पोटास तथा क्याल्सियम पोषण व्यवस्थापन गर्ने।",
            cropCare = "अग्लो जातमा बाँस वा डोरीको थाँक्रो (Staking) दिने र अनावश्यक बगली मुना हटाउने।",
            pestDiseaseControl = "पछौटे डढुवा (Late Blight), ओइलाउने रोग र फल कुहाउने गवारोबाट जोगाउन नियमित अवलोकन र एकीकृत शत्रुजीव व्यवस्थापन (IPM) अपनाउने।",
            harvesting = "बजारको दूरी हेरी रंग परिवर्तन हुन थालेपछि (Breaker stage) वा पूरा पाकेपछि टिप्ने।",
            storage = "चोटपटक नलाग्ने गरी क्रेटमा मिलाएर शीतल ठाउँमा राख्ने।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) बागवानी महाशाखा / FAO EcoCrop",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/640px-Tomato_je.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-ginger",
            categoryId = "cat-cash",
            nameNe = "अदुवा",
            nameEn = "Ginger",
            scientificName = "Zingiber officinale Rosc.",
            descriptionNe = "अदुवा बकैयाका पहाडी पाखो तथा छहारीयुक्त बारीहरूमा किसानहरूले उत्पादन गर्ने प्रमुख मसला तथा नगदे बाली हो।",
            varieties = "कपुरकोट अदुवा-१, कपुरकोट अदुवा-२, स्थानीय बोसे र नसे",
            climateOverview = "न्यानो र ओसिलो हावापानीका साथै हल्का छहारी (जस्तै मकैसँग अन्तरबाली) अदुवा खेतीमा लाभदायक हुन्छ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 100,
                altitudeOptimalMinMeters = 600,
                altitudeOptimalMaxMeters = 1500,
                altitudeMaxMeters = 1600,
                altitudeSummaryNe = "१०० देखि १६०० मिटरसम्म (मध्य पहाड तथा भित्री मधेस)",
                altitudeGrowthNotesNe = "न्यानो तथा आर्द्र मध्य पहाडी पाखो बारी (६००–१५०० मिटर) र भित्री मधेसमा रेशारहित एवं गुणस्तरीय गानो विकास हुन्छ; १६०० मिटरभन्दा माथि चिसोले वृद्धि सुस्त हुन्छ।",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस र मध्य पहाडी पाखो क्षेत्र (मकवानपुर, सल्यान, इलाम, पाल्पा आदि)",
                soilTypesNe = "पानीको उत्तम निकास भएको, प्राङ्गारिक पदार्थयुक्त खुकुलो दोमट माटो",
                soilTextureNe = "कम्तीमा ३० से.मी. गहिरो खुकुलो बलौटे दोमट वा चिम्टाइलो दोमट",
                soilPhMin = 6.0,
                soilPhMax = 6.5,
                soilDrainageNe = "अत्यन्त राम्रो निकास (जलजमावमा गानो कुहिने)",
                soilFertilityNe = "पोटास र प्राङ्गारिक पोषणयुक्त उर्वर माटो",
                soilOrganicMatterNe = "उच्च ह्युमस तथा बाक्लो छापो (Mulching) आवश्यक",
                tempMin = 19.0,
                tempOptimalMin = 20.0,
                tempOptimalMax = 30.0,
                tempMax = 30.0,
                humidityMin = null,
                humidityMax = null,
                rainfallMinMm = 1500,
                rainfallMaxMm = 2500,
                rainfallPeriodLabelNe = "वार्षिक (मनसुनमा अच्छी वितरण)",
                waterRequirementNe = "मध्यम र सन्तुलित; ड्याङमा पानी जमेमा गानो कुहिन्छ",
                sunlightNe = "आंशिक छहारी वा मध्यम घाम अनुकूल",
                plantingSeasonNe = "चैत मध्यदेखि वैशाख अन्त्यसम्म",
                sowingPeriodNe = "चैत मध्यदेखि वैशाख (प्रि-मनसुनमा गानो रोप्ने)",
                transplantingPeriodNe = null,
                growingDurationNe = "८ देखि ९ महिना (२२५ देखि २६० दिन)",
                harvestingPeriodNe = "मंसिर–माघ (पात र बोट सुकेपछि)",
                irrigationGuidanceNe = "माटोको चिस्यान कायम राख्न पराल वा स्याउलाको बाक्लो छापो हाल्ने; वर्षामा पानी जम्न नदिने।",
                specialConsiderationsNe = "कम तापक्रममा गानो सुषुप्त (Dormant) हुन्छ र ड्याङमा पानी जम्दा गानो कुहिने (Rhizome Rot) रोग तीव्र रूपमा फैलिन्छ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "माटो गहिरो जोती २० से.मी. उँचो ड्याङ बनाई पानी तर्किने नाली बनाउने।",
            seedSelection = "२-३ वटा स्वस्थ आँखला भएको, रोगमुक्त बीउ गानो छान्ने र ट्राइकोडर्मा/सुरक्षित उपचार गरेर रोप्ने।",
            spacing = "लाइनदेखि लाइन ३० से.मी. र गानोदेखि गानो २५ से.मी.।",
            fertilizer = "प्रशस्त कुहिएको गोबर/कम्पोस्ट मल, छापो (Mulching) तथा सिफारिस अनुसार मलखाद प्रयोग गर्ने।",
            cropCare = "रोपेपछि बाक्लो छापो राख्ने र आवश्यकता अनुसार झार गोडी उकेरा लगाउने।",
            pestDiseaseControl = "गानो कुहिने रोग (Rhizome Rot) अदुवाको मुख्य समस्या भएकाले स्वस्थ बीउ, पानी निकास र जैविक ट्राइकोडर्मा व्यवस्थापनमा ध्यान दिने।",
            harvesting = "रोपेको ८-९ महिनामा पात र बोट सुक्न थालेपछि गानो नकाटिने गरी खन्ने (माउ अदुवा बिचमा निकाल्ने स्थानीय चलन समेत रहेको छ)।",
            storage = "सुक्खा र हावा खेल्ने छायादार ठाउँ वा सुरक्षित खाडल प्रविधि (Pit storage) मा बीउ अदुवा सुरक्षित राख्ने।",
            sourceReference = "राष्ट्रिय अदुवा बाली अनुसन्धान कार्यक्रम, कपुरकोट (NARC) / FAO",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/640px-Ginger_plant.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-mustard",
            categoryId = "cat-oilseed",
            nameNe = "तोरी",
            nameEn = "Mustard / Rapeseed",
            scientificName = "Brassica campestris var. toria",
            descriptionNe = "तोरी बकैयाका धान काटेपछिका खेतबारीमा हिउँद याममा लगाइने प्रमुख तेलहन बाली हो।",
            varieties = "विकास, उन्नति, प्रगति, प्रीति, मोरङ तोरी-२",
            climateOverview = "हिउँदको चिसो र घमाइलो मौसममा तोरी राम्रो फल्दछ। अत्यधिक कुहिरो र हुस्सुले लाही कीराको प्रकोप बढाउन सक्छ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeOptimalMinMeters = 100,
                altitudeOptimalMaxMeters = 1200,
                altitudeMaxMeters = 1500,
                altitudeSummaryNe = "तराईदेखि १५०० मिटर मध्य पहाडसम्म",
                altitudeGrowthNotesNe = "तराई, भित्री मधेस र मध्य पहाडी उपत्यकामा हिउँदमा छिटो तयार हुन्छ। मध्य पहाडमा विकास अवधि हल्का लामो हुन सक्छ।",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस तथा न्यानो मध्य पहाड",
                soilTypesNe = "हल्का दोमटदेखि चिम्टाइलो दोमट माटो",
                soilTextureNe = "बलौटे दोमट वा मध्यम दोमट",
                soilPhMin = 6.0,
                soilPhMax = 7.5,
                soilDrainageNe = "राम्रो निकास भएको",
                soilFertilityNe = "NPK सँगै सल्फर र बोरोन आवश्यक",
                soilOrganicMatterNe = "सामान्य गोबर/कम्पोस्ट मल",
                tempMin = 10.0,
                tempOptimalMin = 12.0,
                tempOptimalMax = 25.0,
                tempMax = 25.0,
                humidityMin = null,
                humidityMax = null,
                rainfallMinMm = 300,
                rainfallMaxMm = 400,
                rainfallPeriodLabelNe = "हिउँद बाली अवधिमा",
                waterRequirementNe = "न्यून देखि मध्यम",
                sunlightNe = "पूर्ण घाम आवश्यक",
                plantingSeasonNe = "असोज १५ देखि कात्तिक १५ सम्म",
                sowingPeriodNe = "असोज–कात्तिक",
                transplantingPeriodNe = null,
                growingDurationNe = "८५ देखि १२० दिन (जात अनुसार)",
                harvestingPeriodNe = "पुस–फागुन (७५–८०% कोसा पहेँलो हुँदा)",
                irrigationGuidanceNe = "फूल फुल्नुअघि र कोसा लाग्ने अवस्था (रोपेको २५-३० दिन र ५० दिन आसपास) मा हल्का सिँचाइ गर्दा उत्पादनमा उल्लेखनीय वृद्धि हुन्छ।",
                specialConsiderationsNe = "पाक्ने बेलामा सुक्खा र घमाइलो मौसम आवश्यक हुन्छ; लगातार कुहिरो र तुषारोमा अल्टरनेरिया डढुवा र लाही कीराको जोखिम बढ्छ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "धान काटेपछि खेतमा चिस्यान छँदै २ पटक जोतेर माटो मसिनो बनाई छर्ने।",
            seedSelection = "रोगमुक्त, सफा र उच्च तेल प्रतिशत भएका उन्नत जातको बीउ प्रयोग गर्ने।",
            spacing = "लाइनदेखि लाइन ३० से.मी. र बोटदेखि बोट १० से.मी. (छरुवा वा लाइन दुवै प्रविधि)।",
            fertilizer = "कम्पोस्ट मल र नाइट्रोजन, फस्फोरस, पोटासका साथै तेलहन बालीमा सल्फर र बोरोनको सन्तुलित प्रयोग लाभदायक हुन्छ।",
            cropCare = "उम्रेको १५-२० दिनमा बाक्लो बिरुवा छाँटेर (Thinning) झार गोड्ने।",
            pestDiseaseControl = "लाही कीरा (Aphids) र डढुवा रोगको समयमै निगरानी गरी सुरक्षित जैविक/सिफारिसकृत उपाय अपनाउने।",
            harvesting = "७५-८० प्रतिशत कोसा पहेँलो भएपछि बिहानीपख शीत छँदै कटानी गर्ने ताकि दाना खेतमा नझरोस्।",
            storage = "दानालाई घाममा राम्ररी सुकाई चिस्यान ८ प्रतिशतभन्दा कम पारेर सुक्खा भाँडा वा बोरामा भण्डारण गर्ने।",
            sourceReference = "तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC) / कृषि डायरी",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/640px-Among_Mustard_Dreams.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-lentil",
            categoryId = "cat-pulse",
            nameNe = "मसुरो",
            nameEn = "Lentil",
            scientificName = "Lens culinaris",
            descriptionNe = "मसुरो हिउँदमा धान खेतको चिस्यानमा वा बारीमा लगाइने प्रमुख दलहन बाली हो। यसले माटोको उर्वराशक्ति बढाउन र पौष्टिक दाल आपूर्ति गर्न मद्दत गर्दछ।",
            varieties = "शिमल, शिखर, सगुन, सिन्दुर, शिशिर, खजुरा मसुरो-१, २, ३",
            climateOverview = "हिउँदको शीतल मौसम वृद्धि चरणका लागि र न्यानो सुक्खा मौसम पाक्ने चरणका लागि उपयुक्त हुन्छ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeOptimalMinMeters = 60,
                altitudeOptimalMaxMeters = 1000,
                altitudeMaxMeters = 1500,
                altitudeSummaryNe = "तराई, भित्री मधेस तथा तल्ला मध्य पहाडी बेँसी (६० - १५०० मिटर)",
                altitudeGrowthNotesNe = "मुख्यतः तराई र भित्री मधेसमा धानपछिको अवशिष्ट चिस्यानमा लगाइन्छ; मध्य पहाडी नदी उपत्यका र बेँसीमा समेत सफल हुन्छ।",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस तथा मध्य पहाडका नदी उपत्यका (मध्य/सुदूरपश्चिम र मधेस/बागमती बेँसी क्षेत्र)",
                soilTypesNe = "पानी नजम्ने दोमट र हल्का चिम्टाइलो माटो",
                soilTextureNe = "दोमट वा बलौटे दोमट",
                soilPhMin = 5.5,
                soilPhMax = 7.0,
                soilDrainageNe = "पानी नजम्ने राम्रो निकास (चिम्टाइलो जलजमाव अनुपयुक्त)",
                soilFertilityNe = "फस्फोरस र राइजोबियम (जराबाट नाइट्रोजन स्थिरीकरण)",
                soilOrganicMatterNe = "सामान्य गोबर/कम्पोस्ट तथा धानपछिको चिस्यान",
                tempMin = 5.0,
                tempOptimalMin = 18.0,
                tempOptimalMax = 29.0,
                tempMax = 32.0,
                humidityMin = null,
                humidityMax = null,
                rainfallMinMm = 300,
                rainfallMaxMm = 500,
                rainfallPeriodLabelNe = "हिउँद बाली अवधिमा (अवशिष्ट चिस्यानसहित)",
                waterRequirementNe = "न्यून — प्रायः हिउँदे चिस्यानमा नै फल्ने, पानी जम्न नहुने",
                sunlightNe = "पूर्ण घाम",
                plantingSeasonNe = "कात्तिक (धान काटिसकेपछि वा धान काट्नु १५ दिनअघि रिले खेती)",
                sowingPeriodNe = "कात्तिक मध्यदेखि मंसिर (असोज–मंसिर)",
                transplantingPeriodNe = null,
                growingDurationNe = "१२० देखि १४५ दिन (जात अनुसार)",
                harvestingPeriodNe = "चैत–वैशाख (प्रि-मनसुन वर्षाअघि)",
                irrigationGuidanceNe = "पानी जम्न नदिने; अत्यन्त सुक्खा भएमा कोसा लाग्ने बेलामा हल्का सिँचाइ गर्ने।",
                specialConsiderationsNe = "फूल फुल्ने बेलामा एक्कासी तापक्रम बढे फूल झर्ने समस्या आउन सक्छ, र पाक्ने बेलामा सुक्खा मौसम आवश्यक हुन्छ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "राम्ररी जोतेर सम्याएको वा धान खेतको रिले बालीका रूपमा छर्न सकिने।",
            seedSelection = "स्वस्थ बीउलाई राइजोबियम (Rhizobium) र ढुसीनाशक बाट उपचार गरी प्रयोग गर्ने।",
            spacing = "लाइन रोपाइँमा लाइनदेखि लाइन २०-२५ से.मी.।",
            fertilizer = "कोसेबाली भएकाले जराको गिर्खाबाट नाइट्रोजन स्थिरीकरण गर्दछ; सिफारिस अनुसार आधारभूत फस्फोरस र कम्पोस्ट मल प्रयोग गर्ने।",
            cropCare = "सुरुवाती ३०-३५ दिनसम्म खेतलाई झारपातमुक्त राख्ने।",
            pestDiseaseControl = "जरा कुहिने/ओइलाउने रोग (Wilt), स्टेमफाइलियम डढुवा र कोसा खाने कीराबाट जोगाउन प्राविधिक सल्लाह अनुसार व्यवस्थापन गर्ने।",
            harvesting = "बोट र कोसा सुकेर खैरो भएपछि बिहानीपख कटानी गरी सुकाएर दाउनी गर्ने।",
            storage = "दानालाई सुकाएर चिस्यान १० प्रतिशत मुनि झारी सुरक्षित भाँडा वा सुपरग्रेन ब्यागमा घुन नलाग्ने गरी राख्ने।",
            sourceReference = "कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC) / FAO EcoCrop",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/640px-Lens_culinaris_sl13.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-banana",
            categoryId = "cat-fruit",
            nameNe = "केरा",
            nameEn = "Banana",
            scientificName = "Musa spp.",
            descriptionNe = "केरा बकैयाका न्यानो तल्लो उपत्यका र खोला किनारका बस्तीहरूमा वर्षैभरि उत्पादन र आयआर्जन दिने प्रमुख फलफूल बाली हो।",
            varieties = "मालभोग, विलियम हाइब्रिड, जी-९ (G-9), झापाली मालभोग, चिनियाँ चम्पा",
            climateOverview = "न्यानो, आर्द्र र तुषारो नपर्ने हावापानी केराको व्यावसायिक उत्पादनका लागि उपयुक्त हुन्छ।",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeOptimalMinMeters = 60,
                altitudeOptimalMaxMeters = 600,
                altitudeMaxMeters = 1000,
                altitudeSummaryNe = "तराईदेखि करिब १००० मिटरसम्मका न्यानो उपत्यका",
                altitudeGrowthNotesNe = "तुषारो नपर्ने ६०–९०० मिटरको तराई, भित्री मधेस र न्यानो नदी उपत्यका उत्तम हुन्छ; १००० मिटरमाथिका न्यानो भूभागमा फल पाक्ने समय लामो हुन्छ र तुषारोमा बोट मर्न सक्छ।",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई, भित्री मधेस तथा तुषारोरहित तल्ला उपत्यका",
                soilTypesNe = "गहिरो, मलिलो, पानी नजम्ने दोमट माटो",
                soilTextureNe = "गहिरो पाँगो वा बलौटे दोमट",
                soilPhMin = 6.0,
                soilPhMax = 7.5,
                soilDrainageNe = "अत्यन्त राम्रो निकास तथा हावा खेल्ने (जलजमाव नसहने)",
                soilFertilityNe = "उच्च पोटास, नाइट्रोजन तथा पोषक तत्व आवश्यक",
                soilOrganicMatterNe = "खाडलमा प्रशस्त पाकेको गोबर/कम्पोस्ट मल आवश्यक",
                tempMin = 10.0,
                tempOptimalMin = 20.0,
                tempOptimalMax = 35.0,
                tempMax = 38.0,
                humidityMin = 60.0,
                humidityMax = 85.0,
                rainfallMinMm = 1200,
                rainfallMaxMm = 2500,
                rainfallPeriodLabelNe = "वार्षिक (वा नियमित सिँचाइ)",
                waterRequirementNe = "उच्च — तर पानी जम्न नहुने, नियमित सिँचाइ आवश्यक",
                sunlightNe = "पूर्ण घाम आवश्यक",
                plantingSeasonNe = "फागुन-चैत र असार-साउन (वर्षाको सुरुवात)",
                sowingPeriodNe = "असार–साउन (वर्षे) र फागुन–चैत (वसन्त) मा बिरुवा रोप्ने",
                transplantingPeriodNe = "६०x६०x६० से.मी. खाडलमा स्वस्थ पुतली वा टिस्युकल्चर बिरुवा सार्ने",
                growingDurationNe = "११ देखि १५ महिना (पहिलो घरी तयार हुन)",
                harvestingPeriodNe = "रोपेको १२–१५ महिनापछि (बजार अनुसार वर्षैभरि उपलब्ध)",
                irrigationGuidanceNe = "सुक्खा याममा साप्ताहिक वा माटो हेरी सिँचाइ दिने र वर्षायाममा निकास खोल्ने।",
                specialConsiderationsNe = "१०°C भन्दा तल र ३८°C भन्दा माथि केराको वृद्धि रोकिन्छ; उच्च आर्द्रता (६०–८५%) पात र कोसा विकासका लागि उपयुक्त हुन्छ र हुरीबतासबाट बचाउन टेको आवश्यक पर्दछ।",
                lastUpdatedDate = "2026-11-25"
            ),
            landPreparation = "६०x६०x६० से.मी. आकारका खाडल खनेर पाकेको गोबरमल र माटो मिसाई पुर्ने।",
            seedSelection = "स्वस्थ तरबार आकारको पुतली (Sword sucker) वा टिस्युकल्चर बिरुवा छनोट गर्ने।",
            spacing = "जात अनुसार बोटदेखि बोट र लाइनदेखि लाइन २ देखि २.५ मिटर।",
            fertilizer = "खाडल तयार गर्दा प्रशस्त कम्पोस्ट मल हाल्ने र वृद्धि अवस्था अनुसार कृषि प्राविधिकको सिफारिस बमोजिम पोषण व्यवस्थापन गर्ने।",
            cropCare = "अनावश्यक धेरै पुतली (Suckers) हटाई मुख्य घरीको वृद्धि सुनिश्चित गर्ने र बुङ्गो आएपछि घरीलाई टेको दिने।",
            pestDiseaseControl = "पानामा ओइलाउने रोग (Panama Wilt), गवारो कीरा (Rhizome Weevil) र पात थोप्ले रोगको नियमित निरीक्षण गर्ने।",
            harvesting = "कोसा भरिएर कुना हराएपछि र हल्का पहेंलोपन देखिन थालेपछि घरी काट्ने।",
            storage = "सावधानीपूर्वक छायाँमा सुरक्षित राखी स्वाभाविक रूपमा पकाएर बजार व्यवस्थापन गर्ने।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय / FAO",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/640px-Banana_tree_%28musa%29.jpg"
        )
    )

    val varietyRequirements = listOf(
        // Paddy varieties (NARC)
        CropVarietyRequirementEntity(
            id = "var-paddy-savitri",
            cropId = "crop-paddy",
            cropNameNe = "धान",
            cropNameEn = "Paddy / Rice",
            varietyNameNe = "सावित्री",
            varietyNameEn = "Savitri",
            notesNe = "दीर्घकालीन लोकप्रिय जात; मरुवा रोगप्रति मध्यम सहनशील।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeMaxMeters = 700,
                altitudeSummaryNe = "तराई, भित्री मधेस तथा तल्ला नदी फाँट",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई, भित्री मधेस तथा तल्ला बेँसी",
                plantingSeasonNe = "असार–साउन (बर्खे धान)",
                growingDurationNe = "१४० देखि १६० दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-paddy-radha4",
            cropId = "crop-paddy",
            cropNameNe = "धान",
            cropNameEn = "Paddy / Rice",
            varietyNameNe = "राधा-४",
            varietyNameEn = "Radha-4",
            notesNe = "सुख्खा सहनशील जात; वर्षामा आधारित कम सिँचाइ हुने क्षेत्रमा समेत उपयुक्त।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeMaxMeters = 700,
                altitudeSummaryNe = "तराई तथा तल्ला सुख्खा प्रभावित फाँट",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई तथा भित्री मधेस",
                plantingSeasonNe = "असार–साउन (बर्खे धान)",
                growingDurationNe = "१३५ देखि १५५ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-paddy-ramdhan",
            cropId = "crop-paddy",
            cropNameNe = "धान",
            cropNameEn = "Paddy / Rice",
            varietyNameNe = "रामधान",
            varietyNameEn = "Ramdhan",
            notesNe = "उच्च उत्पादकत्व दिने लोकप्रिय व्यावसायिक धान जात।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeMaxMeters = 900,
                altitudeSummaryNe = "तराई, भित्री मधेस र तल्ला उपत्यका",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई, भित्री मधेस तथा तल्ला नदी उपत्यका",
                plantingSeasonNe = "असार–साउन (बर्खे धान)",
                growingDurationNe = "१४० देखि १६० दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-paddy-hardinath3",
            cropId = "crop-paddy",
            cropNameNe = "धान",
            cropNameEn = "Paddy / Rice",
            varietyNameNe = "हर्दिनाथ-३",
            varietyNameEn = "Hardinath-3",
            notesNe = "छिटो पाक्ने (अगौटे) जात; चैते तथा सघन बाली चक्रका लागि उपयुक्त।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 60,
                altitudeMaxMeters = 700,
                altitudeSummaryNe = "तराई तथा भित्री मधेस",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई र भित्री मधेस",
                plantingSeasonNe = "फागुन–चैत (चैते) तथा असार (बर्खे)",
                growingDurationNe = "११० देखि ११५ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),

        // Maize varieties (NARC)
        CropVarietyRequirementEntity(
            id = "var-maize-deuti",
            cropId = "crop-maize",
            cropNameNe = "मकै",
            cropNameEn = "Maize / Corn",
            varietyNameNe = "देउती",
            varietyNameEn = "Deuti",
            notesNe = "सेतो दाना हुने र पातका रोगप्रति राम्रो सहनशीलता भएको मध्य पहाडी जात।",
            sourceReference = "राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "मध्य पहाड (उच्च पहाडी पाखोसहित)",
                suitableZonesCsv = "mid_hill",
                suitableRegionsNe = "मध्य पहाडी क्षेत्र",
                plantingSeasonNe = "फागुन–वैशाख (वसन्त/वर्षे)",
                growingDurationNe = "१३० देखि १३५ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-maize-shitala",
            cropId = "crop-maize",
            cropNameNe = "मकै",
            cropNameEn = "Maize / Corn",
            varietyNameNe = "शितला",
            varietyNameEn = "Shitala",
            notesNe = "सेतो दाना हुने पहाडी तथा मध्य पहाडी क्षेत्रका लागि पूर्ण अवधिको जात।",
            sourceReference = "राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "मध्य पहाड र पहाडी क्षेत्र",
                suitableZonesCsv = "mid_hill,high_hill",
                suitableRegionsNe = "पहाडी तथा मध्य पहाडी बारी",
                plantingSeasonNe = "वैशाख–जेठ (वर्षे पहाड)",
                growingDurationNe = "१३५ देखि १४० दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-maize-manakamana3",
            cropId = "crop-maize",
            cropNameNe = "मकै",
            cropNameEn = "Maize / Corn",
            varietyNameNe = "मनकामना-३",
            varietyNameEn = "Manakamana-3",
            notesNe = "बोट बलियो भई नढल्ने (Lodging resistant) मध्य पहाडमा अत्यन्त लोकप्रिय जात।",
            sourceReference = "राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 1000,
                altitudeMaxMeters = 1700,
                altitudeSummaryNe = "१००० देखि १७०० मिटर (मध्य पहाड)",
                suitableZonesCsv = "mid_hill",
                suitableRegionsNe = "मध्य पहाड (१०००–१७०० मिटर)",
                plantingSeasonNe = "फागुन–जेठ",
                growingDurationNe = "१३५ देखि १४५ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),

        // Cauliflower varieties
        CropVarietyRequirementEntity(
            id = "var-cauli-kathmandu-local",
            cropId = "crop-cauliflower",
            cropNameNe = "काउली",
            cropNameEn = "Cauliflower",
            varietyNameNe = "काठमाडौँ स्थानीय",
            varietyNameEn = "Kathmandu Local",
            notesNe = "खुला परागसेचित (OP) मध्यम–पछौटे यामको भरपर्दो जात।",
            sourceReference = "राष्ट्रिय तरकारी बाली विकास केन्द्र / NARC",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 1000,
                altitudeMaxMeters = 2000,
                altitudeSummaryNe = "१००० देखि २००० मिटर (मध्य पहाड)",
                suitableZonesCsv = "mid_hill",
                suitableRegionsNe = "मध्य पहाडी उपत्यका तथा पाखो",
                plantingSeasonNe = "भदौ–कात्तिक (मध्यम–पछौटे)",
                growingDurationNe = "११० देखि १३०+ दिन (रोपेदेखि कटानी)",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-cauli-snow-mystic",
            cropId = "crop-cauliflower",
            cropNameNe = "काउली",
            cropNameEn = "Cauliflower",
            varietyNameNe = "स्नो मिस्टिक",
            varietyNameEn = "Snow Mystic",
            notesNe = "कसिलो सेतो गुँद लाग्ने व्यावसायिक पछौटे हाइब्रिड जात।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / कृषि प्रविधि",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 800,
                altitudeMaxMeters = 1800,
                altitudeSummaryNe = "८०० देखि १८०० मिटर (तल्लो र मध्य पहाड)",
                suitableZonesCsv = "mid_hill",
                suitableRegionsNe = "तल्लो र मध्य पहाडी क्षेत्र",
                transplantingPeriodNe = "असोज–मंसिर",
                growingDurationNe = "बेर्ना सारेपछि ८० देखि १०० दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),

        // Tomato variety
        CropVarietyRequirementEntity(
            id = "var-tomato-srijana",
            cropId = "crop-tomato",
            cropNameNe = "गोलभेंडा",
            cropNameEn = "Tomato",
            varietyNameNe = "सिर्जना (NARC हाइब्रिड)",
            varietyNameEn = "Srijana",
            notesNe = "NARC द्वारा विकास गरिएको नेपालको पहिलो हाइब्रिड; ओइलाउने रोग (Bacterial Wilt) प्रति सहनशील र टनेल खेतीका लागि उत्तम।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) बागवानी महाशाखा",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMinMeters = 800,
                altitudeMaxMeters = 1500,
                altitudeSummaryNe = "८०० देखि १५००+ मिटर (मध्य पहाड तथा टनेल)",
                suitableZonesCsv = "inner_terai,mid_hill",
                suitableRegionsNe = "मध्य पहाड र पहाडी उपत्यका (खुला तथा प्लाष्टिक टनेल)",
                plantingSeasonNe = "भदौ–असोज र माघ–फागुन (टनेलमा वर्षैभरि)",
                growingDurationNe = "बेर्ना सारेको ६५ देखि ८० दिनमा पहिलो टिपाइ",
                lastUpdatedDate = "2026-11-25"
            )
        ),

        // Ginger varieties
        CropVarietyRequirementEntity(
            id = "var-ginger-kapurkot1",
            cropId = "crop-ginger",
            cropNameNe = "अदुवा",
            cropNameEn = "Ginger",
            varietyNameNe = "कपुरकोट अदुवा-१",
            varietyNameEn = "Kapurkot Aduwa-1",
            notesNe = "रेशा कम भएको तथा सुठो बनाउँदा उच्च उत्पादन दिने NARC सिफारिस जात।",
            sourceReference = "राष्ट्रिय अदुवा बाली अनुसन्धान कार्यक्रम, कपुरकोट (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMaxMeters = 1600,
                altitudeSummaryNe = "१६०० मिटरसम्म (भित्री मधेस, बेँसी र मध्य पहाड)",
                suitableZonesCsv = "inner_terai,mid_hill",
                suitableRegionsNe = "भित्री मधेस, तल्ला पहाड र मध्य पहाड",
                plantingSeasonNe = "चैत–वैशाख",
                growingDurationNe = "२२५ देखि २४० दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-ginger-kapurkot2",
            cropId = "crop-ginger",
            cropNameNe = "अदुवा",
            cropNameEn = "Ginger",
            varietyNameNe = "कपुरकोट अदुवा-२",
            varietyNameEn = "Kapurkot Aduwa-2",
            notesNe = "भण्डारण क्षमता राम्रो भएको र मध्य पहाडी बारीका लागि उपयुक्त जात।",
            sourceReference = "राष्ट्रिय अदुवा बाली अनुसन्धान कार्यक्रम, कपुरकोट (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMaxMeters = 1600,
                altitudeSummaryNe = "१६०० मिटरसम्म (भित्री मधेस, बेँसी र मध्य पहाड)",
                suitableZonesCsv = "inner_terai,mid_hill",
                suitableRegionsNe = "भित्री मधेस, तल्ला पहाड र मध्य पहाड",
                plantingSeasonNe = "चैत–वैशाख",
                growingDurationNe = "२४० देखि २६० दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),

        // Mustard varieties
        CropVarietyRequirementEntity(
            id = "var-mustard-bikas",
            cropId = "crop-mustard",
            cropNameNe = "तोरी",
            cropNameEn = "Mustard / Toria",
            varietyNameNe = "विकास",
            varietyNameEn = "Bikas",
            notesNe = "छिटो पाक्ने भएकाले सघन बाली प्रणालीमा उपयुक्त।",
            sourceReference = "तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "तराई तथा भित्री मधेस",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई र भित्री मधेस",
                plantingSeasonNe = "असोज–कात्तिक",
                growingDurationNe = "८५ देखि ९० दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-mustard-unnati",
            cropId = "crop-mustard",
            cropNameNe = "तोरी",
            cropNameEn = "Mustard / Toria",
            varietyNameNe = "उन्नति",
            varietyNameEn = "Unnati",
            notesNe = "सिञ्चित तराई तथा भित्री मधेस क्षेत्रमा उच्च उत्पादन क्षमता।",
            sourceReference = "तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "तराई, भित्री मधेस तथा सिञ्चित बेँसी",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई, भित्री मधेस र उपत्यका",
                plantingSeasonNe = "असोज–कात्तिक",
                growingDurationNe = "करिब ८६ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-mustard-pragati",
            cropId = "crop-mustard",
            cropNameNe = "तोरी",
            cropNameEn = "Mustard / Toria",
            varietyNameNe = "प्रगति",
            varietyNameEn = "Pragati",
            notesNe = "न्यानो मध्य पहाडसम्म अनुकूल हुने मध्यम अवधिको तोरी जात।",
            sourceReference = "तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "तराई, भित्री मधेस र न्यानो मध्य पहाड",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस र पूर्वी/मध्य पहाड",
                plantingSeasonNe = "असोज–कात्तिक",
                growingDurationNe = "करिब ९९ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),

        // Lentil varieties
        CropVarietyRequirementEntity(
            id = "var-lentil-khajura2",
            cropId = "crop-lentil",
            cropNameNe = "मसुरो",
            cropNameEn = "Lentil",
            varietyNameNe = "खजुरा मसुरो-२",
            varietyNameEn = "Khajura Masuro-2",
            notesNe = "जिङ्क र फलाम तत्व राम्रो हुने तथा विविध क्षेत्रमा अनुकूल।",
            sourceReference = "कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "तराई, भित्री मधेस र मध्य–उच्च पहाडी बेँसी",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस तथा मध्य पहाडी बेँसी",
                plantingSeasonNe = "कात्तिक–मंसिर",
                growingDurationNe = "१३० देखि १३४ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-lentil-shimal",
            cropId = "crop-lentil",
            cropNameNe = "मसुरो",
            cropNameEn = "Lentil",
            varietyNameNe = "शिमल",
            varietyNameEn = "Shimal",
            notesNe = "रोग सहनशील तथा मध्य पहाडसम्म सफल लोकप्रिय जात।",
            sourceReference = "कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "तराई, भित्री मधेस र मध्य पहाड",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस र मध्य पहाड",
                plantingSeasonNe = "कात्तिक–मंसिर",
                growingDurationNe = "करिब १४३ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-lentil-shikhar",
            cropId = "crop-lentil",
            cropNameNe = "मसुरो",
            cropNameEn = "Lentil",
            varietyNameNe = "शिखर",
            varietyNameEn = "Shikhar",
            notesNe = "व्यापक भौगोलिक अनुकूलता भएको उच्च उत्पादनशील मसुरो।",
            sourceReference = "कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC)",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "तराई, भित्री मधेस र मध्य पहाड",
                suitableZonesCsv = "terai,inner_terai,mid_hill",
                suitableRegionsNe = "तराई, भित्री मधेस र मध्य पहाड",
                plantingSeasonNe = "कात्तिक–मंसिर",
                growingDurationNe = "करिब १४३ दिन",
                lastUpdatedDate = "2026-11-25"
            )
        ),

        // Banana varieties
        CropVarietyRequirementEntity(
            id = "var-banana-g9",
            cropId = "crop-banana",
            cropNameNe = "केरा",
            cropNameEn = "Banana",
            varietyNameNe = "जी-९ (Giant Naine)",
            varietyNameEn = "G-9",
            notesNe = "पुड्को बोट, पानामा ओइलाउने रोगप्रति सहनशील र उच्च व्यावसायिक उत्पादन।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय",
            growingConditions = GrowingConditionsEmbedded(
                altitudeMaxMeters = 1000,
                altitudeSummaryNe = "तराईदेखि न्यानो मध्य पहाडी उपत्यका (१००० मिटर आसपाससम्म)",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई, भित्री मधेस तथा न्यानो उपत्यका",
                plantingSeasonNe = "असार–साउन र फागुन–चैत",
                growingDurationNe = "१२ देखि १४ महिना",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-banana-william-hybrid",
            cropId = "crop-banana",
            cropNameNe = "केरा",
            cropNameEn = "Banana",
            varietyNameNe = "विलियम हाइब्रिड",
            varietyNameEn = "William Hybrid",
            notesNe = "ठूलो घरी र पानामा ओइलाउने रोग सहनशीलता भएको व्यावसायिक जात।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "उष्ण तथा उपोष्ण तराई र भित्री उपत्यका",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई तथा भित्री मधेस",
                plantingSeasonNe = "असार–साउन र फागुन–चैत",
                growingDurationNe = "१२ देखि १४ महिना",
                lastUpdatedDate = "2026-11-25"
            )
        ),
        CropVarietyRequirementEntity(
            id = "var-banana-malbhog",
            cropId = "crop-banana",
            cropNameNe = "केरा",
            cropNameEn = "Banana",
            varietyNameNe = "मालभोग",
            varietyNameEn = "Malbhog",
            notesNe = "उत्कृष्ट स्वाद, स्थानीय बजार माग र बञ्ची टप रोगप्रति राम्रो सहनशीलता।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय",
            growingConditions = GrowingConditionsEmbedded(
                altitudeSummaryNe = "तराई, भित्री मधेस तथा तल्ला उपत्यका",
                suitableZonesCsv = "terai,inner_terai",
                suitableRegionsNe = "तराई र भित्री मधेस",
                plantingSeasonNe = "असार–साउन र फागुन–चैत",
                growingDurationNe = "१२ देखि १४+ महिना",
                lastUpdatedDate = "2026-11-25"
            )
        )
    )

    val cropImages = listOf(
        CropImageEntity(
            id = "img-paddy-1",
            cropId = "crop-paddy",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/6/6e/Rice_Fields_in_Jamuna%2C_Nepal.jpg",
            captionNe = "नेपालका खेतहरूमा हुर्कँदै गरेको धान बाली (स्रोत: Wikimedia Commons)",
            stageCategory = "पहिचान तथा वृद्धि",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-maize-1",
            cropId = "crop-maize",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg/640px-Starr-100621-7681-Zea_mays-crop_view_abandoned_pineapple_fields_above-Honokowai-Maui_%2824415271633%29.jpg",
            captionNe = "मकै बाली हुर्कँदै गरेको खेत दृश्य (स्रोत: Wikimedia Commons)",
            stageCategory = "पहिचान तथा वृद्धि",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-cauliflower-1",
            cropId = "crop-cauliflower",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Chou-fleur_02.jpg/640px-Chou-fleur_02.jpg",
            captionNe = "काउली बालीको पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "उत्पादन चरण",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-tomato-1",
            cropId = "crop-tomato",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/640px-Tomato_je.jpg",
            captionNe = "पाकेको गोलभेंडाको सामान्य पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "उत्पादन चरण",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-ginger-1",
            cropId = "crop-ginger",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Ginger_plant.jpg/640px-Ginger_plant.jpg",
            captionNe = "अदुवाको बोट पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "पहिचान तथा वृद्धि",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-mustard-1",
            cropId = "crop-mustard",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Among_Mustard_Dreams.jpg/640px-Among_Mustard_Dreams.jpg",
            captionNe = "नेपालको खेतमा फुलेको तोरी बाली (स्रोत: Wikimedia Commons)",
            stageCategory = "फूल फुल्ने चरण",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-lentil-1",
            cropId = "crop-lentil",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Lens_culinaris_sl13.jpg/640px-Lens_culinaris_sl13.jpg",
            captionNe = "मसुरो बालीको बोट र कोसा (स्रोत: Wikimedia Commons)",
            stageCategory = "फल तथा कोसा चरण",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-banana-1",
            cropId = "crop-banana",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Banana_tree_%28musa%29.jpg/640px-Banana_tree_%28musa%29.jpg",
            captionNe = "स्वस्थ केराको बोट र घरी (स्रोत: Wikimedia Commons)",
            stageCategory = "फल तथा उत्पादन चरण",
            isSampleImage = false
        )
    )

    /**
     * Verified YouTube videos from Nepali agricultural channels + transparent sample downloadable test videos
     * so offline download and Media3 ExoPlayer playback can be tested legitimately without unauthorized YouTube downloading.
     */
    val educationalVideos = listOf(
        // Paddy - Verified YouTube Videos
        EducationalVideoEntity(
            id = "vid-paddy-seed-aitc",
            cropId = "crop-paddy",
            topicId = 1, // बीउ छनोट तथा तयारी
            titleNe = "धानको उत्पादन बढाउन सिकौं विज्ञबाट धानको ब्याड राख्न",
            descriptionNe = "धानको स्वस्थ ब्याड तयार पार्ने र बीउ छनोट तथा व्यवस्थापन सम्बन्धी जानकारीमूलक भिडियो।",
            channelName = "Krishi Patrika",
            thumbnailUrl = "https://i.ytimg.com/vi/OflsI4icQGY/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=OflsI4icQGY",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),
        EducationalVideoEntity(
            id = "vid-paddy-cultivation-loksewa",
            cropId = "crop-paddy",
            topicId = 2, // जग्गा तयारी तथा रोपाइँ
            titleNe = "धान खेती प्रविधि (Dhan Kheti Garne Tarika)",
            descriptionNe = "धान खेती गर्दा जग्गाको तयारी, रोपाइँ विधि र समग्र बाली व्यवस्थापन सम्बन्धी शैक्षिक सामग्री।",
            channelName = "Krishi Loksewa",
            thumbnailUrl = "https://i.ytimg.com/vi/zIczJuB2N2M/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=zIczJuB2N2M",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),
        EducationalVideoEntity(
            id = "vid-paddy-weed-narc",
            cropId = "crop-paddy",
            topicId = 6, // बालीको हेरचाह
            titleNe = "धान खेती र झारपात व्यवस्थापन (NARC)",
            descriptionNe = "नेपाल कृषि अनुसन्धान परिषद् (NARC) का विज्ञहरूद्वारा धान खेतमा झारपात व्यवस्थापन सम्बन्धी सल्लाह।",
            channelName = "NTV NEWS NEPAL",
            thumbnailUrl = "https://i.ytimg.com/vi/ttjs2dSpI3A/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=ttjs2dSpI3A",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),
        EducationalVideoEntity(
            id = "vid-paddy-sample-offline",
            cropId = "crop-paddy",
            topicId = 5, // रोग तथा कीरा नियन्त्रण
            titleNe = "अफलाइन भिडियो प्लेयर परीक्षण नमुना (धान)",
            descriptionNe = "विकास परीक्षण (Sample Test Media) का लागि अनुमतिप्राप्त डाउनलोड योग्य MP4 फाइल। युट्युब भिडियो डाउनलोड प्रतिबन्धित भएकाले यो नमुना फाइलले अफलाइन डाउनलोडर र Media3 प्लेयर प्रमाणित गर्दछ।",
            channelName = "बकैया विकास नमुना (Sample Test)",
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Rice_plants_%28IRRI%29.jpg/320px-Rice_plants_%28IRRI%29.jpg",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            duration = "00:15",
            provider = "authorized_local",
            downloadPermission = true,
            downloadableFileUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            isSampleMedia = true
        ),

        // Maize - Verified YouTube Videos + Sample Offline
        EducationalVideoEntity(
            id = "vid-maize-care-labtv",
            cropId = "crop-maize",
            topicId = 6, // बालीको हेरचाह
            titleNe = "मकै गोड्दै - मकै खेती तथा गोडमेल अभ्यास",
            descriptionNe = "नेपाली ग्रामीण परिवेशमा मकै गोडमेल र हेरचाह गर्ने व्यावहारिक अभ्यास।",
            channelName = "LAB TV NEPAL",
            thumbnailUrl = "https://i.ytimg.com/vi/AMoKn2J8fFA/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=AMoKn2J8fFA",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),
        EducationalVideoEntity(
            id = "vid-maize-weed-chiran",
            cropId = "crop-maize",
            topicId = 6, // बालीको हेरचाह
            titleNe = "मकैमा झारपात नियन्त्रण प्रविधि",
            descriptionNe = "मकै खेतीमा झारपात व्यवस्थापन सम्बन्धी कृषि जानकारी।",
            channelName = "Agro Chiran",
            thumbnailUrl = "https://i.ytimg.com/vi/Fl8IKosrGqM/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=Fl8IKosrGqM",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),
        EducationalVideoEntity(
            id = "vid-maize-sample-offline",
            cropId = "crop-maize",
            topicId = 3, // मलखाद व्यवस्थापन
            titleNe = "अफलाइन भिडियो प्लेयर परीक्षण नमुना (मकै)",
            descriptionNe = "विकास परीक्षण (Sample Test Media) का लागि अनुमतिप्राप्त डाउनलोड योग्य MP4 फाइल।",
            channelName = "बकैया विकास नमुना (Sample Test)",
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Zea_mays_crop.jpg/320px-Zea_mays_crop.jpg",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            duration = "00:15",
            provider = "authorized_local",
            downloadPermission = true,
            downloadableFileUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            isSampleMedia = true
        ),

        // Cauliflower - Verified AITC Nepal + Krishi Television Videos
        EducationalVideoEntity(
            id = "vid-cauli-aitc",
            cropId = "crop-cauliflower",
            topicId = 2, // जग्गा तयारी तथा रोपाइँ
            titleNe = "काउली खेती प्रविधि",
            descriptionNe = "कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal) द्वारा प्रकाशित काउली खेती सम्बन्धी आधिकारिक शैक्षिक भिडियो।",
            channelName = "कृषि सूचना तथा प्रशिक्षण केन्द्र II AITC NEPAL",
            thumbnailUrl = "https://i.ytimg.com/vi/NzwRsir1-ps/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=NzwRsir1-ps",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),
        EducationalVideoEntity(
            id = "vid-cauli-krishitv",
            cropId = "crop-cauliflower",
            topicId = 6, // बालीको हेरचाह
            titleNe = "काउली, ब्रोकाउली, बन्दाकाउली खेती गर्दा ध्यान दिनुपर्ने कुराहरू",
            descriptionNe = "गोभी वर्गीय तरकारी खेतीमा ध्यान दिनुपर्ने प्राविधिक पक्षहरू।",
            channelName = "Krishi Television HD",
            thumbnailUrl = "https://i.ytimg.com/vi/aEtHCR-b77o/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=aEtHCR-b77o",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),

        // Tomato - Verified Krishi TV Video
        EducationalVideoEntity(
            id = "vid-tomato-krishitv",
            cropId = "crop-tomato",
            topicId = 1, // बीउ छनोट तथा तयारी
            titleNe = "स्वदेशमै उत्पादित गोलभेंडाको बीउ र उच्च उत्पादन प्रविधि",
            descriptionNe = "स्वदेशी गोलभेंडाको बीउ प्रयोग तथा उत्पादन व्यवस्थापन सम्बन्धी भिडियो।",
            channelName = "Krishi Television HD",
            thumbnailUrl = "https://i.ytimg.com/vi/F0iA4iceI-0/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=F0iA4iceI-0",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),

        // Ginger - Verified Nepali Agriculture Video
        EducationalVideoEntity(
            id = "vid-ginger-nepal",
            cropId = "crop-ginger",
            topicId = 8, // उत्पादन तथा भण्डारण
            titleNe = "अदुवा खेती तथा व्यावसायिक सम्भावना",
            descriptionNe = "नेपालमा व्यावसायिक अदुवा खेती र उत्पादन अनुभव सम्बन्धी भिडियो।",
            channelName = "Krishna Budhathoki",
            thumbnailUrl = "https://i.ytimg.com/vi/zl1ampj04Es/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=zl1ampj04Es",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        ),

        // Mustard - Verified AITC Nepal Video
        EducationalVideoEntity(
            id = "vid-mustard-aitc",
            cropId = "crop-mustard",
            topicId = 2, // जग्गा तयारी तथा रोपाइँ
            titleNe = "तोरी र मकै खेतीबाट व्यावसायिक कृषि यात्रा",
            descriptionNe = "कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal) द्वारा प्रस्तुत तोरी तथा मिश्रित खेती अनुभव।",
            channelName = "कृषि सूचना तथा प्रशिक्षण केन्द्र II AITC NEPAL",
            thumbnailUrl = "https://i.ytimg.com/vi/CB3o_HuWwQs/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=CB3o_HuWwQs",
            duration = null,
            provider = "youtube",
            downloadPermission = false,
            isSampleMedia = false
        )
    )

    /**
     * Verified official Bakaiya Rural Municipality & Bagmati Province Makwanpur contacts retrieved from
     * bakaiyakrishi.gov.np and bagamati.gov.np.
     * Unverified individual ward phone numbers are NOT invented per Section 10 and Section 16.
     */
    val helplineContacts = listOf(
        HelplineContactEntity(
            id = "help-bakaiya-agri-1",
            nameNe = "हिमश्वेता भट्ट",
            designationNe = "निशुल्क कृषि प्राविधिक सेवा — आर्थिक विकास शाखा / कृषि सम्पर्क",
            wardNe = "बकैया गाउँपालिका कार्यालय (हात्तीसुँडे)",
            wardNumber = 0,
            mobileNumber = "9868445081",
            email = "bakaiyaagri@gmail.com",
            isVerified = true
        ),
        HelplineContactEntity(
            id = "help-bakaiya-cao",
            nameNe = "बकैया गाउँपालिका कार्यपालिकाको कार्यालय",
            designationNe = "प्रमुख प्रशासकीय अधिकृत / कृषि तथा पशु सेवा समन्वय",
            wardNe = "बकैया-३, हात्तीसुँडे, मकवानपुर",
            wardNumber = 3,
            mobileNumber = "9855088066",
            email = "bakaiyagaunpalika@gmail.com",
            isVerified = true
        ),
        HelplineContactEntity(
            id = "help-makwanpur-vet",
            nameNe = "भेटेरिनरी अस्पताल तथा पशु सेवा विज्ञ केन्द्र, मकवानपुर",
            designationNe = "जिल्ला पशु चिकित्सक तथा पशुपालन प्राविधिक सेवा",
            wardNe = "मकवानपुर जिल्ला (बकैयासहित सबै स्थानीय तह)",
            wardNumber = 0,
            mobileNumber = "057-412828",
            email = "vhospitalmak@gmail.com",
            isVerified = true
        ),
        HelplineContactEntity(
            id = "help-makwanpur-vet-mob",
            nameNe = "भेटेरिनरी अस्पताल मोबाइल सेवा (मकवानपुर)",
            designationNe = "पशु रोग तथा आकस्मिक परामर्श सम्पर्क",
            wardNe = "मकवानपुर जिल्ला तथा बकैया क्षेत्र",
            wardNumber = 0,
            mobileNumber = "9855070728",
            email = "vhospitalmak@gmail.com",
            isVerified = true
        )
    )

    // Livestock delegations for unified seed-data access
    val animalCategories get() = BakaiyaLivestockSeedData.animalCategories
    val animals get() = BakaiyaLivestockSeedData.animals
    val animalBreeds get() = BakaiyaLivestockSeedData.animalBreeds
    val animalHousing get() = BakaiyaLivestockSeedData.animalHousing
    val animalFeeding get() = BakaiyaLivestockSeedData.animalFeeding
    val animalHealth get() = BakaiyaLivestockSeedData.animalHealth
    val animalDiseases get() = BakaiyaLivestockSeedData.animalDiseases
    val animalImages get() = BakaiyaLivestockSeedData.animalImages
}
