package com.unghostdude.budjet.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
    val username = FormControl(
        validators = listOf(Validators.Required(), Validators.MaxLength(64), Validators.Name())
    )
    val accountName = FormControl(
        validators = listOf(Validators.Required(), Validators.MaxLength(64), Validators.Name())
    )
    var currency by mutableStateOf<Currency?>(null)

    fun canSetupAccount(): Boolean {
        return currency != null && username.currentValue.isNotEmpty() && username.isValid && accountName.currentValue.isNotEmpty() && accountName.isValid
    }

    fun setupAccount(onCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            state = AccountSetupScreenState.Loading
            val account = Account(
                id = UUID.randomUUID(),
                name = accountName.currentValue,
                startAmount = 0.0,
                defaultCurrency = currency!!
            )
            accountRepo.insert(account)
            userData.setFirstTime(false)
            userData.setActiveAccount(account.id)
            userData.setUsername(username.currentValue)
            onCompleted()
        }
    }
}

sealed class AccountSetupScreenState{
    data object Idle: AccountSetupScreenState()
    data object Loading: AccountSetupScreenState()
}