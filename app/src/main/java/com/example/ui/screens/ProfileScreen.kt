package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaPrimaryGradient
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    user: UserEntity?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isGuest = user?.isGuest ?: true
    val isPremium = user?.isPremium ?: false
    val name = if (!user?.name.isNullOrBlank()) user.name else "Sanctuary Seeker"
    val email = if (!user?.email.isNullOrBlank()) user.email else "Guest Sanctuary Account"
    val userId = user?.id ?: "local_user"

    val goals by viewModel.goals.collectAsState()
    val aiHistory by viewModel.aiHistory.collectAsState()

    val joinDateStr = remember(user?.createdAt) {
        val ts = user?.createdAt ?: System.currentTimeMillis()
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        sdf.format(Date(ts))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen")
            .background(ManifestaBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ManifestaBrandHeader(iconSize = 32.dp, fontSize = 18.sp)
        }

        // Profile Identity Card (User ID, Name, Email, Avatar, Created date, Premium status)
        item {
            ManifestaGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ManifestaPrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.firstOrNull()?.uppercase() ?: "M",
                                color = ManifestaText,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ManifestaText,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isGuest) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ManifestaGlass)
                                            .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "GUEST",
                                            color = ManifestaSecondaryText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else if (isPremium) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ManifestaAccent.copy(alpha = 0.2f))
                                            .border(1.dp, ManifestaAccent.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "PRO",
                                            color = ManifestaAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall,
                                color = ManifestaSecondaryText
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Member since $joinDateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = ManifestaSecondaryText.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // User ID row with copy button (for user-specific data isolation verification)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "UID: $userId",
                            color = ManifestaSecondaryText,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Manifesta User ID", userId)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast("User ID copied")
                            },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy UID", tint = ManifestaSecondaryText, modifier = Modifier.size(14.dp))
                        }
                    }

                    if (isGuest) {
                        ManifestaButton(
                            text = "Create Account / Sign In",
                            onClick = { viewModel.openAuthModal() },
                            style = ManifestaButtonStyle.PRIMARY_GRADIENT,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "profile_save_account_button"
                        )
                    }
                }
            }
        }

        // Subscription & Premium Status Details
        item {
            ManifestaGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isPremium) ManifestaAccent.copy(alpha = 0.4f) else ManifestaGlassBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (isPremium) ManifestaAccent else ManifestaSecondaryText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPremium) "Manifesta Premium Active" else "Standard Free Sanctuary",
                                color = if (isPremium) ManifestaAccent else ManifestaText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!isPremium) {
                            ManifestaButton(
                                text = "Upgrade",
                                onClick = { viewModel.navigateTo(AppScreen.Premium) },
                                style = ManifestaButtonStyle.PRIMARY_GRADIENT,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (isPremium) {
                        Text(
                            text = "Subscription ID: ${user?.subscriptionId ?: "SUB_MFT_VIP_2026"}",
                            color = ManifestaSecondaryText,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Plan: ${user?.premiumTier ?: "PRO_ANNUAL"} • Unlimited AI & DNA Frequencies",
                            color = ManifestaText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Upgrade to Pro to unlock 528Hz DNA frequency audio, unlimited AI generation scripts, and multi-device cloud isolation.",
                            color = ManifestaSecondaryText,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Stats Grid
        item {
            Column {
                ManifestaSectionHeader(title = "STREAK & PROGRESS")
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Current Streak",
                        value = "${user?.currentStreak ?: 7}d",
                        icon = Icons.Rounded.LocalFireDepartment,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Longest Streak",
                        value = "${user?.longestStreak ?: 14}d",
                        icon = Icons.Rounded.AutoAwesome,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Rituals Done",
                        value = "${user?.ritualsCompleted ?: 12}",
                        icon = Icons.Outlined.CheckCircleOutline,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Active Goals",
                        value = "${goals.count { !it.isCompleted }}",
                        icon = Icons.Outlined.Flag,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Backend Features Navigation Links
        item {
            ManifestaGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileMenuRow(
                        title = "Manifestation Goals (${goals.size})",
                        subtitle = "Manage personal goals & target intentions",
                        icon = Icons.Outlined.Flag,
                        onClick = { viewModel.navigateToTab(ManifestaDestination.VISION) }
                    )
                    HorizontalDivider(color = ManifestaGlassBorder)
                    ProfileMenuRow(
                        title = "AI Manifestation History (${aiHistory.size})",
                        subtitle = "View generated affirmations and scripts",
                        icon = Icons.Outlined.HistoryEdu,
                        onClick = { viewModel.openHistorySheet() }
                    )
                    HorizontalDivider(color = ManifestaGlassBorder)
                    ProfileMenuRow(
                        title = "App & Audio Preferences",
                        subtitle = "Reminders, categories, voice pitch & volume",
                        icon = Icons.Outlined.Tune,
                        onClick = { viewModel.openSettingsDialog() }
                    )
                    HorizontalDivider(color = ManifestaGlassBorder)
                    ProfileMenuRow(
                        title = "Audio Rituals & Soundscapes",
                        subtitle = "432Hz ambient, rain & meditation",
                        icon = Icons.Outlined.GraphicEq,
                        onClick = { viewModel.navigateToTab(ManifestaDestination.AUDIO) }
                    )
                    HorizontalDivider(color = ManifestaGlassBorder)
                    ProfileMenuRow(
                        title = "Cloud Sync & Firestore Backup",
                        subtitle = if (isGuest) "Sign in to backup to Firebase Cloud" else "Sync affirmations & goals to Firebase Firestore",
                        icon = Icons.Outlined.CloudSync,
                        onClick = {
                            if (isGuest) {
                                viewModel.openAuthModal()
                            } else {
                                viewModel.syncAllToCloud()
                            }
                        }
                    )
                    HorizontalDivider(color = ManifestaGlassBorder)
                    ProfileMenuRow(
                        title = "Privacy & Data Isolation",
                        subtitle = "Strict local cryptographic storage & Privacy Policy",
                        icon = Icons.Outlined.Security,
                        onClick = { viewModel.openPrivacyPolicy() }
                    )
                }
            }
        }

        // Auth / Sign In / Log Out
        item {
            ManifestaButton(
                text = if (isGuest) "Sign In / Register" else "Log Out",
                onClick = {
                    if (isGuest) {
                        viewModel.openAuthModal()
                    } else {
                        viewModel.logout()
                    }
                },
                style = ManifestaButtonStyle.GLASS_OUTLINE,
                modifier = Modifier.fillMaxWidth(),
                testTag = "profile_auth_action_button"
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    ManifestaGlassCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ManifestaSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                color = ManifestaText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = ManifestaSecondaryText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = ManifestaPrimary.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ManifestaSecondaryText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    color = ManifestaText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                subtitle?.let {
                    Text(
                        text = it,
                        color = ManifestaSecondaryText,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = ManifestaSecondaryText.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
    }
}
