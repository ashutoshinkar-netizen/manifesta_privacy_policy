package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Manifesta Exact Brand Color System
val ManifestaBackground = Color(0xFF08070D)
val ManifestaSecondaryBg = Color(0xFF0D0B14)
val ManifestaSurface = Color(0xFF12101A)
val ManifestaSurfaceElevated = Color(0xFF1A1726)

val ManifestaGlass = Color(0x12FFFFFF) // rgba(255,255,255,0.07)
val ManifestaGlassBorder = Color(0x1AFFFFFF) // rgba(255,255,255,0.10)
val ManifestaGlassBorderActive = Color(0x40A855F7)

val ManifestaPrimary = Color(0xFFA855F7)
val ManifestaSecondary = Color(0xFFEC4899)
val ManifestaAccent = Color(0xFFF5C77A)

val ManifestaText = Color(0xFFFFFFFF)
val ManifestaSecondaryText = Color(0xFFA8A3B3)
val ManifestaMuted = Color(0xFF6F6A78)

val ManifestaSuccess = Color(0xFF7DD3A8)
val ManifestaError = Color(0xFFF87171)

// Gradients
val ManifestaPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(ManifestaPrimary, ManifestaSecondary)
)

val ManifestaDiagonalGradient = Brush.linearGradient(
    colors = listOf(ManifestaPrimary, ManifestaSecondary)
)

val ManifestaGlowGradient = Brush.radialGradient(
    colors = listOf(ManifestaPrimary.copy(alpha = 0.35f), Color.Transparent)
)

val ManifestaCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0x1AFFFFFF), Color(0x08FFFFFF))
)
