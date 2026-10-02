package com.fingoal.app

import com.fingoal.app.di.initKoin
import com.fingoal.app.presentation.auth.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatformTools

fun initializeKoin() {
    initKoin()
}

fun getAuthViewModelBridge(): AuthViewModelBridge {
    val viewModel = KoinPlatformTools
        .defaultContext()
        .get()
        .get<AuthViewModel>()

    return AuthViewModelBridge(viewModel)
}

class AuthViewModelBridge(
    private val viewModel: AuthViewModel
) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    var state: AuthBridgeState = AuthBridgeState.Idle
        private set

    fun start(
        onStateChanged: (AuthBridgeState) -> Unit
    ) {
        job?.cancel()

        job = scope.launch {
            viewModel.uiState.collectLatest { uiState ->

                val bridgeState = when (uiState) {
                    is com.fingoal.app.presentation.auth.AuthUiState.Idle ->
                        AuthBridgeState.Idle

                    is com.fingoal.app.presentation.auth.AuthUiState.Loading ->
                        AuthBridgeState.Loading

                    is com.fingoal.app.presentation.auth.AuthUiState.Success ->
                        AuthBridgeState.Success(uiState.userId)

                    is com.fingoal.app.presentation.auth.AuthUiState.Error ->
                        AuthBridgeState.Error(uiState.message)
                }

                state = bridgeState
                onStateChanged(bridgeState)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun login(
        email: String,
        password: String
    ) {
        viewModel.login(email, password)
    }

    fun register(
        email: String,
        password: String,
        confirm: String
    ) {
        viewModel.register(email, password, confirm)
    }

    fun resetState() {
        viewModel.resetState()
    }
}

sealed class AuthBridgeState {

    data object Idle : AuthBridgeState()

    data object Loading : AuthBridgeState()

    data class Success(
        val userId: String
    ) : AuthBridgeState()

    data class Error(
        val message: String
    ) : AuthBridgeState()
}