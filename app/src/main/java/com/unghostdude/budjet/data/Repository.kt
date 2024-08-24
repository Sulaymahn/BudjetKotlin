package com.unghostdude.budjet.data

import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.AppTheme
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.BudgetCategoryEntity
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.CategoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BudgetRepository {
    suspend fun insert(budget: BudgetEntity)
    suspend fun insert(items: List<BudgetCategoryEntity>)
    suspend fun insert(budget: BudgetEntity, categoryIds: List<Int>)
    suspend fun update(budget: BudgetEntity)
    suspend fun update(budget: BudgetEntity, categoryIds: List<Int>)
    suspend fun delete(budget: BudgetEntity)
    fun get(id: String): Flow<Budget?>
    fun get(): Flow<List<Budget>>
}

interface AccountRepository {
    suspend fun insert(account: AccountEntity)
    suspend fun update(account: AccountEntity)
    suspend fun delete(account: AccountEntity)
    fun get(id: UUID): Flow<AccountEntity?>
    fun getWithBalance(id: UUID): Flow<Account>
    fun getWithBalance(): Flow<List<Account>>
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

interface AnalyticRepository {
    fun getTotalIncome(): Flow<Int>
    fun getTotalExpense(): Flow<Int>
    fun getTotalBalance(): Flow<Int>
    fun getAccountBalance(accountId: String): Flow<Int>
}

interface AppSettingRepository {
    val username: Flow<String>
    val isFirstTime: Flow<Boolean>
    val theme: Flow<AppTheme>
    val showBalance: Flow<Boolean>
    val activeAccount: Flow<UUID?>
    suspend fun setActiveAccount(id: UUID)
    suspend fun setFirstTime(value: Boolean)
    suspend fun setUsername(username: String)
    suspend fun setTheme(theme: AppTheme)
    suspend fun setBalanceVisibility(value: Boolean)
}

