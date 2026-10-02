package com.fingoal.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val id: String,
    val email: String,
    val message: String?
)