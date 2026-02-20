package com.firsttech.assistant.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, val dueDate: String? = null,
    val isDone: Boolean = false, val createdAt: Long = System.currentTimeMillis()
)
@Entity(tableName = "memos")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String, val tags: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
