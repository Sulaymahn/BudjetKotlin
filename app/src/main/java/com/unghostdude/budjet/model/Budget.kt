package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity("budgets")
data class Budget (
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val accountIds: List<UUID>,
    var name: String,
    val amount: Double,
    val recurrence: Recurrence,
    val categories: List<String>,
    val start: Instant?,
    val end: Instant?
)