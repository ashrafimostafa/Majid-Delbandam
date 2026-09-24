package com.mostafa.majiddelbandam.ui.screens.shop

import android.os.Build
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.audio.LocalGameAudio
import com.mostafa.majiddelbandam.data.repository.GameRepository
import com.mostafa.majiddelbandam.data.repository.HelperType
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.PlayerProgress
import com.mostafa.majiddelbandam.ui.components.CircleBackButton
import com.mostafa.majiddelbandam.ui.components.CoinIcon
import com.mostafa.majiddelbandam.ui.components.MajidPortrait
import com.mostafa.majiddelbandam.ui.components.LiquidGlass
import com.mostafa.majiddelbandam.ui.theme.Adobe
import com.mostafa.majiddelbandam.ui.theme.Ashrafi
import com.mostafa.majiddelbandam.ui.theme.AshrafiDeep
import com.mostafa.majiddelbandam.ui.theme.Clay
import com.mostafa.majiddelbandam.ui.theme.Parchment
import com.mostafa.majiddelbandam.ui.theme.Turquoise
import kotlinx.coroutines.launch

private val OnSurface = Color(0xFF2C1701)
private val OnVariant = Color(0xFF3E4947)
private val Banner = Color(0xFFF5EBD7)
private val ClayWell = Color(0xFFECE0C8)
private val SurfaceHigh = Color(0xFFFFE3CC)
private val TealShelf = Color(0xFF0C4A45)
private val Brass = Color(0xFFB45309)
private val BrassShelf = Color(0xFF78350F)
private val Cream = Color(0xFFFCF8F0)
private val Tertiary = Color(0xFF992142)

private data class CoinOffer(
    val title: Int,
    val coins: Int,
    val toman: Int,
    val badge: Int,
    val icon: ImageVector,
    val featured: Boolean = false,
    val badgeDark: Boolean = false
)

private data class AssistOffer(
    val title: Int,
    val body: Int,
    val pack: Int,
    val price: Int,
    val count: Int,
    val type: HelperType,
    val icon: ImageVector,
    val iconTint: Color,
    val packTint: Color
)

@Composable
fun ShopScreen(
    progress: PlayerProgress,
    repository: GameRepository,
    onBack: () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val delivered = stringResource(R.string.coins_delivered)
    val notEnough = stringResource(R.string.not_enough)
    val coins = listOf(
        CoinOffer(R.string.pack_copper, 100, 10_000, R.string.special_discount, Icons.Filled.MonetizationOn),
        CoinOffer(R.string.pack_velvet, 550, 35_000, R.string.bonus_fifty, Icons.Filled.Savings),
        CoinOffer(R.string.pack_chest, 1_200, 69_000, R.string.most_popular, Icons.Filled.Inventory2, featured = true),
        CoinOffer(R.string.pack_jar, 3_000, 140_000, R.string.royal_badge, Icons.Filled.Diamond, badgeDark = true)
    )
    val assists = listOf(
        AssistOffer(
            R.string.candle_shop_title, R.string.candle_shop_body, R.string.candle_pack,
            180, 3, HelperType.CANDLE, Icons.Filled.Lightbulb, AshrafiDeep, Brass
        )
    )

    Box(Modifier.fillMaxSize()) {
        ShopBackdrop()
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            ShopHeader(ashrafi = progress.ashrafi, onClose = onBack)
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MirzaBanner()
                SectionTitle(
                    title = stringResource(R.string.coin_packs),
                    badge = stringResource(R.string.instant_delivery),
                    badgeTint = Tertiary
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    coins.chunked(2).forEach { row ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { offer ->
                                CoinTile(
                                    offer = offer,
                                    modifier = Modifier.weight(1f),
                                    onBuy = {
                                        scope.launch {
                                            repository.grantPack(offer.coins)
                                            snackbar.showSnackbar(delivered)
                                        }
                                    }
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
                DiamondRule()
                SectionTitle(
                    title = stringResource(R.string.helpers),
                    badge = stringResource(R.string.helper_tools_tag),
                    badgeTint = OnVariant,
                    diamondColor = Brass
                )
                assists.forEach { offer ->
                    AssistRow(
                        offer = offer,
                        onBuy = {
                            scope.launch {
                                val ok = repository.spendOnHelper(offer.price, offer.type, offer.count)
                                if (!ok) snackbar.showSnackbar(notEnough)
                            }
                        }
                    )
                }
                ShopFooter()
            }
        }
        SnackbarHost(
            snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        )
    }
}

@Composable
private fun ShopBackdrop() {
    Image(
        painter = painterResource(R.drawable.courtyard),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFF2C1701).copy(alpha = 0.28f),
                    0.4f to Color.Transparent,
                    1f to Color(0xFF2C1701).copy(alpha = 0.45f)
                )
            )
    )
    Canvas(Modifier.fillMaxSize()) {
        val step = 16.dp.toPx()
        var y = 0f
        while (y < size.height) {
            var x = 0f
            while (x < size.width) {
                drawCircle(Adobe.copy(alpha = 0.10f), radius = 0.75.dp.toPx(), center = Offset(x, y))
                x += step
            }
            y += step
        }
    }
}

@Composable
private fun ShopHeader(ashrafi: Int, onClose: () -> Unit) {
    LiquidGlass(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleBackButton(onClick = LocalGameAudio.current.wrap(onClose))
        Text(
            stringResource(R.string.shop_title),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = Color(0xFF005C55),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceHigh.copy(alpha = 0.6f))
                .border(1.dp, Adobe.copy(alpha = 0.15f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CoinIcon(size = 16.dp)
            Text(PersianLetters.toPersianGrouped(ashrafi), fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 12.sp)
            Text(stringResource(R.string.ashrafi), color = OnVariant.copy(alpha = 0.8f), fontSize = 11.sp)
        }
    }
    }
}

@Composable
private fun MirzaBanner() {
    LiquidGlass(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp)) {
    Box(Modifier.padding(16.dp).fillMaxWidth()) {
        Text("✦", color = AshrafiDeep.copy(alpha = 0.3f), modifier = Modifier.align(Alignment.TopEnd), fontSize = 12.sp)
        Text("✦", color = AshrafiDeep.copy(alpha = 0.3f), modifier = Modifier.align(Alignment.BottomStart), fontSize = 12.sp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                MajidPortrait(size = 88.dp)
                Text(
                    stringResource(R.string.mirza),
                    modifier = Modifier
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(Tertiary)
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    color = Banner,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.shop_wisdom), color = Tertiary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(stringResource(R.string.shop_quote), color = OnSurface, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 24.sp)
                Text(stringResource(R.string.shop_quote_sub), color = OnVariant.copy(alpha = 0.75f), fontSize = 11.sp)
            }
        }
    }
    }
}

@Composable
private fun SectionTitle(title: String, badge: String, badgeTint: Color, diamondColor: Color = Turquoise) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .size(10.dp)
                    .rotate(45f)
                    .background(diamondColor, RoundedCornerShape(1.dp))
            )
            Text(title, color = Parchment, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Text(
            badge,
            modifier = Modifier
                .clip(CircleShape)
                .background(if (badgeTint == Tertiary) Color(0xFFFFD9DD).copy(alpha = 0.6f) else Color.Transparent)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            color = if (badgeTint == Tertiary) Color(0xFFFFD9DD) else Parchment,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CoinTile(offer: CoinOffer, onBuy: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    LiquidGlass(modifier = modifier, shape = shape) {
    Box(Modifier.fillMaxWidth().padding(10.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(18.dp))
            Box(
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (offer.featured) Color(0xFFFFEADA) else ClayWell)
                    .border(1.dp, Adobe.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (offer.icon == Icons.Filled.MonetizationOn) {
                    CoinIcon(size = 32.dp)
                } else {
                    Icon(offer.icon, null, tint = if (offer.badgeDark) Tertiary else if (offer.featured) AshrafiDeep else Color(0xFFB45309), modifier = Modifier.size(28.dp))
                }
            }
            Text(stringResource(offer.title), color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                CoinIcon(size = 16.dp)
                Text(PersianLetters.toPersianGrouped(offer.coins), color = Color(0xFF005C55), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(stringResource(R.string.ashrafi), color = OnVariant, fontSize = 11.sp)
            }
            ShelfPay(
                label = PersianLetters.toPersianGrouped(offer.toman),
                unit = stringResource(R.string.toman),
                onClick = onBuy,
                brass = offer.featured,
                modifier = Modifier.fillMaxWidth()
            )
        }
        val badgeBg = when {
            offer.featured -> Brass
            offer.badgeDark -> Color(0xFF881337)
            offer.icon == Icons.Filled.Savings -> Turquoise
            else -> Color(0xFFB45309).copy(alpha = 0.15f)
        }
        val badgeFg = if (offer.featured || offer.badgeDark || offer.icon == Icons.Filled.Savings) Cream else Color(0xFF78350F)
        Text(
            stringResource(offer.badge),
            modifier = Modifier
                .align(if (offer.featured) Alignment.TopEnd else Alignment.TopStart)
                .offset(y = if (offer.featured) (-14).dp else 0.dp)
                .clip(if (offer.featured) CircleShape else RoundedCornerShape(4.dp))
                .background(badgeBg)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            color = badgeFg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
    }
}

@Composable
private fun AssistRow(offer: AssistOffer, onBuy: () -> Unit) {
    LiquidGlass(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp)) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ClayWell)
                .border(1.dp, Adobe.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(offer.icon, null, tint = offer.iconTint, modifier = Modifier.size(24.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(stringResource(offer.title), color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(stringResource(offer.body), color = OnVariant, fontSize = 11.sp, lineHeight = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    if (offer.type == HelperType.FAL) Icons.Filled.AutoAwesome else Icons.Outlined.Inventory2,
                    null,
                    tint = offer.packTint,
                    modifier = Modifier.size(12.dp)
                )
                Text(stringResource(offer.pack), color = offer.packTint, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceHigh)
                .border(1.dp, AshrafiDeep.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clickable(onClick = LocalGameAudio.current.wrap(onBuy))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CoinIcon(size = 14.dp)
            Text(PersianLetters.toPersianGrouped(offer.price), fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 13.sp)
        }
    }
    }
}

@Composable
private fun DiamondRule() {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(Modifier.width(80.dp).height(1.dp).background(Parchment.copy(alpha = 0.45f)))
        Text("  ◆  ", color = Parchment.copy(alpha = 0.8f), fontSize = 12.sp)
        Box(Modifier.width(80.dp).height(1.dp).background(Parchment.copy(alpha = 0.45f)))
    }
}

@Composable
private fun ShopFooter() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(ClayWell.copy(alpha = 0.6f))
                .border(1.dp, Adobe.copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Filled.VerifiedUser, null, tint = Turquoise, modifier = Modifier.size(15.dp))
            Text(stringResource(R.string.shop_secure), color = Parchment, fontSize = 11.sp)
        }
        Text(stringResource(R.string.shop_copyright), color = Parchment.copy(alpha = 0.8f), fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ShelfPay(
    label: String,
    unit: String,
    onClick: () -> Unit,
    brass: Boolean,
    modifier: Modifier = Modifier
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val shelf = if (brass) BrassShelf else TealShelf
    val fill = if (brass) Brass else Turquoise
    Box(
        modifier
            .graphicsLayer { translationY = if (pressed) 2f else 0f }
            .shadow(if (pressed) 1.dp else 3.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(shelf)
            .padding(bottom = if (pressed) 1.dp else 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(fill)
            .clickable(interactionSource = source, indication = null, onClick = LocalGameAudio.current.wrap(onClick))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Cream, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(unit, color = Cream.copy(alpha = 0.9f), fontSize = 10.sp)
        }
    }
}
