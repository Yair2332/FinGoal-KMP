package com.fingoal.app.ui.screens.goals

import com.fingoal.app.domain.model.Goal
import com.fingoal.app.ui.components.AssistantQuestion

data class GoalUiState(
    val isLoading: Boolean = false,
    val goals: List<Goal> = emptyList(),
    val errorMessage: String? = null,
    val assistantQuestions: List<AssistantQuestion> = emptyList()
)