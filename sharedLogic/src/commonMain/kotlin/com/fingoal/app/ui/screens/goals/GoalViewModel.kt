package com.fingoal.app.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.domain.model.Goal
import com.fingoal.app.domain.usecase.goals.AddGoalContributionUseCase
import com.fingoal.app.domain.usecase.goals.AddGoalUseCase
import com.fingoal.app.domain.usecase.goals.DeleteGoalUseCase
import com.fingoal.app.domain.usecase.goals.GetGoalsUseCase
import com.fingoal.app.domain.usecase.goals.SyncGoalsUseCase
import com.fingoal.app.domain.usecase.goals.UpdateGoalUseCase
import com.fingoal.app.domain.usecase.goals.WithdrawGoalUseCase
import com.fingoal.app.ui.components.AssistantQuestion
import com.fingoal.app.ui.screens.goals.components.GoalSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoalViewModel(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val addGoalUseCase: AddGoalUseCase,
    private val syncGoalsUseCase: SyncGoalsUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val addGoalContributionUseCase: AddGoalContributionUseCase,
    private val withdrawGoalUseCase: WithdrawGoalUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
        refreshGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            getGoalsUseCase().collect { list ->

                val assistantQuestions =
                    buildAssistantQuestions(list)

                _uiState.update {
                    it.copy(
                        goals = list,
                        isLoading = false,
                        assistantQuestions = assistantQuestions
                    )
                }
            }
        }
    }

    private fun buildAssistantQuestions(
        goals: List<Goal>
    ): List<AssistantQuestion> {

        val totalGoals = goals.size

        return listOf(
            AssistantQuestion(
                question = "¿Cuántas metas tengo?",
                answer = if (totalGoals == 0) {
                    "Actualmente no tienes metas registradas."
                } else {
                    "Actualmente tienes $totalGoals metas registradas."
                }
            )
        )
    }

    fun refreshGoals() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            syncGoalsUseCase()
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message) }
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun saveGoal(
        goal: Goal?,
        title: String,
        desc: String,
        amount: Double,
        image: String
    ) = viewModelScope.launch {

        println(
            """
        [GoalDebug] SAVE START
        goal=${goal?.remoteId}
        title=$title
        description=$desc
        targetAmount=$amount
        image=$image
        """.trimIndent()
        )

        try {
            if (goal == null) {
                addGoalUseCase(title, desc, amount, image)

                println("[GoalDebug] ADD GOAL COMPLETED")

            } else {
                updateGoalUseCase(
                    goal.copy(
                        title = title,
                        description = desc,
                        targetAmount = amount,
                        localImagePath = image
                    )
                )

                println("[GoalDebug] UPDATE GOAL COMPLETED")
            }

            println("[GoalDebug] SAVE END")

        } catch (e: Exception) {

            println("[GoalDebug] SAVE ERROR: ${e.message}")
            e.printStackTrace()

            _uiState.update {
                it.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteGoal(goal: Goal) = viewModelScope.launch {
        try {
            deleteGoalUseCase(goal)
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    fun handleContribution(
        goalId: String,
        amount: Double,
        isAdding: Boolean
    ) = viewModelScope.launch {
        try {
            val userId = userPreferences.userId.firstOrNull() ?: throw Exception("No autorizado")
            if (isAdding) {
                addGoalContributionUseCase(goalId, userId, amount)
            } else {
                withdrawGoalUseCase(goalId, userId, amount)
            }
            syncGoalsUseCase()
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    fun getGoalSummary(): GoalSummary {
        val goals = _uiState.value.goals

        val totalTarget = goals.sumOf {
            it.targetAmount
        }

        val averageTarget =
            if (goals.isEmpty()) {
                0.0
            } else {
                totalTarget / goals.size
            }

        return GoalSummary(
            totalGoals = goals.size,
            totalTarget = totalTarget,
            averageTarget = averageTarget
        )
    }
}