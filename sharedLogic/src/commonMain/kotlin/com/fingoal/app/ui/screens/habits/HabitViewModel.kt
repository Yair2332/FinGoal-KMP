package com.fingoal.app.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.usecase.habits.CreateHabitUseCase
import com.fingoal.app.domain.usecase.habits.DeleteHabitUseCase
import com.fingoal.app.domain.usecase.habits.GetHabitsUseCase
import com.fingoal.app.domain.usecase.habits.SyncHabitsUseCase
import com.fingoal.app.domain.usecase.habits.ToggleHabitUseCase
import com.fingoal.app.domain.usecase.habits.UpdateHabitUseCase
import com.fingoal.app.ui.components.AssistantQuestion
import com.fingoal.app.ui.screens.habits.components.HabitSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HabitViewModel(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val syncHabitsUseCase: SyncHabitsUseCase,
    private val toggleHabitUseCase: ToggleHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
        syncHabits()
    }

    fun showAddDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = true,
                habitToEdit = null
            )
        }
    }

    fun showEditDialog(habit: Habit) {
        _uiState.update {
            it.copy(
                showAddDialog = true,
                habitToEdit = habit
            )
        }
    }

    fun hideDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = false,
                habitToEdit = null
            )
        }
    }

    fun showDeleteDialog(habit: Habit) {
        _uiState.update {
            it.copy(
                habitToDelete = habit
            )
        }
    }

    fun hideDeleteDialog() {
        _uiState.update {
            it.copy(
                habitToDelete = null
            )
        }
    }

    fun syncHabits() {
        viewModelScope.launch {
            try {
                syncHabitsUseCase()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al sincronizar: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase().collect { list ->

                val assistantQuestions =
                    buildAssistantQuestions(list)

                _uiState.update {
                    it.copy(
                        habits = list,
                        assistantQuestions = assistantQuestions
                    )
                }
            }
        }
    }

    private fun buildAssistantQuestions(
        habits: List<Habit>
    ): List<AssistantQuestion> {

        val totalHabits = habits.size

        val completedToday = habits.count {
            it.completedToday
        }

        val pendingToday = habits.count {
            !it.completedToday
        }

        val dailyHabits = habits.count {
            it.frequency.equals(
                "DIARIO",
                ignoreCase = true
            )
        }

        val weeklyHabits = habits.count {
            it.frequency.equals(
                "SEMANAL",
                ignoreCase = true
            )
        }

        val monthlyHabits = habits.count {
            it.frequency.equals(
                "MENSUAL",
                ignoreCase = true
            )
        }

        val totalStreak = habits.sumOf {
            it.streak
        }

        val bestStreakHabit = habits.maxByOrNull {
            it.streak
        }

        val completionPercentage =
            if (totalHabits > 0) {
                (completedToday.toDouble() / totalHabits) * 100
            } else {
                0.0
            }

        val pendingHabitWithBestStreak = habits
            .filter { !it.completedToday }
            .maxByOrNull { it.streak }

        return listOf(

            // =====================================================
            // RESUMEN
            // =====================================================

            AssistantQuestion(
                question = "¿Cuántos hábitos tengo?",
                answer = if (totalHabits == 0) {
                    "Actualmente no tienes hábitos registrados."
                } else {
                    "Actualmente tienes $totalHabits hábitos registrados."
                }
            ),

            // =====================================================
            // FRECUENCIA
            // =====================================================

            AssistantQuestion(
                question = "¿Cuántos hábitos diarios tengo?",
                answer = if (dailyHabits == 0) {
                    "No tienes hábitos diarios registrados."
                } else {
                    "Tienes $dailyHabits hábitos diarios."
                }
            ),

            AssistantQuestion(
                question = "¿Cuántos hábitos semanales tengo?",
                answer = if (weeklyHabits == 0) {
                    "No tienes hábitos semanales registrados."
                } else {
                    "Tienes $weeklyHabits hábitos semanales."
                }
            ),

            AssistantQuestion(
                question = "¿Cuántos hábitos mensuales tengo?",
                answer = if (monthlyHabits == 0) {
                    "No tienes hábitos mensuales registrados."
                } else {
                    "Tienes $monthlyHabits hábitos mensuales."
                }
            ),

            // =====================================================
            // PROGRESO DE HOY
            // =====================================================

            AssistantQuestion(
                question = "¿Cuántos hábitos completé hoy?",
                answer = if (completedToday == 0) {
                    "Todavía no completaste ningún hábito hoy."
                } else {
                    "Hoy completaste $completedToday de tus " +
                            "$totalHabits hábitos."
                }
            ),

            AssistantQuestion(
                question = "¿Cuántos hábitos me faltan hoy?",
                answer = if (pendingToday == 0) {
                    "¡Excelente! Completaste todos tus hábitos de hoy."
                } else {
                    "Te faltan $pendingToday hábitos por completar hoy."
                }
            ),

            AssistantQuestion(
                question = "¿Qué porcentaje de mis hábitos completé hoy?",
                answer = if (totalHabits == 0) {
                    "No tienes hábitos registrados."
                } else {
                    "Hoy completaste aproximadamente " +
                            "${completionPercentage.toInt()}% de tus hábitos."
                }
            ),

            AssistantQuestion(
                question = "¿Cómo voy con mis hábitos hoy?",
                answer = when {
                    totalHabits == 0 ->
                        "Todavía no tienes hábitos registrados."

                    completionPercentage == 100.0 ->
                        "¡Excelente! Completaste todos tus hábitos de hoy."

                    completionPercentage >= 75 ->
                        "Vas muy bien. Ya completaste " +
                                "${completionPercentage.toInt()}% de tus hábitos."

                    completionPercentage >= 50 ->
                        "Vas por buen camino. Ya completaste más de la mitad " +
                                "de tus hábitos."

                    completionPercentage > 0 ->
                        "Ya empezaste, pero todavía tienes " +
                                "$pendingToday hábitos pendientes."

                    else ->
                        "Todavía no completaste ningún hábito hoy."
                }
            ),

            // =====================================================
            // RACHAS
            // =====================================================

            AssistantQuestion(
                question = "¿Cuál es mi mejor racha?",
                answer = bestStreakHabit?.let {
                    if (it.streak > 0) {
                        "Tu mejor racha es de ${it.streak} días " +
                                "con el hábito \"${it.name}\"."
                    } else {
                        "Todavía no tienes ninguna racha registrada."
                    }
                } ?: "Todavía no tienes hábitos registrados."
            ),

            AssistantQuestion(
                question = "¿Qué hábito tiene mi mejor racha?",
                answer = bestStreakHabit?.let {
                    if (it.streak > 0) {
                        "\"${it.name}\" tiene tu mejor racha, " +
                                "con ${it.streak} días."
                    } else {
                        "Todavía no tienes hábitos con una racha activa."
                    }
                } ?: "Todavía no tienes hábitos registrados."
            ),

            AssistantQuestion(
                question = "¿Cuántos días de racha tengo en total?",
                answer = if (totalStreak == 0) {
                    "Todavía no tienes días de racha acumulados."
                } else {
                    "Tienes $totalStreak días de racha acumulados " +
                            "entre todos tus hábitos."
                }
            ),

            // =====================================================
            // HÁBITOS PENDIENTES
            // =====================================================

            AssistantQuestion(
                question = "¿Qué hábito debería completar hoy?",
                answer = pendingHabitWithBestStreak?.let {
                    if (it.streak > 0) {
                        "Tienes pendiente \"${it.name}\". " +
                                "Lleva una racha de ${it.streak} días, " +
                                "así que completarlo hoy te ayudará a mantenerla."
                    } else {
                        "Tienes pendiente \"${it.name}\". " +
                                "Puedes aprovechar para empezar una nueva racha."
                    }
                } ?: if (totalHabits == 0) {
                    "Todavía no tienes hábitos registrados."
                } else {
                    "¡Excelente! No tienes hábitos pendientes para hoy."
                }
            ),

            AssistantQuestion(
                question = "¿Estoy siendo constante con mis hábitos?",
                answer = when {
                    totalHabits == 0 ->
                        "Todavía no tienes hábitos para analizar."

                    completionPercentage == 100.0 ->
                        "Sí. Hoy completaste todos tus hábitos."

                    completionPercentage >= 75 ->
                        "Sí. Vas bastante bien y estás completando " +
                                "la mayoría de tus hábitos."

                    completionPercentage >= 50 ->
                        "Vas bien, pero todavía puedes mejorar tu constancia."

                    completionPercentage > 0 ->
                        "Ya empezaste, pero tienes varios hábitos pendientes."

                    else ->
                        "Hoy todavía no completaste ningún hábito."
                }
            ),

            // =====================================================
            // HÁBITO CON MAYOR RENDIMIENTO
            // =====================================================

            AssistantQuestion(
                question = "¿Cuál es mi hábito más constante?",
                answer = bestStreakHabit?.let {
                    if (it.streak > 0) {
                        "\"${it.name}\" es tu hábito con mayor racha, " +
                                "con ${it.streak} días consecutivos."
                    } else {
                        "Todavía no tienes suficiente información " +
                                "para determinarlo."
                    }
                } ?: "Todavía no tienes hábitos registrados."
            )
        )
    }

    fun toggleHabit(habitId: String) {
        viewModelScope.launch {
            try {
                toggleHabitUseCase(habitId)
            } catch (e: Exception) {
                e.printStackTrace()

                _uiState.update {
                    it.copy(
                        errorMessage = "Error al actualizar: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                updateHabitUseCase(habit)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al editar: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteHabit() {
        val habit = _uiState.value.habitToDelete ?: return

        viewModelScope.launch {
            deleteHabitUseCase(habit.remoteId)
            hideDeleteDialog()
        }
    }

    fun createHabit(
        title: String,
        description: String,
        frequency: String
    ) {
        viewModelScope.launch {
            try {
                val newHabit = Habit(
                    name = title,
                    description = description,
                    frequency = frequency
                )

                createHabitUseCase(newHabit)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al crear: ${e.message}"
                    )
                }
            }
        }
    }


    fun getHabitSummary(): HabitSummary {
        val habits = _uiState.value.habits

        return HabitSummary(
            total = habits.size,
            daily = habits.count {
                it.frequency.equals(
                    "DIARIO",
                    ignoreCase = true
                )
            },
            weekly = habits.count {
                it.frequency.equals(
                    "SEMANAL",
                    ignoreCase = true
                )
            },
            monthly = habits.count {
                it.frequency.equals(
                    "MENSUAL",
                    ignoreCase = true
                )
            }
        )
    }
}