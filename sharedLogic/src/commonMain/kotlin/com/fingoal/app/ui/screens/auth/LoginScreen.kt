package com.fingoal.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fingoal.sharedlogic.generated.resources.Res
import com.fingoal.app.presentation.auth.AuthUiState
import com.fingoal.app.presentation.auth.AuthViewModel
import com.fingoal.app.ui.screens.auth.components.AuthButton
import com.fingoal.app.ui.screens.auth.components.AuthTextField
import fingoal.sharedlogic.generated.resources.fingoal
import org.jetbrains.compose.resources.painterResource


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                onLoginSuccess()
            }

            is AuthUiState.Error -> {
                viewModel.resetState()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.fingoal),
            contentDescription = "Logo Fingoal",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Inicia sesión para continuar",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo",
            icon = Icons.Default.Email
        )

        Spacer(Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            icon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(Modifier.height(32.dp))

        AuthButton(
            text = "Entrar",
            isLoading = uiState is AuthUiState.Loading
        ) {
            viewModel.login(
                email = email,
                pass = password
            )
        }

        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}