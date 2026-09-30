package com.fingoal.app.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {

    val dbFilePath =
        NSHomeDirectory() + "/fingoal.db"

    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
}