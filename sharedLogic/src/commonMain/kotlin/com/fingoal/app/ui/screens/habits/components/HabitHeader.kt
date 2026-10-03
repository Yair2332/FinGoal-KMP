package com.fingoal.app.ui.screens.habits.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fingoal.app.ui.components.AnimatedMotivationalCard
import fingoal.sharedlogic.generated.resources.Res
import fingoal.sharedlogic.generated.resources.habitos

@Composable
fun HabitHeader(
    phrases: List<String>
) {
    Spacer(
        modifier = Modifier.height(16.dp)
    )

    AnimatedMotivationalCard(
        imageRes = Res.drawable.habitos,
        phrases = phrases,
        shadowColor = Color(0xFFF57C00)
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )
}