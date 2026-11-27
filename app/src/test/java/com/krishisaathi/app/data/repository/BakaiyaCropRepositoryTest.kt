package com.krishisaathi.app.data.repository

import com.krishisaathi.app.core.common.DispatcherProvider
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
import com.krishisaathi.app.data.local.HelplineContactEntity
import com.krishisaathi.app.data.remote.AppSyncController
import com.krishisaathi.app.data.remote.RegistrationUploadResult
import com.krishisaathi.app.data.remote.RemoteContentSyncClient
import com.krishisaathi.app.data.remote.RemoteSyncBundle
import com.krishisaathi.app.data.remote.SupabaseAnimalBreedDto
import com.krishisaathi.app.data.remote.SupabaseAnimalCategoryDto
import com.krishisaathi.app.data.remote.SupabaseAnimalDiseaseDto
import com.krishisaathi.app.data.remote.SupabaseAnimalDto
import com.krishisaathi.app.data.remote.SupabaseAnimalFeedingDto
import com.krishisaathi.app.data.remote.SupabaseAnimalHealthDto
import com.krishisaathi.app.data.remote.SupabaseAnimalHousingDto
import com.krishisaathi.app.data.remote.SupabaseAnimalImageDto
import com.krishisaathi.app.data.remote.SupabaseCropCategoryDto
import com.krishisaathi.app.data.remote.SupabaseCropDto
import com.krishisaathi.app.data.remote.SupabaseCropImageDto
import com.krishisaathi.app.data.remote.SupabaseCropVarietyDto
import com.krishisaathi.app.data.remote.SupabaseEducationalVideoDto
import com.krishisaathi.app.data.remote.SupabaseHelplineDto
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.model.RegistrationSyncStatus
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
import java.io.IOException

class BakaiyaCropRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
    }

    private fun sampleBundle(): RemoteSyncBundle = RemoteSyncBundle(
        cropCategories = listOf(
            SupabaseCropCategoryDto("cat-cereal", "खाद्यान्न बाली", "Cereals", sortOrder = 1),
            SupabaseCropCategoryDto("cat-vegetable", "तरकारी बाली", "Vegetables", sortOrder = 2),
            SupabaseCropCategoryDto("cat-fruit", "फलफूल बाली", "Fruits", sortOrder = 3),
            SupabaseCropCategoryDto("cat-pulse", "दलहन बाली", "Pulses", sortOrder = 4),
            SupabaseCropCategoryDto("cat-oilseed", "तेलहन बाली", "Oilseeds", sortOrder = 5),
            SupabaseCropCategoryDto("cat-cash-spice", "मसला तथा नगदे बाली", "Spices & Cash Crops", sortOrder = 6)
        ),
        crops = listOf(
            SupabaseCropDto(
                id = "crop-paddy",
                categoryId = "cat-cereal",
                nameNe = "धान",
                nameEn = "Paddy (Rice)",
                scientificName = "Oryza sativa",
                descriptionNe = "बकैयाको तल्लो बेसी तथा बगर क्षेत्रमा मुख्य खाद्यान्न बाली।",
                plantingSeason = "जेठ - असार",
                sourceReference = "NARC Rice Varietal Guide",
                thumbnailUrl = "https://example.org/paddy.jpg"
            ),
            SupabaseCropDto(
                id = "crop-maize",
                categoryId = "cat-cereal",
                nameNe = "मकै",
                nameEn = "Maize",
                scientificName = "Zea mays",
                descriptionNe = "बकैयाको बारी क्षेत्रमा उपयुक्त बाली।",
                sourceReference = "NARC Maize Guide"
            ),
            SupabaseCropDto(
                id = "crop-cauliflower",
                categoryId = "cat-vegetable",
                nameNe = "काउली",
                nameEn = "Cauliflower",
                scientificName = "Brassica oleracea var. botrytis",
                descriptionNe = "हिउँदे तरकारी बाली।",
                sourceReference = "NARC Vegetable Guide"
            ),
            SupabaseCropDto(
                id = "crop-tomato",
                categoryId = "cat-vegetable",
                nameNe = "गोलभेंडा",
                nameEn = "Tomato",
                scientificName = "Solanum lycopersicum",
                descriptionNe = "व्यवसायिक तरकारी खेती।",
                sourceReference = "NARC Vegetable Guide"
            )
        ),
        cropVarieties = listOf(
            SupabaseCropVarietyDto(
                id = "var-paddy-savitri",
                cropId = "crop-paddy",
                cropNameNe = "धान",
                cropNameEn = "Paddy",
                varietyNameNe = "सावित्री",
                varietyNameEn = "Savitri",
                sourceReference = "NARC"
            )
        ),
        cropImages = listOf(
            SupabaseCropImageDto(
                id = "img-paddy-1",
                cropId = "crop-paddy",
                imageUrl = "https://example.org/paddy.jpg",
                captionNe = "धान खेती",
                stageCategory = "field"
            )
        ),
        videos = listOf(
            SupabaseEducationalVideoDto(
                id = "vid-cauli-aitc",
                cropId = "crop-cauliflower",
                topicId = 1,
                titleNe = "काउली नर्सरी प्रविधि",
                descriptionNe = "युट्युब शैक्षिक सामग्री",
                channelName = "AITC Nepal",
                thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
                videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                provider = "youtube",
                downloadPermission = false,
                downloadableFileUrl = null
            ),
            SupabaseEducationalVideoDto(
                id = "vid-paddy-sample-offline",
                cropId = "crop-paddy",
                topicId = 2,
                titleNe = "धान रोपाइँ प्रविधि",
                descriptionNe = "अफलाइन सुरक्षित भिडियो",
                channelName = "Bakaiya Krishi",
                thumbnailUrl = "https://example.org/thumb.jpg",
                videoUrl = "https://example.org/video.mp4",
                provider = "mp4",
                downloadPermission = true,
                downloadableFileUrl = "https://example.org/video.mp4"
            )
        ),
        helplineContacts = listOf(
            SupabaseHelplineDto(
                id = "help-1",
                nameNe = "बकैया गाउँपालिका कृषि शाखा",
                designationNe = "कृषि अधिकृत",
                wardNe = "गाउँपालिका कार्यालय",
                wardNumber = 4,
                mobileNumber = "9855000001",
                email = "agri@bakaiya.gov.np",
                isVerified = true
            )
        ),
        animalCategories = listOf(
            SupabaseAnimalCategoryDto("acat-cattle-buffalo", "गाई तथा भैँसी", "Cattle & Buffalo", sortOrder = 1),
            SupabaseAnimalCategoryDto("acat-goat", "बाख्रा तथा भेडा", "Goat & Sheep", sortOrder = 2),
            SupabaseAnimalCategoryDto("acat-poultry", "कुखुरा तथा हाँस", "Poultry", sortOrder = 3),
            SupabaseAnimalCategoryDto("acat-pig-fish", "बंगुर तथा माछा", "Pig & Fishery", sortOrder = 4)
        ),
        animals = listOf(
            SupabaseAnimalDto(
                id = "animal-goat",
                categoryId = "acat-goat",
                nameNe = "बाख्रापालन",
                nameEn = "Goat",
                speciesCode = "goat",
                summaryNe = "बकैयामा खरी तथा बोयर वर्णसंकर बाख्रापालन।",
                safetyNoticeNe = "आकस्मिक लक्षणमा तुरुन्त पशु चिकित्सकसँग परामर्श लिनुहोस्।",
                sourceReference = "DLS Nepal Goat Manual",
                thumbnailUrl = "https://example.org/goat.jpg",
                reviewStatus = "published",
                isVerifiedContent = true,
                isPublished = true,
                sortOrder = 1
            ),
            SupabaseAnimalDto(
                id = "animal-cow",
                categoryId = "acat-cattle-buffalo",
                nameNe = "गाईपालन",
                nameEn = "Cattle",
                speciesCode = "cattle",
                summaryNe = "दूध उत्पादनका लागि उन्नत गाईपालन।",
                safetyNoticeNe = "औषधि मात्रा पशु चिकित्सकबाट मात्र निर्धारण गर्नुहोस्।",
                sourceReference = "NARC Dairy Guide",
                reviewStatus = "published",
                isVerifiedContent = true,
                isPublished = true,
                sortOrder = 2
            ),
            SupabaseAnimalDto(
                id = "animal-sheep",
                categoryId = "acat-goat",
                nameNe = "भेडापालन",
                nameEn = "Sheep",
                speciesCode = "sheep",
                summaryNe = "समीक्षाधीन विवरण",
                safetyNoticeNe = "समीक्षाधीन",
                sourceReference = "",
                reviewStatus = "in_review",
                isVerifiedContent = false,
                isPublished = false,
                sortOrder = 3
            )
        ),
        animalBreeds = listOf(
            SupabaseAnimalBreedDto(
                id = "breed-khari",
                animalId = "animal-goat",
                breedNameNe = "खरी",
                breedNameEn = "Khari",
                originType = "local",
                purposeNe = "मासु",
                characteristicsNe = "पहाडी हावापानी अनुकूल स्थानीय जात।"
            )
        ),
        animalHousing = listOf(
            SupabaseAnimalHousingDto(
                id = "house-goat",
                animalId = "animal-goat",
                shedDesignNe = "टाँड भएको हावा चल्ने खोर",
                ventilationLightNe = "दोहोरो हावा प्रवाह र पर्याप्त घाम",
                beddingDrainageNe = "सुख्खा सोत्तर र पिसाब निकास",
                sanitationRoutineNe = "दैनिक सफाइ",
                biosecurityNotesNe = "नयाँ बाख्रा १४ दिन क्वारेन्टिनमा राख्ने"
            )
        ),
        animalFeeding = listOf(
            SupabaseAnimalFeedingDto(
                id = "feed-goat",
                animalId = "animal-goat",
                greenFodderForageNe = "इपिल इपिल, नेपियर, डालेघाँस",
                dryRoughageNe = "पराल वा सुख्खा घाँस",
                cleanWaterNe = "सफा पिउने पानी २४ घण्टा उपलब्ध",
                feedingFrequencyNe = "बिहान र बेलुका नियमित"
            )
        ),
        animalHealth = listOf(
            SupabaseAnimalHealthDto(
                id = "health-goat",
                animalId = "animal-goat",
                dailyHygieneNe = "खोर सुख्खा र सफा राख्ने",
                biosecurityIsolationNe = "बिरामी बाख्रा अलग राख्ने",
                vaccinationAwarenessNe = "पी.पी.आर. खोप अभियानमा सहभागी हुने",
                dewormingParasiteControlNe = "प्राविधिक सल्लाहअनुसार जुका नियन्त्रण",
                warningSignsNe = "ज्वरो, छेराउटी वा दानापानी छोड्नु",
                vetReferralGuidanceNe = "नजिकको पशु सेवा केन्द्रमा सम्पर्क गर्ने"
            )
        ),
        animalDiseases = listOf(
            SupabaseAnimalDiseaseDto(
                id = "dis-ppr",
                animalId = "animal-goat",
                nameNe = "पी.पी.आर. (गोट प्लेग)",
                nameEn = "PPR",
                pathogenType = "viral",
                symptomsNe = "उच्च ज्वरो, आँखा नाकबाट सिँगान बग्नु, मुखमा घाउ",
                preventionBiosecurityNe = "नियमित खोप र जैविक सुरक्षा",
                immediateFarmerActionNe = "तुरुन्त बथानबाट अलग गर्ने",
                veterinaryCautionNe = "तुरुन्त पशु चिकित्सकलाई देखाउनुहोस्।"
            )
        ),
        animalImages = listOf(
            SupabaseAnimalImageDto(
                id = "aimg-goat",
                animalId = "animal-goat",
                imageUrl = "https://example.org/goat.jpg",
                captionNe = "बाख्रापालन",
                stageCategory = "adult"
            )
        )
    )

    private fun fakeSyncClient(
        syncProvider: suspend () -> Result<RemoteSyncBundle> = { Result.success(sampleBundle()) }
    ): RemoteContentSyncClient = object : RemoteContentSyncClient(null) {
        override val isSupabaseConfigured: Boolean = true
        override suspend fun fetchAllRemoteContent(): Result<RemoteSyncBundle> = syncProvider()
    }

    @Test
    fun roomStartsEmptyWithNoInbuiltDataAndPopulatesStrictlyFromSupabaseSync() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(
            fakeDao,
            testDispatchers,
            remoteClient = fakeSyncClient()
        )

        // Room starts strictly empty — zero inbuilt agricultural or livestock data
        assertTrue(repository.getCategories().first().isEmpty())
        assertTrue(repository.getCrops().first().isEmpty())
        assertTrue(repository.getHelplineContacts().first().isEmpty())
        assertTrue(repository.getAnimalCategories().isEmpty())
        assertTrue(repository.getAllAnimals().isEmpty())

        // Sync directly from Supabase response populates all collections
        val syncResult = repository.syncRemoteContent()
        assertTrue(syncResult.isSuccess)

        assertEquals(6, repository.getCategories().first().size)
        assertTrue(repository.searchCrops("धान").first().any { it.id == "crop-paddy" })
        assertTrue(repository.searchCrops("Maize").first().any { it.id == "crop-maize" })
        assertTrue(repository.searchCrops("xyznonexistent").first().isEmpty())
        assertTrue(
            repository.getVarietyRequirementsForCrop("crop-paddy")
                .first()
                .any { it.varietyNameEn == "Savitri" }
        )
        assertEquals(1, repository.getHelplineContacts().first().size)
    }

    @Test
    fun categoryFilteringReturnsMatchingCropsOnly() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(
            fakeDao,
            testDispatchers,
            remoteClient = fakeSyncClient()
        )
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
        val repository = BakaiyaCropRepositoryImpl(
            fakeDao,
            testDispatchers,
            remoteClient = fakeSyncClient()
        )
        repository.syncRemoteContent()

        // 1. Unauthorized YouTube video cannot be marked downloaded
        repository.markVideoDownloaded("vid-cauli-aitc", "/tmp/fake.mp4", 1234L)
        assertTrue(repository.getDownloadedVideos().first().isEmpty())

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
        val repository = BakaiyaCropRepositoryImpl(
            fakeDao,
            testDispatchers,
            remoteClient = fakeSyncClient()
        )
        assertTrue(repository.syncRemoteContent().isSuccess)

        val categories = repository.getAnimalCategories()
        assertEquals(4, categories.size)

        val publishedAnimals = repository.getAllAnimals()
        assertEquals(2, publishedAnimals.size)
        assertTrue(publishedAnimals.all { it.isPublished })
        assertNull(repository.getAnimalById("animal-sheep"))

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

    @Test
    fun syncFailurePreservesPreviouslyCachedRoomContent() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        var failSync = false
        val remote = fakeSyncClient {
            if (failSync) Result.failure(IOException("Network unavailable"))
            else Result.success(sampleBundle())
        }
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers, remoteClient = remote)

        assertTrue(repository.syncRemoteContent().isSuccess)
        assertEquals(4, repository.getCrops().first().size)
        assertEquals(2, repository.getAllAnimals().size)

        failSync = true
        val offlineRes = repository.syncRemoteContent()
        assertTrue(offlineRes.isFailure)
        assertEquals(4, repository.getCrops().first().size)
        assertEquals(2, repository.getAllAnimals().size)
    }

    @Test
    fun offlineRegistrationQueuesPendingAndSyncsWhenOnlineAndHandlesDuplicates() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        var currentBehavior: (FarmerRegistration) -> RegistrationUploadResult = {
            RegistrationUploadResult.NetworkError(IOException("Offline"))
        }
        val fakeRemote = object : RemoteContentSyncClient(null) {
            override val isSupabaseConfigured: Boolean = true
            override suspend fun uploadFarmerRegistration(registration: FarmerRegistration): RegistrationUploadResult {
                return currentBehavior(registration)
            }
        }
        val repository = BakaiyaCropRepositoryImpl(fakeDao, testDispatchers, remoteClient = fakeRemote)

        // 1. Offline registration saves locally with PENDING status
        val offlineRes = repository.saveFarmerRegistration(
            name = "सीता थापा",
            ward = 4,
            phone = "9841112233",
            toleVillage = "हात्तीसुँडे",
            consentStatus = true
        )
        assertTrue(offlineRes.isSuccess)
        val afterOffline = repository.getFarmerRegistration().first()
        assertNotNull(afterOffline)
        assertEquals(RegistrationSyncStatus.PENDING, afterOffline?.syncStatus)
        assertEquals("हात्तीसुँडे", afterOffline?.toleVillage)
        assertEquals(true, afterOffline?.consentStatus)

        // 2. Retry when network comes back online upgrades status to SYNCED
        currentBehavior = { RegistrationUploadResult.Success }
        val retryRes = repository.retryPendingRegistrationSync()
        assertTrue(retryRes.isSuccess)
        val afterRetry = repository.getFarmerRegistration().first()
        assertEquals(RegistrationSyncStatus.SYNCED, afterRetry?.syncStatus)

        // 3. Duplicate phone returned from server transitions safely to SYNCED
        currentBehavior = { RegistrationUploadResult.AlreadyRegistered }
        val dupRes = repository.saveFarmerRegistration(
            name = "सीता थापा",
            ward = 4,
            phone = "9841112233",
            toleVillage = "हात्तीसुँडे",
            consentStatus = true
        )
        assertTrue(dupRes.isSuccess)
        assertEquals(RegistrationSyncStatus.SYNCED, repository.getFarmerRegistration().first()?.syncStatus)

        // 4. Server HTTP 400 validation error marks FAILED and reports failure
        currentBehavior = { RegistrationUploadResult.ServerError("Edge Function HTTP 400: Invalid") }
        val errRes = repository.saveFarmerRegistration(
            name = "सीता थापा",
            ward = 4,
            phone = "9841112233",
            toleVillage = "हात्तीसुँडे",
            consentStatus = true
        )
        assertTrue(errRes.isFailure)
        assertEquals(RegistrationSyncStatus.FAILED, repository.getFarmerRegistration().first()?.syncStatus)
    }

    @Test
    fun appSyncControllerRefreshesContentAndIncrementsGeneration() = runBlocking {
        val fakeDao = FakeBakaiyaCropDao()
        val repository = BakaiyaCropRepositoryImpl(
            fakeDao,
            testDispatchers,
            remoteClient = fakeSyncClient()
        )
        val controller = AppSyncController(repository = repository, dataStoreManager = null)

        assertEquals(0L, controller.syncGeneration.value)
        val firstResult = controller.refreshNow(force = false)
        assertTrue(firstResult.isSuccess)
        assertEquals(1L, controller.syncGeneration.value)
        assertEquals(4, repository.getCrops().first().size)

        // Forced pull-to-refresh runs immediately bypassing throttle
        val forcedResult = controller.refreshNow(force = true)
        assertTrue(forcedResult.isSuccess)
        assertEquals(2L, controller.syncGeneration.value)
    }

    private class FakeBakaiyaCropDao : BakaiyaCropDao {
        private val categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
        private val crops = MutableStateFlow<List<BakaiyaCropEntity>>(emptyList())
        private val varietyRequirements = MutableStateFlow<List<CropVarietyRequirementEntity>>(emptyList())
        private val images = MutableStateFlow<List<CropImageEntity>>(emptyList())
        private val videos = MutableStateFlow<List<EducationalVideoEntity>>(emptyList())
        private val contacts = MutableStateFlow<List<HelplineContactEntity>>(emptyList())
        private val registration = MutableStateFlow<FarmerRegistrationEntity?>(null)

        private var animalCategories = emptyList<AnimalCategoryEntity>()
        private var animals = emptyList<AnimalEntity>()
        private var animalBreeds = emptyList<AnimalBreedEntity>()
        private var animalHousing = emptyList<AnimalHousingEntity>()
        private var animalFeeding = emptyList<AnimalFeedingEntity>()
        private var animalHealth = emptyList<AnimalHealthEntity>()
        private var animalDiseases = emptyList<AnimalDiseaseEntity>()
        private var animalImages = emptyList<AnimalImageEntity>()

        override fun getAllCategories(): Flow<List<CategoryEntity>> = categories

        override fun upsertCategories(categories: List<CategoryEntity>): List<Long> {
            this.categories.value = categories
            return categories.mapIndexed { i, _ -> i.toLong() }
        }

        override fun clearCategories(): Int {
            val prev = categories.value.size
            categories.value = emptyList()
            return prev
        }

        override fun countPublishedCropsSync(): Int = crops.value.count { it.isPublished }

        override fun getAllCrops(): Flow<List<BakaiyaCropEntity>> =
            crops.map { list -> list.filter { it.isPublished } }

        override fun getCropById(cropId: String): Flow<BakaiyaCropEntity?> =
            crops.map { list -> list.find { it.id == cropId } }

        override fun getCropsByCategory(categoryId: String): Flow<List<BakaiyaCropEntity>> =
            crops.map { list -> list.filter { it.isPublished && it.categoryId == categoryId } }

        override fun searchCrops(query: String): Flow<List<BakaiyaCropEntity>> =
            crops.map { list ->
                list.filter {
                    it.isPublished && (
                        it.nameNe.contains(query, ignoreCase = true) ||
                            it.nameEn.contains(query, ignoreCase = true) ||
                            it.scientificName.contains(query, ignoreCase = true)
                    )
                }
            }

        override fun upsertCrops(crops: List<BakaiyaCropEntity>): List<Long> {
            this.crops.value = crops
            return crops.mapIndexed { i, _ -> i.toLong() }
        }

        override fun clearCrops(): Int {
            val prev = crops.value.size
            crops.value = emptyList()
            return prev
        }

        override fun getVarietyRequirementsForCrop(cropId: String): Flow<List<CropVarietyRequirementEntity>> =
            varietyRequirements.map { list -> list.filter { it.cropId == cropId } }

        override fun upsertVarietyRequirements(varieties: List<CropVarietyRequirementEntity>): List<Long> {
            this.varietyRequirements.value = varieties
            return varieties.mapIndexed { i, _ -> i.toLong() }
        }

        override fun clearVarietyRequirements(): Int {
            val prev = varietyRequirements.value.size
            varietyRequirements.value = emptyList()
            return prev
        }

        override fun getImagesForCrop(cropId: String): Flow<List<CropImageEntity>> =
            images.map { list -> list.filter { it.cropId == cropId } }

        override fun upsertImages(images: List<CropImageEntity>): List<Long> {
            this.images.value = images
            return images.mapIndexed { i, _ -> i.toLong() }
        }

        override fun clearImages(): Int {
            val prev = images.value.size
            images.value = emptyList()
            return prev
        }

        override fun getVideosForCrop(cropId: String): Flow<List<EducationalVideoEntity>> =
            videos.map { list -> list.filter { it.cropId == cropId } }

        override fun getAllVideos(): Flow<List<EducationalVideoEntity>> = videos

        override fun getVideosForTargetSync(cropId: String): List<EducationalVideoEntity> =
            videos.value.filter { it.cropId == cropId }

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

        override fun clearNonDownloadedVideos(): Int {
            val before = videos.value.size
            videos.value = videos.value.filter { it.isDownloaded }
            return before - videos.value.size
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

        override fun updateRegistrationSyncStatus(status: String): Int {
            this.registration.value = this.registration.value?.copy(syncStatus = status)
            return if (this.registration.value != null) 1 else 0
        }

        override fun clearFarmerRegistration(): Int {
            this.registration.value = null
            return 1
        }

        override fun getAllAnimalCategoriesSync(): List<AnimalCategoryEntity> = animalCategories

        override fun clearAnimalCategories(): Int {
            val prev = animalCategories.size
            animalCategories = emptyList()
            return prev
        }

        override fun upsertAnimalCategories(categories: List<AnimalCategoryEntity>): List<Long> {
            animalCategories = categories
            return categories.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getPublishedAnimalsSync(): List<AnimalEntity> =
            animals.filter { it.isPublished }

        override fun getPublishedAnimalsByCategorySync(categoryId: String): List<AnimalEntity> =
            animals.filter { it.isPublished && it.categoryId == categoryId }

        override fun getPublishedAnimalByIdSync(animalId: String): AnimalEntity? =
            animals.firstOrNull { it.id == animalId && it.isPublished }

        override fun clearAnimals(): Int {
            val prev = animals.size
            animals = emptyList()
            return prev
        }

        override fun upsertAnimals(animals: List<AnimalEntity>): List<Long> {
            this.animals = animals
            return animals.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getBreedsForAnimalSync(animalId: String): List<AnimalBreedEntity> =
            animalBreeds.filter { it.animalId == animalId }

        override fun clearAnimalBreeds(): Int {
            val prev = animalBreeds.size
            animalBreeds = emptyList()
            return prev
        }

        override fun upsertAnimalBreeds(breeds: List<AnimalBreedEntity>): List<Long> {
            animalBreeds = breeds
            return breeds.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getHousingForAnimalSync(animalId: String): AnimalHousingEntity? =
            animalHousing.firstOrNull { it.animalId == animalId }

        override fun clearAnimalHousing(): Int {
            val prev = animalHousing.size
            animalHousing = emptyList()
            return prev
        }

        override fun upsertAnimalHousing(list: List<AnimalHousingEntity>): List<Long> {
            animalHousing = list
            return list.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getFeedingForAnimalSync(animalId: String): AnimalFeedingEntity? =
            animalFeeding.firstOrNull { it.animalId == animalId }

        override fun clearAnimalFeeding(): Int {
            val prev = animalFeeding.size
            animalFeeding = emptyList()
            return prev
        }

        override fun upsertAnimalFeeding(list: List<AnimalFeedingEntity>): List<Long> {
            animalFeeding = list
            return list.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getHealthForAnimalSync(animalId: String): AnimalHealthEntity? =
            animalHealth.firstOrNull { it.animalId == animalId }

        override fun clearAnimalHealth(): Int {
            val prev = animalHealth.size
            animalHealth = emptyList()
            return prev
        }

        override fun upsertAnimalHealth(list: List<AnimalHealthEntity>): List<Long> {
            animalHealth = list
            return list.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getDiseasesForAnimalSync(animalId: String): List<AnimalDiseaseEntity> =
            animalDiseases.filter { it.animalId == animalId }

        override fun clearAnimalDiseases(): Int {
            val prev = animalDiseases.size
            animalDiseases = emptyList()
            return prev
        }

        override fun upsertAnimalDiseases(list: List<AnimalDiseaseEntity>): List<Long> {
            animalDiseases = list
            return list.mapIndexed { i, _ -> i.toLong() }
        }

        override fun getImagesForAnimalSync(animalId: String): List<AnimalImageEntity> =
            animalImages.filter { it.animalId == animalId }

        override fun clearAnimalImages(): Int {
            val prev = animalImages.size
            animalImages = emptyList()
            return prev
        }

        override fun upsertAnimalImages(list: List<AnimalImageEntity>): List<Long> {
            animalImages = list
            return list.mapIndexed { i, _ -> i.toLong() }
        }
    }
}
