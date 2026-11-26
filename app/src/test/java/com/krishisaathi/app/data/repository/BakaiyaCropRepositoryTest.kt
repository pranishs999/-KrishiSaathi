package com.krishisaathi.app.data.repository

import com.krishisaathi.app.core.common.DispatcherProvider
import com.krishisaathi.app.data.local.BakaiyaCropDao
import com.krishisaathi.app.data.local.BakaiyaCropEntity
import com.krishisaathi.app.data.local.CategoryEntity
import com.krishisaathi.app.data.local.CropImageEntity
import com.krishisaathi.app.data.local.CropVarietyRequirementEntity
import com.krishisaathi.app.data.local.EducationalVideoEntity
import com.krishisaathi.app.data.local.FarmerRegistrationEntity
import com.krishisaathi.app.data.local.HelplineContactEntity
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BakaiyaCropRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun syncPopulatesAllCollectionsAndSearchWorksForNepaliAndEnglish() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers)

        val syncResult = repository.syncRemoteContent()
        assertTrue(syncResult.isSuccess)

        val categories = repository.getCategories().first()
        assertEquals(6, categories.size)

        val paddyMatches = repository.searchCrops("धान").first()
        assertTrue(paddyMatches.any { it.id == "crop-paddy" })

        val maizeMatches = repository.searchCrops("Maize").first()
        assertTrue(maizeMatches.any { it.id == "crop-maize" })

        val noneMatches = repository.searchCrops("xyznonexistent").first()
        assertTrue(noneMatches.isEmpty())

        val paddyVarieties = repository.getVarietyRequirementsForCrop("crop-paddy").first()
        assertTrue(paddyVarieties.any { it.varietyNameEn == "Savitri" })
    }

    @Test
    fun categoryFilteringReturnsMatchingCropsOnly() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers)
        repository.syncRemoteContent()

        val vegetables = repository.getCropsByCategory("cat-vegetable").first()
        assertTrue(vegetables.isNotEmpty())
        assertTrue(vegetables.all { it.categoryId == "cat-vegetable" })
        assertTrue(vegetables.any { it.id == "crop-cauliflower" })
        assertTrue(vegetables.any { it.id == "crop-tomato" })
    }

    @Test
    fun offlineVideoVerificationRequiresPhysicalFileExistenceAndAuthorizedPermission() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers)
        repository.syncRemoteContent()

        // 1. Unauthorized YouTube video cannot be marked downloaded
        repository.markVideoDownloaded("vid-cauli-aitc", "/tmp/fake.mp4", 1234L)
        val afterUnauthorized = repository.getDownloadedVideos().first()
        assertTrue(afterUnauthorized.isEmpty())

        // 2. Authorized video pointing to non-existent file is filtered out
        repository.markVideoDownloaded("vid-paddy-sample-offline", "/nonexistent/path/video.mp4", 1024L)
        assertTrue(repository.getDownloadedVideos().first().isEmpty())

        // 3. Authorized video pointing to real physical file appears in library & deletes cleanly
        val realFile = tempFolder.newFile("vid-paddy-sample-offline.mp4").apply {
            writeBytes(ByteArray(2048) { 1 })
        }
        repository.markVideoDownloaded("vid-paddy-sample-offline", realFile.absolutePath, realFile.length())

        val downloaded = repository.getDownloadedVideos().first()
        assertEquals(1, downloaded.size)
        assertEquals("vid-paddy-sample-offline", downloaded.first().id)
        assertEquals(2048L, downloaded.first().fileSizeBytes)

        repository.deleteDownloadedVideo("vid-paddy-sample-offline")
        assertTrue(repository.getDownloadedVideos().first().isEmpty())
        assertTrue(!realFile.exists())
    }

    @Test
    fun farmerRegistrationPersistsValidDataAndRejectsInvalidInputs() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers)

        assertNull(repository.getFarmerRegistration().first())

        assertTrue(repository.saveFarmerRegistration(" ", 5, "9841234567").isFailure)
        assertTrue(repository.saveFarmerRegistration("राम बहादुर", 15, "9841234567").isFailure)
        assertTrue(repository.saveFarmerRegistration("राम बहादुर", 5, "12345").isFailure)
        assertNull(repository.getFarmerRegistration().first())

        val success = repository.saveFarmerRegistration("  राम बहादुर  ", 5, " +977 9841234567 ")
        assertTrue(success.isSuccess)

        val saved = repository.getFarmerRegistration().first()
        assertNotNull(saved)
        assertEquals("राम बहादुर", saved?.fullName)
        assertEquals(BakaiyaMunicipality.DEFAULT_NAME, saved?.municipality)
        assertEquals(5, saved?.wardNumber)
        assertEquals("9841234567", saved?.phoneNumber)
        assertTrue((saved?.registeredAt ?: 0L) > 0L)

        repository.clearFarmerRegistration()
        assertNull(repository.getFarmerRegistration().first())
    }

    @Test
    fun livestockRepositoryFiltersOutUnpublishedSpeciesAndReturnsVerifiedGuidance() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers)

        val categories = repository.getAnimalCategories()
        assertEquals(4, categories.size)

        val publishedAnimals = repository.getAllAnimals()
        assertEquals(6, publishedAnimals.size)
        assertTrue(publishedAnimals.all { it.isPublished })
        assertNull(repository.getAnimalById("animal-sheep"))
        assertNull(repository.getAnimalById("animal-duck"))

        val goat = repository.getAnimalById("animal-goat")
        assertNotNull(goat)
        assertEquals("बाख्रापालन", goat?.nameNe)
        assertTrue(repository.getBreedsForAnimal("animal-goat").isNotEmpty())
        assertNotNull(repository.getHousingForAnimal("animal-goat"))
        assertNotNull(repository.getFeedingForAnimal("animal-goat"))
        assertNotNull(repository.getHealthForAnimal("animal-goat"))
        assertTrue(repository.getDiseasesForAnimal("animal-goat").isNotEmpty())
        assertTrue(repository.getImagesForAnimal("animal-goat").isNotEmpty())
    }

    private class FakeBakaiyaCropDao : BakaiyaCropDao {
        private val categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
        private val crops = MutableStateFlow<List<BakaiyaCropEntity>>(emptyList())
        private val varietyRequirements = MutableStateFlow<List<CropVarietyRequirementEntity>>(emptyList())
        private val images = MutableStateFlow<List<CropImageEntity>>(emptyList())
        private val videos = MutableStateFlow<List<EducationalVideoEntity>>(emptyList())
        private val contacts = MutableStateFlow<List<HelplineContactEntity>>(emptyList())
        private val registration = MutableStateFlow<FarmerRegistrationEntity?>(null)

        override fun getAllCategories(): Flow<List<CategoryEntity>> = categories

        override fun upsertCategories(categories: List<CategoryEntity>): List<Long> {
            this.categories.value = categories
            return categories.mapIndexed { i, _ -> i.toLong() }
        }

        override fun countPublishedCropsSync(): Int = crops.value.count { it.isPublished }

        override fun getAllCrops(): Flow<List<BakaiyaCropEntity>> = crops

        override fun getCropById(cropId: String): Flow<BakaiyaCropEntity?> =
            crops.map { list -> list.find { it.id == cropId } }

        override fun getCropsByCategory(categoryId: String): Flow<List<BakaiyaCropEntity>> =
            crops.map { list -> list.filter { it.categoryId == categoryId } }

        override fun searchCrops(query: String): Flow<List<BakaiyaCropEntity>> =
            crops.map { list ->
                list.filter {
                    it.nameNe.contains(query, ignoreCase = true) ||
                            it.nameEn.contains(query, ignoreCase = true) ||
                            it.scientificName.contains(query, ignoreCase = true)
                }
            }

        override fun upsertCrops(crops: List<BakaiyaCropEntity>): List<Long> {
            this.crops.value = crops
            return crops.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getVarietyRequirementsForCrop(cropId: String): Flow<List<CropVarietyRequirementEntity>> =
            varietyRequirements.map { list -> list.filter { it.cropId == cropId } }

        override fun upsertVarietyRequirements(varieties: List<CropVarietyRequirementEntity>): List<Long> {
            this.varietyRequirements.value = varieties
            return varieties.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getImagesForCrop(cropId: String): Flow<List<CropImageEntity>> =
            images.map { list -> list.filter { it.cropId == cropId } }

        override fun upsertImages(images: List<CropImageEntity>): List<Long> {
            this.images.value = images
            return images.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getVideosForCrop(cropId: String): Flow<List<EducationalVideoEntity>> =
            videos.map { list -> list.filter { it.cropId == cropId } }

        override fun getVideosForCropAndTopic(cropId: String, topicId: Int): Flow<List<EducationalVideoEntity>> =
            videos.map { list -> list.filter { it.cropId == cropId && it.topicId == topicId } }

        override fun getDownloadedVideos(): Flow<List<EducationalVideoEntity>> =
            videos.map { list -> list.filter { it.isDownloaded } }

        override fun getVideoById(videoId: String): Flow<EducationalVideoEntity?> =
            videos.map { list -> list.find { it.id == videoId } }

        override fun findVideoByIdSync(videoId: String): EducationalVideoEntity? =
            videos.value.find { it.id == videoId }

        override fun insertVideosIgnoreConflict(videos: List<EducationalVideoEntity>): List<Long> {
            val currentMap = this.videos.value.associateBy { it.id }.toMutableMap()
            videos.forEach { vid ->
                if (!currentMap.containsKey(vid.id)) currentMap[vid.id] = vid
            }
            this.videos.value = currentMap.values.toList()
            return videos.mapIndexed { i, _ -> i.toLong() }
        }

        override fun upsertVideos(videos: List<EducationalVideoEntity>): List<Long> {
            this.videos.value = videos
            return videos.mapIndexed { i, _ -> i.toLong() }
        }

        override fun updateVideo(video: EducationalVideoEntity): Int {
            this.videos.value = this.videos.value.map { if (it.id == video.id) video else it }
            return 1
        }

        override fun markVideoDeleted(videoId: String): Int {
            this.videos.value = this.videos.value.map {
                if (it.id == videoId) it.copy(isDownloaded = false, localFilePath = null, fileSizeBytes = null)
                else it
            }
            return 1
        }

        override fun getAllHelplineContacts(): Flow<List<HelplineContactEntity>> = contacts

        override fun getContactsByWard(wardNumber: Int): Flow<List<HelplineContactEntity>> =
            contacts.map { list -> list.filter { it.wardNumber == wardNumber } }

        override fun upsertContacts(contacts: List<HelplineContactEntity>): List<Long> {
            this.contacts.value = contacts
            return contacts.mapIndexed { i, _ -> i.toLong() }
        }

        override fun clearHelplineContacts(): Int {
            this.contacts.value = emptyList()
            return 1
        }

        override fun getFarmerRegistration(): Flow<FarmerRegistrationEntity?> = registration

        override fun findFarmerRegistrationSync(): FarmerRegistrationEntity? = registration.value

        override fun saveFarmerRegistration(registration: FarmerRegistrationEntity): Long {
            this.registration.value = registration
            return 1L
        }

        override fun clearFarmerRegistration(): Int {
            this.registration.value = null
            return 1
        }
    }
}
