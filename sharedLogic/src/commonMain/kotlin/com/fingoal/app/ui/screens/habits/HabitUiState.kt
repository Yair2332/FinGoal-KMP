package com.fingoal.app.ui.screens.habits

import com.fingoal.app.domain.model.Habit
import com.fingoal.app.ui.components.AssistantQuestion

data class HabitUiState(
    val isLoading: Boolean = false,
    val habits: List<Habit> = emptyList(),
    val errorMessage: String? = null,
    val showAddDialog: Boolean = false,
    val habitToEdit: Habit? = null,
    val habitToDelete: Habit? = null,
    val assistantQuestions: List<AssistantQuestion> = emptyList()
)