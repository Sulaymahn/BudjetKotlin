package com.unghostdude.budjet.viewmodel;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.data.ViewRepository
import com.unghostdude.budjet.model.Category
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.ui.transaction.CreateTransactionScreenDialog
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.FormGroup
import com.unghostdude.budjet.utilities.Validators
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer.Form
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Currency
import java.util.UUID

@HiltViewModel
class CreateTransactionScreenViewModel @Inject constructor(
    private val viewRepo: ViewRepository,
    private val transactionRepo: TransactionRepository
) : ViewModel() {
    val categories = viewRepo.getCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    val title = FormControl("", listOf(Validators.Required(), Validators.MaxLength(64)))
    var category by mutableStateOf<Category?>(null)
    var transactionType by mutableStateOf(TransactionType.Expense)
    var date by mutableStateOf(LocalDateTime.now())
    var amount: Double? by mutableStateOf(null)

    fun canCreateTransaction(): Boolean {
        return title.isValid && category != null && amount != null
    }

    fun createTransaction() {
        viewModelScope.launch {
            transactionRepo.insert(
                Transaction(
                    id = UUID.randomUUID(),
                    accountId = UUID.randomUUID(),
                    type = transactionType,
                    amount = amount!!,
                    date = date.toInstant(ZoneOffset.UTC),
                    categoryId = category!!.id,
                    lastModified = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                    labels = listOf(),
                    title = title.currentValue,
                    note = "",
                    conversionRate = null,
                    destinationAccountId = null,
                    dueDate = null,
                    currency = Currency.getInstance("NGN")
                )
            )
        }
    }
}
