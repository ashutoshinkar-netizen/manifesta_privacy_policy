package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AiHistorySheet
import com.example.ui.dialogs.AuthModalDialog
import com.example.ui.dialogs.GoalDialog
import com.example.ui.dialogs.GuestSavePromptDialog
import com.example.ui.dialogs.PrivacyPolicyDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.ShareCardDialog
import com.example.ui.navigation.ManifestaBottomNavigationBar
import com.example.ui.navigation.ManifestaDestination
import com.example.ui.screens.AudioScreen
import com.example.ui.screens.CreateScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RitualScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText
import com.example.ui.theme.ManifestaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManifestaTheme {
                val viewModel: MainViewModel = viewModel()
                ManifestaApp(viewModel)
            }
        }
    }
}

@Composable
fun ManifestaApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val user by viewModel.user.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val recentAffirmations by viewModel.recentAffirmations.collectAsState()
    val visionCards by viewModel.visionCards.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val aiHistory by viewModel.aiHistory.collectAsState()

    val showAuthModal by viewModel.showAuthModal.collectAsState()
    val showGuestPrompt by viewModel.showGuestSavePrompt.collectAsState()
    val showGoalDialog by viewModel.showGoalDialog.collectAsState()
    val editingGoal by viewModel.editingGoal.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val showPrivacyPolicy by viewModel.showPrivacyPolicyDialog.collectAsState()
    val showHistorySheet by viewModel.showHistorySheet.collectAsState()
    val activeShareAffirmation by viewModel.activeShareAffirmation.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ManifestaBackground)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "screen_navigation"
        ) { screen ->
            when (screen) {
                is AppScreen.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            viewModel.navigateTo(AppScreen.Onboarding)
                        }
                    )
                }

                is AppScreen.Onboarding -> {
                    OnboardingScreen(
                        onFinished = {
                            viewModel.navigateTo(AppScreen.Main(ManifestaDestination.HOME))
                        }
                    )
                }

                is AppScreen.GuidedRitual -> {
                    RitualScreen(
                        viewModel = viewModel,
                        ritualType = screen.ritualType,
                        onBack = {
                            viewModel.navigateTo(AppScreen.Main(ManifestaDestination.HOME))
                        }
                    )
                }

                is AppScreen.Premium -> {
                    PremiumScreen(
                        viewModel = viewModel,
                        onClose = {
                            viewModel.navigateTo(AppScreen.Main(ManifestaDestination.HOME))
                        }
                    )
                }

                is AppScreen.Main -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = ManifestaBackground,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            Column {
                                // Mini player bar if playing audio and not on Audio tab
                                if (playerState.isPlaying && screen.destination != ManifestaDestination.AUDIO) {
                                    MiniAudioBar(
                                        title = playerState.currentTitle,
                                        isPlaying = playerState.isPlaying,
                                        onTogglePlay = { viewModel.audioManager.togglePlayPause() },
                                        onOpenAudioTab = { viewModel.navigateToTab(ManifestaDestination.AUDIO) }
                                    )
                                }

                                ManifestaBottomNavigationBar(
                                    currentDestination = screen.destination,
                                    onNavigate = { dest ->
                                        viewModel.navigateToTab(dest)
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .statusBarsPadding()
                        ) {
                            when (screen.destination) {
                                ManifestaDestination.HOME -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        user = user,
                                        recentAffirmations = recentAffirmations
                                    )
                                }

                                ManifestaDestination.CREATE -> {
                                    CreateScreen(
                                        viewModel = viewModel
                                    )
                                }

                                ManifestaDestination.AUDIO -> {
                                    AudioScreen(
                                        viewModel = viewModel,
                                        playerState = playerState
                                    )
                                }

                                ManifestaDestination.VISION -> {
                                    VisionScreen(
                                        viewModel = viewModel,
                                        visionCards = visionCards
                                    )
                                }

                                ManifestaDestination.PROFILE -> {
                                    ProfileScreen(
                                        viewModel = viewModel,
                                        user = user
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Toast / Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 80.dp, start = 20.dp, end = 20.dp)
        )

        // Auth Modal Dialog (Sign In / Register / Google / Forgot Password)
        if (showAuthModal) {
            AuthModalDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.dismissAuthModal() }
            )
        }

        // Guest Save Prompt Dialog
        if (showGuestPrompt) {
            GuestSavePromptDialog(
                onDismiss = { viewModel.dismissGuestSavePrompt() },
                onOpenAuth = {
                    viewModel.dismissGuestSavePrompt()
                    viewModel.openAuthModal()
                },
                onSaveLocally = {
                    viewModel.forceSaveLatestAffirmationAsGuest()
                }
            )
        }

        // Goal Dialog (Create / Edit Manifestation Goals)
        if (showGoalDialog) {
            GoalDialog(
                goal = editingGoal,
                onDismiss = { viewModel.dismissGoalDialog() },
                onSave = { title, desc, cat ->
                    viewModel.saveGoal(title, desc, cat)
                },
                onDelete = { id ->
                    viewModel.deleteGoal(id)
                    viewModel.dismissGoalDialog()
                }
            )
        }

        // App & Audio Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = appSettings,
                onDismiss = { viewModel.dismissSettingsDialog() },
                onOpenPrivacyPolicy = {
                    viewModel.dismissSettingsDialog()
                    viewModel.openPrivacyPolicy()
                },
                onSave = { notif, morning, evening, cats, pitch, rate, soundscape, vol, haptic ->
                    viewModel.updateSettings(
                        notificationsEnabled = notif,
                        morningReminder = morning,
                        eveningReminder = evening,
                        categories = cats,
                        voicePitch = pitch,
                        speechRate = rate,
                        defaultSoundscape = soundscape,
                        backgroundVolume = vol,
                        haptic = haptic
                    )
                }
            )
        }

        // AI History Sheet (Vault of saved affirmations & scripts)
        if (showHistorySheet) {
            AiHistorySheet(
                historyList = aiHistory,
                onDismiss = { viewModel.dismissHistorySheet() },
                onPlayAudio = { item ->
                    viewModel.playAffirmationAudio(
                        title = item.shortMantra.ifBlank { "Affirmation" },
                        text = item.primaryAffirmation,
                        category = item.category
                    )
                    viewModel.dismissHistorySheet()
                    viewModel.navigateToTab(ManifestaDestination.AUDIO)
                },
                onDelete = { id ->
                    viewModel.deleteAiHistory(id)
                },
                onShowToast = { msg ->
                    viewModel.showToast(msg)
                }
            )
        }

        // In-App Privacy Policy Dialog
        if (showPrivacyPolicy) {
            PrivacyPolicyDialog(
                onDismiss = { viewModel.dismissPrivacyPolicy() }
            )
        }

        // Share Card Dialog
        activeShareAffirmation?.let { affirmation ->
            ShareCardDialog(
                affirmation = affirmation,
                onDismiss = { viewModel.dismissShareDialog() }
            )
        }
    }
}

@Composable
fun MiniAudioBar(
    title: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onOpenAudioTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        ManifestaSurface,
                        ManifestaPrimary.copy(alpha = 0.25f),
                        ManifestaSecondary.copy(alpha = 0.15f)
                    )
                )
            )
            .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenAudioTab)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ManifestaPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = ManifestaSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        color = ManifestaText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Playing ritual audio • Tap for player",
                        color = ManifestaSecondaryText,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = ManifestaText,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
