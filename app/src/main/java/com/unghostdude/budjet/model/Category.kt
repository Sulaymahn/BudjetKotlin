package com.unghostdude.budjet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val id: Int = 0,
    val name: String,
    val icon: String,
    val color: Int
)