package com.fingoal.app.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(appDeclaration: org.koin.dsl.KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            networkModule,
            databaseModule,
            storageModule,
            repositoryModule,
            useCaseModule,
            targetModule
        )
    }
}