package com.fingoal.app.di

import com.fingoal.app.data.local.database.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule: Module = module {
    single { getDatabaseBuilder() }
}