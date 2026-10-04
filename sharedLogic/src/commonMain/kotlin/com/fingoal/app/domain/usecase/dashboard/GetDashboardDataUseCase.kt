package com.fingoal.app.domain.usecase.dashboard

import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.repository.GoalRepository
import com.fingoal.app.domain.repository.HabitRepository
import com.fingoal.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

data class DashboardData(
    val totalBalance: Double,
    val monthlyExpenses: Double,
    val totalSavings: Double,
    val recentHabits: List<Habit>,
    val topGoal: Goal?,
    val chartData: List<Float>
)

class GetDashboardDataUseCase(
    private val transactionRepository: TransactionRepository,
    private val habitRepository: HabitRepository,
    private val goalRepository: GoalRepository
) {

    operator fun invoke(): Flow<DashboardData> {
        return combine(
            transactionRepository.getTransactions(),
            habitRepository.getHabits(),
            goalRepository.getGoals()
        ) { transactions, habits, goals ->

            // Fecha actual
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            // Primer día del mes actual
            val startOfMonth = LocalDate(
                year = now.year,
                monthNumber = now.monthNumber,
                dayOfMonth = 1
            )

            val startOfMonthMillis = startOfMonth
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()

            // Solo transacciones del mes actual,
            // ordenadas desde la más antigua hasta la más reciente
            val monthlyTransactions = transactions
                .filter { it.date >= startOfMonthMillis }
                .sortedBy { it.date }

            // Balance total histórico
            val totalIncome = transactions
                .filter { it.isIncome }
                .sumOf { it.amount }

            val totalExpenses = transactions
                .filter { !it.isIncome }
                .sumOf { it.amount }

            val totalBalance = totalIncome - totalExpenses

            // Gastos solamente del mes actual
            val monthlyExpenses = monthlyTransactions
                .filter { !it.isIncome }
                .sumOf { it.amount }

            // Total ahorrado
            val savingsTotal = goals.sumOf { it.currentAmount }

            // Objetivo con mayor monto objetivo
            val topGoal = goals.maxByOrNull { it.targetAmount }

            // Datos para el gráfico:
            // comienza en 0 y acumula ingresos/gastos del mes
            var runningBalance = 0.0

            val chartPoints = buildList {
                add(0f)

                monthlyTransactions.forEach { transaction ->
                    runningBalance += if (transaction.isIncome) {
                        transaction.amount
                    } else {
                        -transaction.amount
                    }

                    add(runningBalance.toFloat())
                }
            }

            DashboardData(
                totalBalance = totalBalance,
                monthlyExpenses = monthlyExpenses,
                totalSavings = savingsTotal,
                recentHabits = habits.take(5),
                topGoal = topGoal,
                chartData = chartPoints
            )
        }
    }
}