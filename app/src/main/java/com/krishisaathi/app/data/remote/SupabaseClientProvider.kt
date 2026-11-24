package com.krishisaathi.app.data.remote

import com.krishisaathi.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Initializes and provides the SupabaseClient instance configured with Auth (GoTrue)
 * and PostgREST plugins.
 * Uses BuildConfig credentials loaded securely from local.properties.
 */
object SupabaseClientProvider {

    fun createClient(
        url: String = BuildConfig.SUPABASE_URL,
        key: String = BuildConfig.SUPABASE_ANON_KEY
    ): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

