package com.fingoal.app.data.remote.dto

data class ContributionRequest(
    val goalId: String,
    val userId: String,
    val amount: Double
)