package com.krishisaathi.app.data.repository

import com.krishisaathi.app.core.common.DispatcherProvider
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.data.local.BakaiyaCropDao
import com.krishisaathi.app.data.local.BakaiyaCropEntity
import com.krishisaathi.app.data.local.CategoryEntity
import com.krishisaathi.app.data.local.CropImageEntity
import com.krishisaathi.app.data.local.EducationalVideoEntity
import com.krishisaathi.app.data.local.FarmerRegistrationEntity
import com.krishisaathi.app.data.local.HelplineContactEntity
import com.krishisaathi.app.data.local.KrishiSaathiDatabase
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.model.CropImage
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.model.HelplineContact
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class BakaiyaCropRepositoryImpl(
    private val dao: BakaiyaCropDao,
    private val dispatchers: DispatcherProvider
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
            KrishiSaathiDatabase.seedDatabase(dao)
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

    private fun BakaiyaCropEntity.toDomain() = BakaiyaCrop(
        id = id,
        categoryId = categoryId,
        nameNe = nameNe,
        nameEn = nameEn,
        scientificName = scientificName,
        descriptionNe = descriptionNe,
        varieties = varieties,
        plantingSeason = plantingSeason,
        climateOverview = climateOverview,
        altitude = altitude,
        tempMin = tempMin,
        tempMax = tempMax,
        humidityMin = humidityMin,
        humidityMax = humidityMax,
        soilTypes = soilTypes,
        soilPhMin = soilPhMin,
        soilPhMax = soilPhMax,
        sunlight = sunlight,
        waterRequirement = waterRequirement,
        landPreparation = landPreparation,
        seedSelection = seedSelection,
        spacing = spacing,
        fertilizer = fertilizer,
        irrigation = irrigation,
        cropCare = cropCare,
        pestDiseaseControl = pestDiseaseControl,
        harvesting = harvesting,
        storage = storage,
        sourceReference = sourceReference,
        isVerifiedContent = isVerifiedContent,
        thumbnailUrl = thumbnailUrl,
        isPublished = isPublished
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
}
