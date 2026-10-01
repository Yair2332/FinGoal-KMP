package com.fingoal.app.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.fingoal.app.data.local.dao.GoalDao
import com.fingoal.app.data.local.dao.HabitDao
import com.fingoal.app.data.local.dao.TransactionDao
import com.fingoal.app.data.local.entities.GoalEntity
import com.fingoal.app.data.local.entities.HabitEntity
import com.fingoal.app.data.local.entities.TransactionEntity

@Database(
    entities = [
        GoalEntity::class,
        HabitEntity::class,
        TransactionEntity::class
    ],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao

    abstract fun habitDao(): HabitDao

    abstract fun transactionDao(): TransactionDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}