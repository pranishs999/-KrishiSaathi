package com.krishisaathi.app.ui.screens.registration

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.data.local.prefs.DataStoreManager
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.domain.model.RegistrationSyncStatus
import com.krishisaathi.app.domain.registration.FarmerRegistrationCoordinator
import com.krishisaathi.app.domain.registration.RegistrationOutcome
import com.krishisaathi.app.worker.SyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val fullName: String = "",
    val municipality: String = "${BakaiyaMunicipality.DEFAULT_NAME}।",
    val selectedWard: Int? = null,
    val phoneNumber: String = "",
    val toleVillage: String = "",
    val consentGiven: Boolean = false,
    val nameError: String? = null,
    val wardError: String? = null,
    val phoneError: String? = null,
    val consentError: String? = null,
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
    private val coordinator: FarmerRegistrationCoordinator,
    private val dataStoreManager: DataStoreManager? = null,
    private val repository: com.krishisaathi.app.domain.repository.BakaiyaCropRepository? = null,
    private val appContext: Context? = null
) : ViewModel() {

    private data class EphemeralState(
        val nameError: String? = null,
        val wardError: String? = null,
        val phoneError: String? = null,
        val consentError: String? = null,
        val submissionError: String? = null,
        val isSubmitting: Boolean = false,
        val isCompleted: Boolean = false
    )

    private val fullNameFlow = savedStateHandle.getStateFlow(KEY_FULL_NAME, "")
    private val selectedWardFlow = savedStateHandle.getStateFlow<Int?>(KEY_SELECTED_WARD, null)
    private val phoneNumberFlow = savedStateHandle.getStateFlow(KEY_PHONE_NUMBER, "")
    private val toleVillageFlow = savedStateHandle.getStateFlow(KEY_TOLE_VILLAGE, "")
    private val consentGivenFlow = savedStateHandle.getStateFlow(KEY_CONSENT_GIVEN, false)
    private val ephemeralState = MutableStateFlow(EphemeralState())

    val uiState: StateFlow<RegistrationUiState> = combine(
        combine(fullNameFlow, selectedWardFlow, phoneNumberFlow) { fullName, ward, phone ->
            Triple(fullName, ward, phone)
        },
        combine(toleVillageFlow, consentGivenFlow, ephemeralState) { tole, consent, ui ->
            Triple(tole, consent, ui)
        }
    ) { (fullName, ward, phone), (tole, consent, ui) ->
        RegistrationUiState(
            fullName = fullName,
            municipality = "${BakaiyaMunicipality.DEFAULT_NAME}।",
            selectedWard = ward,
            phoneNumber = phone,
            toleVillage = tole,
            consentGiven = consent,
            nameError = ui.nameError,
            wardError = ui.wardError,
            phoneError = ui.phoneError,
            consentError = ui.consentError,
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

    fun onToleVillageChanged(value: String) {
        savedStateHandle[KEY_TOLE_VILLAGE] = value
        ephemeralState.update { it.copy(submissionError = null) }
    }

    fun onConsentChanged(checked: Boolean) {
        savedStateHandle[KEY_CONSENT_GIVEN] = checked
        ephemeralState.update { it.copy(consentError = null, submissionError = null) }
    }

    fun submitRegistration() {
        if (ephemeralState.value.isSubmitting || ephemeralState.value.isCompleted) return

        val currentName = fullNameFlow.value
        val currentWard = selectedWardFlow.value
        val currentPhone = phoneNumberFlow.value
        val currentTole = toleVillageFlow.value
        val currentConsent = consentGivenFlow.value

        val validation = coordinator.validate(
            rawName = currentName,
            selectedWard = currentWard,
            rawPhone = currentPhone,
            rawTole = currentTole,
            consentGiven = currentConsent
        )
        if (!validation.isValid) {
            ephemeralState.update {
                it.copy(
                    nameError = validation.nameError,
                    wardError = validation.wardError,
                    phoneError = validation.phoneError,
                    consentError = validation.consentError,
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
                consentError = null,
                submissionError = null,
                isSubmitting = true
            )
        }

        viewModelScope.launch {
            when (
                val outcome = coordinator.registerFarmer(
                    rawName = currentName,
                    selectedWard = currentWard,
                    rawPhone = currentPhone,
                    rawTole = currentTole,
                    consentGiven = currentConsent
                )
            ) {
                is RegistrationOutcome.ValidationFailed -> {
                    ephemeralState.update {
                        it.copy(
                            isSubmitting = false,
                            nameError = outcome.validation.nameError,
                            wardError = outcome.validation.wardError,
                            phoneError = outcome.validation.phoneError,
                            consentError = outcome.validation.consentError
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
                    if (currentTole.isNotBlank()) {
                        dataStoreManager?.setToleVillage(currentTole)
                    }
                    val savedRecord = repository?.getFarmerRegistration()?.firstOrNull()
                    val syncState = savedRecord?.syncStatus ?: RegistrationSyncStatus.PENDING
                    dataStoreManager?.setRegistrationSyncStatus(syncState.name)
                    if (syncState != RegistrationSyncStatus.SYNCED && appContext != null) {
                        SyncWorker.enqueueRegistrationRetry(appContext)
                    }
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
        private const val KEY_TOLE_VILLAGE = "registration_tole_village"
        private const val KEY_CONSENT_GIVEN = "registration_consent_given"

        fun factory(
            appContainer: AppContainer,
            context: Context? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistrationViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    coordinator = FarmerRegistrationCoordinator(
                        repository = appContainer.bakaiyaCropRepository,
                        registrationPreferences = appContainer.registrationPreferences
                    ),
                    dataStoreManager = appContainer.dataStoreManager,
                    repository = appContainer.bakaiyaCropRepository,
                    appContext = context?.applicationContext
                )
            }
        }
    }
}
