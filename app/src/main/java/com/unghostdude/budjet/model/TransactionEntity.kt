package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Instant
import java.util.Currency
import java.util.UUID

@Entity(
    tableName = "transactions"
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val accountId: UUID,
    val destinationAccountId: UUID?,
    val currency: Currency,
    val type: TransactionType,
    val conversionRate: Double?,
    val amount: Double,
    val title: String,
    val note: String,
    @ColumnInfo(index = true)
    val categoryId: Int,
    val date: Instant,
    val created: Instant,
    val dueDate: Instant?,
    val lastModified: Instant
)

data class Transaction(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: AccountEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)