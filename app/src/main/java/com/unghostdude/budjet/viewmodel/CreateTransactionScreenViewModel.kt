package com.unghostdude.budjet.viewmodel;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.CategoryRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionEntity
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@HiltViewModel
class CreateTransactionScreenViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val userData: AppSettingRepository
) : ViewModel() {
    val categories = categoryRepository.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    val title = FormControl(validators = listOf(Validators.MaxLength(64)))
    var category by mutableStateOf<CategoryEntity?>(null)
    var transactionType by mutableStateOf(TransactionType.Expense)
    var date by mutableStateOf<LocalDateTime>(LocalDateTime.now())
    var amount: Double? by mutableStateOf(null)
    var note = FormControl()

    fun canCreateTransaction(): Boolean {
        return title.isValid && category != null && amount != null
    }

    fun createTransaction(account: AccountEntity, onCreated: () -> Unit) {
        viewModelScope.launch {
            val transaction = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = account.id,
                type = transactionType,
                amount = amount!!,
                date = date.toInstant(ZoneOffset.UTC),
                categoryId = category!!.id,
                created = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                lastModified = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                title = title.currentValue.ifBlank { null },
                note = note.currentValue.ifBlank { null },
                conversionRate = null,
                destinationAccountId = null,
                dueDate = null,
                currency = account.defaultCurrency
            )

            transactionRepository.insert(transaction)
            onCreated()
        }
    }
}
