package org.quijava.quijava.compose.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated circular progress indicator with smooth rotation
 */
@Composable
fun AnimatedProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(size)) {
        val canvasSize = this.size.minDimension
        val radius = (canvasSize - strokeWidth.toPx()) / 2
        val center = Offset(canvasSize / 2, canvasSize / 2)

        // Draw arc
        drawArc(
            color = color,
            startAngle = rotation,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            ),
            topLeft = Offset(
                center.x - radius,
                center.y - radius
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}

/**
 * Pulsating dots loading indicator
 */
@Composable
fun PulsatingDotsIndicator(
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp,
    spacing: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    
    val scales = List(3) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    delayMillis = index * 200,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale$index"
        )
    }

    Canvas(modifier = modifier.size(
        width = dotSize * 3 + spacing * 2,
        height = dotSize
    )) {
        val dotRadius = dotSize.toPx() / 2
        val totalWidth = size.width
        val centerY = size.height / 2

        scales.forEachIndexed { index, scale ->
            val x = dotRadius + index * (dotSize.toPx() + spacing.toPx())
            drawCircle(
                color = color,
                radius = dotRadius * scale.value,
                center = Offset(x, centerY)
            )
        }
    }
}

/**
 * Spinning bars loading indicator
 */
@Composable
fun SpinningBarsIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    barCount: Int = 12,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bars")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(size)) {
        val canvasSize = this.size.minDimension
        val barLength = canvasSize * 0.25f
        val barWidth = canvasSize * 0.08f
        val radius = canvasSize / 2 - barLength / 2
        val center = Offset(canvasSize / 2, canvasSize / 2)

        for (i in 0 until barCount) {
            val angle = Math.toRadians((360.0 / barCount * i + rotation).toDouble())
            val alpha = 1f - (i.toFloat() / barCount)
            
            val startX = center.x + (radius * cos(angle)).toFloat()
            val startY = center.y + (radius * sin(angle)).toFloat()
            val endX = center.x + ((radius + barLength) * cos(angle)).toFloat()
            val endY = center.y + ((radius + barLength) * sin(angle)).toFloat()

            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
