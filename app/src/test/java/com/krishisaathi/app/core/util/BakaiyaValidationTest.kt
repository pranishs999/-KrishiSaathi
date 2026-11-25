package com.krishisaathi.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BakaiyaValidationTest {

    @Test
    fun nepalPhoneValidationAcceptsValidNumbersAndRejectsInvalidOnes() {
        assertTrue(ValidationUtils.isValidNepalPhoneNumber("9868445081"))
        assertTrue(ValidationUtils.isValidNepalPhoneNumber("9855088066"))
        assertTrue(ValidationUtils.isValidNepalPhoneNumber("9741234567"))
        assertTrue(ValidationUtils.isValidNepalPhoneNumber("057-520123"))
        assertTrue(ValidationUtils.isValidNepalPhoneNumber("98 68 44 50 81"))

        assertFalse(ValidationUtils.isValidNepalPhoneNumber(""))
        assertFalse(ValidationUtils.isValidNepalPhoneNumber("123456"))
        assertFalse(ValidationUtils.isValidNepalPhoneNumber("9512345678"))
        assertFalse(ValidationUtils.isValidNepalPhoneNumber("986844508199"))
        assertFalse(ValidationUtils.isValidNepalPhoneNumber("98684abc81"))
    }

    @Test
    fun nepalMobileValidationEnforcesTenDigitsHandlesPrefixAndDevanagari() {
        assertTrue(ValidationUtils.isValidNepalMobileNumber("9841234567"))
        assertTrue(ValidationUtils.isValidNepalMobileNumber("+977 9841234567"))
        assertTrue(ValidationUtils.isValidNepalMobileNumber("९८४१२३४५६७"))
        assertTrue(ValidationUtils.isValidNepalMobileNumber("9741234567"))

        assertFalse(ValidationUtils.isValidNepalMobileNumber("057-520123"))
        assertFalse(ValidationUtils.isValidNepalMobileNumber("12345"))
        assertFalse(ValidationUtils.isValidNepalMobileNumber("9512345678"))
        assertFalse(ValidationUtils.isValidNepalMobileNumber("98412abc67"))
    }

    @Test
    fun wardValidationEnforcesBakaiyaWardsOneToTwelve() {
        (1..12).forEach { ward ->
            assertTrue(ValidationUtils.isValidBakaiyaWard(ward))
        }
        assertFalse(ValidationUtils.isValidBakaiyaWard(null))
        assertFalse(ValidationUtils.isValidBakaiyaWard(0))
        assertFalse(ValidationUtils.isValidBakaiyaWard(13))
    }

    @Test
    fun youTubeUrlValidationEnforcesHttpsAndKnownHosts() {
        assertTrue(ValidationUtils.isValidYouTubeUrl("https://www.youtube.com/watch?v=NzwRsir1-ps"))
        assertTrue(ValidationUtils.isValidYouTubeUrl("https://youtube.com/watch?v=ttjs2dSpI3A"))
        assertTrue(ValidationUtils.isValidYouTubeUrl("https://youtu.be/NzwRsir1-ps"))

        assertFalse(ValidationUtils.isValidYouTubeUrl(null))
        assertFalse(ValidationUtils.isValidYouTubeUrl(""))
        assertFalse(ValidationUtils.isValidYouTubeUrl("http://www.youtube.com/watch?v=NzwRsir1-ps"))
        assertFalse(ValidationUtils.isValidYouTubeUrl("https://example.com/video.mp4"))
    }

    @Test
    fun youTubeChannelUrlValidationEnforcesHttpsAndChannelPaths() {
        assertTrue(ValidationUtils.isValidYouTubeChannelUrl("https://www.youtube.com/@iiaitcnepal2768"))
        assertTrue(ValidationUtils.isValidYouTubeChannelUrl("https://youtube.com/@KrishiTelevisionHD1"))
        assertTrue(ValidationUtils.isValidYouTubeChannelUrl("https://www.youtube.com/channel/UC12345"))

        assertFalse(ValidationUtils.isValidYouTubeChannelUrl(null))
        assertFalse(ValidationUtils.isValidYouTubeChannelUrl(""))
        assertFalse(ValidationUtils.isValidYouTubeChannelUrl("https://www.youtube.com/@"))
        assertFalse(ValidationUtils.isValidYouTubeChannelUrl("http://www.youtube.com/@iiaitcnepal2768"))
        assertFalse(ValidationUtils.isValidYouTubeChannelUrl("https://example.com/@channel"))
    }

    @Test
    fun authorizedDownloadUrlRequiresBothPermissionAndHttps() {
        val httpsUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        assertTrue(ValidationUtils.isAuthorizedDownloadUrl(httpsUrl, downloadPermission = true))
        assertFalse(ValidationUtils.isAuthorizedDownloadUrl(httpsUrl, downloadPermission = false))
        assertFalse(ValidationUtils.isAuthorizedDownloadUrl("http://example.com/vid.mp4", downloadPermission = true))
        assertFalse(ValidationUtils.isAuthorizedDownloadUrl(null, downloadPermission = true))
    }

    @Test
    fun fileSizeFormatterProducesHumanReadableStrings() {
        assertEquals("अज्ञात साइज", ValidationUtils.formatFileSize(null))
        assertEquals("अज्ञात साइज", ValidationUtils.formatFileSize(0L))
        assertEquals("512 KB", ValidationUtils.formatFileSize(512L * 1024L))
        assertEquals("2.5 MB", ValidationUtils.formatFileSize((2.5 * 1024 * 1024).toLong()))
    }
}
