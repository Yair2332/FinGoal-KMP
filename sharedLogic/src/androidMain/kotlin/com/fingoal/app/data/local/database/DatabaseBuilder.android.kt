package com.fingoal.app.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(
    context: Context
): RoomDatabase.Builder<AppDatabase> {

    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("fingoal.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}