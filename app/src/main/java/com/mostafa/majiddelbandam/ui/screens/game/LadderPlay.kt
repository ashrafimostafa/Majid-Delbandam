package com.mostafa.majiddelbandam.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.audio.LocalGameAudio
import com.mostafa.majiddelbandam.domain.Puzzle
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Clay
import com.mostafa.majiddelbandam.ui.theme.Turquoise
import com.mostafa.majiddelbandam.ui.components.LiquidGlass

private val OnSurface = Color(0xFF2C1701)
private val Glass = Color(0xEBFFF8F5)
private val AmberFace = Color(0xFFFEFAF0)
private val TealFace = Color(0xFFF5FBF8)
private val CurrentFace = Color(0xFFEDF7F5)
private val RoseFace = Color(0xFFFDF2F2)

@Composable
fun SimpleLadder(
    puzzle: Puzzle,
    committed: List<String>,
    draft: String,
    onUndo: () -> Unit
) {
    val start = committed.firstOrNull() ?: puzzle.startWord
    val length = start.length
    LiquidGlass(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Caption(stringResource(R.string.source_label))
        WordLine(word = start, style = LineStyle.Start)
        committed.drop(1).forEach { word ->
            WordLine(word = word, style = LineStyle.Accepted)
        }
        Caption(stringResource(R.string.current_step_label))
        InputLine(
            draft = draft,
            length = length,
            canUndo = committed.size > 1,
            onUndo = onUndo
        )
        Text(
            stringResource(R.string.enter_word_hint),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.88f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            color = OnSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Caption(stringResource(R.string.target_label))
        WordLine(word = puzzle.endWord, style = LineStyle.End)
    }
    }
}

private enum class LineStyle { Start, Accepted, Input, End }

@Composable
private fun Caption(text: String) {
    Text(
        text = text.trimEnd(':', '：').trim(),
        color = OnSurface,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start
    )
}

@Composable
private fun WordLine(word: String, style: LineStyle) {
    val bg = when (style) {
        LineStyle.Start -> AmberFace
        LineStyle.Accepted -> TealFace
        LineStyle.End -> RoseFace
        LineStyle.Input -> CurrentFace
    }
    val border = when (style) {
        LineStyle.Start -> Color(0xFFFDE68A)
        LineStyle.Accepted -> Color(0xFF99F6E4)
        LineStyle.End -> Color(0xFFFECDD3)
        LineStyle.Input -> Turquoise
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        word.forEach { ch ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, border, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(ch.toString(), color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun InputLine(
    draft: String,
    length: Int,
    canUndo: Boolean,
    onUndo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CurrentFace)
            .border(2.dp, Turquoise, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(length) { index ->
                val letter = draft.getOrNull(index)?.toString().orEmpty()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (letter.isEmpty()) Color(0xFFF8FAFC) else Color.White)
                        .border(
                            1.dp,
                            if (index == draft.length) Turquoise else Color(0xFF99F6E4),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (letter.isEmpty()) "·" else letter,
                        color = if (letter.isEmpty()) Color(0xFFA8A29E) else OnSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
        if (canUndo) {
            Icon(
                Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(R.string.undo),
                tint = Turquoise,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = LocalGameAudio.current.wrap(onUndo))
            )
        }
    }
}

@Composable
fun IranianSnack(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFF8EE))
            .border(1.5.dp, Color(0xFFC4A574), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("۞", color = Adobe, fontSize = 12.sp)
        Text(
            "«$text»",
            color = Clay,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Text("✦  ✦  ✦", color = Color(0xFFC4A574), fontSize = 9.sp)
    }
}
