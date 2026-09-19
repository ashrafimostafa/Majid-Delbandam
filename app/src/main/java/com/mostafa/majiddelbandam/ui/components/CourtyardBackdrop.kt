package com.mostafa.majiddelbandam.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Brick
import com.mostafa.majiddelbandam.ui.theme.BrickDark
import com.mostafa.majiddelbandam.ui.theme.Geranium
import com.mostafa.majiddelbandam.ui.theme.GeraniumLeaf
import com.mostafa.majiddelbandam.ui.theme.Howz
import com.mostafa.majiddelbandam.ui.theme.HowzDeep
import com.mostafa.majiddelbandam.ui.theme.OrosiAmber
import com.mostafa.majiddelbandam.ui.theme.OrosiCyan
import com.mostafa.majiddelbandam.ui.theme.OrosiRose
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise

@Composable
fun CourtyardBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFCDE8E4), Parchment, Color(0xFFF3E0C8))
            )
        )
        drawOrosi(this)
        drawWalls(this)
        drawHowz(this)
        drawGeraniums(this)
    }
}

private fun drawOrosi(scope: DrawScope) = with(scope) {
    val top = size.height * 0.06f
    val height = size.height * 0.16f
    val cols = 7
    val rows = 3
    val paneW = size.width / cols
    val paneH = height / rows
    val colors = listOf(OrosiAmber, OrosiRose, OrosiCyan, Turquoise, Geranium)
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            drawRect(
                color = colors[(r + c) % colors.size].copy(alpha = 0.55f),
                topLeft = Offset(c * paneW + 4f, top + r * paneH + 4f),
                size = Size(paneW - 8f, paneH - 8f)
            )
        }
    }
    drawRect(
        color = BrickDark.copy(alpha = 0.55f),
        topLeft = Offset(0f, top - 8f),
        size = Size(size.width, height + 16f),
        style = Stroke(width = 10f)
    )
}

private fun drawWalls(scope: DrawScope) = with(scope) {
    val brickH = 22f
    val brickW = 64f
    val wallTop = size.height * 0.22f
    val wallBottom = size.height * 0.58f
    var y = wallTop
    var row = 0
    while (y < wallBottom) {
        var x = if (row % 2 == 0) 0f else -brickW / 2f
        while (x < size.width) {
            drawRoundRect(
                color = if ((row + x.toInt()) % 3 == 0) Adobe else Brick.copy(alpha = 0.55f),
                topLeft = Offset(x + 2f, y + 2f),
                size = Size(brickW - 4f, brickH - 4f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            x += brickW
        }
        y += brickH
        row++
    }
}

private fun drawHowz(scope: DrawScope) = with(scope) {
    val cx = size.width / 2f
    val cy = size.height * 0.72f
    val w = size.width * 0.46f
    val h = size.height * 0.16f
    drawRoundRect(
        color = BrickDark,
        topLeft = Offset(cx - w / 2f - 10f, cy - h / 2f - 10f),
        size = Size(w + 20f, h + 20f),
        cornerRadius = CornerRadius(18f, 18f)
    )
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(Howz, HowzDeep),
            center = Offset(cx, cy)
        ),
        topLeft = Offset(cx - w / 2f, cy - h / 2f),
        size = Size(w, h),
        cornerRadius = CornerRadius(16f, 16f)
    )
    drawCircle(Color.White.copy(alpha = 0.25f), radius = w * 0.08f, center = Offset(cx - w * 0.18f, cy - h * 0.15f))
}

private fun drawGeraniums(scope: DrawScope) = with(scope) {
    fun pot(x: Float, y: Float) {
        drawRect(GeraniumLeaf, topLeft = Offset(x + 10f, y - 28f), size = Size(8f, 28f))
        drawCircle(Geranium, radius = 16f, center = Offset(x + 14f, y - 36f))
        drawCircle(Geranium.copy(alpha = 0.8f), radius = 11f, center = Offset(x + 4f, y - 30f))
        val pot = Path().apply {
            moveTo(x, y)
            lineTo(x + 28f, y)
            lineTo(x + 22f, y + 26f)
            lineTo(x + 6f, y + 26f)
            close()
        }
        drawPath(pot, Adobe)
    }
    pot(size.width * 0.12f, size.height * 0.62f)
    pot(size.width * 0.82f, size.height * 0.62f)
    pot(size.width * 0.22f, size.height * 0.84f)
    pot(size.width * 0.72f, size.height * 0.84f)
}
