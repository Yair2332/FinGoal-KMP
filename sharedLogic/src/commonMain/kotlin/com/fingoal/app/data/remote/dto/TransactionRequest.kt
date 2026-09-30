package com.fingoal.app.data.remote.dto

data class TransactionRequest(
    val userId: String,
    val title: String,
    val description: String = "",
    val amount: Double,
    val category: String,
    val type: String
)