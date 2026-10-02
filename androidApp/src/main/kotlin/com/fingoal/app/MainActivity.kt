package com.fingoal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fingoal.app.presentation.navigation.RootNavigation
import com.fingoal.app.presentation.theme.FinGoalTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FinGoalTheme {
                RootNavigation(
                    isDarkMode = false,
                    onToggleDarkMode = {}
                )
            }
        }
    }
}