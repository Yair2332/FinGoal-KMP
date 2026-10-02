package com.fingoal.app.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.usecase.habits.CreateHabitUseCase
import com.fingoal.app.domain.usecase.habits.DeleteHabitUseCase
import com.fingoal.app.domain.usecase.habits.GetHabitsUseCase
import com.fingoal.app.domain.usecase.habits.SyncHabitsUseCase
import com.fingoal.app.domain.usecase.habits.ToggleHabitUseCase
import com.fingoal.app.domain.usecase.habits.UpdateHabitUseCase
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

    fun showAddDialog() = _uiState.update { it.copy(showAddDialog = true, habitToEdit = null) }
    fun showEditDialog(habit: Habit) = _uiState.update { it.copy(showAddDialog = true, habitToEdit = habit) }
    fun hideDialog() = _uiState.update { it.copy(showAddDialog = false, habitToEdit = null) }

    fun showDeleteDialog(habit: Habit) = _uiState.update { it.copy(habitToDelete = habit) }
    fun hideDeleteDialog() = _uiState.update { it.copy(habitToDelete = null) }

    fun syncHabits() {
        viewModelScope.launch {
            try {
                syncHabitsUseCase()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al sincronizar: ${e.message}") }
            }
        }
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase().collect { list ->
                _uiState.update { it.copy(habits = list) }
            }
        }
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
                _uiState.update { it.copy(errorMessage = "Error al editar: ${e.message}") }
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

    fun createHabit(title: String, description: String, frequency: String) {
        viewModelScope.launch {
            try {
                val newHabit = Habit(
                    name = title,
                    description = description,
                    frequency = frequency
                )
                createHabitUseCase(newHabit)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al crear: ${e.message}") }
            }
        }
    }
}