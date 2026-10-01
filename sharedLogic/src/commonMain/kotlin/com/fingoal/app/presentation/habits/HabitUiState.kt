package com.fingoal.app.presentation.habits

import com.fingoal.app.domain.model.Habit

data class HabitUiState(
    val isLoading: Boolean = false,
    val habits: List<Habit> = emptyList(),
    val errorMessage: String? = null,
    val showAddDialog: Boolean = false,
    val habitToEdit: Habit? = null,
    val habitToDelete: Habit? = null
)