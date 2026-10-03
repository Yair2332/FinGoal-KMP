package com.fingoal.app.data.repository

import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.data.local.dao.GoalDao
import com.fingoal.app.data.local.entities.GoalEntity
import com.fingoal.app.data.mapper.toDomain
import com.fingoal.app.data.mapper.toEntity
import com.fingoal.app.data.remote.GoalApiService
import com.fingoal.app.data.remote.dto.ContributionRequest
import com.fingoal.app.data.remote.dto.GoalRequest
import com.fingoal.app.data.remote.dto.UpdateGoalRequest
import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class GoalRepositoryImpl(
    private val goalApiService: GoalApiService,
    private val goalDao: GoalDao,
    private val userPreferences: UserPreferences
) : GoalRepository {

    override fun getGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun syncGoals() {
        try {
            val userId = userPreferences.userId.firstOrNull()
                ?: throw Exception("No autorizado")

            val remoteGoals = goalApiService.getGoals(userId)

            val localEntities = remoteGoals.map { dto ->
                GoalEntity(
                    remoteId = dto.id ?: "",
                    title = dto.title ?: "",
                    description = dto.description ?: "",
                    targetAmount = dto.targetAmount ?: 0.0,
                    currentAmount = dto.currentAmount ?: 0.0,
                    createdAt = dto.createdAt ?: 0L,
                    priority = dto.priority ?: 0,
                    status = dto.status ?: "",
                    localImagePath = dto.localImagePath ?: ""
                )
            }

            goalDao.replaceAllGoals(localEntities)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addGoal(
        title: String,
        description: String,
        targetAmount: Double,
        imageUrl: String
    ) {
        val userId = userPreferences.userId.firstOrNull()
            ?: throw Exception("No autorizado")

        val request = GoalRequest(
            userId = userId,
            title = title,
            description = description,
            targetAmount = targetAmount,
            currentAmount = 0.0,
            priority = 1,
            localImagePath = imageUrl
        )

        val responseDto = goalApiService.createGoal(request)

        val localEntity = GoalEntity(
            remoteId = responseDto.id ?: "",
            title = responseDto.title ?: "",
            description = responseDto.description ?: "",
            targetAmount = responseDto.targetAmount ?: 0.0,
            currentAmount = responseDto.currentAmount ?: 0.0,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            priority = responseDto.priority ?: 0,
            status = responseDto.status ?: "",
            localImagePath = responseDto.localImagePath ?: ""
        )

        goalDao.insertGoal(localEntity)
    }

    override suspend fun updateGoal(goal: Goal) {
        val request = UpdateGoalRequest(
            name = goal.title,
            description = goal.description,
            targetAmount = goal.targetAmount,
            localImagePath = goal.localImagePath
        )

        goalApiService.updateGoal(
            goal.remoteId,
            request
        )

        goalDao.updateGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goal: Goal) {
        goalApiService.deleteGoal(goal.remoteId)
        goalDao.deleteByRemoteId(goal.remoteId)
    }

    override suspend fun addContribution(
        goalId: String,
        userId: String,
        amount: Double
    ) {
        val request = ContributionRequest(
            goalId,
            userId,
            amount
        )

        goalApiService.addContribution(request)

        val localGoal = goalDao.getGoalByRemoteId(goalId)

        if (localGoal != null) {
            val updatedGoal = localGoal.copy(
                currentAmount = localGoal.currentAmount + amount
            )

            goalDao.updateGoal(updatedGoal)
        }
    }

    override suspend fun withdraw(
        goalId: String,
        userId: String,
        amount: Double
    ) {
        val request = ContributionRequest(
            goalId,
            userId,
            amount
        )

        goalApiService.withdraw(request)
        syncGoals()
    }
}