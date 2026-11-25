package com.krishisaathi.app.core.util

import java.util.Locale

/**
 * Pure Kotlin validation and formatting helpers testable in standard JVM unit tests.
 */
object ValidationUtils {

    /**
     * Converts Devanagari numerals (०-९) to ASCII digits and strips whitespace/hyphens and optional +977 prefix.
     */
    fun normalizeNepalMobileNumber(rawPhone: String): String {
        val asciiDigits = buildString(rawPhone.length) {
            for (ch in rawPhone) {
                when (ch) {
                    in '\u0966'..'\u096F' -> append(('0'.code + (ch.code - '\u0966'.code)).toChar())
                    else -> append(ch)
                }
            }
        }
        val stripped = asciiDigits.replace(Regex("[\\s-]"), "")
        return when {
            stripped.startsWith("+977") -> stripped.removePrefix("+977")
            stripped.startsWith("977") && stripped.length == 13 -> stripped.removePrefix("977")
            else -> stripped
        }
    }

    /**
     * Validates Nepali mobile numbers (10 digits starting with 98, 97, or 96).
     */
    fun isValidNepalMobileNumber(rawPhone: String): Boolean {
        val cleaned = normalizeNepalMobileNumber(rawPhone)
        return cleaned.length == 10 &&
                (cleaned.startsWith("98") || cleaned.startsWith("97") || cleaned.startsWith("96")) &&
                cleaned.all { it in '0'..'9' }
    }

    /**
     * Validates Nepali mobile numbers (10 digits starting with 98, 97, or 96) or standard Nepal landline (0XX-XXXXXX / 057XXXXXX).
     */
    fun isValidNepalPhoneNumber(rawPhone: String): Boolean {
        val cleaned = normalizeNepalMobileNumber(rawPhone)
        val isMobile = cleaned.length == 10 &&
                (cleaned.startsWith("98") || cleaned.startsWith("97") || cleaned.startsWith("96")) &&
                cleaned.all { it in '0'..'9' }
        val isLandline = cleaned.length in 9..10 &&
                cleaned.startsWith("0") &&
                cleaned.all { it in '0'..'9' }
        return isMobile || isLandline
    }

    /**
     * Validates Bakaiya Rural Municipality ward numbers (Wards 1-12).
     */
    fun isValidBakaiyaWard(ward: Int?): Boolean = ward != null && ward in 1..12

    /**
     * Validates strict HTTPS external URLs for YouTube educational video playback.
     */
    fun isValidYouTubeUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val trimmed = url.trim()
        return trimmed.startsWith("https://www.youtube.com/watch?v=") ||
                trimmed.startsWith("https://youtube.com/watch?v=") ||
                trimmed.startsWith("https://youtu.be/")
    }

    /**
     * Validates strict HTTPS external URLs for verified YouTube channel handles or paths.
     */
    fun isValidYouTubeChannelUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val trimmed = url.trim()
        return (trimmed.startsWith("https://www.youtube.com/@") ||
                trimmed.startsWith("https://youtube.com/@") ||
                trimmed.startsWith("https://www.youtube.com/channel/") ||
                trimmed.startsWith("https://www.youtube.com/c/")) &&
                trimmed.length > "https://www.youtube.com/@".length
    }

    /**
     * Validates that an offline downloadable video URL uses HTTPS and has authorized permission.
     */
    fun isAuthorizedDownloadUrl(url: String?, downloadPermission: Boolean): Boolean {
        if (!downloadPermission || url.isNullOrBlank()) return false
        return url.trim().startsWith("https://")
    }

    /**
     * Formats raw byte size into human-readable MB or KB.
     */
    fun formatFileSize(bytes: Long?): String {
        if (bytes == null || bytes <= 0L) return "अज्ञात साइज"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
            else -> String.format(Locale.US, "%.0f KB", kb.coerceAtLeast(1.0))
        }
    }
}
