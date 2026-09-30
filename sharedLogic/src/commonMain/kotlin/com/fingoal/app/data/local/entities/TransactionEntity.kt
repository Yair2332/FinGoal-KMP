package com.fingoal.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: String = "",
    val title: String,
    val description: String = "",
    val amount: Double,
    val category: String,
    val date: Long,
    val isIncome: Boolean
)