package com.fingoal.app.di

import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.data.local.database.AppDatabase
import com.fingoal.app.data.local.database.getRoomDatabase
import com.fingoal.app.data.remote.AuthApiService
import com.fingoal.app.data.remote.GoalApiService
import com.fingoal.app.data.remote.HabitApiService
import com.fingoal.app.data.remote.TransactionApiService
import com.fingoal.app.data.remote.createHttpClient
import com.fingoal.app.data.repository.AuthRepositoryImpl
import com.fingoal.app.data.repository.GoalRepositoryImpl
import com.fingoal.app.data.repository.HabitRepositoryImpl
import com.fingoal.app.data.repository.TransactionRepositoryImpl
import com.fingoal.app.domain.repository.AuthRepository
import com.fingoal.app.domain.repository.GoalRepository
import com.fingoal.app.domain.repository.HabitRepository
import com.fingoal.app.domain.repository.TransactionRepository
import org.koin.dsl.module

// Reemplaza a NetworkModule
val networkModule = module {
    single { createHttpClient() }
    single { AuthApiService(get()) }
    single { GoalApiService(get()) }
    single { HabitApiService(get()) }
    single { TransactionApiService(get()) }
}

// Reemplaza a DatabaseModule
val databaseModule = module {
    single { getRoomDatabase(get()) }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().goalDao() }
    single { get<AppDatabase>().habitDao() }
}

// Reemplaza a StorageModule
val storageModule = module {
    single { UserPreferences(get()) }
}

// Reemplaza a RepositoryModule
val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get(), get()) }
    single<GoalRepository> { GoalRepositoryImpl(get(), get(), get()) }
    single<HabitRepository> { HabitRepositoryImpl(get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}

// Módulo maestro que exporta todas las dependencias compartidas
val appModule = module {
    includes(networkModule, databaseModule, storageModule, repositoryModule)
}