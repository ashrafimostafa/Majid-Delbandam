package com.mostafa.majiddelbandam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mostafa.majiddelbandam.ui.theme.AshrafiDeep
import com.mostafa.majiddelbandam.ui.theme.CreamCard
import com.mostafa.majiddelbandam.ui.theme.GoldLeaf
import com.mostafa.majiddelbandam.ui.theme.Turquoise

@Composable
fun TazhibFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(CreamCard)
            .border(3.dp, GoldLeaf, RoundedCornerShape(28.dp))
            .drawBehind {
                val inset = 14.dp.toPx()
                drawRoundRect(
                    color = Turquoise.copy(alpha = 0.55f),
                    topLeft = Offset(inset, inset),
                    size = size.copy(width = size.width - inset * 2, height = size.height - inset * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
                val r = 10.dp.toPx()
                val spots = listOf(
                    Offset(28.dp.toPx(), 28.dp.toPx()),
                    Offset(size.width - 28.dp.toPx(), 28.dp.toPx()),
                    Offset(28.dp.toPx(), size.height - 28.dp.toPx()),
                    Offset(size.width - 28.dp.toPx(), size.height - 28.dp.toPx())
                )
                spots.forEach { drawCircle(AshrafiDeep, r, it) }
            }
            .padding(24.dp)
    ) {
        content()
    }
}
