package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaText

/**
 * Manifesta Logo:
 * Minimal flowing "M" combined with a delicate four-pointed star sparkle.
 * Purple -> Pink gradient with soft glow.
 */
@Composable
fun ManifestaLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    animated: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_anim")
    val pulseAlpha by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.7f) }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val width = this.size.width
            val height = this.size.height

            val gradientBrush = Brush.linearGradient(
                colors = listOf(ManifestaPrimary, ManifestaSecondary),
                start = Offset(0f, height * 0.2f),
                end = Offset(width, height * 0.8f)
            )

            // Outer soft atmospheric glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ManifestaPrimary.copy(alpha = 0.28f * pulseAlpha),
                        ManifestaSecondary.copy(alpha = 0.12f * pulseAlpha),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, height * 0.5f),
                    radius = width * 0.65f
                ),
                radius = width * 0.65f
            )

            // Flowing geometric-organic "M"
            val strokeWidth = (width * 0.088f).coerceAtLeast(2.5f)
            val path = Path().apply {
                // Left leg starting with gentle upward curve
                moveTo(width * 0.18f, height * 0.76f)
                cubicTo(
                    width * 0.20f, height * 0.50f,
                    width * 0.24f, height * 0.28f,
                    width * 0.36f, height * 0.28f
                )
                // Flowing down to center valley
                cubicTo(
                    width * 0.44f, height * 0.28f,
                    width * 0.48f, height * 0.58f,
                    width * 0.52f, height * 0.58f
                )
                // Flowing up to right peak
                cubicTo(
                    width * 0.56f, height * 0.58f,
                    width * 0.62f, height * 0.28f,
                    width * 0.72f, height * 0.28f
                )
                // Right leg descending smoothly
                cubicTo(
                    width * 0.80f, height * 0.28f,
                    width * 0.82f, height * 0.55f,
                    width * 0.84f, height * 0.76f
                )
            }

            drawPath(
                path = path,
                brush = gradientBrush,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Tiny four-pointed sparkle/star in upper right quadrant
            val starCenterX = width * 0.84f
            val starCenterY = height * 0.22f
            val starRadius = width * 0.13f
            val starInner = starRadius * 0.25f

            val starPath = Path().apply {
                moveTo(starCenterX, starCenterY - starRadius) // top
                quadraticTo(starCenterX, starCenterY, starCenterX + starInner, starCenterY)
                lineTo(starCenterX + starRadius, starCenterY) // right
                quadraticTo(starCenterX, starCenterY, starCenterX, starCenterY + starInner)
                lineTo(starCenterX, starCenterY + starRadius) // bottom
                quadraticTo(starCenterX, starCenterY, starCenterX - starInner, starCenterY)
                lineTo(starCenterX - starRadius, starCenterY) // left
                quadraticTo(starCenterX, starCenterY, starCenterX, starCenterY - starInner)
                close()
            }

            drawPath(
                path = starPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, ManifestaSecondary),
                    center = Offset(starCenterX, starCenterY),
                    radius = starRadius
                )
            )
        }
    }
}

@Composable
fun ManifestaBrandHeader(
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp,
    fontSize: TextUnit = 20.sp,
    showTagline: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ManifestaLogoIcon(size = iconSize)
        Spacer(modifier = Modifier.width(10.dp))
        androidx.compose.foundation.layout.Column {
            Text(
                text = "MANIFESTA",
                color = ManifestaText,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp
            )
            if (showTagline) {
                Text(
                    text = "Think it. Feel it. Become it.",
                    color = com.example.ui.theme.ManifestaSecondaryText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
