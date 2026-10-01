package com.fingoal.app.data.remote

import com.fingoal.app.data.remote.ApiConstants.BASE_URL
import com.fingoal.app.data.remote.dto.AuthRequest
import com.fingoal.app.data.remote.dto.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApiService(private val client: HttpClient) {

    suspend fun register(request: AuthRequest): AuthResponse {
        return client.post("$BASE_URL/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun login(request: AuthRequest): AuthResponse {
        return client.post("$BASE_URL/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}