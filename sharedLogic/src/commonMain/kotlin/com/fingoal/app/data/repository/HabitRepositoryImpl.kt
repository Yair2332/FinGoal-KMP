package com.fingoal.app.data.repository

import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.data.local.dao.HabitDao
import com.fingoal.app.data.local.entities.HabitEntity
import com.fingoal.app.data.mapper.toDomain
import com.fingoal.app.data.mapper.toEntity
import com.fingoal.app.data.remote.HabitApiService
import com.fingoal.app.data.remote.dto.HabitDto
import com.fingoal.app.data.remote.dto.UpdateHabitRequest
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HabitRepositoryImpl(
    private val api: HabitApiService,
    private val dao: HabitDao,
    private val userPreferences: UserPreferences
) : HabitRepository {

    private fun getTodayString(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
    }

    override fun getHabits(): Flow<List<Habit>> {
        return dao.getAllHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncHabits() {
        try {
            val userId = userPreferences.userId.firstOrNull() ?: throw Exception("No autorizado")
            val todayStr = getTodayString()

            val remote = api.getHabits(userId, todayStr)
            val entities = remote.map { dto ->
                HabitEntity(
                    remoteId = dto.id ?: "",
                    title = dto.title ?: "",
                    description = dto.description ?: "",
                    frequency = dto.frequency ?: "",
                    isActive = dto.isActive ?: true,
                    streak = dto.streak ?: 0,
                    lastCompletedAt = dto.lastCompletedAt ?: 0L,
                    completedToday = dto.completedToday ?: false
                )
            }
            dao.clearAllHabits()
            dao.insertHabits(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun toggleHabit(habitId: String) {
        try {
            val todayStr = getTodayString()
            val response = api.toggleHabit(habitId, mapOf("todayStr" to todayStr))

            val currentHabit = dao.getHabitByRemoteId(habitId)
            val completedToday = response["completedToday"] as? Boolean ?: false
            val streak = (response["streak"] as? Number)?.toInt() ?: 0

            if (currentHabit != null) {
                dao.updateHabit(currentHabit.copy(completedToday = completedToday, streak = streak))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        val request = UpdateHabitRequest(
            title = habit.name,
            description = habit.description,
            frequency = habit.frequency,
            isActive = true
        )

        api.updateHabit(habit.remoteId, request)
        dao.updateHabit(habit.toEntity())
        syncHabits()
    }

    override suspend fun deleteHabit(habitId: String) {
        api.deleteHabit(habitId)
        dao.deleteHabitByRemoteId(habitId)
    }

    override suspend fun createHabit(habit: Habit) {
        val userId = userPreferences.userId.firstOrNull() ?: throw Exception("No autorizado")
        val newHabit = HabitDto(
            id = "",
            title = habit.name,
            description = habit.description,
            frequency = habit.frequency,
            isActive = true,
            streak = 0,
            lastCompletedAt = 0,
            completedToday = false,
            userId = userId
        )
        api.createHabit(newHabit)
        syncHabits()
    }
}