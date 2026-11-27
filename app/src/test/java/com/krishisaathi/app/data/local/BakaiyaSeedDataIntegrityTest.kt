package com.krishisaathi.app.data.local

import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.data.remote.SupabaseCropCategoryDto
import com.krishisaathi.app.data.remote.SupabaseCropDto
import com.krishisaathi.app.data.remote.SupabaseEducationalVideoDto
import com.krishisaathi.app.data.remote.SupabaseHelplineDto
import com.krishisaathi.app.domain.model.NepalEcologicalZone
import com.krishisaathi.app.domain.model.VerifiedYouTubeChannels
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BakaiyaSeedDataIntegrityTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun supabaseCropAndCategoryDtosDecodeWithOptionalFields() {
        val categoriesJson = """
            [
              {"id":"cat-cereal","name_ne":"खाद्यान्न बाली","name_en":"Cereals","sort_order":1,"thumbnail_url":"https://example.org/cereal.jpg"}
            ]
        """.trimIndent()
        val cropsJson = """
            [
              {
                "id":"crop-paddy",
                "category_id":"cat-cereal",
                "name_ne":"धान",
                "name_en":"Paddy",
                "scientific_name":"Oryza sativa",
                "description_ne":"बकैयाको मुख्य बाली",
                "suitable_zones_csv":"TERAI,INNER_TERAI,MID_HILL",
                "source_reference":"NARC",
                "is_published":true,
                "is_verified_content":true
              }
            ]
        """.trimIndent()

        val categories = json.decodeFromString<List<SupabaseCropCategoryDto>>(categoriesJson)
        val crops = json.decodeFromString<List<SupabaseCropDto>>(cropsJson)

        assertEquals(1, categories.size)
        assertEquals("cat-cereal", categories.first().id)
        assertEquals(1, crops.size)
        assertEquals("धान", crops.first().nameNe)
        assertTrue(NepalEcologicalZone.parseList(crops.first().suitableZonesCsv).size == 3)
    }

    @Test
    fun supabaseVideoAndHelplineDtosEnforceValidationRules() {
        val youtubeDto = SupabaseEducationalVideoDto(
            id = "vid-1",
            cropId = "crop-paddy",
            topicId = 1,
            titleNe = "धान खेती",
            descriptionNe = "शैक्षिक भिडियो",
            channelName = "AITC Nepal",
            thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
            videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            provider = "youtube",
            downloadPermission = false,
            downloadableFileUrl = null
        )
        assertFalse(youtubeDto.downloadPermission)
        assertTrue(ValidationUtils.isValidYouTubeUrl(youtubeDto.videoUrl))

        val helplineDto = SupabaseHelplineDto(
            id = "help-1",
            nameNe = "कृषि शाखा बकैया",
            designationNe = "कृषि अधिकृत",
            wardNe = "गाउँपालिका कार्यालय",
            wardNumber = 4,
            mobileNumber = "9855000001",
            isVerified = true
        )
        assertTrue(ValidationUtils.isValidNepalPhoneNumber(helplineDto.mobileNumber))
    }

    @Test
    fun verifiedYouTubeChannelsHaveValidUrlsAndHandles() {
        assertTrue(VerifiedYouTubeChannels.channels.isNotEmpty())
        VerifiedYouTubeChannels.channels.forEach { channel ->
            assertTrue("Channel ${channel.id} name blank", channel.nameNe.isNotBlank())
            assertTrue("Channel ${channel.id} handle invalid", channel.handle.startsWith("@"))
            assertTrue("Channel ${channel.id} URL invalid", ValidationUtils.isValidYouTubeChannelUrl(channel.channelUrl))
            assertTrue("Channel ${channel.id} must be verified", channel.isVerified)
        }
    }
}
