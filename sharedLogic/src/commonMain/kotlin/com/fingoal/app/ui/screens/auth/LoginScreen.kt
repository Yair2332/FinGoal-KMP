package com.fingoal.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fingoal.app.ui.screens.auth.components.AuthButton
import com.fingoal.app.ui.screens.auth.components.AuthTextField
import fingoal.sharedlogic.generated.resources.Res
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isDark = isSystemInDarkTheme()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                onLoginSuccess()
            }

            is AuthUiState.Error -> {
                errorMessage = state.message
                viewModel.resetState()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDark) Color(0xFF121212)
                else Color.White
            )
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
            color = if (isDark) Color.White else Color.Black
        )

        Text(
            text = "Inicia sesión para continuar",
            color = if (isDark) {
                Color(0xFFE0E0E0)
            } else {
                Color(0xFF555555)
            },
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

        errorMessage?.let { message ->
            Spacer(Modifier.height(12.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = if (isDark) {
                    Color(0xFF80CBC4)
                } else {
                    Color(0xFF00695C)
                }
            )
        }
    }
}