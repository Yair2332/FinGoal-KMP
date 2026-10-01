package com.fingoal.app.data.mapper

import com.fingoal.app.data.local.entities.GoalEntity
import com.fingoal.app.domain.model.Goal

fun GoalEntity.toDomain() = Goal(
    id = this.id,
    remoteId = this.remoteId,
    title = this.title,
    description = this.description,
    targetAmount = this.targetAmount,
    currentAmount = this.currentAmount,
    createdAt = this.createdAt,
    priority = this.priority,
    status = this.status,
    localImagePath = this.localImagePath
)

fun Goal.toEntity() = GoalEntity(
    id = this.id,
    remoteId = this.remoteId,
    title = this.title,
    description = this.description,
    targetAmount = this.targetAmount,
    currentAmount = this.currentAmount,
    createdAt = this.createdAt,
    priority = this.priority,
    status = this.status,
    localImagePath = this.localImagePath
)