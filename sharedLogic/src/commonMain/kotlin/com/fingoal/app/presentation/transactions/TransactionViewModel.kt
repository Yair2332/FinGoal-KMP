package com.fingoal.app.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.usecase.transactions.AddTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.DeleteTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.GetTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.SyncTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.UpdateTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val syncTransactionsUseCase: SyncTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
        syncWithServer()
    }

    fun showAddSheet() = _uiState.update { it.copy(showBottomSheet = true, editingTransaction = null) }

    fun showEditSheet(transaction: Transaction) = _uiState.update {
        it.copy(showBottomSheet = true, editingTransaction = transaction)
    }

    fun hideSheet() = _uiState.update { it.copy(showBottomSheet = false, editingTransaction = null) }

    fun showDeleteDialog(transaction: Transaction) = _uiState.update { it.copy(transactionToDelete = transaction) }

    fun hideDeleteDialog() = _uiState.update { it.copy(transactionToDelete = null) }

    private fun loadTransactions() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getTransactionsUseCase()
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, transactions = list) }
                }
        }
    }

    private fun syncWithServer() {
        viewModelScope.launch {
            syncTransactionsUseCase()
        }
    }

    fun insertTransaction(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                addTransactionUseCase(title, amount, category, isIncome, description)
                hideSheet()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteTransaction() {
        val transaction = _uiState.value.transactionToDelete ?: return
        viewModelScope.launch {
            try {
                deleteTransactionUseCase(transaction.id, transaction.remoteId)
                hideDeleteDialog()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al borrar: ${e.message}") }
            }
        }
    }

    fun editTransaction(
        localId: Long,
        remoteId: String,
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                updateTransactionUseCase(localId, remoteId, title, amount, category, isIncome, description)
                hideSheet()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al editar: ${e.message}") }
            }
        }
    }
}