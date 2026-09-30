package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.audio.AudioPlayerState
import com.example.data.local.entity.AudioAffirmationEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ManifestaBrandHeader
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaChip
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.components.ManifestaLogoIcon
import com.example.ui.components.ManifestaSectionHeader
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaGlassBorderActive
import com.example.ui.theme.ManifestaMuted
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaPrimaryGradient
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioScreen(
    viewModel: MainViewModel,
    playerState: AudioPlayerState,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val user by viewModel.user.collectAsState()
    val isUserPremium = user?.isPremium ?: false
    val audioList by viewModel.audioAffirmations.collectAsState()
    val playbackProgressList by viewModel.audioPlaybackProgress.collectAsState()

    val categories = listOf("All", "Morning", "Evening", "Wealth", "DNA 528Hz", "Favorites")

    val filteredAudios = remember(audioList, selectedCategory) {
        when (selectedCategory) {
            "All" -> audioList
            "Favorites" -> audioList.filter { it.isFavorite }
            "DNA 528Hz" -> audioList.filter { it.isPremiumOnly || it.soundscape.contains("528") }
            else -> audioList.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("audio_screen")
            .background(ManifestaBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ManifestaBrandHeader(iconSize = 32.dp, fontSize = 18.sp)
        }

        // Active Player Card / Soundscape Visualizer
        item {
            ManifestaGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (playerState.isPlaying) ManifestaGlassBorderActive else ManifestaGlassBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ManifestaPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = playerState.category.uppercase(),
                                color = ManifestaAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = playerState.soundscape,
                            color = ManifestaSecondaryText,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Visualizer Sphere / Wave
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        ManifestaPrimary.copy(alpha = if (playerState.isPlaying) 0.45f else 0.2f),
                                        ManifestaSecondary.copy(alpha = if (playerState.isPlaying) 0.30f else 0.1f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ManifestaLogoIcon(size = 42.dp, animated = playerState.isPlaying)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = playerState.currentTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = ManifestaText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "“${playerState.currentAffirmation}”",
                        style = MaterialTheme.typography.bodySmall,
                        color = ManifestaSecondaryText,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio Waveform representation
                    AudioWaveformVisualizer(isPlaying = playerState.isPlaying)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Player Controls (Rewind 10, Play/Pause, Forward 10)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.audioManager.seekRelative(-10000) },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Rounded.Replay10, contentDescription = "Rewind 10s", tint = ManifestaSecondaryText)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(ManifestaPrimaryGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true),
                                    onClick = { viewModel.audioManager.togglePlayPause() }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                tint = ManifestaText,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = { viewModel.audioManager.seekRelative(10000) },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Rounded.Forward10, contentDescription = "Forward 10s", tint = ManifestaSecondaryText)
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    ManifestaChip(
                        text = cat,
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }
        }

        // Audio Catalog
        item {
            ManifestaSectionHeader(
                title = "SANCTUARY AUDIO LIBRARY",
                subtitle = "${filteredAudios.size} Guided Sessions & Frequencies"
            )
        }

        items(filteredAudios, key = { it.id }) { audio ->
            val progressItem = playbackProgressList.find { it.audioId == audio.id }
            val isPlayingThis = playerState.isPlaying && playerState.currentTitle == audio.title
            val isLocked = audio.isPremiumOnly && !isUserPremium

            AudioAffirmationCard(
                audio = audio,
                isPlaying = isPlayingThis,
                isLocked = isLocked,
                playCount = progressItem?.playCount ?: 0,
                onPlay = {
                    viewModel.playAffirmationAudio(
                        audioId = audio.id,
                        title = audio.title,
                        text = audio.affirmationText,
                        category = audio.category,
                        voiceType = audio.voiceType,
                        soundscape = audio.soundscape,
                        isPremiumOnly = audio.isPremiumOnly
                    )
                },
                onToggleFavorite = {
                    viewModel.toggleAudioFavorite(audio.id, audio.isFavorite)
                },
                onUpgrade = {
                    viewModel.navigateTo(AppScreen.Premium)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AudioAffirmationCard(
    audio: AudioAffirmationEntity,
    isPlaying: Boolean,
    isLocked: Boolean,
    playCount: Int,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onUpgrade: () -> Unit
) {
    ManifestaGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isPlaying) ManifestaGlassBorderActive else ManifestaGlassBorder,
        onClick = if (isLocked) onUpgrade else onPlay
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlaying) ManifestaPrimaryGradient
                        else Brush.linearGradient(listOf(ManifestaGlass, ManifestaGlass))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Locked - Premium",
                        tint = ManifestaAccent,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = ManifestaText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = audio.title,
                        color = ManifestaText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (audio.isPremiumOnly) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManifestaAccent.copy(alpha = 0.2f))
                                .border(1.dp, ManifestaAccent.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("528Hz PRO", color = ManifestaAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${audio.category} • ${audio.soundscape}",
                    color = ManifestaSecondaryText,
                    fontSize = 11.sp
                )

                if (playCount > 0) {
                    Text(
                        text = "Played $playCount times",
                        color = ManifestaAccent.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (audio.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (audio.isFavorite) ManifestaSecondary else ManifestaSecondaryText
                )
            }
        }
    }
}

@Composable
fun AudioWaveformVisualizer(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val waveAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_anim"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        val barWidth = 4.dp.toPx()
        val spacing = 6.dp.toPx()
        val totalBars = (size.width / (barWidth + spacing)).toInt()
        val centerY = size.height / 2

        for (i in 0 until totalBars) {
            val progress = i.toFloat() / totalBars
            val multiplier = if (isPlaying) {
                kotlin.math.sin(progress * Math.PI.toFloat() * 2 + (waveAnim * 3)).coerceIn(0.2f, 1f)
            } else {
                0.2f
            }
            val barHeight = (size.height * 0.8f) * multiplier
            val startX = i * (barWidth + spacing)

            drawLine(
                color = if (isPlaying) ManifestaSecondary else ManifestaGlassBorderActive,
                start = Offset(startX, centerY - barHeight / 2),
                end = Offset(startX, centerY + barHeight / 2),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
