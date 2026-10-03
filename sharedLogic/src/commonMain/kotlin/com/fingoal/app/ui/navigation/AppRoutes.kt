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
    object Home : BottomNavItem(
        DashboardListRoute,
        "Panel",
        Icons.Default.Home
    )

    object Transactions : BottomNavItem(
        TransactionListRoute,
        "Flujo",
        Icons.Default.Payments
    )

    object Habits : BottomNavItem(
        HabitListRoute,
        "Hábitos",
        Icons.Default.Checklist
    )

    object Goals : BottomNavItem(
        GoalListRoute,
        "Metas",
        Icons.Default.Savings
    )
}