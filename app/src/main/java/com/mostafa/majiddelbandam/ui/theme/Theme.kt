package com.mostafa.majiddelbandam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val PersianLightScheme = lightColorScheme(
    primary = Turquoise,
    onPrimary = Parchment,
    secondary = Ashrafi,
    onSecondary = Charcoal,
    tertiary = Clay,
    background = Parchment,
    onBackground = Charcoal,
    surface = CreamCard,
    onSurface = IndigoInk,
    surfaceVariant = PaperEdge,
    onSurfaceVariant = Mist,
    outline = GoldLeaf,
    error = Geranium
)

@Composable
fun MajidDelbandamTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = PersianLightScheme,
            typography = MajidDelbandamTypography,
            content = content
        )
    }
}
