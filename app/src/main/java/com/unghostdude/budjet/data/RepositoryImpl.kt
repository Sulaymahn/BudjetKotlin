package com.unghostdude.budjet.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.unghostdude.budjet.R
import com.unghostdude.budjet.contract.TransactionRepository
import com.unghostdude.budjet.dataStore
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.AppTheme
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.BudgetCategoryEntity
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionEntity
import com.unghostdude.budjet.model.DetailedTransaction
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionForCreation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID



class RoomAccountRepository(private val dao: AccountDao) :
    AccountRepository {
    override suspend fun insert(account: AccountEntity) = dao.insert(account)
    override suspend fun update(account: AccountEntity) {
        dao.update(account)
        dao.updateTransactionCurrency(account.id.toString())
    }

    override suspend fun delete(account: AccountEntity) {
        dao.delete(account)
        dao.deleteTransactions(account.id.toString())
    }

    override fun get(id: UUID): Flow<AccountEntity> = dao.get(id.toString())
    override fun getWithBalance(id: UUID): Flow<Account> = dao.getWithBalance(id.toString())
    override fun getWithBalance(): Flow<List<Account>> = dao.getWithBalance()
    override fun get(): Flow<List<AccountEntity>> = dao.get()
    override fun getFirst(): Flow<AccountEntity?> = dao.getFirst()
}

class RoomBudgetRepository(private val dao: BudgetDao) :
    BudgetRepository {
    override suspend fun insert(budget: BudgetEntity) = dao.insert(budget)
    override suspend fun insert(items: List<BudgetCategoryEntity>) = dao.insert(items)
    override suspend fun insert(budget: BudgetEntity, categoryIds: List<Int>) =
        dao.insert(budget, categoryIds)

    override suspend fun update(budget: BudgetEntity) = dao.update(budget)
    override suspend fun update(budget: BudgetEntity, categoryIds: List<Int>) =
        dao.update(budget, categoryIds)

    override suspend fun delete(budget: BudgetEntity) = dao.delete(budget)
    override fun get(id: String): Flow<Budget> = dao.get(id)
    override fun get(): Flow<List<Budget>> = dao.get()
}

class RoomCategoryRepository(private val dao: CategoryDao) :
    CategoryRepository {
    override suspend fun insert(category: CategoryEntity) = dao.insert(category)
    override suspend fun update(category: CategoryEntity) = dao.update(category)
    override suspend fun delete(category: CategoryEntity) = dao.delete(category)
    override fun get(id: String): Flow<CategoryEntity> = dao.get(id)
    override fun get(): Flow<List<CategoryEntity>> = dao.get()
}

class RoomAnalyticRepository(private val dao: AnalyticDao) : AnalyticRepository {
    override fun getTotalIncome(): Flow<Int> = dao.getIncome()
    override fun getTotalExpense(): Flow<Int> = dao.getExpense()
    override fun getTotalBalance(): Flow<Int> = dao.getTotalBalance()
    override fun getAccountBalance(accountId: String): Flow<Int> = dao.getAccountBalance(accountId)
}

class DataStoreAppSetting(private val context: Context) : AppSettingRepository {
    override val username: Flow<String>
        get() = context.dataStore.data.map {
            it[stringPreferencesKey(context.getString(R.string.username_key))] ?: ""
        }
    override val isFirstTime: Flow<Boolean>
        get() = context.dataStore.data.map {
            it[booleanPreferencesKey(context.getString(R.string.first_time_key))] ?: true
        }
    override val theme: Flow<AppTheme>
        get() = context.dataStore.data.map {
            val ordinal = it[intPreferencesKey(context.getString(R.string.theme_key))] ?: 0
            AppTheme.entries[ordinal]
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

    override suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit {
            it[intPreferencesKey(context.getString(R.string.theme_key))] = theme.ordinal
        }
    }

    override suspend fun setBalanceVisibility(value: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(context.getString(R.string.balance_visibility_key))] = value
        }
    }
}

