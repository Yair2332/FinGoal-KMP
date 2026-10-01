package com.fingoal.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.domain.usecase.auth.LoginUseCase
import com.fingoal.app.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val userId: StateFlow<String?> = userPreferences.userId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun login(email: String, pass: String) {
        val validationError = AuthValidator.validateLogin(email, pass)
        if (validationError != null) {
            _uiState.value = AuthUiState.Error(validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginUseCase(email, pass)
                .onSuccess { id ->
                    userPreferences.saveUserId(id)
                    _uiState.value = AuthUiState.Success(id)
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error("Credenciales inválidas")
                }
        }
    }

    fun register(email: String, pass: String, confirm: String) {
        val validationError = AuthValidator.validateRegister(email, pass, confirm)
        if (validationError != null) {
            _uiState.value = AuthUiState.Error(validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            registerUseCase(email, pass)
                .onSuccess { id ->
                    userPreferences.saveUserId(id)
                    _uiState.value = AuthUiState.Success(id)
                }
                .onFailure { error ->
                    val message = if (error.message?.contains("400") == true) {
                        "El usuario ya existe"
                    } else {
                        "Ocurrió un error inesperado"
                    }
                    _uiState.value = AuthUiState.Error(message)
                }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}