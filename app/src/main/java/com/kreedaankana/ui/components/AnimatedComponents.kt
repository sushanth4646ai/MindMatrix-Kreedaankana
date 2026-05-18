package com.kreedaankana.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kreedaankana.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AnimatedBorderCard(
    modifier: Modifier = Modifier,
    borderColor: Color = NeonGreen,
    cornerRadius: Int = 24,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "border_animation")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "border_offset"
    )

    Box(
        modifier = modifier
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            borderColor.copy(alpha = 1f),
                            borderColor.copy(alpha = 0.3f),
                            borderColor.copy(alpha = 0f),
                            borderColor.copy(alpha = 0.3f),
                            borderColor.copy(alpha = 1f)
                        ),
                        start = Offset(animatedOffset * size.width, 0f),
                        end = Offset(animatedOffset * size.width + size.width, size.height)
                    ),
                    size = size
                )
            }
            .clip(RoundedCornerShape(cornerRadius.dp))
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(cornerRadius.dp))
            .background(SurfaceDark)
    ) {
        content()
    }
}

@Composable
fun PulseGlow(
    color: Color = NeonGreen,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(modifier = Modifier.drawBehind {
        drawCircle(
            color = color.copy(alpha = alpha * 0.3f),
            radius = size.minDimension * 0.8f
        )
    }) {
        content()
    }
}

@Composable
fun LiveBadge(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_alpha"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ErrorRed.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ErrorRed.copy(alpha = alpha))
        )
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelSmall,
            color = ErrorRed
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "confirmed" -> SuccessGreen.copy(alpha = 0.2f) to SuccessGreen
        "pending" -> WarningYellow.copy(alpha = 0.2f) to WarningYellow
        "cancelled" -> ErrorRed.copy(alpha = 0.2f) to ErrorRed
        else -> GlassWhite15 to TextPrimary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun AnimatedScoreText(
    score: String,
    modifier: Modifier = Modifier
) {
    var displayScore by remember { mutableStateOf("0") }
    val animatedScore = score.toIntOrNull() ?: 0

    LaunchedEffect(score) {
        for (i in 0..animatedScore) {
            delay(50L)
            displayScore = i.toString()
        }
    }

    Text(
        text = displayScore,
        style = MaterialTheme.typography.displayMedium,
        color = NeonGreen,
        modifier = modifier
    )
}