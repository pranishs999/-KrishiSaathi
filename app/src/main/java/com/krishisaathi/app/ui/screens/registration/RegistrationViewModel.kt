package com.krishisaathi.app.ui.screens.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.domain.registration.FarmerRegistrationCoordinator
import com.krishisaathi.app.domain.registration.RegistrationOutcome
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val fullName: String = "",
    val municipality: String = "${BakaiyaMunicipality.DEFAULT_NAME}।",
    val selectedWard: Int? = null,
    val phoneNumber: String = "",
    val nameError: String? = null,
    val wardError: String? = null,
    val phoneError: String? = null,
    val submissionError: String? = null,
    val isSubmitting: Boolean = false,
    val isCompleted: Boolean = false
)

/**
 * State holder for mandatory first-launch farmer registration.
 * Preserves unsaved input across configuration changes and process recreation via SavedStateHandle.
 */
class RegistrationViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val coordinator: FarmerRegistrationCoordinator
) : ViewModel() {

    private data class EphemeralState(
        val nameError: String? = null,
        val wardError: String? = null,
        val phoneError: String? = null,
        val submissionError: String? = null,
        val isSubmitting: Boolean = false,
        val isCompleted: Boolean = false
    )

    private val fullNameFlow = savedStateHandle.getStateFlow(KEY_FULL_NAME, "")
    private val selectedWardFlow = savedStateHandle.getStateFlow<Int?>(KEY_SELECTED_WARD, null)
    private val phoneNumberFlow = savedStateHandle.getStateFlow(KEY_PHONE_NUMBER, "")
    private val ephemeralState = MutableStateFlow(EphemeralState())

    val uiState: StateFlow<RegistrationUiState> = combine(
        fullNameFlow,
        selectedWardFlow,
        phoneNumberFlow,
        ephemeralState
    ) { fullName, ward, phone, ui ->
        RegistrationUiState(
            fullName = fullName,
            municipality = "${BakaiyaMunicipality.DEFAULT_NAME}।",
            selectedWard = ward,
            phoneNumber = phone,
            nameError = ui.nameError,
            wardError = ui.wardError,
            phoneError = ui.phoneError,
            submissionError = ui.submissionError,
            isSubmitting = ui.isSubmitting,
            isCompleted = ui.isCompleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RegistrationUiState()
    )

    fun onFullNameChanged(value: String) {
        savedStateHandle[KEY_FULL_NAME] = value
        ephemeralState.update { it.copy(nameError = null, submissionError = null) }
    }

    fun onWardSelected(ward: Int) {
        if (ValidationUtils.isValidBakaiyaWard(ward)) {
            savedStateHandle[KEY_SELECTED_WARD] = ward
            ephemeralState.update { it.copy(wardError = null, submissionError = null) }
        }
    }

    fun onPhoneNumberChanged(value: String) {
        if (value.length <= 15) {
            savedStateHandle[KEY_PHONE_NUMBER] = value
            ephemeralState.update { it.copy(phoneError = null, submissionError = null) }
        }
    }

    fun submitRegistration() {
        if (ephemeralState.value.isSubmitting || ephemeralState.value.isCompleted) return

        val currentName = fullNameFlow.value
        val currentWard = selectedWardFlow.value
        val currentPhone = phoneNumberFlow.value

        val validation = coordinator.validate(currentName, currentWard, currentPhone)
        if (!validation.isValid) {
            ephemeralState.update {
                it.copy(
                    nameError = validation.nameError,
                    wardError = validation.wardError,
                    phoneError = validation.phoneError,
                    submissionError = null
                )
            }
            return
        }

        ephemeralState.update {
            it.copy(
                nameError = null,
                wardError = null,
                phoneError = null,
                submissionError = null,
                isSubmitting = true
            )
        }

        viewModelScope.launch {
            when (val outcome = coordinator.registerFarmer(currentName, currentWard, currentPhone)) {
                is RegistrationOutcome.ValidationFailed -> {
                    ephemeralState.update {
                        it.copy(
                            isSubmitting = false,
                            nameError = outcome.validation.nameError,
                            wardError = outcome.validation.wardError,
                            phoneError = outcome.validation.phoneError
                        )
                    }
                }

                is RegistrationOutcome.PersistenceFailed -> {
                    ephemeralState.update {
                        it.copy(
                            isSubmitting = false,
                            isCompleted = false,
                            submissionError = outcome.errorMessage
                        )
                    }
                }

                RegistrationOutcome.Success -> {
                    ephemeralState.update {
                        it.copy(
                            isSubmitting = false,
                            submissionError = null,
                            isCompleted = true
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_FULL_NAME = "registration_full_name"
        private const val KEY_SELECTED_WARD = "registration_selected_ward"
        private const val KEY_PHONE_NUMBER = "registration_phone_number"

        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistrationViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    coordinator = FarmerRegistrationCoordinator(
                        repository = appContainer.bakaiyaCropRepository,
                        registrationPreferences = appContainer.registrationPreferences
                    )
                )
            }
        }
    }
}
