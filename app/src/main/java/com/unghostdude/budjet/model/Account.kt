package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Currency
import java.util.UUID

@Entity("accounts")
data class Account(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val name: String,
    val defaultCurrency: Currency,
    val startAmount: Double
)