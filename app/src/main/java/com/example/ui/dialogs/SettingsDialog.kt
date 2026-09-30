package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AppSettingsEntity
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaGlassBorderActive
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaText
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    currentSettings: AppSettingsEntity?,
    onDismiss: () -> Unit,
    onOpenPrivacyPolicy: (() -> Unit)? = null,
    onSave: (
        notificationsEnabled: Boolean,
        morningReminder: String,
        eveningReminder: String,
        categories: String,
        voicePitch: Float,
        speechRate: Float,
        defaultSoundscape: String,
        backgroundVolume: Float,
        haptic: Boolean
    ) -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(currentSettings?.notificationsEnabled ?: true) }
    var morningReminder by remember { mutableStateOf(currentSettings?.morningReminderTime ?: "08:00 AM") }
    var eveningReminder by remember { mutableStateOf(currentSettings?.eveningReminderTime ?: "09:30 PM") }

    val allCategories = listOf("Self Growth", "Wealth", "Peace", "Career", "Confidence", "Health", "Love")
    val initialSelected = currentSettings?.selectedCategoriesJson?.split(",")?.map { it.trim() }?.toSet()
        ?: setOf("Self Growth", "Wealth", "Peace")
    var selectedCategories by remember { mutableStateOf(initialSelected) }

    var voicePitch by remember { mutableFloatStateOf(currentSettings?.voicePitch ?: 1.0f) }
    var speechRate by remember { mutableFloatStateOf(currentSettings?.speechRate ?: 0.9f) }
    var defaultSoundscape by remember { mutableStateOf(currentSettings?.defaultSoundscape ?: "Ambient 432Hz") }
    var backgroundVolume by remember { mutableFloatStateOf(currentSettings?.backgroundVolume ?: 0.4f) }
    var hapticFeedback by remember { mutableStateOf(currentSettings?.hapticFeedbackEnabled ?: true) }

    val soundscapes = listOf("Ambient 432Hz", "Deep Ocean", "Binaural 528Hz", "Rain Sanctuary")

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("settings_dialog")
    ) {
        ManifestaGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            backgroundColor = ManifestaSecondaryBg,
            borderColor = ManifestaGlassBorder
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "App & Sanctuary Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = ManifestaText,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = ManifestaSecondaryText)
                    }
                }

                // 1. NOTIFICATIONS & REMINDERS
                Text(
                    text = "DAILY REMINDERS",
                    color = ManifestaSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Daily Reminder Notifications", color = ManifestaText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Receive gentle ritual alerts", color = ManifestaSecondaryText, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ManifestaText,
                            checkedTrackColor = ManifestaPrimary
                        )
                    )
                }

                if (notificationsEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ManifestaGlass)
                                .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    morningReminder = if (morningReminder == "08:00 AM") "07:00 AM" else "08:00 AM"
                                }
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "Morning Alignment", color = ManifestaSecondaryText, fontSize = 10.sp)
                                Text(text = morningReminder, color = ManifestaAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ManifestaGlass)
                                .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    eveningReminder = if (eveningReminder == "09:30 PM") "10:30 PM" else "09:30 PM"
                                }
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "Evening Reflection", color = ManifestaSecondaryText, fontSize = 10.sp)
                                Text(text = eveningReminder, color = ManifestaSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider(color = ManifestaGlassBorder)

                // 2. SELECTED MANIFESTATION CATEGORIES
                Text(
                    text = "FOCUS CATEGORIES",
                    color = ManifestaSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allCategories.forEach { cat ->
                        val isSelected = selectedCategories.contains(cat)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ManifestaPrimary.copy(alpha = 0.35f) else ManifestaGlass)
                                .border(
                                    1.dp,
                                    if (isSelected) ManifestaSecondary else ManifestaGlassBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedCategories = if (isSelected) {
                                        if (selectedCategories.size > 1) selectedCategories - cat else selectedCategories
                                    } else {
                                        selectedCategories + cat
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) ManifestaText else ManifestaSecondaryText,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                HorizontalDivider(color = ManifestaGlassBorder)

                // 3. AUDIO PREFERENCES
                Text(
                    text = "AUDIO & SOUNDSCAPE PREFERENCES",
                    color = ManifestaSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(text = "Default Soundscape: $defaultSoundscape", color = ManifestaText, fontSize = 12.sp)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    soundscapes.forEach { s ->
                        val isSelected = defaultSoundscape == s
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ManifestaGlassBorderActive else ManifestaGlass)
                                .border(1.dp, if (isSelected) ManifestaAccent else ManifestaGlassBorder, RoundedCornerShape(8.dp))
                                .clickable { defaultSoundscape = s }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = s,
                                color = if (isSelected) ManifestaText else ManifestaSecondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Speech Rate
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Voice Speech Pace", color = ManifestaSecondaryText, fontSize = 11.sp)
                        Text(text = "${(speechRate * 100).roundToInt()}%", color = ManifestaText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = { speechRate = it },
                        valueRange = 0.7f..1.3f,
                        colors = SliderDefaults.colors(
                            thumbColor = ManifestaSecondary,
                            activeTrackColor = ManifestaPrimary
                        )
                    )
                }

                // Background Volume
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Soundscape Ambience Volume", color = ManifestaSecondaryText, fontSize = 11.sp)
                        Text(text = "${(backgroundVolume * 100).roundToInt()}%", color = ManifestaText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = backgroundVolume,
                        onValueChange = { backgroundVolume = it },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = ManifestaAccent,
                            activeTrackColor = ManifestaSecondary
                        )
                    )
                }

                // Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Haptic & Tactile Feedback", color = ManifestaText, fontSize = 13.sp)
                    Switch(
                        checked = hapticFeedback,
                        onCheckedChange = { hapticFeedback = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ManifestaText,
                            checkedTrackColor = ManifestaPrimary
                        )
                    )
                }

                if (onOpenPrivacyPolicy != null) {
                    HorizontalDivider(color = ManifestaGlassBorder)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenPrivacyPolicy() }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PrivacyTip,
                                contentDescription = null,
                                tint = ManifestaAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Privacy Policy & Data Security",
                                color = ManifestaText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = ManifestaSecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                ManifestaButton(
                    text = "Save Preferences",
                    onClick = {
                        onSave(
                            notificationsEnabled,
                            morningReminder,
                            eveningReminder,
                            selectedCategories.joinToString(","),
                            voicePitch,
                            speechRate,
                            defaultSoundscape,
                            backgroundVolume,
                            hapticFeedback
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "save_settings_button"
                )
            }
        }
    }
}
