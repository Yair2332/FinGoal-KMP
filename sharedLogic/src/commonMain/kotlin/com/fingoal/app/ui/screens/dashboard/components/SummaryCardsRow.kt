package com.fingoal.app.ui.screens.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Savings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SummaryCardsRow(
    totalExpenses: Double,
    savingsTotal: Double
) {
    val expenseTint = Color(0xFFD32F2F)
    val savingsTint = Color(0xFF388E3C)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoCard(
            title = "Gastos Mes",
            value = "$${totalExpenses.toCurrencyString()}",
            icon = Icons.Default.AccountBalanceWallet,
            iconTint = expenseTint,
            modifier = Modifier.weight(1f)
        )

        InfoCard(
            title = "Ahorro Total",
            value = "$${savingsTotal.toCurrencyString()}",
            icon = Icons.Default.Savings,
            iconTint = savingsTint,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun Double.toCurrencyString(): String {
    return kotlin.math.round(this).toLong().toString()
}