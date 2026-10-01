package com.fingoal.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.domain.usecase.dashboard.GetDashboardDataUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    getDashboardData: GetDashboardDataUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = getDashboardData()
        .map { data ->
            DashboardUiState(
                totalBalance = data.totalBalance,
                totalExpenses = data.monthlyExpenses,
                savingsTotal = data.totalSavings,
                recentHabits = data.recentHabits,
                topGoal = data.topGoal,
                chartData = data.chartData,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(isLoading = true)
        )
}