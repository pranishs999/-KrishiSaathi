package com.krishisaathi.app.data.remote

import com.krishisaathi.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Initializes and provides an optional SupabaseClient instance configured with PostgREST.
 * Returns null safely when credentials are absent or URL is not HTTPS so offline mode never fails.
 */
object SupabaseClientProvider {

    fun createClientOrNull(
        url: String = BuildConfig.SUPABASE_URL,
        key: String = BuildConfig.SUPABASE_ANON_KEY
    ): SupabaseClient? {
        val trimmedUrl = url.trim()
        val trimmedKey = key.trim()
        if (!trimmedUrl.startsWith("https://") ||
            trimmedKey.isEmpty() ||
            trimmedKey == "placeholder_anon_key"
        ) {
            return null
        }

        return createSupabaseClient(
            supabaseUrl = trimmedUrl,
            supabaseKey = trimmedKey
        ) {
            install(Postgrest)
        }
    }
}
