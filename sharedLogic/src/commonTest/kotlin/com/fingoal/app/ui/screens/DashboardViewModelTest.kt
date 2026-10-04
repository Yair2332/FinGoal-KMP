package com.fingoal.app.ui.screens

import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.repository.GoalRepository
import com.fingoal.app.domain.repository.HabitRepository
import com.fingoal.app.domain.repository.TransactionRepository
import com.fingoal.app.domain.usecase.dashboard.GetDashboardDataUseCase
import com.fingoal.app.ui.screens.dashboard.DashboardViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DashboardViewModelTest {

    @Test
    fun initialState_isLoading() {
        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun uiState_containsDashboardData() = runTest {
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

        goalRepository.goals.value = listOf(
            createGoal(
                id = 1L,
                currentAmount = 250000.0,
                targetAmount = 1000000.0
            )
        )

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        val viewModel = DashboardViewModel(useCase)

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(400000.0, state.totalBalance)
        assertEquals(100000.0, state.totalExpenses)
        assertEquals(250000.0, state.savingsTotal)
        assertEquals(1, state.recentHabits.size)
        assertEquals(1, state.topGoal?.id)
    }

    @Test
    fun uiState_containsChartData() = runTest {
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

        val viewModel = createViewModel(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        advanceUntilIdle()

        assertEquals(
            listOf(0f, 50000f, 40000f, 35000f),
            viewModel.uiState.value.chartData
        )
    }

    @Test
    fun assistantQuestions_containsFiveQuestions() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()

        assertEquals(
            5,
            viewModel.uiState.value.assistantQuestions.size
        )
    }

    @Test
    fun assistantQuestions_containsBalanceQuestion() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first()

        assertEquals(
            "¿Cuál es mi saldo total?",
            question.question
        )

        assertEquals(
            "Tu saldo total actual es de 0.0.",
            question.answer
        )
    }

    @Test
    fun assistantQuestions_containsMonthlyExpensesQuestion() = runTest {
        val transactionRepository = FakeTransactionRepository()

        val now = Clock.System.now().toEpochMilliseconds()

        transactionRepository.transactions.value = listOf(
            createTransaction(
                id = 1L,
                amount = 75000.0,
                isIncome = false,
                date = now
            )
        )

        val viewModel = createViewModel(
            transactionRepository = transactionRepository
        )

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first {
                it.question == "¿Cuánto gasté este mes?"
            }

        assertEquals(
            "Este mes llevas gastos por un total de 75000.0.",
            question.answer
        )
    }

    @Test
    fun assistantQuestions_withHabits_showsHabitCount() = runTest {
        val habitRepository = FakeHabitRepository()

        habitRepository.habits.value = listOf(
            createHabit(1L),
            createHabit(2L),
            createHabit(3L)
        )

        val viewModel = createViewModel(
            habitRepository = habitRepository
        )

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first {
                it.question == "¿Cuántos hábitos tengo para hoy?"
            }

        assertEquals(
            "Hoy tienes 3 hábitos para revisar.",
            question.answer
        )
    }

    @Test
    fun assistantQuestions_withoutHabits_showsEmptyMessage() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first {
                it.question == "¿Cuántos hábitos tengo para hoy?"
            }

        assertEquals(
            "No tienes hábitos registrados para mostrar hoy.",
            question.answer
        )
    }

    @Test
    fun assistantQuestions_withGoal_showsGoalMessage() = runTest {
        val goalRepository = FakeGoalRepository()

        goalRepository.goals.value = listOf(
            createGoal(
                id = 1L,
                targetAmount = 1000000.0
            )
        )

        val viewModel = createViewModel(
            goalRepository = goalRepository
        )

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first {
                it.question == "¿Tengo un próximo objetivo?"
            }

        assertEquals(
            "Sí, tienes un objetivo próximo que puedes revisar desde la sección de metas.",
            question.answer
        )
    }

    @Test
    fun assistantQuestions_withoutGoal_showsEmptyGoalMessage() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first {
                it.question == "¿Tengo un próximo objetivo?"
            }

        assertEquals(
            "Actualmente no tienes un objetivo próximo registrado.",
            question.answer
        )
    }

    private fun createViewModel(
        transactionRepository: FakeTransactionRepository = FakeTransactionRepository(),
        habitRepository: FakeHabitRepository = FakeHabitRepository(),
        goalRepository: FakeGoalRepository = FakeGoalRepository()
    ): DashboardViewModel {

        val useCase = GetDashboardDataUseCase(
            transactionRepository,
            habitRepository,
            goalRepository
        )

        return DashboardViewModel(useCase)
    }

    private fun createTransaction(
        id: Long,
        amount: Double,
        isIncome: Boolean,
        date: Long
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

    private fun createHabit(id: Long): Habit {
        return Habit(
            id = id,
            remoteId = "habit-$id",
            name = "Hábito $id",
            description = "",
            frequency = "DAILY"
        )
    }

    private fun createGoal(
        id: Long,
        targetAmount: Double,
        currentAmount: Double = 0.0
    ): Goal {
        return Goal(
            id = id,
            remoteId = "goal-$id",
            title = "Meta $id",
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

private class FakeTransactionRepository : TransactionRepository {

    val transactions =
        MutableStateFlow<List<Transaction>>(emptyList())

    override fun getTransactions(): Flow<List<Transaction>> =
        transactions

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

private class FakeHabitRepository : HabitRepository {

    val habits =
        MutableStateFlow<List<Habit>>(emptyList())

    override fun getHabits(): Flow<List<Habit>> =
        habits

    override suspend fun syncHabits() {}

    override suspend fun updateHabit(habit: Habit) {}

    override suspend fun deleteHabit(habitId: String) {}

    override suspend fun createHabit(habit: Habit) {}

    override suspend fun toggleHabit(habitId: String) {}
}

private class FakeGoalRepository : GoalRepository {

    val goals =
        MutableStateFlow<List<Goal>>(emptyList())

    override fun getGoals(): Flow<List<Goal>> =
        goals

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