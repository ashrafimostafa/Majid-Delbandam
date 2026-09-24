package com.mostafa.majiddelbandam.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun LiquidGlass(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(28.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val frost = if (Build.VERSION.SDK_INT >= 31) Modifier.blur(10.dp) else Modifier
    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x14000000)
            )
            .clip(shape)
    ) {
        Box(
            Modifier
                .matchParentSize()
                .then(frost)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.55f),
                            Color(0xFFFFF4EA).copy(alpha = 0.28f),
                            Color(0xFFFFE3CC).copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0.42f)
                        )
                    )
                )
        )
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.38f),
                        0.45f to Color.Transparent,
                        1f to Color(0xFFC4A574).copy(alpha = 0.10f)
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.78f),
                            Color.White.copy(alpha = 0.18f),
                            Color.White.copy(alpha = 0.50f)
                        )
                    ),
                    shape
                )
        )
        content()
    }
}
