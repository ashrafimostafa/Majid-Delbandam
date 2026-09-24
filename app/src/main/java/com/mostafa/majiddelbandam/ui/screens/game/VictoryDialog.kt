package com.mostafa.majiddelbandam.ui.screens.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.audio.LocalGameAudio
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.Victory
import com.mostafa.majiddelbandam.ui.components.CoinIcon
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Clay
import com.mostafa.majiddelbandam.ui.theme.Turquoise
import kotlinx.coroutines.delay

private val OnSurface = Color(0xFF2C1701)
private val SurfaceLow = Color(0xFFFFF1E7)
private val SurfaceMid = Color(0xFFFFEADA)
private val SurfaceHigh = Color(0xFFFFE3CC)
private val SurfaceVariant = Color(0xFFFFDCBD)
private val Tertiary = Color(0xFF992142)
private val TealDeep = Color(0xFF0C4A45)
private val GoldDeep = Color(0xFF78350F)
private val AmberLine = Color(0xFFD97706)

@Composable
fun VictoryDialog(
    victory: Victory,
    stageLabel: String,
    landmark: String,
    nextLevelId: Int?,
    onNext: () -> Unit,
    onMap: () -> Unit,
    onShare: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.courtyard),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.05f),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(OnSurface.copy(alpha = 0.35f)))
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to OnSurface.copy(alpha = 0.40f),
                        0.45f to Color.Transparent,
                        1f to OnSurface.copy(alpha = 0.70f)
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLow)
                        .border(1.dp, Adobe.copy(alpha = 0.30f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(10.dp))
                    VictoryStars(stars = victory.stars)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.victory_headline),
                        color = Color(0xFF005C55),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(CircleShape)
                            .background(Turquoise.copy(alpha = 0.10f))
                            .border(1.dp, Turquoise.copy(alpha = 0.20f), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(Turquoise))
                        Text(
                            stringResource(R.string.stage_conquered, stageLabel),
                            color = Turquoise,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    ManuscriptDivider()
                    DialogueBubble(victory)
                    RewardTray(victory, landmark)
                    MetricsRow(victory)
                    Spacer(Modifier.height(8.dp))
                    PressButton(
                        label = if (nextLevelId != null) {
                            stringResource(
                                R.string.next_level_num,
                                PersianLetters.toPersianDigits(nextLevelId)
                            )
                        } else {
                            stringResource(R.string.neighborhood_map)
                        },
                        icon = Icons.Filled.PlayArrow,
                        container = Turquoise,
                        content = Color.White,
                        shelf = TealDeep,
                        onClick = if (nextLevelId != null) onNext else onMap,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PressButton(
                            label = stringResource(R.string.neighborhood_map),
                            icon = Icons.Filled.Stairs,
                            container = SurfaceMid,
                            content = Adobe,
                            shelf = GoldDeep,
                            onClick = onMap,
                            modifier = Modifier.weight(1f)
                        )
                        PressButton(
                            label = stringResource(R.string.share_honor),
                            icon = Icons.Filled.Share,
                            container = SurfaceMid,
                            content = Color(0xFF3E4947),
                            shelf = Color(0xFFB8A48A),
                            onClick = onShare,
                            modifier = Modifier.weight(1f),
                            iconTint = Tertiary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-8).dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceMid)
                        .border(1.dp, AmberLine.copy(alpha = 0.50f), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("✦", color = Tertiary, fontSize = 12.sp)
                    Text(
                        stringResource(R.string.conquest_title, landmark),
                        color = Color(0xFF005C55),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text("✦", color = Tertiary, fontSize = 12.sp)
                }
            }
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = SurfaceHigh.copy(alpha = 0.90f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    stringResource(R.string.game_footnote, landmark),
                    color = SurfaceHigh.copy(alpha = 0.90f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun VictoryStars(stars: Int) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StarBadge(filled = stars >= 1, delayMs = 150, size = 48.dp, tilt = -6f)
        StarBadge(filled = stars >= 2, delayMs = 300, size = 64.dp, tilt = 0f, lift = 8.dp)
        StarBadge(filled = stars >= 3, delayMs = 450, size = 48.dp, tilt = 6f)
    }
}

@Composable
private fun StarBadge(
    filled: Boolean,
    delayMs: Int,
    size: androidx.compose.ui.unit.Dp,
    tilt: Float,
    lift: androidx.compose.ui.unit.Dp = 0.dp
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(filled) {
        delay(delayMs.toLong())
        shown = true
    }
    val scale by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(500),
        label = "star-pop"
    )
    Box(
        modifier = Modifier
            .padding(bottom = lift)
            .size(size)
            .scale(scale)
            .rotate(tilt)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(
                if (filled) {
                    Brush.linearGradient(listOf(AmberLine, Color(0xFFF59E0B), Color(0xFFFEF08A)))
                } else {
                    Brush.linearGradient(listOf(Color(0xFFD6D3D1), Color(0xFFE7E5E4)))
                }
            )
            .border(2.dp, if (filled) GoldDeep else Color(0xFFA8A29E), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = null,
            tint = if (filled) GoldDeep else Color(0xFFA8A29E),
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

@Composable
private fun ManuscriptDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, AmberLine.copy(alpha = 0.40f))))
        )
        Text("◆", color = Adobe, fontSize = 12.sp)
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(AmberLine.copy(alpha = 0.40f), Color.Transparent)))
        )
    }
}

@Composable
private fun DialogueBubble(victory: Victory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceMid)
            .border(1.dp, AmberLine.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFB93B59))
                .border(1.dp, Tertiary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    stringResource(R.string.majid_name).trimEnd(':'),
                    color = Tertiary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    stringResource(R.string.nostalgic_voice),
                    color = Color(0xFF3E4947).copy(alpha = 0.80f),
                    fontSize = 10.sp
                )
            }
            Text(
                buildAnnotatedString {
                    append("«دلبندم، دیدی بالاخره ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF005C55))) {
                        append("«${victory.startWord}»")
                    }
                    append(" رو رسوندی به ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Tertiary)) {
                        append("«${victory.endWord}»")
                    }
                    append("! میرزا از تعجب شاخ درآورد!»")
                },
                color = OnSurface,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun RewardTray(victory: Victory, landmark: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(listOf(SurfaceHigh, SurfaceVariant)))
            .border(1.dp, AmberLine.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceMid)
                .border(1.dp, AmberLine.copy(alpha = 0.50f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            CoinIcon(size = 28.dp)
        }
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "+${PersianLetters.toPersianDigits(victory.coins)}",
                    color = Adobe,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(stringResource(R.string.gold_ashrafi), color = Color(0xFF3E4947), fontSize = 12.sp)
            }
            Text(stringResource(R.string.qajar_reward, landmark), color = Color(0xFF3E4947), fontSize = 10.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Tertiary.copy(alpha = 0.10f))
                .border(1.dp, Tertiary.copy(alpha = 0.30f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(stringResource(R.string.special_reward), color = Tertiary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MetricsRow(victory: Victory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricTile(
            icon = Icons.Filled.Stairs,
            caption = stringResource(R.string.path_steps),
            value = stringResource(
                R.string.steps_vs_shortest,
                PersianLetters.toPersianDigits(victory.stepsTaken),
                PersianLetters.toPersianDigits(victory.optimalSteps)
            ),
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            icon = Icons.Filled.Timer,
            caption = stringResource(R.string.conquest_time),
            value = PersianLetters.formatSeconds(victory.elapsedMs),
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            icon = Icons.Filled.WorkspacePremium,
            caption = stringResource(R.string.fal_scroll),
            value = stringResource(if (victory.usedHelp) R.string.used_help else R.string.no_help),
            valueColor = if (victory.usedHelp) OnSurface else Tertiary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricTile(
    icon: ImageVector,
    caption: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = OnSurface
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceMid)
            .border(1.dp, AmberLine.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Turquoise, modifier = Modifier.size(18.dp))
        Text(caption, color = Color(0xFF3E4947), fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        Text(
            value,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun PressButton(
    label: String,
    icon: ImageVector,
    container: Color,
    content: Color,
    shelf: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = content
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Row(
        modifier = modifier
            .offset(y = if (pressed) 2.dp else 0.dp)
            .shadow(if (pressed) 2.dp else 4.dp, RoundedCornerShape(12.dp), ambientColor = shelf, spotColor = shelf)
            .clip(RoundedCornerShape(12.dp))
            .background(container)
            .border(1.dp, AmberLine.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = LocalGameAudio.current.wrap(onClick))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        Text(label, color = content, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
    }
}
