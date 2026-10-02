package com.fingoal.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalDto(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("targetAmount")
    val targetAmount: Double,

    @SerialName("currentAmount")
    val currentAmount: Double? = null,

    @SerialName("createdAt")
    val createdAt: Long? = null,

    @SerialName("priority")
    val priority: Int? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("localImagePath")
    val localImagePath: String
)