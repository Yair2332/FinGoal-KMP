package com.fingoal.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fingoal.app.data.local.entities.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Query("DELETE FROM habits")
    suspend fun clearAllHabits()


    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE remoteId = :remoteId")
    suspend fun deleteHabitByRemoteId(remoteId: String)

    @Query("""
    SELECT * FROM habits 
    ORDER BY completedToday ASC 
    LIMIT 2
""")
    fun getRecentHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getHabitByRemoteId(remoteId: String): HabitEntity?




}