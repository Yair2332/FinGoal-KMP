package com.fingoal.app.ui.screens.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GoalSummary(
    val totalGoals: Int,
    val totalTarget: Double,
    val averageTarget: Double
)

@Composable
fun GoalSummaryCard(
    summary: GoalSummary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding( 8.dp,0.dp,8.dp,8.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "Resumen de metas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = if (summary.totalGoals == 1) {
                    "Tienes 1 meta activa"
                } else {
                    "Tienes ${summary.totalGoals} metas totales"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                GoalSummaryItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Flag,
                    label = "Metas",
                    value = summary.totalGoals.toString()
                )

                GoalSummaryItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "Objetivo total",
                    value = "$${formatAmount(summary.totalTarget)}"
                )

                GoalSummaryItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TrendingUp,
                    label = "Promedio",
                    value = "$${formatAmount(summary.averageTarget)}"
                )
            }
        }
    }
}

@Composable
private fun GoalSummaryItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                vertical = 12.dp,
                horizontal = 6.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatAmount(amount: Double): String {
    val rounded = kotlin.math.round(amount * 100) / 100

    return if (rounded % 1.0 == 0.0) {
        rounded.toLong().toString()
    } else {
        rounded.toString()
    }
}