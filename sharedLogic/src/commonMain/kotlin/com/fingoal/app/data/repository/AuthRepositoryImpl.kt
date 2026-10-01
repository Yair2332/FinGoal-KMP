package com.fingoal.app.data.repository

import com.fingoal.app.data.remote.AuthApiService
import com.fingoal.app.data.remote.dto.AuthRequest
import com.fingoal.app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApiService
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Result<String> = try {
        val response = api.login(AuthRequest(email, pass))
        Result.success(response.id ?: "")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun register(email: String, pass: String): Result<String> = try {
        val response = api.register(AuthRequest(email, pass))
        Result.success(response.id ?: "")
    } catch (e: Exception) {
        Result.failure(e)
    }
}