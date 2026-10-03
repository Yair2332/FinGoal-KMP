package com.fingoal.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import fingoal.sharedlogic.generated.resources.Res
import fingoal.sharedlogic.generated.resources.habitosinfo
import fingoal.sharedlogic.generated.resources.objetivosinfo
import fingoal.sharedlogic.generated.resources.transferencias
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String = "Confirmar",
    dismissButtonText: String = "Cancelar"
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(dismissButtonText)
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateComponent(
    message: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "arrow_animation"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.8f
            ),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(
            modifier = Modifier.height(60.dp)
        )

        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer {
                    translationY = offsetY
                }
        )
    }
}

@Composable
fun AnimatedMotivationalCard(
    imageRes: DrawableResource,
    phrases: List<String>,
    shadowColor: Color,
    intervalMillis: Long = 5000L,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMillis)
            currentIndex = (currentIndex + 1) % phrases.size
        }
    }

    Box(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            shadowColor.copy(alpha = 0.5f),
                            shadowColor.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = phrases[currentIndex],
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(1000)
                    ) togetherWith fadeOut(
                        animationSpec = tween(1000)
                    )
                },
                label = "text_animation"
            ) { targetText ->
                Text(
                    text = targetText,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun InfoModal(
    onDismiss: () -> Unit,
    imageRes: DrawableResource,
    items: List<Triple<ImageVector, String, String>>
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                items.forEach { (icon, title, text) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(
                            start = 32.dp,
                            bottom = 8.dp
                        )
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entendido")
                }
            }
        }
    }
}

data class HelpInfo(
    val imageRes: DrawableResource,
    val items: List<Triple<ImageVector, String, String>>
)

fun getHelpInfoForRoute(
    route: String?
): HelpInfo? {
    return when {
        route?.contains("TransactionListRoute") == true -> {
            HelpInfo(
                imageRes = Res.drawable.transferencias,
                items = listOf(
                    Triple(
                        Icons.Default.AddCircle,
                        "Registrar",
                        "Toca el botón '+' para añadir un nuevo movimiento."
                    ),
                    Triple(
                        Icons.Default.Edit,
                        "Editar",
                        "Presiona una transacción para modificarla."
                    ),
                    Triple(
                        Icons.Default.Analytics,
                        "Balance",
                        "Visualiza el resumen mensual en la parte superior."
                    )
                )
            )
        }

        route?.contains("HabitListRoute") == true -> {
            HelpInfo(
                imageRes = Res.drawable.habitosinfo,
                items = listOf(
                    Triple(
                        Icons.Default.Add,
                        "Crear Hábito",
                        "Define una rutina financiera recurrente."
                    ),
                    Triple(
                        Icons.Default.Edit,
                        "Editar",
                        "Presiona un hábito para modificarlo."
                    ),
                    Triple(
                        Icons.Default.LocalFireDepartment,
                        "Racha",
                        "Completa hábitos varios días seguidos para mantener tu racha."
                    )
                )
            )
        }

        route?.contains("GoalListRoute") == true -> {
            HelpInfo(
                imageRes = Res.drawable.objetivosinfo,
                items = listOf(
                    Triple(
                        Icons.Default.Savings,
                        "Nueva Meta",
                        "Establece un objetivo de ahorro específico."
                    ),
                    Triple(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        "Progreso",
                        "Mira cuánto te falta para alcanzar tu meta."
                    ),
                    Triple(
                        Icons.Default.Edit,
                        "Editar",
                        "Presiona una meta para modificarla."
                    )
                )
            )
        }

        else -> null
    }
}