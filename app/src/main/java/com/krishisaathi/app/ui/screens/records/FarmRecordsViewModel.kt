package com.krishisaathi.app.ui.screens.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.FarmRecord
import com.krishisaathi.app.domain.model.FarmRecordType
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class FarmRecordsUiState(
    val records: List<FarmRecord> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val selectedFilter: FarmRecordFilter = FarmRecordFilter.ALL,
    val isAddDialogOpen: Boolean = false,
    val errorMessageNe: String? = null
)

enum class FarmRecordFilter(val labelNe: String) {
    ALL("सबै"),
    INCOME("आम्दानी"),
    EXPENSE("खर्च")
}

class FarmRecordsViewModel(
    private val repository: BakaiyaCropRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(FarmRecordFilter.ALL)
    private val _isAddDialogOpen = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private data class RecordTotalsTuple(
        val allRecords: List<FarmRecord>,
        val totalIncome: Double,
        val totalExpense: Double
    )

    val uiState: StateFlow<FarmRecordsUiState> = combine(
        combine(
            repository.getAllFarmRecords(),
            repository.getTotalIncome(),
            repository.getTotalExpense()
        ) { allRecords, totalIncome, totalExpense ->
            RecordTotalsTuple(allRecords, totalIncome, totalExpense)
        },
        _selectedFilter,
        _isAddDialogOpen,
        _errorMessage
    ) { totals, filter, isDialogOpen, errorMessage ->
        val filtered = when (filter) {
            FarmRecordFilter.ALL -> totals.allRecords
            FarmRecordFilter.INCOME -> totals.allRecords.filter { it.type == FarmRecordType.INCOME }
            FarmRecordFilter.EXPENSE -> totals.allRecords.filter { it.type == FarmRecordType.EXPENSE }
        }
        FarmRecordsUiState(
            records = filtered,
            totalIncome = totals.totalIncome,
            totalExpense = totals.totalExpense,
            selectedFilter = filter,
            isAddDialogOpen = isDialogOpen,
            errorMessageNe = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FarmRecordsUiState()
    )

    fun setFilter(filter: FarmRecordFilter) {
        _selectedFilter.value = filter
    }

    fun openAddDialog() {
        _errorMessage.value = null
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
    }

    fun addRecord(
        type: FarmRecordType,
        categoryNe: String,
        amount: Double,
        descriptionNe: String,
        cropNameNe: String?,
        dateMillis: Long
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val record = FarmRecord(
                id = UUID.randomUUID().toString(),
                type = type,
                categoryNe = categoryNe.trim(),
                amount = amount,
                descriptionNe = descriptionNe.trim(),
                cropNameNe = cropNameNe?.trim()?.ifBlank { null },
                dateMillis = dateMillis,
                createdAt = now
            )
            val result = repository.addFarmRecord(record)
            if (result.isSuccess) {
                _errorMessage.value = null
                _isAddDialogOpen.value = false
            } else {
                _errorMessage.value = "हिसाब सुरक्षित गर्न समस्या आयो। कृपया मान्य वर्ग र रकम राख्नुहोस्।"
            }
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteFarmRecord(recordId)
        }
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FarmRecordsViewModel(appContainer.bakaiyaCropRepository)
            }
        }
    }
}
