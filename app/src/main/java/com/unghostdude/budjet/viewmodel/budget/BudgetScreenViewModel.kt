package com.unghostdude.budjet.viewmodel.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unghostdude.budjet.contract.TransactionRepository
import com.unghostdude.budjet.data.BudgetRepository
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.BudgetCycle
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class BudgetScreenViewModel @Inject constructor(
    private val budgetRepo: BudgetRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    val budgets = budgetRepo.get().combine(
        transactionRepository.get()
    ) { budgets, transactions ->
        budgets.map { budget ->
            val filteredTransactions = transactions
                .filter { transaction ->
                    transaction.account.id == budget.account.id && transaction.category.id in budget.categories.map { it.id }
                }
                .filter { transaction ->
                    isTransactionInBudgetCycle(
                        transaction,
                        budget.budget.cycleSize,
                        budget.budget.cycle
                    )
                }

            calculateBudget(budget, filteredTransactions)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private fun isTransactionInBudgetCycle(
        transaction: Transaction,
        period: Long,
        cycle: BudgetCycle
    ): Boolean {
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        return when (cycle) {
            BudgetCycle.Daily -> transaction.date.isAfter(now.minus(period, ChronoUnit.DAYS))
            BudgetCycle.Weekly -> transaction.date.isAfter(now.minus(period, ChronoUnit.WEEKS))
            BudgetCycle.Monthly -> transaction.date.isAfter(now.minus(period, ChronoUnit.MONTHS))
            BudgetCycle.Yearly -> transaction.date.isAfter(now.minus(period, ChronoUnit.YEARS))
            else -> true
        }
    }

    private fun calculateBudget(
        budget: Budget,
        transactions: List<Transaction>
    ): BudgetForView {
        val totalSpent = transactions.sumOf { it.amount }
        return BudgetForView(
            budget = budget.budget,
            account = budget.account,
            currentAmount = totalSpent
        )
    }
}

data class BudgetForView(
    val budget: BudgetEntity,
    val account: AccountEntity,
    val currentAmount: Double
)