package com.fingoal.app.presentation.screens.auth

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fingoal.app.presentation.auth.AuthUiState
import com.fingoal.app.presentation.auth.AuthViewModel
import com.fingoal.app.presentation.screens.auth.components.AuthButton
import com.fingoal.app.presentation.screens.auth.components.AuthTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> onRegisterSuccess()
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(120.dp).padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        Text("Comienza tu aventura financiera hoy", color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))
        AuthTextField(email, { email = it }, "Correo", Icons.Default.Email)
        Spacer(Modifier.height(12.dp))
        AuthTextField(password, { password = it }, "Contraseña", Icons.Default.Lock, true)
        Spacer(Modifier.height(12.dp))
        AuthTextField(confirm, { confirm = it }, "Confirmar Contraseña", Icons.Default.Lock, true)
        Spacer(Modifier.height(24.dp))
        AuthButton("Registrarse", uiState is AuthUiState.Loading) {
            viewModel.register(email, password, confirm)
        }
        TextButton(onClick = onNavigateToLogin) { Text("¿Ya tienes cuenta? Inicia sesión") }
    }
}