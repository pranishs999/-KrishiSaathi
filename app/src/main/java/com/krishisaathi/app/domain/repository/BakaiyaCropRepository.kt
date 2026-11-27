package com.krishisaathi.app.domain.repository

import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.domain.model.AnimalBreed
import com.krishisaathi.app.domain.model.AnimalCategory
import com.krishisaathi.app.domain.model.AnimalDisease
import com.krishisaathi.app.domain.model.AnimalFeedingGuidance
import com.krishisaathi.app.domain.model.AnimalHealthGuidance
import com.krishisaathi.app.domain.model.AnimalHousingGuidance
import com.krishisaathi.app.domain.model.AnimalImage
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.model.CropImage
import com.krishisaathi.app.domain.model.CropVarietyRequirement
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.model.HelplineContact
import kotlinx.coroutines.flow.Flow

interface BakaiyaCropRepository {
    // ---- Crop methods ----
    fun getCategories(): Flow<List<CropCategory>>
    fun getCrops(): Flow<List<BakaiyaCrop>>
    fun getCropById(cropId: String): Flow<BakaiyaCrop?>
    fun getVarietyRequirementsForCrop(cropId: String): Flow<List<CropVarietyRequirement>>
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
    suspend fun saveFarmerRegistration(
        name: String,
        ward: Int,
        phone: String,
        municipality: String = BakaiyaMunicipality.DEFAULT_NAME,
        toleVillage: String = "",
        consentStatus: Boolean = false
    ): Result<Unit>
    suspend fun clearFarmerRegistration(): Result<Unit>
    /** Retries uploading a pending registration to Supabase. */
    suspend fun retryPendingRegistrationSync(): Result<Unit>
    suspend fun markVideoDownloaded(videoId: String, localPath: String, sizeBytes: Long): Result<Unit>
    suspend fun deleteDownloadedVideo(videoId: String): Result<Unit>
    suspend fun syncRemoteContent(): Result<Unit>
    suspend fun ensureContentSeeded(): Result<Unit>

    // ---- Livestock / पशुपालन methods ----
    suspend fun getAnimalCategories(): List<AnimalCategory>
    suspend fun getAllAnimals(): List<Animal>
    suspend fun getAnimalsByCategory(categoryId: String): List<Animal>
    suspend fun getAnimalById(animalId: String): Animal?
    suspend fun getBreedsForAnimal(animalId: String): List<AnimalBreed>
    suspend fun getHousingForAnimal(animalId: String): AnimalHousingGuidance?
    suspend fun getFeedingForAnimal(animalId: String): AnimalFeedingGuidance?
    suspend fun getHealthForAnimal(animalId: String): AnimalHealthGuidance?
    suspend fun getDiseasesForAnimal(animalId: String): List<AnimalDisease>
    suspend fun getImagesForAnimal(animalId: String): List<AnimalImage>
    suspend fun getVideosForAnimal(animalId: String): List<EducationalVideo>
}
