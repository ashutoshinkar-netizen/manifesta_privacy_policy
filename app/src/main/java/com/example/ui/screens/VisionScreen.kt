package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ManifestationGoalEntity
import com.example.data.local.entity.VisionCardEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ManifestaBrandHeader
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaChip
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaMuted
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText

private enum class VisionTab {
    VISION_BOARD,
    GOALS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VisionScreen(
    viewModel: MainViewModel,
    visionCards: List<VisionCardEntity>,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(VisionTab.VISION_BOARD) }
    var showAddVisionDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val goals by viewModel.goals.collectAsState()

    val categories = listOf("All", "Self Growth", "Wealth", "Career", "Travel", "Home", "Health", "Love")

    val filteredCards = remember(visionCards, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") visionCards
        else visionCards.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    val filteredGoals = remember(goals, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") goals
        else goals.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("vision_screen")
            .background(ManifestaBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ManifestaBrandHeader(iconSize = 28.dp, fontSize = 16.sp)

            ManifestaButton(
                text = if (activeTab == VisionTab.VISION_BOARD) "+ Vision" else "+ New Goal",
                onClick = {
                    if (activeTab == VisionTab.VISION_BOARD) {
                        showAddVisionDialog = true
                    } else {
                        viewModel.openGoalDialog(null)
                    }
                },
                style = ManifestaButtonStyle.PRIMARY_GRADIENT,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                testTag = "add_vision_or_goal_button"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Switcher Tab: Vision Board vs Goals
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ManifestaGlass)
                .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeTab == VisionTab.VISION_BOARD) ManifestaPrimary.copy(alpha = 0.35f) else Color.Transparent)
                    .clickable { activeTab = VisionTab.VISION_BOARD }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vision Board (${visionCards.size})",
                    color = if (activeTab == VisionTab.VISION_BOARD) ManifestaText else ManifestaSecondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeTab == VisionTab.GOALS) ManifestaPrimary.copy(alpha = 0.35f) else Color.Transparent)
                    .clickable { activeTab = VisionTab.GOALS }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Manifestation Goals (${goals.size})",
                    color = if (activeTab == VisionTab.GOALS) ManifestaText else ManifestaSecondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(5).forEach { cat ->
                ManifestaChip(
                    text = cat,
                    selected = selectedCategoryFilter == cat,
                    onClick = { selectedCategoryFilter = cat }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content
        if (activeTab == VisionTab.VISION_BOARD) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredCards, key = { it.id }) { card ->
                    VisionCardItem(
                        card = card,
                        onDelete = { viewModel.deleteVisionCard(card.id) }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        } else {
            // GOALS LIST (CRUD with User Isolation)
            if (filteredGoals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No manifestation goals yet ✨",
                            color = ManifestaText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Add your first intention to track your manifestation journey.",
                            color = ManifestaSecondaryText,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ManifestaButton(
                            text = "Create Goal",
                            onClick = { viewModel.openGoalDialog(null) },
                            style = ManifestaButtonStyle.PRIMARY_GRADIENT
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredGoals, key = { it.id }) { goal ->
                        GoalItemCard(
                            goal = goal,
                            onToggleComplete = { viewModel.toggleGoalCompletion(goal) },
                            onEdit = { viewModel.openGoalDialog(goal) },
                            onDelete = { viewModel.deleteGoal(goal.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }

    if (showAddVisionDialog) {
        AddVisionSheet(
            onDismiss = { showAddVisionDialog = false },
            onAdd = { title, goal, category, affirmation ->
                viewModel.addVisionCard(title, goal, category, affirmation)
                showAddVisionDialog = false
            }
        )
    }
}

@Composable
fun GoalItemCard(
    goal: ManifestationGoalEntity,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ManifestaGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (goal.isCompleted) Color(0xFF7DD3A8).copy(alpha = 0.4f) else ManifestaGlassBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (goal.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (goal.isCompleted) "Completed" else "Incomplete",
                    tint = if (goal.isCompleted) Color(0xFF7DD3A8) else ManifestaSecondaryText
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ManifestaPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = goal.category.uppercase(),
                            color = ManifestaAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = ManifestaSecondaryText, modifier = Modifier.size(15.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = goal.title,
                    color = if (goal.isCompleted) ManifestaSecondaryText else ManifestaText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                if (goal.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = goal.description,
                        color = ManifestaSecondaryText,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VisionCardItem(
    card: VisionCardEntity,
    onDelete: () -> Unit
) {
    val gradients = listOf(
        Brush.verticalGradient(listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))),
        Brush.verticalGradient(listOf(Color(0xFF831843), Color(0xFF3B0764))),
        Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF0F172A))),
        Brush.verticalGradient(listOf(Color(0xFF701A75), Color(0xFF1E1B4B)))
    )
    val backgroundBrush = gradients[card.gradientIndex % gradients.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundBrush)
            .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = card.category.uppercase(),
                        color = ManifestaAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete",
                        tint = ManifestaSecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = card.title,
                color = ManifestaText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = "“${card.affirmationText}”",
                color = ManifestaSecondaryText,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddVisionSheet(
    onDismiss: () -> Unit,
    onAdd: (title: String, goal: String, category: String, affirmation: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Career") }
    var affirmation by remember { mutableStateOf("") }

    val categories = listOf("Career", "Love", "Wealth", "Travel", "Home", "Health", "Lifestyle")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ManifestaSecondaryBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "New Vision Board Focus",
                style = MaterialTheme.typography.titleMedium,
                color = ManifestaText,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Vision Title (e.g., Executive Producer)", color = ManifestaMuted) },
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
                value = affirmation,
                onValueChange = { affirmation = it },
                placeholder = { Text("Affirmation for this vision...", color = ManifestaMuted) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ManifestaPrimary,
                    unfocusedBorderColor = ManifestaGlassBorder,
                    focusedTextColor = ManifestaText,
                    unfocusedTextColor = ManifestaText
                )
            )

            ManifestaButton(
                text = "Save to Board",
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            title,
                            goal.ifBlank { title },
                            category,
                            affirmation.ifBlank { "I embody this vision fully each day." }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
