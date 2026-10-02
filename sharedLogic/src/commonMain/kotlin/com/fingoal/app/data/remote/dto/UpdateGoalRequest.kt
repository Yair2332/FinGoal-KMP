package com.fingoal.app.data.remote.dto
import kotlinx.serialization.Serializable


@Serializable
data class UpdateGoalRequest(
    val name: String?,
    val description: String?,
    val targetAmount: Double?,
    val localImagePath: String?
)