package com.fingoal.app.ui.screens.habits.components

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HabitSummary(
    val total: Int,
    val daily: Int,
    val weekly: Int,
    val monthly: Int
)

@Composable
fun HabitSummaryCard(
    summary: HabitSummary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding( 8.dp,0.dp,8.dp,8.dp)
            .shadow(
            elevation = 2.dp,
            shape = RoundedCornerShape(20.dp)
        ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.outlineVariant

    ) {
        Column(
            modifier = Modifier.padding(18.dp)

        ) {
            Text(
                text = "Resumen de hábitos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = if (summary.total == 1) {
                    "Tienes 1 hábito registrado"
                } else {
                    "Tienes ${summary.total} hábitos registrados"
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
                HabitSummaryItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Today,
                    label = "Diarios",
                    value = summary.daily
                )

                HabitSummaryItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.EventRepeat,
                    label = "Semanales",
                    value = summary.weekly
                )

                HabitSummaryItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CalendarMonth,
                    label = "Mensuales",
                    value = summary.monthly
                )
            }
        }
    }
}

@Composable
private fun HabitSummaryItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: Int
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                vertical = 12.dp,
                horizontal = 8.dp
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
            text = value.toString(),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}