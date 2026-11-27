package com.krishisaathi.app.domain.registration

import com.krishisaathi.app.AppEntryState
import com.krishisaathi.app.data.local.prefs.RegistrationPreferences
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
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class RegistrationFlowTest {

    @Test
    fun emptyNameWardAndInvalidPhoneFailValidationWithoutSavingState() = runBlocking {
        val repo = FakeRegistrationRepository()
        val prefs = FakeRegistrationPreferences()
        val coordinator = FarmerRegistrationCoordinator(repo, prefs)

        // Initial launch is NeedsRegistration
        assertEquals(AppEntryState.NeedsRegistration, coordinator.observeEntryState().first())

        // Empty name
        val emptyNameOutcome = coordinator.registerFarmer("  ", 3, "9841234567")
        assertTrue(emptyNameOutcome is RegistrationOutcome.ValidationFailed)
        assertNotNull((emptyNameOutcome as RegistrationOutcome.ValidationFailed).validation.nameError)
        assertNull(emptyNameOutcome.validation.wardError)
        assertNull(emptyNameOutcome.validation.phoneError)

        // Empty ward
        val emptyWardOutcome = coordinator.registerFarmer("सीता माया", null, "9841234567")
        assertTrue(emptyWardOutcome is RegistrationOutcome.ValidationFailed)
        assertNotNull((emptyWardOutcome as RegistrationOutcome.ValidationFailed).validation.wardError)

        // Invalid phone
        val invalidPhoneOutcome = coordinator.registerFarmer("सीता माया", 4, "12345")
        assertTrue(invalidPhoneOutcome is RegistrationOutcome.ValidationFailed)
        assertNotNull((invalidPhoneOutcome as RegistrationOutcome.ValidationFailed).validation.phoneError)

        // Missing consent
        val missingConsentOutcome = coordinator.registerFarmer("सीता माया", 4, "9841234567", "हात्तीसुँडे", consentGiven = false)
        assertTrue(missingConsentOutcome is RegistrationOutcome.ValidationFailed)
        assertNotNull((missingConsentOutcome as RegistrationOutcome.ValidationFailed).validation.consentError)

        assertFalse(prefs.isCompleted.value)
        assertNull(repo.registration.value)
        assertEquals(AppEntryState.NeedsRegistration, coordinator.observeEntryState().first())
    }

    @Test
    fun failedDatabaseSaveNeverMarksRegistrationCompleted() = runBlocking {
        val repo = FakeRegistrationRepository(failSave = true)
        val prefs = FakeRegistrationPreferences()
        val coordinator = FarmerRegistrationCoordinator(repo, prefs)

        val outcome = coordinator.registerFarmer("हरि प्रसाद", 7, "9855012345")

        assertTrue(outcome is RegistrationOutcome.PersistenceFailed)
        assertFalse(prefs.isCompleted.value)
        assertEquals(0, prefs.setCompletedCallCount)
        assertNull(repo.registration.value)
        assertEquals(AppEntryState.NeedsRegistration, coordinator.observeEntryState().first())
    }

    @Test
    fun failedDataStoreCommitReportsPersistenceFailure() = runBlocking {
        val repo = FakeRegistrationRepository(failSave = false)
        val prefs = FakeRegistrationPreferences(failWrite = true)
        val coordinator = FarmerRegistrationCoordinator(repo, prefs)

        val outcome = coordinator.registerFarmer("हरि प्रसाद", 7, "9855012345")

        assertTrue(outcome is RegistrationOutcome.PersistenceFailed)
        assertFalse(prefs.isCompleted.value)
        assertEquals(AppEntryState.NeedsRegistration, coordinator.observeEntryState().first())
    }

    @Test
    fun validRegistrationPersistsBothRoomRecordAndDataStoreAndTransitionsEntryStateToRegistered() = runBlocking {
        val repo = FakeRegistrationRepository()
        val prefs = FakeRegistrationPreferences()
        val coordinator = FarmerRegistrationCoordinator(repo, prefs)

        assertEquals(AppEntryState.NeedsRegistration, coordinator.observeEntryState().first())

        val outcome = coordinator.registerFarmer("  राम बहादुर राई  ", 3, "+977 9841234567")

        assertEquals(RegistrationOutcome.Success, outcome)
        assertTrue(prefs.isCompleted.value)
        assertEquals("राम बहादुर राई", repo.registration.value?.fullName)
        assertEquals(BakaiyaMunicipality.DEFAULT_NAME, repo.registration.value?.municipality)
        assertEquals(3, repo.registration.value?.wardNumber)
        assertEquals("9841234567", repo.registration.value?.phoneNumber)
        assertEquals(AppEntryState.Registered, coordinator.observeEntryState().first())
    }

    @Test
    fun entryStateFallsBackSafelyToNeedsRegistrationWhenPreferencesThrow() = runBlocking {
        val repo = FakeRegistrationRepository().apply {
            registration.value = FarmerRegistration(
                fullName = "कविता गोले",
                wardNumber = 2,
                phoneNumber = "9841000000"
            )
        }
        val failingPrefs = object : RegistrationPreferences {
            override val isRegistrationCompleted: Flow<Boolean> = flow {
                throw IOException("Simulated read error")
            }

            override suspend fun setRegistrationCompleted(completed: Boolean) = Unit
        }

        val coordinator = FarmerRegistrationCoordinator(repo, failingPrefs)
        assertEquals(AppEntryState.NeedsRegistration, coordinator.observeEntryState().first())
    }

    private class FakeRegistrationPreferences(
        private val failWrite: Boolean = false
    ) : RegistrationPreferences {
        val isCompleted = MutableStateFlow(false)
        var setCompletedCallCount = 0

        override val isRegistrationCompleted: Flow<Boolean> = isCompleted

        override suspend fun setRegistrationCompleted(completed: Boolean) {
            if (failWrite) throw IOException("Simulated DataStore write failure")
            setCompletedCallCount++
            isCompleted.value = completed
        }
    }

    private class FakeRegistrationRepository(
        private val failSave: Boolean = false
    ) : BakaiyaCropRepository {
        val registration = MutableStateFlow<FarmerRegistration?>(null)

        override fun getFarmerRegistration(): Flow<FarmerRegistration?> = registration

        override suspend fun saveFarmerRegistration(
            name: String,
            ward: Int,
            phone: String,
            municipality: String,
            toleVillage: String,
            consentStatus: Boolean
        ): Result<Unit> {
            if (failSave) {
                return Result.failure(IllegalStateException("Simulated Room save failure"))
            }
            registration.value = FarmerRegistration(
                fullName = name.trim(),
                municipality = municipality,
                wardNumber = ward,
                phoneNumber = phone,
                toleVillage = toleVillage,
                consentStatus = consentStatus,
                registeredAt = 123456789L
            )
            return Result.success(Unit)
        }

        override suspend fun retryPendingRegistrationSync(): Result<Unit> = Result.success(Unit)

        override suspend fun clearFarmerRegistration(): Result<Unit> {
            registration.value = null
            return Result.success(Unit)
        }

        override fun getCategories(): Flow<List<CropCategory>> = MutableStateFlow(emptyList())
        override fun getCrops(): Flow<List<BakaiyaCrop>> = MutableStateFlow(emptyList())
        override fun getCropById(cropId: String): Flow<BakaiyaCrop?> = MutableStateFlow(null)
        override fun getVarietyRequirementsForCrop(cropId: String): Flow<List<CropVarietyRequirement>> = MutableStateFlow(emptyList())
        override fun getCropsByCategory(categoryId: String): Flow<List<BakaiyaCrop>> = MutableStateFlow(emptyList())
        override fun searchCrops(query: String): Flow<List<BakaiyaCrop>> = MutableStateFlow(emptyList())
        override fun getCropImages(cropId: String): Flow<List<CropImage>> = MutableStateFlow(emptyList())
        override fun getCropVideos(cropId: String): Flow<List<EducationalVideo>> = MutableStateFlow(emptyList())
        override fun getVideosForTopic(cropId: String, topicId: Int): Flow<List<EducationalVideo>> = MutableStateFlow(emptyList())
        override fun getDownloadedVideos(): Flow<List<EducationalVideo>> = MutableStateFlow(emptyList())
        override fun getVideoById(videoId: String): Flow<EducationalVideo?> = MutableStateFlow(null)
        override fun getHelplineContacts(): Flow<List<HelplineContact>> = MutableStateFlow(emptyList())
        override fun getContactsByWard(wardNumber: Int): Flow<List<HelplineContact>> = MutableStateFlow(emptyList())
        override suspend fun markVideoDownloaded(videoId: String, localPath: String, sizeBytes: Long) = Result.success(Unit)
        override suspend fun deleteDownloadedVideo(videoId: String) = Result.success(Unit)
        override suspend fun syncRemoteContent() = Result.success(Unit)
        override suspend fun ensureContentSeeded() = Result.success(Unit)
        override suspend fun getAnimalCategories(): List<AnimalCategory> = emptyList()
        override suspend fun getAllAnimals(): List<Animal> = emptyList()
        override suspend fun getAnimalsByCategory(categoryId: String): List<Animal> = emptyList()
        override suspend fun getAnimalById(animalId: String): Animal? = null
        override suspend fun getBreedsForAnimal(animalId: String): List<AnimalBreed> = emptyList()
        override suspend fun getHousingForAnimal(animalId: String): AnimalHousingGuidance? = null
        override suspend fun getFeedingForAnimal(animalId: String): AnimalFeedingGuidance? = null
        override suspend fun getHealthForAnimal(animalId: String): AnimalHealthGuidance? = null
        override suspend fun getDiseasesForAnimal(animalId: String): List<AnimalDisease> = emptyList()
        override suspend fun getImagesForAnimal(animalId: String): List<AnimalImage> = emptyList()
        override suspend fun getVideosForAnimal(animalId: String): List<EducationalVideo> = emptyList()
    }
}
