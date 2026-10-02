package com.fingoal.app.data.repository

import com.fingoal.app.data.remote.AuthApiService
import com.fingoal.app.data.remote.dto.AuthRequest
import com.fingoal.app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApiService
) : AuthRepository {

    override suspend fun login(
        email: String,
        pass: String
    ): Result<String> = try {
        val response = api.login(AuthRequest(email, pass))

        println("FinGoal AUTH LOGIN RESPONSE: $response")

        Result.success(response.id ?: "")
    } catch (e: Exception) {
        println("FinGoal AUTH LOGIN ERROR: ${e::class.simpleName}")
        println("FinGoal AUTH LOGIN MESSAGE: ${e.message}")
        e.printStackTrace()

        Result.failure(e)
    }

    override suspend fun register(
        email: String,
        pass: String
    ): Result<String> = try {
        val response = api.register(AuthRequest(email, pass))

        println("FinGoal AUTH REGISTER RESPONSE: $response")

        Result.success(response.id ?: "")
    } catch (e: Exception) {
        println("FinGoal AUTH REGISTER ERROR: ${e::class.simpleName}")
        println("FinGoal AUTH REGISTER MESSAGE: ${e.message}")
        e.printStackTrace()

        Result.failure(e)
    }
}