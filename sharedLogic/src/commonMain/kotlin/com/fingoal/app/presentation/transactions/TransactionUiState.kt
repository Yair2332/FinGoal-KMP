package com.fingoal.app.presentation.transactions

import com.fingoal.app.domain.model.Transaction

data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null,
    val showBottomSheet: Boolean = false,
    val editingTransaction: Transaction? = null,
    val transactionToDelete: Transaction? = null
)