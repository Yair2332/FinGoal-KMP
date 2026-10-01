package com.fingoal.app.domain.usecase.transactions

import com.fingoal.app.domain.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(localId: Long, remoteId: String) {
        repository.deleteTransaction(localId, remoteId)
    }
}