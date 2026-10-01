package com.fingoal.app.domain.usecase.transactions

import com.fingoal.app.domain.repository.TransactionRepository

class UpdateTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        localId: Long, remoteId: String, title: String, amount: Double, category: String, isIncome: Boolean, description: String
    ) {
        repository.updateTransaction(localId, remoteId, title, amount, category, isIncome, description)
    }
}