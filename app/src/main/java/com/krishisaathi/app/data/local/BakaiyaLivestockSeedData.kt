package com.krishisaathi.app.data.local

import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.domain.model.AnimalBreed
import com.krishisaathi.app.domain.model.AnimalBreedingGuidance
import com.krishisaathi.app.domain.model.AnimalCategory
import com.krishisaathi.app.domain.model.AnimalDisease
import com.krishisaathi.app.domain.model.AnimalFeedingGuidance
import com.krishisaathi.app.domain.model.AnimalHealthGuidance
import com.krishisaathi.app.domain.model.AnimalHousingGuidance
import com.krishisaathi.app.domain.model.AnimalImage
import com.krishisaathi.app.domain.model.AnimalProductionGuidance
import com.krishisaathi.app.domain.model.EducationalVideo

/**
 * Verified Livestock Knowledge Content Store for Bakaiya Krishi Saathi (पशुपालन मोड्युल).
 *
 * Content Safety & Data Integrity Policy:
 * - No fabricated drug dosages, vaccine schedules, or specific numeric feed ration quantities.
 * - Directs farmers to local Livestock Service Technicians (पशु सेवा प्राविधिक / भेटेरिनरी डाक्टर)
 *   for case-specific diagnosis and treatment.
 * - Unverified/incomplete species (Sheep: in_review, Duck: needs_source) keep `isPublished = false`
 *   so they do not leak into the public farmer UI until editorial verification completes.
 * - Uses verified Wikimedia Commons CC-licensed images and verified Nepali agricultural YouTube IDs.
 */
object BakaiyaLivestockSeedData {

    private const val VET_SOURCE = "कृषि ज्ञान केन्द्र मकवानपुर / भेटेरिनरी अस्पताल तथा पशु सेवा विज्ञ केन्द्र / NARC"

    val animalCategories = listOf(
        AnimalCategory(
            id = "acat-cattle-buffalo",
            nameNe = "गाई / भैंसी",
            nameEn = "Cattle & Buffalo",
            descriptionNe = "दूध, मलखाद र कृषि कार्यका लागि प्रमुख ठूला पशुधन",
            iconName = "pets",
            sortOrder = 1
        ),
        AnimalCategory(
            id = "acat-goat",
            nameNe = "बाख्रा / भेडा",
            nameEn = "Goat & Sheep",
            descriptionNe = "बकैयाका पाखो तथा मध्य पहाडी भेगमा लोकप्रिय मासु तथा आयआर्जनको स्रोत",
            iconName = "pets",
            sortOrder = 2
        ),
        AnimalCategory(
            id = "acat-poultry",
            nameNe = "कुखुरा / हाँस",
            nameEn = "Poultry",
            descriptionNe = "अण्डा तथा मासु उत्पादनका लागि घरायसी तथा व्यावसायिक पंक्षीपालन",
            iconName = "egg",
            sortOrder = 3
        ),
        AnimalCategory(
            id = "acat-pig-fish",
            nameNe = "बंगुर / माछा",
            nameEn = "Pig & Fishery",
            descriptionNe = "तल्लो उपत्यका र खोला किनार क्षेत्रका लागि लाभदायक पशुपालन तथा मत्स्यपालन",
            iconName = "water",
            sortOrder = 4
        )
    )

    val animals = listOf(
        Animal(
            id = "animal-cattle",
            categoryId = "acat-cattle-buffalo",
            nameNe = "गाईपालन",
            nameEn = "Cattle Farming",
            scientificName = "Bos taurus / Bos indicus",
            speciesCode = "cattle",
            summaryNe = "बकैया गाउँपालिकाका तल्लो फाँट र मध्य पहाडी बस्तीहरूमा दूध उत्पादन तथा प्राङ्गारिक गोबर मलका लागि गाईपालन महत्वपूर्ण आधार हो। उन्नत तथा स्थानीय नस्लको उचित गोठ, सन्तुलित आहारा र स्वास्थ्य हेरचाहबाट दिगो आम्दानी लिन सकिन्छ।",
            purposeTagsNe = "दूध उत्पादन, गोबर/प्राङ्गारिक मल, बायोग्याँस",
            bakaiyaRelevanceNe = "बकैया-३, ४, ५, ६ र ७ का कृषक समूहहरूमा डेरी सहकारी मार्फत दूध संकलन र खेतीमा गोबर मलको प्रयोग व्यापक छ।",
            climateSuitabilityNe = "न्यानो समशीतोष्णदेखि उपोष्ण हावापानी; ग्रीष्मयाममा छाया र हावा चल्ने गोठ आवश्यक।",
            altitudeRangeNe = "६० देखि २००० मिटरसम्म उपयुक्त",
            recommendedBreedsSummaryNe = "उन्नत क्रस (जर्सी क्रस, होल्स्टाइन फ्रिजियन क्रस) तथा स्थानीय (लुलु, अछामे, पहाडी)",
            sourceReference = VET_SOURCE,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Cow_female_black_white.jpg/640px-Cow_female_black_white.jpg",
            reviewStatus = "published",
            isVerifiedContent = true,
            isPublished = true,
            sortOrder = 1
        ),
        Animal(
            id = "animal-buffalo",
            categoryId = "acat-cattle-buffalo",
            nameNe = "भैंसीपालन",
            nameEn = "Buffalo Farming",
            scientificName = "Bubalus bubalis",
            speciesCode = "buffalo",
            summaryNe = "उच्च घ्यू र चिल्लोपदार्थ (Fat) युक्त दूधका लागि नेपालमा भैंसीपालन अत्यन्त लोकप्रिय छ। मकवानपुरको भित्री मधेस तथा मध्य पहाडी भेगमा स्थानीय तथा मुर्रा क्रस भैंसी सफलतापूर्वक पालिन्छ।",
            purposeTagsNe = "उच्च चिल्लोपनयुक्त दूध, मासु, प्राङ्गारिक मल",
            bakaiyaRelevanceNe = "बकैयाका नदी किनार र भित्री उपत्यकामा हरियो घाँस र पराल उपलब्ध हुने भएकाले भैंसीपालन निकै अनुकूल छ।",
            climateSuitabilityNe = "उपोष्ण र न्यानो हावापानी; गर्मीयाममा आहाल (Wallowing) वा सफा पानी छर्कने व्यवस्था उपयोगी।",
            altitudeRangeNe = "६० देखि १८०० मिटरसम्म उपयुक्त",
            recommendedBreedsSummaryNe = "मुर्रा क्रस, लिमे, पारकोटे र स्थानीय पहाडी भैंसी",
            sourceReference = VET_SOURCE,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Water_buffalo_at_India.jpg/640px-Water_buffalo_at_India.jpg",
            reviewStatus = "published",
            isVerifiedContent = true,
            isPublished = true,
            sortOrder = 2
        ),
        Animal(
            id = "animal-goat",
            categoryId = "acat-goat",
            nameNe = "बाख्रापालन",
            nameEn = "Goat Farming",
            scientificName = "Capra aegagrus hircus",
            speciesCode = "goat",
            summaryNe = "बकैया गाउँपालिकाका पाखो बारी, चरिचरन तथा डालेघाँस पाइने मध्य पहाडी वडाहरूमा बाख्रापालन कम लगानीमा छिटो नगद आम्दानी दिने मुख्य व्यवसाय हो।",
            purposeTagsNe = "मासु उत्पादन (खसी/बोका), मलखाद, ग्रामीण रोजगारी",
            bakaiyaRelevanceNe = "धियाल, मन्थली, छतिवन र शिखरपुरका पहाडी टोलहरूमा डालेघाँस (बडहर, खन्यु, इपिल) मा आधारित बाख्रापालन फस्टाएको छ।",
            climateSuitabilityNe = "सुक्खा र हावा चल्ने मौसम अनुकूल; ओसिलो चिसो र वर्षाबाट टाँडे खोरले बचाउँछ।",
            altitudeRangeNe = "१०० देखि २५०० मिटरसम्म उपयुक्त",
            recommendedBreedsSummaryNe = "खरी (स्थानीय प्रमुख), जमुनापारी क्रस, बोयर क्रस (व्यावसायिक मासु)",
            sourceReference = VET_SOURCE,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Hausziege_04.jpg/640px-Hausziege_04.jpg",
            reviewStatus = "published",
            isVerifiedContent = true,
            isPublished = true,
            sortOrder = 3
        ),
        Animal(
            id = "animal-chicken",
            categoryId = "acat-poultry",
            nameNe = "कुखुरापालन",
            nameEn = "Poultry (Chicken) Farming",
            scientificName = "Gallus gallus domesticus",
            speciesCode = "chicken",
            summaryNe = "अण्डा र मासु उत्पादनका लागि घरायसी अर्ध-सघन स्थानीय जात तथा व्यावसायिक ब्रोइलर/लेयर्स/गिरिराज कुखुरापालन बकैयामा व्यापक रूपमा गरिन्छ।",
            purposeTagsNe = "अण्डा, मासु, घरायसी पोषण र छिटो आम्दानी",
            bakaiyaRelevanceNe = "साना तथा महिला कृषक समूहहरूका लागि लोकल तथा गिरिराज/न्यूह्याम्पसायर कुखुरापालन लोकप्रिय आयआर्जन कार्यक्रम हो।",
            climateSuitabilityNe = "न्यानो र समशीतोष्ण हावापानी; चल्लाका लागि ब्रोडिङ (तापमान व्यवस्थापन) अत्यन्त संवेदनशील हुन्छ।",
            altitudeRangeNe = "६० देखि २२०० मिटरसम्म",
            recommendedBreedsSummaryNe = "स्थानीय (साकिनी/घाँटी खुइले), गिरिराज, न्यूह्याम्पसायर, कोब-५०० (ब्रोइलर)",
            sourceReference = VET_SOURCE,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Female_pair.jpg/640px-Female_pair.jpg",
            reviewStatus = "published",
            isVerifiedContent = true,
            isPublished = true,
            sortOrder = 4
        ),
        Animal(
            id = "animal-pig",
            categoryId = "acat-pig-fish",
            nameNe = "बंगुरपालन",
            nameEn = "Pig Farming",
            scientificName = "Sus scrofa domesticus",
            speciesCode = "pig",
            summaryNe = "छिटो तौल बढ्ने, उच्च प्रजनन क्षमता भएको र स्थानीय कृषि उप-उत्पादन उपयोग गर्न सकिने भएकाले बकैयाका विभिन्न समुदायमा बंगुरपालन महत्वपूर्ण व्यवसाय हो।",
            purposeTagsNe = "मासु उत्पादन, छिटो प्रतिफल, प्राङ्गारिक मल",
            bakaiyaRelevanceNe = "स्थानीय बजार तथा हेटौंडा बजारको माग पूर्तिका लागि व्यवस्थित पक्की खोर र जैविक सुरक्षासहित पालन गरिन्छ।",
            climateSuitabilityNe = "तराईदेखि मध्य पहाडसम्म अनुकूल; गर्मीमा शीतल पानी र जाडोमा सुक्खा सोत्तर आवश्यक।",
            altitudeRangeNe = "६० देखि १८०० मिटरसम्म",
            recommendedBreedsSummaryNe = "हुर्रा/पाँग्रे (स्थानीय), योर्कशायर, ल्यान्ड्रेस, ड्युरोक र धरान कालो सुँगुर",
            sourceReference = VET_SOURCE,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Sow_and_five_piglets.jpg/640px-Sow_and_five_piglets.jpg",
            reviewStatus = "published",
            isVerifiedContent = true,
            isPublished = true,
            sortOrder = 5
        ),
        Animal(
            id = "animal-fish",
            categoryId = "acat-pig-fish",
            nameNe = "माछापालन",
            nameEn = "Fishery / Aquaculture",
            scientificName = "Labeo rohita / Cyprinus carpio",
            speciesCode = "fish",
            summaryNe = "बकैया खोला तथा सिँचाइ उपलब्ध तल्लो फाँटहरूमा मिश्रित कार्प माछापालन (Polyculture) र टिलापिया/पङ्गासियस खेतीबाट राम्रो आम्दानी लिन सकिन्छ।",
            purposeTagsNe = "ताजा माछा उत्पादन, बहु-प्रजाति पोखरी खेती",
            bakaiyaRelevanceNe = "पानीको स्रोत भएका तल्लो वडाहरूमा कृषि–माछा एकीकृत (बंगुर-माछा वा हाँस-माछा) प्रणाली उपयोगी छ।",
            climateSuitabilityNe = "न्यानो पानी (कार्पका लागि २०–३०°C उपयुक्त); चिसो उच्च भेगमा रेन्बो ट्राउट अलग प्रविधिमा गरिन्छ।",
            altitudeRangeNe = "६० देखि १२०० मिटरसम्म (कार्प माछा)",
            recommendedBreedsSummaryNe = "रहु (Rohu), नैनी (Mrigal), भाकुर (Catla), कमन कार्प, सिल्भर कार्प, ग्रास कार्प",
            sourceReference = VET_SOURCE,
            thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f4/Catla_catla.jpg/640px-Catla_catla.jpg",
            reviewStatus = "published",
            isVerifiedContent = true,
            isPublished = true,
            sortOrder = 6
        ),
        // Intentionally unpublished unverified species per editorial policy
        Animal(
            id = "animal-sheep",
            categoryId = "acat-goat",
            nameNe = "भेडापालन",
            nameEn = "Sheep Farming",
            scientificName = "Ovis aries",
            speciesCode = "sheep",
            summaryNe = "समीक्षाको क्रममा रहेको सामग्री।",
            sourceReference = VET_SOURCE,
            reviewStatus = "in_review",
            isVerifiedContent = false,
            isPublished = false,
            sortOrder = 7
        ),
        Animal(
            id = "animal-duck",
            categoryId = "acat-poultry",
            nameNe = "हाँसपालन",
            nameEn = "Duck Farming",
            scientificName = "Anas platyrhynchos",
            speciesCode = "duck",
            summaryNe = "थप स्रोत प्रमाणीकरण आवश्यक सामग्री।",
            sourceReference = VET_SOURCE,
            reviewStatus = "needs_source",
            isVerifiedContent = false,
            isPublished = false,
            sortOrder = 8
        )
    )

    val animalBreeds = listOf(
        // Cattle
        AnimalBreed(
            id = "breed-cattle-achhame",
            animalId = "animal-cattle",
            breedNameNe = "अछामे / स्थानीय पहाडी गाई",
            breedNameEn = "Achhame / Local Hill Cattle",
            originType = "indigenous",
            purposeNe = "सहनशील दूध तथा प्राङ्गारिक मल",
            climateSuitabilityNe = "पहाडी र कम स्रोत भएको परिवेशमा अत्यन्त सहनशील",
            characteristicsNe = "सानो कद, रोगप्रतिरोधी क्षमता उच्च र स्थानीय घाँसपातमा बाँच्न सक्ने।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalBreed(
            id = "breed-cattle-jersey-cross",
            animalId = "animal-cattle",
            breedNameNe = "जर्सी क्रस",
            breedNameEn = "Jersey Crossbred",
            originType = "crossbred",
            purposeNe = "व्यावसायिक दूध उत्पादन",
            climateSuitabilityNe = "बकैयाको भित्री मधेस र मध्य पहाडी हावापानीमा अनुकूल",
            characteristicsNe = "मध्यम शरीर, दूधमा चिल्लोपन राम्रो र नियमित आहारामा उच्च दूध उत्पादन।",
            sourceReference = VET_SOURCE,
            sortOrder = 2
        ),
        // Buffalo
        AnimalBreed(
            id = "breed-buffalo-parkote",
            animalId = "animal-buffalo",
            breedNameNe = "पारकोटे / स्थानीय पहाडी भैंसी",
            breedNameEn = "Parkote / Indigenous Hill Buffalo",
            originType = "indigenous",
            purposeNe = "दूध तथा पहाडी अनुकूलता",
            climateSuitabilityNe = "मध्य पहाड र पाखो भेगका लागि अत्यन्त अनुकूल",
            characteristicsNe = "कालो रङ, बलियो खुर, कम दानामा पनि टिकाउ र रोग प्रतिरोधी।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalBreed(
            id = "breed-buffalo-murrah-cross",
            animalId = "animal-buffalo",
            breedNameNe = "मुर्रा क्रस",
            breedNameEn = "Murrah Crossbred",
            originType = "crossbred",
            purposeNe = "उच्च दूध तथा घ्यू उत्पादन",
            climateSuitabilityNe = "तल्लो उपत्यका र नियमित हरियो घाँस उपलब्ध क्षेत्रमा उत्तम",
            characteristicsNe = "घुम्रिएको छोटो सिङ, उच्च दूध क्षमता र व्यावसायिक डेरीमा लोकप्रिय।",
            sourceReference = VET_SOURCE,
            sortOrder = 2
        ),
        // Goat
        AnimalBreed(
            id = "breed-goat-khari",
            animalId = "animal-goat",
            breedNameNe = "खरी बाख्रा",
            breedNameEn = "Khari Goat",
            originType = "indigenous",
            purposeNe = "मासु उत्पादन र नियमित प्रजनन",
            climateSuitabilityNe = "नेपालको तराईदेखि मध्य पहाडसम्म सबैभन्दा सफल स्थानीय जात",
            characteristicsNe = "जुम्ल्याहा पाठापाठी पाउने उच्च क्षमता, छिटो ब्याउने र स्थानीय हावापानी सहनशील।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalBreed(
            id = "breed-goat-jamunapari-cross",
            animalId = "animal-goat",
            breedNameNe = "जमुनापारी / बोयर क्रस",
            breedNameEn = "Jamunapari / Boer Cross",
            originType = "crossbred",
            purposeNe = "छिटो तौल वृद्धि र मासु उत्पादन",
            climateSuitabilityNe = "खोर व्यवस्थापन र पोसिलो घाँस भएका व्यावसायिक फार्ममा उपयुक्त",
            characteristicsNe = "खरी बाख्रामा नस्ल सुधार गरी छिटो शारीरिक वृद्धि लिन उपयोगी (प्राविधिक परामर्शमा छनोट गर्ने)।",
            sourceReference = VET_SOURCE,
            sortOrder = 2
        ),
        // Chicken
        AnimalBreed(
            id = "breed-chicken-local",
            animalId = "animal-chicken",
            breedNameNe = "स्थानीय (साकिनी / घाँटी खुइले)",
            breedNameEn = "Local Indigenous (Sakini)",
            originType = "indigenous",
            purposeNe = "अण्डा र स्वादिलो मासु",
            climateSuitabilityNe = "सबै ग्रामीण बस्तीमा सहज",
            characteristicsNe = "खुला र अर्ध-सघन प्रणालीमा पाल्न सकिने, रोग सहने र स्थानीय बजारमा उच्च माग।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalBreed(
            id = "breed-chicken-giriraja",
            animalId = "animal-chicken",
            breedNameNe = "गिरिराज / न्यूह्याम्पसायर",
            breedNameEn = "Giriraja / New Hampshire",
            originType = "improved",
            purposeNe = "दोहोरो प्रयोजन (अण्डा र मासु)",
            climateSuitabilityNe = "बकैयाको ग्रामीण अर्ध-सघन पालनमा अत्यन्त सफल",
            characteristicsNe = "स्थानीय कुखुराजस्तै देखिने तर छिटो बढ्ने र तुलनात्मक रूपमा धेरै अण्डा दिने।",
            sourceReference = VET_SOURCE,
            sortOrder = 2
        ),
        // Pig
        AnimalBreed(
            id = "breed-pig-hurra",
            animalId = "animal-pig",
            breedNameNe = "हुर्रा / पाँग्रे (स्थानीय)",
            breedNameEn = "Hurra / Indigenous Pig",
            originType = "indigenous",
            purposeNe = "मासु र कम खर्चिलो पालन",
            climateSuitabilityNe = "स्थानीय परिस्थितिमा रोग प्रतिरोधी",
            characteristicsNe = "सहनशील तर व्यावसायिक उत्पादनका लागि उन्नत नस्लसँग क्रस गरिन्छ।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalBreed(
            id = "breed-pig-yorkshire",
            animalId = "animal-pig",
            breedNameNe = "योर्कशायर / ल्यान्ड्रेस क्रस",
            breedNameEn = "Yorkshire / Landrace Crossbred",
            originType = "crossbred",
            purposeNe = "व्यावसायिक मासु उत्पादन",
            climateSuitabilityNe = "पक्की सफा खोर र सन्तुलित आहारा भएका फार्ममा उपयुक्त",
            characteristicsNe = "छिटो तौल बढ्ने, धेरै पाठापाठी जन्माउने र राम्रो मासु अनुपात।",
            sourceReference = VET_SOURCE,
            sortOrder = 2
        ),
        // Fish
        AnimalBreed(
            id = "breed-fish-rohu",
            animalId = "animal-fish",
            breedNameNe = "रहु (Rohu)",
            breedNameEn = "Labeo rohita",
            originType = "indigenous",
            purposeNe = "पोखरीको मध्य तहमा हुर्कने प्रमुख माछा",
            climateSuitabilityNe = "तराई तथा भित्री मधेसका न्यानो पोखरी",
            characteristicsNe = "मिश्रित कार्प खेतीको मुख्य प्रजाति; बजारमा उच्च उपभोक्ता माग।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalBreed(
            id = "breed-fish-silver-carp",
            animalId = "animal-fish",
            breedNameNe = "सिल्भर कार्प / कमन कार्प",
            breedNameEn = "Silver & Common Carp",
            originType = "improved",
            purposeNe = "माथिल्लो र तल्लो तहको आहार उपयोग",
            climateSuitabilityNe = "बकैयाको न्यानो उपत्यका पोखरीमा अनुकूल",
            characteristicsNe = "प्राकृतिक सूक्ष्म वनस्पति (Plankton) र पोखरीको तल्लो आहार खाएर छिटो बढ्छ।",
            sourceReference = VET_SOURCE,
            sortOrder = 2
        )
    )

    val animalHousing = listOf(
        AnimalHousingGuidance(
            id = "house-cattle",
            animalId = "animal-cattle",
            shedDesignNe = "गोठ पूर्व-पश्चिम वा दक्षिण-पूर्व मोहडामा बनाउने जसले बिहानको घाम छिर्न र दिउँसोको हुरी/पानी छल्न मद्दत गर्छ। भुइँ नचिप्लिने र पिसाब निकासका लागि हल्का भिरालो (१–२% ढलान) हुनुपर्छ।",
            floorSpaceStandardNe = "पशुलाई सजिलै उभिन, बस्न र फर्कन पुग्ने खुला ठाउँ (ठूलो पशु अनुसार प्राविधिक सल्लाहमा गोठ आकार मिलाउने)।",
            ventilationLightNe = "अमोनिया ग्याँस र ओस जम्न नदिन दुवैतर्फ हावा आवतजावत हुने झ्याल तथा प्रशस्त प्राकृतिक उज्यालोको व्यवस्था गर्ने।",
            beddingDrainageNe = "सुक्खा रबर म्याट वा सफा सुक्खा परालको सोत्तर प्रयोग गर्ने तथा मलमूत्र जम्मा हुने खाडल गोठभन्दा अलग राख्ने।",
            sanitationRoutineNe = "बिहान-बेलुका गोबर सफा गरी सफा पानीले भुइँ धुने र हप्तामा एकपटक सुरक्षित कीटाणुनाशक (प्राविधिक सल्लाहमा) प्रयोग गर्ने।",
            biosecurityNotesNe = "नयाँ किनेको गाईलाई कम्तीमा २–३ हप्ता अलग्गै (Quarantine) राखेर मात्र अन्य पशुसँग मिसाउने।",
            sourceReference = VET_SOURCE
        ),
        AnimalHousingGuidance(
            id = "house-buffalo",
            animalId = "animal-buffalo",
            shedDesignNe = "भैंसीका लागि बलियो काठ/पक्की खाँबो र गर्मीयाममा शीतल रहने छानो (जस्ता मुनि इन्सुलेसन वा टाली/खर) उपयुक्त हुन्छ।",
            floorSpaceStandardNe = "ब्याउने तथा दुहुनो भैंसीलाई छुट्टै आरामदायी थलो राख्ने।",
            ventilationLightNe = "खुला हावा चल्ने गोठ भैंसीको छालाको ताप नियन्त्रणका लागि महत्वपूर्ण छ।",
            beddingDrainageNe = "गोठको भुइँ रफ सिमेन्ट वा इँटाको राखी पिसाब बग्ने कुलेसो बनाउने।",
            sanitationRoutineNe = "दैनिक गोबर हटाउने, गोठ सुख्खा राख्ने र लामखुट्टे/झिँगा नियन्त्रणमा ध्यान दिने।",
            biosecurityNotesNe = "थुनेलो रोग रोकथामका लागि दूध दुहुने ठाउँ सफा राख्ने र बाहिरी व्यक्तिको अनावश्यक प्रवेश रोक्ने।",
            sourceReference = VET_SOURCE
        ),
        AnimalHousingGuidance(
            id = "house-goat",
            animalId = "animal-goat",
            shedDesignNe = "बाख्राका लागि जमिनभन्दा कम्तीमा ३–४ फिट माथि उठेको काठ/बाँसको स्लेट (ब्याटन) भुइँ भएको टाँडे खोर सर्वोत्तम हुन्छ।",
            floorSpaceStandardNe = "खोरमा उमेर र समूह (ब्याउने माउ, पाठापाठी, खसी) अनुसार अलग कोठा (Compartment) बनाउने।",
            ventilationLightNe = "भुइँको दरारबाट बड्कौला र पिसाब तल झर्ने र खोरमा ताजा हावा बहने बनाउने।",
            beddingDrainageNe = "भुइँ सुक्खा राख्दा खुर कुहिने र निमोनिया जस्ता समस्या घट्छन्।",
            sanitationRoutineNe = "खोर मुनिको मल नियमित सफा गर्ने र दाना-पानीको डुँड सधैं सफा राख्ने।",
            biosecurityNotesNe = "बाहिरबाट ल्याएको बाख्रालाई सिधै बथानमा नमिसाउने र खुट्टा सफा राख्ने।",
            sourceReference = VET_SOURCE
        ),
        AnimalHousingGuidance(
            id = "house-chicken",
            animalId = "animal-chicken",
            shedDesignNe = "मुसा, न्याउरी र वर्षाबाट सुरक्षित तारजालीसहितको पूर्व-पश्चिम मोहडा भएको खोर निर्माण गर्ने।",
            floorSpaceStandardNe = "भीडभाड नगरी खोर क्षमता अनुसार चल्ला र कुखुरा राख्ने।",
            ventilationLightNe = "सास फेर्न सजिलो होस् भनी माथिल्लो भाग खुला जाली र जाडो/वर्षामा पर्दा (Curtain) व्यवस्थापन गर्ने।",
            beddingDrainageNe = "सुक्खा भुस वा काठको धुलोको सोत्तर (Deep litter) प्रयोग गरी चिसो र ढुसी पर्न नदिने।",
            sanitationRoutineNe = "प्रत्येक ब्याच निस्केपछि खोर पूरै धोई चुन र कीटाणुनाशक छर्केर कम्तीमा २ हप्ता खाली (All-in All-out) राख्ने।",
            biosecurityNotesNe = "खोर प्रवेशद्वारमा खुट्टा डुबाउने (Footbath) राख्ने र जंगली चरा भित्र पस्न नदिने।",
            sourceReference = VET_SOURCE
        ),
        AnimalHousingGuidance(
            id = "house-pig",
            animalId = "animal-pig",
            shedDesignNe = "गन्ध र फोहोर नियन्त्रणका लागि बस्ती र खानेपानी मुहानभन्दा अलि टाढा पक्की भुइँ र पर्खाल भएको खोर बनाउने।",
            floorSpaceStandardNe = "माउ, पाठा र हुर्कने बंगुरलाई अलग खण्ड र कसरत गर्ने सानो खुला ठाउँ राख्ने।",
            ventilationLightNe = "राम्रो हावा र उज्यालो हुने गरी जाली वा झ्याल राख्ने।",
            beddingDrainageNe = "पानी सजिलै बग्ने पक्की ढलान र गोबर-पिसाब बायोग्याँस वा खाडलमा जाने नाली बनाउने।",
            sanitationRoutineNe = "दैनिक खोर पखाल्ने, तर जाडोयाममा पाठाहरू नचिसिउन् भनी सुक्खा परालको सोत्तर राख्ने।",
            biosecurityNotesNe = "भान्साको जुठो वा बाहिरी काँचो मासुजन्य फोहोर सिधै नखुवाई उमालेर वा सुरक्षित दाना मात्र दिने।",
            sourceReference = VET_SOURCE
        ),
        AnimalHousingGuidance(
            id = "house-fish",
            animalId = "animal-fish",
            shedDesignNe = "चिम्टाइलो र पानी अडिने माटोमा १.५ देखि २ मिटर गहिरो आयताकार पोखरी (बाढी पस्न नसक्ने र पानी निकाल्न मिल्ने डिलसहित) बनाउने।",
            floorSpaceStandardNe = "पोखरीको डिल बलियो र २:१ ढलानमा राख्ने।",
            ventilationLightNe = "पर्याप्त घाम पर्न दिन पोखरी वरिपरि ठूला रूखहरूको छायाँ नपार्ने (घुलनशील अक्सिजनका लागि महत्वपूर्ण)।",
            beddingDrainageNe = "पानी भित्र पस्ने र बाहिर निस्कने पाइपमा सानो जाली लगाई जङ्गली माछा वा माछा भाग्न रोक्ने।",
            sanitationRoutineNe = "बायो-प्ल्याङ्क्टन र पानीको रङ (हल्का हरियो) नियमित जाँच गर्ने।",
            biosecurityNotesNe = "नयाँ भुरा छोड्नुअघि पोखरी सफा गरी चुन र प्राङ्गारिक मलको तयारी (पशु/मत्स्य प्राविधिक सल्लाहमा) गर्ने।",
            sourceReference = VET_SOURCE
        )
    )

    val animalFeeding = listOf(
        AnimalFeedingGuidance(
            id = "feed-cattle",
            animalId = "animal-cattle",
            greenFodderForageNe = "नेपियर, सटारिया, मुलटो, मकै चरी, बदामे घाँस, तथा बडहर/इपिल जस्ता मौसमी हरियो घाँस र डालेघाँस नियमित खुवाउने।",
            dryRoughageNe = "धानको पराल, मकैको ढोड र कोदोको नल सानो टुक्रा पारेर (Chaffing) खुवाउँदा पाचन राम्रो हुन्छ र घाँस खेर जाँदैन।",
            concentrateFeedNe = "मकै, गहुँको चोकर, भटमास/तोरीको पिना र गेडागुडीको ढुटो मिसाइएको सन्तुलित दाना दुहुनो र गर्भिणी अवस्थामा आवश्यकता अनुसार दिने।",
            mineralSaltNe = "पशुका लागि खनिज मिश्रण (Mineral Mixture) र ढिके नुन/खनिज ब्लक नियमित उपलब्ध गराउने।",
            cleanWaterNe = "चिसो र सफा पिउने पानी गोठमा चौबिसै घण्टा उपलब्ध हुनुपर्छ।",
            rationQuantityNoteNe = "पशुको तौल, दूध उत्पादन र गर्भ अवस्था अनुसार दाना-घाँसको यकिन मात्रा स्थानीय पशु सेवा प्राविधिकसँग सल्लाह गरी निर्धारण गर्नुहोस्।",
            feedingFrequencyNe = "दिनमा ३–४ पटक हरियो/सुक्खा घाँस र दूध दुहुने समयमा सन्तुलित दाना।",
            sourceReference = VET_SOURCE
        ),
        AnimalFeedingGuidance(
            id = "feed-buffalo",
            animalId = "animal-buffalo",
            greenFodderForageNe = "मौसमी हरियो घाँस (नेपियर, जै, बरसिम, बडहर, खन्यु) सधैं सन्तुलित रूपमा दिने।",
            dryRoughageNe = "काटेको धानको पराल, गहुँको छ्वाली वा उपचारित पराल उपलब्ध गराउने।",
            concentrateFeedNe = "उच्च दूध उत्पादनका लागि ऊर्जा र प्रोटिनयुक्त (मकै, चोकर, पिना) सन्तुलित दाना दिने।",
            mineralSaltNe = "दूध ज्वरो र प्रजनन समस्या घटाउन नियमित खनिज तत्त्व र नुन खुवाउने।",
            cleanWaterNe = "भैंसीलाई दूध र शरीर शीतल राख्न पर्याप्त सफा पानी अनिवार्य हुन्छ।",
            rationQuantityNoteNe = "दानाको ठोस परिमाण पशुको शारीरिक तौल र दैनिक दूध उत्पादन हेरी पशु प्राविधिकको सल्लाहमा मिलाउनुहोस्।",
            feedingFrequencyNe = "नियमित समय तालिका अनुसार दिनमा ३ पटक घाँस र २ पटक दाना।",
            sourceReference = VET_SOURCE
        ),
        AnimalFeedingGuidance(
            id = "feed-goat",
            animalId = "animal-goat",
            greenFodderForageNe = "बाख्राले डालेघाँस (खन्यु, बडहर, काभ्रो, कुटमिरो, इपिल) र भुइँघाँस (स्टाइलो, किम्बु) असाध्यै मन पराउँछ। शीत ओभाएपछि मात्र चराउन वा घाँस दिनुपर्छ।",
            dryRoughageNe = "वर्षायाममा पखाला रोक्न केही सुक्खा घाँस, पराल वा हे (Hay) मिसाएर दिनु लाभदायक हुन्छ।",
            concentrateFeedNe = "पाठा हुर्काउने माउ, प्रजनन योग्य बोका र खसी बनाउँदा सन्तुलित दाना थप्न सकिन्छ।",
            mineralSaltNe = "खोरमा झुण्ड्याइने खनिज ब्लक र सफा नुन उपयोगी हुन्छ।",
            cleanWaterNe = "नदी वा आहालको फोहोर पानी नदिई डुँडमा सफा पानी राख्ने।",
            rationQuantityNoteNe = "हरियो शीत लागेको घाँस र एक्कासि धेरै अनाज/रोटी खुवाउँदा पेट फुल्ने (Bloat) जोखिम हुने हुँदा प्राविधिक सल्लाहमा आहारा तालिका अपनाउनुहोस्।",
            feedingFrequencyNe = "बिहान र बेलुका डुँडमा ताजा घाँस टाँगेर (जमिनमा नझर्ने गरी) खुवाउने।",
            sourceReference = VET_SOURCE
        ),
        AnimalFeedingGuidance(
            id = "feed-chicken",
            animalId = "animal-chicken",
            greenFodderForageNe = "अर्ध-सघन लोकल/गिरिराज कुखुरालाई हरियो सागपात, बर्सिम र कीरा-फट्याङ्ग्रा चर्ने व्यवस्था गर्न सकिन्छ।",
            dryRoughageNe = "कुनै खस्रो पराल आवश्यक नपर्ने; पाचनका लागि सफा साना गिट्टी (Grit) उपयोगी।",
            concentrateFeedNe = "चल्ला (Starter), हुर्कने (Grower) र अण्डा/मासु (Layer/Finisher) चरण अनुसार ढुसी नलागेको गुणस्तरीय दाना प्रयोग गर्ने।",
            mineralSaltNe = "अण्डाको बोक्रा बलियो बनाउन क्याल्सियम र भिटामिन खनिज (प्राविधिक सल्लाहमा)।",
            cleanWaterNe = "भाँडा (Drinker) दैनिक सफा गरी ताजा र सफा पानी सधैं उपलब्ध राख्ने।",
            rationQuantityNoteNe = "कुखुराको जात र उमेर हप्ता अनुसार दानाको प्रकार र परिमाण स्थानीय भेटेरिनरी/फिड विज्ञको सल्लाहमा मिलाउनुहोस्।",
            feedingFrequencyNe = "चल्ला र व्यावसायिक कुखुरालाई नियमित ताजा दाना र घरायसीलाई बिहान-बेलुका।",
            sourceReference = VET_SOURCE
        ),
        AnimalFeedingGuidance(
            id = "feed-pig",
            animalId = "animal-pig",
            greenFodderForageNe = "कर्कलो, सखरखण्डको पात, बन्दाका पात र मुलाको पात सफा गरी खुवाउन सकिन्छ।",
            dryRoughageNe = "बंगुर एक-आमाशयिक (Monogastric) प्राणी भएकाले कडा पराल पचाउन सक्दैन; कोमल हरियो र अन्नमा आधारित दाना उपयुक्त हुन्छ।",
            concentrateFeedNe = "मकै, ढुटो, चोकर, भटमास पिना तथा सुरक्षित पकाइएको कृषि उप-उत्पादन प्रयोग गर्ने।",
            mineralSaltNe = "खनिज मिश्रण र नुन दानामा सन्तुलित राख्ने (धेरै नुन विषाक्त हुन सक्छ)।",
            cleanWaterNe = "ताजा र सफा पिउने पानी सधैं उपलब्ध राख्ने।",
            rationQuantityNoteNe = "गर्भिणी माउ र बढ्दो पाठाका लागि प्रोटिन र ऊर्जा सन्तुलन पशु प्राविधिकसँग सल्लाह गरी निर्धारण गर्नुहोस्।",
            feedingFrequencyNe = "दिनमा २ देखि ३ पटक निश्चित समयमा।",
            sourceReference = VET_SOURCE
        ),
        AnimalFeedingGuidance(
            id = "feed-fish",
            animalId = "animal-fish",
            greenFodderForageNe = "ग्रास कार्प माछाले नेपियर, केराको पात, बर्सीम वा कोमल हरियो घाँस खान्छ।",
            dryRoughageNe = "लागू नहुने।",
            concentrateFeedNe = "धानको ढुटो (Rice bran) र तोरीको पिना बराबर अनुपातमा भिजाएर वा पेलेट दाना ख्वाउने ठाउँ (Feeding bag/tray) मा दिने।",
            mineralSaltNe = "पोखरीको प्राकृतिक आहार बढाउन प्राङ्गारिक र रासायनिक मल व्यवस्थापन (मत्स्य प्राविधिकको सल्लाहमा)।",
            cleanWaterNe = "पोखरीमा स्वच्छ, अक्सिजनयुक्त पानी (pH ६.५–८.५) कायम राख्ने।",
            rationQuantityNoteNe = "अत्यधिक दाना हाल्दा पानी कुहिने र माछा मर्ने भएकाले माछाको तौल अनुपात मत्स्य प्राविधिकको सल्लाहमा तय गर्नुहोस्।",
            feedingFrequencyNe = "दैनिक बिहान घाम लागेपछि निश्चित एकै स्थानमा।",
            sourceReference = VET_SOURCE
        )
    )

    val animalBreeding = listOf(
        AnimalBreedingGuidance(
            id = "breed-guide-cattle",
            animalId = "animal-cattle",
            breedingAgeWeightNe = "जात र शारीरिक विकास अनुसार उचित उमेर र तौल पुगेपछि मात्र प्रजनन गराउने।",
            estrusHeatSignsNe = "कराउने, अन्य गाईमाथि चढ्न खोज्ने, योनीबाट सफा तन्किने सिँगान (Mucus) बग्ने र दूध घट्ने।",
            matingAiGuidanceNe = "बाली गएको १२–१८ घण्टाभित्र नजिकको पशु सेवा प्राविधिकबाट कृत्रिम गर्भाधान (AI) वा स्वस्थ साँढेबाट प्रजनन गराउने।",
            pregnancyIncubationPeriodNe = "औसत करिब ९ महिना (२८० दिन आसपास)।",
            pregnantCareNe = "अन्तिम २–३ महिनामा दूध सुकाउने (Dry period), पौष्टिक आहारा र चिप्लो भुइँबाट जोगाउने।",
            birthingHatchingCareNe = "सफा, सुक्खा र न्यानो सुत्केरी थलो तयार राख्ने; गाह्रो भए तुरुन्त पशु चिकित्सक बोलाउने।",
            newbornChickFryCareNe = "बाच्छा-बाच्छी जन्मेको १–२ घण्टाभित्र आमाको बिगौती दूध (Colostrum) अनिवार्य खुवाउने।",
            weaningBroodingNe = "क्रमशः दाना र कोमल घाँस सिकाउँदै सुरक्षित हुर्काउने।",
            sourceReference = VET_SOURCE
        ),
        AnimalBreedingGuidance(
            id = "breed-guide-goat",
            animalId = "animal-goat",
            breedingAgeWeightNe = "खरी बाख्रा सामान्यतया ८–१० महिनामा (उचित तौल पुगेपछि) प्रजनन योग्य हुन्छन्।",
            estrusHeatSignsNe = "पुच्छर हल्लाइरहने, पटक–पटक कराउने, योनी रातो र ओसिलो हुने तथा छटपटाउने।",
            matingAiGuidanceNe = "अन्तःप्रजनन (Inbreeding) रोक्न नाता नपर्ने स्वस्थ र प्रमाणित उन्नत बोका वा कृत्रिम गर्भाधान प्रयोग गर्ने।",
            pregnancyIncubationPeriodNe = "औसत करिब ५ महिना (१४५–१५० दिन)।",
            pregnantCareNe = "गर्भवती बाख्रालाई अन्य बथानबाट लडाइँ नहोस् भनी अलग्गै र सन्तुलित आहारामा राख्ने।",
            birthingHatchingCareNe = "ब्याउने बेला सफा सुक्खा सोत्तर राख्ने र साल अड्किएमा प्राविधिकको सहयोग लिने।",
            newbornChickFryCareNe = "पाठापाठीको नाक-मुखको झिल्ली सफा गरी १ घण्टाभित्र बिगौती दूध खुवाउने र न्यानो राख्ने।",
            weaningBroodingNe = "३–४ महिनापछि क्रमशः दूध छुटाउने।",
            sourceReference = VET_SOURCE
        )
    )

    val animalHealth = listOf(
        AnimalHealthGuidance(
            id = "health-cattle",
            animalId = "animal-cattle",
            dailyHygieneNe = "गोठ सफा र सुक्खा राख्ने, दूध दुहुनुअघि हात र थुन मनतातो सफा पानीले धुने।",
            biosecurityIsolationNe = "बिरामी पशुलाई तुरुन्त स्वस्थ पशुबाट अलग्गै राख्ने र अन्य फार्मको जुत्ता-चप्पल सिधै गोठमा नल्याउने।",
            vaccinationAwarenessNe = "खोरेत (FMD), भ्यागुते (HS), चर्चरे (BQ) जस्ता संक्रामक रोगविरुद्ध गाउँपालिका/पशु सेवा कार्यालयको अभियान अनुसार खोप लगाउने।",
            dewormingParasiteControlNe = "नामले, गोलो जुका र बाह्य परजीवी (किर्ना/जुम्रा) नियन्त्रणका लागि पशु प्राविधिकको सल्लाहमा नियमित परीक्षण र उपचार गर्ने।",
            warningSignsNe = "उच्च ज्वरो, खान-उग्राउन छोड्नु, मुखबाट र्‍याल चुहिनु, खुट्टा खोच्याउनु, थुन सुन्निनु वा एक्कासि दूध घट्नु।",
            vetReferralGuidanceNe = "कुनै पनि लक्षण देखिएमा आफैं जथाभावी औषधि नदिई तुरुन्त नजिकको पशु सेवा प्राविधिक वा भेटेरिनरी डाक्टरसँग जाँच गराउनुहोस्।",
            sourceReference = VET_SOURCE
        ),
        AnimalHealthGuidance(
            id = "health-buffalo",
            animalId = "animal-buffalo",
            dailyHygieneNe = "थुनेलो र खुरको संक्रमण रोक्न गोठको ओसिलोपन घटाउने र थुन सफा राख्ने।",
            biosecurityIsolationNe = "नयाँ भैंसीलाई अलग्गै निरीक्षणमा राख्ने।",
            vaccinationAwarenessNe = "खोरेत (FMD), भ्यागुते (HS) लगायत स्थानीय पशु सेवा केन्द्रले तोकेको खोप कार्यक्रममा सहभागी हुने।",
            dewormingParasiteControlNe = "आहाल वा सिमसार क्षेत्रको घाँस खाँदा नामले जुका लाग्ने जोखिम बढी हुने हुँदा प्राविधिक परामर्शमा जुका जाँच/औषधि गर्ने।",
            warningSignsNe = "कहाली लाग्ने पखाला, सास फेर्न गाह्रो, मुख-खुरमा घाउ वा थुन कडा हुनु।",
            vetReferralGuidanceNe = "तुरुन्त पशु सेवा प्राविधिकलाई खबर गर्नुहोस् (औषधि मात्रा डाक्टरको सल्लाहबिना नदिनुहोस्)।",
            sourceReference = VET_SOURCE
        ),
        AnimalHealthGuidance(
            id = "health-goat",
            animalId = "animal-goat",
            dailyHygieneNe = "टाँडे खोर सफा र सुक्खा राख्ने; वर्षामा खुर सफा गरी नङ्ग्रा काट्ने (Hoof trimming)।",
            biosecurityIsolationNe = "बजारबाट ल्याएका वा बिरामी बाख्रालाई ३ हप्ता अलग खोरमा राख्ने।",
            vaccinationAwarenessNe = "पी.पी.आर. (PPR - बाख्राको महामारी) र अन्य आवश्यक खोप पशु सेवा कार्यालय मार्फत समयमै लगाउने।",
            dewormingParasiteControlNe = "आँखाको तल्लो परेला पहेँलो/सेतो देखिनु र पेट फुल्नु जुकाको संकेत हुन सक्छ; गोबर जाँच वा प्राविधिक सल्लाहमा जुका नियन्त्रण गर्ने।",
            warningSignsNe = "लगातार सिँगान बग्नु, खोकी, पखाला, ज्वरो, पेट फुल्नु वा झुस्स भएर बस्नु।",
            vetReferralGuidanceNe = "पी.पी.आर. र निमोनिया छिटो घातक हुने भएकाले लक्षण देख्नेबित्तिकै पशु सेवा प्राविधिक बोलाउनुहोस्।",
            sourceReference = VET_SOURCE
        ),
        AnimalHealthGuidance(
            id = "health-chicken",
            animalId = "animal-chicken",
            dailyHygieneNe = "दाना-पानीका भाँडा दैनिक पखाल्ने र खोरको सोत्तर सुक्खा/झुरो राख्ने।",
            biosecurityIsolationNe = "अनावश्यक व्यक्ति र बाहिरी गाडी/क्रेट खोर नजिक नल्याउने।",
            vaccinationAwarenessNe = "रानीखेत (Newcastle Disease), गम्बोरो (IBD) आदि विरुद्ध भेटेरिनरी डाक्टरको मार्गदर्शन अनुसार खोप तालिका अपनाउने।",
            dewormingParasiteControlNe = "खोरमा जुम्रा, उपियाँ र कोक्सिडिओसिस (रगत मिसिएको पखाला) बाट जोगाउन सरसफाइ कायम राख्ने।",
            warningSignsNe = "झोक्राउने, पखेटा लत्रिने, हरियो/सेतो/रगत मिश्रित सुली, स्वाँस्वाँ गर्ने वा अकस्मात मृत्युदर बढ्ने।",
            vetReferralGuidanceNe = "एक्कासि कुखुरा मर्न थालेमा तुरुन्त बर्ड फ्लु वा रानीखेत जाँचका लागि नजिकको भेटेरिनरी कार्यालयमा सम्पर्क गर्नुहोस्।",
            sourceReference = VET_SOURCE
        ),
        AnimalHealthGuidance(
            id = "health-pig",
            animalId = "animal-pig",
            dailyHygieneNe = "खोर सफा गर्ने र खानेकुरा ढुसीरहित दिने।",
            biosecurityIsolationNe = "खोरमा जैविक सुरक्षा कडा राख्ने; बाहिरी फोहोर वा काँचो मासुका अवशेष नदिने (स्वाइन फिवर रोकथामको लागि)।",
            vaccinationAwarenessNe = "क्लासिकल स्वाइन फिवर (CSF) लगायतका खोपका लागि स्थानीय पशु सेवा शाखामा सम्पर्क राख्ने।",
            dewormingParasiteControlNe = "जुका र लुतो/छाला रोग नियन्त्रणका लागि खोर सफा राखी प्राविधिक सल्लाहमा उपचार गर्ने।",
            warningSignsNe = "उच्च ज्वरो, कान/छालामा रातो-नीलो दाग, खाना छोड्नु वा श्वासप्रश्वास समस्या।",
            vetReferralGuidanceNe = "स्वाइन फिवर अति संक्रामक हुने भएकाले शंकास्पद लक्षणमा तुरुन्त भेटेरिनरी चिकित्सकलाई देखाउनुहोस्।",
            sourceReference = VET_SOURCE
        ),
        AnimalHealthGuidance(
            id = "health-fish",
            animalId = "animal-fish",
            dailyHygieneNe = "सडेगलेको दाना वा अत्यधिक मलखाद नहाल्ने र पानीको गुणस्तर (रङ, गन्ध) जाँचिरहने।",
            biosecurityIsolationNe = "रोग लागेको पोखरीको जाल नधोई अर्को पोखरीमा प्रयोग नगर्ने।",
            vaccinationAwarenessNe = "नेपालमा सामान्य कार्प पालनमा खोपभन्दा पोखरी तयारी, चुन प्रयोग र गुणस्तरीय भुरा छनोट मुख्य रोकथाम हो।",
            dewormingParasiteControlNe = "माछाको कत्ला वा पखेटामा परजीवी/घाउ देखिएमा मत्स्य प्राविधिकबाट पानी र माछा परीक्षण गराउने।",
            warningSignsNe = "बिहान सबेरै माछा पानीको सतहमा आएर मुख बाउनु (अक्सिजन कमी), शरीरमा रातो घाउ देखिनु (EUS) वा पानी गन्हाउनु।",
            vetReferralGuidanceNe = "अक्सिजन घटेमा तुरुन्त ताजा पानी पठाउने र माछा रोग देखिएमा मत्स्य विज्ञ/प्राविधिकसँग सल्लाह लिनुहोस्।",
            sourceReference = VET_SOURCE
        )
    )

    val animalDiseases = listOf(
        AnimalDisease(
            id = "dis-cattle-fmd",
            animalId = "animal-cattle",
            nameNe = "खोरेत रोग (FMD)",
            nameEn = "Foot-and-Mouth Disease",
            pathogenType = "viral",
            symptomsNe = "उच्च ज्वरो आउने, मुख र जिब्रो तथा खुरको कापमा फोका र घाउ हुने, अत्यधिक र्‍याल चुहाउने र लङ्गडो हुने।",
            preventionBiosecurityNe = "पशु सेवा कार्यालयको तालिका अनुसार नियमित खोरेत खोप लगाउने र संक्रमित क्षेत्रबाट पशु ओसारपसार नगर्ने।",
            immediateFarmerActionNe = "बिरामी गाईलाई तुरुन्त अलग्गै राख्ने, कोमल घाँस/खोले दिने र खुर-मुख सफा राख्ने।",
            veterinaryCautionNe = "जीवाणु संक्रमण रोक्न र उचित उपचारका लागि तुरुन्त पशु चिकित्सक वा प्राविधिकको जाँच गराउनुहोस्।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalDisease(
            id = "dis-buffalo-mastitis",
            animalId = "animal-buffalo",
            nameNe = "थुनेलो (Mastitis)",
            nameEn = "Mastitis",
            pathogenType = "bacterial",
            symptomsNe = "कल्चौडो रातो भई सुन्निने, छुँदा दुख्ने, दूधमा छोकरा, रगत वा पीप जस्तो देखिने र दूध घट्ने।",
            preventionBiosecurityNe = "सफा गोठ, दूध दुहुनुअघि हात/थुन धुने र दुहिसकेपछि सफा र सुरक्षित राख्ने।",
            immediateFarmerActionNe = "सफा हातले संक्रमित थुनको दूध अलग भाँडामा दोहेर नष्ट गर्ने र थुनमा चिसो/तातो सेकाइ बारे प्राविधिक सल्लाह लिने।",
            veterinaryCautionNe = "आफैं जथाभावी एन्टिबायोटिक नचलाई पशु चिकित्सकबाट जाँच गराई मात्र उपचार सुरु गर्नुहोस्।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalDisease(
            id = "dis-goat-ppr",
            animalId = "animal-goat",
            nameNe = "पी.पी.आर. / बाख्राको हैजा (PPR)",
            nameEn = "Peste des Petits Ruminants",
            pathogenType = "viral",
            symptomsNe = "तीव्र ज्वरो, आँखाबाट कचेरा र नाकबाट बाक्लो सिँगान बग्ने, मुखभित्र गिजामा घाउ र गन्हाउने पखाला।",
            preventionBiosecurityNe = "प्रत्येक बाख्रालाई पी.पी.आर. खोप लगाउने र नयाँ बाख्रा ल्याउँदा क्वारेन्टाइनमा राख्ने।",
            immediateFarmerActionNe = "बिरामी बाख्रा तुरुन्त अलग्गै राखी सफा पानी र झोलिलो खानेकुरा दिने।",
            veterinaryCautionNe = "यो अति संक्रामक र प्राणघातक भाइरल रोग हो; शंका लाग्नेबित्तिकै पशु सेवा कार्यालयमा खबर गर्नुहोस्।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalDisease(
            id = "dis-chicken-newcastle",
            animalId = "animal-chicken",
            nameNe = "रानीखेत रोग (Newcastle Disease)",
            nameEn = "Newcastle Disease",
            pathogenType = "viral",
            symptomsNe = "झोक्राउने, हरियो/पहेँलो पखाला, श्वास फेर्न गाह्रो, घाँटी बटारिने (Torticollis) र अचानक उच्च मृत्युदर।",
            preventionBiosecurityNe = "समयमै खोप लगाउने, खोरमा बाहिरी मान्छे र चराचुरुङ्गी छिर्न नदिने (कडा जैविक सुरक्षा)।",
            immediateFarmerActionNe = "मरेका कुखुरालाई गहिरो खाल्डोमा चुन हालेर सुरक्षित पुर्ने र खोर सफा गर्ने।",
            veterinaryCautionNe = "भाइरल रोग भएकाले औषधिले निको हुँदैन; तुरुन्त भेटेरिनरी जाँच र अन्य बथान जोगाउन सल्लाह लिनुहोस्।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalDisease(
            id = "dis-pig-csf",
            animalId = "animal-pig",
            nameNe = "स्वाइन फिवर (CSF / ASF सचेतना)",
            nameEn = "Swine Fever",
            pathogenType = "viral",
            symptomsNe = "उच्च ज्वरो, झुण्डिएर बस्ने, खाना नखाने, पेट र कानमा रगतको नीलो/बैजनी दाग देखिने।",
            preventionBiosecurityNe = "नियमित खोप (CSF) र कडा जैविक सुरक्षा (जुठो उमाल्ने, खोर निसंक्रमण गर्ने)।",
            immediateFarmerActionNe = "बिरामी बंगुरलाई तत्काल अलग राख्ने र अन्य किसानको खोरमा आवतजावत बन्द गर्ने।",
            veterinaryCautionNe = "अत्यधिक संक्रामक रोग भएकाले तुरुन्त पशु सेवा शाखा र भेटेरिनरी अस्पतालमा रिपोर्ट गर्नुहोस्।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        ),
        AnimalDisease(
            id = "dis-fish-eus",
            animalId = "animal-fish",
            nameNe = "शरीर कुहिने / रातो घाउ रोग (EUS)",
            nameEn = "Epizootic Ulcerative Syndrome",
            pathogenType = "fungal/bacterial",
            symptomsNe = "माछाको शरीरमा रातो दाग वा गहिरो घाउ हुनु, सुस्त तैरिनु र जाडो मौसममा बढी देखिनु।",
            preventionBiosecurityNe = "पोखरीमा चुनको उचित व्यवस्थापन, सफा पानी र बाढी/बाहिरी माछा प्रवेश नियन्त्रण।",
            immediateFarmerActionNe = "घाउ लागेका मरेका माछा पोखरीबाट तुरुन्त बाहिर निकाल्ने।",
            veterinaryCautionNe = "पोखरीको पानी तथा माछा परीक्षण गरी मत्स्य प्राविधिकको सिफारिस अनुसार मात्र चुन वा उपचार प्रयोग गर्नुहोस्।",
            sourceReference = VET_SOURCE,
            sortOrder = 1
        )
    )

    val animalProduction = listOf(
        AnimalProductionGuidance(
            id = "prod-cattle",
            animalId = "animal-cattle",
            primaryProductsNe = "ताजा दूध, दही/घिउ, प्राङ्गारिक गोबर मल",
            cleanProductionPracticeNe = "सफा दूध उत्पादन (Clean Milk Production): स्टिलको सफा बाल्टी प्रयोग गर्ने, हात र थुन धुने र थुनेलो भएको दूध अलग राख्ने।",
            postHarvestHandlingNe = "दूध दोहेको २ घण्टाभित्र चिलिङ सेन्टर वा सहकारीमा पुर्‍याउने वा उमाल्ने।",
            manureWasteManagementNe = "गोबरलाई कम्पोस्ट खाडल वा बायोग्याँस प्लाण्टमा हाली उच्च गुणस्तरीय प्राङ्गारिक मल बनाउने।",
            marketingNotesNe = "स्थानीय दुग्ध सहकारी तथा हेटौंडा बजार।",
            sourceReference = VET_SOURCE
        ),
        AnimalProductionGuidance(
            id = "prod-goat",
            animalId = "animal-goat",
            primaryProductsNe = "खसी/बोका मासु, बड्कौला मल",
            cleanProductionPracticeNe = "नस्ल अनुसार उचित उमेर र तौल पुगेपछि बिक्री योग्य खसी-बोका तयार गर्ने।",
            postHarvestHandlingNe = "पशु ढुवानी गर्दा यातना नहुने, हावा र पानीको सुविधासहित सुरक्षित ढुवानी गर्ने।",
            manureWasteManagementNe = "बाख्राको बड्कौला तरकारी तथा फलफूल बालीका लागि उच्च पोषक प्राङ्गारिक मल हो।",
            marketingNotesNe = "स्थानीय कृषि सहकारी, हाटबजार र दशैं/चाडपर्व लक्षित बजार।",
            sourceReference = VET_SOURCE
        )
    )

    val animalImages = listOf(
        AnimalImage(
            id = "aimg-cattle-1",
            animalId = "animal-cattle",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Cow_female_black_white.jpg/640px-Cow_female_black_white.jpg",
            captionNe = "गाईपालन सामान्य पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "breed",
            licenseType = "CC BY-SA",
            sortOrder = 1
        ),
        AnimalImage(
            id = "aimg-buffalo-1",
            animalId = "animal-buffalo",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Water_buffalo_at_India.jpg/640px-Water_buffalo_at_India.jpg",
            captionNe = "भैंसीपालन सामान्य पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "breed",
            licenseType = "CC BY-SA",
            sortOrder = 1
        ),
        AnimalImage(
            id = "aimg-goat-1",
            animalId = "animal-goat",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Hausziege_04.jpg/640px-Hausziege_04.jpg",
            captionNe = "बाख्रापालन सामान्य पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "breed",
            licenseType = "CC BY-SA",
            sortOrder = 1
        ),
        AnimalImage(
            id = "aimg-chicken-1",
            animalId = "animal-chicken",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Female_pair.jpg/640px-Female_pair.jpg",
            captionNe = "कुखुरापालन सामान्य पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "breed",
            licenseType = "CC BY-SA",
            sortOrder = 1
        ),
        AnimalImage(
            id = "aimg-pig-1",
            animalId = "animal-pig",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Sow_and_five_piglets.jpg/640px-Sow_and_five_piglets.jpg",
            captionNe = "बंगुर तथा पाठापाठी पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "breed",
            licenseType = "CC BY-SA",
            sortOrder = 1
        ),
        AnimalImage(
            id = "aimg-fish-1",
            animalId = "animal-fish",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f4/Catla_catla.jpg/640px-Catla_catla.jpg",
            captionNe = "कार्प प्रजाति माछा पहिचान तस्बिर (स्रोत: Wikimedia Commons)",
            stageCategory = "breed",
            licenseType = "CC BY-SA",
            sortOrder = 1
        )
    )

    // Videos tagged by animalId -> mapped as EducationalVideo for playback
    val animalVideos: Map<String, List<EducationalVideo>> = mapOf(
        "animal-goat" to listOf(
            EducationalVideo(
                id = "vid-livestock-goat-aitc",
                cropId = "animal-goat",
                topicId = 6,
                titleNe = "व्यावसायिक बाख्रापालन तथा आधुनिक खोर व्यवस्थापन",
                descriptionNe = "बाख्राको टाँडे खोर, सरसफाइ र स्वास्थ्य हेरचाह सम्बन्धी शैक्षिक जानकारी।",
                channelName = "कृषि सूचना तथा प्रशिक्षण केन्द्र II AITC NEPAL",
                thumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Hausziege_04.jpg/640px-Hausziege_04.jpg",
                videoUrl = "https://www.youtube.com/results?search_query=AITC+Nepal+bakhra+palan",
                duration = null,
                provider = "youtube",
                downloadPermission = false,
                downloadableFileUrl = null,
                localFilePath = null,
                fileSizeBytes = null,
                isDownloaded = false,
                isSampleMedia = false
            )
        )
    )
}
