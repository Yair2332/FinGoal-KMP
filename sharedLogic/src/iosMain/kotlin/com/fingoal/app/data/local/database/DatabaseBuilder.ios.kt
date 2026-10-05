package com.fingoal.app.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {

    val documentsDirectory =
        NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).first() as String

    val dbFilePath =
        "$documentsDirectory/fingoal.db"

    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
}