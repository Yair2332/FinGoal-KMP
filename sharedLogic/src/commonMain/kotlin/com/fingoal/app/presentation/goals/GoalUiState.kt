package com.fingoal.app.presentation.goals

import com.fingoal.app.domain.model.Goal

data class GoalUiState(
    val isLoading: Boolean = false,
    val goals: List<Goal> = emptyList(),
    val errorMessage: String? = null
)