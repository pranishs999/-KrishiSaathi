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

    val crops = listOf(
        BakaiyaCropEntity(
            id = "crop-paddy",
            categoryId = "cat-cereal",
            nameNe = "धान",
            nameEn = "Paddy / Rice",
            scientificName = "Oryza sativa L.",
            descriptionNe = "धान बकैया गाउँपालिकाको तल्लो उपत्यका तथा खोला किनारका फाँटहरू (छतिवन, हात्तीसुँडे, शिखरपुर) को प्रमुख खाद्यान्न बाली हो।",
            varieties = "सावित्री, राधा-४, रामधान, हर्दिनाथ-३, सुख्खा धान-३",
            plantingSeason = "असार १ देखि साउन १५ सम्म (बर्खे धान)",
            climateOverview = "धान खेतीका लागि न्यानो र ओसिलो हावापानी उपयुक्त मानिन्छ। बाली वृद्धि चरणमा पर्याप्त चिस्यान र पाक्ने बेलामा घमाइलो मौसम आवश्यक हुन्छ।",
            altitude = "६० देखि १५०० मिटर (बकैयाका तल्लो फाँट तथा टार)",
            tempMin = 20.0,
            tempMax = 35.0,
            humidityMin = null, // Specific % omitted when not standardized across cultivars
            humidityMax = null,
            soilTypes = "पानी अडिने चिम्टाइलो र दोमट माटो",
            soilPhMin = 5.5,
            soilPhMax = 6.8,
            sunlight = "दैनिक ६ देखि ८ घण्टा पूर्ण घाम आवश्यक",
            waterRequirement = "उच्च — खेतमा नियमित चिस्यान तथा हिलो व्यवस्थापन आवश्यक",
            landPreparation = "१. खेतलाई २-३ पटक जोतेर पानी लगाई हिल्याउने।\n२. डल्ला फुटाई पाटा लगाएर खेत सम्याउने।\n३. आली मर्मत गरी झारपात सडाउने।",
            seedSelection = "१. स्वस्थ, पुष्ट र प्रमाणित बीउ छान्ने।\n२. प्रति रोपनी करिब २.५ देखि ३ केजी बीउको ब्याड राख्ने।\n३. नुनपानी घोलमा तैरिएका भुस/कमजोर बीउ हटाउने।",
            spacing = "लाइनदेखि लाइन २० से.मी. र बोटदेखि बोट १५ से.मी. दूरीमा प्रति गाँज २-३ वटा बेर्ना रोप्ने।",
            fertilizer = "नेपाल कृषि अनुसन्धान परिषद् (NARC) को सामान्य सिफारिस अनुसार प्राङ्गारिक कम्पोस्ट मल र नाइट्रोजन, फस्फोरस तथा पोटास सन्तुलित रूपमा प्रयोग गर्ने (माटो परीक्षण प्रतिवेदन अनुसार मात्रा निर्धारण गर्नु उत्तम हुन्छ)।",
            irrigation = "बेर्ना सारेको सुरुवाती दिनमा २-३ से.मी. पानी राख्ने। गाँज आउने र पसाउने (फूल फुल्ने) चरणमा खेत सुक्न नदिने।",
            cropCare = "रोपेको २५-३० दिन र ४५-५० दिनमा गरी दुई पटक गोडमेल गर्ने। खेतका आली सफा राख्ने।",
            pestDiseaseControl = "मरुवा (Blast), डढुवा र गवारो कीरा प्रमुख समस्या हुन्। रोग प्रतिरोधी जात लगाउने र प्राविधिकको सल्लाहमा मात्र विषादी प्रयोग गर्ने।",
            harvesting = "बालाको ८५-९० प्रतिशत दाना पहेँलो भई पाकेपछि कटानी गर्ने र घाममा सुकाएर दाउनी गर्ने।",
            storage = "दानालाई राम्ररी सुकाई चिस्यान १२ प्रतिशतभन्दा कम बनाएर सफा भकारी, सुपरग्रेन ब्याग वा मेटालिक ड्रममा हावा नछिर्ने गरी राख्ने।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / कृषि डायरी",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Rice_plants_%28IRRI%29.jpg/640px-Rice_plants_%28IRRI%29.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-maize",
            categoryId = "cat-cereal",
            nameNe = "मकै",
            nameEn = "Maize / Corn",
            scientificName = "Zea mays L.",
            descriptionNe = "मकै बकैया गाउँपालिकाको मध्य तथा उच्च पहाडी पाखो बारी (धियाल, मन्थली, ठूलो धियाल) को प्रमुख खाद्यान्न बाली हो।",
            varieties = "देउती, शितला, मनकामना-३, रामपुर कम्पोजिट, पोषिलो मकै-१",
            plantingSeason = "फागुन १५ देखि वैशाख १५ (वर्षे मकै)",
            climateOverview = "मकै न्यानो हावापानी मन पराउने तर पानी जमेको नसहने बाली हो। बकैयाका पहाडी बारीहरूमा वसन्त तथा वर्षे याममा सफलतापूर्वक खेती गरिन्छ।",
            altitude = "मध्य पहाडदेखि तराईसम्म (३०० देखि १८०० मिटर क्षेत्र उपयुक्त)",
            tempMin = 18.0,
            tempMax = 32.0,
            humidityMin = null,
            humidityMax = null,
            soilTypes = "पानीको राम्रो निकास भएको, प्राङ्गारिक पदार्थयुक्त दोमट र बलौटे दोमट माटो",
            soilPhMin = 5.5,
            soilPhMax = 7.0,
            sunlight = "पूर्ण घाम आवश्यक",
            waterRequirement = "मध्यम — पानी जम्न दिनु हुँदैन, जुँगा निस्कने र दाना भरिने बेला चिस्यान अनिवार्य",
            landPreparation = "१. हिउँदको वर्षापछि जमिनलाई २-३ पटक गहिरो जोत्ने।\n२. माटो बुर्बुराउँदो बनाउने र पानी निकासको कुलेसो मिलाउने।",
            seedSelection = "१. प्रमाणित तथा उपचारित बीउ छान्ने।\n२. प्रति रोपनी करिब १ देखि १.२ केजी बीउ कुलेसोमा रोप्ने।",
            spacing = "लाइनदेखि लाइन ६०-७५ से.मी. र बोटदेखि बोट २५ से.मी. दूरी कायम गर्ने।",
            fertilizer = "गोबर/कम्पोस्ट मल र आधारभूत मल (डीएपी, पोटास तथा आधा युरिया) रोप्ने बेलामा प्रयोग गरी बाँकी युरिया घुँडा-घुँडा भएपछि र चमरा आउनुअघि टपड्रेस गर्ने (माटो परीक्षण अनुसार मात्रा यकिन गर्ने)।",
            irrigation = "जरा फैलाउने, घुँडा आउने, चमरा तथा जुँगा निस्कने र दूध पस्ने अवस्थामा सिँचाइ दिनु महत्वपूर्ण हुन्छ।",
            cropCare = "रोपेको २५-३० दिनमा पहिलो गोडमेल गरी फेदमा उकेरा (माटो चढाउने) लगाउने र बाक्लो बिरुवा छाँट्ने।",
            pestDiseaseControl = "अमेरिकन फौजी कीरा (Fall Armyworm) को गुभोमा नियमित निगरानी गर्ने। फेरोमोन ट्र्याप, निमजन्य जैविक विषादी वा कृषि प्राविधिकको सिफारिस अनुसार नियन्त्रण अपनाउने।",
            harvesting = "घोगाको खोस्टा सुकेर खैरो भएपछि र दानाको फेदमा कालो दाग (Black layer) बनेपछि भाँच्ने।",
            storage = "घोगा वा छोडाएको दानालाई घाममा राम्ररी सुकाई चिस्यान १२ प्रतिशतभन्दा कम भएपछि सुक्खा स्थान वा हर्मेटिक ब्यागमा भण्डारण गर्ने।",
            sourceReference = "राष्ट्रिय मकै बाली अनुसन्धान कार्यक्रम, रामपुर (NARC)",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Zea_mays_crop.jpg/640px-Zea_mays_crop.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-cauliflower",
            categoryId = "cat-vegetable",
            nameNe = "काउली",
            nameEn = "Cauliflower",
            scientificName = "Brassica oleracea var. botrytis",
            descriptionNe = "काउली बकैया गाउँपालिकाका कृषकहरूको प्रमुख व्यावसायिक तरकारी बाली हो। हिउँदे तथा बेमौसमी दुवै याममा खेती गरी राम्रो आम्दानी लिन सकिन्छ।",
            varieties = "काठमाडौँ स्थानीय, स्नो मिस्टिक, सिल्भर कप (मौसम अनुसार अगौटे/मध्यम/पछौटे जात)",
            plantingSeason = "भदौ देखि कात्तिक (मुख्य हिउँदे याम); पहाडी भेगमा बेमौसमी खेती समेत हुने",
            climateOverview = "चिसो तथा समशीतोष्ण हावापानी काउलीको गुँद (Curd) विकासका लागि उत्तम हुन्छ।",
            altitude = "तराईदेखि उच्च पहाडसम्म (मौसम अनुसार जात छनोट गरी)",
            tempMin = 15.0,
            tempMax = 25.0,
            humidityMin = null,
            humidityMax = null,
            soilTypes = "प्राङ्गारिक पदार्थयुक्त, पानी नजम्ने मलिलो दोमट माटो",
            soilPhMin = 6.0,
            soilPhMax = 7.0,
            sunlight = "दैनिक खुला घाम आवश्यक",
            waterRequirement = "नियमित हल्का चिस्यान आवश्यक, तर जलजमाव हुन नहुने",
            landPreparation = "१. जमिनलाई ३-४ पटक जोती डल्ला फुटाउने।\n२. पानी निकासका लागि उँचो ब्याड र कुलेसो बनाउने।",
            seedSelection = "नर्सरी ब्याडमा स्वस्थ बेर्ना तयार पारी ४-५ पात आएपछि मुख्य खेतमा सार्ने।",
            spacing = "जात अनुसार लाइनदेखि लाइन ५० से.मी. र बोटदेखि बोट ४५ से.मी.।",
            fertilizer = "प्रशस्त कुहिएको गोबरमलका साथै सिफारिस अनुसार सन्तुलित रासायनिक मल तथा सूक्ष्म तत्व (बोरोन/मोलिब्डेनम) प्रयोग गर्ने।",
            irrigation = "बेर्ना सारेपछि तुरुन्त सिँचाइ गरी माटोको चिस्यान हेरी साप्ताहिक वा आवश्यकता अनुसार पानी दिने।",
            cropCare = "काउलीको फूल पहेँलो हुन नदिन नजिकैको पातले फूललाई हल्का ढाकिदिने (Blanching) र गोडमेल गर्ने।",
            pestDiseaseControl = "गाँठे रोग (Clubroot) बाट बच्न माटोको pH सन्तुलन र बाली चक्र अपनाउने। पुतली लार्भा र लाही कीराको जैविक तथा सुरक्षित व्यवस्थापन गर्ने।",
            harvesting = "फूल कसिलो र सफा सेतो छँदै, कल्ली फाट्नुअघि नै धारिलो चक्कुले पातसहित काट्ने।",
            storage = "काटेपछि छायादार र चिसो स्थानमा राखी छिटो ताजा अवस्था मै बजार पठाउने।",
            sourceReference = "राष्ट्रिय तरकारी बाली विकास केन्द्र / कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC)",
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
            plantingSeason = "तल्लो क्षेत्रमा भदौ-असोज र माघ-फागुन; टनेलमा वर्षैभरि अनुकूल व्यवस्थापन",
            climateOverview = "न्यानो र घमाइलो हावापानी गोलभेंडाका लागि उपयुक्त हुन्छ। तुषारो र अत्यधिक वर्षाले बोट तथा फलमा असर गर्दछ।",
            altitude = "तराईदेखि १८०० मिटर पहाडी क्षेत्रसम्म",
            tempMin = 18.0,
            tempMax = 28.0,
            humidityMin = null,
            humidityMax = null,
            soilTypes = "पानी निकास भएको गहिरो र उर्वर बलौटे दोमट माटो",
            soilPhMin = 6.0,
            soilPhMax = 6.8,
            sunlight = "दैनिक ६-८ घण्टा घाम आवश्यक",
            waterRequirement = "नियमित सन्तुलित चिस्यान; थोपा सिँचाइ उपयोगी",
            landPreparation = "माटो राम्रोसँग खनजोत गरी उँचो ड्याङ (Raised Bed) निर्माण गर्ने।",
            seedSelection = "रोगमुक्त नर्सरीमा बेर्ना उत्पादन गरी करिब २५-३० दिनमा मुख्य जमिनमा सार्ने।",
            spacing = "सामान्यतया लाइनदेखि लाइन ६०-७५ से.मी. र बोटदेखि बोट ४५-५० से.मी.।",
            fertilizer = "प्रशस्त पाकेको गोबरमल र सिफारिस अनुसार नाइट्रोजन, फस्फोरस, पोटास तथा क्याल्सियम पोषण व्यवस्थापन गर्ने।",
            irrigation = "फल लाग्ने र बढ्ने बेलामा एकनासको चिस्यान राख्ने (अचानक धेरै पानी वा सुक्खाले फल फुट्ने समस्या आउँछ)।",
            cropCare = "अग्लो जातमा बाँस वा डोरीको थाँक्रो (Staking) दिने र अनावश्यक बगली मुना हटाउने।",
            pestDiseaseControl = "पछौटे डढुवा (Late Blight), ओइलाउने रोग र फल कुहाउने गवारोबाट जोगाउन नियमित अवलोकन र एकीकृत शत्रुजीव व्यवस्थापन (IPM) अपनाउने।",
            harvesting = "बजारको दूरी हेरी रंग परिवर्तन हुन थालेपछि (Breaker stage) वा पूरा पाकेपछि टिप्ने।",
            storage = "चोटपटक नलाग्ने गरी क्रेटमा मिलाएर शीतल ठाउँमा राख्ने।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) बागवानी महाशाखा",
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
            plantingSeason = "चैत मध्यदेखि वैशाख अन्त्यसम्म",
            climateOverview = "न्यानो र ओसिलो हावापानीका साथै हल्का छहारी (जस्तै मकैसँग अन्तरबाली) अदुवा खेतीमा लाभदायक हुन्छ।",
            altitude = "समुद्र सतहदेखि करिब १५०० मिटरसम्म",
            tempMin = 19.0,
            tempMax = 30.0,
            humidityMin = null,
            humidityMax = null,
            soilTypes = "पानीको उत्तम निकास भएको, प्राङ्गारिक पदार्थयुक्त खुकुलो दोमट माटो",
            soilPhMin = 5.5,
            soilPhMax = 6.5,
            sunlight = "आंशिक छहारी वा मध्यम घाम अनुकूल",
            waterRequirement = "मध्यम र सन्तुलित; ड्याङमा पानी जमेमा गानो कुहिन्छ",
            landPreparation = "माटो गहिरो जोती २० से.मी. उँचो ड्याङ बनाई पानी तर्किने नाली बनाउने।",
            seedSelection = "२-३ वटा स्वस्थ आँखला भएको, रोगमुक्त बीउ गानो छान्ने र ट्राइकोडर्मा/सुरक्षित उपचार गरेर रोप्ने।",
            spacing = "लाइनदेखि लाइन ३० से.मी. र गानोदेखि गानो २५ से.मी.।",
            fertilizer = "प्रशस्त कुहिएको गोबर/कम्पोस्ट मल, छापो (Mulching) तथा सिफारिस अनुसार मलखाद प्रयोग गर्ने।",
            irrigation = "माटोको चिस्यान कायम राख्न पराल वा स्याउलाको बाक्लो छापो हाल्ने; वर्षामा पानी जम्न नदिने।",
            cropCare = "रोपेपछि बाक्लो छापो राख्ने र आवश्यकता अनुसार झार गोडी उकेरा लगाउने।",
            pestDiseaseControl = "गानो कुहिने रोग (Rhizome Rot) अदुवाको मुख्य समस्या भएकाले स्वस्थ बीउ, पानी निकास र जैविक ट्राइकोडर्मा व्यवस्थापनमा ध्यान दिने।",
            harvesting = "रोपेको ८-९ महिनामा पात र बोट सुक्न थालेपछि गानो नकाटिने गरी खन्ने (माउ अदुवा बिचमा निकाल्ने स्थानीय चलन समेत रहेको छ)।",
            storage = "सुक्खा र हावा खेल्ने छायादार ठाउँ वा सुरक्षित खाडल प्रविधि (Pit storage) मा बीउ अदुवा सुरक्षित राख्ने।",
            sourceReference = "राष्ट्रिय अदुवा बाली अनुसन्धान कार्यक्रम, कपुरकोट (NARC)",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Ginger_plant.jpg/640px-Ginger_plant.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-mustard",
            categoryId = "cat-oilseed",
            nameNe = "तोरी",
            nameEn = "Mustard / Rapeseed",
            scientificName = "Brassica campestris var. toria",
            descriptionNe = "तोरी बकैयाका धान काटेपछिका खेतबारीमा हिउँद याममा लगाइने प्रमुख तेलहन बाली हो।",
            varieties = "विकास, उन्नति, प्रगति, प्रीति, मोरङ तोरी-२",
            plantingSeason = "असोज १५ देखि कात्तिक १५ सम्म",
            climateOverview = "हिउँदको चिसो र घमाइलो मौसममा तोरी राम्रो फल्दछ। अत्यधिक कुहिरो र हुस्सुले लाही कीराको प्रकोप बढाउन सक्छ।",
            altitude = "तराईदेखि करिब १५०० मिटर मध्य पहाडसम्म",
            tempMin = 12.0,
            tempMax = 25.0,
            humidityMin = null,
            humidityMax = null,
            soilTypes = "हल्का दोमटदेखि चिम्टाइलो दोमट माटो",
            soilPhMin = 6.0,
            soilPhMax = 7.5,
            sunlight = "पूर्ण घाम आवश्यक",
            waterRequirement = "न्यून देखि मध्यम",
            landPreparation = "धान काटेपछि खेतमा चिस्यान छँदै २ पटक जोतेर माटो मसिनो बनाई छर्ने।",
            seedSelection = "रोगमुक्त, सफा र उच्च तेल प्रतिशत भएका उन्नत जातको बीउ प्रयोग गर्ने।",
            spacing = "लाइनदेखि लाइन ३० से.मी. र बोटदेखि बोट १० से.मी. (छरुवा वा लाइन दुवै प्रविधि)।",
            fertilizer = "कम्पोस्ट मल र नाइट्रोजन, फस्फोरस, पोटासका साथै तेलहन बालीमा सल्फर र बोरोनको सन्तुलित प्रयोग लाभदायक हुन्छ।",
            irrigation = "फूल फुल्नुअघि र कोसा लाग्ने अवस्था (रोपेको २५-३० दिन र ५० दिन आसपास) मा हल्का सिँचाइ गर्दा उत्पादनमा उल्लेखनीय वृद्धि हुन्छ।",
            cropCare = "उम्रेको १५-२० दिनमा बाक्लो बिरुवा छाँटेर (Thinning) झार गोड्ने।",
            pestDiseaseControl = "लाही कीरा (Aphids) र डढुवा रोगको समयमै निगरानी गरी सुरक्षित जैविक/सिफारिसकृत उपाय अपनाउने।",
            harvesting = "७५-८० प्रतिशत कोसा पहेँलो भएपछि बिहानीपख शीत छँदै कटानी गर्ने ताकि दाना खेतमा नझरोस्।",
            storage = "दानालाई घाममा राम्ररी सुकाई चिस्यान ८ प्रतिशतभन्दा कम पारेर सुक्खा भाँडा वा बोरामा भण्डारण गर्ने।",
            sourceReference = "तेलहन बाली अनुसन्धान कार्यक्रम, नवलपुर (NARC)",
            isVerifiedContent = true,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Mustard_field_in_Nepal.jpg/640px-Mustard_field_in_Nepal.jpg"
        ),
        BakaiyaCropEntity(
            id = "crop-lentil",
            categoryId = "cat-pulse",
            nameNe = "मसुरो",
            nameEn = "Lentil",
            scientificName = "Lens culinaris",
            descriptionNe = "मसुरो हिउँदमा धान खेतको चिस्यानमा वा बारीमा लगाइने प्रमुख दलहन बाली हो। यसले माटोको उर्वराशक्ति बढाउन र पौष्टिक दाल आपूर्ति गर्न मद्दत गर्दछ।",
            varieties = "शिमल, शिखर, सगुन, सिन्दुर, शिशिर, खजुरा मसुरो-१, २, ३",
            plantingSeason = "कात्तिक (धान काटिसकेपछि वा धान काट्नु १५ दिनअघि रिले खेती)",
            climateOverview = "हिउँदको शीतल मौसम वृद्धि चरणका लागि र न्यानो सुक्खा मौसम पाक्ने चरणका लागि उपयुक्त हुन्छ।",
            altitude = "तराई, भित्री मधेस तथा मध्य पहाडी बेँसी",
            tempMin = null,
            tempMax = null,
            humidityMin = null,
            humidityMax = null,
            soilTypes = "पानी नजम्ने दोमट र हल्का चिम्टाइलो माटो",
            soilPhMin = null,
            soilPhMax = null,
            sunlight = "पूर्ण घाम",
            waterRequirement = "न्यून — प्रायः हिउँदे चिस्यानमा नै फल्ने, पानी जम्न नहुने",
            landPreparation = "राम्ररी जोतेर सम्याएको वा धान खेतको रिले बालीका रूपमा छर्न सकिने।",
            seedSelection = "स्वस्थ बीउलाई राइजोबियम (Rhizobium) र ढुसीनाशक बाट उपचार गरी प्रयोग गर्ने।",
            spacing = "लाइन रोपाइँमा लाइनदेखि लाइन २०-२५ से.मी.।",
            fertilizer = "कोसेबाली भएकाले जराको गिर्खाबाट नाइट्रोजन स्थिरीकरण गर्दछ; सिफारिस अनुसार आधारभूत फस्फोरस र कम्पोस्ट मल प्रयोग गर्ने।",
            irrigation = "पानी जम्न नदिने; अत्यन्त सुक्खा भएमा कोसा लाग्ने बेलामा हल्का सिँचाइ गर्ने।",
            cropCare = "सुरुवाती ३०-३५ दिनसम्म खेतलाई झारपातमुक्त राख्ने।",
            pestDiseaseControl = "जरा कुहिने/ओइलाउने रोग (Wilt), स्टेमफाइलियम डढुवा र कोसा खाने कीराबाट जोगाउन प्राविधिक सल्लाह अनुसार व्यवस्थापन गर्ने।",
            harvesting = "बोट र कोसा सुकेर खैरो भएपछि बिहानीपख कटानी गरी सुकाएर दाउनी गर्ने।",
            storage = "दानालाई सुकाएर चिस्यान १० प्रतिशत मुनि झारी सुरक्षित भाँडा वा सुपरग्रेन ब्यागमा घुन नलाग्ने गरी राख्ने।",
            sourceReference = "कोसेबाली अनुसन्धान कार्यक्रम, खजुरा (NARC)",
            isVerifiedContent = true,
            thumbnailUrl = null
        ),
        BakaiyaCropEntity(
            id = "crop-banana",
            categoryId = "cat-fruit",
            nameNe = "केरा",
            nameEn = "Banana",
            scientificName = "Musa spp.",
            descriptionNe = "केरा बकैयाका न्यानो तल्लो उपत्यका र खोला किनारका बस्तीहरूमा वर्षैभरि उत्पादन र आयआर्जन दिने प्रमुख फलफूल बाली हो।",
            varieties = "मालभोग, विलियम हाइब्रिड, जी-९ (G-9), झापाली मालभोग, चिनियाँ चम्पा",
            plantingSeason = "फागुन-चैत र असार-साउन (वर्षाको सुरुवात)",
            climateOverview = "न्यानो, आर्द्र र तुषारो नपर्ने हावापानी केराको व्यावसायिक उत्पादनका लागि उपयुक्त हुन्छ।",
            altitude = "तराईदेखि करिब १००० मिटरसम्मका न्यानो उपत्यका",
            tempMin = 15.0,
            tempMax = 35.0,
            humidityMin = null,
            humidityMax = null,
            soilTypes = "गहिरो, मलिलो, पानी नजम्ने दोमट माटो",
            soilPhMin = 6.0,
            soilPhMax = 7.5,
            sunlight = "पूर्ण घाम आवश्यक",
            waterRequirement = "उच्च — तर पानी जम्न नहुने, नियमित सिँचाइ आवश्यक",
            landPreparation = "६०x६०x६० से.मी. आकारका खाडल खनेर पाकेको गोबरमल र माटो मिसाई पुर्ने।",
            seedSelection = "स्वस्थ तरबार आकारको पुतली (Sword sucker) वा टिस्युकल्चर बिरुवा छनोट गर्ने।",
            spacing = "जात अनुसार बोटदेखि बोट र लाइनदेखि लाइन २ देखि २.५ मिटर।",
            fertilizer = "खाडल तयार गर्दा प्रशस्त कम्पोस्ट मल हाल्ने र वृद्धि अवस्था अनुसार कृषि प्राविधिकको सिफारिस बमोजिम पोषण व्यवस्थापन गर्ने।",
            irrigation = "सुक्खा याममा साप्ताहिक वा माटो हेरी सिँचाइ दिने र वर्षायाममा निकास खोल्ने।",
            cropCare = "अनावश्यक धेरै पुतली (Suckers) हटाई मुख्य घरीको वृद्धि सुनिश्चित गर्ने र बुङ्गो आएपछि घरीलाई टेको दिने।",
            pestDiseaseControl = "पानामा ओइलाउने रोग (Panama Wilt), गवारो कीरा (Rhizome Weevil) र पात थोप्ले रोगको नियमित निरीक्षण गर्ने।",
            harvesting = "कोसा भरिएर कुना हराएपछि र हल्का पहेंलोपन देखिन थालेपछि घरी काट्ने।",
            storage = "सावधानीपूर्वक छायाँमा सुरक्षित राखी स्वाभाविक रूपमा पकाएर बजार व्यवस्थापन गर्ने।",
            sourceReference = "नेपाल कृषि अनुसन्धान परिषद् (NARC) / फलफूल विकास निर्देशनालय",
            isVerifiedContent = true,
            thumbnailUrl = null
        )
    )

    val cropImages = listOf(
        CropImageEntity(
            id = "img-paddy-1",
            cropId = "crop-paddy",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Rice_plants_%28IRRI%29.jpg/640px-Rice_plants_%28IRRI%29.jpg",
            captionNe = "धान बालीको सामान्य पहिचान तस्बिर (स्रोत: IRRI / Wikimedia Commons)",
            stageCategory = "पहिचान तथा वृद्धि",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-maize-1",
            cropId = "crop-maize",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Zea_mays_crop.jpg/640px-Zea_mays_crop.jpg",
            captionNe = "मकै बालीको सामान्य पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
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
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Ginger_plant.jpg/640px-Ginger_plant.jpg",
            captionNe = "अदुवाको बोट पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "पहिचान तथा वृद्धि",
            isSampleImage = false
        ),
        CropImageEntity(
            id = "img-mustard-1",
            cropId = "crop-mustard",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Mustard_field_in_Nepal.jpg/640px-Mustard_field_in_Nepal.jpg",
            captionNe = "नेपालको खेतमा फुलेको तोरी बाली (स्रोत: Wikimedia Commons)",
            stageCategory = "फूल फुल्ने चरण",
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
     * Verified official Bakaiya Rural Municipality contacts retrieved from bakaiyakrishi.gov.np.
     * Unverified individual ward phone numbers are NOT invented per Section 10 and Section 16.
     */
    val helplineContacts = listOf(
        HelplineContactEntity(
            id = "help-bakaiya-agri-1",
            nameNe = "हिमश्वेता भट्ट",
            designationNe = "आर्थिक विकास शाखा प्रमुख / सूचना अधिकारी (कृषि सम्पर्क)",
            wardNe = "बकैया गाउँपालिका कार्यालय (हात्तीसुँडे)",
            wardNumber = 0,
            mobileNumber = "9868445081",
            email = "bakaiyaagri@gmail.com",
            isVerified = true
        ),
        HelplineContactEntity(
            id = "help-bakaiya-cao",
            nameNe = "बकैया गाउँपालिका कार्यपालिकाको कार्यालय",
            designationNe = "प्रमुख प्रशासकीय अधिकृत / मुख्य कार्यालय सम्पर्क",
            wardNe = "बकैया-३, हात्तीसुँडे, मकवानपुर",
            wardNumber = 3,
            mobileNumber = "9855088066",
            email = "bakaiyagaunpalika@gmail.com",
            isVerified = true
        )
    )
}
