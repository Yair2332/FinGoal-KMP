package com.fingoal.app.domain.repository

import com.fingoal.app.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    suspend fun syncHabits()
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habitId: String)
    suspend fun createHabit(habit: Habit)
    suspend fun toggleHabit(habitId: String)
}