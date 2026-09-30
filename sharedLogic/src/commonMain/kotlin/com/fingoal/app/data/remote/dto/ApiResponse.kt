package com.fingoal.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    @SerialName("id") val id: String?,
    @SerialName("message") val message: String?
)