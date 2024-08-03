package com.unghostdude.budjet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.model.AccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
    private val userRepo: AppSettingRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    val accounts = accountRepo.get().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )

    fun switchAccount(account: AccountEntity) {
        viewModelScope.launch {
            userRepo.setActiveAccount(account.id)
        }
    }
}