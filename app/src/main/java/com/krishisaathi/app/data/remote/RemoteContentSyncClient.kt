package com.krishisaathi.app.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Coordinates remote published-content synchronization for crops, livestock, and verified helpline records.
 * Strictly fetches published records only and falls back safely to local Room/Seed storage offline.
 */
class RemoteContentSyncClient(
    private val supabaseClient: SupabaseClient?,
    private val cmsPublicBaseUrl: String? = null
) {

    suspend fun fetchPublishedLivestockJson(): Result<String?> = withContext(Dispatchers.IO) {
        try {
            val base = cmsPublicBaseUrl?.trim()?.trimEnd('/')
            if (!base.isNullOrEmpty() && base.startsWith("https://")) {
                val url = URL("$base/api/v1/public/livestock/")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8_000
                    readTimeout = 8_000
                    setRequestProperty("Accept", "application/json")
                }
                return@withContext conn.useAndRead()
            }

            if (supabaseClient != null) {
                val response = supabaseClient
                    .from("animals")
                    .select()
                return@withContext Result.success(response.data)
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun HttpURLConnection.useAndRead(): Result<String?> {
        return try {
            val code = responseCode
            if (code in 200..299) {
                val payload = inputStream.bufferedReader().use { it.readText() }
                Result.success(payload)
            } else {
                Result.failure(IllegalStateException("HTTP $code from CMS public endpoint"))
            }
        } finally {
            disconnect()
        }
    }
}
