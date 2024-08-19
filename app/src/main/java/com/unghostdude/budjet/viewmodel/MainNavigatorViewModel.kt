package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.model.AccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainNavigatorViewModel @Inject constructor(
    accountRepo: AccountRepository,
    userRepo: AppSettingRepository
) : ViewModel() {
    val preference = combine(
        userRepo.activeAccount,
        userRepo.username,
        userRepo.showBalance,
        userRepo.isFirstTime,
        accountRepo.get()
    ) { accountId, username, balance, firstTime, accounts ->
        var acc = accounts.firstOrNull {
            it.id == accountId
        }

        if(acc == null){
            try{
                acc = accounts.first()
                userRepo.setActiveAccount(acc.id)
            }
            catch (_: Exception){

            }
        }

        MainNavigatorState.Idle(
            account = acc,
            username = username,
            isFirstTime = firstTime,
            showBalance = balance
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        MainNavigatorState.Loading
    )
}

sealed class MainNavigatorState {
    data object Loading : MainNavigatorState()
    data class Idle(
        val account: AccountEntity?,
        val username: String,
        val isFirstTime: Boolean,
        val showBalance: Boolean
    ) : MainNavigatorState()
}