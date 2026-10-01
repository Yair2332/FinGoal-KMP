package com.fingoal.app.domain.repository

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<String>
    suspend fun register(email: String, pass: String): Result<String>
}