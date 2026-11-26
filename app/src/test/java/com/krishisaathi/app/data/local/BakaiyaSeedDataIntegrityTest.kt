package com.krishisaathi.app.data.local

import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.domain.model.NepalEcologicalZone
import com.krishisaathi.app.domain.model.VerifiedYouTubeChannels
import com.krishisaathi.app.domain.model.VideoTopic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BakaiyaSeedDataIntegrityTest {

    @Test
    fun allSixNepaliCropCategoriesAreDefined() {
        val expectedNepaliCategories = setOf(
            "खाद्यान्न बाली",
            "तरकारी बाली",
            "फलफूल बाली",
            "दलहन बाली",
            "तेलहन बाली",
            "मसला तथा नगदे बाली"
        )
        val actualNames = BakaiyaSeedData.categories.map { it.nameNe }.toSet()
        assertEquals(expectedNepaliCategories, actualNames)
    }

    @Test
    fun cropsMapToValidCategoriesHaveSourcesAndValidEnvRanges() {
        val categoryIds = BakaiyaSeedData.categories.map { it.id }.toSet()
        assertTrue(BakaiyaSeedData.crops.isNotEmpty())

        BakaiyaSeedData.crops.forEach { crop ->
            val gc = crop.growingConditions
            assertTrue("Crop ${crop.id} category missing", categoryIds.contains(crop.categoryId))
            assertTrue("Crop ${crop.id} Nepali name blank", crop.nameNe.isNotBlank())
            assertTrue("Crop ${crop.id} source blank", crop.sourceReference.isNotBlank())
            assertFalse("Crop ${crop.id} lastUpdatedDate blank", gc.lastUpdatedDate.isNullOrBlank())

            // Altitude constraints
            assertNotNull("Crop ${crop.id} missing suitable regions", gc.suitableRegionsNe)
            assertTrue(
                "Crop ${crop.id} must have valid Nepal ecological zones",
                NepalEcologicalZone.parseList(gc.suitableZonesCsv).isNotEmpty()
            )
            if (gc.altitudeMinMeters != null && gc.altitudeMaxMeters != null) {
                assertTrue("Altitude inverted for ${crop.id}", gc.altitudeMinMeters!! <= gc.altitudeMaxMeters!!)
            }
            if (gc.altitudeOptimalMinMeters != null && gc.altitudeOptimalMaxMeters != null) {
                assertTrue("Optimal altitude inverted for ${crop.id}", gc.altitudeOptimalMinMeters!! <= gc.altitudeOptimalMaxMeters!!)
            }

            // Temperature & Optimal constraints
            if (gc.tempMin != null && gc.tempMax != null) {
                assertTrue("Temp range inverted for ${crop.id}", gc.tempMin!! <= gc.tempMax!!)
            }
            if (gc.tempOptimalMin != null && gc.tempOptimalMax != null) {
                assertTrue("Optimal temp inverted for ${crop.id}", gc.tempOptimalMin!! <= gc.tempOptimalMax!!)
            }

            // Soil pH & Rainfall bounds
            if (gc.soilPhMin != null && gc.soilPhMax != null) {
                assertTrue("pH range inverted for ${crop.id}", gc.soilPhMin!! <= gc.soilPhMax!!)
            }
            if (gc.rainfallMinMm != null && gc.rainfallMaxMm != null) {
                assertTrue("Rainfall range inverted for ${crop.id}", gc.rainfallMinMm!! <= gc.rainfallMaxMm!!)
            }
            if (gc.humidityMin != null && gc.humidityMax != null) {
                assertTrue("Humidity range inverted for ${crop.id}", gc.humidityMin!! <= gc.humidityMax!!)
            }
        }
    }

    @Test
    fun everyCropHasVerifiedVarietyRequirementsWithValidBounds() {
        val cropIds = BakaiyaSeedData.crops.map { it.id }.toSet()
        val varietiesByCrop = BakaiyaSeedData.varietyRequirements.groupBy { it.cropId }

        cropIds.forEach { cropId ->
            assertTrue("Expected variety growing conditions for $cropId", !varietiesByCrop[cropId].isNullOrEmpty())
        }

        BakaiyaSeedData.varietyRequirements.forEach { variety ->
            val vc = variety.growingConditions
            assertTrue("Variety ${variety.id} references unknown crop", cropIds.contains(variety.cropId))
            assertTrue("Variety ${variety.id} name blank", variety.varietyNameNe.isNotBlank())
            assertTrue("Variety ${variety.id} source blank", variety.sourceReference.isNotBlank())
            if (vc.altitudeMinMeters != null && vc.altitudeMaxMeters != null) {
                assertTrue("Variety ${variety.id} altitude inverted", vc.altitudeMinMeters!! <= vc.altitudeMaxMeters!!)
            }
        }
    }

    @Test
    fun videosStrictlySeparateYouTubeFromAuthorizedDownloads() {
        val cropIds = BakaiyaSeedData.crops.map { it.id }.toSet()
        val validTopics = VideoTopic.entries.map { it.id }.toSet()

        BakaiyaSeedData.educationalVideos.forEach { video ->
            assertTrue("Video ${video.id} unknown crop", cropIds.contains(video.cropId))
            assertTrue("Video ${video.id} invalid topic", validTopics.contains(video.topicId))

            if (video.provider == "youtube") {
                assertFalse("YouTube video must NEVER allow direct download: ${video.id}", video.downloadPermission)
                assertTrue("Invalid YouTube URL in ${video.id}", ValidationUtils.isValidYouTubeUrl(video.videoUrl))
                assertEquals(null, video.downloadableFileUrl)
            } else {
                assertTrue("Offline video must have explicit permission", video.downloadPermission)
                assertTrue(
                    "Invalid download URL for ${video.id}",
                    ValidationUtils.isAuthorizedDownloadUrl(video.downloadableFileUrl, video.downloadPermission)
                )
            }
        }
    }

    @Test
    fun helplineContactsAreVerifiedAndValid() {
        assertTrue(BakaiyaSeedData.helplineContacts.isNotEmpty())
        BakaiyaSeedData.helplineContacts.forEach { contact ->
            assertTrue("Contact ${contact.id} must be verified", contact.isVerified)
            assertTrue("Invalid phone for ${contact.id}", ValidationUtils.isValidNepalPhoneNumber(contact.mobileNumber))
        }
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
