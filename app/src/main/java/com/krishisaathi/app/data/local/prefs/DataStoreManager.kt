package com.krishisaathi.app.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bakaiya_krishi_saathi_prefs")

/**
 * Lightweight Preferences DataStore holding non-sensitive application metadata such as
 * last content update timestamp. No authentication credentials or tokens are stored.
 */
class DataStoreManager(private val context: Context) {

    companion object {
        private val KEY_LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
    }

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_LAST_SYNC_TIMESTAMP] ?: 0L }

    suspend fun updateLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_SYNC_TIMESTAMP] = timestamp }
    }
}
