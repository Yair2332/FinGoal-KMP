package com.fingoal.app.domain.repository

import com.fingoal.app.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoals(): Flow<List<Goal>>
    suspend fun syncGoals()
    suspend fun addGoal(title: String, description: String, targetAmount: Double, imageUrl: String)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    suspend fun addContribution(goalId: String, userId: String, amount: Double)
    suspend fun withdraw(goalId: String, userId: String, amount: Double)
}