package com.krishisaathi.app.data.remote

import com.krishisaathi.app.data.local.prefs.DataStoreManager
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Coordinates app-wide foreground auto-refresh, connectivity-restored sync,
 * and manual pull-to-refresh across all screens.
 */
class AppSyncController(
    private val repository: BakaiyaCropRepository,
    private val dataStoreManager: DataStoreManager? = null
) {
    private val syncMutex = Mutex()
    private var lastAttemptAtMs: Long = 0L

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncGeneration = MutableStateFlow(0L)
    val syncGeneration: StateFlow<Long> = _syncGeneration.asStateFlow()

    /**
     * Synchronizes pending farmer registrations and all Supabase content into Room.
     * Deduplicates overlapping sync calls and throttles automatic refreshes.
     */
    suspend fun refreshNow(force: Boolean = true): Result<Unit> {
        val now = System.currentTimeMillis()
        if (!force && (now - lastAttemptAtMs) < AUTO_REFRESH_THROTTLE_MS) {
            return Result.success(Unit)
        }

        return syncMutex.withLock {
            val checkTime = System.currentTimeMillis()
            if (!force && (checkTime - lastAttemptAtMs) < AUTO_REFRESH_THROTTLE_MS) {
                return@withLock Result.success(Unit)
            }
            lastAttemptAtMs = checkTime
            _isSyncing.value = true
            try {
                repository.retryPendingRegistrationSync()
                val contentResult = repository.syncRemoteContent()
                if (contentResult.isSuccess) {
                    dataStoreManager?.updateLastSyncTimestamp(System.currentTimeMillis())
                    _syncGeneration.value = _syncGeneration.value + 1L
                }
                contentResult
            } finally {
                _isSyncing.value = false
            }
        }
    }

    companion object {
        const val FOREGROUND_AUTO_REFRESH_INTERVAL_MS = 60_000L
        private const val AUTO_REFRESH_THROTTLE_MS = 15_000L
    }
}
