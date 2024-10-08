package com.unghostdude.budjet.viewmodel.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Currency
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AccountSetupScreenViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
    private val userData: AppSettingRepository
) : ViewModel() {
    var state: AccountSetupScreenState by mutableStateOf(
        AccountSetupScreenState.Idle
    )
    val accountName = FormControl(
        validators = listOf(Validators.Required(), Validators.MaxLength(32))
    )
    var currency by mutableStateOf<Currency?>(null)

    fun canSetupAccount(): Boolean {
        return currency != null && accountName.peepValidity()
    }

    fun setupAccount(onCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            state = AccountSetupScreenState.Loading
            val account = AccountEntity(
                id = UUID.randomUUID(),
                name = accountName.currentValue.trim(),
                startAmount = 0.0,
                currency = currency!!,
                created = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            )
            accountRepo.insert(account)
            userData.setFirstTime(false)
            userData.setActiveAccount(account.id)
            onCompleted()
        }
    }
}

sealed class AccountSetupScreenState{
    data object Idle: AccountSetupScreenState()
    data object Loading: AccountSetupScreenState()
}