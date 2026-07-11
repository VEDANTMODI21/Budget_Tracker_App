package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,
    val amount: Double,
    val type: String, // "LENT" (money given to someone) or "BORROWED" (money taken from someone)
    val description: String,
    val timestamp: Long,
    val dueDate: Long, // 0 if no due date
    val isResolved: Boolean = false
)
