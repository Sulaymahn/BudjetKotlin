package com.unghostdude.budjet.data


import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.Category
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionForCard
import com.unghostdude.budjet.model.TransactionForDetail
import com.unghostdude.budjet.model.TransactionTemplate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TransactionRepository {
    suspend fun insert(transaction: Transaction)
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    fun get(id: String): Flow<Transaction?>
    fun get(): Flow<List<Transaction>>
}

interface TransactionTemplateRepository {
    suspend fun insert(template: TransactionTemplate)
    suspend fun update(template: TransactionTemplate)
    suspend fun delete(template: TransactionTemplate)
    fun get(id: String): Flow<TransactionTemplate?>
    fun get(): Flow<List<TransactionTemplate>>
}

interface BudgetRepository {
    suspend fun insert(budget: Budget)
    suspend fun update(budget: Budget)
    suspend fun delete(budget: Budget)
    fun get(id: String): Flow<Budget?>
    fun get(): Flow<List<Budget>>
}

interface AccountRepository {
    suspend fun insert(account: Account)
    suspend fun update(account: Account)
    suspend fun delete(account: Account)
    fun get(id: String): Flow<Account?>
    fun get(): Flow<List<Account>>
    fun getFirst(): Flow<Account?>
}

interface CategoryRepository {
    suspend fun insert(category: Category)
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    fun get(id: String): Flow<Category?>
    fun get(): Flow<List<Category>>
}

interface ViewRepository {
    fun getCategory(id: String): Category?
    fun getCategories(): Flow<List<Category>>
    fun getTransactionCard(): Flow<List<TransactionForCard>>
    fun getTransactionDetail(transactionId: String): Flow<TransactionForDetail?>
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

interface AnalyticRepository{
    fun getTotalIncome(): Flow<Int>
    fun getTotalExpense(): Flow<Int>
    fun getTotalBalance(): Flow<Int>
}