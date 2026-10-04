package com.fingoal.app.ui.screens

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.repository.GoalRepository
import com.fingoal.app.domain.usecase.goals.AddGoalContributionUseCase
import com.fingoal.app.domain.usecase.goals.AddGoalUseCase
import com.fingoal.app.domain.usecase.goals.DeleteGoalUseCase
import com.fingoal.app.domain.usecase.goals.GetGoalsUseCase
import com.fingoal.app.domain.usecase.goals.SyncGoalsUseCase
import com.fingoal.app.domain.usecase.goals.UpdateGoalUseCase
import com.fingoal.app.domain.usecase.goals.WithdrawGoalUseCase
import com.fingoal.app.ui.screens.goals.GoalViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.collections.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class GoalViewModelTest {

    @Test
    fun initialState_loadsGoals() = runTest {
        val repository = FakeGoalRepository()

        repository.goals.value = listOf(
            createGoal(1L, "Celular", 500000.0)
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.goals.size)
        assertEquals("Celular", viewModel.uiState.value.goals.first().title)
    }

    @Test
    fun assistantQuestions_showsGoalCount() = runTest {
        val repository = FakeGoalRepository()

        repository.goals.value = listOf(
            createGoal(1L, "Celular", 500000.0),
            createGoal(2L, "Notebook", 1000000.0)
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first()

        assertEquals(
            "¿Cuántas metas tengo?",
            question.question
        )

        assertEquals(
            "Actualmente tienes 2 metas registradas.",
            question.answer
        )
    }

    @Test
    fun assistantQuestions_withoutGoals_showsEmptyMessage() = runTest {
        val repository = FakeGoalRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val question = viewModel.uiState.value.assistantQuestions
            .first()

        assertEquals(
            "Actualmente no tienes metas registradas.",
            question.answer
        )
    }

    @Test
    fun refreshGoals_syncsRepository() = runTest {
        val repository = FakeGoalRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        repository.syncResult = Result.success(Unit)

        viewModel.refreshGoals()

        advanceUntilIdle()

        assertEquals(1, repository.syncCalls)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun refreshGoals_withError_setsErrorMessage() = runTest {
        val repository = FakeGoalRepository()

        repository.syncResult =
            Result.failure(Exception("Error de sincronización"))

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertEquals(
            "Error de sincronización",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun saveGoal_withoutExistingGoal_createsGoal() = runTest {
        val repository = FakeGoalRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.saveGoal(
            goal = null,
            title = "Nuevo celular",
            desc = "Ahorrar para un celular",
            amount = 800000.0,
            image = "celular.jpg"
        )

        advanceUntilIdle()

        assertEquals(1, repository.addGoalCalls)
        assertEquals("Nuevo celular", repository.lastTitle)
        assertEquals("Ahorrar para un celular", repository.lastDescription)
        assertEquals(800000.0, repository.lastTargetAmount)
        assertEquals("celular.jpg", repository.lastImage)
    }

    @Test
    fun saveGoal_withExistingGoal_updatesGoal() = runTest {
        val repository = FakeGoalRepository()

        val originalGoal = createGoal(
            id = 1L,
            title = "Celular",
            targetAmount = 500000.0
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.saveGoal(
            goal = originalGoal,
            title = "Notebook",
            desc = "Nueva descripción",
            amount = 1500000.0,
            image = "notebook.jpg"
        )

        advanceUntilIdle()

        assertEquals(1, repository.updateGoalCalls)
        assertEquals(
            "Notebook",
            repository.lastUpdatedGoal?.title
        )
        assertEquals(
            "Nueva descripción",
            repository.lastUpdatedGoal?.description
        )
        assertEquals(
            1500000.0,
            repository.lastUpdatedGoal?.targetAmount
        )
        assertEquals(
            "notebook.jpg",
            repository.lastUpdatedGoal?.localImagePath
        )

        // Los datos originales que no modifica el ViewModel
        // deben mantenerse.
        assertEquals(
            originalGoal.remoteId,
            repository.lastUpdatedGoal?.remoteId
        )

        assertEquals(
            originalGoal.currentAmount,
            repository.lastUpdatedGoal?.currentAmount
        )
    }

    @Test
    fun saveGoal_withError_setsErrorMessage() = runTest {
        val repository = FakeGoalRepository()

        repository.addGoalResult =
            Result.failure(Exception("No se pudo crear la meta"))

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.saveGoal(
            goal = null,
            title = "Meta",
            desc = "Descripción",
            amount = 500000.0,
            image = ""
        )

        advanceUntilIdle()

        assertEquals(
            "No se pudo crear la meta",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun deleteGoal_callsRepository() = runTest {
        val repository = FakeGoalRepository()

        val goal = createGoal(
            id = 1L,
            title = "Celular",
            targetAmount = 500000.0
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.deleteGoal(goal)

        advanceUntilIdle()

        assertEquals(1, repository.deleteGoalCalls)
        assertEquals(goal, repository.lastDeletedGoal)
    }

    @Test
    fun deleteGoal_withError_setsErrorMessage() = runTest {
        val repository = FakeGoalRepository()

        repository.deleteGoalResult =
            Result.failure(Exception("No se pudo eliminar"))

        val goal = createGoal(
            id = 1L,
            title = "Celular",
            targetAmount = 500000.0
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.deleteGoal(goal)

        advanceUntilIdle()

        assertEquals(
            "No se pudo eliminar",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun handleContribution_withAdd_usesUserIdAndAddsContribution() = runTest {
        val repository = FakeGoalRepository()

        val dataStore = TestDataStore()
        val userPreferences = UserPreferences(dataStore)

        userPreferences.saveUserId("user-123")

        val viewModel = createViewModel(
            repository = repository,
            userPreferences = userPreferences
        )

        advanceUntilIdle()

        viewModel.handleContribution(
            goalId = "goal-1",
            amount = 100000.0,
            isAdding = true
        )

        advanceUntilIdle()

        assertEquals(1, repository.addContributionCalls)
        assertEquals("goal-1", repository.lastContributionGoalId)
        assertEquals("user-123", repository.lastContributionUserId)
        assertEquals(100000.0, repository.lastContributionAmount)
        assertEquals(1, repository.syncCalls)
    }

    @Test
    fun handleContribution_withWithdraw_usesUserIdAndWithdraws() = runTest {
        val repository = FakeGoalRepository()

        val dataStore = TestDataStore()
        val userPreferences = UserPreferences(dataStore)

        userPreferences.saveUserId("user-123")

        val viewModel = createViewModel(
            repository = repository,
            userPreferences = userPreferences
        )

        advanceUntilIdle()

        viewModel.handleContribution(
            goalId = "goal-1",
            amount = 50000.0,
            isAdding = false
        )

        advanceUntilIdle()

        assertEquals(1, repository.withdrawCalls)
        assertEquals("goal-1", repository.lastWithdrawGoalId)
        assertEquals("user-123", repository.lastWithdrawUserId)
        assertEquals(50000.0, repository.lastWithdrawAmount)
        assertEquals(1, repository.syncCalls)
    }

    @Test
    fun handleContribution_withoutUserId_setsUnauthorizedError() = runTest {
        val repository = FakeGoalRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.handleContribution(
            goalId = "goal-1",
            amount = 100000.0,
            isAdding = true
        )

        advanceUntilIdle()

        assertEquals(
            "No autorizado",
            viewModel.uiState.value.errorMessage
        )

        assertEquals(0, repository.addContributionCalls)
        assertEquals(0, repository.withdrawCalls)
    }

    @Test
    fun getGoalSummary_calculatesCorrectValues() = runTest {
        val repository = FakeGoalRepository()

        repository.goals.value = listOf(
            createGoal(
                id = 1L,
                title = "Celular",
                targetAmount = 500000.0
            ),
            createGoal(
                id = 2L,
                title = "Notebook",
                targetAmount = 1500000.0
            ),
            createGoal(
                id = 3L,
                title = "Viaje",
                targetAmount = 1000000.0
            )
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val summary = viewModel.getGoalSummary()

        assertEquals(3, summary.totalGoals)
        assertEquals(3000000.0, summary.totalTarget)
        assertEquals(1000000.0, summary.averageTarget)
    }

    @Test
    fun getGoalSummary_withoutGoals_returnsZeros() = runTest {
        val repository = FakeGoalRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val summary = viewModel.getGoalSummary()

        assertEquals(0, summary.totalGoals)
        assertEquals(0.0, summary.totalTarget)
        assertEquals(0.0, summary.averageTarget)
    }

    private fun createViewModel(
        repository: FakeGoalRepository = FakeGoalRepository(),
        userPreferences: UserPreferences = UserPreferences(TestDataStore())
    ): GoalViewModel {

        return GoalViewModel(
            getGoalsUseCase = GetGoalsUseCase(repository),
            addGoalUseCase = AddGoalUseCase(repository),
            syncGoalsUseCase = SyncGoalsUseCase(repository),
            updateGoalUseCase = UpdateGoalUseCase(repository),
            deleteGoalUseCase = DeleteGoalUseCase(repository),
            addGoalContributionUseCase =
                AddGoalContributionUseCase(repository),
            withdrawGoalUseCase =
                WithdrawGoalUseCase(repository),
            userPreferences = userPreferences
        )
    }

    private fun createGoal(
        id: Long,
        title: String,
        targetAmount: Double
    ): Goal {
        return Goal(
            id = id,
            remoteId = "goal-$id",
            title = title,
            description = "Descripción",
            targetAmount = targetAmount,
            currentAmount = 0.0,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            priority = 1,
            status = "ACTIVE",
            localImagePath = ""
        )
    }
}

private class FakeGoalRepository : GoalRepository {

    val goals = MutableStateFlow<List<Goal>>(emptyList())

    var syncResult: Result<Unit> = Result.success(Unit)
    var addGoalResult: Result<Unit> = Result.success(Unit)
    var updateGoalResult: Result<Unit> = Result.success(Unit)
    var deleteGoalResult: Result<Unit> = Result.success(Unit)
    var addContributionResult: Result<Unit> = Result.success(Unit)
    var withdrawResult: Result<Unit> = Result.success(Unit)

    var syncCalls = 0
    var addGoalCalls = 0
    var updateGoalCalls = 0
    var deleteGoalCalls = 0
    var addContributionCalls = 0
    var withdrawCalls = 0

    var lastTitle: String? = null
    var lastDescription: String? = null
    var lastTargetAmount: Double? = null
    var lastImage: String? = null

    var lastUpdatedGoal: Goal? = null
    var lastDeletedGoal: Goal? = null

    var lastContributionGoalId: String? = null
    var lastContributionUserId: String? = null
    var lastContributionAmount: Double? = null

    var lastWithdrawGoalId: String? = null
    var lastWithdrawUserId: String? = null
    var lastWithdrawAmount: Double? = null

    override fun getGoals(): Flow<List<Goal>> =
        goals

    override suspend fun syncGoals() {
        syncCalls++

        syncResult.getOrThrow()
    }

    override suspend fun addGoal(
        title: String,
        description: String,
        targetAmount: Double,
        imageUrl: String
    ) {
        addGoalCalls++

        lastTitle = title
        lastDescription = description
        lastTargetAmount = targetAmount
        lastImage = imageUrl

        addGoalResult.getOrThrow()
    }

    override suspend fun updateGoal(goal: Goal) {
        updateGoalCalls++

        lastUpdatedGoal = goal

        updateGoalResult.getOrThrow()
    }

    override suspend fun deleteGoal(goal: Goal) {
        deleteGoalCalls++

        lastDeletedGoal = goal

        deleteGoalResult.getOrThrow()
    }

    override suspend fun addContribution(
        goalId: String,
        userId: String,
        amount: Double
    ) {
        addContributionCalls++

        lastContributionGoalId = goalId
        lastContributionUserId = userId
        lastContributionAmount = amount

        addContributionResult.getOrThrow()
    }

    override suspend fun withdraw(
        goalId: String,
        userId: String,
        amount: Double
    ) {
        withdrawCalls++

        lastWithdrawGoalId = goalId
        lastWithdrawUserId = userId
        lastWithdrawAmount = amount

        withdrawResult.getOrThrow()
    }
}

private class TestDataStore : DataStore<Preferences> {

    private val preferences =
        MutableStateFlow<Preferences>(emptyPreferences())

    override val data: Flow<Preferences>
        get() = preferences

    override suspend fun updateData(
        transform: suspend (Preferences) -> Preferences
    ): Preferences {
        val updated = transform(preferences.value)
        preferences.value = updated
        return updated
    }
}