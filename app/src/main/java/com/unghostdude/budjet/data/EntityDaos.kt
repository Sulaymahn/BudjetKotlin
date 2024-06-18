package com.unghostdude.budjet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.Category
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionForCard
import com.unghostdude.budjet.model.TransactionForDetail
import com.unghostdude.budjet.model.TransactionTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun get(id: String): Flow<Transaction>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun get(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getWithAccountId(accountId: String): Flow<List<Transaction>>
}

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * from budgets WHERE id = :id")
    fun get(id: String): Flow<Budget>

    @Query("SELECT * from budgets ORDER BY name")
    fun get(): Flow<List<Budget>>
}

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("SELECT * from accounts WHERE id = :id")
    fun get(id: String): Flow<Account>

    @Query("SELECT * from accounts ORDER BY name")
    fun get(): Flow<List<Account>>

    @Query("SELECT * from accounts LIMIT 1")
    fun getFirst(): Flow<Account?>
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
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * from categories WHERE id = :id")
    fun get(id: String): Flow<Category>

    @Query("SELECT * from categories ORDER BY name")
    fun get(): Flow<List<Category>>
}

@Dao
interface ViewDao {
    @Query("SELECT * from categories WHERE id = :id")
    fun getCategory(id: String): Category?

    @Query("SELECT * from categories ORDER BY name")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT t.id, t.title, t.currency, t.amount, t.type, c.name categoryName, c.icon, c.color, t.date FROM transactions t LEFT OUTER JOIN categories c ON t.categoryId = c.id ORDER BY date DESC")
    fun getTransactionCard(): Flow<List<TransactionForCard>>

    @Query("SELECT t.title, t.currency, t.amount, t.type, c.name categoryName, t.note, t.date FROM transactions t LEFT OUTER JOIN categories c ON t.categoryId = c.id WHERE t.id = :transactionId")
    fun getTransactionDetail(transactionId: String): Flow<TransactionForDetail?>
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