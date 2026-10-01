package com.fingoal.app.domain.usecase.habits

import com.fingoal.app.domain.repository.HabitRepository

class DeleteHabitUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: String) {
        repository.deleteHabit(habitId)
    }
}