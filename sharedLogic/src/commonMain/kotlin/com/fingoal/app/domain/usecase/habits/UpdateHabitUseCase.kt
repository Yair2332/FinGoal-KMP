package com.fingoal.app.domain.usecase.habits

import com.fingoal.app.data.local.entities.HabitEntity
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.repository.HabitRepository

class UpdateHabitUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        repository.updateHabit(habit)
    }
}