package com.unghostdude.budjet.viewmodel.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val settingRepository: AppSettingRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = transactionRepository.get().flatMapLatest { transactions ->
        val map = transactions.groupBy { transaction ->
            transaction.transaction.date.atZone(ZoneId.systemDefault()).toLocalDate()
        }

        val res = mutableListOf<TransactionGroup>()
        for (a in map) {
            res.add(
                TransactionGroup(
                    date = a.key,
                    transactions = a.value
                )
            )
        }
        flowOf(res)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )
}

data class TransactionGroup(
    val date: LocalDate,
    val transactions: List<Transaction>
)