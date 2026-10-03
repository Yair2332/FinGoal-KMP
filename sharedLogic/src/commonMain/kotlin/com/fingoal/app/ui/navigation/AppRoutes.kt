package com.fingoal.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object DashboardListRoute

@Serializable
object GoalListRoute

@Serializable
object HabitListRoute

@Serializable
object TransactionListRoute

sealed class BottomNavItem(
    val route: Any,
    val title: String,
    val icon: ImageVector
) {

    data object Home : BottomNavItem(
        route = DashboardListRoute,
        title = "Panel",
        icon = Icons.Default.Home
    )

    data object Transactions : BottomNavItem(
        route = TransactionListRoute,
        title = "Flujo",
        icon = Icons.Default.Payments
    )

    data object Habits : BottomNavItem(
        route = HabitListRoute,
        title = "Hábitos",
        icon = Icons.Default.Checklist
    )

    data object Goals : BottomNavItem(
        route = GoalListRoute,
        title = "Metas",
        icon = Icons.Default.Savings
    )
}