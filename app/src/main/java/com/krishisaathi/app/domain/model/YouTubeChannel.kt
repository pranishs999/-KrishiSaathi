package com.krishisaathi.app.domain.model

/**
 * Verified Nepali agricultural YouTube channels for the
 * "कृषि प्रविधि युट्यूब च्यानलहरू" directory.
 *
 * Channel handles and names are verified from YouTube.
 * No subscriber counts or other metrics are invented.
 */
data class YouTubeChannel(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val handle: String,
    val descriptionNe: String,
    val channelUrl: String,
    val isVerified: Boolean = true
)

object VerifiedYouTubeChannels {
    val channels = listOf(
        YouTubeChannel(
            id = "ch-aitc",
            nameNe = "कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal)",
            nameEn = "AITC Nepal",
            handle = "@iiaitcnepal2768",
            descriptionNe = "नेपाल सरकारको कृषि सूचना तथा प्रशिक्षण केन्द्रको आधिकारिक युट्युब च्यानल। बाली खेती प्रविधि, मौसमी सल्लाह र कृषि तालिम।",
            channelUrl = "https://www.youtube.com/@iiaitcnepal2768"
        ),
        YouTubeChannel(
            id = "ch-krishitv",
            nameNe = "कृषि टेलिभिजन एचडी",
            nameEn = "Krishi Television HD",
            handle = "@KrishiTelevisionHD1",
            descriptionNe = "नेपालको कृषि टेलिभिजन — बाली खेती प्रविधि, पशुपालन, मत्स्यपालन र कृषि समाचार प्रस्तुत गर्ने राष्ट्रिय च्यानल।",
            channelUrl = "https://www.youtube.com/@KrishiTelevisionHD1"
        ),
        YouTubeChannel(
            id = "ch-ntv-narc",
            nameNe = "NTV NEWS NEPAL (कृषि NARC कार्यक्रम)",
            nameEn = "NTV NEWS NEPAL",
            handle = "@ntvnewsnepal",
            descriptionNe = "नेपाल टेलिभिजनको कृषि अनुसन्धान परिषद् (NARC) सम्बन्धी कृषि कार्यक्रम र विज्ञ सल्लाह।",
            channelUrl = "https://www.youtube.com/@ntvnewsnepal"
        ),
        YouTubeChannel(
            id = "ch-patrika",
            nameNe = "कृषि पत्रिका",
            nameEn = "Krishi Patrika",
            handle = "@KrishiPatrika",
            descriptionNe = "कृषि पत्रिकाद्वारा बाली खेती, पशुपालन र कृषि प्रविधिसम्बन्धी शैक्षिक भिडियो।",
            channelUrl = "https://www.youtube.com/@KrishiPatrika"
        ),
        YouTubeChannel(
            id = "ch-labtv",
            nameNe = "LAB TV NEPAL",
            nameEn = "LAB TV Nepal",
            handle = "@LABTVNEPAL",
            descriptionNe = "ग्रामीण जीवनशैली, कृषि अभ्यास र नेपाली किसानहरूको कथा प्रस्तुत गर्ने च्यानल।",
            channelUrl = "https://www.youtube.com/@LABTVNEPAL"
        ),
        YouTubeChannel(
            id = "ch-agrochiran",
            nameNe = "एग्रो चिरान",
            nameEn = "Agro Chiran",
            handle = "@AgroChiran",
            descriptionNe = "बाली रोग व्यवस्थापन, झारपात नियन्त्रण र कृषि प्रविधिसम्बन्धी व्यावहारिक भिडियोहरू।",
            channelUrl = "https://www.youtube.com/@AgroChiran"
        ),
        YouTubeChannel(
            id = "ch-krishi-loksewa",
            nameNe = "कृषि लोकसेवा तयारी",
            nameEn = "Krishi Loksewa",
            handle = "@krishiloksewa",
            descriptionNe = "कृषि लोकसेवा तयारी र बाली खेती प्रविधि सम्बन्धी जानकारीमूलक शैक्षिक भिडियोहरू।",
            channelUrl = "https://www.youtube.com/@krishiloksewa"
        )
    )
}
