package com.fingoal.app.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fingoal.app.domain.model.Goal
import com.fingoal.app.presentation.goals.GoalViewModel
import com.fingoal.app.ui.components.AnimatedMotivationalCard
import com.fingoal.app.ui.components.ConfirmationDialog
import com.fingoal.app.ui.components.EmptyStateComponent
import com.fingoal.app.ui.screens.goals.components.GoalFormContent
import com.fingoal.app.ui.screens.goals.components.GoalItem
import fingoal.sharedlogic.generated.resources.Res
import fingoal.sharedlogic.generated.resources.metas_target

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalViewModel,
    showSheet: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<Goal?>(null) }
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showSheet) {
        if (showSheet) {
            showBottomSheet = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedMotivationalCard(
                    imageRes = Res.drawable.metas_target,
                    phrases = listOf(
                        "Una meta sin un plan es solo un deseo.",
                        "El éxito es el resultado de metas claras."
                    ),
                    shadowColor = Color(0xFF0056B3)
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
            } else if (uiState.goals.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyStateComponent(
                            "¡No tienes metas aún! Pulsa el botón de abajo para registrar tu primer meta."
                        )
                    }
                }
            } else {
                items(
                    items = uiState.goals,
                    key = { goal -> goal.remoteId }
                ) { goal ->
                    GoalItem(
                        goal = goal,
                        onDelete = {
                            goalToDelete = goal
                            showDeleteDialog = true
                        },
                        onEdit = {
                            goalToEdit = goal
                            showBottomSheet = true
                        },
                        onContribution = { amount, isAdding ->
                            viewModel.handleContribution(
                                goal.remoteId,
                                amount,
                                isAdding
                            )
                        }
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    goalToEdit = null
                    onDismiss()
                }
            ) {
                GoalFormContent(
                    goal = goalToEdit,
                    onSave = { title, desc, amount, image ->
                        viewModel.saveGoal(
                            goalToEdit,
                            title,
                            desc,
                            amount,
                            image
                        )

                        showBottomSheet = false
                        goalToEdit = null
                        onDismiss()
                    }
                )
            }
        }

        if (showDeleteDialog && goalToDelete != null) {
            ConfirmationDialog(
                onDismiss = {
                    showDeleteDialog = false
                },
                onConfirm = {
                    goalToDelete?.let {
                        viewModel.deleteGoal(it)
                    }
                    showDeleteDialog = false
                },
                title = "¿Eliminar meta?",
                text = "¿Seguro que quieres eliminar '${goalToDelete?.title}'?",
                confirmButtonText = "Eliminar"
            )
        }
    }
}