package com.example.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaMuted
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaText

enum class ManifestaDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("home", "Home", Icons.Outlined.Home, "nav_home"),
    CREATE("create", "Create", Icons.Outlined.AutoAwesome, "nav_create"),
    AUDIO("audio", "Audio", Icons.Outlined.GraphicEq, "nav_audio"),
    VISION("vision", "Vision", Icons.Outlined.GridView, "nav_vision"),
    PROFILE("profile", "Profile", Icons.Outlined.Person, "nav_profile")
}

@Composable
fun ManifestaBottomNavigationBar(
    currentDestination: ManifestaDestination,
    onNavigate: (ManifestaDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ManifestaSecondaryBg)
            .drawBehind {
                // Subtle top border (rgba 255, 255, 255, 0.10)
                drawLine(
                    color = ManifestaGlassBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding()
            .height(68.dp)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ManifestaDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) ManifestaText else ManifestaMuted,
                    animationSpec = tween(250),
                    label = "nav_color"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(destination.testTag)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, radius = 26.dp, color = ManifestaPrimary.copy(alpha = 0.2f)),
                            onClick = { onNavigate(destination) }
                        )
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            // Purple/pink gradient glow behind selected icon
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                ManifestaPrimary.copy(alpha = 0.35f),
                                                ManifestaSecondary.copy(alpha = 0.15f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.title,
                            tint = if (isSelected) ManifestaSecondary else contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = destination.title,
                        color = contentColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}
