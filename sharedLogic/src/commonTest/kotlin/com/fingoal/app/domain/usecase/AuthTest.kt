package com.fingoal.app.domain.usecase

import com.fingoal.app.domain.repository.AuthRepository
import com.fingoal.app.domain.usecase.auth.LoginUseCase
import com.fingoal.app.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthTest {

    @Test
    fun loginUseCase_callsRepositoryWithCorrectData() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LoginUseCase(repository)

        val result = useCase(
            email = "test@gmail.com",
            pass = "123456"
        )

        assertTrue(result.isSuccess)
        assertEquals("user-123", result.getOrNull())

        assertEquals("test@gmail.com", repository.lastEmail)
        assertEquals("123456", repository.lastPassword)
    }

    @Test
    fun loginUseCase_returnsRepositoryFailure() = runTest {
        val repository = FakeAuthRepository()
        repository.loginResult = Result.failure(
            Exception("Credenciales inválidas")
        )

        val useCase = LoginUseCase(repository)

        val result = useCase(
            email = "test@gmail.com",
            pass = "wrong"
        )

        assertTrue(result.isFailure)
        assertEquals(
            "Credenciales inválidas",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun registerUseCase_callsRepositoryWithCorrectData() = runTest {
        val repository = FakeAuthRepository()
        val useCase = RegisterUseCase(repository)

        val result = useCase(
            email = "newuser@gmail.com",
            pass = "123456"
        )

        assertTrue(result.isSuccess)
        assertEquals("user-456", result.getOrNull())

        assertEquals("newuser@gmail.com", repository.lastEmail)
        assertEquals("123456", repository.lastPassword)
    }

    @Test
    fun registerUseCase_returnsRepositoryFailure() = runTest {
        val repository = FakeAuthRepository()
        repository.registerResult = Result.failure(
            Exception("El usuario ya existe")
        )

        val useCase = RegisterUseCase(repository)

        val result = useCase(
            email = "existing@gmail.com",
            pass = "123456"
        )

        assertTrue(result.isFailure)
        assertEquals(
            "El usuario ya existe",
            result.exceptionOrNull()?.message
        )
    }
}

private class FakeAuthRepository : AuthRepository {

    var lastEmail: String? = null
    var lastPassword: String? = null

    var loginResult: Result<String> =
        Result.success("user-123")

    var registerResult: Result<String> =
        Result.success("user-456")

    override suspend fun login(
        email: String,
        pass: String
    ): Result<String> {
        lastEmail = email
        lastPassword = pass

        return loginResult
    }

    override suspend fun register(
        email: String,
        pass: String
    ): Result<String> {
        lastEmail = email
        lastPassword = pass

        return registerResult
    }
}