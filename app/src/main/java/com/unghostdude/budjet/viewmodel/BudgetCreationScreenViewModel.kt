package com.unghostdude.budjet.viewmodel;

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.data.BudgetRepository
import com.unghostdude.budjet.data.CategoryRepository
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.Recurrence
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
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    val categories = categoryRepository.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    val name = FormControl(validators = listOf(Validators.MinLength(3)))
    var amount: Double? by mutableStateOf(null)
    var selectedCategories by mutableStateOf<List<CategoryEntity>>(listOf())
    var cycle by mutableStateOf<Recurrence?>(null)

    fun canCreateBudget(): Boolean {
        return name.isValid && amount != null && selectedCategories.isNotEmpty() && cycle != null
    }

    fun createBudget(account: AccountEntity, callback: () -> Unit) {
        viewModelScope.launch {
            val budget = BudgetEntity(
                id = UUID.randomUUID(),
                accountId = account.id,
                amount = amount!!,
                name = name.currentValue,
                recurrence = cycle!!,
                created = LocalDateTime.now().toInstant(ZoneOffset.UTC),
                start = null,
                end = null
            )
            budgetRepository.insert(budget, selectedCategories)
            callback()
        }
    }
}
