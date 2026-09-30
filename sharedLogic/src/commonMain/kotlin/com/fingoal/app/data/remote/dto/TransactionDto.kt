package com.fingoal.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("description") val description: String?,

    val amount: Double,
    val category: String,
    val type: String,

    @SerialName("createdAt")
    val createdAt: Long
)