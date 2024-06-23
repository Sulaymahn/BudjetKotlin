package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AnalyticRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.model.AccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardScreenViewModel @Inject constructor(
    private val analyticRepo: AnalyticRepository,
    private val transactionRepository: TransactionRepository,
    private val settingRepository: AppSettingRepository
) : ViewModel() {
    private val accountFlow = MutableStateFlow<AccountEntity?>(null)

    fun listen(account: AccountEntity) {
        if(accountFlow.value?.id != account.id){
            accountFlow.value = account
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val balance = settingRepository.activeAccount.flatMapLatest { accountId ->
        if (accountId == null) {
            flowOf(null)
        } else {
            analyticRepo.getAccountBalance(accountId.toString())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        null
    )
}