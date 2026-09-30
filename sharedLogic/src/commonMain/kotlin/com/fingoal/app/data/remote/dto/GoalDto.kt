package com.fingoal.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalDto(
    @SerialName("id") val id: String,
    @SerialName("name") val title: String,
    @SerialName("description") val description: String,
    @SerialName("targetAmount") val targetAmount: Double,
    @SerialName("currentAmount") val currentAmount: Double,
    @SerialName("createdAt") val createdAt: Long,
    @SerialName("priority") val priority: Int,
    @SerialName("status") val status: String,
    @SerialName("localImagePath") val localImagePath: String
)