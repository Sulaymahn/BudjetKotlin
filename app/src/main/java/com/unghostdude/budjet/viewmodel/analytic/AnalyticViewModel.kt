package com.unghostdude.budjet.viewmodel.analytic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.contract.TransactionRepository
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.model.TimeRange
import com.unghostdude.budjet.model.DetailedTransaction
import com.unghostdude.budjet.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AnalyticViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val settingRepository: AppSettingRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _timeRange = MutableStateFlow(TimeRange.TODAY)
    val range = _timeRange.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeAccount = settingRepository.activeAccount
        .filterNotNull()
        .flatMapLatest {
            accountRepository.get(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<Transaction>> =
        combine(activeAccount.filterNotNull(), _timeRange)
        { account, range ->
            transactionRepository.getByDateRange(range, account.id)
        }
            .flatMapLatest { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    fun filterTransactions(timeRange: TimeRange, accountId: UUID) {
        _timeRange.value = timeRange
    }
}