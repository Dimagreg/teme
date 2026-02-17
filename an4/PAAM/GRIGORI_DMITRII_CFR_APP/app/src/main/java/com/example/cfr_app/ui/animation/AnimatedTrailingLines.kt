package com.example.cfr_app.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.cfr_app.ui.theme.CFR_DARK_GREEN

@Composable
fun AnimatedTrailingLines(
    isMoving: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "trailing_lines")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing, delayMillis = 250),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset2"
    )

    val offset3 by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing, delayMillis = 500),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset3"
    )

    if (isMoving) {
        Canvas(modifier = modifier.size(200.dp, 40.dp)) {
            val lineLength = 400f
            val spacing = 12f
            val centerY = size.height / 2

            // Line 1 (top)
            drawLine(
                color = CFR_DARK_GREEN,
                start = Offset(offset1, centerY - spacing),
                end = Offset(offset1 + lineLength, centerY - spacing),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Line 2 (middle)
            drawLine(
                color = CFR_DARK_GREEN,
                start = Offset(offset2, centerY),
                end = Offset(offset2 + lineLength, centerY),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Line 3 (bottom)
            drawLine(
                color = CFR_DARK_GREEN,
                start = Offset(offset3, centerY + spacing),
                end = Offset(offset3 + lineLength, centerY + spacing),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
    }
}