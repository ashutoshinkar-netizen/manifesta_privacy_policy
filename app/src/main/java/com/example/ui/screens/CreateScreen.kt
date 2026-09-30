package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AffirmationEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ManifestaBrandHeader
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaChip
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.components.ManifestaSectionHeader
import com.example.ui.components.ManifestaShimmerBox
import com.example.ui.navigation.ManifestaDestination
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaMuted
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText

data class CategoryItem(val name: String, val emoji: String)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var goalText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Wealth") }
    var selectedEmotion by remember { mutableStateOf("Empowered") }

    val isGenerating = viewModel.isGeneratingAffirmation.collectAsState()
    val latestBundle = viewModel.latestGeneratedBundle.collectAsState()
    val aiHistory by viewModel.aiHistory.collectAsState()

    val categories = remember {
        listOf(
            CategoryItem("Wealth", "✨"),
            CategoryItem("Career", "🚀"),
            CategoryItem("Confidence", "🦁"),
            CategoryItem("Love", "💖"),
            CategoryItem("Peace", "🌿"),
            CategoryItem("Health", "💫"),
            CategoryItem("Self Growth", "🌱")
        )
    }

    val emotions = remember {
        listOf(
            "Empowered",
            "Abundant",
            "Peaceful",
            "Magnetic",
            "Unstoppable",
            "Focused",
            "Grateful"
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("create_screen")
            .background(ManifestaBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ManifestaBrandHeader(iconSize = 32.dp, fontSize = 18.sp)

                ManifestaButton(
                    text = "Vault (${aiHistory.size})",
                    onClick = { viewModel.openHistorySheet() },
                    style = ManifestaButtonStyle.GLASS_OUTLINE,
                    icon = Icons.Outlined.HistoryEdu,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        item {
            Column {
                Text(
                    text = "What are you manifesting?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ManifestaText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your intention guides the AI to craft personal affirmations & manifestation scripts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ManifestaSecondaryText,
                    fontSize = 14.sp
                )
            }
        }

        // Input Box
        item {
            OutlinedTextField(
                value = goalText,
                onValueChange = { goalText = it },
                placeholder = {
                    Text(
                        text = "I want to attract abundance, clarity, and inner power...",
                        color = ManifestaMuted,
                        fontSize = 15.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("goal_input"),
                shape = RoundedCornerShape(18.dp),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ManifestaSurface,
                    unfocusedContainerColor = ManifestaGlass,
                    focusedBorderColor = ManifestaPrimary,
                    unfocusedBorderColor = ManifestaGlassBorder,
                    focusedTextColor = ManifestaText,
                    unfocusedTextColor = ManifestaText,
                    cursorColor = ManifestaSecondary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
        }

        // Categories
        item {
            Column {
                Text(
                    text = "Goal Category",
                    color = ManifestaSecondaryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        ManifestaChip(
                            text = cat.name,
                            emoji = cat.emoji,
                            selected = selectedCategory == cat.name,
                            onClick = { selectedCategory = cat.name }
                        )
                    }
                }
            }
        }

        // Emotions
        item {
            Column {
                Text(
                    text = "How do you want to feel?",
                    color = ManifestaSecondaryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    emotions.forEach { emotion ->
                        ManifestaChip(
                            text = emotion,
                            selected = selectedEmotion == emotion,
                            onClick = { selectedEmotion = emotion }
                        )
                    }
                }
            }
        }

        // CTA Generate Button
        item {
            val isBusy = isGenerating.value
            ManifestaButton(
                text = if (isBusy) "Manifesting With AI..." else "Create My Affirmation & Script ✨",
                onClick = {
                    focusManager.clearFocus()
                    val query = goalText.ifBlank { "Living in abundance, peace, and purpose" }
                    viewModel.generateAffirmation(query, selectedCategory, selectedEmotion)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBusy,
                testTag = "generate_button"
            )
        }

        // Shimmer Loading State
        if (isGenerating.value) {
            item {
                ManifestaGlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ManifestaShimmerBox(
                            modifier = Modifier
                                .width(120.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        ManifestaShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        ManifestaShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }

        // Generated Result Bundle Display
        latestBundle.value?.let { bundle ->
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    ManifestaGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("affirmation_card"),
                        borderColor = ManifestaPrimary.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(ManifestaSecondary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${bundle.category.uppercase()} • ${bundle.emotion.uppercase()}",
                                        color = ManifestaSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    text = "AI CRAFTED",
                                    color = ManifestaPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Primary Affirmation
                            Text(
                                text = "“${bundle.primaryText}”",
                                color = ManifestaText,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Supporting Affirmations
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ManifestaSurface)
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "SUPPORTING AFFIRMATIONS",
                                    color = ManifestaSecondaryText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(text = "• ${bundle.supporting1}", color = ManifestaText, fontSize = 13.sp)
                                Text(text = "• ${bundle.supporting2}", color = ManifestaText, fontSize = 13.sp)
                                Text(text = "• ${bundle.supporting3}", color = ManifestaText, fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Guided Manifestation Script
                            if (bundle.manifestationScript.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.Black.copy(alpha = 0.35f))
                                        .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "GUIDED MANIFESTATION SCRIPT",
                                                color = ManifestaAccent,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText("Manifestation Script", bundle.manifestationScript)
                                                    clipboard.setPrimaryClip(clip)
                                                    viewModel.showToast("Script copied to clipboard ✨")
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy script", tint = ManifestaSecondaryText, modifier = Modifier.size(15.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = bundle.manifestationScript,
                                            color = ManifestaSecondaryText,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Morning & Night rituals
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ManifestaSurface)
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.WbSunny, null, tint = ManifestaPrimary, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Morning", color = ManifestaPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(bundle.morningText, color = ManifestaSecondaryText, fontSize = 12.sp, lineHeight = 16.sp)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ManifestaSurface)
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.NightsStay, null, tint = ManifestaSecondary, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Night", color = ManifestaSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(bundle.nightText, color = ManifestaSecondaryText, fontSize = 12.sp, lineHeight = 16.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Why this affirmation?
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ManifestaGlass)
                                    .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Why this affirmation?",
                                        color = ManifestaAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = bundle.whyExplanation,
                                        color = ManifestaSecondaryText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Buttons: Save, Listen, Remix, Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ManifestaButton(
                                        text = "Save",
                                        onClick = { viewModel.saveLatestAffirmation() },
                                        style = ManifestaButtonStyle.PRIMARY_GRADIENT,
                                        icon = Icons.Rounded.Bookmark,
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        testTag = "save_button"
                                    )

                                    ManifestaButton(
                                        text = "Listen",
                                        onClick = {
                                            viewModel.playAffirmationAudio(
                                                title = bundle.shortMantra,
                                                text = bundle.primaryText,
                                                category = bundle.category
                                            )
                                            viewModel.navigateToTab(ManifestaDestination.AUDIO)
                                        },
                                        style = ManifestaButtonStyle.GLASS_OUTLINE,
                                        icon = Icons.Rounded.PlayArrow,
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        testTag = "listen_button"
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            viewModel.generateAffirmation(
                                                goal = goalText.ifBlank { "Living in abundance, peace, and purpose" },
                                                category = selectedCategory,
                                                emotion = selectedEmotion
                                            )
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Outlined.Refresh, contentDescription = "Remix", tint = ManifestaSecondaryText)
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.openShareDialog(
                                                AffirmationEntity(
                                                    primaryText = bundle.primaryText,
                                                    supporting1 = bundle.supporting1,
                                                    supporting2 = bundle.supporting2,
                                                    supporting3 = bundle.supporting3,
                                                    shortMantra = bundle.shortMantra,
                                                    morningText = bundle.morningText,
                                                    nightText = bundle.nightText,
                                                    whyExplanation = bundle.whyExplanation,
                                                    category = bundle.category
                                                )
                                            )
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Outlined.IosShare, contentDescription = "Share", tint = ManifestaSecondaryText)
                                    }
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
