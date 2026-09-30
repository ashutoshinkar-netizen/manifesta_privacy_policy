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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.entity.ManifestationGoalEntity
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaMuted
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GoalDialog(
    goal: ManifestationGoalEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, category: String) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    var title by remember { mutableStateOf(goal?.title ?: "") }
    var description by remember { mutableStateOf(goal?.description ?: "") }
    var category by remember { mutableStateOf(goal?.category ?: "Self Growth") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Self Growth", "Wealth", "Career", "Peace", "Health", "Love", "Lifestyle")

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("goal_dialog")
    ) {
        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = ManifestaSecondaryBg,
            borderColor = ManifestaGlassBorder
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (goal != null) "Edit Manifestation Goal" else "New Manifestation Goal",
                        style = MaterialTheme.typography.titleMedium,
                        color = ManifestaText,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = ManifestaSecondaryText)
                    }
                }

                // Category chips
                Text(
                    text = "CATEGORY",
                    color = ManifestaSecondaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category.equals(cat, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ManifestaPrimary.copy(alpha = 0.35f) else ManifestaGlass)
                                .border(
                                    1.dp,
                                    if (isSelected) ManifestaSecondary else ManifestaGlassBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { category = cat }
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Goal Title (e.g. Build Dream Sanctuary)", color = ManifestaMuted, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ManifestaPrimary,
                        unfocusedBorderColor = ManifestaGlassBorder,
                        focusedTextColor = ManifestaText,
                        unfocusedTextColor = ManifestaText
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Description & Intention details...", color = ManifestaMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ManifestaPrimary,
                        unfocusedBorderColor = ManifestaGlassBorder,
                        focusedTextColor = ManifestaText,
                        unfocusedTextColor = ManifestaText
                    )
                )

                errorMessage?.let {
                    Text(text = it, color = Color(0xFFFF8A80), fontSize = 12.sp)
                }

                ManifestaButton(
                    text = if (goal != null) "Update Goal" else "Create Goal",
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "Please enter a goal title"
                            return@ManifestaButton
                        }
                        onSave(title, description, category)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "save_goal_button"
                )

                if (goal != null && onDelete != null) {
                    ManifestaButton(
                        text = "Delete Goal",
                        onClick = { onDelete(goal.id) },
                        style = ManifestaButtonStyle.GLASS_OUTLINE,
                        icon = Icons.Outlined.Delete,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "delete_goal_button"
                    )
                }
            }
        }
    }
}
