package com.unghostdude.budjet.viewmodel;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
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
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@HiltViewModel
class CreateTransactionScreenViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val userData: AppSettingRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    val categories = categoryRepository.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    var accounts = accountRepository.get()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            listOf()
        )

    var account by mutableStateOf<AccountEntity?>(null)
    var category by mutableStateOf<CategoryEntity?>(null)
    var transactionType by mutableStateOf(TransactionType.Expense)
    var date by mutableStateOf<LocalDateTime>(LocalDateTime.now())
    var amount: Double? by mutableStateOf(null)
    var note = FormControl()
    var title = FormControl()

    fun canCreateTransaction(): Boolean {
        return account != null && category != null && amount != null
    }

    fun createTransaction(onCreated: () -> Unit) {
        viewModelScope.launch {
            val transaction = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = account!!.id,
                type = transactionType,
                amount = amount!!,
                date = date.toInstant(ZoneOffset.UTC),
                categoryId = category!!.id,
                created = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                lastModified = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                note = note.currentValue.ifBlank { null },
                conversionRate = null,
                destinationAccountId = null,
                title = title.currentValue,
                dueDate = null,
                currency = account!!.currency
            )

            transactionRepository.insert(transaction)
            onCreated()
        }
    }
}
