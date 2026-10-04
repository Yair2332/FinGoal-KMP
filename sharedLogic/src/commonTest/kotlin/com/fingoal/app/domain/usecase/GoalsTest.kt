package com.fingoal.app.domain.usecase

import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.repository.GoalRepository
import com.fingoal.app.domain.usecase.goals.AddGoalContributionUseCase
import com.fingoal.app.domain.usecase.goals.AddGoalUseCase
import com.fingoal.app.domain.usecase.goals.DeleteGoalUseCase
import com.fingoal.app.domain.usecase.goals.GetGoalsUseCase
import com.fingoal.app.domain.usecase.goals.SyncGoalsUseCase
import com.fingoal.app.domain.usecase.goals.UpdateGoalUseCase
import com.fingoal.app.domain.usecase.goals.WithdrawGoalUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoalsTest {

    private val goal = Goal(
        id = 1L,
        remoteId = "goal-1",
        title = "Comprar una notebook",
        description = "Ahorrar para comprar una notebook nueva",
        targetAmount = 1000000.0,
        currentAmount = 250000.0,
        createdAt = 1759276800000L,
        priority = 1,
        status = "ACTIVE",
        localImagePath = ""
    )

    // ---------------------------------------------------------
    // GET GOALS
    // ---------------------------------------------------------

    @Test
    fun getGoals_returnsRepositoryGoals() = runTest {
        val fakeRepository = FakeGoalRepository()

        fakeRepository.goals.value = listOf(goal)

        val useCase = GetGoalsUseCase(fakeRepository)

        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals(goal, result[0])
    }

    // ---------------------------------------------------------
    // ADD GOAL
    // ---------------------------------------------------------

    @Test
    fun addGoal_callsRepositoryWithCorrectData() = runTest {
        val fakeRepository = FakeGoalRepository()
        val useCase = AddGoalUseCase(fakeRepository)

        useCase(
            title = "Comprar una notebook",
            description = "Ahorrar para una notebook nueva",
            targetAmount = 1000000.0,
            imageUrl = "notebook.jpg"
        )

        assertTrue(fakeRepository.addGoalCalled)

        assertEquals("Comprar una notebook", fakeRepository.addedTitle)
        assertEquals(
            "Ahorrar para una notebook nueva",
            fakeRepository.addedDescription
        )
        assertEquals(1000000.0, fakeRepository.addedTargetAmount)
        assertEquals("notebook.jpg", fakeRepository.addedImageUrl)
    }

    // ---------------------------------------------------------
    // UPDATE GOAL
    // ---------------------------------------------------------

    @Test
    fun updateGoal_callsRepositoryWithCorrectGoal() = runTest {
        val fakeRepository = FakeGoalRepository()
        val useCase = UpdateGoalUseCase(fakeRepository)

        val updatedGoal = goal.copy(
            title = "Comprar una notebook nueva",
            targetAmount = 1200000.0
        )

        useCase(updatedGoal)

        assertTrue(fakeRepository.updateGoalCalled)
        assertEquals(updatedGoal, fakeRepository.updatedGoal)
    }

    // ---------------------------------------------------------
    // DELETE GOAL
    // ---------------------------------------------------------

    @Test
    fun deleteGoal_callsRepositoryWithCorrectGoal() = runTest {
        val fakeRepository = FakeGoalRepository()
        val useCase = DeleteGoalUseCase(fakeRepository)

        useCase(goal)

        assertTrue(fakeRepository.deleteGoalCalled)
        assertEquals(goal, fakeRepository.deletedGoal)
    }

    // ---------------------------------------------------------
    // ADD CONTRIBUTION
    // ---------------------------------------------------------

    @Test
    fun addGoalContribution_callsRepositoryWithCorrectData() = runTest {
        val fakeRepository = FakeGoalRepository()
        val useCase = AddGoalContributionUseCase(fakeRepository)

        useCase(
            goalId = "goal-123",
            userId = "user-456",
            amount = 50000.0
        )

        assertTrue(fakeRepository.addContributionCalled)

        assertEquals("goal-123", fakeRepository.contributionGoalId)
        assertEquals("user-456", fakeRepository.contributionUserId)
        assertEquals(50000.0, fakeRepository.contributionAmount)
    }

    // ---------------------------------------------------------
    // WITHDRAW GOAL
    // ---------------------------------------------------------

    @Test
    fun withdrawGoal_callsRepositoryWithCorrectData() = runTest {
        val fakeRepository = FakeGoalRepository()
        val useCase = WithdrawGoalUseCase(fakeRepository)

        useCase(
            goalId = "goal-123",
            userId = "user-456",
            amount = 25000.0
        )

        assertTrue(fakeRepository.withdrawCalled)

        assertEquals("goal-123", fakeRepository.withdrawGoalId)
        assertEquals("user-456", fakeRepository.withdrawUserId)
        assertEquals(25000.0, fakeRepository.withdrawAmount)
    }

    // ---------------------------------------------------------
    // SYNC GOALS
    // ---------------------------------------------------------

    @Test
    fun syncGoals_callsRepository() = runTest {
        val fakeRepository = FakeGoalRepository()
        val useCase = SyncGoalsUseCase(fakeRepository)

        useCase()

        assertTrue(fakeRepository.syncGoalsCalled)
    }
}

/**
 * Fake del GoalRepository para probar los UseCases
 * sin depender de Room, API o la implementación real.
 */
private class FakeGoalRepository : GoalRepository {

    val goals = MutableStateFlow<List<Goal>>(emptyList())

    var addGoalCalled = false
    var updateGoalCalled = false
    var deleteGoalCalled = false
    var addContributionCalled = false
    var withdrawCalled = false
    var syncGoalsCalled = false

    var addedTitle = ""
    var addedDescription = ""
    var addedTargetAmount = 0.0
    var addedImageUrl = ""

    var updatedGoal: Goal? = null
    var deletedGoal: Goal? = null

    var contributionGoalId = ""
    var contributionUserId = ""
    var contributionAmount = 0.0

    var withdrawGoalId = ""
    var withdrawUserId = ""
    var withdrawAmount = 0.0

    override fun getGoals(): Flow<List<Goal>> {
        return goals
    }

    override suspend fun syncGoals() {
        syncGoalsCalled = true
    }

    override suspend fun addGoal(
        title: String,
        description: String,
        targetAmount: Double,
        imageUrl: String
    ) {
        addGoalCalled = true

        addedTitle = title
        addedDescription = description
        addedTargetAmount = targetAmount
        addedImageUrl = imageUrl
    }

    override suspend fun updateGoal(goal: Goal) {
        updateGoalCalled = true
        updatedGoal = goal
    }

    override suspend fun deleteGoal(goal: Goal) {
        deleteGoalCalled = true
        deletedGoal = goal
    }

    override suspend fun addContribution(
        goalId: String,
        userId: String,
        amount: Double
    ) {
        addContributionCalled = true

        contributionGoalId = goalId
        contributionUserId = userId
        contributionAmount = amount
    }

    override suspend fun withdraw(
        goalId: String,
        userId: String,
        amount: Double
    ) {
        withdrawCalled = true

        withdrawGoalId = goalId
        withdrawUserId = userId
        withdrawAmount = amount
    }
}