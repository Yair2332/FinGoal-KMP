package com.fingoal.app.domain.repository

import com.fingoal.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
    suspend fun syncTransactions()
    suspend fun addTransaction(title: String, amount: Double, category: String, isIncome: Boolean, description: String)
    suspend fun deleteTransaction(localId: Long, remoteId: String)
    suspend fun updateTransaction(localId: Long, remoteId: String, title: String, amount: Double, category: String, isIncome: Boolean, description: String)
}