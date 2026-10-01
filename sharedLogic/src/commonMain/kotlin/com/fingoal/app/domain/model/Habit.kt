package com.fingoal.app.domain.model


data class Habit(
    val id: Long = 0,
    val remoteId: String = "",
    val name: String,
    val description: String = "",
    val frequency: String,
    val completedToday: Boolean = false,
    val streak: Int = 0
)