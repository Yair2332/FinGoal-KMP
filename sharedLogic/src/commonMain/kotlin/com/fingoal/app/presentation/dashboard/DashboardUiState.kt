package com.fingoal.app.presentation.dashboard

import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.model.Habit

data class DashboardUiState(
    val totalBalance: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val savingsTotal: Double = 0.0,
    val recentHabits: List<Habit> = emptyList(),
    val topGoal: Goal? = null,
    val chartData: List<Float> = emptyList(),
    val isLoading: Boolean = false
)