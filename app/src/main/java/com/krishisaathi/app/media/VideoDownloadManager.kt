package com.krishisaathi.app.media

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.StatFs
import com.krishisaathi.app.core.common.DispatcherProvider
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

sealed class VideoDownloadState {
    object Idle : VideoDownloadState()
    data class Downloading(
        val progress: Float,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : VideoDownloadState()

    data class Paused(
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : VideoDownloadState()

    data class Completed(
        val localPath: String,
        val fileSizeBytes: Long
    ) : VideoDownloadState()

    data class Failed(
        val errorMessageNe: String
    ) : VideoDownloadState()
}

/**
 * Controlled offline video download manager for authorized educational content ONLY.
 *
 * Enforces:
 * - Strict permission check (`video.downloadPermission == true` + HTTPS URL).
 * - Duplicate download prevention.
 * - Free disk storage verification via StatFs.
 * - Protected app-private storage (`filesDir/authorized_videos/`).
 * - Pause/resume support via HTTP Range headers when supported by server.
 * - Never triggers downloads automatically without explicit user action.
 */
class VideoDownloadManager(
    private val context: Context,
    private val repository: BakaiyaCropRepository,
    private val dispatchers: DispatcherProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)
    private val activeJobs = mutableMapOf<String, Job>()

    private val _downloadStates = MutableStateFlow<Map<String, VideoDownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, VideoDownloadState>> = _downloadStates.asStateFlow()

    private val videoDirectory: File
        get() = File(context.filesDir, "authorized_videos").apply {
            if (!exists()) mkdirs()
        }

    fun startOrResumeDownload(video: EducationalVideo) {
        if (!ValidationUtils.isAuthorizedDownloadUrl(video.downloadableFileUrl, video.downloadPermission)) {
            updateState(
                video.id,
                VideoDownloadState.Failed("यो भिडियो अफलाइन डाउनलोडका लागि अनुमतिप्राप्त छैन।")
            )
            return
        }

        // Prevent duplicate concurrent download jobs
        if (activeJobs[video.id]?.isActive == true) {
            return
        }

        val safeFileKey = sanitizeFileId(video.id)
        val targetFile = File(videoDirectory, "$safeFileKey.mp4")
        if (targetFile.exists() && targetFile.length() > 0L) {
            scope.launch {
                repository.markVideoDownloaded(video.id, targetFile.absolutePath, targetFile.length())
                updateState(
                    video.id,
                    VideoDownloadState.Completed(targetFile.absolutePath, targetFile.length())
                )
            }
            return
        }

        if (!isNetworkAvailable()) {
            updateState(
                video.id,
                VideoDownloadState.Failed("इन्टरनेट जडान छैन। कृपया डाउनलोड गर्न इन्टरनेट खोल्नुहोस्।")
            )
            return
        }

        val partFile = File(videoDirectory, "$safeFileKey.mp4.part")

        val job = scope.launch {
            var connection: HttpURLConnection? = null
            var input: InputStream? = null
            var output: FileOutputStream? = null
            try {
                val existingBytes = if (partFile.exists()) partFile.length() else 0L
                connection = openHttpsConnectionWithRedirectGuard(
                    initialUrl = video.downloadableFileUrl!!,
                    existingBytes = existingBytes
                )
                if (connection == null) {
                    updateState(
                        video.id,
                        VideoDownloadState.Failed("असुरक्षित जडान अस्वीकार गरियो। कृपया HTTPS स्रोत प्रयोग गर्नुहोस्।")
                    )
                    return@launch
                }

                val responseCode = connection.responseCode
                val isResumed = responseCode == HttpURLConnection.HTTP_PARTIAL && existingBytes > 0L
                if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_PARTIAL) {
                    updateState(
                        video.id,
                        VideoDownloadState.Failed("सर्भरबाट भिडियो प्राप्त गर्न सकिएन (कोड: $responseCode)।")
                    )
                    return@launch
                }

                val contentLength = connection.contentLengthLong
                val totalBytes = when {
                    isResumed && contentLength > 0L -> existingBytes + contentLength
                    contentLength > 0L -> contentLength
                    else -> -1L
                }

                // Check available storage (minimum 15 MB buffer or content length + 5 MB)
                val requiredSpace = if (totalBytes > 0) (totalBytes - existingBytes) + 5L * 1024 * 1024 else 15L * 1024 * 1024
                if (!hasEnoughStorage(requiredSpace)) {
                    updateState(
                        video.id,
                        VideoDownloadState.Failed("फोनमा भण्डारण ठाउँ अपुग छ। कृपया खाली ठाउँ बनाउनुहोस्।")
                    )
                    return@launch
                }

                var downloaded = if (isResumed) existingBytes else 0L
                if (!isResumed && partFile.exists()) {
                    partFile.delete()
                }

                updateState(
                    video.id,
                    VideoDownloadState.Downloading(
                        progress = if (totalBytes > 0) (downloaded.toFloat() / totalBytes) else 0f,
                        downloadedBytes = downloaded,
                        totalBytes = totalBytes
                    )
                )

                input = connection.inputStream
                output = FileOutputStream(partFile, isResumed)
                val buffer = ByteArray(8192)
                var lastUiUpdate = 0L

                while (isActive) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    output.write(buffer, 0, read)
                    downloaded += read

                    val now = System.currentTimeMillis()
                    if (now - lastUiUpdate >= 120L) {
                        val progress = if (totalBytes > 0) (downloaded.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f
                        updateState(
                            video.id,
                            VideoDownloadState.Downloading(progress, downloaded, totalBytes)
                        )
                        lastUiUpdate = now
                    }
                }

                output.flush()
                output.close()
                output = null

                if (!isActive) {
                    updateState(video.id, VideoDownloadState.Paused(downloaded, totalBytes))
                    return@launch
                }

                // Atomically finalize file
                if (targetFile.exists()) targetFile.delete()
                val renamed = partFile.renameTo(targetFile)
                if (!renamed || !targetFile.exists()) {
                    updateState(
                        video.id,
                        VideoDownloadState.Failed("भिडियो सुरक्षित गर्न सकिएन।")
                    )
                    return@launch
                }

                val finalSize = targetFile.length()
                repository.markVideoDownloaded(video.id, targetFile.absolutePath, finalSize)
                updateState(video.id, VideoDownloadState.Completed(targetFile.absolutePath, finalSize))
            } catch (_: CancellationException) {
                val partialSize = if (partFile.exists()) partFile.length() else 0L
                updateState(video.id, VideoDownloadState.Paused(partialSize, -1L))
            } catch (e: Exception) {
                updateState(
                    video.id,
                    VideoDownloadState.Failed("भिडियो डाउनलोड असफल भयो। इन्टरनेट जाँच गरी पुनः प्रयास गर्नुहोस्।")
                )
            } finally {
                try { input?.close() } catch (_: Exception) {}
                try { output?.close() } catch (_: Exception) {}
                connection?.disconnect()
                activeJobs.remove(video.id)
            }
        }

        activeJobs[video.id] = job
    }

    fun pauseDownload(videoId: String) {
        activeJobs[videoId]?.cancel()
        activeJobs.remove(videoId)
    }

    fun deleteVideo(videoId: String) {
        activeJobs[videoId]?.cancel()
        activeJobs.remove(videoId)
        val safeFileKey = sanitizeFileId(videoId)
        scope.launch {
            val partFile = File(videoDirectory, "$safeFileKey.mp4.part")
            if (partFile.exists()) partFile.delete()
            repository.deleteDownloadedVideo(videoId)
            updateState(videoId, VideoDownloadState.Idle)
        }
    }

    private fun sanitizeFileId(videoId: String): String {
        return videoId.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "video" }
    }

    private fun openHttpsConnectionWithRedirectGuard(
        initialUrl: String,
        existingBytes: Long
    ): HttpURLConnection? {
        var currentUrl = initialUrl
        repeat(4) {
            val parsed = URL(currentUrl)
            if (!parsed.protocol.equals("https", ignoreCase = true)) {
                return null
            }
            val conn = (parsed.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 20_000
                instanceFollowRedirects = false
                if (existingBytes > 0L) {
                    setRequestProperty("Range", "bytes=$existingBytes-")
                }
            }
            conn.connect()
            val code = conn.responseCode
            if (code in 300..399) {
                val location = conn.getHeaderField("Location")?.trim()
                conn.disconnect()
                if (location.isNullOrBlank() || !location.startsWith("https://", ignoreCase = true)) {
                    return null
                }
                currentUrl = location
            } else {
                return conn
            }
        }
        return null
    }

    private fun updateState(videoId: String, state: VideoDownloadState) {
        _downloadStates.update { current -> current + (videoId to state) }
    }

    private fun hasEnoughStorage(requiredBytes: Long): Boolean {
        return try {
            val stat = StatFs(videoDirectory.absolutePath)
            stat.availableBytes >= requiredBytes
        } catch (_: Exception) {
            true
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
