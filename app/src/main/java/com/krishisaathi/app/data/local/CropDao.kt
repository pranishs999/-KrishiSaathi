package com.krishisaathi.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for cached crop records.
 *
 * Product Constraint:
 * Offline operations for end-user farmers are strictly READ-ONLY.
 * Insert/Upsert operations are reserved for background synchronization routines.
 */
@Dao
interface CropDao {

    /**
     * Read-only stream of all cached crops sorted by Nepali name.
     */
    @Query("SELECT * FROM crops ORDER BY name_ne ASC")
    fun getAllCrops(): Flow<List<CropEntity>>

    /**
     * Read-only stream of a single cached crop by ID.
     */
    @Query("SELECT * FROM crops WHERE id = :cropId LIMIT 1")
    fun getCropById(cropId: String): Flow<CropEntity?>

    /**
     * Read-only search across Nepali and English crop names.
     */
    @Query("SELECT * FROM crops WHERE name_ne LIKE '%' || :query || '%' OR name_en LIKE '%' || :query || '%' ORDER BY name_ne ASC")
    fun searchCrops(query: String): Flow<List<CropEntity>>

    /**
     * Background sync insertion (upsert). Used by repository during cloud sync.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrops(crops: List<CropEntity>)

    /**
     * Background sync single crop insertion.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrop(crop: CropEntity)

    /**
     * Background sync cache purge. Returns the count of deleted rows.
     */
    @Query("DELETE FROM crops")
    suspend fun clearAllCrops(): Int
}

