package com.fingoal.app.data.remote

import com.fingoal.app.data.remote.ApiConstants.BASE_URL
import com.fingoal.app.data.remote.dto.ApiResponse
import com.fingoal.app.data.remote.dto.TransactionDto
import com.fingoal.app.data.remote.dto.TransactionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TransactionApiService(private val client: HttpClient) {

    suspend fun getTransactions(userId: String): List<TransactionDto> {
        return client.get("$BASE_URL/api/transactions/$userId").body()
    }

    suspend fun createTransaction(request: TransactionRequest): TransactionDto {
        return client.post("$BASE_URL/api/transactions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateTransaction(transactionId: String, request: TransactionRequest): TransactionDto {
        return client.patch("$BASE_URL/api/transactions/$transactionId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteTransaction(transactionId: String): ApiResponse {
        return client.delete("$BASE_URL/api/transactions/$transactionId").body()
    }
}