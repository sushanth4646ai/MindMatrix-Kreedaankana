package com.kreedaankana.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.kreedaankana.ui.theme.GlassWhite10
import com.kreedaankana.ui.theme.GlassWhite15

@Composable
fun PremiumShimmer(
    modifier: Modifier = Modifier,
    content: @Composable (Brush) -> Unit
) {
    val shimmerColors = listOf(
        GlassWhite10,
        GlassWhite15,
        GlassWhite10
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = 0f)
    )
    content(brush)
}

@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    height: Int = 180
) {
    Box(
        modifier = modifier
            .height(height.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        PremiumShimmer { brush ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            )
        }
    }
}

@Composable
fun ShimmerText(
    modifier: Modifier = Modifier,
    width: Float = 150f,
    height: Int = 16
) {
    Box(
        modifier = modifier
            .width(width.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        PremiumShimmer { brush ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            )
        }
    }
}