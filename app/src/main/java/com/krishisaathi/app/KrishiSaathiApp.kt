package com.krishisaathi.app

import android.app.Application
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.di.DefaultAppContainer
import com.krishisaathi.app.worker.SyncWorker

/**
 * Custom Application class for Krishi Saathi.
 * Initializes the AppContainer for manual dependency injection and schedules
 * the periodic background sync worker.
 */
class KrishiSaathiApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)

        // Schedule periodic background sync for agricultural advisory data
        SyncWorker.enqueuePeriodicSync(this)
    }
}

