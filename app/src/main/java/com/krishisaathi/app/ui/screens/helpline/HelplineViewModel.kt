package com.krishisaathi.app.ui.screens.helpline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.HelplineContact
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HelplineUiState(
    val contacts: List<HelplineContact> = emptyList(),
    val selectedWardFilter: Int? = null, // null = All, 0 = Municipality Office, 1..12 = Wards
    val dialErrorMessage: String? = null,
    val isLoading: Boolean = true
)

class HelplineViewModel(
    private val repository: BakaiyaCropRepository
) : ViewModel() {

    private val _selectedWard = MutableStateFlow<Int?>(null)
    private val _dialError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HelplineUiState> = combine(
        repository.getHelplineContacts(),
        _selectedWard,
        _dialError
    ) { allContacts, wardFilter, error ->
        val filtered = if (wardFilter == null) {
            allContacts
        } else {
            allContacts.filter { it.wardNumber == wardFilter }
        }
        HelplineUiState(
            contacts = filtered,
            selectedWardFilter = wardFilter,
            dialErrorMessage = error,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HelplineUiState(isLoading = true)
    )

    fun selectWard(wardNumber: Int?) {
        _selectedWard.value = wardNumber
        _dialError.value = null
    }

    fun setDialError(message: String?) {
        _dialError.value = message
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HelplineViewModel(appContainer.bakaiyaCropRepository)
            }
        }
    }
}
