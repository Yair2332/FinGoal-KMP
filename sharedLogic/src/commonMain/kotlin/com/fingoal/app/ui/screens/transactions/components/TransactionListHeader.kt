package com.fingoal.app.ui.screens.transactions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TransactionListHeader(
    ingresos: Double,
    gastos: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PeriodoActual()

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ResumenFinanciero(
            ingresos = ingresos,
            gastos = gastos
        )
    }
}