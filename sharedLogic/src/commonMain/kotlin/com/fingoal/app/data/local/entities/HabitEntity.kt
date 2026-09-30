package com.fingoal.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String,
    val title: String,
    val description: String,
    val frequency: String,
    val isActive: Boolean,
    val streak: Int,
    val lastCompletedAt: Long,
    val completedToday: Boolean
)