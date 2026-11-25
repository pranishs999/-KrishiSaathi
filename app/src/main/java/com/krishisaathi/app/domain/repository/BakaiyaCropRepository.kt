package com.krishisaathi.app.domain.repository

import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.model.CropImage
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.model.FarmRecord
import com.krishisaathi.app.domain.model.FarmRecordType
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.model.HelplineContact
import kotlinx.coroutines.flow.Flow

interface BakaiyaCropRepository {
    fun getCategories(): Flow<List<CropCategory>>
    fun getCrops(): Flow<List<BakaiyaCrop>>
    fun getCropById(cropId: String): Flow<BakaiyaCrop?>
    fun getCropsByCategory(categoryId: String): Flow<List<BakaiyaCrop>>
    fun searchCrops(query: String): Flow<List<BakaiyaCrop>>
    fun getCropImages(cropId: String): Flow<List<CropImage>>
    fun getCropVideos(cropId: String): Flow<List<EducationalVideo>>
    fun getVideosForTopic(cropId: String, topicId: Int): Flow<List<EducationalVideo>>
    fun getDownloadedVideos(): Flow<List<EducationalVideo>>
    fun getVideoById(videoId: String): Flow<EducationalVideo?>
    fun getHelplineContacts(): Flow<List<HelplineContact>>
    fun getContactsByWard(wardNumber: Int): Flow<List<HelplineContact>>
    fun getFarmerRegistration(): Flow<FarmerRegistration?>
    suspend fun saveFarmerRegistration(name: String, ward: Int, phone: String): Result<Unit>
    suspend fun clearFarmerRegistration(): Result<Unit>
    suspend fun markVideoDownloaded(videoId: String, localPath: String, sizeBytes: Long): Result<Unit>
    suspend fun deleteDownloadedVideo(videoId: String): Result<Unit>
    suspend fun syncRemoteContent(): Result<Unit>

    // Farm Records (फार्मको हिसाब)
    fun getAllFarmRecords(): Flow<List<FarmRecord>>
    fun getFarmRecordsByType(type: FarmRecordType): Flow<List<FarmRecord>>
    fun getTotalIncome(): Flow<Double>
    fun getTotalExpense(): Flow<Double>
    suspend fun addFarmRecord(record: FarmRecord): Result<Unit>
    suspend fun deleteFarmRecord(recordId: String): Result<Unit>
}
