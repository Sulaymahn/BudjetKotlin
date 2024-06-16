package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Currency
import java.util.UUID

@Entity(
    tableName = "transactions"
)
data class Transaction(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val accountId: UUID,
    val destinationAccountId: UUID?,
    val currency: Currency,
    val type: TransactionType,
    val conversionRate: Double?,
    val amount: Double,
    val title: String?,
    val note: String?,
    @ColumnInfo(index = true)
    val categoryId: Int,
    val labels: List<String>,
    @ColumnInfo(index = true)
    val date: Instant,
    val dueDate: Instant?,
    val lastModified: Instant
)

data class TransactionForCard(
    val id: String,
    val title: String?,
    val currency: Currency,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String?,
    val icon: String?,
    val color: Long?,
    val date: Instant
)

data class TransactionForDetail(
    val title: String?,
    val currency: Currency,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String,
    val note: String?,
    val date: Instant
)