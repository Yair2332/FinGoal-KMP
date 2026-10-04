package com.fingoal.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.usecase.transactions.AddTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.DeleteTransactionUseCase
import com.fingoal.app.domain.usecase.transactions.GetTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.SyncTransactionsUseCase
import com.fingoal.app.domain.usecase.transactions.UpdateTransactionUseCase
import com.fingoal.app.ui.components.AssistantQuestion
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

    private val _uiState = MutableStateFlow(
        TransactionUiState()
    )

    val uiState: StateFlow<TransactionUiState> =
        _uiState.asStateFlow()

    init {
        loadTransactions()
        syncWithServer()
    }

    /*
     * ============================================================
     * BOTTOM SHEET
     * ============================================================
     */

    fun showAddSheet() {
        _uiState.update {
            it.copy(
                showBottomSheet = true,
                editingTransaction = null
            )
        }
    }

    fun showEditSheet(
        transaction: Transaction
    ) {
        _uiState.update {
            it.copy(
                showBottomSheet = true,
                editingTransaction = transaction
            )
        }
    }

    fun hideSheet() {
        _uiState.update {
            it.copy(
                showBottomSheet = false,
                editingTransaction = null
            )
        }
    }

    /*
     * ============================================================
     * DELETE
     * ============================================================
     */

    fun showDeleteDialog(
        transaction: Transaction
    ) {
        _uiState.update {
            it.copy(
                transactionToDelete = transaction
            )
        }
    }

    fun hideDeleteDialog() {
        _uiState.update {
            it.copy(
                transactionToDelete = null
            )
        }
    }

    /*
     * ============================================================
     * CARGAR TRANSACCIONES
     * ============================================================
     */

    private fun loadTransactions() {

        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        viewModelScope.launch {

            getTransactionsUseCase()
                .catch { error ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
                .collect { list ->

                    /*
                     * Acá estaba el problema.
                     *
                     * Cada vez que llegan las transacciones,
                     * también construimos las preguntas del
                     * asistente.
                     */

                    val assistantQuestions =
                        buildAssistantQuestions(list)

                    _uiState.update {

                        it.copy(
                            isLoading = false,
                            transactions = list,
                            assistantQuestions = assistantQuestions
                        )
                    }
                }
        }
    }

    /*
     * ============================================================
     * SINCRONIZACIÓN
     * ============================================================
     */

    private fun syncWithServer() {

        viewModelScope.launch {
            syncTransactionsUseCase()
        }
    }

    /*
     * ============================================================
     * INSERTAR
     * ============================================================
     */

    fun insertTransaction(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            try {

                addTransactionUseCase(
                    title,
                    amount,
                    category,
                    isIncome,
                    description
                )

                hideSheet()

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /*
     * ============================================================
     * ELIMINAR
     * ============================================================
     */

    fun deleteTransaction() {

        val transaction =
            _uiState.value.transactionToDelete
                ?: return

        viewModelScope.launch {

            try {

                deleteTransactionUseCase(
                    transaction.id,
                    transaction.remoteId
                )

                hideDeleteDialog()

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        errorMessage =
                            "Error al borrar: ${e.message}"
                    )
                }
            }
        }
    }

    /*
     * ============================================================
     * EDITAR
     * ============================================================
     */

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

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            try {

                updateTransactionUseCase(
                    localId = localId,
                    remoteId = remoteId,
                    title = title,
                    amount = amount,
                    category = category,
                    isIncome = isIncome,
                    description = description
                )

                hideSheet()

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "Error al editar: ${e.message}"
                    )
                }
            }
        }
    }

    /*
     * ============================================================
     * PREGUNTAS DEL ASISTENTE
     * ============================================================
     */

    private fun buildAssistantQuestions(
        transactions: List<Transaction>
    ): List<AssistantQuestion> {

        val totalIngresos = transactions
            .filter {
                it.isIncome
            }
            .sumOf {
                it.amount
            }

        val totalGastos = transactions
            .filter {
                !it.isIncome
            }
            .sumOf {
                it.amount
            }

        val cantidadTotal =
            transactions.size

        val cantidadIngresos =
            transactions.count {
                it.isIncome
            }

        val cantidadGastos =
            transactions.count {
                !it.isIncome
            }

        return listOf(

            AssistantQuestion(
                question = "¿Cuánto dinero ingresé?",
                answer =
                    "En total ingresaste $${formatAmount(totalIngresos)}."
            ),

            AssistantQuestion(
                question = "¿Cuánto gasté?",
                answer =
                    "En total gastaste $${formatAmount(totalGastos)}."
            ),

            AssistantQuestion(
                question = "¿Cuántas transacciones tengo?",
                answer =
                    "Tienes $cantidadTotal transacciones registradas."
            ),

            AssistantQuestion(
                question = "¿Cuántos ingresos tengo?",
                answer =
                    "Tienes $cantidadIngresos ingresos registrados."
            ),

            AssistantQuestion(
                question = "¿Cuántos gastos tengo?",
                answer =
                    "Tienes $cantidadGastos gastos registrados."
            )
        )
    }

    /*
     * ============================================================
     * FORMATO DE DINERO
     * ============================================================
     */

    private fun formatAmount(
        amount: Double
    ): String {
        return amount.toString()
    }
}