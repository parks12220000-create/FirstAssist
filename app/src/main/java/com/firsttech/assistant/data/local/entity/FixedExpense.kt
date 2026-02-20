package com.firsttech.assistant.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "fixed_expenses")
data class FixedExpense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val amount: Int, val paymentDay: Int,
    val category: String = "기타", val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
