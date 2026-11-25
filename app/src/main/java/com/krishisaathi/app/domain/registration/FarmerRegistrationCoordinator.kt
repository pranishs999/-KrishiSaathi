package com.krishisaathi.app.domain.registration

import com.krishisaathi.app.AppEntryState
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.data.local.prefs.RegistrationPreferences
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

data class RegistrationValidationResult(
    val nameError: String? = null,
    val wardError: String? = null,
    val phoneError: String? = null,
    val sanitizedName: String = "",
    val normalizedPhone: String = ""
) {
    val isValid: Boolean
        get() = nameError == null && wardError == null && phoneError == null
}

sealed interface RegistrationOutcome {
    data class ValidationFailed(val validation: RegistrationValidationResult) : RegistrationOutcome
    data class PersistenceFailed(val errorMessage: String) : RegistrationOutcome
    data object Success : RegistrationOutcome
}

/**
 * Pure Kotlin coordinator for validating and committing mandatory first-launch farmer registration.
 * Guarantees DataStore registration completion is persisted ONLY after Room confirms saving the record.
 */
class FarmerRegistrationCoordinator(
    private val repository: BakaiyaCropRepository,
    private val registrationPreferences: RegistrationPreferences
) {

    fun validate(
        rawName: String,
        selectedWard: Int?,
        rawPhone: String
    ): RegistrationValidationResult {
        val sanitizedName = rawName.trim()
        val normalizedPhone = ValidationUtils.normalizeNepalMobileNumber(rawPhone)

        val nameError = if (sanitizedName.isEmpty()) {
            "कृपया किसानको नाम लेख्नुहोस्।"
        } else {
            null
        }
        val wardError = if (!ValidationUtils.isValidBakaiyaWard(selectedWard)) {
            "कृपया वडा नम्बर छान्नुहोस्।"
        } else {
            null
        }
        val phoneError = if (!ValidationUtils.isValidNepalMobileNumber(normalizedPhone)) {
            "कृपया ९८ वा ९७ बाट सुरु हुने १० अंकको मान्य नेपाली मोबाइल नम्बर लेख्नुहोस्।"
        } else {
            null
        }

        return RegistrationValidationResult(
            nameError = nameError,
            wardError = wardError,
            phoneError = phoneError,
            sanitizedName = sanitizedName,
            normalizedPhone = normalizedPhone
        )
    }

    suspend fun registerFarmer(
        rawName: String,
        selectedWard: Int?,
        rawPhone: String
    ): RegistrationOutcome {
        val validation = validate(rawName, selectedWard, rawPhone)
        if (!validation.isValid) {
            return RegistrationOutcome.ValidationFailed(validation)
        }

        val saveResult = repository.saveFarmerRegistration(
            name = validation.sanitizedName,
            ward = selectedWard!!,
            phone = validation.normalizedPhone,
            municipality = BakaiyaMunicipality.DEFAULT_NAME
        )
        if (saveResult.isFailure) {
            return RegistrationOutcome.PersistenceFailed(
                "किसान विवरण सुरक्षित हुन सकेन। कृपया पुनः प्रयास गर्नुहोस्।"
            )
        }

        return try {
            registrationPreferences.setRegistrationCompleted(true)
            RegistrationOutcome.Success
        } catch (_: Exception) {
            RegistrationOutcome.PersistenceFailed(
                "दर्ता अवस्था सुरक्षित गर्न समस्या आयो। कृपया पुनः प्रयास गर्नुहोस्।"
            )
        }
    }

    fun observeEntryState(): Flow<AppEntryState> = combine(
        registrationPreferences.isRegistrationCompleted
            .catch { emit(false) },
        repository.getFarmerRegistration()
            .catch { emit(null) }
    ) { isCompleted, farmerRecord ->
        if (isCompleted && farmerRecord != null) {
            AppEntryState.Registered
        } else {
            AppEntryState.NeedsRegistration
        }
    }
}
