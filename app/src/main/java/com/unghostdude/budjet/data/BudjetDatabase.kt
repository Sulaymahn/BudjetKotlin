package com.unghostdude.budjet.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unghostdude.budjet.model.Account
import com.unghostdude.budjet.model.Budget
import com.unghostdude.budjet.model.Category
import com.unghostdude.budjet.model.Transaction
import com.unghostdude.budjet.model.TransactionTemplate
import java.io.File
import java.time.Instant
import java.util.Currency
import java.util.UUID
import java.util.concurrent.Executors

class Converters {
    @TypeConverter
    fun stringToCurrency(value: String): Currency {
        return Currency.getInstance(value)
    }

    @TypeConverter
    fun currencyToString(value: Currency): String {
        return value.currencyCode
    }

    @TypeConverter
    fun stringToCategories(value: String): List<String> {
        return value.split(',')
    }

    @TypeConverter
    fun categoriesToString(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun stringToUuids(value: String): List<UUID> {
        return value.split(',').map {
            UUID.fromString(it)
        }
    }

    @TypeConverter
    fun uuidsToString(value: List<UUID>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun stringToUuid(value: String): UUID {
        return UUID.fromString(value)
    }

    @TypeConverter
    fun uuidToString(value: UUID): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToInstant(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    fun instantToString(value: Instant): Long {
        return value.toEpochMilli()
    }
}

@Database(
    entities = [
        Transaction::class,
        TransactionTemplate::class,
        Account::class,
        Budget::class,
        Category::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BudjetDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun templateDao(): TransactionTemplateDao
    abstract fun budgetDao(): BudgetDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun viewDao(): ViewDao
    abstract fun analyticDao(): AnalyticDao

//    companion object {
//        @Volatile
//        private var instance: BudjetDatabase? = null
//
//        fun getDatabase(context: Context): BudjetDatabase {
//            return instance ?: synchronized(this) {
//                Room.databaseBuilder(context, BudjetDatabase::class.java, "budjet_db")
//                    .addCallback(newDatabaseCallback(context))
//                    .build()
//                    .also {
//                        instance = it
//                    }
//            }
//        }
//
//        private fun newDatabaseCallback(context: Context) = object : Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//
//                Executors.newSingleThreadExecutor().execute {
//                    db.execSQL(
//                        "INSERT OR IGNORE INTO categories (name, icon, color) VALUES " +
//                                "('Food & Drinks', 'fastfood', -15414114)," +
//                                "('Bills & Fees', 'payments', -15414114)," +
//                                "('Transport', 'car', -15414114)," +
//                                "('Housing', 'apartment', -15414114)," +
//                                "('Entertainment', 'attractions', -15414114)," +
//                                "('Electronic', 'electronic', -15414114)," +
//                                "('School', 'school', -15414114)," +
//                                "('Health', 'medicbag', -15414114)," +
//                                "('Work', 'work', -15414114)"
//                    )
//
//                    val oldDbPath = context.filesDir.absolutePath + "/.local/share/Budjet.db3"
//                    if (File(oldDbPath).exists()) {
//                        val oldDb = SQLiteDatabase.openDatabase(
//                            oldDbPath,
//                            null,
//                            SQLiteDatabase.OPEN_READWRITE
//                        )
//
//                        oldDb.execSQL("CREATE TABLE IF NOT EXISTS temp_category_map AS SELECT * FROM TransactionEntity")
//
//                        oldDb.execSQL(
//                            "UPDATE temp_category_map SET Category = CASE Category " +
//                                    "WHEN 'Food' THEN 'Food & Drinks'" +
//                                    "WHEN 'Transportation' THEN 'Transport'" +
//                                    "WHEN 'Utility' THEN 'Bills & Fees'" +
//                                    "WHEN 'Healthcare' THEN 'Health'" +
//                                    "ELSE Category END"
//                        )
//
//                        db.execSQL("ATTACH DATABASE '$oldDbPath' AS oldDb")
//
//                        db.execSQL("INSERT OR IGNORE INTO transactions (id, accountId, destinationAccountId, currency, type, conversionRate, amount, title, note, categoryId, labels, date, dueDate, lastModified) SELECT o.Id, o.AccountId, NULL, o.Currency, CASE o.Type WHEN 1 THEN 'Expense' ELSE 'Income' END, o.ConversionRate, o.Amount, o.Title, o.Description, c.id, o.Labels, (o.Date - 621355968000000000) / 10000, (o.DueDate - 621355968000000000) / 10000, (o.LastModified - 621355968000000000) / 10000 FROM oldDb.temp_category_map o LEFT OUTER JOIN categories c ON o.Category = c.name")
//                        db.execSQL("INSERT OR IGNORE INTO templates (id, amount, title, note, type) SELECT Id, Amount, Title, Description, CASE WHEN Type = 1 THEN 'Expense' ELSE 'Income' END FROM oldDb.TransactionTemplateEntity;")
//                        db.execSQL("INSERT OR IGNORE INTO accounts SELECT * FROM oldDb.AccountEntity")
//                        db.execSQL("INSERT OR IGNORE INTO budgets (id, accountIds, name, amount, recurrence, categories, start, end) SELECT Id, AccountId, Name, Amount, CASE DurationCycle WHEN 1 THEN 'Daily' WHEN 2 THEN 'Weekly' WHEN 3 THEN 'Monthly' WHEN 4 THEN 'Yearly' ELSE 'OneTime' END, Categories, CASE WHEN StartDate IS NULL THEN NULL ELSE ((StartDate - 621355968000000000) / 10000) END, CASE WHEN EndDate IS NULL THEN NULL ELSE ((EndDate - 621355968000000000) / 10000) END FROM oldDb.BudgetEntity")
//
//                        db.execSQL("DETACH oldDb")
//                        oldDb.close()
//                    }
//                }
//            }
//        }
//    }
}