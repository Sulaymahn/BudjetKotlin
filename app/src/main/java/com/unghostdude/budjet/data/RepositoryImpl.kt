package com.unghostdude.budjet.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.unghostdude.budjet.R
import com.unghostdude.budjet.dataStore
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.BudgetCategoryEntity
import com.unghostdude.budjet.model.BudgetWithAccountAndCategories
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionEntity
import com.unghostdude.budjet.model.TransactionTemplate
import com.unghostdude.budjet.model.TransactionWithAccountAndCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomTransactionRepository(private val dao: TransactionDao) :
    TransactionRepository {
    override suspend fun insert(transaction: TransactionEntity) = dao.insert(transaction)
    override suspend fun update(transaction: TransactionEntity) = dao.update(transaction)
    override suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
    override fun get(id: String): Flow<TransactionWithAccountAndCategory> = dao.get(id)
    override fun get(): Flow<List<TransactionWithAccountAndCategory>> = dao.get()
    override fun getWithAccountId(accountId: String): Flow<List<TransactionWithAccountAndCategory>> = dao.getWithAccountId(accountId)
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
    override suspend fun insert(account: AccountEntity) = dao.insert(account)
    override suspend fun update(account: AccountEntity) = dao.update(account)
    override suspend fun delete(account: AccountEntity) = dao.delete(account)
    override fun get(id: String): Flow<AccountEntity> = dao.get(id)
    override fun get(): Flow<List<AccountEntity>> = dao.get()
    override fun getFirst(): Flow<AccountEntity?> = dao.getFirst()
}

class RoomBudgetRepository(private val dao: BudgetDao) :
    BudgetRepository {
    override suspend fun insert(budget: BudgetEntity) = dao.insert(budget)
    override suspend fun insert(items: List<BudgetCategoryEntity>) = dao.insert(items)
    override suspend fun insert(budget: BudgetEntity, items: List<CategoryEntity>) = dao.insert(budget, items)
    override suspend fun update(budget: BudgetEntity) = dao.update(budget)
    override suspend fun delete(budget: BudgetEntity) = dao.delete(budget)
    override fun get(id: String): Flow<BudgetWithAccountAndCategories> = dao.get(id)
    override fun get(): Flow<List<BudgetWithAccountAndCategories>> = dao.get()
    override fun getWithAccountId(id: String): Flow<List<BudgetWithAccountAndCategories>> = dao.getWithAccountId(id)
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
            it[stringPreferencesKey(context.getString(R.string.username_key))] ?: "User"
        }
    override val isFirstTime: Flow<Boolean>
        get() = context.dataStore.data.map {
            it[booleanPreferencesKey(context.getString(R.string.first_time_key))] ?: true
        }
    override val useDynamicColor: Flow<Boolean>
        get() = context.dataStore.data.map {
            it[booleanPreferencesKey(context.getString(R.string.dynamic_theme_key))] ?: true
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

