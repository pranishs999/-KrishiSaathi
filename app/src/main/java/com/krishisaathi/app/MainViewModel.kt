package com.krishisaathi.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.registration.FarmerRegistrationCoordinator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

sealed interface AppEntryState {
    data object Loading : AppEntryState
    data object NeedsRegistration : AppEntryState
    data object Registered : AppEntryState
}

/**
 * Observes persistent farmer registration status across Room and DataStore
 * to enforce first-launch registration before accessing the main application.
 */
class MainViewModel(
    coordinator: FarmerRegistrationCoordinator
) : ViewModel() {

    val entryState: StateFlow<AppEntryState> = coordinator.observeEntryState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppEntryState.Loading
        )

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(
                    coordinator = FarmerRegistrationCoordinator(
                        repository = appContainer.bakaiyaCropRepository,
                        registrationPreferences = appContainer.registrationPreferences
                    )
                )
            }
        }
    }
}
