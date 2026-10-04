package com.fingoal.app.domain.usecase

import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.repository.GoalRepository
import com.fingoal.app.domain.repository.HabitRepository
import com.fingoal.app.domain.repository.TransactionRepository
import com.fingoal.app.domain.usecase.dashboard.GetDashboardDataUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardTest {

    // ---------------------------------------------------------
    // TOTAL BALANCE
    // ---------------------------------------------------------

    @Test
    fun getDashboardData_calculatesTotalBalance() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val habitRepository = FakeHabitRepository()
        val goalRepository = FakeGoalRepository()

        transactionRepository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                amount = 500000.0,
                isIncome = true
            ),
            createTransaction(
                id = 2L,
                amount = 100000.0,
                isIncome = false
            ),
            createTransaction(
                id = 3L,
                amount = 50000.0,
                isIncome = false
            )
        )

        val useCase = GetDashboardDataUseCase(
            transactionRepository = transactionRepository,
            habitRepository = habitRepository,
            goalRepository = goalRepository
        )

        val result = useCase().first()

        assertEquals(350000.0, result.totalBalance)
    }

    // ---------------------------------------------------------
    // MONTHLY EXPENSES
    // ---------------------------------------------------------

    @Test
    fun getDashboardData_calculatesMonthlyExpenses() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val habitRepository = FakeHabitRepository()
        val goalRepository = FakeGoalRepository()

        val now = Clock.System.now().toEpochMilliseconds()

        transactionRepository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                amount = 500000.0,
                isIncome = true,
                date = now
            ),
            createTransaction(
                id = 2L,
                amount = 100000.0,
                isIncome = false,
                date = now
            )
        )

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        val result = useCase().first()

        assertEquals(100000.0, result.monthlyExpenses)
    }

    // ---------------------------------------------------------
    // TOTAL SAVINGS
    // ---------------------------------------------------------

    @Test
    fun getDashboardData_calculatesTotalSavings() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val habitRepository = FakeHabitRepository()
        val goalRepository = FakeGoalRepository()

        goalRepository.goals.value = listOf(
            createGoal(
                id = 1L,
                currentAmount = 100000.0
            ),
            createGoal(
                id = 2L,
                currentAmount = 250000.0
            )
        )

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        val result = useCase().first()

        assertEquals(350000.0, result.totalSavings)
    }

    // ---------------------------------------------------------
    // RECENT HABITS
    // ---------------------------------------------------------

    @Test
    fun getDashboardData_returnsMaximumFiveHabits() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val habitRepository = FakeHabitRepository()
        val goalRepository = FakeGoalRepository()

        habitRepository.habits.value = List(7) { index ->
            Habit(
                id = index.toLong(),
                remoteId = "habit-$index",
                name = "Hábito $index",
                description = "",
                frequency = "DAILY"
            )
        }

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        val result = useCase().first()

        assertEquals(5, result.recentHabits.size)
    }

    // ---------------------------------------------------------
    // TOP GOAL
    // ---------------------------------------------------------

    @Test
    fun getDashboardData_returnsGoalWithHighestTargetAmount() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val habitRepository = FakeHabitRepository()
        val goalRepository = FakeGoalRepository()

        val smallGoal = createGoal(
            id = 1L,
            title = "Celular",
            targetAmount = 500000.0
        )

        val bigGoal = createGoal(
            id = 2L,
            title = "Notebook",
            targetAmount = 1500000.0
        )

        goalRepository.goals.value = listOf(
            smallGoal,
            bigGoal
        )

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        val result = useCase().first()

        assertEquals(bigGoal, result.topGoal)
    }

    // ---------------------------------------------------------
    // CHART DATA
    // ---------------------------------------------------------

    @Test
    fun getDashboardData_buildsCumulativeChartData() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val habitRepository = FakeHabitRepository()
        val goalRepository = FakeGoalRepository()

        val now = Clock.System.now().toEpochMilliseconds()

        transactionRepository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                amount = 50000.0,
                isIncome = true,
                date = now - 3000L
            ),
            createTransaction(
                id = 2L,
                amount = 10000.0,
                isIncome = false,
                date = now - 2000L
            ),
            createTransaction(
                id = 3L,
                amount = 5000.0,
                isIncome = false,
                date = now - 1000L
            )
        )

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        val result = useCase().first()

        assertEquals(
            listOf(0f, 50000f, 40000f, 35000f),
            result.chartData
        )
    }

    // ---------------------------------------------------------
    // EMPTY DATA
    // ---------------------------------------------------------

    @Test
    fun getDashboardData_withEmptyRepositories_returnsEmptyData() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val habitRepository = FakeHabitRepository()
        val goalRepository = FakeGoalRepository()

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        val result = useCase().first()

        assertEquals(0.0, result.totalBalance)
        assertEquals(0.0, result.monthlyExpenses)
        assertEquals(0.0, result.totalSavings)
        assertTrue(result.recentHabits.isEmpty())
        assertEquals(null, result.topGoal)
        assertEquals(listOf(0f), result.chartData)
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    private fun createTransaction(
        id: Long,
        amount: Double,
        isIncome: Boolean,
        date: Long = Clock.System.now().toEpochMilliseconds()
    ): Transaction {
        return Transaction(
            id = id,
            remoteId = "transaction-$id",
            title = "Transacción $id",
            description = "",
            amount = amount,
            category = "General",
            date = date,
            isIncome = isIncome
        )
    }

    private fun createGoal(
        id: Long,
        title: String = "Meta $id",
        targetAmount: Double = 1000000.0,
        currentAmount: Double = 0.0
    ): Goal {
        return Goal(
            id = id,
            remoteId = "goal-$id",
            title = title,
            description = "",
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            priority = 1,
            status = "ACTIVE",
            localImagePath = ""
        )
    }
}

/**
 * Fake TransactionRepository
 */
private class FakeTransactionRepository : TransactionRepository {

    val transactions = MutableStateFlow<List<Transaction>>(emptyList())

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactions
    }

    override suspend fun syncTransactions() {}

    override suspend fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {}

    override suspend fun deleteTransaction(
        localId: Long,
        remoteId: String
    ) {}

    override suspend fun updateTransaction(
        localId: Long,
        remoteId: String,
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {}
}

/**
 * Fake HabitRepository
 */
private class FakeHabitRepository : HabitRepository {

    val habits = MutableStateFlow<List<Habit>>(emptyList())

    override fun getHabits(): Flow<List<Habit>> {
        return habits
    }

    override suspend fun syncHabits() {}

    override suspend fun updateHabit(habit: Habit) {}

    override suspend fun deleteHabit(habitId: String) {}

    override suspend fun createHabit(habit: Habit) {}

    override suspend fun toggleHabit(habitId: String) {}
}

/**
 * Fake GoalRepository
 */
private class FakeGoalRepository : GoalRepository {

    val goals = MutableStateFlow<List<Goal>>(emptyList())

    override fun getGoals(): Flow<List<Goal>> {
        return goals
    }

    override suspend fun syncGoals() {}

    override suspend fun addGoal(
        title: String,
        description: String,
        targetAmount: Double,
        imageUrl: String
    ) {}

    override suspend fun updateGoal(goal: Goal) {}

    override suspend fun deleteGoal(goal: Goal) {}

    override suspend fun addContribution(
        goalId: String,
        userId: String,
        amount: Double
    ) {}

    override suspend fun withdraw(
        goalId: String,
        userId: String,
        amount: Double
    ) {}
}