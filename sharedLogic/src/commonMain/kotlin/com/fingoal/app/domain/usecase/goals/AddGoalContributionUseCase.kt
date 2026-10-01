package com.fingoal.app.domain.usecase.goals

import com.fingoal.app.domain.repository.GoalRepository

class AddGoalContributionUseCase(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goalId: String, userId: String, amount: Double) {
        repository.addContribution(goalId, userId, amount)
    }
}