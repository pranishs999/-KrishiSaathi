package com.krishisaathi.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.data.remote.AppSyncController
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.domain.model.AnimalCategory
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val categories: List<CropCategory> = emptyList(),
    val featuredCrops: List<BakaiyaCrop> = emptyList(),
    val seasonalCrops: List<BakaiyaCrop> = emptyList(),
    val totalCropsCount: Int = 0,
    val heroCropImageUrl: String? = null,
    val totalAnimalsCount: Int = 0,
    val heroLivestockImageUrl: String? = null,
    val livestockSubtitleNe: String = "",
    val farmerRegistration: FarmerRegistration? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val syncErrorNe: String? = null
)

private data class LivestockHomeSummary(
    val categories: List<AnimalCategory> = emptyList(),
    val animals: List<Animal> = emptyList(),
    val isSyncing: Boolean = false,
    val isUserRefreshing: Boolean = false,
    val syncErrorNe: String? = null
)

class HomeViewModel(
    private val repository: BakaiyaCropRepository,
    private val appSyncController: AppSyncController? = null
) : ViewModel() {

    private val _livestockAndSync = MutableStateFlow(LivestockHomeSummary(isSyncing = true))

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCategories(),
        repository.getCrops(),
        repository.getFarmerRegistration(),
        _livestockAndSync
    ) { categories, crops, farmer, syncSummary ->
        val verifiedSeasonal = crops.filter {
            it.isVerifiedContent && !it.plantingSeason.isNullOrBlank()
        }.take(3)

        val cropImage = crops.firstOrNull { !it.thumbnailUrl.isNullOrBlank() }?.thumbnailUrl
            ?: categories.firstOrNull { !it.thumbnailUrl.isNullOrBlank() }?.thumbnailUrl
        val livestockImage = syncSummary.animals.firstOrNull { !it.thumbnailUrl.isNullOrBlank() }?.thumbnailUrl
            ?: syncSummary.categories.firstOrNull { !it.thumbnailUrl.isNullOrBlank() }?.thumbnailUrl
        val animalNamesSummary = syncSummary.animals
            .take(4)
            .joinToString(", ") { it.nameNe }

        HomeUiState(
            categories = categories,
            featuredCrops = crops.take(4),
            seasonalCrops = verifiedSeasonal,
            totalCropsCount = crops.size,
            heroCropImageUrl = cropImage,
            totalAnimalsCount = syncSummary.animals.size,
            heroLivestockImageUrl = livestockImage,
            livestockSubtitleNe = animalNamesSummary,
            farmerRegistration = farmer,
            isLoading = syncSummary.isSyncing,
            isRefreshing = syncSummary.isUserRefreshing,
            syncErrorNe = syncSummary.syncErrorNe
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    init {
        syncFromRemote(force = false, showPullIndicator = false)
        appSyncController?.let { controller ->
            viewModelScope.launch {
                controller.syncGeneration.drop(1).collect {
                    reloadLivestockFromRoom()
                }
            }
        }
    }

    fun refresh() {
        syncFromRemote(force = true, showPullIndicator = true)
    }

    private fun syncFromRemote(force: Boolean, showPullIndicator: Boolean) {
        viewModelScope.launch {
            val cachedCategories = repository.getAnimalCategories()
            val cachedAnimals = repository.getAllAnimals()
            _livestockAndSync.value = LivestockHomeSummary(
                categories = cachedCategories,
                animals = cachedAnimals,
                isSyncing = cachedAnimals.isEmpty(),
                isUserRefreshing = showPullIndicator,
                syncErrorNe = null
            )

            val syncResult = if (appSyncController != null) {
                appSyncController.refreshNow(force = force)
            } else {
                repository.syncRemoteContent()
            }
            val updatedCategories = repository.getAnimalCategories()
            val updatedAnimals = repository.getAllAnimals()

            _livestockAndSync.value = LivestockHomeSummary(
                categories = updatedCategories,
                animals = updatedAnimals,
                isSyncing = false,
                isUserRefreshing = false,
                syncErrorNe = if (syncResult.isFailure && updatedAnimals.isEmpty()) {
                    "Supabase बाट डेटा सिंक हुन सकेन। कृपया इन्टरनेट जडान जाँच गरी पुनः प्रयास गर्नुहोस्।"
                } else {
                    null
                }
            )
        }
    }

    private suspend fun reloadLivestockFromRoom() {
        val current = _livestockAndSync.value
        _livestockAndSync.value = current.copy(
            categories = repository.getAnimalCategories(),
            animals = repository.getAllAnimals(),
            syncErrorNe = null
        )
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(
                    repository = appContainer.bakaiyaCropRepository,
                    appSyncController = appContainer.appSyncController
                )
            }
        }
    }
}
