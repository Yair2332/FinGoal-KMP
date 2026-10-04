package com.fingoal.app.ui.screens.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fingoal.app.ui.screens.transactions.getIconForCategory
import com.fingoal.app.ui.screens.transactions.toAmountString

data class ExpenseCategorySummary(
    val category: String,
    val amount: Double,
    val percentage: Float
)

@Composable
fun ExpenseCategorySummary(
    categories: List<ExpenseCategorySummary>,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.outlineVariant)
            .padding(18.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Column {
                Text(
                    text = "¿En qué estoy gastando?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Tus principales categorías",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 170.dp)
                .verticalScroll(rememberScrollState())
        ) {

            categories.forEachIndexed { index, item ->

                ExpenseCategoryRow(
                    category = item.category,
                    amount = item.amount,
                    percentage = item.percentage
                )

                if (index != categories.lastIndex) {
                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseCategoryRow(
    category: String,
    amount: Double,
    percentage: Float
) {

    val icon = getIconForCategory(category)

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.background
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$${amount.toAmountString()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(10.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.background
        )
    }
}