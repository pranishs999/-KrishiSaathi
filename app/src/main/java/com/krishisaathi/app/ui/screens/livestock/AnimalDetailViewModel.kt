package com.krishisaathi.app.ui.screens.livestock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.domain.model.AnimalBreed
import com.krishisaathi.app.domain.model.AnimalDisease
import com.krishisaathi.app.domain.model.AnimalFeedingGuidance
import com.krishisaathi.app.domain.model.AnimalHealthGuidance
import com.krishisaathi.app.domain.model.AnimalHousingGuidance
import com.krishisaathi.app.domain.model.AnimalImage
import com.krishisaathi.app.domain.model.EducationalVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

data class AnimalDetailUiState(
    val animal: Animal? = null,
    val breeds: List<AnimalBreed> = emptyList(),
    val housing: AnimalHousingGuidance? = null,
    val feeding: AnimalFeedingGuidance? = null,
    val health: AnimalHealthGuidance? = null,
    val diseases: List<AnimalDisease> = emptyList(),
    val images: List<AnimalImage> = emptyList(),
    val videos: List<EducationalVideo> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
)

class AnimalDetailViewModel(
    private val animalId: String,
    private val appContainer: AppContainer
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalDetailUiState())
    val uiState: StateFlow<AnimalDetailUiState> = _uiState.asStateFlow()

    init {
        loadAnimalDetail(forceRemoteRefresh = false)
        viewModelScope.launch {
            appContainer.appSyncController.syncGeneration.drop(1).collect {
                reloadFromRoom()
            }
        }
    }

    fun refresh() {
        loadAnimalDetail(forceRemoteRefresh = true)
    }

    private fun loadAnimalDetail(forceRemoteRefresh: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoading = _uiState.value.animal == null,
            isRefreshing = forceRemoteRefresh
        )
        viewModelScope.launch(appContainer.dispatchers.io) {
            try {
                if (forceRemoteRefresh) {
                    appContainer.appSyncController.refreshNow(force = true)
                }
                reloadFromRoom()
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, isRefreshing = false)
            }
        }
    }

    private suspend fun reloadFromRoom() {
        val repo = appContainer.bakaiyaCropRepository
        val animal = repo.getAnimalById(animalId)
        val breeds = repo.getBreedsForAnimal(animalId)
        val housing = repo.getHousingForAnimal(animalId)
        val feeding = repo.getFeedingForAnimal(animalId)
        val health = repo.getHealthForAnimal(animalId)
        val diseases = repo.getDiseasesForAnimal(animalId)
        val images = repo.getImagesForAnimal(animalId)
        val videos = repo.getVideosForAnimal(animalId)
        _uiState.value = AnimalDetailUiState(
            animal = animal,
            breeds = breeds,
            housing = housing,
            feeding = feeding,
            health = health,
            diseases = diseases,
            images = images,
            videos = videos,
            isLoading = false,
            isRefreshing = false
        )
    }

    companion object {
        fun factory(
            animalId: String,
            appContainer: AppContainer
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AnimalDetailViewModel(animalId, appContainer)
            }
        }
    }
}
