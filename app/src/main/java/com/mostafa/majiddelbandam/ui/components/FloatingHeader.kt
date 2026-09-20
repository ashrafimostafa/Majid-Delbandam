package com.mostafa.majiddelbandam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Toys
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.ui.theme.Ashrafi
import com.mostafa.majiddelbandam.ui.theme.CreamCard
import com.mostafa.majiddelbandam.ui.theme.GoldLeaf
import com.mostafa.majiddelbandam.ui.theme.IndigoInk

@Composable
fun FloatingHeader(
    ashrafi: Int,
    onShop: () -> Unit,
    onDaily: () -> Unit,
    onWheel: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(CreamCard.copy(alpha = 0.94f))
            .border(1.dp, GoldLeaf.copy(alpha = 0.7f), RoundedCornerShape(28.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AshrafiChip(ashrafi)
        HeaderIcon(Icons.Outlined.Storefront, stringResource(R.string.shop_title), onShop)
        HeaderIcon(Icons.Outlined.MenuBook, stringResource(R.string.daily_maktab), onDaily)
        HeaderIcon(Icons.Outlined.Toys, stringResource(R.string.wheel_title), onWheel)
        Box(Modifier.weight(1f))
        if (showBack && onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        } else {
            IconButton(onClick = onSettings) {
                Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings))
            }
        }
    }
}

@Composable
fun AshrafiChip(amount: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Ashrafi.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Ashrafi),
            contentAlignment = Alignment.Center
        ) {
            Text("ا", color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
        Text(
            text = PersianLetters.toPersianDigits(amount),
            style = MaterialTheme.typography.titleMedium,
            color = IndigoInk
        )
    }
}

@Composable
private fun HeaderIcon(
    image: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(image, contentDescription = label, tint = IndigoInk)
    }
}
