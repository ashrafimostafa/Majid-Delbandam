package com.mostafa.majiddelbandam.ui.screens.map

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.data.repository.GameRepository
import com.mostafa.majiddelbandam.domain.Neighborhood
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.PlayerProgress
import com.mostafa.majiddelbandam.domain.Puzzle
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Ashrafi
import com.mostafa.majiddelbandam.ui.theme.AshrafiDeep
import com.mostafa.majiddelbandam.ui.theme.Charcoal
import com.mostafa.majiddelbandam.ui.theme.Clay
import com.mostafa.majiddelbandam.ui.theme.GoldLeaf
import com.mostafa.majiddelbandam.ui.theme.Howz
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise
import kotlinx.coroutines.launch

private val OnSurface = Color(0xFF2C1701)
private val BannerFill = Color(0xFFF5EBD7)
private val SurfaceHigh = Color(0xFFFFE3CC)
private val LockClay = Color(0xFFECE0C8)
private val TealShelf = Color(0xFF0C4A45)

@Composable
fun NeighborhoodMapScreen(
    progress: PlayerProgress,
    repository: GameRepository,
    onPlay: (Int) -> Unit,
    onShop: () -> Unit,
    onWheel: () -> Unit,
    onDaily: () -> Unit
) {
    val currentId = progress.nextCampaignId()
    val hood = Neighborhood.of(currentId)
    val puzzle = remember(currentId) { repository.puzzle(currentId) }
    var showHelp by remember { mutableStateOf(false) }
    var showStart by remember { mutableStateOf(false) }
    val today = PersianLetters.localEpochDay()
    val wheelFree = progress.lastWheelEpochDay != today
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.courtyard),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.02f),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to OnSurface.copy(alpha = 0.45f),
                        0.35f to Color.Transparent,
                        1f to OnSurface.copy(alpha = 0.75f)
                    )
                )
        )
        Box(Modifier.fillMaxSize().background(Parchment.copy(alpha = 0.12f)))
        CourtyardAmbience()

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            MapToolbar(
                progress = progress,
                neighborhood = hood.title,
                wheelFree = wheelFree,
                onAddCoins = onShop,
                onShop = onShop,
                onDaily = onDaily,
                onWheel = onWheel,
                onSettings = { showHelp = true },
                onSound = {
                    scope.launch { repository.setSoundEnabled(!progress.soundEnabled) }
                }
            )
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                LevelTrail(
                    currentId = currentId,
                    progress = progress,
                    landmark = hood.subtitle,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onCurrent = { showStart = true },
                    onCompleted = onPlay
                )
            }
            PlayCta(
                levelId = currentId,
                landmark = hood.subtitle,
                onClick = { showStart = true }
            )
        }
    }

    if (showStart && puzzle != null) {
        LevelStartDialog(
            puzzle = puzzle,
            landmark = hood.subtitle,
            onDismiss = { showStart = false },
            onEnter = {
                showStart = false
                onPlay(currentId)
            }
        )
    }
    if (showHelp) {
        AlertDialog(
            onDismissRequest = { showHelp = false },
            confirmButton = {
                TextButton(onClick = { showHelp = false }) {
                    Text(stringResource(R.string.close))
                }
            },
            title = { Text(stringResource(R.string.how_to_play)) },
            text = { Text(stringResource(R.string.how_to_body)) }
        )
    }
}

@Composable
private fun CourtyardAmbience() {
    val motion = rememberInfiniteTransition(label = "ambience")
    val ripple by motion.animateFloat(
        0.6f, 2.4f,
        infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "ripple"
    )
    val ripple2 by motion.animateFloat(
        0.6f, 2.4f,
        infiniteRepeatable(tween(3200, 1600, FastOutSlowInEasing), RepeatMode.Restart),
        label = "ripple2"
    )
    val fly1x by motion.animateFloat(
        0f, 18f,
        infiniteRepeatable(tween(8000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1x"
    )
    val fly1y by motion.animateFloat(
        0f, -24f,
        infiniteRepeatable(tween(8000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1y"
    )
    val fly2x by motion.animateFloat(
        0f, -22f,
        infiniteRepeatable(tween(10000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2x"
    )
    val fly2y by motion.animateFloat(
        0f, -34f,
        infiniteRepeatable(tween(10000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2y"
    )
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = maxHeight * 0.28f)
                .size(width = 192.dp, height = 112.dp),
            contentAlignment = Alignment.Center
        ) {
            RippleRing(ripple)
            RippleRing(ripple2, Color(0xFF9CF2E8).copy(alpha = 0.15f))
        }
        Text(
            "🦋",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 28.dp)
                .offset { IntOffset(with(density) { fly1x.dp.roundToPx() }, with(density) { fly1y.dp.roundToPx() }) },
            fontSize = 16.sp
        )
        Text(
            "🦋",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp, bottom = 48.dp)
                .offset { IntOffset(with(density) { fly2x.dp.roundToPx() }, with(density) { fly2y.dp.roundToPx() }) },
            fontSize = 14.sp
        )
    }
}

@Composable
private fun RippleRing(scale: Float, color: Color = Color(0xFF9CF2E8).copy(alpha = 0.22f)) {
    Box(
        Modifier
            .size(width = (96 * scale).dp, height = (48 * scale).dp)
            .border(1.dp, color.copy(alpha = (1.4f - scale).coerceIn(0.05f, 0.8f)), CircleShape)
            .background(color, CircleShape)
    )
}

@Composable
private fun MapToolbar(
    progress: PlayerProgress,
    neighborhood: String,
    wheelFree: Boolean,
    onAddCoins: () -> Unit,
    onShop: () -> Unit,
    onDaily: () -> Unit,
    onWheel: () -> Unit,
    onSettings: () -> Unit,
    onSound: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AshrafiCapsule(progress.ashrafi, onAddCoins)
            TitleBadge(
                neighborhood = neighborhood,
                modifier = Modifier.weight(1f)
            )
            RoundIcon(Icons.Filled.Settings, stringResource(R.string.settings), onSettings)
            RoundIcon(
                if (progress.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                stringResource(R.string.sound),
                onSound
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuestChip(
                icon = Icons.Filled.Storefront,
                overline = stringResource(R.string.shop_day_deal),
                title = stringResource(R.string.shop_title),
                iconTint = Color(0xFF92400E),
                onClick = onShop,
                modifier = Modifier.weight(1f)
            )
            QuestChip(
                icon = Icons.Filled.School,
                overline = stringResource(R.string.daily_challenge_label),
                title = stringResource(R.string.daily_short),
                iconTint = Turquoise,
                onClick = onDaily,
                modifier = Modifier.weight(1f)
            )
            Box(Modifier.weight(1f)) {
                QuestChip(
                    icon = Icons.Outlined.Sync,
                    overline = stringResource(R.string.wheel_luck),
                    title = stringResource(R.string.wheel_short),
                    iconTint = Clay,
                    onClick = onWheel,
                    modifier = Modifier.fillMaxWidth()
                )
                if (wheelFree) {
                    val bounce by rememberInfiniteTransition(label = "free").animateFloat(
                        0f, -4f,
                        infiniteRepeatable(tween(700), RepeatMode.Reverse),
                        label = "freeY"
                    )
                    Text(
                        text = stringResource(R.string.free_today),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-4).dp, y = bounce.dp - 8.dp)
                            .clip(CircleShape)
                            .background(Clay)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AshrafiCapsule(amount: Int, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Parchment.copy(alpha = 0.95f))
            .border(1.dp, Adobe.copy(alpha = 0.3f), CircleShape)
            .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Ashrafi)
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, stringResource(R.string.add_coins), tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                PersianLetters.toPersianGrouped(amount),
                color = Color(0xFF78350F),
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                lineHeight = 16.sp
            )
        }
        Text(stringResource(R.string.ashrafi), color = Color(0xFF92400E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Filled.MonetizationOn, null, tint = AshrafiDeep, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun TitleBadge(neighborhood: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(BannerFill, SurfaceHigh)))
            .border(2.dp, AshrafiDeep.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "✨  ${stringResource(R.string.app_name)}  ✨",
            color = OnSurface,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(neighborhood, color = Color(0xFF78350F), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RoundIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Parchment.copy(alpha = 0.95f))
            .border(1.dp, Adobe.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, label, tint = OnSurface, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun QuestChip(
    icon: ImageVector,
    overline: String,
    title: String,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BannerFill.copy(alpha = 0.95f))
            .border(1.dp, AshrafiDeep.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(overline, fontSize = 9.sp, color = Color(0xFF78350F).copy(alpha = 0.6f), lineHeight = 10.sp)
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurface, maxLines = 1, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun LevelTrail(
    currentId: Int,
    progress: PlayerProgress,
    landmark: String,
    onCurrent: () -> Unit,
    onCompleted: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val older = listOfNotNull(
        (currentId - 2).takeIf { it >= 1 },
        (currentId - 1).takeIf { it >= 1 }
    )
    val newer = listOfNotNull(
        (currentId + 1).takeIf { it <= 900 },
        (currentId + 2).takeIf { it <= 900 }
    )
    BoxWithConstraints(modifier.padding(horizontal = 12.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val sx = size.width / 360f
            val sy = size.height / 480f
            val path = Path().apply {
                moveTo(90 * sx, 390 * sy)
                cubicTo(140 * sx, 370 * sy, 240 * sx, 370 * sy, 260 * sx, 300 * sy)
                cubicTo(275 * sx, 240 * sy, 160 * sx, 220 * sy, 180 * sx, 150 * sy)
                cubicTo(190 * sx, 110 * sy, 120 * sx, 80 * sy, 105 * sx, 40 * sy)
            }
            drawPath(
                path,
                Parchment.copy(alpha = 0.6f),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path,
                AshrafiDeep,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f))
                )
            )
        }
        older.getOrNull(0)?.let { id ->
            Anchored(0.18f, 0.88f) {
                CompletedNode(id, progress.starsByLevel[id] ?: 3) { onCompleted(id) }
            }
        }
        older.getOrNull(1)?.let { id ->
            Anchored(0.82f, 0.70f) {
                CompletedNode(id, progress.starsByLevel[id] ?: 3) { onCompleted(id) }
            }
        }
        Anchored(0.50f, 0.42f) {
            CurrentNode(currentId, landmark, onCurrent)
        }
        newer.getOrNull(0)?.let { id ->
            Anchored(0.76f, 0.20f) { LockedNode(id) }
        }
        newer.getOrNull(1)?.let { id ->
            Anchored(0.24f, 0.06f) { LockedNode(id) }
        }
    }
}

@Composable
private fun Anchored(xFrac: Float, yFrac: Float, content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val x = maxWidth * xFrac
        val y = maxHeight * yFrac
        Box(
            Modifier
                .offset { IntOffset(x.roundToPx(), y.roundToPx()) }
                .graphicsLayer {
                    translationX = -size.width / 2f
                    translationY = -size.height / 2f
                }
        ) { content() }
    }
}

@Composable
private fun CompletedNode(id: Int, stars: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy((-2).dp)) {
            repeat(3) { i ->
                Icon(
                    Icons.Filled.Star,
                    null,
                    tint = if (i < stars) Ashrafi else GoldLeaf.copy(alpha = 0.25f),
                    modifier = Modifier.size(if (i == 1) 14.dp else 12.dp)
                )
            }
        }
        ShelfButton(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            shelf = Color(0xFF854D0E),
            modifier = Modifier.size(56.dp),
            brush = Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.level_word), color = Color(0xFF78350F), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                Text(PersianLetters.toPersianDigits(id), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
        StatusPill(
            if (stars >= 3) stringResource(R.string.stars_count, PersianLetters.toPersianDigits(stars))
            else stringResource(R.string.completed_label)
        )
    }
}

@Composable
private fun CurrentNode(id: Int, landmark: String, onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "current").animateFloat(
        1f, 1.04f,
        infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val bounce by rememberInfiniteTransition(label = "flag").animateFloat(
        0f, -6f,
        infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "flagY"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .offset(y = bounce.dp)
                .clip(CircleShape)
                .background(Clay)
                .border(1.dp, GoldLeaf, CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Filled.Flag, null, tint = Color.White, modifier = Modifier.size(14.dp))
            Text(landmark, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(84.dp)
                .scale(pulse)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(Howz, Turquoise, TealShelf)))
                .border(4.dp, GoldLeaf, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
                Text(
                    "${stringResource(R.string.level_word)} ${PersianLetters.toPersianDigits(id)}",
                    color = GoldLeaf,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Parchment.copy(alpha = 0.95f))
                .border(1.dp, AshrafiDeep.copy(alpha = 0.4f), CircleShape)
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                stringResource(R.string.reward_plus, PersianLetters.toPersianDigits(50)),
                color = Color(0xFF78350F),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(Icons.Filled.MonetizationOn, null, tint = Ashrafi, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun LockedNode(id: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LockClay)
                .border(2.dp, Adobe.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Lock, null, tint = Color(0xFF78716C), modifier = Modifier.size(18.dp))
                Text(PersianLetters.toPersianDigits(id), color = Color(0xFF57534E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        StatusPill(stringResource(R.string.locked))
    }
}

@Composable
private fun StatusPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(top = 4.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(BannerFill.copy(alpha = 0.9f))
            .border(1.dp, Adobe.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        color = Color(0xFF78350F),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun PlayCta(levelId: Int, landmark: String, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShelfButton(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            shelf = TealShelf,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            brush = Brush.horizontalGradient(listOf(Turquoise, Color(0xFF0D9488)))
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.PlayCircle, null, tint = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.start_level, PersianLetters.toPersianDigits(levelId)),
                        color = Parchment,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        stringResource(R.string.discover_words, landmark),
                        color = Color(0xFFCCFBF1),
                        fontSize = 11.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Ashrafi.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.reward_short, PersianLetters.toPersianDigits(50)),
                        color = Color(0xFF451A03),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Icon(Icons.Filled.MonetizationOn, null, tint = Color(0xFF451A03), modifier = Modifier.size(14.dp))
                }
            }
        }
        Text(
            stringResource(R.string.proverb),
            modifier = Modifier
                .padding(top = 8.dp)
                .clip(CircleShape)
                .background(BannerFill.copy(alpha = 0.9f))
                .border(1.dp, Adobe.copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            color = Color(0xFF78350F),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LevelStartDialog(
    puzzle: Puzzle,
    landmark: String,
    onDismiss: () -> Unit,
    onEnter: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BannerFill)
                .border(2.dp, AshrafiDeep, RoundedCornerShape(28.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .align(Alignment.Start)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LockClay)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, stringResource(R.string.close), tint = Color(0xFF78350F), modifier = Modifier.size(16.dp))
                }
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(Howz, TealShelf)))
                        .border(2.dp, GoldLeaf, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Cottage, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Text(landmark, color = Color(0xFF92400E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${stringResource(R.string.level_word)} ${PersianLetters.toPersianDigits(puzzle.id)}",
                    color = OnSurface,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LockClay)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.hidden_words), color = Color(0xFF78350F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        WordStub(puzzle.startWord)
                        WordStub(stringResource(R.string.steps_needed, PersianLetters.toPersianDigits(puzzle.steps)))
                        WordStub(puzzle.endWord, highlight = true)
                    }
                }
                ShelfButton(
                    onClick = onEnter,
                    shape = RoundedCornerShape(12.dp),
                    shelf = TealShelf,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    brush = Brush.horizontalGradient(listOf(Turquoise, Turquoise))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stringResource(R.string.enter_game), color = Color.White, fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WordStub(text: String, highlight: Boolean = false) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (highlight) Color(0xFFFDE68A) else Parchment)
            .border(1.dp, if (highlight) AshrafiDeep else Adobe.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color(0xFF451A03), fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun ShelfButton(
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    shelf: Color,
    brush: Brush,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    Box(
        modifier = modifier
            .graphicsLayer { translationY = if (pressed) 3f else 0f }
            .shadow(if (pressed) 2.dp else 8.dp, shape)
            .clip(shape)
            .background(shelf)
            .padding(bottom = if (pressed) 1.dp else 4.dp)
            .clip(shape)
            .background(brush)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}
