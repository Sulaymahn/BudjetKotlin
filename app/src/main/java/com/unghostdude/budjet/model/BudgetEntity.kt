package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Instant
import java.util.UUID

@Entity("budgets")
data class BudgetEntity (
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val accountId: UUID,
    var name: String,
    val amount: Double,
    val cycleSize: Long,
    val cycle: BudgetCycle,
    val created: Instant,
    val start: Instant,
    val end: Instant?
)

data class Budget(
    @Embedded
    val budget: BudgetEntity,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: AccountEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BudgetCategoryEntity::class,
            parentColumn = "budgetId",
            entityColumn = "categoryId"
        )
    )
    var categories: List<CategoryEntity>
)