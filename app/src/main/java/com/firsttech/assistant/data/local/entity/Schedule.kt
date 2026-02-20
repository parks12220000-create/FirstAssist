package com.firsttech.assistant.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, val date: String, val time: String? = null,
    val location: String? = null, val memo: String? = null,
    val isRecurring: Boolean = false, val createdAt: Long = System.currentTimeMillis()
)
