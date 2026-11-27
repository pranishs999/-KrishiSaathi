package com.krishisaathi.app.ui.screens.crops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CropsUiState(
    val categories: List<CropCategory> = emptyList(),
    val crops: List<BakaiyaCrop> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class CropsViewModel(
    private val repository: BakaiyaCropRepository,
    initialCategoryId: String? = null
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(initialCategoryId)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<CropsUiState> = combine(
        repository.getCategories(),
        repository.getCrops(),
        _selectedCategory,
        _searchQuery
    ) { categories, allCrops, selectedCategory, query ->
        val filtered = allCrops.filter { crop ->
            val matchesCategory = selectedCategory == null || crop.categoryId == selectedCategory
            val matchesQuery = query.isBlank() ||
                    crop.nameNe.contains(query, ignoreCase = true) ||
                    crop.nameEn.contains(query, ignoreCase = true) ||
                    crop.scientificName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        CropsUiState(
            categories = categories,
            crops = filtered,
            selectedCategoryId = selectedCategory,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CropsUiState(isLoading = true, selectedCategoryId = initialCategoryId)
    )

    init {
        viewModelScope.launch {
            repository.ensureContentSeeded()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.syncRemoteContent()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryId: String?) {
        _selectedCategory.value = categoryId
    }

    companion object {
        fun factory(appContainer: AppContainer, initialCategoryId: String? = null): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CropsViewModel(appContainer.bakaiyaCropRepository, initialCategoryId)
                }
            }
    }
}
