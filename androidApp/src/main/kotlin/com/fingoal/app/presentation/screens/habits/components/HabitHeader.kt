package com.fingoal.app.presentation.screens.habits.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fingoal.app.R
import com.fingoal.app.presentation.components.AnimatedMotivationalCard

@Composable
fun HabitHeader(phrases: List<String>) {
    Spacer(modifier = Modifier.height(16.dp))
    AnimatedMotivationalCard(
        imageRes = R.drawable.habitos,
        phrases = phrases,
        shadowColor = Color(0xFFF57C00)
    )
    Spacer(modifier = Modifier.height(8.dp))
}