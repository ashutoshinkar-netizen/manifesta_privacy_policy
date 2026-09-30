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
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaGlassBorderActive
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaPrimaryGradient
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText
import kotlinx.coroutines.delay

@Composable
fun RitualScreen(
    viewModel: MainViewModel,
    ritualType: String,
    onBack: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var intentionText by remember { mutableStateOf("I align my energy with peace, purpose, and deliberate focus.") }
    var reflectionText by remember { mutableStateOf("") }

    val stepTitles = listOf(
        "Breathe",
        "Set Intention",
        "Affirm",
        "Listen",
        "Visualize",
        "Reflect"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ritual_screen")
            .background(ManifestaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with back button, step progress & ritual type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = ManifestaText
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$ritualType Ritual".uppercase(),
                        color = ManifestaSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "${stepIndex + 1} / 6 • ${stepTitles[stepIndex]}",
                        color = ManifestaText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(modifier = Modifier.size(48.dp))
            }

            // Progress Line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(6) { idx ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (idx <= stepIndex) ManifestaSecondary else ManifestaGlassBorder
                            )
                    )
                }
            }

            // Step Content Center Stage
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = stepIndex,
                    transitionSpec = {
                        fadeIn(tween(350)) togetherWith fadeOut(tween(200))
                    },
                    label = "ritual_steps"
                ) { step ->
                    when (step) {
                        0 -> BreatheStep()
                        1 -> IntentionStep(
                            intention = intentionText,
                            onIntentionChange = { intentionText = it }
                        )
                        2 -> AffirmStep(
                            intention = intentionText
                        )
                        3 -> ListenStep(
                            viewModel = viewModel,
                            intention = intentionText
                        )
                        4 -> VisualizeStep()
                        5 -> JournalStep(
                            reflection = reflectionText,
                            onReflectionChange = { reflectionText = it }
                        )
                    }
                }
            }

            // Bottom Continue / Finish button
            Column(modifier = Modifier.fillMaxWidth()) {
                if (stepIndex < 5) {
                    ManifestaButton(
                        text = "Continue",
                        onClick = { stepIndex++ },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.AutoMirrored.Rounded.ArrowForward,
                        testTag = "ritual_continue_button"
                    )
                } else {
                    ManifestaButton(
                        text = "Complete Ritual ✨",
                        onClick = {
                            viewModel.completeRitual(
                                ritualType = ritualType,
                                intention = intentionText,
                                reflection = reflectionText
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Rounded.CheckCircle,
                        testTag = "ritual_complete_button"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/** Step 1: Interactive 4-4-4 Breathing Circle */
@Composable
fun BreatheStep() {
    var breathPhase by remember { mutableStateOf("Inhale...") }
    var secondsLeft by remember { mutableIntStateOf(4) }

    LaunchedEffect(Unit) {
        while (true) {
            breathPhase = "Inhale..."
            for (i in 4 downTo 1) {
                secondsLeft = i
                delay(1000)
            }
            breathPhase = "Hold..."
            for (i in 4 downTo 1) {
                secondsLeft = i
                delay(1000)
            }
            breathPhase = "Exhale..."
            for (i in 4 downTo 1) {
                secondsLeft = i
                delay(1000)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "breath_scale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Center Your Nervous System",
            style = MaterialTheme.typography.headlineSmall,
            color = ManifestaText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Synchronize your breath to clear mental noise and ground yourself.",
            style = MaterialTheme.typography.bodyMedium,
            color = ManifestaSecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.width * 0.35f) * scale

                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(
                            ManifestaPrimary.copy(alpha = 0.35f),
                            ManifestaSecondary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    center = center,
                    radius = radius * 1.3f
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(ManifestaPrimary, ManifestaSecondary)
                    ),
                    center = center,
                    radius = radius
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = breathPhase,
                    color = ManifestaText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$secondsLeft",
                    color = ManifestaText.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/** Step 2: Set Intention */
@Composable
fun IntentionStep(
    intention: String,
    onIntentionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Your Sacred Intention",
            style = MaterialTheme.typography.headlineSmall,
            color = ManifestaText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Give your energy a deliberate trajectory today.",
            style = MaterialTheme.typography.bodyMedium,
            color = ManifestaSecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = intention,
            onValueChange = onIntentionChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ManifestaSurface,
                unfocusedContainerColor = ManifestaGlass,
                focusedBorderColor = ManifestaPrimary,
                unfocusedBorderColor = ManifestaGlassBorder,
                focusedTextColor = ManifestaText,
                unfocusedTextColor = ManifestaText
            )
        )
    }
}

/** Step 3: Affirm */
@Composable
fun AffirmStep(intention: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Anchor Your Truth",
            style = MaterialTheme.typography.headlineSmall,
            color = ManifestaText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Read aloud with conviction. Feel every word in your body.",
            style = MaterialTheme.typography.bodyMedium,
            color = ManifestaSecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ManifestaPrimary.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = ManifestaSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "“$intention”",
                    color = ManifestaText,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Step 4: Listen */
@Composable
fun ListenStep(viewModel: MainViewModel, intention: String) {
    val playerState = viewModel.playerState

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Immerse in Sound",
            style = MaterialTheme.typography.headlineSmall,
            color = ManifestaText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Listen to your affirmation bathed in calming frequencies.",
            style = MaterialTheme.typography.bodyMedium,
            color = ManifestaSecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(ManifestaPrimaryGradient)
                .clickable {
                    if (playerState.value.isPlaying) {
                        viewModel.audioManager.pause()
                    } else {
                        viewModel.playAffirmationAudio(
                            title = "Ritual Alignment",
                            text = intention
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (playerState.value.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = "Toggle Audio",
                tint = ManifestaText,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (playerState.value.isPlaying) "Playing ritual audio..." else "Tap to listen",
            color = ManifestaAccent,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Step 5: Visualize */
@Composable
fun VisualizeStep() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Embody the Feeling",
            style = MaterialTheme.typography.headlineSmall,
            color = ManifestaText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Close your eyes for 60 seconds. See yourself living your intention with ease, gratitude, and confidence.",
            style = MaterialTheme.typography.bodyMedium,
            color = ManifestaSecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(ManifestaPrimary.copy(alpha = 0.35f), ManifestaSecondary.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
                .border(1.dp, ManifestaGlassBorderActive, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = ManifestaAccent,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

/** Step 6: Journal / Reflect */
@Composable
fun JournalStep(
    reflection: String,
    onReflectionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Capture Your Clarity",
            style = MaterialTheme.typography.headlineSmall,
            color = ManifestaText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "What small step or feeling came up for you during this ritual?",
            style = MaterialTheme.typography.bodyMedium,
            color = ManifestaSecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = reflection,
            onValueChange = onReflectionChange,
            placeholder = { Text("Today I will show up by...", color = ManifestaSecondaryText.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ManifestaSurface,
                unfocusedContainerColor = ManifestaGlass,
                focusedBorderColor = ManifestaPrimary,
                unfocusedBorderColor = ManifestaGlassBorder,
                focusedTextColor = ManifestaText,
                unfocusedTextColor = ManifestaText
            )
        )
    }
}
