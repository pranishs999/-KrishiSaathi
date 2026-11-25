package com.krishisaathi.app.data.repository

import com.krishisaathi.app.core.common.DispatcherProvider
import com.krishisaathi.app.data.local.BakaiyaCropDao
import com.krishisaathi.app.data.local.BakaiyaCropEntity
import com.krishisaathi.app.data.local.CategoryEntity
import com.krishisaathi.app.data.local.CropImageEntity
import com.krishisaathi.app.data.local.EducationalVideoEntity
import com.krishisaathi.app.data.local.FarmRecordEntity
import com.krishisaathi.app.data.local.FarmerRegistrationEntity
import com.krishisaathi.app.data.local.HelplineContactEntity
import com.krishisaathi.app.domain.model.FarmRecord
import com.krishisaathi.app.domain.model.FarmRecordType
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
    fun optionalRegistrationPersistsAndClearsWithoutAuth() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers)

        assertNull(repository.getFarmerRegistration().first())

        repository.saveFarmerRegistration("  राम बहादुर  ", 5, " 9841234567 ")
        val saved = repository.getFarmerRegistration().first()
        assertNotNull(saved)
        assertEquals("राम बहादुर", saved?.fullName)
        assertEquals(5, saved?.wardNumber)
        assertEquals("9841234567", saved?.phoneNumber)

        repository.clearFarmerRegistration()
        assertNull(repository.getFarmerRegistration().first())
    }

    @Test
    fun farmRecordCrudValidatesComputesTotalsAndDeletes() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers)

        // Invalid record should fail cleanly
        val invalidResult = repository.addFarmRecord(
            FarmRecord(
                id = "rec-invalid",
                type = FarmRecordType.EXPENSE,
                categoryNe = " ",
                amount = 0.0,
                descriptionNe = "",
                cropNameNe = null,
                dateMillis = 1000L,
                createdAt = 1000L
            )
        )
        assertTrue(invalidResult.isFailure)

        val expense = FarmRecord(
            id = "rec-1",
            type = FarmRecordType.EXPENSE,
            categoryNe = "मलखाद",
            amount = 1500.0,
            descriptionNe = "डीएपी खरिद",
            cropNameNe = "धान",
            dateMillis = 2000L,
            createdAt = 2000L
        )
        val income = FarmRecord(
            id = "rec-2",
            type = FarmRecordType.INCOME,
            categoryNe = "बाली बिक्री",
            amount = 5200.0,
            descriptionNe = "धान बिक्री",
            cropNameNe = "धान",
            dateMillis = 3000L,
            createdAt = 3000L
        )

        assertTrue(repository.addFarmRecord(expense).isSuccess)
        assertTrue(repository.addFarmRecord(income).isSuccess)

        assertEquals(2, repository.getAllFarmRecords().first().size)
        assertEquals(1, repository.getFarmRecordsByType(FarmRecordType.INCOME).first().size)
        assertEquals(5200.0, repository.getTotalIncome().first(), 0.001)
        assertEquals(1500.0, repository.getTotalExpense().first(), 0.001)

        repository.deleteFarmRecord("rec-1")
        assertEquals(1, repository.getAllFarmRecords().first().size)
        assertEquals(0.0, repository.getTotalExpense().first(), 0.001)
    }

    private class FakeBakaiyaCropDao : BakaiyaCropDao {
        private val categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
        private val crops = MutableStateFlow<List<BakaiyaCropEntity>>(emptyList())
        private val images = MutableStateFlow<List<CropImageEntity>>(emptyList())
        private val videos = MutableStateFlow<List<EducationalVideoEntity>>(emptyList())
        private val contacts = MutableStateFlow<List<HelplineContactEntity>>(emptyList())
        private val registration = MutableStateFlow<FarmerRegistrationEntity?>(null)
        private val farmRecords = MutableStateFlow<List<FarmRecordEntity>>(emptyList())

        override fun getAllCategories(): Flow<List<CategoryEntity>> = categories

        override fun upsertCategories(categories: List<CategoryEntity>): List<Long> {
            this.categories.value = categories
            return categories.mapIndexed { i, _ -> i.toLong() }
        }

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

        override fun saveFarmerRegistration(registration: FarmerRegistrationEntity): Long {
            this.registration.value = registration
            return 1L
        }

        override fun clearFarmerRegistration(): Int {
            this.registration.value = null
            return 1
        }

        override fun getAllFarmRecords(): Flow<List<FarmRecordEntity>> =
            farmRecords.map { list -> list.sortedByDescending { it.dateMillis } }

        override fun getFarmRecordsByType(type: String): Flow<List<FarmRecordEntity>> =
            farmRecords.map { list -> list.filter { it.type == type }.sortedByDescending { it.dateMillis } }

        override fun insertFarmRecord(record: FarmRecordEntity): Long {
            val current = farmRecords.value.filterNot { it.id == record.id } + record
            farmRecords.value = current
            return 1L
        }

        override fun deleteFarmRecord(recordId: String): Int {
            val before = farmRecords.value.size
            farmRecords.value = farmRecords.value.filterNot { it.id == recordId }
            return before - farmRecords.value.size
        }

        override fun getTotalIncome(): Flow<Double> =
            farmRecords.map { list -> list.filter { it.type == "income" }.sumOf { it.amount } }

        override fun getTotalExpense(): Flow<Double> =
            farmRecords.map { list -> list.filter { it.type == "expense" }.sumOf { it.amount } }
    }
}
