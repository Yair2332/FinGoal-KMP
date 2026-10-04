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

        if (goals.isEmpty()) {
            return listOf(
                AssistantQuestion(
                    question = "¿Cuántas metas tengo?",
                    answer = "Actualmente no tienes metas registradas."
                ),
                AssistantQuestion(
                    question = "¿Tengo metas activas?",
                    answer = "Todavía no tienes metas para alcanzar."
                ),
                AssistantQuestion(
                    question = "¿Cuánto dinero tengo ahorrado en mis metas?",
                    answer = "Todavía no tienes dinero acumulado en metas."
                ),
                AssistantQuestion(
                    question = "¿Cuánto me falta para alcanzar mis metas?",
                    answer = "No tienes metas pendientes actualmente."
                )
            )
        }

        // =========================================================
        // PROGRESO INDIVIDUAL
        // =========================================================

        fun progress(goal: Goal): Double {
            if (goal.targetAmount <= 0.0) return 0.0

            return (
                    goal.currentAmount / goal.targetAmount
                    ) * 100.0
        }

        val completedGoals = goals.count {
            it.currentAmount >= it.targetAmount &&
                    it.targetAmount > 0
        }

        val pendingGoals = goals.count {
            it.currentAmount < it.targetAmount
        }

        val totalTarget = goals.sumOf {
            it.targetAmount
        }

        val totalSaved = goals.sumOf {
            it.currentAmount
        }

        val totalRemaining = goals.sumOf {
            (it.targetAmount - it.currentAmount)
                .coerceAtLeast(0.0)
        }

        val overallProgress =
            if (totalTarget > 0) {
                (totalSaved / totalTarget) * 100.0
            } else {
                0.0
            }

        // =========================================================
        // META MÁS AVANZADA
        // =========================================================

        val mostAdvancedGoal = goals
            .filter { it.targetAmount > 0 }
            .maxByOrNull {
                progress(it)
            }

        // =========================================================
        // META MÁS CERCANA
        // =========================================================

        val closestGoal = goals
            .filter {
                it.currentAmount < it.targetAmount &&
                        it.targetAmount > 0
            }
            .minByOrNull {
                it.targetAmount - it.currentAmount
            }

        // =========================================================
        // META MÁS LEJANA
        // =========================================================

        val furthestGoal = goals
            .filter {
                it.targetAmount > 0
            }
            .minByOrNull {
                progress(it)
            }

        // =========================================================
        // META CON MAYOR OBJETIVO
        // =========================================================

        val biggestGoal = goals
            .maxByOrNull {
                it.targetAmount
            }

        // =========================================================
        // META CON MÁS DINERO ACUMULADO
        // =========================================================

        val mostSavedGoal = goals
            .maxByOrNull {
                it.currentAmount
            }

        // =========================================================
        // META PRIORITARIA
        // =========================================================

        val priorityGoal = goals
            .filter {
                it.currentAmount < it.targetAmount
            }
            .maxByOrNull {
                it.priority
            }

        return listOf(

            // =====================================================
            // RESUMEN
            // =====================================================

            AssistantQuestion(
                question = "¿Cuántas metas tengo?",
                answer =
                    "Actualmente tienes $totalGoals metas registradas."
            ),

            AssistantQuestion(
                question = "¿Cuántas metas completé?",
                answer = if (completedGoals == 0) {
                    "Todavía no completaste ninguna meta."
                } else {
                    "Has completado $completedGoals de tus $totalGoals metas."
                }
            ),

            AssistantQuestion(
                question = "¿Cuántas metas tengo pendientes?",
                answer = if (pendingGoals == 0) {
                    "¡Excelente! No tienes metas pendientes."
                } else {
                    "Tienes $pendingGoals metas pendientes por alcanzar."
                }
            ),

            // =====================================================
            // DINERO
            // =====================================================

            AssistantQuestion(
                question = "¿Cuánto dinero tengo ahorrado en mis metas?",
                answer =
                    "Actualmente tienes acumulados " +
                            "$${formatAmount(totalSaved)} " +
                            "en tus metas."
            ),

            AssistantQuestion(
                question = "¿Cuánto me falta para alcanzar mis metas?",
                answer = if (totalRemaining > 0) {
                    "En total te faltan " +
                            "$${formatAmount(totalRemaining)} " +
                            "para alcanzar tus metas pendientes."
                } else {
                    "Ya alcanzaste el objetivo de todas tus metas."
                }
            ),

            AssistantQuestion(
                question = "¿Cuánto dinero necesito para completar todas mis metas?",
                answer =
                    "Necesitas todavía " +
                            "$${formatAmount(totalRemaining)} " +
                            "para completar todas tus metas pendientes."
            ),

            // =====================================================
            // PROGRESO
            // =====================================================

            AssistantQuestion(
                question = "¿Qué porcentaje de mis metas completé?",
                answer =
                    "Considerando el dinero acumulado frente al objetivo total, " +
                            "has completado aproximadamente " +
                            "${overallProgress.coerceAtMost(100.0).toInt()}%."
            ),

            AssistantQuestion(
                question = "¿Cómo voy con mis metas?",
                answer = when {
                    overallProgress >= 100 ->
                        "¡Excelente! Has alcanzado el objetivo total de tus metas."

                    overallProgress >= 75 ->
                        "Vas muy bien. Has alcanzado aproximadamente " +
                                "${overallProgress.toInt()}% de tus objetivos."

                    overallProgress >= 50 ->
                        "Vas por buen camino. Ya alcanzaste aproximadamente " +
                                "${overallProgress.toInt()}%."

                    overallProgress > 0 ->
                        "Ya empezaste a avanzar. Actualmente llevas " +
                                "${overallProgress.toInt()}% de progreso."

                    else ->
                        "Todavía no tienes dinero acumulado en tus metas."
                }
            ),

            // =====================================================
            // META MÁS AVANZADA
            // =====================================================

            AssistantQuestion(
                question = "¿Cuál es mi meta más avanzada?",
                answer = mostAdvancedGoal?.let {
                    "\"${it.title}\" es tu meta más avanzada, " +
                            "con aproximadamente ${progress(it).toInt()}% completado."
                } ?: "No tienes metas con progreso registrado."
            ),

            // =====================================================
            // META MÁS CERCANA
            // =====================================================

            AssistantQuestion(
                question = "¿Qué meta estoy más cerca de completar?",
                answer = closestGoal?.let {
                    val remaining =
                        (it.targetAmount - it.currentAmount)
                            .coerceAtLeast(0.0)

                    "\"${it.title}\" es la meta que tienes más cerca " +
                            "de completar. Te faltan " +
                            "$${formatAmount(remaining)}."
                } ?: "Ya alcanzaste todas tus metas."
            ),

            // =====================================================
            // META MÁS LEJANA
            // =====================================================

            AssistantQuestion(
                question = "¿Cuál es mi meta más atrasada?",
                answer = furthestGoal?.let {
                    "\"${it.title}\" es actualmente tu meta con menor progreso, " +
                            "con aproximadamente ${progress(it).toInt()}%."
                } ?: "No tienes metas pendientes."
            ),

            // =====================================================
            // MAYOR OBJETIVO
            // =====================================================

            AssistantQuestion(
                question = "¿Cuál es mi meta más grande?",
                answer = biggestGoal?.let {
                    "\"${it.title}\" es tu meta con el objetivo más grande, " +
                            "de $${formatAmount(it.targetAmount)}."
                } ?: "No tienes metas registradas."
            ),

            // =====================================================
            // MAYOR AHORRO
            // =====================================================

            AssistantQuestion(
                question = "¿En qué meta tengo más dinero?",
                answer = mostSavedGoal?.let {
                    "\"${it.title}\" es la meta en la que más dinero tienes " +
                            "acumulado: $${formatAmount(it.currentAmount)}."
                } ?: "Todavía no tienes dinero acumulado."
            ),

            // =====================================================
            // PRIORIDAD
            // =====================================================

            AssistantQuestion(
                question = "¿Cuál es mi meta prioritaria?",
                answer = priorityGoal?.let {
                    "\"${it.title}\" es actualmente tu meta prioritaria. " +
                            "Llevas ${progress(it).toInt()}% de progreso."
                } ?: "No tienes metas pendientes."
            ),

            // =====================================================
            // CONSEJO
            // =====================================================

            AssistantQuestion(
                question = "¿En qué meta debería concentrarme?",
                answer = when {
                    closestGoal != null -> {
                        val remaining =
                            (closestGoal.targetAmount -
                                    closestGoal.currentAmount)
                                .coerceAtLeast(0.0)

                        "Podrías concentrarte en \"${closestGoal.title}\", " +
                                "porque es la meta más cercana a completarse. " +
                                "Te faltan $${formatAmount(remaining)}."
                    }

                    else ->
                        "Ya alcanzaste todas tus metas pendientes."
                }
            )
        )
    }

    private fun formatAmount(amount: Double): String {
        return amount.toString()
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