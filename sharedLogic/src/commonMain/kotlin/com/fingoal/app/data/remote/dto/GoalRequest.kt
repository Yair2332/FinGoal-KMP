package com.fingoal.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalRequest(
    val userId: String,
    @SerialName("name") val title: String,
    val description: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val priority: Int,
    val localImagePath: String
)