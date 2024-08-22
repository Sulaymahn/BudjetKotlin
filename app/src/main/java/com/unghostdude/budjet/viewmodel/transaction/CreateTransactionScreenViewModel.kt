package com.unghostdude.budjet.viewmodel.transaction;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.contract.TransactionRepository
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.CategoryRepository
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionForCreation
import com.unghostdude.budjet.model.TransactionType
import com.unghostdude.budjet.utilities.FormControl
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

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
    var date by mutableStateOf<ZonedDateTime>(ZonedDateTime.now())
    var amount: Double? by mutableStateOf(null)
    var note = FormControl()
    var title = FormControl()

    fun canCreateTransaction(): Boolean {
        return account != null && category != null && amount != null
    }

    fun createTransaction(onCreated: () -> Unit) {
        viewModelScope.launch {
            transactionRepository.insert(
                TransactionForCreation(
                    account = account!!,
                    destinationAccount = null,
                    amount = amount!!,
                    type = transactionType,
                    title = title.currentValue,
                    note = note.currentValue,
                    category = category!!,
                    date = date,
                    dueDate = null
                )
            )
            onCreated()
        }
    }
}
