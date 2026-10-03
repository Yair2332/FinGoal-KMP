package com.fingoal.app.ui.screens.goals.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssistantPhoto
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fingoal.app.domain.model.Goal

@Composable
fun GoalFormContent(
    goal: Goal?,
    onSave: (String, String, Double, String) -> Unit
) {
    var title by remember {
        mutableStateOf(goal?.title ?: "")
    }

    var desc by remember {
        mutableStateOf(goal?.description ?: "")
    }

    var amount by remember {
        mutableStateOf(goal?.targetAmount?.toString() ?: "")
    }

    var image by remember {
        mutableStateOf(goal?.localImagePath ?: "")
    }

    var hasAttemptedSave by remember {
        mutableStateOf(false)
    }

    val parsedAmount = amount.toDoubleOrNull()
    val isAmountValid = parsedAmount != null && parsedAmount > 0
    val isTitleValid = title.isNotBlank()

    val showError = hasAttemptedSave &&
            (!isTitleValid || !isAmountValid)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AssistantPhoto,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (goal == null) {
                    "Nueva Meta"
                } else {
                    "Editar Meta"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text("Título")
            },
            placeholder = {
                Text("Ej: Viaje a Japón")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = hasAttemptedSave && !isTitleValid,
            singleLine = true
        )

        OutlinedTextField(
            value = amount,
            onValueChange = {
                if (
                    it.isEmpty() ||
                    it.matches(Regex("^\\d*\\.?\\d{0,2}$"))
                ) {
                    amount = it
                }
            },
            label = {
                Text("Monto Objetivo ($)")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = hasAttemptedSave && !isAmountValid,
            singleLine = true
        )

        OutlinedTextField(
            value = image,
            onValueChange = {
                image = it
            },
            label = {
                Text("URL de imagen (opcional)")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = desc,
            onValueChange = {
                desc = it
            },
            label = {
                Text("Descripción breve")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 3
        )

        if (showError) {
            Text(
                text = "Por favor, revisa que el título y el monto sean correctos.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                hasAttemptedSave = true

                if (isTitleValid && isAmountValid) {
                    onSave(
                        title,
                        desc,
                        parsedAmount!!,
                        image
                    )
                }
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Save,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                "Guardar Meta",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}