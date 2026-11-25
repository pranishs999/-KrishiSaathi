package com.krishisaathi.app.media

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.krishisaathi.app.core.util.ValidationUtils

sealed class YouTubeLaunchResult {
    object Launched : YouTubeLaunchResult()
    data class Error(val messageNe: String) : YouTubeLaunchResult()
}

/**
 * Safely launches genuine YouTube educational URLs and verified agricultural channels
 * via the official YouTube application or device web browser without unauthorized URL extraction or scraping.
 */
object YouTubeLauncher {

    fun openYouTubeVideo(context: Context, videoUrl: String?): YouTubeLaunchResult {
        if (!ValidationUtils.isValidYouTubeUrl(videoUrl)) {
            return YouTubeLaunchResult.Error("अमान्य वा अनुपलब्ध युट्युब भिडियो लिङ्क।")
        }
        return launchHttpsUri(context, videoUrl!!.trim(), "भिडियो खोल्न समस्या आयो। कृपया इन्टरनेट जाँच गर्नुहोस्।")
    }

    fun openYouTubeChannel(context: Context, channelUrl: String?): YouTubeLaunchResult {
        if (!ValidationUtils.isValidYouTubeChannelUrl(channelUrl)) {
            return YouTubeLaunchResult.Error("अमान्य वा अनुपलब्ध युट्युब च्यानल लिङ्क।")
        }
        return launchHttpsUri(context, channelUrl!!.trim(), "च्यानल खोल्न समस्या आयो। कृपया इन्टरनेट जाँच गर्नुहोस्।")
    }

    private fun launchHttpsUri(
        context: Context,
        url: String,
        genericErrorNe: String
    ): YouTubeLaunchResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            YouTubeLaunchResult.Launched
        } catch (_: ActivityNotFoundException) {
            YouTubeLaunchResult.Error("तपाईंको फोनमा युट्युब वा वेब ब्राउजर एप उपलब्ध छैन।")
        } catch (_: Exception) {
            YouTubeLaunchResult.Error(genericErrorNe)
        }
    }
}
