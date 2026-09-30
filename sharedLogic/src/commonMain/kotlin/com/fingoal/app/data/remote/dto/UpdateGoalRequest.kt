package com.fingoal.app.data.remote.dto

data class UpdateGoalRequest(
    val name: String?,
    val description: String?,
    val targetAmount: Double?,
    val localImagePath: String?
)