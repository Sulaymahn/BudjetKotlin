package com.unghostdude.budjet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.BudgetCategoryEntity
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionEntity
import com.unghostdude.budjet.model.DetailedTransaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun get(id: String): Flow<DetailedTransaction>

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun get(): Flow<List<DetailedTransaction>>

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getByAccount(accountId: String): Flow<List<DetailedTransaction>>

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE categoryId IN (:categoryIds) ORDER BY date DESC")
    fun getByCategory(categoryIds: List<Int>): Flow<List<DetailedTransaction>>
}

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: BudgetEntity)

    @Insert
    suspend fun insert(budget: BudgetCategoryEntity)

    @Insert
    suspend fun insert(items: List<BudgetCategoryEntity>)

    @androidx.room.Transaction
    suspend fun insert(budget: BudgetEntity, categoryIds: List<Int>) {
        insert(budget)
        val ids = categoryIds.map { id ->
            BudgetCategoryEntity(
                budgetId = budget.id,
                categoryId = id
            )
        }
        insert(ids)
    }

    @Update
    suspend fun update(budget: BudgetEntity)

    @androidx.room.Transaction
    suspend fun update(budget: BudgetEntity, categories: List<Int>) {
        update(budget)
        val ids = categories.map { id ->
            BudgetCategoryEntity(
                budgetId = budget.id,
                categoryId = id
            )
        }

        deleteCategories(budget.id)
        insert(ids)
    }

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("DELETE FROM budget_category WHERE budgetId = :budgetId")
    suspend fun deleteCategories(budgetId: UUID)

    @androidx.room.Transaction
    @Query("SELECT * from budgets WHERE id = :id")
    fun get(id: String): Flow<Budget>

    @androidx.room.Transaction
    @Query("SELECT * from budgets ORDER BY name")
    fun get(): Flow<List<Budget>>

}

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: AccountEntity)

    @Update
    suspend fun update(account: AccountEntity)

    @Query("UPDATE transactions SET currency = (SELECT currency FROM accounts WHERE id = :id LIMIT 1) WHERE accountId = :id")
    suspend fun updateTransactionCurrency(id: String)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteTransactions(accountId: String)

    @Query("SELECT * from accounts WHERE id = :id")
    fun get(id: String): Flow<AccountEntity>

    @Query("SELECT accounts.id, name, currency, startAmount, accounts.created, balance FROM accounts INNER JOIN (SELECT a.id, COALESCE(SUM(CASE WHEN t.type = 'Income' THEN t.amount WHEN t.type = 'Expense' THEN -t.amount ELSE 0 END), 0) AS balance FROM accounts AS a LEFT JOIN transactions AS t ON a.id = t.accountId GROUP BY a.id) AS q ON accounts.id = q.id WHERE accounts.id = :id")
    fun getWithBalance(id: String): Flow<Account>

    @Query("SELECT accounts.id, name, currency, startAmount, accounts.created, balance FROM accounts INNER JOIN (SELECT a.id, COALESCE(SUM(CASE WHEN t.type = 'Income' THEN t.amount WHEN t.type = 'Expense' THEN -t.amount ELSE 0 END), 0) AS balance FROM accounts AS a LEFT JOIN transactions AS t ON a.id = t.accountId GROUP BY a.id) AS q ON accounts.id = q.id")
    fun getWithBalance(): Flow<List<Account>>

    @Query("SELECT * from accounts ORDER BY name")
    fun get(): Flow<List<AccountEntity>>


    @Query("SELECT * from accounts LIMIT 1")
    fun getFirst(): Flow<AccountEntity?>
}

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: CategoryEntity)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("SELECT * from categories WHERE id = :id")
    fun get(id: String): Flow<CategoryEntity>

    @Query("SELECT * from categories ORDER BY name")
    fun get(): Flow<List<CategoryEntity>>
}

@Dao
interface AnalyticDao {
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Income'")
    fun getIncome(): Flow<Int>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense'")
    fun getExpense(): Flow<Int>

    @Query("SELECT SUM(CASE WHEN type = 'Income' THEN amount ELSE 0 END) - SUM(CASE WHEN type = 'Expense' THEN amount ELSE 0 END) FROM transactions")
    fun getTotalBalance(): Flow<Int>

    @Query("SELECT SUM(CASE WHEN type = 'Income' THEN amount ELSE 0 END) - SUM(CASE WHEN type = 'Expense' THEN amount ELSE 0 END) FROM transactions WHERE accountId = :accountId")
    fun getAccountBalance(accountId: String): Flow<Int>
}