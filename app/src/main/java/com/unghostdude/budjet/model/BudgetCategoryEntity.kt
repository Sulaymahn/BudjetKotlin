package com.unghostdude.budjet.model

import androidx.room.Entity
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "budget_category",
    primaryKeys = ["budgetId", "categoryId"],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["budgetId", "categoryId"])
    ]
)
data class BudgetCategoryEntity(
    val budgetId: UUID,
    val categoryId: Int
)