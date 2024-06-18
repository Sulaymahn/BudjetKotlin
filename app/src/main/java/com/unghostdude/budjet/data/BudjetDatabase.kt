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
    fun uuidsToString(value: List<UUID?>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun stringToUuid(value: String?): UUID? {
        return try {
            UUID.fromString(value)
        } catch (_: Exception) {
            null
        }
    }

    @TypeConverter
    fun uuidToString(value: UUID?): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToInstant(value: Long?): Instant? {
        return if (value != null) Instant.ofEpochMilli(value) else null
    }

    @TypeConverter
    fun instantToString(value: Instant?): Long? {
        return value?.toEpochMilli()
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
}