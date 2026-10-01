package com.fingoal.app.domain.usecase.goals

import com.fingoal.app.domain.repository.GoalRepository

class AddGoalUseCase(private val repository: GoalRepository) {
    suspend operator fun invoke(title: String, description: String, targetAmount: Double, imageUrl: String) {
        repository.addGoal(title, description, targetAmount, imageUrl)
    }
}