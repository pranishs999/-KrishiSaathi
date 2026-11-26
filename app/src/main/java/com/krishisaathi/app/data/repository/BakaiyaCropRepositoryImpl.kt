package com.krishisaathi.app.data.repository

import com.krishisaathi.app.core.common.DispatcherProvider
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.data.local.BakaiyaCropDao
import com.krishisaathi.app.data.local.BakaiyaCropEntity
import com.krishisaathi.app.data.local.CategoryEntity
import com.krishisaathi.app.data.local.CropImageEntity
import com.krishisaathi.app.data.local.CropVarietyRequirementEntity
import com.krishisaathi.app.data.local.EducationalVideoEntity
import com.krishisaathi.app.data.local.FarmerRegistrationEntity
import com.krishisaathi.app.data.local.GrowingConditionsEmbedded
import com.krishisaathi.app.data.local.HelplineContactEntity
import com.krishisaathi.app.data.local.KrishiSaathiDatabase
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.model.CropGrowingCondition
import com.krishisaathi.app.domain.model.CropImage
import com.krishisaathi.app.domain.model.CropVarietyRequirement
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.model.HelplineContact
import com.krishisaathi.app.domain.model.NepalEcologicalZone
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.krishisaathi.app.data.local.BakaiyaLivestockSeedData
import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.domain.model.AnimalBreed
import com.krishisaathi.app.domain.model.AnimalCategory
import com.krishisaathi.app.domain.model.AnimalDisease
import com.krishisaathi.app.domain.model.AnimalFeedingGuidance
import com.krishisaathi.app.domain.model.AnimalHealthGuidance
import com.krishisaathi.app.domain.model.AnimalHousingGuidance
import com.krishisaathi.app.domain.model.AnimalImage
import java.io.File

class BakaiyaCropRepositoryImpl(
    private val dao: BakaiyaCropDao,
    private val dispatchers: DispatcherProvider,
    private val database: KrishiSaathiDatabase? = null
) : BakaiyaCropRepository {

    override fun getCategories(): Flow<List<CropCategory>> {
        return dao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getCrops(): Flow<List<BakaiyaCrop>> {
        return dao.getAllCrops().map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getCropById(cropId: String): Flow<BakaiyaCrop?> {
        return dao.getCropById(cropId).map { it?.toDomain() }.flowOn(dispatchers.io)
    }

    override fun getVarietyRequirementsForCrop(cropId: String): Flow<List<CropVarietyRequirement>> {
        return dao.getVarietyRequirementsForCrop(cropId).map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getCropsByCategory(categoryId: String): Flow<List<BakaiyaCrop>> {
        return dao.getCropsByCategory(categoryId).map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun searchCrops(query: String): Flow<List<BakaiyaCrop>> {
        return dao.searchCrops(query).map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getCropImages(cropId: String): Flow<List<CropImage>> {
        return dao.getImagesForCrop(cropId).map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getCropVideos(cropId: String): Flow<List<EducationalVideo>> {
        return dao.getVideosForCrop(cropId).map { list ->
            list.map { it.toSyncedDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getVideosForTopic(cropId: String, topicId: Int): Flow<List<EducationalVideo>> {
        return dao.getVideosForCropAndTopic(cropId, topicId).map { list ->
            list.map { it.toSyncedDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getDownloadedVideos(): Flow<List<EducationalVideo>> {
        return dao.getDownloadedVideos().map { list ->
            // Verify physical file existence on disk before displaying as playable (Section 8.2)
            list.filter { entity ->
                entity.localFilePath?.let { File(it).exists() } == true
            }.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getVideoById(videoId: String): Flow<EducationalVideo?> {
        return dao.getVideoById(videoId).map { it?.toSyncedDomain() }.flowOn(dispatchers.io)
    }

    override fun getHelplineContacts(): Flow<List<HelplineContact>> {
        return dao.getAllHelplineContacts().map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getContactsByWard(wardNumber: Int): Flow<List<HelplineContact>> {
        return dao.getContactsByWard(wardNumber).map { list ->
            list.map { it.toDomain() }
        }.flowOn(dispatchers.io)
    }

    override fun getFarmerRegistration(): Flow<FarmerRegistration?> {
        return dao.getFarmerRegistration().map { it?.toDomain() }.flowOn(dispatchers.io)
    }

    override suspend fun saveFarmerRegistration(
        name: String,
        ward: Int,
        phone: String,
        municipality: String
    ): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val trimmedName = name.trim()
                val trimmedMunicipality = municipality.trim().ifBlank { BakaiyaMunicipality.DEFAULT_NAME }
                val normalizedPhone = ValidationUtils.normalizeNepalMobileNumber(phone)

                if (trimmedName.isEmpty() ||
                    !ValidationUtils.isValidBakaiyaWard(ward) ||
                    !ValidationUtils.isValidNepalMobileNumber(normalizedPhone)
                ) {
                    return@withContext Result.failure(
                        IllegalArgumentException("कृपया मान्य नाम, वडा नम्बर र १० अंकको मोबाइल नम्बर राख्नुहोस्।")
                    )
                }

                val insertedRowId = dao.saveFarmerRegistration(
                    FarmerRegistrationEntity(
                        fullName = trimmedName,
                        municipality = trimmedMunicipality,
                        wardNumber = ward,
                        phoneNumber = normalizedPhone,
                        registeredAt = System.currentTimeMillis()
                    )
                )
                val persisted = dao.findFarmerRegistrationSync()
                if (insertedRowId <= 0L || persisted == null) {
                    return@withContext Result.failure(
                        IllegalStateException("किसान विवरण डेटाबेसमा सुरक्षित हुन सकेन।")
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun clearFarmerRegistration(): Result<Unit> = withContext(dispatchers.io) {
        try {
            dao.clearFarmerRegistration()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markVideoDownloaded(
        videoId: String,
        localPath: String,
        sizeBytes: Long
    ): Result<Unit> = withContext(dispatchers.io) {
        try {
            val video = dao.findVideoByIdSync(videoId)
            if (video != null && video.downloadPermission) {
                dao.updateVideo(
                    video.copy(
                        isDownloaded = true,
                        localFilePath = localPath,
                        fileSizeBytes = sizeBytes
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDownloadedVideo(videoId: String): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val video = dao.findVideoByIdSync(videoId)
                video?.localFilePath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                dao.markVideoDeleted(videoId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun syncRemoteContent(): Result<Unit> = withContext(dispatchers.io) {
        try {
            if (database != null) {
                KrishiSaathiDatabase.seedDatabaseInTransaction(database)
            } else {
                KrishiSaathiDatabase.seedDatabase(dao)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun ensureContentSeeded(): Result<Unit> = withContext(dispatchers.io) {
        try {
            if (dao.countPublishedCropsSync() == 0) {
                if (database != null) {
                    KrishiSaathiDatabase.seedDatabaseInTransaction(database)
                } else {
                    KrishiSaathiDatabase.seedDatabase(dao)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun EducationalVideoEntity.toSyncedDomain(): EducationalVideo {
        val fileExists = isDownloaded && localFilePath?.let { File(it).exists() } == true
        return toDomain().copy(
            isDownloaded = fileExists,
            localFilePath = if (fileExists) localFilePath else null
        )
    }

    private fun CategoryEntity.toDomain() = CropCategory(id, nameNe, nameEn, iconName)

    private fun GrowingConditionsEmbedded.toDomain(sourceReference: String) = CropGrowingCondition(
        altitudeMinMeters = altitudeMinMeters,
        altitudeOptimalMinMeters = altitudeOptimalMinMeters,
        altitudeOptimalMaxMeters = altitudeOptimalMaxMeters,
        altitudeMaxMeters = altitudeMaxMeters,
        altitudeSummaryNe = altitudeSummaryNe,
        altitudeGrowthNotesNe = altitudeGrowthNotesNe,
        suitableZones = NepalEcologicalZone.parseList(suitableZonesCsv),
        suitableRegionsNe = suitableRegionsNe,
        soilTypesNe = soilTypesNe,
        soilTextureNe = soilTextureNe,
        soilPhMin = soilPhMin,
        soilPhMax = soilPhMax,
        soilDrainageNe = soilDrainageNe,
        soilFertilityNe = soilFertilityNe,
        soilOrganicMatterNe = soilOrganicMatterNe,
        tempMin = tempMin,
        tempOptimalMin = tempOptimalMin,
        tempOptimalMax = tempOptimalMax,
        tempMax = tempMax,
        humidityMin = humidityMin,
        humidityMax = humidityMax,
        rainfallMinMm = rainfallMinMm,
        rainfallMaxMm = rainfallMaxMm,
        rainfallPeriodLabelNe = rainfallPeriodLabelNe,
        waterRequirementNe = waterRequirementNe,
        sunlightNe = sunlightNe,
        plantingSeasonNe = plantingSeasonNe,
        sowingPeriodNe = sowingPeriodNe,
        transplantingPeriodNe = transplantingPeriodNe,
        growingDurationNe = growingDurationNe,
        harvestingPeriodNe = harvestingPeriodNe,
        irrigationGuidanceNe = irrigationGuidanceNe,
        specialConsiderationsNe = specialConsiderationsNe,
        sourceReference = sourceReference,
        lastUpdatedDate = lastUpdatedDate
    )

    private fun BakaiyaCropEntity.toDomain() = BakaiyaCrop(
        id = id,
        categoryId = categoryId,
        nameNe = nameNe,
        nameEn = nameEn,
        scientificName = scientificName,
        descriptionNe = descriptionNe,
        varieties = varieties,
        climateOverview = climateOverview,
        growingConditions = growingConditions.toDomain(sourceReference),
        landPreparation = landPreparation,
        seedSelection = seedSelection,
        spacing = spacing,
        fertilizer = fertilizer,
        cropCare = cropCare,
        pestDiseaseControl = pestDiseaseControl,
        harvesting = harvesting,
        storage = storage,
        sourceReference = sourceReference,
        isVerifiedContent = isVerifiedContent,
        thumbnailUrl = thumbnailUrl,
        isPublished = isPublished
    )

    private fun CropVarietyRequirementEntity.toDomain() = CropVarietyRequirement(
        id = id,
        cropId = cropId,
        cropNameNe = cropNameNe,
        cropNameEn = cropNameEn,
        varietyNameNe = varietyNameNe,
        varietyNameEn = varietyNameEn,
        notesNe = notesNe,
        growingConditions = growingConditions.toDomain(sourceReference)
    )

    private fun CropImageEntity.toDomain() =
        CropImage(id, cropId, imageUrl, captionNe, stageCategory, isSampleImage)

    private fun EducationalVideoEntity.toDomain() = EducationalVideo(
        id = id,
        cropId = cropId,
        topicId = topicId,
        titleNe = titleNe,
        descriptionNe = descriptionNe,
        channelName = channelName,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        duration = duration,
        provider = provider,
        downloadPermission = downloadPermission,
        downloadableFileUrl = downloadableFileUrl,
        localFilePath = localFilePath,
        fileSizeBytes = fileSizeBytes,
        isDownloaded = isDownloaded,
        isSampleMedia = isSampleMedia
    )

    private fun HelplineContactEntity.toDomain() =
        HelplineContact(id, nameNe, designationNe, wardNe, wardNumber, mobileNumber, email, isVerified)

    private fun FarmerRegistrationEntity.toDomain() =
        FarmerRegistration(
            fullName = fullName,
            municipality = municipality,
            wardNumber = wardNumber,
            phoneNumber = phoneNumber,
            registeredAt = registeredAt
        )

    // ---- Livestock / पशुपालन repository methods ----

    override suspend fun getAnimalCategories(): List<AnimalCategory> =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalCategories.sortedBy { it.sortOrder }
        }

    override suspend fun getAllAnimals(): List<Animal> =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animals.filter { it.isPublished }.sortedBy { it.sortOrder }
        }

    override suspend fun getAnimalsByCategory(categoryId: String): List<Animal> =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animals
                .filter { it.categoryId == categoryId && it.isPublished }
                .sortedBy { it.sortOrder }
        }

    override suspend fun getAnimalById(animalId: String): Animal? =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animals.find { it.id == animalId && it.isPublished }
        }

    override suspend fun getBreedsForAnimal(animalId: String): List<AnimalBreed> =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalBreeds
                .filter { it.animalId == animalId }
                .sortedBy { it.sortOrder }
        }

    override suspend fun getHousingForAnimal(animalId: String): AnimalHousingGuidance? =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalHousing.find { it.animalId == animalId }
        }

    override suspend fun getFeedingForAnimal(animalId: String): AnimalFeedingGuidance? =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalFeeding.find { it.animalId == animalId }
        }

    override suspend fun getHealthForAnimal(animalId: String): AnimalHealthGuidance? =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalHealth.find { it.animalId == animalId }
        }

    override suspend fun getDiseasesForAnimal(animalId: String): List<AnimalDisease> =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalDiseases
                .filter { it.animalId == animalId }
                .sortedBy { it.sortOrder }
        }

    override suspend fun getImagesForAnimal(animalId: String): List<AnimalImage> =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalImages
                .filter { it.animalId == animalId }
                .sortedBy { it.sortOrder }
        }

    override suspend fun getVideosForAnimal(animalId: String): List<EducationalVideo> =
        withContext(dispatchers.io) {
            BakaiyaLivestockSeedData.animalVideos[animalId].orEmpty()
        }
}
