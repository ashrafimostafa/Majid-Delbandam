package com.mostafa.majiddelbandam.ui.screens.game

import android.content.Intent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.di.LocalAppContainer
import com.mostafa.majiddelbandam.domain.Neighborhood
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.Puzzle
import com.mostafa.majiddelbandam.domain.starsForSteps
import com.mostafa.majiddelbandam.ui.components.PersianKeyboard
import com.mostafa.majiddelbandam.ui.game.GameViewModel
import com.mostafa.majiddelbandam.ui.game.GameViewModelFactory
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Ashrafi
import com.mostafa.majiddelbandam.ui.theme.Clay
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise

private val OnSurface = Color(0xFF2C1701)
private val GoldBorder = Color(0xFFE2CDA9)
private val Connector = Color(0xFFCFBE9F)
private val AmberFace = Color(0xFFFEFAF0)
private val AmberTile = Color(0xFFFFFBEB)
private val TealFace = Color(0xFFF5FBF8)
private val CurrentFace = Color(0xFFEDF7F5)
private val RoseFace = Color(0xFFFDF2F2)
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
    var showDehkhoda by remember { mutableStateOf(false) }

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

    Box(Modifier.fillMaxSize()) {
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
                    SpeechBalloon(
                        start = puzzle.startWord,
                        end = puzzle.endWord,
                        shortest = puzzle.steps
                    )
                    LadderBoard(
                        puzzle = puzzle,
                        committed = state.committed,
                        draft = state.draft,
                        selectedIndex = state.selectedIndex,
                        hintIndex = state.hintIndex,
                        onSelect = vm::selectIndex,
                        onUndo = vm::undoStep
                    )
                    val message = when (state.message) {
                        "same" -> stringResource(R.string.tap_and_swap)
                        "one" -> stringResource(R.string.need_one_letter)
                        "dict" -> stringResource(R.string.invalid_word)
                        "coins" -> stringResource(R.string.not_enough)
                        "next" -> stringResource(R.string.step_opened)
                        else -> stringResource(R.string.tap_and_swap)
                    }
                    Text(
                        message,
                        color = if (state.message == "dict" || state.message == "one") Clay else Color(0xFF115E59),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    PowerUps(
                        onScroll = vm::useScroll,
                        onCandle = vm::useCandle,
                        onDehkhoda = { showDehkhoda = true }
                    )
                }
            }
            val last = state.committed.lastOrNull().orEmpty()
            val changed = PersianLetters.changedIndex(last, state.draft)
            val highlight = state.hintLetter ?: changed?.let { state.draft.getOrNull(it) }
            val confirmLabel = if (state.draft == last || state.draft.isEmpty()) {
                stringResource(R.string.change_one_hint)
            } else {
                stringResource(R.string.submit_word, state.draft)
            }
            PersianKeyboard(
                dimmedKeys = state.dimmedKeys,
                highlighted = highlight,
                confirmLabel = confirmLabel,
                onLetter = vm::type,
                onBackspace = vm::backspace,
                onConfirm = vm::submit,
                onHelp = { showHelp = true },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
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
    if (showDehkhoda) {
        val neighbors = vm.nearbyWords()
        AlertDialog(
            onDismissRequest = { showDehkhoda = false },
            confirmButton = {
                TextButton(onClick = { showDehkhoda = false }) {
                    Text(stringResource(R.string.close))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDehkhoda = false
                    vm.useFal()
                }) {
                    Text(stringResource(R.string.hafez_fal))
                }
            },
            title = { Text(stringResource(R.string.dehkhoda)) },
            text = {
                Text(
                    if (neighbors.isEmpty()) {
                        stringResource(R.string.dehkhoda_empty)
                    } else {
                        neighbors.joinToString("  •  ")
                    }
                )
            }
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
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Parchment.copy(alpha = 0.90f))
            .border(1.dp, GoldBorder.copy(alpha = 0.60f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFFBEB))
                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.back),
                tint = OnSurface,
                modifier = Modifier.size(20.dp)
            )
        }
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
                Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = Adobe, modifier = Modifier.size(14.dp))
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
                    .clickable(onClick = onSettings),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = Color(0xFF57534E),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SpeechBalloon(start: String, end: String, shortest: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(Ashrafi, Color(0xFFFDE68A))))
                .border(2.dp, Adobe.copy(alpha = 0.60f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("👳‍♂️", fontSize = 22.sp)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Glass)
                .border(1.dp, Color(0xFFFCD34D).copy(alpha = 0.60f), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                buildAnnotatedString {
                    append("«مجید جان دلبندم، از ")
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold,
                            background = Color(0xB3FEF3C7)
                        )
                    ) { append(start) }
                    append(" تا ")
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF9F1239),
                            fontWeight = FontWeight.Bold,
                            background = Color(0xB3FFE4E6)
                        )
                    ) { append(end) }
                    append(" هر بار فقط ")
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF115E59),
                            fontWeight = FontWeight.Bold,
                            background = Color(0xB3CCFBF1)
                        )
                    ) { append("۱ حرف") }
                    append(" را عوض کن و همین‌طور ادامه بده. کوتاه‌ترین راه ")
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF115E59),
                            fontWeight = FontWeight.Bold,
                            background = Color(0xB3CCFBF1)
                        )
                    ) { append("${PersianLetters.toPersianDigits(shortest)} گام") }
                    append(" است؛ همان را بروی سه ستاره مال توست!»")
                },
                color = OnSurface,
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LadderBoard(
    puzzle: Puzzle,
    committed: List<String>,
    draft: String,
    selectedIndex: Int,
    hintIndex: Int?,
    onSelect: (Int) -> Unit,
    onUndo: () -> Unit
) {
    val start = committed.firstOrNull() ?: puzzle.startWord
    val previous = committed.lastOrNull() ?: start
    val doneMids = if (draft == previous) committed.drop(1).dropLast(1) else committed.drop(1)
    val takenSteps = (committed.size - 1).coerceAtLeast(0)
    val shortest = puzzle.steps.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Glass)
            .border(1.dp, Color(0xFFDFCBB0), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .border(width = 0.dp, color = Color.Transparent, shape = RoundedCornerShape(0.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.Stairs, contentDescription = null, tint = Color(0xFF115E59), modifier = Modifier.size(16.dp))
                Text(stringResource(R.string.ladder_title), color = Color(0xFF78350F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Text(
                stringResource(
                    R.string.ladder_step,
                    PersianLetters.toPersianDigits(takenSteps),
                    PersianLetters.toPersianDigits(shortest)
                ),
                color = Color(0xFF78716C),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFEBDCCA))
        )
        Spacer(Modifier.height(8.dp))
        LadderRow(
            label = stringResource(R.string.source_label),
            word = start,
            previous = start,
            style = LadderStyle.Source,
            trailing = {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Adobe, modifier = Modifier.size(18.dp))
            },
            leadingIcon = Icons.Filled.Flag
        )
        ConnectorMark()
        doneMids.forEachIndexed { index, word ->
            val from = if (index == 0) start else doneMids[index - 1]
            LadderRow(
                label = stringResource(R.string.step_n, PersianLetters.toPersianDigits(index + 1)),
                word = word,
                previous = from,
                style = LadderStyle.Done,
                trailing = {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(18.dp))
                }
            )
            ConnectorMark()
        }
        LadderRow(
            label = stringResource(R.string.current_step_label),
            word = draft,
            previous = previous,
            style = LadderStyle.Current,
            selectedIndex = selectedIndex,
            hintIndex = hintIndex,
            onSelect = onSelect,
            trailing = {
                if (committed.size > 1) {
                    Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        contentDescription = stringResource(R.string.undo),
                        tint = Turquoise,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onUndo)
                    )
                } else {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = Turquoise, modifier = Modifier.size(18.dp))
                }
            }
        )
        Text(
            stringResource(R.string.next_opens),
            color = Turquoise,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 2.dp),
            textAlign = TextAlign.Center
        )
        ConnectorMark()
        LadderRow(
            label = stringResource(R.string.target_label),
            word = puzzle.endWord,
            previous = puzzle.endWord,
            style = LadderStyle.Target,
            trailing = {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFFB7185), modifier = Modifier.size(18.dp))
            },
            leadingIcon = Icons.Filled.Flag,
            leadingTint = Clay
        )
    }
}

private enum class LadderStyle { Source, Done, Current, Target }

@Composable
private fun ConnectorMark() {
    Text("✦", color = Connector, fontSize = 10.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

@Composable
private fun LadderRow(
    label: String,
    word: String,
    previous: String,
    style: LadderStyle,
    trailing: @Composable () -> Unit,
    leadingIcon: ImageVector? = null,
    leadingTint: Color = Adobe,
    selectedIndex: Int? = null,
    hintIndex: Int? = null,
    onSelect: ((Int) -> Unit)? = null
) {
    val pulse = rememberInfiniteTransition(label = "current")
    val glow by pulse.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "glow"
    )
    val rowBg = when (style) {
        LadderStyle.Source -> AmberFace
        LadderStyle.Done -> TealFace
        LadderStyle.Current -> CurrentFace
        LadderStyle.Target -> RoseFace
    }
    val borderColor = when (style) {
        LadderStyle.Source -> Color(0xFFFDE68A)
        LadderStyle.Done -> Color(0xFF99F6E4)
        LadderStyle.Current -> Turquoise
        LadderStyle.Target -> Color(0xFFFECDD3)
    }
    val labelColor = when (style) {
        LadderStyle.Source -> Color(0xFF78350F)
        LadderStyle.Done -> Color(0xFF134E4A)
        LadderStyle.Current -> Turquoise
        LadderStyle.Target -> Clay
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .border(
                width = if (style == LadderStyle.Current) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (style == LadderStyle.Current) {
                Box(
                    Modifier
                        .size(8.dp)
                        .graphicsLayer { alpha = glow }
                        .clip(CircleShape)
                        .background(Turquoise)
                )
            }
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = leadingTint, modifier = Modifier.size(14.dp))
            }
            Text(label, color = labelColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val changed = PersianLetters.changedIndex(previous, word)
            word.forEachIndexed { index, ch ->
                LetterTile(
                    letter = ch.toString(),
                    style = style,
                    changed = changed == index,
                    selected = selectedIndex == index,
                    hinted = hintIndex == index,
                    onClick = onSelect?.let { handler -> { handler(index) } }
                )
            }
        }
        Box(Modifier.padding(start = 8.dp), contentAlignment = Alignment.Center) { trailing() }
    }
}

@Composable
private fun LetterTile(
    letter: String,
    style: LadderStyle,
    changed: Boolean,
    selected: Boolean,
    hinted: Boolean,
    onClick: (() -> Unit)?
) {
    val bg = when {
        hinted -> Color(0xFFFDE68A)
        style == LadderStyle.Source -> AmberTile
        style == LadderStyle.Done && changed -> Color(0xFFF0FDFA)
        style == LadderStyle.Current && changed -> Turquoise
        style == LadderStyle.Target -> Color(0xFFFFF1F2)
        else -> Color.White
    }
    val border = when {
        hinted -> Ashrafi
        selected -> Turquoise
        style == LadderStyle.Source -> Color(0xFFFCD34D)
        style == LadderStyle.Done && changed -> Color(0xFF14B8A6)
        style == LadderStyle.Current && changed -> Turquoise
        style == LadderStyle.Current -> Color(0xFF5EEAD4)
        style == LadderStyle.Target -> Color(0xFFFDA4AF)
        else -> Color(0xFFE7E5E4)
    }
    val text = when {
        style == LadderStyle.Current && changed -> Color.White
        style == LadderStyle.Source -> Color(0xFF78350F)
        style == LadderStyle.Done && changed -> Color(0xFF134E4A)
        style == LadderStyle.Current -> Turquoise
        style == LadderStyle.Target -> Clay
        else -> Color(0xFF44403C)
    }
    val scale by animateFloatAsState(if (selected) 1.12f else 1f, label = "tile-scale")
    Box(
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(if (selected || hinted) 2.dp else 1.dp, border, RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun PowerUps(
    onScroll: () -> Unit,
    onCandle: () -> Unit,
    onDehkhoda: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PowerChip(
            icon = Icons.AutoMirrored.Filled.HelpCenter,
            iconTint = Color(0xFFB45309),
            title = stringResource(R.string.hint_scroll),
            badge = stringResource(R.string.scroll_cost),
            border = Color(0xFFFCD34D).copy(alpha = 0.70f),
            onClick = onScroll,
            modifier = Modifier.weight(1f)
        )
        PowerChip(
            icon = Icons.Filled.Whatshot,
            iconTint = Color(0xFF0F766E),
            title = stringResource(R.string.wisdom_candle),
            border = Color(0xFF5EEAD4).copy(alpha = 0.70f),
            onClick = onCandle,
            modifier = Modifier.weight(1f)
        )
        PowerChip(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            iconTint = Clay,
            title = stringResource(R.string.dehkhoda),
            border = Color(0xFFD6D3D1).copy(alpha = 0.80f),
            onClick = onDehkhoda,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PowerChip(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    border: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Glass)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
        Text(title, color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
        if (badge != null) {
            Text(
                badge,
                color = Color(0xFF92400E),
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

