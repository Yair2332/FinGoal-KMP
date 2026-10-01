package com.fingoal.app.domain.usecase.auth

import com.fingoal.app.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String) =
        repository.register(email, pass)
}