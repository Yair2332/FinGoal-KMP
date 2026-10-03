package com.fingoal.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.fingoal.app.ui.navigation.RootNavigation

@Composable
fun App() {
    var isDarkMode by remember {
        mutableStateOf(false)
    }

    RootNavigation(
        isDarkMode = isDarkMode,
        onToggleDarkMode = {
            isDarkMode = !isDarkMode
        }
    )
}