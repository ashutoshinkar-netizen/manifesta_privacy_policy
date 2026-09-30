package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ManifestaDarkColorScheme = darkColorScheme(
    primary = ManifestaPrimary,
    onPrimary = ManifestaText,
    primaryContainer = ManifestaSurfaceElevated,
    onPrimaryContainer = ManifestaText,
    secondary = ManifestaSecondary,
    onSecondary = ManifestaText,
    secondaryContainer = ManifestaSurface,
    onSecondaryContainer = ManifestaText,
    tertiary = ManifestaAccent,
    onTertiary = ManifestaBackground,
    background = ManifestaBackground,
    onBackground = ManifestaText,
    surface = ManifestaSurface,
    onSurface = ManifestaText,
    surfaceVariant = ManifestaSecondaryBg,
    onSurfaceVariant = ManifestaSecondaryText,
    outline = ManifestaGlassBorder,
    outlineVariant = ManifestaGlassBorderActive,
    error = ManifestaError,
    onError = ManifestaText
)

@Composable
fun ManifestaTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ManifestaBackground.toArgb()
                window.navigationBarColor = ManifestaSecondaryBg.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = ManifestaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
