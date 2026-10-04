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

        val dailyHabits =
            habits.count {
                it.frequency.equals(
                    "DIARIO",
                    ignoreCase = true
                )
            }

        val weeklyHabits =
            habits.count {
                it.frequency.equals(
                    "SEMANAL",
                    ignoreCase = true
                )
            }

        val monthlyHabits =
            habits.count {
                it.frequency.equals(
                    "MENSUAL",
                    ignoreCase = true
                )
            }

        return listOf(
            AssistantQuestion(
                question = "¿Cuántos hábitos tengo?",
                answer = if (totalHabits == 0) {
                    "Actualmente no tienes hábitos registrados."
                } else {
                    "Actualmente tienes $totalHabits hábitos registrados."
                }
            ),

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