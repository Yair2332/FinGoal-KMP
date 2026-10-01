package com.fingoal.app.data.mapper

import com.fingoal.app.data.local.entities.HabitEntity
import com.fingoal.app.domain.model.Habit

fun HabitEntity.toDomain() = Habit(
    id = this.id.toLong(),
    remoteId = this.remoteId,
    name = this.title,
    description = this.description,
    frequency = this.frequency,
    completedToday = this.completedToday,
    streak = this.streak
)

fun Habit.toEntity() = HabitEntity(
    id = this.id.toInt(),
    remoteId = this.remoteId,
    title = this.name,
    description = this.description,
    frequency = this.frequency,
    isActive = true,
    streak = this.streak,
    lastCompletedAt = 0,
    completedToday = completedToday
)