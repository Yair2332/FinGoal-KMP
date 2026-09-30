package com.fingoal.app.data.remote.dto

data class UpdateHabitRequest(
    val title: String,
    val description: String,
    val frequency: String,
    val isActive: Boolean
)