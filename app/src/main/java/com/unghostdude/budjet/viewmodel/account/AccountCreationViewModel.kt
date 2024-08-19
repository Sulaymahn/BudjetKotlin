package com.unghostdude.budjet.viewmodel.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
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
class AccountCreationViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val name = FormControl(
        validators = listOf(Validators.Required())
    )

    var startingAmountIsExpense by mutableStateOf(false)

    val startingAmount = FormControl(
        validators = listOf()
    )
    val currency = FormControl(
        validators = listOf(Validators.Required(), Validators.CurrencyCode())
    )

    fun isValid(): Boolean {
        return name.peepValidity() && startingAmount.peepValidity() && currency.peepValidity()
    }

    fun create(callback: () -> Unit) {
        viewModelScope.launch {
            val amount: Double = getAmount()
            val account = AccountEntity(
                id = UUID.randomUUID(),
                name = name.currentValue,
                currency = Currency.getInstance(currency.currentValue),
                startAmount = amount,
                created = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            )

            accountRepository.insert(account)
            callback()
        }
    }

    private fun getAmount(): Double {
        return if (startingAmount.currentValue.isEmpty()) {
            0.0
        } else {
            if (startingAmountIsExpense) {
                -1 * (startingAmount.currentValue.toDouble())
            } else {
                startingAmount.currentValue.toDouble()
            }
        }
    }
}