package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LeafGreen,
    onPrimary = Color.Black,
    primaryContainer = ButtonGreen,
    onPrimaryContainer = Color.White,
    secondary = HillGreen,
    onSecondary = Color.Black,
    tertiary = SagePanel,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextMuted,
    outline = Color(0xFF3B5242)
)

private val LightColorScheme = lightColorScheme(
    primary = ButtonGreen,
    onPrimary = Color.White,
    primaryContainer = SagePanel,
    onPrimaryContainer = HeadingsLogo,
    secondary = LeafGreen,
    onSecondary = Color.White,
    tertiary = HillGreen,
    background = AgrigradeBackground,
    onBackground = TextDark,
    surface = LightLayer,
    onSurface = TextDark,
    surfaceVariant = SurfaceCream,
    onSurfaceVariant = TextMuted,
    outline = CardBorder
)

@Composable
fun AgrigradeTheme(
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

// Keep backward compatibility alias if needed
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AgrigradeTheme(darkTheme = darkTheme, content = content)
}
