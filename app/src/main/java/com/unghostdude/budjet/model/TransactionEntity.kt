package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
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

data class DetailedTransaction(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: AccountEntity,

    @Relation(
        parentColumn = "destinationAccountId",
        entityColumn = "id"
    )
    val destinationAccount: AccountEntity?,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

data class Transaction(
    val id: UUID,
    val account: AccountEntity,
    val destinationAccount: AccountEntity?,
    val currency: Currency,
    val type: TransactionType,
    val amount: Double,
    val title: String,
    val note: String,
    val category: CategoryEntity,
    val date: ZonedDateTime,
    val created: ZonedDateTime,
    val dueDate: ZonedDateTime?,
    val lastModified: ZonedDateTime
)

data class TransactionForCreation(
    val account: AccountEntity,
    val destinationAccount: AccountEntity?,
    val type: TransactionType,
    val amount: Double,
    val title: String,
    val note: String,
    val category: CategoryEntity,
    val date: ZonedDateTime,
    val dueDate: ZonedDateTime?
)

data class TransactionForUpdate(
    val id: UUID,
    val account: AccountEntity,
    val destinationAccount: AccountEntity?,
    val type: TransactionType,
    val amount: Double,
    val title: String,
    val note: String,
    val category: CategoryEntity,
    val date: ZonedDateTime,
    val dueDate: ZonedDateTime?
)