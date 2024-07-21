package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Currency
import java.util.UUID

@Entity("accounts")
data class AccountEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val name: String,
    val currency: Currency,
    val startAmount: Double
)

data class AccountWithBalance(
    val id: UUID,
    val name: String,
    val currency: Currency,
    val startAmount: Double,
    val balance: Double
)

data class AccountWithBudgets(
    @Embedded
    val account: AccountEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val budgets: List<BudgetEntity>
)