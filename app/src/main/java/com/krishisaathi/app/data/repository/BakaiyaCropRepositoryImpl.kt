package com.krishisaathi.app.data.repository

import com.krishisaathi.app.core.common.DispatcherProvider
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.data.local.AnimalBreedEntity
import com.krishisaathi.app.data.local.AnimalCategoryEntity
import com.krishisaathi.app.data.local.AnimalDiseaseEntity
import com.krishisaathi.app.data.local.AnimalEntity
import com.krishisaathi.app.data.local.AnimalFeedingEntity
import com.krishisaathi.app.data.local.AnimalHealthEntity
import com.krishisaathi.app.data.local.AnimalHousingEntity
import com.krishisaathi.app.data.local.AnimalImageEntity
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
import com.krishisaathi.app.data.remote.RegistrationUploadResult
import com.krishisaathi.app.data.remote.RemoteContentSyncClient
import com.krishisaathi.app.data.remote.RemoteSyncBundle
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
import com.krishisaathi.app.domain.model.CropGrowingCondition
import com.krishisaathi.app.domain.model.CropImage
import com.krishisaathi.app.domain.model.CropVarietyRequirement
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.model.HelplineContact
import com.krishisaathi.app.domain.model.NepalEcologicalZone
import com.krishisaathi.app.domain.model.RegistrationSyncStatus
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Single-Source-of-Truth Repository:
 * - Zero inbuilt crop, livestock, or helpline data.
 * - Reads exclusively from the local Room SQLite database.
 * - Refreshes Room tables exclusively from Supabase via [RemoteContentSyncClient].
 */
class BakaiyaCropRepositoryImpl(
    private val dao: BakaiyaCropDao,
    private val dispatchers: DispatcherProvider,
    private val database: KrishiSaathiDatabase? = null,
    private val remoteClient: RemoteContentSyncClient? = null
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
        municipality: String,
        toleVillage: String,
        consentStatus: Boolean
    ): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val trimmedName = name.trim()
                val trimmedMunicipality = municipality.trim().ifBlank { BakaiyaMunicipality.DEFAULT_NAME }
                val trimmedTole = toleVillage.trim()
                val normalizedPhone = ValidationUtils.normalizeNepalMobileNumber(phone)

                if (trimmedName.isEmpty() ||
                    !ValidationUtils.isValidBakaiyaWard(ward) ||
                    !ValidationUtils.isValidNepalMobileNumber(normalizedPhone)
                ) {
                    return@withContext Result.failure(
                        IllegalArgumentException("कृपया मान्य नाम, वडा नम्बर र १० अंकको मोबाइल नम्बर राख्नुहोस्।")
                    )
                }

                val registrationTime = System.currentTimeMillis()
                val entity = FarmerRegistrationEntity(
                    fullName = trimmedName,
                    municipality = trimmedMunicipality,
                    wardNumber = ward,
                    phoneNumber = normalizedPhone,
                    toleVillage = trimmedTole,
                    consentStatus = consentStatus,
                    registeredAt = registrationTime,
                    syncStatus = RegistrationSyncStatus.PENDING.name
                )
                val insertedRowId = dao.saveFarmerRegistration(entity)
                val persisted = dao.findFarmerRegistrationSync()
                if (insertedRowId <= 0L || persisted == null) {
                    return@withContext Result.failure(
                        IllegalStateException("किसान विवरण डेटाबेसमा सुरक्षित हुन सकेन।")
                    )
                }

                val client = remoteClient
                if (client != null && client.isSupabaseConfigured) {
                    when (val uploadResult = client.uploadFarmerRegistration(persisted.toDomain())) {
                        is RegistrationUploadResult.Success,
                        is RegistrationUploadResult.AlreadyRegistered -> {
                            dao.updateRegistrationSyncStatus(RegistrationSyncStatus.SYNCED.name)
                        }
                        is RegistrationUploadResult.ServerError -> {
                            if (uploadResult.message.startsWith("Edge Function HTTP 400")) {
                                dao.updateRegistrationSyncStatus(RegistrationSyncStatus.FAILED.name)
                                return@withContext Result.failure(
                                    IllegalArgumentException("सर्भर प्रमाणीकरण असफल भयो। कृपया विवरण जाँच गर्नुहोस्।")
                                )
                            }
                            dao.updateRegistrationSyncStatus(RegistrationSyncStatus.PENDING.name)
                        }
                        is RegistrationUploadResult.NetworkError -> {
                            dao.updateRegistrationSyncStatus(RegistrationSyncStatus.PENDING.name)
                        }
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun retryPendingRegistrationSync(): Result<Unit> = withContext(dispatchers.io) {
        try {
            val existing = dao.findFarmerRegistrationSync() ?: return@withContext Result.success(Unit)
            if (existing.syncStatus == RegistrationSyncStatus.SYNCED.name) {
                return@withContext Result.success(Unit)
            }
            val client = remoteClient
                ?: return@withContext Result.failure(IllegalStateException("Remote client unavailable"))
            if (!client.isSupabaseConfigured) {
                return@withContext Result.failure(IllegalStateException("Supabase not configured"))
            }

            when (val result = client.uploadFarmerRegistration(existing.toDomain())) {
                is RegistrationUploadResult.Success,
                is RegistrationUploadResult.AlreadyRegistered -> {
                    dao.updateRegistrationSyncStatus(RegistrationSyncStatus.SYNCED.name)
                    Result.success(Unit)
                }
                is RegistrationUploadResult.ServerError -> {
                    dao.updateRegistrationSyncStatus(RegistrationSyncStatus.FAILED.name)
                    Result.failure(IllegalStateException(result.message))
                }
                is RegistrationUploadResult.NetworkError -> {
                    dao.updateRegistrationSyncStatus(RegistrationSyncStatus.PENDING.name)
                    Result.failure(result.exception)
                }
            }
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

    /**
     * Fetches ALL content directly from Supabase and replaces local Room cache tables.
     * Preserves farmer_registration and locally downloaded offline video files.
     */
    override suspend fun syncRemoteContent(): Result<Unit> = withContext(dispatchers.io) {
        val client = remoteClient
            ?: return@withContext Result.failure(IllegalStateException("Supabase client unavailable"))
        val remoteResult = client.fetchAllRemoteContent()
        if (remoteResult.isFailure) {
            return@withContext Result.failure(
                remoteResult.exceptionOrNull() ?: IllegalStateException("Supabase sync failed")
            )
        }
        val bundle = remoteResult.getOrThrow()

        try {
            if (database != null) {
                database.runInTransaction {
                    applyRemoteBundle(bundle)
                }
            } else {
                applyRemoteBundle(bundle)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun ensureContentSeeded(): Result<Unit> = withContext(dispatchers.io) {
        if (dao.countPublishedCropsSync() == 0 || dao.getPublishedAnimalsSync().isEmpty()) {
            syncRemoteContent()
        } else {
            Result.success(Unit)
        }
    }

    private fun applyRemoteBundle(bundle: RemoteSyncBundle) {
        dao.clearImages()
        dao.clearVarietyRequirements()
        dao.clearCrops()
        dao.clearCategories()
        dao.clearHelplineContacts()
        dao.clearNonDownloadedVideos()

        dao.clearAnimalImages()
        dao.clearAnimalDiseases()
        dao.clearAnimalHealth()
        dao.clearAnimalFeeding()
        dao.clearAnimalHousing()
        dao.clearAnimalBreeds()
        dao.clearAnimals()
        dao.clearAnimalCategories()

        dao.upsertCategories(
            bundle.cropCategories.map {
                CategoryEntity(
                    id = it.id,
                    nameNe = it.nameNe,
                    nameEn = it.nameEn,
                    iconName = it.iconName,
                    thumbnailUrl = it.thumbnailUrl,
                    sortOrder = it.sortOrder
                )
            }
        )
        dao.upsertCrops(
            bundle.crops.map {
                BakaiyaCropEntity(
                    id = it.id,
                    categoryId = it.categoryId ?: "",
                    nameNe = it.nameNe,
                    nameEn = it.nameEn,
                    scientificName = it.scientificName,
                    descriptionNe = it.descriptionNe,
                    varieties = it.varieties,
                    climateOverview = it.climateOverview,
                    growingConditions = GrowingConditionsEmbedded(
                        altitudeMinMeters = it.altitudeMinM,
                        altitudeOptimalMinMeters = it.altitudeOptMinM,
                        altitudeOptimalMaxMeters = it.altitudeOptMaxM,
                        altitudeMaxMeters = it.altitudeMaxM,
                        altitudeSummaryNe = it.suitableAltitude,
                        altitudeGrowthNotesNe = it.altitudeGrowthNotesNe,
                        suitableZonesCsv = it.suitableZonesCsv,
                        suitableRegionsNe = it.suitableRegionsNe,
                        soilTypesNe = it.soilTypes,
                        soilTextureNe = it.soilTextureNe,
                        soilPhMin = it.soilPhMin,
                        soilPhMax = it.soilPhMax,
                        soilDrainageNe = it.soilDrainageNe,
                        soilFertilityNe = it.soilFertilityNe,
                        soilOrganicMatterNe = it.soilOrganicMatterNe,
                        tempMin = it.tempMin,
                        tempOptimalMin = it.tempOptMin,
                        tempOptimalMax = it.tempOptMax,
                        tempMax = it.tempMax,
                        humidityMin = it.humidityMin,
                        humidityMax = it.humidityMax,
                        rainfallMinMm = it.rainfallMinMm,
                        rainfallMaxMm = it.rainfallMaxMm,
                        rainfallPeriodLabelNe = it.rainfallPeriodLabelNe,
                        waterRequirementNe = it.waterRequirement,
                        sunlightNe = it.sunlight,
                        plantingSeasonNe = it.plantingSeason,
                        sowingPeriodNe = it.sowingPeriodNe,
                        transplantingPeriodNe = it.transplantingPeriodNe,
                        growingDurationNe = it.growingDurationNe,
                        harvestingPeriodNe = it.harvestingPeriodNe,
                        irrigationGuidanceNe = it.irrigationManagement,
                        specialConsiderationsNe = it.specialConsiderationsNe,
                        lastUpdatedDate = it.lastUpdatedDate
                    ),
                    landPreparation = it.landPreparation,
                    seedSelection = it.seedSelection,
                    spacing = it.spacing,
                    fertilizer = it.fertilizerManagement,
                    cropCare = it.cropCare,
                    pestDiseaseControl = it.pestDiseaseControl,
                    harvesting = it.harvesting,
                    storage = it.storage,
                    sourceReference = it.sourceReference,
                    isVerifiedContent = it.isVerifiedContent,
                    thumbnailUrl = it.thumbnailUrl,
                    isPublished = it.isPublished
                )
            }
        )
        dao.upsertVarietyRequirements(
            bundle.cropVarieties.map {
                CropVarietyRequirementEntity(
                    id = it.id,
                    cropId = it.cropId,
                    cropNameNe = it.cropNameNe,
                    cropNameEn = it.cropNameEn,
                    varietyNameNe = it.varietyNameNe,
                    varietyNameEn = it.varietyNameEn,
                    notesNe = it.notesNe,
                    sourceReference = it.sourceReference,
                    growingConditions = GrowingConditionsEmbedded(
                        altitudeMinMeters = it.altitudeMinM,
                        altitudeOptimalMinMeters = it.altitudeOptMinM,
                        altitudeOptimalMaxMeters = it.altitudeOptMaxM,
                        altitudeMaxMeters = it.altitudeMaxM,
                        altitudeSummaryNe = it.suitableAltitude,
                        suitableZonesCsv = it.suitableZonesCsv,
                        suitableRegionsNe = it.suitableRegionsNe,
                        plantingSeasonNe = it.plantingSeason,
                        transplantingPeriodNe = it.transplantingPeriodNe,
                        growingDurationNe = it.growingDurationNe,
                        lastUpdatedDate = it.lastUpdatedDate
                    )
                )
            }
        )
        dao.upsertImages(
            bundle.cropImages.map {
                CropImageEntity(
                    id = it.id,
                    cropId = it.cropId,
                    imageUrl = it.imageUrl,
                    captionNe = it.captionNe,
                    stageCategory = it.stageCategory,
                    isSampleImage = it.isSampleImage
                )
            }
        )
        dao.insertVideosIgnoreConflict(
            bundle.videos.map {
                EducationalVideoEntity(
                    id = it.id,
                    cropId = it.cropId,
                    topicId = it.topicId,
                    titleNe = it.titleNe,
                    descriptionNe = it.descriptionNe,
                    channelName = it.channelName,
                    thumbnailUrl = it.thumbnailUrl,
                    videoUrl = it.videoUrl,
                    duration = it.duration,
                    provider = it.provider,
                    downloadPermission = it.downloadPermission,
                    downloadableFileUrl = it.downloadableFileUrl,
                    isSampleMedia = it.isSampleMedia
                )
            }
        )
        dao.upsertContacts(
            bundle.helplineContacts.map {
                HelplineContactEntity(
                    id = it.id,
                    nameNe = it.nameNe,
                    designationNe = it.designationNe,
                    wardNe = it.wardNe,
                    wardNumber = it.wardNumber,
                    mobileNumber = it.mobileNumber,
                    email = it.email,
                    isVerified = it.isVerified
                )
            }
        )

        dao.upsertAnimalCategories(
            bundle.animalCategories.map {
                AnimalCategoryEntity(
                    id = it.id,
                    nameNe = it.nameNe,
                    nameEn = it.nameEn,
                    descriptionNe = it.descriptionNe,
                    iconName = it.iconName,
                    thumbnailUrl = it.thumbnailUrl,
                    sortOrder = it.sortOrder
                )
            }
        )
        dao.upsertAnimals(
            bundle.animals.map {
                AnimalEntity(
                    id = it.id,
                    categoryId = it.categoryId,
                    nameNe = it.nameNe,
                    nameEn = it.nameEn,
                    scientificName = it.scientificName,
                    speciesCode = it.speciesCode,
                    summaryNe = it.summaryNe,
                    purposeTagsNe = it.purposeTagsNe,
                    bakaiyaRelevanceNe = it.bakaiyaRelevanceNe,
                    climateSuitabilityNe = it.climateSuitabilityNe,
                    altitudeRangeNe = it.altitudeRangeNe,
                    recommendedBreedsSummaryNe = it.recommendedBreedsSummaryNe,
                    safetyNoticeNe = it.safetyNoticeNe.ifBlank {
                        "पशु स्वास्थ्य वा औषधि मात्रा सम्बन्धी अन्तिम निर्णय नजिकको पशु सेवा प्राविधिक वा पशु चिकित्सकको प्रत्यक्ष जाँचपछि मात्र लिनुहोस्।"
                    },
                    sourceReference = it.sourceReference,
                    thumbnailUrl = it.thumbnailUrl,
                    reviewStatus = it.reviewStatus,
                    isVerifiedContent = it.isVerifiedContent,
                    isPublished = it.isPublished,
                    sortOrder = it.sortOrder
                )
            }
        )
        dao.upsertAnimalBreeds(
            bundle.animalBreeds.map {
                AnimalBreedEntity(
                    id = it.id,
                    animalId = it.animalId,
                    breedNameNe = it.breedNameNe,
                    breedNameEn = it.breedNameEn,
                    originType = it.originType,
                    purposeNe = it.purposeNe,
                    climateSuitabilityNe = it.climateSuitabilityNe,
                    characteristicsNe = it.characteristicsNe,
                    notesNe = it.notesNe,
                    sourceReference = it.sourceReference,
                    sortOrder = it.sortOrder
                )
            }
        )
        dao.upsertAnimalHousing(
            bundle.animalHousing.map {
                AnimalHousingEntity(
                    id = it.id,
                    animalId = it.animalId,
                    shedDesignNe = it.shedDesignNe,
                    floorSpaceStandardNe = it.floorSpaceStandardNe,
                    ventilationLightNe = it.ventilationLightNe,
                    beddingDrainageNe = it.beddingDrainageNe,
                    sanitationRoutineNe = it.sanitationRoutineNe,
                    biosecurityNotesNe = it.biosecurityNotesNe,
                    sourceReference = it.sourceReference
                )
            }
        )
        dao.upsertAnimalFeeding(
            bundle.animalFeeding.map {
                AnimalFeedingEntity(
                    id = it.id,
                    animalId = it.animalId,
                    greenFodderForageNe = it.greenFodderForageNe,
                    dryRoughageNe = it.dryRoughageNe,
                    concentrateFeedNe = it.concentrateFeedNe,
                    mineralSaltNe = it.mineralSaltNe,
                    cleanWaterNe = it.cleanWaterNe,
                    rationQuantityNoteNe = it.rationQuantityNoteNe,
                    feedingFrequencyNe = it.feedingFrequencyNe,
                    sourceReference = it.sourceReference
                )
            }
        )
        dao.upsertAnimalHealth(
            bundle.animalHealth.map {
                AnimalHealthEntity(
                    id = it.id,
                    animalId = it.animalId,
                    dailyHygieneNe = it.dailyHygieneNe,
                    biosecurityIsolationNe = it.biosecurityIsolationNe,
                    vaccinationAwarenessNe = it.vaccinationAwarenessNe,
                    dewormingParasiteControlNe = it.dewormingParasiteControlNe,
                    warningSignsNe = it.warningSignsNe,
                    vetReferralGuidanceNe = it.vetReferralGuidanceNe,
                    sourceReference = it.sourceReference
                )
            }
        )
        dao.upsertAnimalDiseases(
            bundle.animalDiseases.map {
                AnimalDiseaseEntity(
                    id = it.id,
                    animalId = it.animalId,
                    nameNe = it.nameNe,
                    nameEn = it.nameEn,
                    pathogenType = it.pathogenType,
                    symptomsNe = it.symptomsNe,
                    preventionBiosecurityNe = it.preventionBiosecurityNe,
                    immediateFarmerActionNe = it.immediateFarmerActionNe,
                    veterinaryCautionNe = it.veterinaryCautionNe,
                    sourceReference = it.sourceReference,
                    sortOrder = it.sortOrder
                )
            }
        )
        dao.upsertAnimalImages(
            bundle.animalImages.map {
                AnimalImageEntity(
                    id = it.id,
                    animalId = it.animalId,
                    imageUrl = it.imageUrl,
                    mediumUrl = it.mediumUrl,
                    thumbUrl = it.thumbUrl,
                    captionNe = it.captionNe,
                    stageCategory = it.stageCategory,
                    creatorName = it.creatorName,
                    sourceUrl = it.sourceUrl,
                    licenseType = it.licenseType,
                    sortOrder = it.sortOrder
                )
            }
        )
    }

    private fun EducationalVideoEntity.toSyncedDomain(): EducationalVideo {
        val fileExists = isDownloaded && localFilePath?.let { File(it).exists() } == true
        return toDomain().copy(
            isDownloaded = fileExists,
            localFilePath = if (fileExists) localFilePath else null
        )
    }

    private fun CategoryEntity.toDomain() =
        CropCategory(id, nameNe, nameEn, iconName, thumbnailUrl)

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
            toleVillage = toleVillage,
            consentStatus = consentStatus,
            registeredAt = registeredAt,
            syncStatus = runCatching { RegistrationSyncStatus.valueOf(syncStatus) }
                .getOrDefault(RegistrationSyncStatus.PENDING)
        )

    // ---- Livestock / पशुपालन repository methods (Room-backed from Supabase) ----

    override suspend fun getAnimalCategories(): List<AnimalCategory> =
        withContext(dispatchers.io) {
            dao.getAllAnimalCategoriesSync().map {
                AnimalCategory(
                    id = it.id,
                    nameNe = it.nameNe,
                    nameEn = it.nameEn,
                    descriptionNe = it.descriptionNe,
                    iconName = it.iconName,
                    thumbnailUrl = it.thumbnailUrl,
                    sortOrder = it.sortOrder
                )
            }
        }

    override suspend fun getAllAnimals(): List<Animal> =
        withContext(dispatchers.io) {
            dao.getPublishedAnimalsSync().map { it.toDomain() }
        }

    override suspend fun getAnimalsByCategory(categoryId: String): List<Animal> =
        withContext(dispatchers.io) {
            dao.getPublishedAnimalsByCategorySync(categoryId).map { it.toDomain() }
        }

    override suspend fun getAnimalById(animalId: String): Animal? =
        withContext(dispatchers.io) {
            dao.getPublishedAnimalByIdSync(animalId)?.toDomain()
        }

    override suspend fun getBreedsForAnimal(animalId: String): List<AnimalBreed> =
        withContext(dispatchers.io) {
            dao.getBreedsForAnimalSync(animalId).map {
                AnimalBreed(
                    id = it.id,
                    animalId = it.animalId,
                    breedNameNe = it.breedNameNe,
                    breedNameEn = it.breedNameEn,
                    originType = it.originType,
                    purposeNe = it.purposeNe,
                    climateSuitabilityNe = it.climateSuitabilityNe,
                    characteristicsNe = it.characteristicsNe,
                    notesNe = it.notesNe,
                    sourceReference = it.sourceReference,
                    sortOrder = it.sortOrder
                )
            }
        }

    override suspend fun getHousingForAnimal(animalId: String): AnimalHousingGuidance? =
        withContext(dispatchers.io) {
            dao.getHousingForAnimalSync(animalId)?.let {
                AnimalHousingGuidance(
                    id = it.id,
                    animalId = it.animalId,
                    shedDesignNe = it.shedDesignNe,
                    floorSpaceStandardNe = it.floorSpaceStandardNe,
                    ventilationLightNe = it.ventilationLightNe,
                    beddingDrainageNe = it.beddingDrainageNe,
                    sanitationRoutineNe = it.sanitationRoutineNe,
                    biosecurityNotesNe = it.biosecurityNotesNe,
                    sourceReference = it.sourceReference
                )
            }
        }

    override suspend fun getFeedingForAnimal(animalId: String): AnimalFeedingGuidance? =
        withContext(dispatchers.io) {
            dao.getFeedingForAnimalSync(animalId)?.let {
                AnimalFeedingGuidance(
                    id = it.id,
                    animalId = it.animalId,
                    greenFodderForageNe = it.greenFodderForageNe,
                    dryRoughageNe = it.dryRoughageNe,
                    concentrateFeedNe = it.concentrateFeedNe,
                    mineralSaltNe = it.mineralSaltNe,
                    cleanWaterNe = it.cleanWaterNe,
                    rationQuantityNoteNe = it.rationQuantityNoteNe,
                    feedingFrequencyNe = it.feedingFrequencyNe,
                    sourceReference = it.sourceReference
                )
            }
        }

    override suspend fun getHealthForAnimal(animalId: String): AnimalHealthGuidance? =
        withContext(dispatchers.io) {
            dao.getHealthForAnimalSync(animalId)?.let {
                AnimalHealthGuidance(
                    id = it.id,
                    animalId = it.animalId,
                    dailyHygieneNe = it.dailyHygieneNe,
                    biosecurityIsolationNe = it.biosecurityIsolationNe,
                    vaccinationAwarenessNe = it.vaccinationAwarenessNe,
                    dewormingParasiteControlNe = it.dewormingParasiteControlNe,
                    warningSignsNe = it.warningSignsNe,
                    vetReferralGuidanceNe = it.vetReferralGuidanceNe,
                    sourceReference = it.sourceReference
                )
            }
        }

    override suspend fun getDiseasesForAnimal(animalId: String): List<AnimalDisease> =
        withContext(dispatchers.io) {
            dao.getDiseasesForAnimalSync(animalId).map {
                AnimalDisease(
                    id = it.id,
                    animalId = it.animalId,
                    nameNe = it.nameNe,
                    nameEn = it.nameEn,
                    pathogenType = it.pathogenType,
                    symptomsNe = it.symptomsNe,
                    preventionBiosecurityNe = it.preventionBiosecurityNe,
                    immediateFarmerActionNe = it.immediateFarmerActionNe,
                    veterinaryCautionNe = it.veterinaryCautionNe,
                    sourceReference = it.sourceReference,
                    sortOrder = it.sortOrder
                )
            }
        }

    override suspend fun getImagesForAnimal(animalId: String): List<AnimalImage> =
        withContext(dispatchers.io) {
            dao.getImagesForAnimalSync(animalId).map {
                AnimalImage(
                    id = it.id,
                    animalId = it.animalId,
                    imageUrl = it.imageUrl,
                    mediumUrl = it.mediumUrl,
                    thumbUrl = it.thumbUrl,
                    captionNe = it.captionNe,
                    stageCategory = it.stageCategory,
                    creatorName = it.creatorName,
                    sourceUrl = it.sourceUrl,
                    licenseType = it.licenseType,
                    sortOrder = it.sortOrder
                )
            }
        }

    override suspend fun getVideosForAnimal(animalId: String): List<EducationalVideo> =
        withContext(dispatchers.io) {
            dao.getVideosForTargetSync(animalId).map { it.toSyncedDomain() }
        }

    private fun AnimalEntity.toDomain() = Animal(
        id = id,
        categoryId = categoryId,
        nameNe = nameNe,
        nameEn = nameEn,
        scientificName = scientificName,
        speciesCode = speciesCode,
        summaryNe = summaryNe,
        purposeTagsNe = purposeTagsNe,
        bakaiyaRelevanceNe = bakaiyaRelevanceNe,
        climateSuitabilityNe = climateSuitabilityNe,
        altitudeRangeNe = altitudeRangeNe,
        recommendedBreedsSummaryNe = recommendedBreedsSummaryNe,
        safetyNoticeNe = safetyNoticeNe,
        sourceReference = sourceReference,
        thumbnailUrl = thumbnailUrl,
        reviewStatus = reviewStatus,
        isVerifiedContent = isVerifiedContent,
        isPublished = isPublished,
        sortOrder = sortOrder
    )
}
