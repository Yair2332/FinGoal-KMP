package com.fingoal.app.ui.screens.transactions

import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.ui.components.AssistantQuestion

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),

    val isLoading: Boolean = false,

    val errorMessage: String? = null,

    val showBottomSheet: Boolean = false,

    val editingTransaction: Transaction? = null,

    val transactionToDelete: Transaction? = null,

    val assistantQuestions: List<AssistantQuestion> = emptyList()
)