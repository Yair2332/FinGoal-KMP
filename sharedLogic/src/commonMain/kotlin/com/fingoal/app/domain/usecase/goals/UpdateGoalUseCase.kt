package com.fingoal.app.domain.usecase.goals

import com.fingoal.app.data.local.entities.GoalEntity
import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.repository.GoalRepository

class UpdateGoalUseCase(private val repository: GoalRepository) {
    suspend operator fun invoke(goal: Goal) = repository.updateGoal(goal)
}