package com.fingoal.app.domain.usecase.habits

import com.fingoal.app.domain.repository.HabitRepository

class SyncHabitsUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke() {
        repository.syncHabits()
    }
}