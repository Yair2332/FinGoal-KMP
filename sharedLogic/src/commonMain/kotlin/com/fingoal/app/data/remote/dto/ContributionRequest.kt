package com.fingoal.app.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class ContributionRequest(
    val goalId: String,
    val userId: String,
    val amount: Double
)