package com.fingoal.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.ui.navigation.RootNavigation
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {

    val userPreferences: UserPreferences = koinInject()

    val isDarkMode by userPreferences.isDarkMode.collectAsState(
        initial = false
    )

    val scope = rememberCoroutineScope()

    RootNavigation(
        isDarkMode = isDarkMode,
        onToggleDarkMode = {
            scope.launch {
                userPreferences.saveDarkMode(!isDarkMode)
            }
        }
    )
}
