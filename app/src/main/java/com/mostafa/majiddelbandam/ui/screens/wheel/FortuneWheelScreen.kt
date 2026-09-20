package com.mostafa.majiddelbandam.ui.screens.wheel

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.audio.LocalGameAudio
import com.mostafa.majiddelbandam.data.repository.GameRepository
import com.mostafa.majiddelbandam.data.repository.HelperType
import com.mostafa.majiddelbandam.data.repository.WheelPrize
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.PlayerProgress
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Ashrafi
import com.mostafa.majiddelbandam.ui.theme.AshrafiDeep
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import java.util.Calendar

private val OnSurface = Color(0xFF2C1701)
private val OnVariant = Color(0xFF3E4947)
private val SurfaceHigh = Color(0xFFFFE3CC)
private val SurfaceLow = Color(0xFFFFF1E7)
private val Banner = Color(0xFFF5EBD7)
private val TealShelf = Color(0xFF0C4A45)
private val Cream = Color(0xFFFCF8F0)
private val Tertiary = Color(0xFF992142)
private val PrimaryDark = Color(0xFF005C55)
private val Cobalt = Color(0xFF1D3989)
private val Pomegranate = Color(0xFFBE123C)
private val AdobeBrown = Color(0xFF78350F)

private data class WheelSlice(
    val prize: WheelPrize,
    val label: String,
    val emoji: String,
    val phrase: String,
    val fill: Color,
    val stroke: Color,
    val text: Color
)

private val slices = listOf(
    WheelSlice(WheelPrize.Coins(50, "۵۰ اشرفی"), "۵۰ اشرفی", "●", "مبارکه دلبندم! ۵۰ اشرفی ناب به کیسه‌ات ریخته شد، خرج شیرینی نکنی همشو!", Color(0xFFD97706), Color(0xFFFEF3C7), Color.White),
    WheelSlice(WheelPrize.Helper(HelperType.SCROLL, "طومار راهنما"), "طومار راهنما", "📜", "به‌به! یک طومار رموز کهن نصیبت شد؛ گره از دشوارترین کلمات می‌گشاید.", Color(0xFFF5EBD7), Color(0xFFD97706), AdobeBrown),
    WheelSlice(WheelPrize.Coins(100, "۱۰۰ اشرفی"), "۱۰۰ اشرفی", "●", "دست مریزاد! ۱۰۰ سکه زرین اشرفی؛ بخت امروزت واقعاً مثل خورشید درخشید!", Color(0xFF0F766E), Color(0xFF9CF2E8), Color.White),
    WheelSlice(WheelPrize.Helper(HelperType.CANDLE, "شمع دانایی"), "شمع دانایی", "🕯️", "شمع شب‌افروز دانایی تقدیم تو! تاریکی جهل رو بسوزون دلبندم.", Pomegranate, Color(0xFFFFD9DD), Color.White),
    WheelSlice(WheelPrize.Empty, "لبخند مجید", "🎭", "دلبندم فدای سرت! قسمت نبود این نوبت؛ ولی دلت شاد و لبت خندون باشه همیشه!", AdobeBrown, Color(0xFFFCD34D), Color(0xFFFDE68A)),
    WheelSlice(WheelPrize.Coins(500, "خمره طلا!"), "خمره طلا!", "🏺", "ای والله! بار و بندیل ببند که خمره طلا رو بُردی دلبندم! ۵۰۰ اشرفی تمام!", Color(0xFFF59E0B), Color.White, Color.White),
    WheelSlice(WheelPrize.Helper(HelperType.FAL, "فال حافظ"), "فال حافظ", "📖", "یوسف گمگشته بازآید به کنعان غم مخور... بخت با تو یار است عزیز دل!", Cobalt, Color(0xFFB6C4FF), Color.White),
    WheelSlice(WheelPrize.Coins(20, "۲۰ اشرفی"), "۲۰ اشرفی", "●", "قطره قطره جمع گردد وانگهی دریا شود! ۲۰ اشرفی نوش جانت.", Color(0xFF0F766E), Color(0xFF9CF2E8), Color.White)
)

private val WheelEasing = CubicBezierEasing(0.12f, 0.8f, 0.15f, 1f)

@Composable
fun FortuneWheelScreen(
    progress: PlayerProgress,
    repository: GameRepository,
    onBack: () -> Unit
) {
    val today = PersianLetters.localEpochDay()
    val alreadySpun = progress.lastWheelEpochDay == today
    val rotation = remember { Animatable(0f) }
    var spinning by remember { mutableStateOf(false) }
    var claimed by remember { mutableStateOf(alreadySpun) }
    val idle = stringResource(R.string.wheel_idle_speech)
    val spinningLine = stringResource(R.string.wheel_spinning_speech)
    var speech by remember { mutableStateOf(idle) }
    val scope = rememberCoroutineScope()
    val blessingDay = remember {
        PersianLetters.toPersianDigits(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }
    var remainMs by remember { mutableLongStateOf(millisUntilMidnight()) }
    LaunchedEffect(alreadySpun) {
        while (isActive) {
            remainMs = millisUntilMidnight()
            delay(1000)
        }
    }

    Box(Modifier.fillMaxSize()) {
        WheelBackdrop()
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            WheelHeader(ashrafi = progress.ashrafi, onBack = onBack)
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFFFEADA).copy(alpha = 0.8f))
                        .border(1.dp, AshrafiDeep.copy(alpha = 0.25f), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, null, tint = Tertiary, modifier = Modifier.size(16.dp))
                    Text(
                        stringResource(R.string.wheel_ribbon, blessingDay),
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Icon(Icons.Filled.AutoAwesome, null, tint = Tertiary, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.height(16.dp))
                ShamsehWheel(rotationDegrees = rotation.value)
                Spacer(Modifier.height(20.dp))
                SpinButton(
                    spinning = spinning,
                    claimed = claimed || alreadySpun,
                    onClick = {
                        if (spinning || claimed || alreadySpun) return@SpinButton
                        val index = Random.nextInt(slices.size)
                        val slice = slices[index]
                        val sliceAngle = 45f
                        val landing = (360f - (index * sliceAngle + 22.5f) + 360f) % 360f
                        val extraTurns = 5 + Random.nextInt(3)
                        val target = rotation.value + extraTurns * 360f +
                            ((landing - (rotation.value % 360f) + 360f) % 360f)
                        spinning = true
                        speech = spinningLine
                        scope.launch {
                            rotation.animateTo(
                                target,
                                tween(durationMillis = 4500, easing = WheelEasing)
                            )
                            repository.applyWheelPrize(slice.prize)
                            speech = "«${slice.phrase}»"
                            spinning = false
                            claimed = true
                        }
                    }
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.HourglassTop, null, tint = AdobeBrown, modifier = Modifier.size(16.dp))
                    Text(stringResource(R.string.next_free_turn), color = OnVariant, fontSize = 13.sp)
                    Text(
                        formatCountdown(remainMs),
                        color = Tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
                MajidKativeh(speech)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WheelBackdrop() {
    val blurMod = if (Build.VERSION.SDK_INT >= 31) Modifier.blur(2.dp) else Modifier
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFDCE1FF).copy(alpha = 0.4f), SurfaceHigh.copy(alpha = 0.6f), Parchment)
                )
            )
    )
    Image(
        painter = painterResource(R.drawable.courtyard),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .then(blurMod)
            .graphicsLayer { alpha = 0.25f },
        contentScale = ContentScale.Crop
    )
    Canvas(Modifier.fillMaxSize()) {
        val step = 24.dp.toPx()
        var y = 0f
        var row = 0
        while (y < size.height) {
            var x = if (row % 2 == 0) 0f else step / 2f
            while (x < size.width) {
                drawCircle(AshrafiDeep.copy(alpha = 0.12f), 0.6.dp.toPx(), Offset(x, y))
                x += step
            }
            y += step
            row++
        }
    }
    Box(
        Modifier
            .padding(top = 0.dp, start = 0.dp)
            .size(280.dp)
            .graphicsLayer { rotationZ = 12f }
            .background(
                Brush.radialGradient(listOf(Color(0xFFFDE68A).copy(alpha = 0.35f), Color.Transparent)),
                CircleShape
            )
    )
}

@Composable
private fun WheelHeader(ashrafi: Int, onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Parchment.copy(alpha = 0.80f))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEADA).copy(alpha = 0.6f))
                .border(1.dp, Color(0xFFBDC9C6).copy(alpha = 0.4f), CircleShape)
                .clickable(onClick = LocalGameAudio.current.wrap(onBack)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, stringResource(R.string.back), tint = PrimaryDark)
        }
        Text(
            stringResource(R.string.wheel_title),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = PrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceHigh.copy(alpha = 0.8f))
                .border(1.dp, AshrafiDeep.copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Filled.MonetizationOn, null, tint = AshrafiDeep, modifier = Modifier.size(16.dp))
            Text(PersianLetters.toPersianGrouped(ashrafi), fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ShamsehWheel(rotationDegrees: Float) {
    val context = LocalContext.current
    val wheelTypeface = remember {
        ResourcesCompat.getFont(context, R.font.vazirmatn_bold) ?: Typeface.DEFAULT_BOLD
    }
    val rimSpin by rememberInfiniteTransition(label = "rim").animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(90_000, easing = LinearEasing), RepeatMode.Restart),
        label = "rimRot"
    )
    Box(
        Modifier.size(320.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(SurfaceLow)
                .border(4.dp, AshrafiDeep.copy(alpha = 0.3f), CircleShape)
        )
        Canvas(
            Modifier
                .fillMaxSize()
                .padding(4.dp)
                .rotate(rimSpin)
        ) {
            drawCircle(
                color = PrimaryDark.copy(alpha = 0.4f),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f))
                )
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(10.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(SurfaceHigh, Color(0xFFFFEADA))))
                .border(4.dp, Color(0xFFFFDCBD), CircleShape)
        )
        Canvas(
            Modifier
                .size(288.dp)
                .shadow(16.dp, CircleShape)
        ) {
            val sweep = 45f
            rotate(rotationDegrees) {
                slices.forEachIndexed { index, slice ->
                    val start = -90f + index * sweep
                    drawArc(
                        color = slice.fill,
                        startAngle = start,
                        sweepAngle = sweep,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    drawArc(
                        color = slice.stroke,
                        startAngle = start,
                        sweepAngle = sweep,
                        useCenter = true,
                        size = Size(size.width, size.height),
                        style = Stroke(width = 3.dp.toPx())
                    )
                    val mid = Math.toRadians((start + sweep / 2.0))
                    val labelR = size.minDimension * 0.32f
                    val lx = center.x + labelR * cos(mid).toFloat()
                    val ly = center.y + labelR * sin(mid).toFloat()
                    drawContext.canvas.nativeCanvas.apply {
                        save()
                        rotate((start + sweep / 2f + 90f), lx, ly)
                        val paint = Paint().apply {
                            color = android.graphics.Color.argb(
                                255,
                                (slice.text.red * 255).toInt(),
                                (slice.text.green * 255).toInt(),
                                (slice.text.blue * 255).toInt()
                            )
                            textAlign = Paint.Align.CENTER
                            textSize = 28f
                            typeface = wheelTypeface
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                        drawText(slice.label, lx, ly, paint)
                        val emojiPaint = Paint(paint).apply {
                            textSize = 26f
                            typeface = Typeface.DEFAULT
                        }
                        drawText(slice.emoji, lx, ly + 28f, emojiPaint)
                        restore()
                    }
                }
            }
            drawCircle(OnSurface, 42.dp.toPx(), center, style = Stroke(3.dp.toPx()))
            drawCircle(OnSurface, 42.dp.toPx(), center)
            drawCircle(Turquoise, 32.dp.toPx(), center)
            drawCircle(Color(0xFFFEF3C7), 32.dp.toPx(), center, style = Stroke(2.dp.toPx()))
            drawCircle(Ashrafi, 16.dp.toPx(), center)
            drawCircle(AdobeBrown, 16.dp.toPx(), center, style = Stroke(2.dp.toPx()))
            drawCircle(Parchment, 6.dp.toPx(), center)
        }
        Pointer(Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun Pointer(modifier: Modifier = Modifier) {
    Canvas(
        modifier.size(width = 42.dp, height = 48.dp)
    ) {
        val needle = Path().apply {
            moveTo(size.width / 2f, 2.dp.toPx())
            lineTo(size.width - 4.dp.toPx(), size.height * 0.68f)
            quadraticTo(size.width / 2f, size.height - 2.dp.toPx(), 4.dp.toPx(), size.height * 0.68f)
            close()
        }
        drawPath(needle, Adobe)
        drawPath(needle, Color(0xFFFDE68A), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(Turquoise, 6.dp.toPx(), Offset(size.width / 2f, size.height * 0.68f))
        drawCircle(Color(0xFFFEF3C7), 6.dp.toPx(), Offset(size.width / 2f, size.height * 0.68f), style = Stroke(1.5.dp.toPx()))
    }
}

@Composable
private fun SpinButton(spinning: Boolean, claimed: Boolean, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val enabled = !spinning && !claimed
    val fill = if (claimed) Color(0xFFFFDCBD) else PrimaryDark
    val shelf = if (claimed) AdobeBrown.copy(alpha = 0.4f) else TealShelf
    val content = if (claimed) OnVariant else Color.White
    Box(
        Modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = if (pressed && enabled) 3f else 0f }
            .shadow(if (pressed) 1.dp else 4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(shelf)
            .padding(bottom = if (pressed && enabled) 1.dp else 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(fill)
            .clickable(enabled = enabled, interactionSource = source, indication = null, onClick = LocalGameAudio.current.wrap(onClick))
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                if (claimed) Icons.Filled.CheckCircle else Icons.AutoMirrored.Filled.RotateRight,
                null,
                tint = content,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                if (claimed) stringResource(R.string.prize_received) else stringResource(R.string.spin),
                color = content,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            if (enabled) {
                Spacer(Modifier.size(8.dp))
                Text(
                    stringResource(R.string.free_today),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Turquoise)
                        .border(1.dp, Color(0xFF9CF2E8).copy(alpha = 0.4f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = Color(0xFFA3FAEF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MajidKativeh(speech: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFEADA))
            .border(1.dp, AdobeBrown.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            "❦",
            color = AshrafiDeep.copy(alpha = 0.15f),
            fontSize = 36.sp,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceHigh)
                    .border(2.dp, Turquoise.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text("👳‍♂️", fontSize = 22.sp, modifier = Modifier.padding(bottom = 4.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Tertiary)
                        .align(Alignment.BottomCenter)
                )
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.majid_name), color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Text(
                        stringResource(R.string.majid_role),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFDCBD))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                        color = OnVariant.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
                Text(speech, color = OnSurface, fontSize = 13.sp, lineHeight = 20.sp)
            }
        }
    }
}

private fun millisUntilMidnight(): Long {
    val now = Calendar.getInstance()
    val next = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return (next.timeInMillis - now.timeInMillis).coerceAtLeast(0)
}

private fun formatCountdown(ms: Long): String {
    val total = (ms / 1000).coerceAtLeast(0)
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    fun two(n: Long) = PersianLetters.toPersianDigits(n.toString().padStart(2, '0'))
    return "${two(h)}:${two(m)}:${two(s)}"
}
