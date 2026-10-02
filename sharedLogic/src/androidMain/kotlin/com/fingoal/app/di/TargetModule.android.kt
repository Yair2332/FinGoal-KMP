package com.fingoal.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.fingoal.app.data.local.database.getDatabaseBuilder
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule: Module = module {

    single {
        getDatabaseBuilder(get())
    }

    single<DataStore<Preferences>> {
        val context = get<Context>().applicationContext

        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                "${context.filesDir.absolutePath}/user_preferences.preferences_pb"
                    .toPath()
            }
        )
    }
}