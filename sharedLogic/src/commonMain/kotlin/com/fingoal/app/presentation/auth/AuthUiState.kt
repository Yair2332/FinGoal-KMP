package com.fingoal.app.presentation.auth

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}