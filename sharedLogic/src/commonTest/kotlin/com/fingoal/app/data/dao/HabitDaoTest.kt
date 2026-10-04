package com.fingoal.app.data.local.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.fingoal.app.data.local.database.AppDatabase
import com.fingoal.app.data.local.entities.HabitEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HabitDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: HabitDao

    @BeforeTest
    fun setup() {
        database = Room.inMemoryDatabaseBuilder<AppDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()

        dao = database.habitDao()
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    private fun createHabit(
        remoteId: String = "habit-1",
        title: String = "Tomar agua",
        description: String = "Tomar 2 litros de agua",
        frequency: String = "DAILY",
        isActive: Boolean = true,
        streak: Int = 5,
        lastCompletedAt: Long = 1000L,
        completedToday: Boolean = false
    ) = HabitEntity(
        remoteId = remoteId,
        title = title,
        description = description,
        frequency = frequency,
        isActive = isActive,
        streak = streak,
        lastCompletedAt = lastCompletedAt,
        completedToday = completedToday
    )

    @Test
    fun insertHabits_savesHabits() = runTest {
        val habits = listOf(
            createHabit(
                remoteId = "habit-1",
                title = "Tomar agua"
            ),
            createHabit(
                remoteId = "habit-2",
                title = "Entrenar"
            )
        )

        dao.insertHabits(habits)

        val result = dao.getAllHabits().first()

        assertEquals(2, result.size)
        assertEquals("habit-1", result[0].remoteId)
        assertEquals("habit-2", result[1].remoteId)
    }

    @Test
    fun getAllHabits_returnsAllHabits() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(remoteId = "habit-1"),
                createHabit(remoteId = "habit-2"),
                createHabit(remoteId = "habit-3")
            )
        )

        val result = dao.getAllHabits().first()

        assertEquals(3, result.size)
    }

    @Test
    fun replaceAllHabits_replacesPreviousHabits() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(
                    remoteId = "old-1",
                    title = "Habito anterior 1"
                ),
                createHabit(
                    remoteId = "old-2",
                    title = "Habito anterior 2"
                )
            )
        )

        val newHabits = listOf(
            createHabit(
                remoteId = "new-1",
                title = "Nuevo habito 1"
            ),
            createHabit(
                remoteId = "new-2",
                title = "Nuevo habito 2"
            )
        )

        dao.replaceAllHabits(newHabits)

        val result = dao.getAllHabits().first()

        assertEquals(2, result.size)
        assertTrue(result.none { it.remoteId == "old-1" })
        assertTrue(result.none { it.remoteId == "old-2" })
        assertTrue(result.any { it.remoteId == "new-1" })
        assertTrue(result.any { it.remoteId == "new-2" })
    }

    @Test
    fun clearAllHabits_removesAllHabits() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(remoteId = "habit-1"),
                createHabit(remoteId = "habit-2")
            )
        )

        dao.clearAllHabits()

        val result = dao.getAllHabits().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun updateHabit_updatesExistingHabit() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(
                    remoteId = "habit-1",
                    title = "Tomar agua",
                    streak = 5,
                    completedToday = false
                )
            )
        )

        val inserted = dao.getHabitByRemoteId("habit-1")

        assertNotNull(inserted)

        val updated = inserted.copy(
            title = "Tomar mucha agua",
            streak = 10,
            completedToday = true
        )

        dao.updateHabit(updated)

        val result = dao.getHabitByRemoteId("habit-1")

        assertNotNull(result)
        assertEquals("Tomar mucha agua", result.title)
        assertEquals(10, result.streak)
        assertTrue(result.completedToday)
    }

    @Test
    fun deleteHabitByRemoteId_removesHabit() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(remoteId = "habit-1"),
                createHabit(remoteId = "habit-2")
            )
        )

        dao.deleteHabitByRemoteId("habit-1")

        val deletedHabit = dao.getHabitByRemoteId("habit-1")
        val remainingHabits = dao.getAllHabits().first()

        assertNull(deletedHabit)
        assertEquals(1, remainingHabits.size)
        assertEquals("habit-2", remainingHabits[0].remoteId)
    }

    @Test
    fun getHabitByRemoteId_returnsCorrectHabit() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(
                    remoteId = "habit-1",
                    title = "Tomar agua"
                ),
                createHabit(
                    remoteId = "habit-2",
                    title = "Entrenar"
                )
            )
        )

        val result = dao.getHabitByRemoteId("habit-2")

        assertNotNull(result)
        assertEquals("habit-2", result.remoteId)
        assertEquals("Entrenar", result.title)
    }

    @Test
    fun getHabitByRemoteId_returnsNullWhenNotFound() = runTest {
        val result = dao.getHabitByRemoteId("does-not-exist")

        assertNull(result)
    }

    @Test
    fun getRecentHabits_returnsMaximumTwoHabits() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(
                    remoteId = "habit-1",
                    title = "Habito 1",
                    completedToday = false
                ),
                createHabit(
                    remoteId = "habit-2",
                    title = "Habito 2",
                    completedToday = false
                ),
                createHabit(
                    remoteId = "habit-3",
                    title = "Habito 3",
                    completedToday = true
                )
            )
        )

        val result = dao.getRecentHabits().first()

        assertEquals(2, result.size)
    }

    @Test
    fun getRecentHabits_returnsIncompleteHabitsFirst() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(
                    remoteId = "habit-1",
                    title = "Completado",
                    completedToday = true
                ),
                createHabit(
                    remoteId = "habit-2",
                    title = "Pendiente 1",
                    completedToday = false
                ),
                createHabit(
                    remoteId = "habit-3",
                    title = "Pendiente 2",
                    completedToday = false
                )
            )
        )

        val result = dao.getRecentHabits().first()

        assertEquals(2, result.size)
        assertFalse(result[0].completedToday)
        assertFalse(result[1].completedToday)
    }

    @Test
    fun getRecentHabits_returnsCompletedHabitWhenThereAreNotEnoughIncompleteHabits() = runTest {
        dao.insertHabits(
            listOf(
                createHabit(
                    remoteId = "habit-1",
                    completedToday = false
                ),
                createHabit(
                    remoteId = "habit-2",
                    completedToday = true
                )
            )
        )

        val result = dao.getRecentHabits().first()

        assertEquals(2, result.size)
        assertFalse(result[0].completedToday)
        assertTrue(result[1].completedToday)
    }

    @Test
    fun insertHabits_withEmptyList_keepsDatabaseEmpty() = runTest {
        dao.insertHabits(emptyList())

        val result = dao.getAllHabits().first()

        assertTrue(result.isEmpty())
    }
}