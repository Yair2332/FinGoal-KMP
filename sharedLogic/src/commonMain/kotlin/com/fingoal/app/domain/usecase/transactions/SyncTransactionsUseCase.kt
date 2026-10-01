package com.fingoal.app.domain.usecase.transactions

import com.fingoal.app.domain.repository.TransactionRepository

class SyncTransactionsUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke() {
        repository.syncTransactions()
    }
}