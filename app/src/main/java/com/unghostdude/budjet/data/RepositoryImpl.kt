package com.unghostdude.budjet.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.unghostdude.budjet.R
import com.unghostdude.budjet.dataStore
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.Category
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionForCard
import com.unghostdude.budjet.model.TransactionForDetail
import com.unghostdude.budjet.model.TransactionTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomTransactionRepository(private val dao: TransactionDao) :
    TransactionRepository {
    override suspend fun insert(transaction: Transaction) = dao.insert(transaction)
    override suspend fun update(transaction: Transaction) = dao.update(transaction)
    override suspend fun delete(transaction: Transaction) = dao.delete(transaction)
    override fun get(id: String): Flow<Transaction> = dao.get(id)
    override fun get(): Flow<List<Transaction>> = dao.get()
}

class RoomTemplateRepository(private val dao: TransactionTemplateDao) :
    TransactionTemplateRepository {
    override suspend fun insert(template: TransactionTemplate) = dao.insert(template)
    override suspend fun update(template: TransactionTemplate) = dao.update(template)
    override suspend fun delete(template: TransactionTemplate) = dao.delete(template)
    override fun get(id: String): Flow<TransactionTemplate> = dao.get(id)
    override fun get(): Flow<List<TransactionTemplate>> = dao.get()
}

class RoomAccountRepository(private val dao: AccountDao) :
    AccountRepository {
    override suspend fun insert(account: Account) = dao.insert(account)
    override suspend fun update(account: Account) = dao.update(account)
    override suspend fun delete(account: Account) = dao.delete(account)
    override fun get(id: String): Flow<Account> = dao.get(id)
    override fun get(): Flow<List<Account>> = dao.get()
    override fun getFirst(): Flow<Account?> = dao.getFirst()
}

class RoomBudgetRepository(private val dao: BudgetDao) :
    BudgetRepository {
    override suspend fun insert(budget: Budget) = dao.insert(budget)
    override suspend fun update(budget: Budget) = dao.update(budget)
    override suspend fun delete(budget: Budget) = dao.delete(budget)
    override fun get(id: String): Flow<Budget> = dao.get(id)
    override fun get(): Flow<List<Budget>> = dao.get()
}

class RoomCategoryRepository(private val dao: CategoryDao) :
    CategoryRepository {
    override suspend fun insert(category: Category) = dao.insert(category)
    override suspend fun update(category: Category) = dao.update(category)
    override suspend fun delete(category: Category) = dao.delete(category)
    override fun get(id: String): Flow<Category> = dao.get(id)
    override fun get(): Flow<List<Category>> = dao.get()
}

class RoomViewRepository(private val dao: ViewDao) :
    ViewRepository {
    override fun getCategory(id: String): Category? = dao.getCategory(id)

    override fun getCategories(): Flow<List<Category>> = dao.getCategories()

    override fun getTransactionCard(): Flow<List<TransactionForCard>> = dao.getTransactionCard()
    override fun getTransactionDetail(transactionId: String): Flow<TransactionForDetail?> =
        dao.getTransactionDetail(transactionId)
}

class DataStoreAppSetting(private val context: Context) : AppSettingRepository {
    override val username: Flow<String>
        get() = context.dataStore.data.map {
            it[stringPreferencesKey(context.getString(R.string.username_key))] ?: "User"
        }
    override val isFirstTime: Flow<Boolean>
        get() = context.dataStore.data.map {
            it[booleanPreferencesKey(context.getString(R.string.first_time_key))] ?: true
        }
    override val useDynamicColor: Flow<Boolean>
        get() = context.dataStore.data.map {
            it[booleanPreferencesKey(context.getString(R.string.dynamic_theme_key))] ?: false
        }
    override val showBalance: Flow<Boolean>
        get() = context.dataStore.data.map {
            it[booleanPreferencesKey(context.getString(R.string.balance_visibility_key))] ?: true
        }
    override val activeAccount: Flow<UUID?>
        get() = context.dataStore.data.map {
            val r = it[stringPreferencesKey(context.getString(R.string.active_account_key))]
            if (r != null) {
                UUID.fromString(r)
            } else {
                null
            }
        }

    override suspend fun setActiveAccount(id: UUID) {
        context.dataStore.edit {
            it[stringPreferencesKey(context.getString(R.string.active_account_key))] = id.toString()
        }
    }

    override suspend fun setFirstTime(value: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(context.getString(R.string.first_time_key))] = value
        }
    }

    override suspend fun setUsername(username: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(context.getString(R.string.username_key))] = username
        }
    }

    override suspend fun setDynamicTheme(value: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(context.getString(R.string.dynamic_theme_key))] = value
        }
    }

    override suspend fun setBalanceVisibility(value: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(context.getString(R.string.balance_visibility_key))] = value
        }
    }
}

class RoomAnalyticRepository(private val dao: AnalyticDao) : AnalyticRepository {
    override fun getTotalIncome(): Flow<Int> = dao.getIncome()
    override fun getTotalExpense(): Flow<Int> = dao.getExpense()
    override fun getTotalBalance(): Flow<Int> = dao.getTotalBalance()
}