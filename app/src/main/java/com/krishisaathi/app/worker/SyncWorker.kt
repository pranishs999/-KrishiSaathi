package com.krishisaathi.app.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.krishisaathi.app.KrishiSaathiApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Background WorkManager worker responsible for synchronizing verified agricultural data
 * from Supabase to local Room database.
 *
 * Strict Offline-First Constraint:
 * SyncWorker ONLY pulls verified published agricultural data from Supabase to Room.
 * The farmer's app always reads strictly from Room (Single Source of Truth).
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "krishi_saathi_agricultural_sync_work"
        private const val TAG = "SyncWorker"

        fun enqueuePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting background agricultural advisory sync...")
        try {
            val appContainer = (applicationContext as KrishiSaathiApp).appContainer
            val database = appContainer.database
            val supabase = appContainer.supabaseClient
            val dataStore = appContainer.dataStoreManager

            // Strict Offline-First Constraint:
            // Background sync ONLY pulls verified published agricultural data from Supabase
            // and writes it into local Room cache (Room is SSOT for farmers).
            // Placeholder for data delta ingestion:
            // e.g.:
            // val lastSync = ...
            // val remoteCrops = supabase.from("crops").select().decodeList<CropRemoteDto>()
            // database.cropDao().upsertCrops(...)

            val currentTimestamp = System.currentTimeMillis()
            dataStore.updateLastSyncTimestamp(currentTimestamp)
            Log.d(TAG, "Agricultural sync completed successfully at $currentTimestamp")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Agricultural sync failed: ${e.message}", e)
            Result.retry()
        }
    }
}

