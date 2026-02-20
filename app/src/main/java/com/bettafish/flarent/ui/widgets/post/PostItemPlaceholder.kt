package com.bettafish.flarent.ui.widgets.post

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PostItemPlaceholder(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "placeholder")
    val alpha by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Column(modifier = modifier.fillMaxWidth().alpha(alpha)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorScheme.onSurface)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.onSurface)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .width(80.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.onSurface)
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colorScheme.onSurface)
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.85f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colorScheme.onSurface)
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.6f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colorScheme.onSurface)
        )
    }
}
