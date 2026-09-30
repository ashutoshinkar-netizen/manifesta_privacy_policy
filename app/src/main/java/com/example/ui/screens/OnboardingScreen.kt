package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ManifestaBrandHeader
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaText
import kotlin.math.sin

data class OnboardingStep(
    val headline: String,
    val subtext: String
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            headline = "Your mind shapes your direction.",
            subtext = "Turn your intentions into personalized affirmations and daily rituals."
        ),
        OnboardingStep(
            headline = "Make your words personal.",
            subtext = "AI creates affirmations based on what you want to attract and become."
        ),
        OnboardingStep(
            headline = "Listen. Repeat. Become.",
            subtext = "Turn your affirmations into calming audio you can return to every day."
        )
    )

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = steps[currentStepIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_screen")
            .background(ManifestaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ManifestaBrandHeader(iconSize = 28.dp, fontSize = 16.sp)

                if (currentStepIndex < 2) {
                    TextButton(
                        onClick = onFinished,
                        modifier = Modifier.testTag("skip_button")
                    ) {
                        Text(
                            text = "Skip",
                            color = ManifestaSecondaryText,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Visual Center Stage
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (currentStepIndex) {
                    0 -> GlowingGradientOrbVisual()
                    1 -> FloatingAffirmationCardsVisual()
                    2 -> AnimatedAudioWaveformVisual()
                }
            }

            // Bottom Text & Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        fadeIn(tween(350)) togetherWith fadeOut(tween(250))
                    },
                    label = "onboarding_text"
                ) { step ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = step.headline,
                            style = MaterialTheme.typography.headlineLarge,
                            color = ManifestaText,
                            textAlign = TextAlign.Center,
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = step.subtext,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ManifestaSecondaryText,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { index ->
                        val isSelected = index == currentStepIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ManifestaSecondary else ManifestaGlassBorder
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Next or CTA Button
                if (currentStepIndex < 2) {
                    ManifestaButton(
                        text = "Continue",
                        onClick = { currentStepIndex++ },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.AutoMirrored.Rounded.ArrowForward,
                        testTag = "onboarding_next_button"
                    )
                } else {
                    ManifestaButton(
                        text = "Begin Your Journey",
                        onClick = onFinished,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.AutoAwesome,
                        testTag = "onboarding_finish_button"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/** Visual 1: Abstract Glowing Gradient Orb */
@Composable
fun GlowingGradientOrbVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width * 0.38f * pulse

            // Deep atmospheric ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ManifestaPrimary.copy(alpha = 0.45f),
                        ManifestaSecondary.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.3f
                ),
                center = center,
                radius = baseRadius * 1.3f
            )

            // Inner glowing core orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        ManifestaPrimary,
                        ManifestaSecondary,
                        Color.Transparent
                    ),
                    center = center.copy(x = center.x - 15f, y = center.y - 15f),
                    radius = baseRadius
                ),
                center = center,
                radius = baseRadius
            )
        }
    }
}

/** Visual 2: Floating Affirmation Cards */
@Composable
fun FloatingAffirmationCardsVisual() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ManifestaPrimary)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "“I am aligned with calm focus and deep purpose.”",
                    color = ManifestaText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ManifestaSecondary.copy(alpha = 0.35f)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = ManifestaSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "“Every action I take compounds into abundance.”",
                    color = ManifestaText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ManifestaSecondaryText)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "“I release tension and trust my unfolding path.”",
                    color = ManifestaSecondaryText,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/** Visual 3: Animated Audio Waveform */
@Composable
fun AnimatedAudioWaveformVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barCount = 28
            val barWidth = 6.dp.toPx()
            val spacing = 5.dp.toPx()
            val totalWidth = barCount * (barWidth + spacing) - spacing
            val startX = (size.width - totalWidth) / 2f
            val centerY = size.height / 2f

            for (i in 0 until barCount) {
                val x = startX + i * (barWidth + spacing)
                val wave = sin(phase + i * 0.28f) * 0.5f + 0.5f
                val height = (30.dp.toPx() + wave * 90.dp.toPx())

                val brush = Brush.verticalGradient(
                    colors = listOf(ManifestaPrimary, ManifestaSecondary),
                    startY = centerY - height / 2f,
                    endY = centerY + height / 2f
                )

                drawLine(
                    brush = brush,
                    start = Offset(x, centerY - height / 2f),
                    end = Offset(x, centerY + height / 2f),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
