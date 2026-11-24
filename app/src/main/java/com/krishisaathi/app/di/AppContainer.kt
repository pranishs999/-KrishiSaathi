package com.krishisaathi.app.di

import android.content.Context
import com.krishisaathi.app.data.local.KrishiSaathiDatabase
import com.krishisaathi.app.data.local.prefs.DataStoreManager
import com.krishisaathi.app.data.remote.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient

/**
 * Manual Dependency Injection container holding core singletons across the application.
 * Decoupled from heavy DI frameworks (like Hilt) to maintain lean architecture and zero bloat.
 */
interface AppContainer {
    val database: KrishiSaathiDatabase
    val dataStoreManager: DataStoreManager
    val supabaseClient: SupabaseClient
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: KrishiSaathiDatabase by lazy {
        KrishiSaathiDatabase.getInstance(context)
    }

    override val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(context)
    }

    override val supabaseClient: SupabaseClient by lazy {
        SupabaseClientProvider.createClient()
    }
}

