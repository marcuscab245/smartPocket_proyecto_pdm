package com.example.smartpocket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    secondary = ForestGreen,
    onSecondary = White,
    background = LightMint,
    surface = White,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal
)

private val DarkColorScheme = darkColorScheme(
    primary = LightMint,
    onPrimary = DarkCharcoal,
    secondary = PrimaryGreen,
    onSecondary = White,
    background = DarkCharcoal,
    surface = ForestGreen,
    onBackground = LightMint,
    onSurface = LightMint
)

@Composable
fun SmartPocketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}