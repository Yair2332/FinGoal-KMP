package com.fingoal.app.presentation.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fingoal.app.presentation.components.ConfirmationDialog
import com.fingoal.app.presentation.components.EmptyStateComponent
import com.fingoal.app.presentation.screens.transactions.components.TransactionFormContent
import com.fingoal.app.presentation.screens.transactions.components.TransactionItem
import com.fingoal.app.presentation.screens.transactions.components.TransactionListHeader
import com.fingoal.app.presentation.transactions.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(viewModel: TransactionViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Usamos el modelo de dominio 'Transaction' para agrupar
    val transaccionesPorFecha = uiState.transactions.groupBy { it.date.toFormattedDate() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val totalIngresos = uiState.transactions.filter { it.isIncome }.sumOf { it.amount }
                val totalGastos = uiState.transactions.filter { !it.isIncome }.sumOf { it.amount }
                TransactionListHeader(ingresos = totalIngresos, gastos = totalGastos)
            }

            if (uiState.transactions.isEmpty() && !uiState.isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyStateComponent(message = "¡No tienes transacciones aún!")
                    }
                }
            } else {
                transaccionesPorFecha.forEach { (fechaLocal, lista) ->
                    item {
                        Text(
                            text = fechaLocal,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 1.dp))
                    }
                    items(items = lista, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onEdit = { viewModel.showEditSheet(transaction) },
                            onDelete = { viewModel.showDeleteDialog(transaction) }
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Diálogo de confirmación
        if (uiState.transactionToDelete != null) {
            ConfirmationDialog(
                onDismiss = { viewModel.hideDeleteDialog() },
                onConfirm = { viewModel.deleteTransaction() },
                title = "¿Eliminar transacción?",
                text = "¿Seguro que quieres eliminar '${uiState.transactionToDelete?.title}'?",
                confirmButtonText = "Eliminar"
            )
        }
    }

    // Bottom Sheet
    if (uiState.showBottomSheet) {
        val editing = uiState.editingTransaction
        ModalBottomSheet(onDismissRequest = { viewModel.hideSheet() }) {
            TransactionFormContent(
                initialTransaction = editing,
                onSave = { title, amount, category, isIncome, description ->
                    if (editing == null) {
                        viewModel.insertTransaction(title, amount, category, isIncome, description)
                    } else {
                        viewModel.editTransaction(editing.id, editing.remoteId, title, amount, category, isIncome, description)
                    }
                }
            )
        }
    }
}