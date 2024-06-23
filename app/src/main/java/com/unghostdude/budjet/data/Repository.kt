package com.unghostdude.budjet.data


import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.BudgetCategoryEntity
import com.unghostdude.budjet.model.BudgetWithAccountAndCategories
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionEntity
import com.unghostdude.budjet.model.TransactionTemplate
import com.unghostdude.budjet.model.TransactionWithAccountAndCategory
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TransactionRepository {
    suspend fun insert(transaction: TransactionEntity)
    suspend fun update(transaction: TransactionEntity)
    suspend fun delete(transaction: TransactionEntity)
    fun get(id: String): Flow<TransactionWithAccountAndCategory?>
    fun get(): Flow<List<TransactionWithAccountAndCategory>>
    fun getWithAccountId(accountId: String): Flow<List<TransactionWithAccountAndCategory>>
}

interface TransactionTemplateRepository {
    suspend fun insert(template: TransactionTemplate)
    suspend fun update(template: TransactionTemplate)
    suspend fun delete(template: TransactionTemplate)
    fun get(id: String): Flow<TransactionTemplate?>
    fun get(): Flow<List<TransactionTemplate>>
}

interface BudgetRepository {
    suspend fun insert(budget: BudgetEntity)
    suspend fun insert(items: List<BudgetCategoryEntity>)
    suspend fun insert(budget: BudgetEntity, items: List<CategoryEntity>)
    suspend fun update(budget: BudgetEntity)
    suspend fun delete(budget: BudgetEntity)
    fun get(id: String): Flow<BudgetWithAccountAndCategories?>
    fun get(): Flow<List<BudgetWithAccountAndCategories>>
    fun getWithAccountId(id: String): Flow<List<BudgetWithAccountAndCategories>>
}

interface AccountRepository {
    suspend fun insert(account: AccountEntity)
    suspend fun update(account: AccountEntity)
    suspend fun delete(account: AccountEntity)
    fun get(id: String): Flow<AccountEntity?>
    fun get(): Flow<List<AccountEntity>>
    fun getFirst(): Flow<AccountEntity?>
}

interface CategoryRepository {
    suspend fun insert(category: CategoryEntity)
    suspend fun update(category: CategoryEntity)
    suspend fun delete(category: CategoryEntity)
    fun get(id: String): Flow<CategoryEntity?>
    fun get(): Flow<List<CategoryEntity>>
}

interface AnalyticRepository{
    fun getTotalIncome(): Flow<Int>
    fun getTotalExpense(): Flow<Int>
    fun getTotalBalance(): Flow<Int>
    fun getAccountBalance(accountId: String): Flow<Int>
}

interface AppSettingRepository {
    val username: Flow<String>
    val isFirstTime : Flow<Boolean>
    val useDynamicColor: Flow<Boolean>
    val showBalance: Flow<Boolean>
    val activeAccount : Flow<UUID?>
    suspend fun setActiveAccount(id: UUID)
    suspend fun setFirstTime(value: Boolean)
    suspend fun setUsername(username: String)
    suspend fun setDynamicTheme(value: Boolean)
    suspend fun setBalanceVisibility(value: Boolean)
}

