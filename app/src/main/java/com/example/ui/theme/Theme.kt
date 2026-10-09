package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ChronovaDarkColorScheme = darkColorScheme(
    primary = CosmicVioletLight,
    onPrimary = MidnightBg,
    primaryContainer = CosmicPurple,
    onPrimaryContainer = Color(0xFFEDE9FE),
    secondary = CelestialGold,
    onSecondary = MidnightBg,
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = StarlightCyan,
    onTertiary = MidnightBg,
    tertiaryContainer = Color(0xFF0C4A6E),
    onTertiaryContainer = Color(0xFFE0F2FE),
    background = MidnightBg,
    onBackground = TextPrimary,
    surface = MidnightSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = MidnightCardBorder,
    outlineVariant = Color(0xFF231B42)
)

private val ChronovaLightColorScheme = lightColorScheme(
    primary = CosmicPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDD6FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = CelestialAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = StarlightCyan,
    onTertiary = Color.White,
    background = LightEditorialBg,
    onBackground = LightTextPrimary,
    surface = LightEditorialSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightEditorialSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightEditorialBorder,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun ChronovaTheme(
    darkTheme: Boolean = true, // Default to stunning Midnight Cosmic theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ChronovaDarkColorScheme else ChronovaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChronovaTypography,
        content = content
    )
}
