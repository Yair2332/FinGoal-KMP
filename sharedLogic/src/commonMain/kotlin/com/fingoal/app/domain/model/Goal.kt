package com.fingoal.app.domain.model


data class Goal(
    val id: Long = 0,
    val remoteId: String = "",
    val title: String,
    val description: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val createdAt: Long,
    val priority: Int,
    val status: String,
    val localImagePath: String
)