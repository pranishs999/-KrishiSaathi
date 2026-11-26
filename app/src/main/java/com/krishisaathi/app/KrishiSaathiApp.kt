package com.krishisaathi.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.di.DefaultAppContainer
import com.krishisaathi.app.worker.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

/**
 * Custom Application class for Krishi Saathi.
 * Initializes lightweight DI container on startup, configures bounded Coil caching,
 * and schedules WorkManager sync asynchronously off the main thread.
 */
class KrishiSaathiApp : Application(), ImageLoaderFactory {

    lateinit var appContainer: AppContainer
        private set

    private val appInitScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)

        // Defer WorkManager initialization & scheduling off the main startup thread
        appInitScope.launch {
            SyncWorker.enqueuePeriodicSync(applicationContext)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.15)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(cacheDir, "coil_image_cache"))
                    .maxSizeBytes(25L * 1024L * 1024L)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
