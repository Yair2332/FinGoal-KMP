package com.fingoal.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.fingoal.app.presentation.auth.AuthViewModel
import com.fingoal.app.presentation.screens.auth.LoginScreen
import com.fingoal.app.presentation.screens.auth.RegisterScreen
import com.fingoal.app.presentation.theme.FinGoalTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootNavigation(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val navController = rememberNavController()

    val userId by viewModel.userId.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "loading"
    ) {

        // Pantalla de carga inicial
        composable("loading") {

            LaunchedEffect(userId) {

                if (userId != null) {

                    val destination =
                        if (userId!!.isEmpty()) {
                            "auth"
                        } else {
                            "main"
                        }

                    navController.navigate(destination) {
                        popUpTo("loading") {
                            inclusive = true
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Grafo de autenticación
        navigation(
            startDestination = "login",
            route = "auth"
        ) {

            composable("login") {

                FinGoalTheme {

                    LoginScreen(
                        onNavigateToRegister = {
                            navController.navigate("register")
                        },

                        onLoginSuccess = {
                            navController.navigate("main") {
                                popUpTo("auth") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }

            composable("register") {

                FinGoalTheme {

                    RegisterScreen(
                        onNavigateToLogin = {
                            navController.popBackStack()
                        },

                        onRegisterSuccess = {
                            navController.navigate("main") {
                                popUpTo("auth") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }
        }

        // Grafo principal
        composable("main") {

            MainAppNavigation(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,

                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },

                userPreferences = viewModel.userPreferences
            )
        }
    }
}