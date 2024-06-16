package com.unghostdude.budjet.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class AccountSetupScreenViewModel @Inject constructor(
    private val accountRepo: AccountRepository
) : ViewModel() {

    val username = FormControl("", listOf(Validators.Required(), Validators.MaxLength(64)))
    val accountName = FormControl("", listOf(Validators.Required(), Validators.MaxLength(64)))
    var currency by mutableStateOf<Currency?>(null)

    fun canSetupAccount(): Boolean {
        return currency != null && username.isValid && accountName.isValid
    }

    fun setupAccount() {

    }
}