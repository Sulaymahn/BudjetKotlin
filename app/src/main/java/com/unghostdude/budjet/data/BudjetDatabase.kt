package com.unghostdude.budjet.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.model.BudgetEntity
import com.unghostdude.budjet.model.BudgetCategoryEntity
import com.unghostdude.budjet.model.CategoryEntity
import com.unghostdude.budjet.model.TransactionEntity
import java.time.Instant
import java.util.Currency
import java.util.UUID

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
        TransactionEntity::class,
        AccountEntity::class,
        BudgetEntity::class,
        CategoryEntity::class,
        BudgetCategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BudjetDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun analyticDao(): AnalyticDao
}