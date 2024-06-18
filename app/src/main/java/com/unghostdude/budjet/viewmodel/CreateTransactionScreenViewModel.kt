package com.unghostdude.budjet.viewmodel;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.data.ViewRepository
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.Category
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.model.supportedCurrencies
import com.unghostdude.budjet.ui.transaction.CreateTransactionScreenDialog
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.FormGroup
import com.unghostdude.budjet.utilities.Validators
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.text.Normalizer.Form
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Currency
import java.util.UUID

@HiltViewModel
class CreateTransactionScreenViewModel @Inject constructor(
    private val viewRepo: ViewRepository,
    private val transactionRepo: TransactionRepository,
    private val userData: AppSettingRepository
) : ViewModel() {
    val categories = viewRepo.getCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    val title = FormControl(validators = listOf(Validators.Required(), Validators.MaxLength(64)))
    var category by mutableStateOf<Category?>(null)
    var transactionType by mutableStateOf(TransactionType.Expense)
    var date by mutableStateOf<LocalDateTime>(LocalDateTime.now())
    var amount: Double? by mutableStateOf(null)
    var note = FormControl()

    fun canCreateTransaction(): Boolean {
        return title.isValid && category != null && amount != null
    }

    fun createTransaction(account: Account, onCreated: () -> Unit) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = UUID.randomUUID(),
                accountId = account.id,
                type = transactionType,
                amount = amount!!,
                date = date.toInstant(ZoneOffset.UTC),
                categoryId = category!!.id,
                lastModified = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                labels = listOf(),
                title = title.currentValue.ifBlank { null },
                note = note.currentValue.ifBlank { null },
                conversionRate = null,
                destinationAccountId = null,
                dueDate = null,
                currency = account.defaultCurrency
            )

            transactionRepo.insert(transaction)
            onCreated()
        }
    }
}
