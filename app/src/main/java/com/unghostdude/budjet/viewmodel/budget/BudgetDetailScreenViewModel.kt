package com.unghostdude.budjet.viewmodel.budget

import androidx.compose.runtime.getValue
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
import com.unghostdude.budjet.model.BudgetForUpdate
import com.unghostdude.budjet.utilities.FormControl
import com.unghostdude.budjet.utilities.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetDetailScreenViewModel @Inject constructor(
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

    private var fetching = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val budget = fetching.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            budgetRepository.get(id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        null
    )

    fun fetch(id: String) {
        if (fetching.value != id) {
            fetching.value = id
        }
    }

    fun updateBudget(budget: BudgetEntity, categories: List<Int>, callback: () -> Unit) {
        viewModelScope.launch {
            budgetRepository.update(budget, categories)
            callback()
        }
    }

    fun deleteBudget(budget: BudgetEntity, callback: () -> Unit) {
        viewModelScope.launch {
            budgetRepository.delete(budget)
            callback()
        }
    }
}