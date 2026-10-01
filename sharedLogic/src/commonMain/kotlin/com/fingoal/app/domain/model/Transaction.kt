package com.fingoal.app.domain.model

data class Transaction(
    val id: Long,
    val remoteId: String,
    val title: String,
    val description: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val isIncome: Boolean
)