package com.unghostdude.budjet

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unghostdude.budjet.data.AccountRepository
import com.unghostdude.budjet.data.BudjetDatabase
import com.unghostdude.budjet.data.RoomAccountRepository
import com.unghostdude.budjet.data.RoomTransactionRepository
import com.unghostdude.budjet.data.RoomViewRepository
import com.unghostdude.budjet.data.TransactionRepository
import com.unghostdude.budjet.data.ViewRepository
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
    fun providesViewRepository(database: BudjetDatabase): ViewRepository {
        return RoomViewRepository(database.viewDao())
    }

    @Provides
    @Singleton
    fun providesBudjetDatabase(@ApplicationContext context: Context): BudjetDatabase {
        Log.e("di", "providing db instance")
        return Room.databaseBuilder(context, BudjetDatabase::class.java, "budjet_db")
            .addCallback(newDatabaseCallback(context))
            .build()
    }

    private fun newDatabaseCallback(context: Context) = object : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            Executors.newSingleThreadExecutor().execute {
                db.execSQL(
                    "INSERT OR IGNORE INTO categories (name, icon, color) VALUES " +
                            "('Food & Drinks', 'fastfood', -15414114)," +
                            "('Bills & Fees', 'payments', -15414114)," +
                            "('Transport', 'car', -15414114)," +
                            "('Housing', 'apartment', -15414114)," +
                            "('Entertainment', 'attractions', -15414114)," +
                            "('Electronic', 'electronic', -15414114)," +
                            "('School', 'school', -15414114)," +
                            "('Health', 'medicbag', -15414114)," +
                            "('Work', 'work', -15414114)"
                )

                Log.e("db", "new db callback")

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

                    db.execSQL("INSERT OR IGNORE INTO transactions (id, accountId, destinationAccountId, currency, type, conversionRate, amount, title, note, categoryId, labels, date, dueDate, lastModified) SELECT o.Id, o.AccountId, NULL, o.Currency, CASE o.Type WHEN 1 THEN 'Expense' ELSE 'Income' END, o.ConversionRate, o.Amount, o.Title, o.Description, c.id, o.Labels, (o.Date - 621355968000000000) / 10000, (o.DueDate - 621355968000000000) / 10000, (o.LastModified - 621355968000000000) / 10000 FROM oldDb.temp_category_map o LEFT OUTER JOIN categories c ON o.Category = c.name")
                    db.execSQL("INSERT OR IGNORE INTO templates (id, amount, title, note, type) SELECT Id, Amount, Title, Description, CASE WHEN Type = 1 THEN 'Expense' ELSE 'Income' END FROM oldDb.TransactionTemplateEntity;")
                    db.execSQL("INSERT OR IGNORE INTO accounts SELECT * FROM oldDb.AccountEntity")
                    db.execSQL("INSERT OR IGNORE INTO budgets (id, accountIds, name, amount, recurrence, categories, start, end) SELECT Id, AccountId, Name, Amount, CASE DurationCycle WHEN 1 THEN 'Daily' WHEN 2 THEN 'Weekly' WHEN 3 THEN 'Monthly' WHEN 4 THEN 'Yearly' ELSE 'OneTime' END, Categories, CASE WHEN StartDate IS NULL THEN NULL ELSE ((StartDate - 621355968000000000) / 10000) END, CASE WHEN EndDate IS NULL THEN NULL ELSE ((EndDate - 621355968000000000) / 10000) END FROM oldDb.BudgetEntity")

                    db.execSQL("DETACH oldDb")
                    oldDb.close()
                }
            }
        }
    }
}