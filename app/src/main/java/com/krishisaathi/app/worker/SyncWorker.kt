package com.krishisaathi.app.worker

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.krishisaathi.app.BuildConfig
import com.krishisaathi.app.KrishiSaathiApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Background WorkManager worker responsible for:
 * 1. Retrying pending offline farmer registrations once internet returns.
 * 2. Refreshing cached agricultural knowledge into local Room database.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "bakaiya_krishi_saathi_sync_work"
        const val REGISTRATION_RETRY_WORK_NAME = "bakaiya_krishi_saathi_registration_retry"
        private const val TAG = "SyncWorker"

        fun enqueuePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }

        fun enqueueRegistrationRetry(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val retryRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                REGISTRATION_RETRY_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                retryRequest
            )
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Running Bakaiya sync worker...")
        }
        try {
            val appContainer = (applicationContext as KrishiSaathiApp).appContainer
            val regSyncResult = appContainer.bakaiyaCropRepository.retryPendingRegistrationSync()
            val contentSyncResult = appContainer.bakaiyaCropRepository.syncRemoteContent()
            if (regSyncResult.isSuccess && contentSyncResult.isSuccess) {
                appContainer.dataStoreManager.updateLastSyncTimestamp(System.currentTimeMillis())
                appContainer.dataStoreManager.setRegistrationSyncStatus("SYNCED")
                Result.success()
            } else {
                Result.retry()
            }
        } catch (_: Exception) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Background sync failed; scheduling retry.")
            }
            Result.retry()
        }
    }
}
