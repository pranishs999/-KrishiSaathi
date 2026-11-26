package com.krishisaathi.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val categories: List<CropCategory> = emptyList(),
    val featuredCrops: List<BakaiyaCrop> = emptyList(),
    val seasonalCrops: List<BakaiyaCrop> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: BakaiyaCropRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCategories(),
        repository.getCrops()
    ) { categories, crops ->
        // Only show seasonal crops that actually have verified plantingSeason strings
        val verifiedSeasonal = crops.filter {
            it.isVerifiedContent && !it.plantingSeason.isNullOrBlank()
        }.take(3)

        HomeUiState(
            categories = categories,
            featuredCrops = crops.take(4),
            seasonalCrops = verifiedSeasonal,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            repository.ensureContentSeeded()
        }
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(appContainer.bakaiyaCropRepository)
            }
        }
    }
}
