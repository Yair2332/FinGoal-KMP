package com.fingoal.app.ui.screens.goals.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ContributionDialog(
    currentAmount: Double,
    targetAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, isAdding: Boolean) -> Unit
) {
    var amount by remember {
        mutableStateOf("")
    }

    val valAmount = amount.toDoubleOrNull() ?: 0.0

    val isPositive = valAmount > 0
    val excedeMeta = (currentAmount + valAmount) > targetAmount
    val excedeSaldo = valAmount > currentAmount

    val showError = amount.isNotEmpty() &&
            (!isPositive || excedeMeta)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Gestionar ahorros",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (
                            it.isEmpty() ||
                            it.matches(Regex("^\\d*\\.?\\d{0,2}$"))
                        ) {
                            amount = it
                        }
                    },
                    label = {
                        Text("Monto ($)")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = showError
                )

                if (showError) {
                    Text(
                        text = if (!isPositive) {
                            "El monto debe ser mayor a 0"
                        } else {
                            "Superas el límite de la meta"
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Restan: $${(targetAmount - currentAmount).toInt()}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        confirmButton = {
            Row {
                Button(
                    onClick = {
                        onConfirm(valAmount, true)
                    },
                    enabled = isPositive && !excedeMeta
                ) {
                    Text("Aportar")
                }

                Spacer(
                    Modifier.width(8.dp)
                )

                OutlinedButton(
                    onClick = {
                        onConfirm(valAmount, false)
                    },
                    enabled = isPositive && !excedeSaldo
                ) {
                    Text("Retirar")
                }
            }
        }
    )
}