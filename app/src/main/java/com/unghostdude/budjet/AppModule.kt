package com.unghostdude.budjet

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.AnalyticRepository
import com.unghostdude.budjet.data.AppSettingRepository
import com.unghostdude.budjet.data.BudgetRepository
import com.unghostdude.budjet.data.BudjetDatabase
import com.unghostdude.budjet.data.CategoryRepository
import com.unghostdude.budjet.data.DataStoreAppSetting
import com.unghostdude.budjet.data.RoomAccountRepository
import com.unghostdude.budjet.data.RoomAnalyticRepository
import com.unghostdude.budjet.data.RoomBudgetRepository
import com.unghostdude.budjet.data.RoomCategoryRepository
import com.unghostdude.budjet.data.RoomTransactionRepository
import com.unghostdude.budjet.data.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesAppSettingRepository(@ApplicationContext context: Context): AppSettingRepository {
        return DataStoreAppSetting(context)
    }

    @Provides
    @Singleton
    fun providesAccountRepository(database: BudjetDatabase): AccountRepository {
        return RoomAccountRepository(database.accountDao())
    }

    @Provides
    @Singleton
    fun providesTransactionRepository(database: BudjetDatabase): TransactionRepository {
        return RoomTransactionRepository(database.transactionDao())
    }

    @Provides
    @Singleton
    fun providesAnalyticRepository(database: BudjetDatabase): AnalyticRepository {
        return RoomAnalyticRepository(database.analyticDao())
    }

    @Provides
    @Singleton
    fun providesBudgetRepository(database: BudjetDatabase): BudgetRepository {
        return RoomBudgetRepository(database.budgetDao())
    }

    @Provides
    @Singleton
    fun providesCategoryRepository(database: BudjetDatabase): CategoryRepository {
        return RoomCategoryRepository(database.categoryDao())
    }

    @Provides
    @Singleton
    fun providesBudjetDatabase(
        @ApplicationContext context: Context
    ): BudjetDatabase {
        return Room.databaseBuilder(context, BudjetDatabase::class.java, "budjet_db")
            .addCallback(newDatabaseCallback(context))
            .build()
    }

    private fun newDatabaseCallback(context: Context) =
        object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                Executors.newSingleThreadExecutor().execute {
                    db.execSQL(
                        "INSERT OR IGNORE INTO categories (name, icon, color) VALUES " +
                                "('Food & Drinks', '', -15414114)," +
                                "('Bills & Fees', '', -15414114)," +
                                "('Transport', '', -15414114)," +
                                "('Housing', '', -15414114)," +
                                "('Entertainment', '', -15414114)," +
                                "('Electronic', '', -15414114)," +
                                "('School', '', -15414114)," +
                                "('Health', '', -15414114)," +
                                "('Work', '', -15414114)," +
                                "('Insurance', '', -15414114)," +
                                "('Shopping', '', -15414114)," +
                                "('Personal', '', -15414114)," +
                                "('Beauty', '', -15414114)," +
                                "('Clothing', '', -15414114)," +
                                "('Internet', '', -15414114)," +
                                "('Cash', '', -15414114)"
                    )

                    val oldDbPath = context.filesDir.absolutePath + "/.local/share/Budjet.db3"
                    if (File(oldDbPath).exists()) {
                        val oldDb = SQLiteDatabase.openDatabase(
                            oldDbPath,
                            null,
                            SQLiteDatabase.OPEN_READWRITE
                        )

                        oldDb.execSQL("CREATE TABLE IF NOT EXISTS temp_category_map AS SELECT * FROM TransactionEntity")

                        oldDb.execSQL(
                            "UPDATE temp_category_map SET Category = CASE Category " +
                                    "WHEN 'Food' THEN 'Food & Drinks'" +
                                    "WHEN 'Transportation' THEN 'Transport'" +
                                    "WHEN 'Utility' THEN 'Bills & Fees'" +
                                    "WHEN 'Healthcare' THEN 'Health'" +
                                    "ELSE Category END"
                        )

                        db.execSQL("ATTACH DATABASE '$oldDbPath' AS oldDb")
                        db.execSQL("INSERT OR IGNORE INTO transactions (id, accountId, destinationAccountId, currency, type, conversionRate, amount, title, note, categoryid, categoryname, categoryicon, categorycolor, date, dueDate, lastModified, created) SELECT o.Id, o.AccountId, NULL, o.Currency, CASE o.Type WHEN 1 THEN 'Expense' ELSE 'Income' END, o.ConversionRate, o.Amount, o.Title, o.Description, c.id, c.name, c.icon, c.color, (o.Date - 621355968000000000) / 10000, (o.DueDate - 621355968000000000) / 10000, (o.LastModified - 621355968000000000) / 10000, (o.LastModified - 621355968000000000) / 10000 FROM oldDb.temp_category_map o LEFT OUTER JOIN categories c ON o.Category = c.name")
                        db.execSQL("INSERT OR IGNORE INTO templates (id, amount, title, note, type) SELECT Id, Amount, Title, Description, CASE WHEN Type = 1 THEN 'Expense' ELSE 'Income' END FROM oldDb.TransactionTemplateEntity;")
                        db.execSQL("INSERT OR IGNORE INTO accounts SELECT * FROM oldDb.AccountEntity")
                        db.execSQL("INSERT OR IGNORE INTO budgets (id, name, amount, recurrence, start, end) SELECT Id, Name, Amount, CASE DurationCycle WHEN 1 THEN 'Daily' WHEN 2 THEN 'Weekly' WHEN 3 THEN 'Monthly' WHEN 4 THEN 'Yearly' ELSE 'OneTime' END, CASE WHEN StartDate IS NULL THEN NULL ELSE ((StartDate - 621355968000000000) / 10000) END, CASE WHEN EndDate IS NULL THEN NULL ELSE ((EndDate - 621355968000000000) / 10000) END FROM oldDb.BudgetEntity")
                        db.execSQL("INSERT OR IGNORE INTO budget_account (budgetId, accountId) SELECT Id, AccountId FROM oldDb.BudgetEntity")

                        val c = db.query("SELECT Id, Categories FROM oldDb.BudgetEntity")
                        if (c.count != 0) {
                            while (c.moveToNext()) {
                                val budgetId = c.getString(0)
                                c.getString(1).split(',').map { it.trim() }.forEach { category ->
                                    val cc = when (category) {
                                        "Food" -> "Food & Drinks"
                                        "Transportation" -> "Transport"
                                        "Utility" -> "Bills & Fees"
                                        "Healthcare" -> "Health"
                                        else -> category
                                    }

                                    val categoryCursor =
                                        db.query("SELECT id FROM categories WHERE name = '${cc}'")
                                    if (categoryCursor.count >= 1) {
                                        categoryCursor.moveToFirst()
                                        val categoryId = categoryCursor.getString(0)
                                        db.execSQL("INSERT INTO budget_category (budgetId, categoryId) VALUES ('${budgetId}', '${categoryId}')")
                                    }
                                    categoryCursor.close()
                                }
                            }
                        }
                        c.close()

                        db.execSQL("DETACH oldDb")
                        oldDb.close()
                    }
                }
            }
        }
}