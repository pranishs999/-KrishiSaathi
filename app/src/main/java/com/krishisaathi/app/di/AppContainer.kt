package com.krishisaathi.app.di

import android.content.Context
import com.krishisaathi.app.core.common.DefaultDispatcherProvider
import com.krishisaathi.app.core.common.DispatcherProvider
import com.krishisaathi.app.data.local.KrishiSaathiDatabase
import com.krishisaathi.app.data.local.prefs.DataStoreManager
import com.krishisaathi.app.data.local.prefs.RegistrationPreferences
import com.krishisaathi.app.data.remote.SupabaseClientProvider
import com.krishisaathi.app.data.repository.BakaiyaCropRepositoryImpl
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import com.krishisaathi.app.media.VideoDownloadManager
import io.github.jan.supabase.SupabaseClient

/**
 * Manual Dependency Injection container holding core singletons and repositories
 * for Bakaiya Krishi Saathi (बकैया कृषि साथी).
 */
interface AppContainer {
    val dispatchers: DispatcherProvider
    val database: KrishiSaathiDatabase
    val dataStoreManager: DataStoreManager
    val registrationPreferences: RegistrationPreferences
    val supabaseClient: SupabaseClient?
    val bakaiyaCropRepository: BakaiyaCropRepository
    val videoDownloadManager: VideoDownloadManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val dispatchers: DispatcherProvider by lazy {
        DefaultDispatcherProvider()
    }

    override val database: KrishiSaathiDatabase by lazy {
        KrishiSaathiDatabase.getInstance(context)
    }

    override val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(context)
    }

    override val registrationPreferences: RegistrationPreferences
        get() = dataStoreManager

    override val supabaseClient: SupabaseClient? by lazy {
        SupabaseClientProvider.createClientOrNull()
    }

    override val bakaiyaCropRepository: BakaiyaCropRepository by lazy {
        BakaiyaCropRepositoryImpl(
            dao = database.bakaiyaCropDao(),
            dispatchers = dispatchers,
            database = database
        )
    }

    override val videoDownloadManager: VideoDownloadManager by lazy {
        VideoDownloadManager(
            context = context.applicationContext,
            repository = bakaiyaCropRepository,
            dispatchers = dispatchers
        )
    }
}
