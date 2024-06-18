package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.model.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainNavigatorViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
    private val userRepo: AppSettingRepository
) : ViewModel() {
    val preference = combine(
        userRepo.activeAccount,
        userRepo.username,
        userRepo.showBalance,
        userRepo.isFirstTime,
        accountRepo.get()
    ) { accountId, username, balance, firstTime, accounts ->
        val acc = accounts.firstOrNull {
            it.id == accountId
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

    val accounts = accountRepo.get().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )
}

sealed class MainNavigatorState {
    data object Loading : MainNavigatorState()
    data class Idle(
        val account: Account?,
        val username: String,
        val isFirstTime: Boolean,
        val showBalance: Boolean
    ) : MainNavigatorState()
}