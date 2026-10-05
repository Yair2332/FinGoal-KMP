package com.fingoal.app.ui.screens.auth.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    isPassword: Boolean = false
) {
    val isDark = isSystemInDarkTheme()

    val textColor = if (isDark) {
        Color.White
    } else {
        Color.Black
    }

    val labelColor = if (isDark) {
        Color(0xFFE0E0E0)
    } else {
        Color(0xFF555555)
    }

    val iconColor = if (isDark) {
        Color(0xFFE0E0E0)
    } else {
        Color(0xFF444444)
    }

    val borderColor = if (isDark) {
        Color(0xFF888888)
    } else {
        Color(0xFF777777)
    }

    val backgroundColor = if (isDark) {
        Color(0xFF1E1E1E)
    } else {
        Color.White
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        label = {
            Text(
                text = label,
                color = labelColor
            )
        },

        leadingIcon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        },

        singleLine = true,

        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) {
                KeyboardType.Password
            } else {
                KeyboardType.Email
            }
        ),

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(12.dp),

        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,

            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,

            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor,

            focusedLeadingIconColor = iconColor,
            unfocusedLeadingIconColor = iconColor,

            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,

            cursorColor = if (isDark) {
                Color.White
            } else {
                Color.Black
            }
        )
    )
}

@Composable
fun AuthButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}