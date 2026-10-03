package com.fingoal.app.ui.screens.auth

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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
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
import com.fingoal.app.presentation.auth.AuthUiState
import com.fingoal.app.presentation.auth.AuthViewModel
import com.fingoal.app.ui.screens.auth.components.AuthButton
import com.fingoal.app.ui.screens.auth.components.AuthTextField

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                onRegisterSuccess()
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
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Comienza tu aventura financiera hoy",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo",
            icon = Icons.Default.Email
        )

        Spacer(Modifier.height(12.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            icon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(Modifier.height(12.dp))

        AuthTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = "Confirmar Contraseña",
            icon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(Modifier.height(24.dp))

        AuthButton(
            text = "Registrarse",
            isLoading = uiState is AuthUiState.Loading
        ) {
            viewModel.register(
                email = email,
                pass = password,
                confirm = confirm
            )
        }

        TextButton(
            onClick = onNavigateToLogin
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}