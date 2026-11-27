package com.krishisaathi.app.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bakaiya_krishi_saathi_prefs")

/**
 * Abstraction for registration completion state persistence.
 */
interface RegistrationPreferences {
    val isRegistrationCompleted: Flow<Boolean>
    suspend fun setRegistrationCompleted(completed: Boolean)
}

/**
 * Lightweight Preferences DataStore holding non-sensitive application metadata such as
 * registration completion state and last content update timestamp.
 * No authentication credentials, OTPs, or MPINs are stored.
 */
class DataStoreManager(private val context: Context) : RegistrationPreferences {

    companion object {
        private val KEY_LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        private val KEY_REGISTRATION_COMPLETED = booleanPreferencesKey("registration_completed")
        private val KEY_TOLE_VILLAGE = stringPreferencesKey("farmer_tole_village")
        private val KEY_REGISTRATION_SYNC_STATUS = stringPreferencesKey("registration_sync_status")
    }

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_LAST_SYNC_TIMESTAMP] ?: 0L }

    val toleVillage: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_TOLE_VILLAGE] ?: "" }

    /** Remote sync status of farmer registration: SYNCED, PENDING, or FAILED. */
    val registrationSyncStatus: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_REGISTRATION_SYNC_STATUS] ?: "PENDING" }

    override val isRegistrationCompleted: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_REGISTRATION_COMPLETED] ?: false }

    suspend fun updateLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_SYNC_TIMESTAMP] = timestamp }
    }

    suspend fun setToleVillage(tole: String) {
        context.dataStore.edit { it[KEY_TOLE_VILLAGE] = tole.trim() }
    }

    suspend fun setRegistrationSyncStatus(status: String) {
        context.dataStore.edit { it[KEY_REGISTRATION_SYNC_STATUS] = status }
    }

    override suspend fun setRegistrationCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_REGISTRATION_COMPLETED] = completed }
    }
}
