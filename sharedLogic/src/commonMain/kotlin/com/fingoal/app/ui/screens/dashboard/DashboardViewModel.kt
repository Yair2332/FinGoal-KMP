package com.fingoal.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.domain.usecase.dashboard.GetDashboardDataUseCase
import com.fingoal.app.ui.components.AssistantQuestion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    getDashboardData: GetDashboardDataUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = getDashboardData()
        .map { data ->

            val assistantQuestions = buildAssistantQuestions(
                totalBalance = data.totalBalance,
                totalExpenses = data.monthlyExpenses,
                savingsTotal = data.totalSavings,
                recentHabitsCount = data.recentHabits.size,
                hasTopGoal = data.topGoal != null
            )

            DashboardUiState(
                totalBalance = data.totalBalance,
                totalExpenses = data.monthlyExpenses,
                savingsTotal = data.totalSavings,
                recentHabits = data.recentHabits,
                topGoal = data.topGoal,
                chartData = data.chartData,
                isLoading = false,
                assistantQuestions = assistantQuestions
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(isLoading = true)
        )

    private fun buildAssistantQuestions(
        totalBalance: Double,
        totalExpenses: Double,
        savingsTotal: Double,
        recentHabitsCount: Int,
        hasTopGoal: Boolean
    ): List<AssistantQuestion> {
        return listOf(
            AssistantQuestion(
                question = "¿Cuál es mi saldo total?",
                answer = "Tu saldo total actual es de $totalBalance."
            ),
            AssistantQuestion(
                question = "¿Cuánto gasté este mes?",
                answer = "Este mes llevas gastos por un total de $totalExpenses."
            ),
            AssistantQuestion(
                question = "¿Cuánto tengo ahorrado?",
                answer = "Actualmente tienes $savingsTotal en ahorros."
            ),
            AssistantQuestion(
                question = "¿Cuántos hábitos tengo para hoy?",
                answer = if (recentHabitsCount == 0) {
                    "No tienes hábitos registrados para mostrar hoy."
                } else {
                    "Hoy tienes $recentHabitsCount hábitos para revisar."
                }
            ),
            AssistantQuestion(
                question = "¿Tengo un próximo objetivo?",
                answer = if (hasTopGoal) {
                    "Sí, tienes un objetivo próximo que puedes revisar desde la sección de metas."
                } else {
                    "Actualmente no tienes un objetivo próximo registrado."
                }
            )
        )
    }
}