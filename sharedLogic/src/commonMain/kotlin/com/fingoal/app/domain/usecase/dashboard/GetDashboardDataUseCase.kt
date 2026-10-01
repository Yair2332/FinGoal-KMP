package com.fingoal.app.domain.usecase.dashboard

import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.repository.GoalRepository
import com.fingoal.app.domain.repository.HabitRepository
import com.fingoal.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

            val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
            val totalExpenses = transactions.filter { !it.isIncome }.sumOf { it.amount }
            val totalBalance = totalIncome - totalExpenses

            val savingsTotal = goals.sumOf { it.currentAmount }
            val topGoal = goals.maxByOrNull { it.targetAmount }

            var runningBalance = 0.0
            val chartPoints = transactions.map {
                runningBalance += if (it.isIncome) it.amount else -it.amount
                runningBalance.toFloat()
            }

            DashboardData(
                totalBalance = totalBalance,
                monthlyExpenses = totalExpenses,
                totalSavings = savingsTotal,
                recentHabits = habits.take(5),
                topGoal = topGoal,
                chartData = chartPoints
            )
        }
    }
}