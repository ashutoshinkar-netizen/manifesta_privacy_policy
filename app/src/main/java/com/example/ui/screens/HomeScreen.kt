package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AffirmationEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ManifestaBrandHeader
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.components.ManifestaSectionHeader
import com.example.ui.navigation.ManifestaDestination
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaGlassBorderActive
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaPrimaryGradient
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    user: UserEntity?,
    recentAffirmations: List<AffirmationEntity>,
    modifier: Modifier = Modifier
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    val userName = if (!user?.name.isNullOrBlank()) user.name else "Creator"
    val streakDays = user?.currentStreak ?: 7

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
            .background(ManifestaBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Brand Header & Premium CTA if not premium
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ManifestaBrandHeader(iconSize = 34.dp, fontSize = 18.sp)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ManifestaPrimary.copy(alpha = 0.25f), ManifestaSecondary.copy(alpha = 0.25f))
                            )
                        )
                        .border(1.dp, ManifestaSecondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = ManifestaPrimary.copy(alpha = 0.2f)),
                            onClick = { viewModel.navigateTo(AppScreen.Premium) }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = ManifestaAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user?.isPremium == true) "PRO" else "UPGRADE",
                            color = ManifestaAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Greeting
        item {
            Column {
                Text(
                    text = "$greeting, $userName ✨",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ManifestaText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "What are you calling into your life today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ManifestaSecondaryText,
                    fontSize = 15.sp
                )
            }
        }

        // Hero Card: TODAY'S INTENTION
        item {
            val heroAffirmation = recentAffirmations.firstOrNull()?.primaryText
                ?: "I am becoming the person capable of creating the life I desire."

            ManifestaGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ManifestaPrimary.copy(alpha = 0.4f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    ManifestaPrimary.copy(alpha = 0.12f),
                                    ManifestaSecondary.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TODAY'S INTENTION",
                                color = ManifestaSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = ManifestaSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "“$heroAffirmation”",
                            color = ManifestaText,
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ManifestaButton(
                                text = "▶ Play affirmation",
                                onClick = {
                                    viewModel.playAffirmationAudio(
                                        title = "Today's Intention",
                                        text = heroAffirmation,
                                        category = "Morning"
                                    )
                                    viewModel.navigateToTab(ManifestaDestination.AUDIO)
                                },
                                style = ManifestaButtonStyle.PRIMARY_GRADIENT,
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                                testTag = "hero_play_button"
                            )

                            IconButton(
                                onClick = {
                                    val heroItem = recentAffirmations.firstOrNull()
                                    if (heroItem != null) {
                                        viewModel.openShareDialog(heroItem)
                                    } else {
                                        viewModel.openShareDialog(
                                            AffirmationEntity(
                                                primaryText = heroAffirmation,
                                                category = "Self Growth"
                                            )
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.IosShare,
                                    contentDescription = "Share",
                                    tint = ManifestaSecondaryText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // YOUR DAILY RITUAL
        item {
            Column {
                ManifestaSectionHeader(
                    title = "YOUR DAILY RITUAL",
                    subtitle = "Align mind, breath and intention"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RitualCard(
                        title = "Morning",
                        duration = "5 min",
                        icon = Icons.Outlined.WbSunny,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.GuidedRitual("Morning")) }
                    )
                    RitualCard(
                        title = "Afternoon",
                        duration = "3 min",
                        icon = Icons.Rounded.WbTwilight,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.GuidedRitual("Afternoon")) }
                    )
                    RitualCard(
                        title = "Night",
                        duration = "10 min",
                        icon = Icons.Rounded.NightsStay,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.GuidedRitual("Night")) }
                    )
                }
            }
        }

        // YOUR JOURNEY (Streak)
        item {
            Column {
                ManifestaSectionHeader(
                    title = "YOUR JOURNEY",
                    subtitle = "Consistency over perfection"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ManifestaGlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(ManifestaAccent.copy(alpha = 0.3f), Color.Transparent)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = ManifestaAccent,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "$streakDays day streak 🔥",
                                    color = ManifestaText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Keep showing up for yourself.",
                                    color = ManifestaSecondaryText,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weekly Activity Dots
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val days = listOf("M", "T", "W", "T", "F", "S", "S")
                            days.forEachIndexed { idx, day ->
                                val active = idx < (streakDays % 7).coerceAtLeast(4)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (active) ManifestaPrimaryGradient else Brush.linearGradient(
                                                    listOf(ManifestaGlass, ManifestaGlass)
                                                )
                                            )
                                            .border(
                                                1.dp,
                                                if (active) ManifestaGlassBorderActive else ManifestaGlassBorder,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (active) {
                                            Icon(
                                                imageVector = Icons.Rounded.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = day,
                                        color = if (active) ManifestaText else ManifestaSecondaryText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // QUICK CREATE
        item {
            Column {
                ManifestaSectionHeader(
                    title = "QUICK CREATE",
                    subtitle = "Manifest with intention"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickCreateCard(
                        title = "Affirmation",
                        icon = Icons.Rounded.AutoAwesome,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateToTab(ManifestaDestination.CREATE) }
                    )
                    QuickCreateCard(
                        title = "Audio Ritual",
                        icon = Icons.Rounded.GraphicEq,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateToTab(ManifestaDestination.AUDIO) }
                    )
                    QuickCreateCard(
                        title = "Vision Board",
                        icon = Icons.Rounded.GridView,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateToTab(ManifestaDestination.VISION) }
                    )
                }
            }
        }

        // RECENT AFFIRMATIONS
        if (recentAffirmations.isNotEmpty()) {
            item {
                ManifestaSectionHeader(
                    title = "RECENT AFFIRMATIONS",
                    actionText = "View All",
                    onActionClick = { viewModel.navigateToTab(ManifestaDestination.PROFILE) }
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(recentAffirmations) { affirmation ->
                        ManifestaGlassCard(
                            modifier = Modifier
                                .width(280.dp)
                                .height(160.dp),
                            onClick = {
                                viewModel.playAffirmationAudio(
                                    title = affirmation.category,
                                    text = affirmation.primaryText,
                                    category = affirmation.category
                                )
                                viewModel.navigateToTab(ManifestaDestination.AUDIO)
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = affirmation.category.uppercase(),
                                        color = ManifestaPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )

                                    IconButton(
                                        onClick = { viewModel.toggleFavorite(affirmation) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (affirmation.isFavorite) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Favorite",
                                            tint = if (affirmation.isFavorite) ManifestaSecondary else ManifestaSecondaryText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "“${affirmation.primaryText}”",
                                    color = ManifestaText,
                                    fontSize = 14.sp,
                                    maxLines = 3,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = ManifestaSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Tap to listen",
                                        color = ManifestaSecondaryText,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RitualCard(
    title: String,
    duration: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ManifestaGlassCard(
        modifier = modifier.height(106.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ManifestaPrimary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    color = ManifestaText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = duration,
                    color = ManifestaSecondaryText,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun QuickCreateCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ManifestaGlassCard(
        modifier = modifier.height(96.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ManifestaPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ManifestaSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = ManifestaText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
