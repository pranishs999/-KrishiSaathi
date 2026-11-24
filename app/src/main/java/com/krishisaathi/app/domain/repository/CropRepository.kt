package com.krishisaathi.app.domain.repository

import com.krishisaathi.app.domain.model.Crop
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract defining offline-first access to crop data.
 * All public read APIs emit reactive Flows directly from the local Room database.
 */
interface CropRepository {
    fun getCrops(): Flow<List<Crop>>
    fun getCropById(cropId: String): Flow<Crop?>
    suspend fun syncCropsFromRemote(): Result<Unit>
}

