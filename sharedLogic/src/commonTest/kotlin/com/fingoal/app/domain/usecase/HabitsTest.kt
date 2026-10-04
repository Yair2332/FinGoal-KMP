package com.fingoal.app.domain.usecase

import com.fingoal.app.domain.model.Habit
import com.fingoal.app.domain.repository.HabitRepository
import com.fingoal.app.domain.usecase.habits.CreateHabitUseCase
import com.fingoal.app.domain.usecase.habits.DeleteHabitUseCase
import com.fingoal.app.domain.usecase.habits.GetHabitsUseCase
import com.fingoal.app.domain.usecase.habits.SyncHabitsUseCase
import com.fingoal.app.domain.usecase.habits.ToggleHabitUseCase
import com.fingoal.app.domain.usecase.habits.UpdateHabitUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HabitsTest {

    private val habit = Habit(
        id = 1L,
        remoteId = "habit-1",
        name = "Entrenar",
        description = "Entrenar durante 1 hora",
        frequency = "DAILY",
        completedToday = false,
        streak = 5
    )

    // ---------------------------------------------------------
    // GET HABITS
    // ---------------------------------------------------------

    @Test
    fun getHabits_returnsRepositoryHabits() = runTest {
        val fakeRepository = FakeHabitRepository()

        fakeRepository.habits.value = listOf(habit)

        val useCase = GetHabitsUseCase(fakeRepository)

        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals(habit, result[0])
    }

    // ---------------------------------------------------------
    // CREATE HABIT
    // ---------------------------------------------------------

    @Test
    fun createHabit_callsRepositoryWithCorrectHabit() = runTest {
        val fakeRepository = FakeHabitRepository()
        val useCase = CreateHabitUseCase(fakeRepository)

        useCase(habit)

        assertTrue(fakeRepository.createHabitCalled)
        assertEquals(habit, fakeRepository.createdHabit)
    }

    // ---------------------------------------------------------
    // UPDATE HABIT
    // ---------------------------------------------------------

    @Test
    fun updateHabit_callsRepositoryWithCorrectHabit() = runTest {
        val fakeRepository = FakeHabitRepository()
        val useCase = UpdateHabitUseCase(fakeRepository)

        val updatedHabit = habit.copy(
            name = "Entrenar en el gimnasio",
            description = "Entrenar durante 1 hora y media",
            frequency = "WEEKLY"
        )

        useCase(updatedHabit)

        assertTrue(fakeRepository.updateHabitCalled)
        assertEquals(updatedHabit, fakeRepository.updatedHabit)
    }

    // ---------------------------------------------------------
    // DELETE HABIT
    // ---------------------------------------------------------

    @Test
    fun deleteHabit_callsRepositoryWithCorrectId() = runTest {
        val fakeRepository = FakeHabitRepository()
        val useCase = DeleteHabitUseCase(fakeRepository)

        useCase("habit-123")

        assertTrue(fakeRepository.deleteHabitCalled)
        assertEquals("habit-123", fakeRepository.deletedHabitId)
    }

    // ---------------------------------------------------------
    // TOGGLE HABIT
    // ---------------------------------------------------------

    @Test
    fun toggleHabit_callsRepositoryWithCorrectId() = runTest {
        val fakeRepository = FakeHabitRepository()
        val useCase = ToggleHabitUseCase(fakeRepository)

        useCase("habit-456")

        assertTrue(fakeRepository.toggleHabitCalled)
        assertEquals("habit-456", fakeRepository.toggledHabitId)
    }

    // ---------------------------------------------------------
    // SYNC HABITS
    // ---------------------------------------------------------

    @Test
    fun syncHabits_callsRepository() = runTest {
        val fakeRepository = FakeHabitRepository()
        val useCase = SyncHabitsUseCase(fakeRepository)

        useCase()

        assertTrue(fakeRepository.syncHabitsCalled)
    }
}

/**
 * Fake del HabitRepository para probar los UseCases
 * sin depender de Room, API o la implementación real.
 */
private class FakeHabitRepository : HabitRepository {

    val habits = MutableStateFlow<List<Habit>>(emptyList())

    var createHabitCalled = false
    var updateHabitCalled = false
    var deleteHabitCalled = false
    var toggleHabitCalled = false
    var syncHabitsCalled = false

    var createdHabit: Habit? = null
    var updatedHabit: Habit? = null
    var deletedHabitId: String? = null
    var toggledHabitId: String? = null

    override fun getHabits(): Flow<List<Habit>> {
        return habits
    }

    override suspend fun syncHabits() {
        syncHabitsCalled = true
    }

    override suspend fun updateHabit(habit: Habit) {
        updateHabitCalled = true
        updatedHabit = habit
    }

    override suspend fun deleteHabit(habitId: String) {
        deleteHabitCalled = true
        deletedHabitId = habitId
    }

    override suspend fun createHabit(habit: Habit) {
        createHabitCalled = true
        createdHabit = habit
    }

    override suspend fun toggleHabit(habitId: String) {
        toggleHabitCalled = true
        toggledHabitId = habitId
    }
}