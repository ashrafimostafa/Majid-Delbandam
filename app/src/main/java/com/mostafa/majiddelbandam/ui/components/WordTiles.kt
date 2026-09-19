package com.mostafa.majiddelbandam.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mostafa.majiddelbandam.ui.theme.Ashrafi
import com.mostafa.majiddelbandam.ui.theme.Charcoal
import com.mostafa.majiddelbandam.ui.theme.GoldLeaf
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise

@Composable
fun WordTiles(
    word: String,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    hintIndex: Int? = null,
    onSelect: ((Int) -> Unit)? = null,
    locked: Boolean = false,
    tileSize: Dp = 46.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        word.forEachIndexed { index, char ->
            val selected = selectedIndex == index
            val hinted = hintIndex == index
            val scale by animateFloatAsState(if (selected) 1.08f else 1f, label = "tile")
            Box(
                modifier = Modifier
                    .size(tileSize)
                    .scale(scale)
                    .shadow(if (selected) 8.dp else 2.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            hinted -> Ashrafi.copy(alpha = 0.35f)
                            locked -> Turquoise.copy(alpha = 0.16f)
                            selected -> Parchment
                            else -> Color.White
                        }
                    )
                    .border(
                        width = if (selected || hinted) 2.dp else 1.dp,
                        color = when {
                            hinted -> Ashrafi
                            selected -> Turquoise
                            locked -> Turquoise.copy(alpha = 0.4f)
                            else -> GoldLeaf.copy(alpha = 0.5f)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .then(
                        if (onSelect != null && !locked) Modifier.clickable { onSelect(index) }
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Charcoal
                )
            }
        }
    }
}
