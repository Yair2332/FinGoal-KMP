package com.fingoal.app.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fingoal.app.ui.components.AssistantFinGoal
import com.fingoal.app.ui.components.ConfirmationDialog
import com.fingoal.app.ui.components.EmptyStateComponent
import com.fingoal.app.ui.screens.habits.components.AddHabitDialog
import com.fingoal.app.ui.screens.habits.components.HabitHeader
import com.fingoal.app.ui.screens.habits.components.HabitItem
import com.fingoal.app.ui.screens.habits.components.HabitSummaryCard

@Composable
fun HabitScreen(
    viewModel: HabitViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val phrases = listOf(
        "Tus hábitos determinan tu futuro financiero. ¡Construye hoy el mañana que deseas!",
        "No es cuánto ganas, sino cómo administras lo que tienes lo que marca la diferencia.",
        "El ahorro es la base de la libertad financiera y la tranquilidad personal.",
        "La disciplina financiera es el puente entre tus objetivos y tus logros."
    )

    val groupedHabits = uiState.habits.groupBy {
        it.frequency.uppercase()
    }

    val order = listOf(
        "DIARIO",
        "SEMANAL",
        "MENSUAL"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .background(
                    MaterialTheme.colorScheme.background
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                bottom = 80.dp
            )
        ) {

            item {
                HabitHeader(
                    phrases = phrases
                )
            }

            item {
                HabitSummaryCard(
                    summary = viewModel.getHabitSummary()
                )
            }

            if (uiState.isLoading) {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

            } else if (uiState.habits.isEmpty()) {

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyStateComponent(
                            message = "¡No tienes hábitos aún! Pulsa el botón para registrar tu primer hábito."
                        )
                    }
                }

            } else {

                order.forEach { frequency ->

                    val habits = groupedHabits[frequency]

                    if (!habits.isNullOrEmpty()) {

                        item {
                            Text(
                                text = frequency
                                    .lowercase()
                                    .replaceFirstChar {
                                        it.uppercase()
                                    },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(
                                    start = 8.dp
                                )
                            )

                            Divider(
                                modifier = Modifier.padding(
                                    vertical = 1.dp
                                )
                            )
                        }

                        items(
                            items = habits,
                            key = { it.remoteId }
                        ) { habit ->

                            HabitItem(
                                habit = habit,

                                onToggle = {
                                    viewModel.toggleHabit(
                                        habit.remoteId
                                    )
                                },

                                onDelete = {
                                    viewModel.showDeleteDialog(
                                        habit
                                    )
                                },

                                onEdit = {
                                    viewModel.showEditDialog(
                                        habit
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        /*
         * ====================================================
         * ASISTENTE FIN GOAL
         * ====================================================
         *
         * Sun es el personaje correspondiente a Hábitos.
         */

        AssistantFinGoal(
            character = "sun",
            questions = uiState.assistantQuestions,
            modifier = Modifier.fillMaxSize()
        )

        /*
         * ====================================================
         * DIÁLOGO ELIMINAR
         * ====================================================
         */

        if (uiState.habitToDelete != null) {

            ConfirmationDialog(
                onDismiss = {
                    viewModel.hideDeleteDialog()
                },

                onConfirm = {
                    viewModel.deleteHabit()
                },

                title = "¿Eliminar hábito?",

                text =
                    "¿Seguro que quieres eliminar '${uiState.habitToDelete?.name}'?",

                confirmButtonText = "Eliminar"
            )
        }

        /*
         * ====================================================
         * DIÁLOGO AGREGAR / EDITAR
         * ====================================================
         */

        if (uiState.showAddDialog) {

            AddHabitDialog(

                initialTitle =
                    uiState.habitToEdit?.name ?: "",

                initialDescription =
                    uiState.habitToEdit?.description ?: "",

                initialFrequency =
                    uiState.habitToEdit?.frequency ?: "DIARIO",

                onDismiss = {
                    viewModel.hideDialog()
                },

                onConfirm = { name, desc, freq ->

                    if (uiState.habitToEdit != null) {

                        viewModel.updateHabit(
                            uiState.habitToEdit!!.copy(
                                name = name,
                                description = desc,
                                frequency = freq
                            )
                        )

                    } else {

                        viewModel.createHabit(
                            name,
                            desc,
                            freq
                        )
                    }

                    viewModel.hideDialog()
                }
            )
        }
    }
}