package com.mostafa.majiddelbandam.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mostafa.majiddelbandam.R

@Composable
fun CoinIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    contentDescription: String? = null
) {
    Image(
        painter = painterResource(R.drawable.ic_coin),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}
