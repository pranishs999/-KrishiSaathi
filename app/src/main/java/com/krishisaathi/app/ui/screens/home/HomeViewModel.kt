package com.krishisaathi.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.domain.model.FarmerRegistration
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val categories: List<CropCategory> = emptyList(),
    val featuredCrops: List<BakaiyaCrop> = emptyList(),
    val seasonalCrops: List<BakaiyaCrop> = emptyList(),
    val farmerProfile: FarmerRegistration? = null,
    val isRegistrationDialogOpen: Boolean = false,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: BakaiyaCropRepository
) : ViewModel() {

    private val _isRegistrationDialogOpen = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCategories(),
        repository.getCrops(),
        repository.getFarmerRegistration(),
        _isRegistrationDialogOpen
    ) { categories, crops, profile, isDialogOpen ->
        // Only show seasonal crops that actually have verified plantingSeason strings
        val verifiedSeasonal = crops.filter {
            it.isVerifiedContent && !it.plantingSeason.isNullOrBlank()
        }.take(3)

        HomeUiState(
            categories = categories,
            featuredCrops = crops.take(4),
            seasonalCrops = verifiedSeasonal,
            farmerProfile = profile,
            isRegistrationDialogOpen = isDialogOpen,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            repository.syncRemoteContent()
        }
    }

    fun openRegistrationDialog() {
        _isRegistrationDialogOpen.value = true
    }

    fun closeRegistrationDialog() {
        _isRegistrationDialogOpen.value = false
    }

    fun registerFarmer(name: String, ward: Int, phone: String) {
        viewModelScope.launch {
            repository.saveFarmerRegistration(name, ward, phone)
            closeRegistrationDialog()
        }
    }

    fun clearRegistration() {
        viewModelScope.launch {
            repository.clearFarmerRegistration()
            closeRegistrationDialog()
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
