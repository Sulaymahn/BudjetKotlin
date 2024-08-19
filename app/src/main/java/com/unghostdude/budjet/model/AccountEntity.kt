package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Currency
import java.util.UUID

@Entity("accounts")
data class AccountEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: UUID,
    val name: String,
    val currency: Currency,
    val startAmount: Double,
    val created: Instant
)

data class AccountForUpdate(
    val id: UUID,
    val name: String,
    val currency: Currency,
    val balance: Double
)

data class Account(
    val id: UUID,
    val name: String,
    val currency: Currency,
    val startAmount: Double,
    val created: Instant,
    val balance: Double
)