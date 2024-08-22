package com.unghostdude.budjet.viewmodel.analytic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.contract.TransactionRepository
import com.unghostdude.budjet.model.TimeRange
import com.unghostdude.budjet.model.DetailedTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _timeRange = MutableStateFlow(TimeRange.TODAY)
    val range = _timeRange.asStateFlow()

    val transactions = transactionRepository.getByDateRange(_timeRange.value)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun filterTransactions(timeRange: TimeRange) {
        _timeRange.value = timeRange
    }
}