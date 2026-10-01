package com.fingoal.app.domain.usecase.transactions

import com.fingoal.app.data.local.entities.TransactionEntity
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getTransactions()
    }
}