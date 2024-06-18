package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AnalyticRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.data.ViewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DashboardScreenViewModel @Inject constructor(
    private val analyticRepo: AnalyticRepository,
    private val transactionRepository: TransactionRepository
): ViewModel() {
    fun balance(accountId: UUID) = analyticRepo.getAccountBalance(accountId.toString()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        null
    )
}