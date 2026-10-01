package com.fingoal.app.domain.usecase.goals

import com.fingoal.app.data.local.entities.GoalEntity
import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow

class GetGoalsUseCase(
    private val repository: GoalRepository
) {
    operator fun invoke(): Flow<List<Goal>> {
        return repository.getGoals()
    }
}