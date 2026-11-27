package com.krishisaathi.app.ui.screens.livestock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.domain.model.AnimalCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LivestockUiState(
    val categories: List<AnimalCategory> = emptyList(),
    val animals: List<Animal> = emptyList(),
    val selectedCategoryId: String? = null,
    val isLoading: Boolean = true
)

class LivestockViewModel(
    private val appContainer: AppContainer,
    private val initialCategoryId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LivestockUiState())
    val uiState: StateFlow<LivestockUiState> = _uiState.asStateFlow()

    init {
        loadLivestockData(forceRemoteRefresh = false)
    }

    fun refresh() {
        loadLivestockData(forceRemoteRefresh = true)
    }

    private fun loadLivestockData(forceRemoteRefresh: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch(appContainer.dispatchers.io) {
            try {
                val repo = appContainer.bakaiyaCropRepository
                if (forceRemoteRefresh) {
                    repo.syncRemoteContent()
                } else {
                    repo.ensureContentSeeded()
                }
                val categories = repo.getAnimalCategories()
                val animals = if (initialCategoryId != null) {
                    repo.getAnimalsByCategory(initialCategoryId)
                } else {
                    repo.getAllAnimals()
                }
                _uiState.value = LivestockUiState(
                    categories = categories,
                    animals = animals,
                    selectedCategoryId = initialCategoryId,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    companion object {
        fun factory(
            appContainer: AppContainer,
            initialCategoryId: String? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LivestockViewModel(appContainer, initialCategoryId)
            }
        }
    }
}
