package com.fingoal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fingoal.app.presentation.navigation.RootNavigation
import com.fingoal.app.presentation.theme.FinGoalTheme

class MainActivity : ComponentActivity() {

    private var isDarkMode by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FinGoalTheme(
                darkTheme = isDarkMode
            ) {
                RootNavigation(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        isDarkMode = !isDarkMode
                    }
                )
            }
        }
    }
}