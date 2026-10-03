package com.fingoal.app.ui.screens.habits.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fingoal.app.domain.model.Habit

@Composable
fun HabitItem(
    habit: Habit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    navigateToHabits: (() -> Unit)? = null,
    isExpandable: Boolean = true
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val frequencyData = when (habit.frequency.uppercase()) {
        "DIARIO" -> Triple(
            Color(0xFF81C784),
            Icons.Default.Book,
            "DIARIO"
        )

        "SEMANAL" -> Triple(
            Color(0xFF64B5F6),
            Icons.Default.Tour,
            "SEMANAL"
        )

        else -> Triple(
            Color(0xFFBA68C8),
            Icons.Default.CalendarToday,
            "MENSUAL"
        )
    }

    val goal = when (habit.frequency.uppercase()) {
        "SEMANAL" -> 7
        "MENSUAL" -> 30
        else -> 1
    }

    val progress = if (habit.frequency.uppercase() == "DIARIO") {
        if (habit.completedToday) 1f else 0f
    } else {
        habit.streak
            .toFloat()
            .coerceAtMost(goal.toFloat()) / goal.toFloat()
    }

    val percent = (progress * 100).toInt()

    val isCompleted = if (habit.frequency.uppercase() == "DIARIO") {
        habit.completedToday
    } else {
        habit.streak >= goal
    }

    val statusColor =
        if (habit.completedToday) {
            Color(0xFFF57C00)
        } else {
            MaterialTheme.colorScheme.outline
        }

    val displayStreakText =
        if (isCompleted) {
            "Completado"
        } else {
            "Racha: ${habit.streak} días"
        }

    val activeColor =
        if (isCompleted) {
            Color(0xFF249F29)
        } else {
            statusColor
        }

    val textColor = MaterialTheme.colorScheme.onSurface
    val expenseColor = Color(0xFFE53935)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp,
                horizontal = if (navigateToHabits == null) 8.dp else 0.dp
            )
            .animateContentSize(),
        onClick = {
            if (navigateToHabits != null) {
                navigateToHabits.invoke()
            } else if (isExpandable) {
                expanded = !expanded
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            0.5.dp,
            Color.LightGray.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(frequencyData.first)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = frequencyData.second,
                        contentDescription = null,
                        tint = frequencyData.first,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Row {
                            Text(
                                text = "${frequencyData.third} | ",
                                style = MaterialTheme.typography.bodySmall,
                                color = frequencyData.first
                            )

                            Text(
                                text = displayStreakText,
                                style = MaterialTheme.typography.bodySmall,
                                color = activeColor
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RACHA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )

                        IconButton(
                            onClick = {
                                if (!habit.completedToday) {
                                    onToggle()
                                }
                            },
                            modifier = Modifier.size(32.dp),
                            enabled = !isCompleted || !habit.completedToday
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = activeColor
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(CircleShape),
                        color = frequencyData.first,
                        trackColor = frequencyData.first.copy(alpha = 0.2f)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                if (isExpandable) {
                    AnimatedVisibility(
                        visible = expanded
                    ) {
                        Column {
                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Text(
                                text = habit.description.ifBlank {
                                    "Sin descripción"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = onEdit
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = onDelete
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = expenseColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}