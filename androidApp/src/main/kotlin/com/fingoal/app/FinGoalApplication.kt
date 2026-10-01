package com.fingoal.app

import android.app.Application
import com.fingoal.app.di.initKoin
import org.koin.android.ext.koin.androidContext

class FinGoalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializa Koin pasando el contexto de Android
        initKoin {
            androidContext(this@FinGoalApplication)
        }
    }
}