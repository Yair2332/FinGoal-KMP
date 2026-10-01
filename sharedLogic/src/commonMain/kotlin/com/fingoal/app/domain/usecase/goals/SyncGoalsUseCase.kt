package com.fingoal.app.domain.usecase.goals

import com.fingoal.app.domain.repository.GoalRepository

class SyncGoalsUseCase(
    private val repository: GoalRepository
) {
    suspend operator fun invoke() {
        repository.syncGoals()
    }
}