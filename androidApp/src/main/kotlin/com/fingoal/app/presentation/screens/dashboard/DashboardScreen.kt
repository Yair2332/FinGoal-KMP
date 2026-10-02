package com.fingoal.app.presentation.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fingoal.app.presentation.dashboard.DashboardViewModel
import com.fingoal.app.presentation.screens.dashboard.components.BalanceCard
import com.fingoal.app.presentation.screens.dashboard.components.BalanceChart
import com.fingoal.app.presentation.screens.dashboard.components.GoalCard
import com.fingoal.app.presentation.screens.dashboard.components.SummaryCardsRow
import com.fingoal.app.presentation.screens.habits.components.HabitItem

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToGoals: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Balance Total
        item {
            BalanceCard(
                totalBalance = state.totalBalance,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onNavigateToTransactions = onNavigateToTransactions
            )
        }

        // 2. Gastos y Ahorro
        item {
            SummaryCardsRow(
                totalExpenses = state.totalExpenses,
                savingsTotal = state.savingsTotal
            )
        }

        // 3. Gráfico de Flujo
        if (state.chartData.isNotEmpty()) {
            item {
                Text(
                    text = "Flujo del mes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    BalanceChart(
                        chartData = state.chartData,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // 4. Hábitos
        state.recentHabits?.let { habits ->
            if (habits.isNotEmpty()) {
                item {
                    Text(
                        text = "Hábitos de hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(habits) { habit ->
                    HabitItem(
                        habit = habit,
                        onToggle = {},
                        onDelete = {},
                        onEdit = {},
                        navigateToHabits = onNavigateToHabits,
                        isExpandable = false
                    )
                }
            }
        }

        // 5. Próximo Objetivo
        state.topGoal?.let { goal ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Próximo objetivo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                GoalCard(goal = goal, onClick = onNavigateToGoals)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}