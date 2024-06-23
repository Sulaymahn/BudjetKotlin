package com.unghostdude.budjet.model

import androidx.room.Entity
import java.util.UUID

@Entity(
    tableName = "budget_category",
    primaryKeys = ["budgetId", "categoryId"]
)
data class BudgetCategoryEntity(
    val budgetId: UUID,
    val categoryId: Int
)