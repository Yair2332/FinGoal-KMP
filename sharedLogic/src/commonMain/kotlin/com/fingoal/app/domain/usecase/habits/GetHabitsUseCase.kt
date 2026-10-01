package com.fingoal.app.domain.usecase.habits

import com.fingoal.app.domain.repository.HabitRepository

class GetHabitsUseCase(private val repo: HabitRepository) {
    operator fun invoke() = repo.getHabits()
}