package com.fingoal.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ToggleHabitRequest(
    val todayStr: String
)