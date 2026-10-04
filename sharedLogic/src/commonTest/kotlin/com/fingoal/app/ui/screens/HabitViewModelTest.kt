package com.fingoal.app.ui.screens

import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.repository.HabitRepository
import com.fingoal.app.domain.usecase.habits.CreateHabitUseCase
import com.fingoal.app.domain.usecase.habits.DeleteHabitUseCase
import com.fingoal.app.domain.usecase.habits.GetHabitsUseCase
import com.fingoal.app.domain.usecase.habits.SyncHabitsUseCase
import com.fingoal.app.domain.usecase.habits.ToggleHabitUseCase
import com.fingoal.app.domain.usecase.habits.UpdateHabitUseCase
import com.fingoal.app.ui.screens.habits.HabitViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HabitViewModelTest {

    @Test
    fun initialState_loadsHabits() = runTest {
        val repository = FakeHabitRepository()

        repository.habits.value = listOf(
            createHabit(
                id = 1L,
                name = "Entrenar",
                frequency = "DIARIO"
            )
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.habits.size)
        assertEquals(
            "Entrenar",
            viewModel.uiState.value.habits.first().name
        )
    }

    @Test
    fun showAddDialog_opensDialogAndClearsEditHabit() {
        val viewModel = createViewModel()

        viewModel.showAddDialog()

        assertTrue(viewModel.uiState.value.showAddDialog)
        assertNull(viewModel.uiState.value.habitToEdit)
    }

    @Test
    fun showEditDialog_setsHabitToEdit() {
        val habit = createHabit(
            id = 1L,
            name = "Leer",
            frequency = "DIARIO"
        )

        val viewModel = createViewModel()

        viewModel.showEditDialog(habit)

        assertTrue(viewModel.uiState.value.showAddDialog)
        assertEquals(
            habit,
            viewModel.uiState.value.habitToEdit
        )
    }

    @Test
    fun hideDialog_closesDialogAndClearsHabit() {
        val habit = createHabit(
            id = 1L,
            name = "Leer",
            frequency = "DIARIO"
        )

        val viewModel = createViewModel()

        viewModel.showEditDialog(habit)
        viewModel.hideDialog()

        assertFalse(viewModel.uiState.value.showAddDialog)
        assertNull(viewModel.uiState.value.habitToEdit)
    }

    @Test
    fun showDeleteDialog_setsHabitToDelete() {
        val habit = createHabit(
            id = 1L,
            name = "Entrenar",
            frequency = "DIARIO"
        )

        val viewModel = createViewModel()

        viewModel.showDeleteDialog(habit)

        assertEquals(
            habit,
            viewModel.uiState.value.habitToDelete
        )
    }

    @Test
    fun hideDeleteDialog_clearsHabitToDelete() {
        val habit = createHabit(
            id = 1L,
            name = "Entrenar",
            frequency = "DIARIO"
        )

        val viewModel = createViewModel()

        viewModel.showDeleteDialog(habit)
        viewModel.hideDeleteDialog()

        assertNull(
            viewModel.uiState.value.habitToDelete
        )
    }

    @Test
    fun syncHabits_callsUseCase() = runTest {
        val repository = FakeHabitRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val initialCalls = repository.syncCalls

        viewModel.syncHabits()

        advanceUntilIdle()

        assertEquals(
            initialCalls + 1,
            repository.syncCalls
        )
    }

    @Test
    fun syncHabits_withError_setsErrorMessage() = runTest {
        val repository = FakeHabitRepository()

        repository.syncError =
            Exception("Error de sincronización")

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        // El sync del init ya produjo el error.
        assertEquals(
            "Error al sincronizar: Error de sincronización",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun toggleHabit_callsUseCaseWithCorrectId() = runTest {
        val repository = FakeHabitRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.toggleHabit("habit-123")

        advanceUntilIdle()

        assertEquals(
            1,
            repository.toggleCalls
        )

        assertEquals(
            "habit-123",
            repository.lastToggledHabitId
        )
    }

    @Test
    fun toggleHabit_withError_setsErrorMessage() = runTest {
        val repository = FakeHabitRepository()

        repository.toggleError =
            Exception("No se pudo actualizar")

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.toggleHabit("habit-123")

        advanceUntilIdle()

        assertEquals(
            "Error al actualizar: No se pudo actualizar",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun updateHabit_callsUseCaseWithCorrectHabit() = runTest {
        val repository = FakeHabitRepository()

        val habit = createHabit(
            id = 1L,
            name = "Leer",
            frequency = "DIARIO"
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.updateHabit(habit)

        advanceUntilIdle()

        assertEquals(
            1,
            repository.updateCalls
        )

        assertEquals(
            habit,
            repository.lastUpdatedHabit
        )
    }

    @Test
    fun updateHabit_withError_setsErrorMessage() = runTest {
        val repository = FakeHabitRepository()

        repository.updateError =
            Exception("No se pudo editar")

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.updateHabit(
            createHabit(
                id = 1L,
                name = "Leer",
                frequency = "DIARIO"
            )
        )

        advanceUntilIdle()

        assertEquals(
            "Error al editar: No se pudo editar",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun deleteHabit_callsUseCaseAndHidesDialog() = runTest {
        val repository = FakeHabitRepository()

        val habit = createHabit(
            id = 1L,
            name = "Entrenar",
            frequency = "DIARIO"
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.showDeleteDialog(habit)

        viewModel.deleteHabit()

        advanceUntilIdle()

        assertEquals(
            1,
            repository.deleteCalls
        )

        assertEquals(
            "habit-1",
            repository.lastDeletedHabitId
        )

        assertNull(
            viewModel.uiState.value.habitToDelete
        )
    }

    @Test
    fun deleteHabit_withoutSelectedHabit_doesNothing() = runTest {
        val repository = FakeHabitRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.deleteHabit()

        advanceUntilIdle()

        assertEquals(
            0,
            repository.deleteCalls
        )
    }

    @Test
    fun createHabit_createsCorrectHabit() = runTest {
        val repository = FakeHabitRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.createHabit(
            title = "Leer",
            description = "Leer 20 minutos",
            frequency = "DIARIO"
        )

        advanceUntilIdle()

        assertEquals(
            1,
            repository.createCalls
        )

        assertEquals(
            "Leer",
            repository.lastCreatedHabit?.name
        )

        assertEquals(
            "Leer 20 minutos",
            repository.lastCreatedHabit?.description
        )

        assertEquals(
            "DIARIO",
            repository.lastCreatedHabit?.frequency
        )
    }

    @Test
    fun createHabit_withError_setsErrorMessage() = runTest {
        val repository = FakeHabitRepository()

        repository.createError =
            Exception("No se pudo crear")

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.createHabit(
            title = "Leer",
            description = "Leer",
            frequency = "DIARIO"
        )

        advanceUntilIdle()

        assertEquals(
            "Error al crear: No se pudo crear",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun assistantQuestions_calculatesHabitTypes() = runTest {
        val repository = FakeHabitRepository()

        repository.habits.value = listOf(
            createHabit(1L, "Entrenar", "DIARIO"),
            createHabit(2L, "Leer", "DIARIO"),
            createHabit(3L, "Ordenar", "SEMANAL"),
            createHabit(4L, "Ahorrar", "MENSUAL")
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val questions =
            viewModel.uiState.value.assistantQuestions

        assertEquals(4, questions.size)

        assertEquals(
            "Actualmente tienes 4 hábitos registrados.",
            questions[0].answer
        )

        assertEquals(
            "Tienes 2 hábitos diarios.",
            questions[1].answer
        )

        assertEquals(
            "Tienes 1 hábitos semanales.",
            questions[2].answer
        )

        assertEquals(
            "Tienes 1 hábitos mensuales.",
            questions[3].answer
        )
    }

    @Test
    fun assistantQuestions_withoutHabits_showsEmptyMessages() = runTest {
        val repository = FakeHabitRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val questions =
            viewModel.uiState.value.assistantQuestions

        assertEquals(4, questions.size)

        assertEquals(
            "Actualmente no tienes hábitos registrados.",
            questions[0].answer
        )

        assertEquals(
            "No tienes hábitos diarios registrados.",
            questions[1].answer
        )

        assertEquals(
            "No tienes hábitos semanales registrados.",
            questions[2].answer
        )

        assertEquals(
            "No tienes hábitos mensuales registrados.",
            questions[3].answer
        )
    }

    @Test
    fun getHabitSummary_calculatesCorrectValues() = runTest {
        val repository = FakeHabitRepository()

        repository.habits.value = listOf(
            createHabit(1L, "Entrenar", "DIARIO"),
            createHabit(2L, "Leer", "DIARIO"),
            createHabit(3L, "Ordenar", "SEMANAL"),
            createHabit(4L, "Ahorrar", "MENSUAL"),
            createHabit(5L, "Cocinar", "MENSUAL")
        )

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val summary = viewModel.getHabitSummary()

        assertEquals(5, summary.total)
        assertEquals(2, summary.daily)
        assertEquals(1, summary.weekly)
        assertEquals(2, summary.monthly)
    }

    @Test
    fun getHabitSummary_withoutHabits_returnsZeros() = runTest {
        val repository = FakeHabitRepository()

        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        val summary = viewModel.getHabitSummary()

        assertEquals(0, summary.total)
        assertEquals(0, summary.daily)
        assertEquals(0, summary.weekly)
        assertEquals(0, summary.monthly)
    }

    private fun createViewModel(
        repository: FakeHabitRepository = FakeHabitRepository()
    ): HabitViewModel {
        return HabitViewModel(
            getHabitsUseCase = GetHabitsUseCase(repository),
            syncHabitsUseCase = SyncHabitsUseCase(repository),
            toggleHabitUseCase = ToggleHabitUseCase(repository),
            updateHabitUseCase = UpdateHabitUseCase(repository),
            deleteHabitUseCase = DeleteHabitUseCase(repository),
            createHabitUseCase = CreateHabitUseCase(repository)
        )
    }

    private fun createHabit(
        id: Long,
        name: String,
        frequency: String
    ): Habit {
        return Habit(
            id = id,
            remoteId = "habit-$id",
            name = name,
            description = "Descripción",
            frequency = frequency,
            completedToday = false,
            streak = 0
        )
    }
}

private class FakeHabitRepository : HabitRepository {

    val habits =
        MutableStateFlow<List<Habit>>(emptyList())

    var syncCalls = 0
    var toggleCalls = 0
    var updateCalls = 0
    var deleteCalls = 0
    var createCalls = 0

    var lastToggledHabitId: String? = null
    var lastUpdatedHabit: Habit? = null
    var lastDeletedHabitId: String? = null
    var lastCreatedHabit: Habit? = null

    var syncError: Exception? = null
    var toggleError: Exception? = null
    var updateError: Exception? = null
    var deleteError: Exception? = null
    var createError: Exception? = null

    override fun getHabits(): Flow<List<Habit>> =
        habits

    override suspend fun syncHabits() {
        syncCalls++

        syncError?.let {
            throw it
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        updateCalls++
        lastUpdatedHabit = habit

        updateError?.let {
            throw it
        }
    }

    override suspend fun deleteHabit(habitId: String) {
        deleteCalls++
        lastDeletedHabitId = habitId

        deleteError?.let {
            throw it
        }
    }

    override suspend fun createHabit(habit: Habit) {
        createCalls++
        lastCreatedHabit = habit

        createError?.let {
            throw it
        }
    }

    override suspend fun toggleHabit(habitId: String) {
        toggleCalls++
        lastToggledHabitId = habitId

        toggleError?.let {
            throw it
        }
    }
}