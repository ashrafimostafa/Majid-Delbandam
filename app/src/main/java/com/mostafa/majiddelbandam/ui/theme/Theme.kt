package com.mostafa.majiddelbandam.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Rose,
    onPrimary = Night,
    background = Night,
    onBackground = Cream,
    surface = NightRaised,
    onSurface = Cream
)

private val LightScheme = lightColorScheme(
    primary = RoseDeep,
    onPrimary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Cloud,
    onSurface = Ink
)

@Composable
fun MajidDelbandamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = MajidDelbandamTypography,
        content = content
    )
}
