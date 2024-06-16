package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("templates")
data class TransactionTemplate(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val amount: Double,
    val title: String,
    val note: String,
    val type: TransactionType
)