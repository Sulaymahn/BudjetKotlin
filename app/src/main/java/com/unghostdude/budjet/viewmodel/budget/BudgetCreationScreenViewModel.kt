package com.unghostdude.budjet.viewmodel.budget;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.BudgetRepository
import com.unghostdude.budjet.data.CategoryRepository
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.BudgetCycle
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BudgetCreationScreenViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    val categories = categoryRepository.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    val accounts = accountRepository.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    val name = FormControl(validators = listOf(Validators.Required(), Validators.MinLength(3)))
    var amount: Double? by mutableStateOf(null)
    var cycleSize by mutableLongStateOf(0)
    var cycle by mutableStateOf<BudgetCycle?>(null)
    var account by mutableStateOf<AccountEntity?>(null)

    private val startDate by mutableStateOf<LocalDateTime>(LocalDateTime.now())

    fun canCreateBudget(): Boolean {
        return name.peepValidity() && amount != null && cycle != null && account != null
    }

    fun createBudget(selectedCategories: List<Int>, callback: () -> Unit) {
        viewModelScope.launch {
            val budget = BudgetEntity(
                id = UUID.randomUUID(),
                accountId = account!!.id,
                amount = amount!!,
                name = name.currentValue.trim(),
                cycleSize = cycleSize,
                cycle = cycle!!,
                created = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                start = startDate.toInstant(ZoneOffset.UTC),
                end = null
            )

            budgetRepository.insert(budget, selectedCategories)

            callback()
        }
    }
}
