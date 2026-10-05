package com.fingoal.app.ui.screens.goals.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fingoal.app.domain.model.Goal
import fingoal.sharedlogic.generated.resources.Res
import fingoal.sharedlogic.generated.resources.metas
import org.jetbrains.compose.resources.painterResource

@Composable
fun GoalItem(
    goal: Goal,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onContribution: (Double, Boolean) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    // Derived values calculated only when goal changes
    val progress = remember(
        goal.currentAmount,
        goal.targetAmount
    ) {
        if (goal.targetAmount > 0) {
            (goal.currentAmount / goal.targetAmount)
                .toFloat()
                .coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "GoalProgressAnimation"
    )

    val isCompleted = remember(
        goal.currentAmount,
        goal.targetAmount
    ) {
        goal.currentAmount >= goal.targetAmount
    }

    val isDarkTheme = isSystemInDarkTheme()

    val successColor = remember(isDarkTheme) {
        if (isDarkTheme) {
            Color(0xFF81C784)
        } else {
            Color(0xFF388E3C)
        }
    }

    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val deleteColor = Color(0xFFE53935)

    if (showDialog) {
        ContributionDialog(
            currentAmount = goal.currentAmount,
            targetAmount = goal.targetAmount,
            onDismiss = {
                showDialog = false
            },
            onConfirm = { amount, isAdding ->
                onContribution(amount, isAdding)
                showDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .animateContentSize()
            .clickable {
                expanded = !expanded
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Goal Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {

                // Si no hay URL, muestra la imagen local metas.webp.
                // Si hay URL, usa Coil para cargarla.
                if (goal.localImagePath.isNullOrBlank()) {
                    Image(
                        painter = painterResource(Res.drawable.metas),
                        contentDescription = goal.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = goal.localImagePath,
                        contentDescription = goal.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                successColor.copy(alpha = 0.6f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "COMPLETADO",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.85f
                    )
                ) {
                    Text(
                        text = "$${goal.targetAmount.toInt()}",
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }

            // Goal Details Container
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Text(
                            text = "Meta: ${goal.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = subTextColor
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "$${goal.currentAmount.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isCompleted) {
                                successColor
                            } else {
                                primaryColor
                            },
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Ahorrado",
                            style = MaterialTheme.typography.bodySmall,
                            color = subTextColor
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(
                            RoundedCornerShape(4.dp)
                        ),
                    color = if (isCompleted) {
                        successColor
                    } else {
                        primaryColor
                    },
                    trackColor = primaryColor.copy(
                        alpha = 0.3f
                    )
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isCompleted) {
                            "¡Meta Completada!"
                        } else {
                            "${(progress * 100).toInt()}% completado"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCompleted) {
                            successColor
                        } else {
                            subTextColor
                        }
                    )

                    if (!isCompleted) {
                        Text(
                            text = "Faltan $${(goal.targetAmount - goal.currentAmount).toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                // Smoothly animated expanding action panel
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.3f
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {

                            TextButton(
                                onClick = onEdit
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar meta",
                                    tint = primaryColor
                                )

                                Text(
                                    text = "Editar",
                                    color = primaryColor
                                )
                            }

                            TextButton(
                                onClick = {
                                    showDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Aportar a la meta",
                                    tint = primaryColor
                                )

                                Text(
                                    text = "Aportar",
                                    color = primaryColor
                                )
                            }

                            TextButton(
                                onClick = onDelete
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Borrar meta",
                                    tint = deleteColor
                                )

                                Text(
                                    text = "Borrar",
                                    color = deleteColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}