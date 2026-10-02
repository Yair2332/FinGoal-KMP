package com.fingoal.app.data.remote.dto
import kotlinx.serialization.Serializable


@Serializable
data class UpdateHabitRequest(
    val title: String,
    val description: String,
    val frequency: String,
    val isActive: Boolean
)