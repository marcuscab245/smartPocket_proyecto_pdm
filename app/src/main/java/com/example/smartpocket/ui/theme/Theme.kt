package com.example.smartpocket.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LavenderAccent,
    onPrimary = DeepBlack,
    secondary = LavenderText,
    onSecondary = DeepBlack,
    background = DeepBlack,
    surface = DeepBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = CardGrey,
    onSurfaceVariant = SecondaryText,
    outline = BorderGrey,
    error = InnecesarioRed,
    errorContainer = InnecesarioRedContainer,
    onErrorContainer = InnecesarioRed
)

@Composable
fun SmartPocketTheme(
    content: @Composable () -> Unit
) {
    // Forzamos el tema oscuro para mantener la estética Beta Stitch
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
