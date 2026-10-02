package com.fingoal.app.presentation.screens.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResumenFinanciero(ingresos: Double, gastos: Double) {

    val incomeColor = Color(0xFF4CAF50)
    val expenseColor = Color(0xFFE53935)

    // Colores de fondo de las tarjetas
    val incomeBg = incomeColor.copy(alpha = 0.15f)
    val expenseBg = expenseColor.copy(alpha = 0.15f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta Ingresos
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = incomeBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = incomeColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ingresos", color = incomeColor, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "$${String.format("%.2f", ingresos)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = incomeColor
                )
            }
        }

        // Tarjeta Gastos
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = expenseBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = expenseColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gastos", color = expenseColor, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "$${String.format("%.2f", gastos)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = expenseColor
                )
            }
        }
    }
}