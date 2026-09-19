package com.mostafa.majiddelbandam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise

private val KeyFace = Color(0xF2FFFFFF)
private val KeyBorder = Color(0x99D9B482)
private val KeyShelf = Color(0xFFD9C5A7)
private val KeyInk = Color(0xFF292524)
private val HighlightFace = Color(0xFFF0FDFA)
private val HighlightBorder = Color(0xFF0D9488)

@Composable
fun PersianKeyboard(
    dimmedKeys: Set<Char>,
    onLetter: (Char) -> Unit,
    onBackspace: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Char? = null,
    confirmLabel: String? = null,
    onHelp: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Parchment.copy(alpha = 0.92f))
            .border(1.dp, Color(0xFFDFCBB0), RoundedCornerShape(16.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val widest = PersianLetters.rows.maxOf { it.length }
            PersianLetters.rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pad = widest - row.length
                    if (pad > 0) Spacer(Modifier.weight(pad / 2f))
                    row.forEach { ch ->
                        PersianKey(
                            label = ch.toString(),
                            enabled = ch !in dimmedKeys,
                            highlighted = ch == highlighted,
                            modifier = Modifier.weight(1f),
                            onClick = { onLetter(ch) }
                        )
                    }
                    if (pad > 0) Spacer(Modifier.weight(pad / 2f))
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionKey(
                onClick = onBackspace,
                modifier = Modifier.size(width = 48.dp, height = 36.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = stringResource(R.string.backspace),
                    tint = KeyInk,
                    modifier = Modifier.size(18.dp)
                )
            }
            ConfirmKey(
                label = confirmLabel ?: stringResource(R.string.confirm),
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
            )
            ActionKey(
                onClick = { onHelp?.invoke() },
                modifier = Modifier.size(width = 48.dp, height = 36.dp)
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = stringResource(R.string.how_to_play),
                    tint = Color(0xFF92400E),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun PersianKey(
    label: String,
    enabled: Boolean,
    highlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val face = when {
        !enabled -> Color(0xFFE7E5E4)
        highlighted -> HighlightFace
        else -> KeyFace
    }
    val border = when {
        highlighted -> HighlightBorder
        else -> KeyBorder
    }
    val shelf = if (pressed || !enabled) Color.Transparent else KeyShelf
    Box(
        modifier = modifier
            .height(32.dp)
            .offset(y = if (pressed && enabled) 1.5.dp else 0.dp)
            .shadow(if (pressed || !enabled) 0.dp else 1.5.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(face)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .then(
                if (shelf != Color.Transparent) {
                    Modifier.border(1.dp, shelf.copy(alpha = 0.01f), RoundedCornerShape(8.dp))
                } else Modifier
            )
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (enabled) {
                if (highlighted) Color(0xFF134E4A) else KeyInk
            } else Color(0xFFA8A29E),
            fontSize = 13.sp,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
private fun ActionKey(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Box(
        modifier = modifier
            .offset(y = if (pressed) 1.5.dp else 0.dp)
            .shadow(if (pressed) 0.dp else 1.5.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(KeyFace)
            .border(1.dp, KeyBorder, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ConfirmKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Row(
        modifier = modifier
            .offset(y = if (pressed) 1.dp else 0.dp)
            .shadow(if (pressed) 2.dp else 4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(if (pressed) Color(0xFF0D5B55) else Turquoise)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        Icon(Icons.Filled.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
