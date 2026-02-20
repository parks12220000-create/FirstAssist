package com.firsttech.assistant.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int, val store: String, val category: String,
    val cardName: String? = null, val date: String, val time: String? = null,
    val isFixed: Boolean = false, val createdAt: Long = System.currentTimeMillis()
)
