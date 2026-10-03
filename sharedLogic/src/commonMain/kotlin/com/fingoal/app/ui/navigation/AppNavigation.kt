package com.fingoal.app.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.presentation.auth.AuthViewModel
import com.fingoal.app.presentation.dashboard.DashboardViewModel
import com.fingoal.app.presentation.goals.GoalViewModel
import com.fingoal.app.presentation.habits.HabitViewModel
import com.fingoal.app.presentation.theme.FinGoalTheme
import com.fingoal.app.presentation.transactions.TransactionViewModel
import com.fingoal.app.ui.components.InfoModal
import com.fingoal.app.ui.components.getHelpInfoForRoute
import com.fingoal.app.ui.screens.auth.LoginScreen
import com.fingoal.app.ui.screens.auth.RegisterScreen
import com.fingoal.app.ui.screens.dashboard.DashboardScreen
import com.fingoal.app.ui.screens.goals.GoalScreen
import com.fingoal.app.ui.screens.habits.HabitScreen
import com.fingoal.app.ui.screens.transactions.TransactionScreen
import fingoal.sharedlogic.generated.resources.Res
import fingoal.sharedlogic.generated.resources.logo
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.painterResource

@Composable
fun RootNavigation(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val authViewModel: AuthViewModel = koinInject()

    val navController = rememberNavController()

    val userId by authViewModel.userId.collectAsState()

    FinGoalTheme(
        darkTheme = isDarkMode
    ) {
        NavHost(
            navController = navController,
            startDestination = "loading"
        ) {

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

            navigation(
                startDestination = "login",
                route = "auth"
            ) {

                composable("login") {

                    LoginScreen(
                        viewModel = authViewModel,

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

                composable("register") {

                    RegisterScreen(
                        viewModel = authViewModel,

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

                    userPreferences = authViewModel.userPreferences
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppNavigation(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit,
    userPreferences: UserPreferences
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination =
        navBackStackEntry?.destination

    val currentRoute =
        currentDestination?.route

    val scope = rememberCoroutineScope()

    var showHelpModal by remember {
        mutableStateOf(false)
    }

    var showGoalDialog by remember {
        mutableStateOf(false)
    }

    val helpInfo =
        getHelpInfoForRoute(currentRoute)

    val transactionViewModel: TransactionViewModel =
        koinInject()

    val habitViewModel: HabitViewModel =
        koinInject()

    val goalViewModel: GoalViewModel =
        koinInject()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Transactions,
        BottomNavItem.Habits,
        BottomNavItem.Goals
    )

    Scaffold(

        topBar = {

            Surface(
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {

                TopAppBar(

                    title = {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Image(
                                painter =
                                    painterResource(
                                        Res.drawable.logo
                                    ),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(end = 8.dp)
                            )

                            Text(
                                text =
                                    getTitleForRoute(
                                        currentRoute
                                    ),
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )
                        }
                    },

                    actions = {

                        if (helpInfo != null) {

                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(26.dp)
                                    .background(
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .primary,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        showHelpModal = true
                                    },
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.QuestionMark,
                                    contentDescription =
                                        "Ayuda",
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onPrimary
                                )
                            }

                        } else {

                            IconButton(
                                onClick = {

                                    scope.launch {

                                        userPreferences.clear()

                                        onLogout()
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.AutoMirrored
                                            .Filled
                                            .ExitToApp,
                                    contentDescription =
                                        "Salir",
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )
                            }
                        }
                    }
                )
            }
        },

        floatingActionButton = {

            val isDashboard =
                currentRoute
                    ?.contains(
                        "DashboardListRoute"
                    ) == true

            val isTransactions =
                currentRoute
                    ?.contains(
                        "TransactionListRoute"
                    ) == true

            val isHabits =
                currentRoute
                    ?.contains(
                        "HabitListRoute"
                    ) == true

            val isGoals =
                currentRoute
                    ?.contains(
                        "GoalListRoute"
                    ) == true

            if (
                isDashboard ||
                isTransactions ||
                isHabits ||
                isGoals
            ) {

                FloatingActionButton(

                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .primary,

                    contentColor =
                        MaterialTheme
                            .colorScheme
                            .onPrimary,

                    shape = CircleShape,

                    modifier = Modifier
                        .offset(y = 50.dp)
                        .size(70.dp),

                    onClick = {

                        when {

                            isDashboard -> {

                                transactionViewModel
                                    .showAddSheet()

                                navController.navigate(
                                    TransactionListRoute
                                ) {

                                    popUpTo<DashboardListRoute> {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                            isTransactions -> {

                                transactionViewModel
                                    .showAddSheet()
                            }

                            isHabits -> {

                                habitViewModel
                                    .showAddDialog()
                            }

                            isGoals -> {

                                showGoalDialog = true
                            }
                        }
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Add,
                        contentDescription =
                            "Agregar"
                    )
                }
            }
        },

        floatingActionButtonPosition =
            FabPosition.Center,

        bottomBar = {

            Surface(
                color =
                    MaterialTheme
                        .colorScheme
                        .surface,
                shadowElevation = 8.dp,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme
                        .colorScheme
                        .outlineVariant
                )
            ) {

                NavigationBar(
                    containerColor =
                        Color.Transparent,
                    tonalElevation = 0.dp
                ) {

                    items.forEachIndexed { index, item ->

                        if (index == 2) {

                            Spacer(
                                modifier =
                                    Modifier.width(64.dp)
                            )
                        }

                        val currentRouteName =
                            currentDestination?.route

                        val selected =
                            currentRouteName
                                ?.contains(
                                    item.route
                                    ::class
                                        .simpleName
                                        ?: ""
                                ) == true

                        NavigationBarItem(

                            selected = selected,

                            onClick = {

                                navController.navigate(
                                    item.route
                                ) {

                                    popUpTo<DashboardListRoute> {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },

                            icon = {

                                Icon(
                                    imageVector =
                                        item.icon,
                                    contentDescription =
                                        item.title
                                )
                            },

                            label = {

                                Text(
                                    item.title
                                )
                            },

                            colors =
                                NavigationBarItemDefaults
                                    .colors(

                                        selectedIconColor =
                                            MaterialTheme
                                                .colorScheme
                                                .secondary,

                                        selectedTextColor =
                                            MaterialTheme
                                                .colorScheme
                                                .secondary,

                                        unselectedIconColor =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurfaceVariant,

                                        unselectedTextColor =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurfaceVariant,

                                        indicatorColor =
                                            Color.Transparent
                                    ),

                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }

    ) { paddingValues ->

        NavHost(

            navController = navController,

            startDestination =
                DashboardListRoute,

            modifier =
                Modifier.padding(
                    paddingValues
                )

        ) {

            composable<DashboardListRoute> {

                val viewModel: DashboardViewModel =
                    koinInject()

                DashboardScreen(

                    viewModel = viewModel,

                    onNavigateToTransactions = {

                        navController.navigate(
                            TransactionListRoute
                        ) {

                            popUpTo<DashboardListRoute> {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },

                    onNavigateToHabits = {

                        navController.navigate(
                            HabitListRoute
                        ) {

                            popUpTo<DashboardListRoute> {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },

                    onNavigateToGoals = {

                        navController.navigate(
                            GoalListRoute
                        ) {

                            popUpTo<DashboardListRoute> {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },

                    isDarkMode =
                        isDarkMode,

                    onToggleDarkMode =
                        onToggleDarkMode
                )
            }

            composable<TransactionListRoute> {

                TransactionScreen(
                    viewModel =
                        transactionViewModel
                )
            }

            composable<HabitListRoute> {

                HabitScreen(
                    viewModel =
                        habitViewModel
                )
            }

            composable<GoalListRoute> {

                GoalScreen(

                    viewModel =
                        goalViewModel,

                    showSheet =
                        showGoalDialog,

                    onDismiss = {
                        showGoalDialog = false
                    }
                )
            }
        }

        if (
            showHelpModal &&
            helpInfo != null
        ) {

            InfoModal(

                onDismiss = {
                    showHelpModal = false
                },

                imageRes =
                    helpInfo.imageRes,

                items =
                    helpInfo.items
            )
        }
    }
}

@Composable
fun getTitleForRoute(
    route: String?
): String {

    return when {

        route == null ->
            "FinGoal"

        route.contains(
            "DashboardListRoute"
        ) ->
            "FinGoal"

        route.contains(
            "TransactionListRoute"
        ) ->
            "Transacciones"

        route.contains(
            "HabitListRoute"
        ) ->
            "Hábitos"

        route.contains(
            "GoalListRoute"
        ) ->
            "Mis Metas"

        else ->
            "FinGoal"
    }
}