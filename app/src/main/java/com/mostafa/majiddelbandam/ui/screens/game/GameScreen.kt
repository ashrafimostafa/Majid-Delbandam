package com.mostafa.majiddelbandam.ui.screens.game

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.focusable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.audio.LocalGameAudio
import com.mostafa.majiddelbandam.di.LocalAppContainer
import com.mostafa.majiddelbandam.domain.Neighborhood
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.Puzzle
import com.mostafa.majiddelbandam.domain.starsForSteps
import com.mostafa.majiddelbandam.ui.components.CircleBackButton
import com.mostafa.majiddelbandam.ui.components.CoinIcon
import com.mostafa.majiddelbandam.ui.components.LiquidGlass
import com.mostafa.majiddelbandam.ui.components.PersianKeyboard
import com.mostafa.majiddelbandam.ui.game.GameViewModel
import com.mostafa.majiddelbandam.ui.game.GameViewModelFactory
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Ashrafi
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise

private val OnSurface = Color(0xFF2C1701)
private val GoldBorder = Color(0xFFE2CDA9)
private val Glass = Color(0xEBFFF8F5)

@Composable
fun GameScreen(
    puzzleId: Int,
    onBack: () -> Unit,
    onMap: () -> Unit,
    onNext: (Int) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: GameViewModel = viewModel(
        key = "game-$puzzleId",
        factory = GameViewModelFactory(container.repository, puzzleId)
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val puzzle = state.puzzle
    val context = LocalContext.current
    var showHelp by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val audio = LocalGameAudio.current
    var lastCommitted by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.victory) {
        if (state.victory != null) audio.win()
    }
    LaunchedEffect(state.message) {
        when (state.message) {
            "empty", "same", "used", "length", "dict", "one" -> audio.wrong()
            else -> Unit
        }
    }
    LaunchedEffect(state.committed.size) {
        if (state.victory == null && state.committed.size > lastCommitted && state.committed.size > 1) {
            audio.correct()
        }
        lastCommitted = state.committed.size
    }

    val victory = state.victory
    if (victory != null && puzzle != null) {
        val nextId = if (puzzle.isDaily) null else (puzzle.id + 1).takeIf { it <= 900 }
        val hood = Neighborhood.of(puzzle.id)
        val landmark = if (puzzle.isDaily) stringResource(R.string.daily_maktab) else hood.subtitle
        val stageLabel = if (puzzle.isDaily) {
            stringResource(R.string.daily_maktab)
        } else {
            stringResource(R.string.stage_title, PersianLetters.toPersianDigits(puzzle.id))
        }
        VictoryDialog(
            victory = victory,
            stageLabel = stageLabel,
            landmark = landmark,
            nextLevelId = nextId,
            onNext = { if (nextId != null) onNext(nextId) else onMap() },
            onMap = onMap,
            onShare = {
                val text = context.getString(
                    R.string.share_pride,
                    stageLabel,
                    victory.startWord,
                    victory.endWord
                )
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(send, context.getString(R.string.share_honor)))
            }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp && event.key == Key.Spacebar) {
                    vm.submit()
                    true
                } else {
                    false
                }
            }
    ) {
        Image(
            painter = painterResource(R.drawable.courtyard),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Color(0xFF1C1917).copy(alpha = 0.25f)))
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            if (puzzle != null) {
                PlayHeader(
                    puzzle = puzzle,
                    ashrafi = state.ashrafi,
                    committedSteps = (state.committed.size - 1).coerceAtLeast(0),
                    onBack = onBack,
                    onSettings = { showHelp = true }
                )
            }
            val ladderScroll = rememberScrollState()
            LaunchedEffect(state.committed.size) {
                ladderScroll.animateScrollTo(ladderScroll.maxValue)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(ladderScroll)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (puzzle != null) {
                    SimpleLadder(
                        puzzle = puzzle,
                        committed = state.committed,
                        draft = state.draft,
                        onUndo = vm::undoStep
                    )
                    PowerUps(
                        candles = state.helpers.candles,
                        recommendation = state.recommendation,
                        onCandle = vm::useCandle
                    )
                }
            }
            Box {
                PersianKeyboard(
                    dimmedKeys = state.dimmedKeys,
                    highlighted = state.hintLetter,
                    confirmLabel = stringResource(R.string.space_key),
                    onLetter = vm::type,
                    onBackspace = vm::backspace,
                    onConfirm = vm::submit,
                    onHelp = { showHelp = true },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
                val snackText = when (state.message) {
                    "empty" -> stringResource(R.string.snack_empty)
                    "same" -> stringResource(R.string.snack_same)
                    "used" -> stringResource(R.string.snack_used)
                    "length" -> stringResource(R.string.snack_length)
                    "dict" -> stringResource(R.string.snack_dict)
                    "one" -> stringResource(R.string.snack_one)
                    "coins" -> stringResource(R.string.snack_coins)
                    else -> null
                }
                if (snackText != null) {
                    LaunchedEffect(state.message) {
                        delay(2800)
                        vm.clearMessage()
                    }
                    IranianSnack(
                        text = snackText,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp)
                            .offset(y = (-8).dp)
                    )
                }
            }
        }
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
private fun PlayHeader(
    puzzle: Puzzle,
    ashrafi: Int,
    committedSteps: Int,
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    val hood = Neighborhood.of(puzzle.id)
    val landmark = if (puzzle.isDaily) stringResource(R.string.daily_maktab) else hood.subtitle
    val title = if (puzzle.isDaily) {
        stringResource(R.string.daily_maktab)
    } else {
        stringResource(R.string.stage_title, PersianLetters.toPersianDigits(puzzle.id))
    }
    val stars = starsForSteps(committedSteps, puzzle.steps)
    LiquidGlass(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleBackButton(onClick = LocalGameAudio.current.wrap(onBack))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("•", color = Adobe, fontSize = 12.sp)
                Text(landmark, color = Color(0xFF44403C), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) { index ->
                    Icon(
                        imageVector = if (index < stars) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (index < stars) Ashrafi else Color(0xFFD6D3D1),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFFFBEB))))
                    .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CoinIcon(size = 14.dp)
                Text(
                    PersianLetters.toPersianGrouped(ashrafi),
                    color = Color(0xFF78350F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = LocalGameAudio.current.wrap(onSettings)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = stringResource(R.string.how_to_play),
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
    }
}

@Composable
private fun PowerUps(
    candles: Int,
    recommendation: String?,
    onCandle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PowerChip(
            icon = Icons.Filled.Whatshot,
            iconTint = Color(0xFF0F766E),
            title = stringResource(R.string.wisdom_candle),
            badge = if (candles > 0) {
                PersianLetters.toPersianDigits(candles)
            } else {
                "${PersianLetters.toPersianDigits(GameViewModel.CANDLE_PRICE)}-"
            },
            onClick = onCandle,
            modifier = Modifier.fillMaxWidth()
        )
        if (recommendation != null) {
            LiquidGlass(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    stringResource(R.string.candle_recommend, recommendation),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PowerChip(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    LiquidGlass(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp)
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = LocalGameAudio.current.wrap(onClick))
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
        Text(title, color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
        if (badge != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                CoinIcon(size = 10.dp)
                Text(
                    badge,
                    color = Color(0xFF92400E),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.sp
                )
            }
        }
    }
    }
}

