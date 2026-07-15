package com.adentweets.app.presentation.components.shimmer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerPostCard() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(22.dp)).shimmerBackground())
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.width(120.dp).height(16.dp).clip(RoundedCornerShape(8.dp)).shimmerBackground())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp).clip(RoundedCornerShape(7.dp)).shimmerBackground())
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp).clip(RoundedCornerShape(7.dp)).shimmerBackground())
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    repeat(4) { Box(modifier = Modifier.size(20.dp).clip(RoundedCornerShape(10.dp)).shimmerBackground()) }
                }
            }
        }
    }
}

@Composable
fun ShimmerProfileHeader() {
    Column {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp).shimmerBackground())
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(40.dp)).shimmerBackground())
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.width(140.dp).height(20.dp).clip(RoundedCornerShape(10.dp)).shimmerBackground())
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.width(100.dp).height(14.dp).clip(RoundedCornerShape(7.dp)).shimmerBackground())
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).clip(RoundedCornerShape(7.dp)).shimmerBackground())
        }
    }
}

@Composable
private fun Modifier.shimmerBackground(): Modifier {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(initialValue = 0f, targetValue = 1200f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "shimmer_translate")
    return this.background(brush = Brush.linearGradient(colors = shimmerColors, start = Offset(translateAnim, 0f), end = Offset(translateAnim + 300f, 0f)))
}