package com.fingoal.app.ui.screens.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BalanceChart(
    chartData: List<Float>,
    modifier: Modifier = Modifier
) {
    if (chartData.isEmpty()) return

    val data = remember(chartData) {
        listOf(0f) + chartData
    }

    val minVal = data.minOrNull() ?: 0f
    val maxVal = data.maxOrNull() ?: 1f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    fun formatCurrency(value: Float): String {
        return if (value >= 1000f) {
            "${value.toInt() / 1000}k"
        } else {
            value.toInt().toString()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .width(35.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatCurrency(maxVal),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )

            Text(
                text = formatCurrency((maxVal + minVal) / 2),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )

            Text(
                text = formatCurrency(minVal),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )
        }

        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 4.dp)
        ) {
            val stepX = size.width / (data.size - 1)

            val gridColor = Color.LightGray.copy(alpha = 0.2f)

            for (i in 0..2) {
                val y = i * (size.height / 2)

                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            for (i in 0 until data.size - 1) {
                val startX = i * stepX

                val startY =
                    size.height -
                            ((data[i] - minVal) / range * size.height)

                val endX = (i + 1) * stepX

                val endY =
                    size.height -
                            ((data[i + 1] - minVal) / range * size.height)

                val lineColor =
                    if (data[i + 1] >= data[i]) {
                        Color(0xFF388E3C)
                    } else {
                        Color(0xFFD32F2F)
                    }

                drawLine(
                    color = lineColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}