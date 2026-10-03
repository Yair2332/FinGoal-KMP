package com.fingoal.app.ui.screens.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.ui.screens.transactions.CategoryProvider

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormContent(
    initialTransaction: Transaction? = null,
    onSave: (
        String,
        Double,
        String,
        Boolean,
        String
    ) -> Unit
) {
    var title by remember {
        mutableStateOf(initialTransaction?.title ?: "")
    }

    var description by remember {
        mutableStateOf(initialTransaction?.description ?: "")
    }

    var amount by remember {
        mutableStateOf(
            initialTransaction?.amount?.toString() ?: ""
        )
    }

    var category by remember {
        mutableStateOf(
            initialTransaction?.category?.uppercase() ?: "OTROS"
        )
    }

    var isIncome by remember {
        mutableStateOf(
            initialTransaction?.isIncome ?: true
        )
    }

    var isError by remember {
        mutableStateOf(false)
    }

    val incomeColor = Color(0xFF4CAF50)
    val expenseColor = Color(0xFFE53935)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = if (initialTransaction == null) {
                    "Nueva Transacción"
                } else {
                    "Editar Transacción"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            val items = listOf(
                "Ingreso" to incomeColor,
                "Gasto" to expenseColor
            )

            items.forEachIndexed { index: Int, pair: Pair<String, Color> ->
                val (text, color) = pair

                val selected = (index == 0) == isIncome

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) {
                                color
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            isIncome = index == 0
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (index == 0) {
                                Icons.Default.ArrowUpward
                            } else {
                                Icons.Default.ArrowDownward
                            },
                            contentDescription = null,
                            tint = if (selected) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = text,
                            color = if (selected) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { value: String ->
                title = value
            },
            label = {
                Text("Título")
            },
            isError = isError && title.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { value: String ->
                if (
                    value.all { char: Char ->
                        char.isDigit() || char == '.'
                    }
                ) {
                    amount = value
                }
            },
            label = {
                Text("Monto ($)")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            isError = isError &&
                    (amount.toDoubleOrNull() ?: 0.0) <= 0,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Categoría",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryProvider.categorias.forEach { cat ->

                val isSelected = category.uppercase() == cat.nombre.uppercase()

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        category = cat.nombre.uppercase()
                    },
                    label = {
                        Text(cat.nombre)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = cat.icono,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { value: String ->
                description = value
            },
            label = {
                Text("Descripción (Opcional)")
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
        )

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull() ?: 0.0

                if (
                    title.isNotBlank() &&
                    parsedAmount > 0 &&
                    category.isNotBlank()
                ) {
                    onSave(
                        title,
                        parsedAmount,
                        category,
                        isIncome,
                        description
                    )
                } else {
                    isError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Guardar Transacción",
                fontWeight = FontWeight.Bold
            )
        }
    }
}