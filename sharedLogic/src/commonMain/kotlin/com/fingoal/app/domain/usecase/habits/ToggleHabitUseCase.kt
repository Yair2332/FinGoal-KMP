package com.fingoal.app.domain.usecase.habits

import com.fingoal.app.domain.repository.HabitRepository

class ToggleHabitUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: String) {
        repository.toggleHabit(habitId)
    }
}