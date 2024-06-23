package com.unghostdude.budjet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.BudgetCategoryEntity
import com.unghostdude.budjet.model.BudgetWithAccountAndCategories
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionEntity
import com.unghostdude.budjet.model.TransactionTemplate
import com.unghostdude.budjet.model.TransactionWithAccountAndCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun get(id: String): Flow<TransactionWithAccountAndCategory>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun get(): Flow<List<TransactionWithAccountAndCategory>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getWithAccountId(accountId: String): Flow<List<TransactionWithAccountAndCategory>>
}

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: BudgetEntity)

    @Insert
    suspend fun insert(items: List<BudgetCategoryEntity>)

    @androidx.room.Transaction
    suspend fun insert(budget: BudgetEntity, items: List<CategoryEntity>) {
        insert(budget)
        val i = items.map { item ->
            BudgetCategoryEntity(
                budgetId = budget.id,
                categoryId = item.id
            )
        }
        insert(i)
    }

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @androidx.room.Transaction
    @Query("SELECT * from budgets WHERE id = :id")
    fun get(id: String): Flow<BudgetWithAccountAndCategories>

    @androidx.room.Transaction
    @Query("SELECT * from budgets ORDER BY name")
    fun get(): Flow<List<BudgetWithAccountAndCategories>>

    @androidx.room.Transaction
    @Query("SELECT * from budgets WHERE accountId = :accountId ORDER BY name")
    fun getWithAccountId(accountId: String): Flow<List<BudgetWithAccountAndCategories>>
}

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: AccountEntity)

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * from accounts WHERE id = :id")
    fun get(id: String): Flow<AccountEntity>

    @Query("SELECT * from accounts ORDER BY name")
    fun get(): Flow<List<AccountEntity>>

    @Query("SELECT * from accounts LIMIT 1")
    fun getFirst(): Flow<AccountEntity?>
}

@Dao
interface TransactionTemplateDao {
    @Insert
    suspend fun insert(template: TransactionTemplate)

    @Update
    suspend fun update(template: TransactionTemplate)

    @Delete
    suspend fun delete(template: TransactionTemplate)

    @Query("SELECT * from templates WHERE id = :id")
    fun get(id: String): Flow<TransactionTemplate>

    @Query("SELECT * from templates")
    fun get(): Flow<List<TransactionTemplate>>
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