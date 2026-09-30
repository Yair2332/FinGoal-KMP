package com.fingoal.app.data.remote.dto

data class HabitDto(
    val id: String,
    val title: String,
    val description: String,
    val frequency: String,
    val isActive: Boolean,
    val streak: Int,
    val lastCompletedAt: Long,
    val completedToday: Boolean,
    val userId: String
)