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
import com.fingoal.app.domain.usecase.auth.LoginUseCase
import com.fingoal.app.domain.usecase.auth.RegisterUseCase
import com.fingoal.app.domain.usecase.dashboard.GetDashboardDataUseCase
import com.fingoal.app.domain.usecase.goals.AddGoalContributionUseCase
import com.fingoal.app.domain.usecase.goals.AddGoalUseCase
import com.fingoal.app.domain.usecase.goals.DeleteGoalUseCase
import com.fingoal.app.domain.usecase.goals.GetGoalsUseCase
import com.fingoal.app.domain.usecase.goals.SyncGoalsUseCase
import com.fingoal.app.domain.usecase.goals.UpdateGoalUseCase
import com.fingoal.app.domain.usecase.goals.WithdrawGoalUseCase
import com.fingoal.app.domain.usecase.habits.CreateHabitUseCase
import com.fingoal.app.domain.usecase.habits.DeleteHabitUseCase
import com.fingoal.app.domain.usecase.habits.GetHabitsUseCase
import com.fingoal.app.domain.usecase.habits.SyncHabitsUseCase
import com.fingoal.app.domain.usecase.habits.ToggleHabitUseCase
import com.fingoal.app.domain.usecase.habits.UpdateHabitUseCase
import com.fingoal.app.domain.usecase.transactions.AddTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.DeleteTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.GetTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.SyncTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.UpdateTransactionUseCase
import org.koin.core.module.Module
import org.koin.dsl.module
import com.fingoal.app.ui.screens.auth.AuthViewModel
import com.fingoal.app.ui.screens.dashboard.DashboardViewModel
import com.fingoal.app.ui.screens.goals.GoalViewModel
import com.fingoal.app.ui.screens.habits.HabitViewModel
import com.fingoal.app.ui.screens.transactions.TransactionViewModel


val viewModelModule = module {
    factory { AuthViewModel(get(), get(), get()) }
    factory { DashboardViewModel(get()) }
    factory { GoalViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { HabitViewModel(get(), get(), get(), get(), get(), get()) }
    factory { TransactionViewModel(get(), get(), get(), get(), get()) }
}

val networkModule = module {
    single { createHttpClient() }
    single { AuthApiService(get()) }
    single { GoalApiService(get()) }
    single { HabitApiService(get()) }
    single { TransactionApiService(get()) }
}

expect val targetModule: Module

val databaseModule = module {
    single { getRoomDatabase(get()) }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().goalDao() }
    single { get<AppDatabase>().habitDao() }
}

val storageModule = module {
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get(), get()) }
    single<GoalRepository> { GoalRepositoryImpl(get(), get(), get()) }
    single<HabitRepository> { HabitRepositoryImpl(get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}

val useCaseModule = module {
    // Auth
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }

    // Dashboard
    factory { GetDashboardDataUseCase(get(), get(), get()) }

    // Goals
    factory { AddGoalContributionUseCase(get()) }
    factory { AddGoalUseCase(get()) }
    factory { DeleteGoalUseCase(get()) }
    factory { GetGoalsUseCase(get()) }
    factory { SyncGoalsUseCase(get()) }
    factory { UpdateGoalUseCase(get()) }
    factory { WithdrawGoalUseCase(get()) }

    // Habits
    factory { CreateHabitUseCase(get()) }
    factory { DeleteHabitUseCase(get()) }
    factory { GetHabitsUseCase(get()) }
    factory { SyncHabitsUseCase(get()) }
    factory { ToggleHabitUseCase(get()) }
    factory { UpdateHabitUseCase(get()) }

    // Transactions
    factory { AddTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }
    factory { SyncTransactionsUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
}

val appModule = module {
    includes(
        networkModule,
        targetModule,
        databaseModule,
        storageModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )
}