package com.fingoal.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.fingoal.app.data.local.database.getDatabaseBuilder
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

actual val targetModule: Module = module {
    single { getDatabaseBuilder() }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                "${NSHomeDirectory()}/Library/Preferences/user_preferences.preferences_pb".toPath()

            }
        )
    }
}