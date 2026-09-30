package com.fingoal.app.data.local.dao

import androidx.room.*
import com.fingoal.app.data.local.entities.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals")
    suspend fun clearAllGoals()

    @Query("SELECT * FROM goals WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getGoalByRemoteId(remoteId: String): GoalEntity?

    @Query("""
    SELECT * FROM goals 
    WHERE currentAmount < targetAmount 
    AND targetAmount > 0 
    ORDER BY (currentAmount * 1.0 / targetAmount) DESC 
    LIMIT 1
""")
    fun getTopGoal(): Flow<GoalEntity?>

    @Query("SELECT SUM(currentAmount) FROM goals WHERE createdAt >= :startOfMonth")
    fun getSavingsForCurrentMonth(startOfMonth: Long): Flow<Double?>

    @Query("DELETE FROM goals WHERE remoteId = :remoteId")
    suspend fun deleteByRemoteId(remoteId: String)

}