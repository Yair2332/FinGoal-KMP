package com.fingoal.app.ui.screens

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.domain.repository.AuthRepository
import com.fingoal.app.domain.usecase.auth.LoginUseCase
import com.fingoal.app.domain.usecase.auth.RegisterUseCase
import com.fingoal.app.ui.screens.auth.AuthUiState
import com.fingoal.app.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthViewModelTest {

    @Test
    fun initialState_isIdle() {
        val viewModel = createViewModel()

        assertEquals(
            AuthUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun login_withEmptyFields_returnsError() {
        val viewModel = createViewModel()

        viewModel.login("", "")

        assertEquals(
            AuthUiState.Error("Completa los campos"),
            viewModel.uiState.value
        )
    }

    @Test
    fun login_withInvalidEmail_returnsError() {
        val viewModel = createViewModel()

        viewModel.login("correo-invalido", "1234567")

        assertEquals(
            AuthUiState.Error("Correo inválido"),
            viewModel.uiState.value
        )
    }

    @Test
    fun login_withShortPassword_returnsError() {
        val viewModel = createViewModel()

        viewModel.login("test@gmail.com", "123456")

        assertEquals(
            AuthUiState.Error("Contraseña muy corta"),
            viewModel.uiState.value
        )
    }

    @Test
    fun login_success_returnsSuccess() = runTest {
        val repository = FakeAuthRepository()
        repository.loginResult = Result.success("user-123")

        val dataStore = TestDataStore()
        val userPreferences = UserPreferences(dataStore)

        val viewModel = createViewModel(
            repository = repository,
            userPreferences = userPreferences
        )

        viewModel.login(
            email = "test@gmail.com",
            pass = "1234567"
        )

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Success("user-123"),
            viewModel.uiState.value
        )

        assertEquals(
            "user-123",
            userPreferences.userId.first()
        )
    }

    @Test
    fun login_withTimeout_returnsSpecificError() = runTest {
        val repository = FakeAuthRepository()

        repository.loginResult = Result.failure(
            Exception("Connection timeout")
        )

        val viewModel = createViewModel(repository = repository)

        viewModel.login(
            email = "test@gmail.com",
            pass = "1234567"
        )

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Error(
                "La conexión tardó demasiado. Intentá de nuevo."
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun login_withGenericError_returnsError() = runTest {
        val repository = FakeAuthRepository()

        repository.loginResult = Result.failure(
            Exception("Error del servidor")
        )

        val viewModel = createViewModel(repository = repository)

        viewModel.login(
            email = "test@gmail.com",
            pass = "1234567"
        )

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Error(
                "No se pudo iniciar sesión. Revisá tus datos y conexión."
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun register_withEmptyFields_returnsError() {
        val viewModel = createViewModel()

        viewModel.register(
            email = "",
            pass = "",
            confirm = ""
        )

        assertEquals(
            AuthUiState.Error("Completa los campos"),
            viewModel.uiState.value
        )
    }

    @Test
    fun register_withInvalidEmail_returnsError() {
        val viewModel = createViewModel()

        viewModel.register(
            email = "correo-invalido",
            pass = "1234567",
            confirm = "1234567"
        )

        assertEquals(
            AuthUiState.Error("Correo inválido"),
            viewModel.uiState.value
        )
    }

    @Test
    fun register_withShortPassword_returnsError() {
        val viewModel = createViewModel()

        viewModel.register(
            email = "test@gmail.com",
            pass = "123456",
            confirm = "123456"
        )

        assertEquals(
            AuthUiState.Error("Contraseña mínimo 6 caracteres"),
            viewModel.uiState.value
        )
    }

    @Test
    fun register_withDifferentPasswords_returnsError() {
        val viewModel = createViewModel()

        viewModel.register(
            email = "test@gmail.com",
            pass = "1234567",
            confirm = "7654321"
        )

        assertEquals(
            AuthUiState.Error("Las contraseñas no coinciden"),
            viewModel.uiState.value
        )
    }

    @Test
    fun register_success_returnsSuccess() = runTest {
        val repository = FakeAuthRepository()

        repository.registerResult = Result.success("user-456")

        val dataStore = TestDataStore()
        val userPreferences = UserPreferences(dataStore)

        val viewModel = createViewModel(
            repository = repository,
            userPreferences = userPreferences
        )

        viewModel.register(
            email = "newuser@gmail.com",
            pass = "1234567",
            confirm = "1234567"
        )

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Success("user-456"),
            viewModel.uiState.value
        )

        assertEquals(
            "user-456",
            userPreferences.userId.first()
        )
    }

    @Test
    fun register_withError_returnsError() = runTest {
        val repository = FakeAuthRepository()

        repository.registerResult = Result.failure(
            Exception("Error de conexión")
        )

        val viewModel = createViewModel(repository = repository)

        viewModel.register(
            email = "test@gmail.com",
            pass = "1234567",
            confirm = "1234567"
        )

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Error(
                "No se pudo completar el registro. Revisá tu conexión e intentá de nuevo."
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_returnsIdle() {
        val viewModel = createViewModel()

        viewModel.login("", "")

        assertTrue(
            viewModel.uiState.value is AuthUiState.Error
        )

        viewModel.resetState()

        assertEquals(
            AuthUiState.Idle,
            viewModel.uiState.value
        )
    }

    private fun createViewModel(
        repository: FakeAuthRepository = FakeAuthRepository(),
        userPreferences: UserPreferences = UserPreferences(TestDataStore())
    ): AuthViewModel {

        return AuthViewModel(
            loginUseCase = LoginUseCase(repository),
            registerUseCase = RegisterUseCase(repository),
            userPreferences = userPreferences
        )
    }
}

private class FakeAuthRepository : AuthRepository {

    var loginResult: Result<String> =
        Result.success("user-123")

    var registerResult: Result<String> =
        Result.success("user-456")

    override suspend fun login(
        email: String,
        pass: String
    ): Result<String> {
        return loginResult
    }

    override suspend fun register(
        email: String,
        pass: String
    ): Result<String> {
        return registerResult
    }
}

private class TestDataStore : DataStore<Preferences> {

    private val preferences =
        MutableStateFlow<Preferences>(emptyPreferences())

    override val data: Flow<Preferences>
        get() = preferences

    override suspend fun updateData(
        transform: suspend (t: Preferences) -> Preferences
    ): Preferences {
        val updated = transform(preferences.value)
        preferences.value = updated
        return updated
    }
}