package com.fingoal.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ToggleHabitResponse(
    val completedToday: Boolean,
    val streak: Int
)