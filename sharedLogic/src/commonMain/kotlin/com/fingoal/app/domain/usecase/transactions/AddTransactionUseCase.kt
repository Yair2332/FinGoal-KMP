package com.fingoal.app.domain.usecase.transactions

import com.fingoal.app.domain.repository.TransactionRepository

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String = ""
    ) {
        repository.addTransaction(title, amount, category, isIncome, description)
    }
}