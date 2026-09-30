package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerState
import com.example.audio.ManifestaAudioManager
import com.example.data.ai.GeminiAffirmationService
import com.example.data.ai.GeneratedAffirmationBundle
import com.example.data.local.ManifestaDatabase
import com.example.data.local.entity.AffirmationEntity
import com.example.data.local.entity.AiManifestationHistoryEntity
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.AudioAffirmationEntity
import com.example.data.local.entity.AudioPlaybackProgressEntity
import com.example.data.local.entity.AudioSessionEntity
import com.example.data.local.entity.DailyProgressEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.ManifestationGoalEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VisionCardEntity
import com.example.data.repository.ManifestaRepository
import com.example.ui.navigation.ManifestaDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Splash : AppScreen()
    object Onboarding : AppScreen()
    data class Main(val destination: ManifestaDestination = ManifestaDestination.HOME) : AppScreen()
    data class GuidedRitual(val ritualType: String = "Morning") : AppScreen()
    object Premium : AppScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ManifestaDatabase.getInstance(application)
    val repository = ManifestaRepository(db.manifestaDao())
    val audioManager = ManifestaAudioManager(application)
    val aiService = GeminiAffirmationService()

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Isolated Data Streams
    val user: StateFlow<UserEntity?> = repository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val appSettings: StateFlow<AppSettingsEntity?> = repository.appSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val savedAffirmations: StateFlow<List<AffirmationEntity>> = repository.savedAffirmations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentAffirmations: StateFlow<List<AffirmationEntity>> = repository.recentAffirmations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteAffirmations: StateFlow<List<AffirmationEntity>> = repository.favoriteAffirmations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val goals: StateFlow<List<ManifestationGoalEntity>> = repository.goals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val aiHistory: StateFlow<List<AiManifestationHistoryEntity>> = repository.aiHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val audioAffirmations: StateFlow<List<AudioAffirmationEntity>> = repository.audioAffirmations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val audioPlaybackProgress: StateFlow<List<AudioPlaybackProgressEntity>> = repository.audioPlaybackProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyProgress: StateFlow<List<DailyProgressEntity>> = repository.dailyProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val visionCards: StateFlow<List<VisionCardEntity>> = repository.visionCards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val journalEntries: StateFlow<List<JournalEntryEntity>> = repository.journalEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val audioSessions: StateFlow<List<AudioSessionEntity>> = repository.audioSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val playerState: StateFlow<AudioPlayerState> = audioManager.playerState

    // Dialog & Flow States
    private val _showGuestSavePrompt = MutableStateFlow(false)
    val showGuestSavePrompt: StateFlow<Boolean> = _showGuestSavePrompt.asStateFlow()

    private val _showAuthModal = MutableStateFlow(false)
    val showAuthModal: StateFlow<Boolean> = _showAuthModal.asStateFlow()

    private val _showGoalDialog = MutableStateFlow(false)
    val showGoalDialog: StateFlow<Boolean> = _showGoalDialog.asStateFlow()

    private val _editingGoal = MutableStateFlow<ManifestationGoalEntity?>(null)
    val editingGoal: StateFlow<ManifestationGoalEntity?> = _editingGoal.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showHistorySheet = MutableStateFlow(false)
    val showHistorySheet: StateFlow<Boolean> = _showHistorySheet.asStateFlow()

    private val _showPrivacyPolicyDialog = MutableStateFlow(false)
    val showPrivacyPolicyDialog: StateFlow<Boolean> = _showPrivacyPolicyDialog.asStateFlow()

    private val _activeShareAffirmation = MutableStateFlow<AffirmationEntity?>(null)
    val activeShareAffirmation: StateFlow<AffirmationEntity?> = _activeShareAffirmation.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Creation State
    private val _isGeneratingAffirmation = MutableStateFlow(false)
    val isGeneratingAffirmation: StateFlow<Boolean> = _isGeneratingAffirmation.asStateFlow()

    private val _latestGeneratedBundle = MutableStateFlow<GeneratedAffirmationBundle?>(null)
    val latestGeneratedBundle: StateFlow<GeneratedAffirmationBundle?> = _latestGeneratedBundle.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeStarterDataIfEmpty()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun navigateToTab(destination: ManifestaDestination) {
        _currentScreen.value = AppScreen.Main(destination)
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun dismissToast() {
        _toastMessage.value = null
    }

    fun openGuestSavePrompt() {
        _showGuestSavePrompt.value = true
    }

    fun dismissGuestSavePrompt() {
        _showGuestSavePrompt.value = false
    }

    fun openAuthModal() {
        _showAuthModal.value = true
    }

    fun dismissAuthModal() {
        _showAuthModal.value = false
    }

    fun openGoalDialog(goal: ManifestationGoalEntity? = null) {
        _editingGoal.value = goal
        _showGoalDialog.value = true
    }

    fun dismissGoalDialog() {
        _showGoalDialog.value = false
        _editingGoal.value = null
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun dismissSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun openHistorySheet() {
        _showHistorySheet.value = true
    }

    fun dismissHistorySheet() {
        _showHistorySheet.value = false
    }

    fun openPrivacyPolicy() {
        _showPrivacyPolicyDialog.value = true
    }

    fun dismissPrivacyPolicy() {
        _showPrivacyPolicyDialog.value = false
    }

    fun openShareDialog(affirmation: AffirmationEntity) {
        _activeShareAffirmation.value = affirmation
    }

    fun dismissShareDialog() {
        _activeShareAffirmation.value = null
    }

    // ==========================================
    // 1. User Authentication Backend Operations
    // ==========================================
    fun signUpWithEmail(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signUp(name, email, password)
            result.onSuccess { user ->
                _showAuthModal.value = false
                _showGuestSavePrompt.value = false
                showToast("Welcome to your sanctuary, ${user.name} ✨")
                onResult(true, "Account created successfully")
            }.onFailure { error ->
                onResult(false, error.message ?: "Signup failed")
            }
        }
    }

    fun loginWithEmail(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess { user ->
                _showAuthModal.value = false
                _showGuestSavePrompt.value = false
                showToast("Welcome back, ${user.name} ✨")
                onResult(true, "Logged in successfully")
            }.onFailure { error ->
                onResult(false, error.message ?: "Login failed")
            }
        }
    }

    fun signInWithGoogle(name: String, email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.googleSignIn(name, email)
            result.onSuccess { user ->
                _showAuthModal.value = false
                _showGuestSavePrompt.value = false
                showToast("Google sign-in complete. Welcome ${user.name} ✨")
                onResult(true, "Google sign-in successful")
            }.onFailure { error ->
                onResult(false, error.message ?: "Google sign-in failed")
            }
        }
    }

    fun forgotPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.requestPasswordReset(email)
            result.onSuccess { code ->
                onResult(true, code)
            }.onFailure { error ->
                onResult(false, error.message ?: "Failed to generate reset code")
            }
        }
    }

    fun resetPassword(email: String, code: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.resetPasswordWithCode(email, code, newPass)
            result.onSuccess {
                showToast("Password updated. Please log in.")
                onResult(true, "Password updated successfully")
            }.onFailure { error ->
                onResult(false, error.message ?: "Password reset failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            showToast("Signed out. Guest mode active.")
        }
    }

    // ==========================================
    // 2. Audio Affirmations with Resume & Entitlements
    // ==========================================
    fun playAffirmationAudio(
        audioId: Long = 0,
        title: String,
        text: String,
        category: String = "Self Growth",
        voiceType: String = "Calm Female",
        soundscape: String = "Ambient 432Hz",
        isPremiumOnly: Boolean = false
    ) {
        val isUserPremium = user.value?.isPremium == true
        if (isPremiumOnly && !isUserPremium) {
            showToast("528Hz DNA frequency requires Manifesta Premium ✨")
            _currentScreen.value = AppScreen.Premium
            return
        }

        viewModelScope.launch {
            if (audioId > 0) {
                repository.recordAudioPlayback(audioId, 0)
            }
        }

        audioManager.playSession(
            title = title,
            affirmationText = text,
            category = category,
            voiceType = voiceType,
            soundscape = soundscape
        )
    }

    fun toggleAudioFavorite(audioId: Long, currentFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleAudioFavorite(audioId, !currentFavorite)
        }
    }

    // ==========================================
    // 3. AI Affirmation & History Generation
    // ==========================================
    fun generateAffirmation(
        goal: String,
        category: String,
        emotion: String
    ) {
        viewModelScope.launch {
            _isGeneratingAffirmation.value = true
            val result = aiService.generateAffirmations(goal, category, emotion)
            val bundle = result.getOrElse {
                aiService.synthesizeAffirmations(goal, category, emotion)
            }
            _latestGeneratedBundle.value = bundle
            _isGeneratingAffirmation.value = false

            // Automatically persist to user's AI Manifestation History backend
            repository.saveAiManifestationHistory(
                goal = goal,
                category = category,
                emotion = emotion,
                primary = bundle.primaryText,
                supporting1 = bundle.supporting1,
                supporting2 = bundle.supporting2,
                supporting3 = bundle.supporting3,
                shortMantra = bundle.shortMantra,
                morningText = bundle.morningText,
                nightText = bundle.nightText,
                manifestationScript = bundle.manifestationScript,
                whyExplanation = bundle.whyExplanation
            )
        }
    }

    fun saveLatestAffirmation(onComplete: (() -> Unit)? = null) {
        val bundle = _latestGeneratedBundle.value ?: return
        val isGuest = user.value?.isGuest ?: true

        if (isGuest) {
            _showGuestSavePrompt.value = true
            return
        }

        viewModelScope.launch {
            repository.saveAffirmation(
                AffirmationEntity(
                    primaryText = bundle.primaryText,
                    supporting1 = bundle.supporting1,
                    supporting2 = bundle.supporting2,
                    supporting3 = bundle.supporting3,
                    shortMantra = bundle.shortMantra,
                    morningText = bundle.morningText,
                    nightText = bundle.nightText,
                    whyExplanation = bundle.whyExplanation,
                    category = bundle.category,
                    isSaved = true
                )
            )
            showToast("Affirmation saved to sanctuary ✨")
            onComplete?.invoke()
        }
    }

    fun forceSaveLatestAffirmationAsGuest() {
        val bundle = _latestGeneratedBundle.value ?: return
        viewModelScope.launch {
            repository.saveAffirmation(
                AffirmationEntity(
                    primaryText = bundle.primaryText,
                    supporting1 = bundle.supporting1,
                    supporting2 = bundle.supporting2,
                    supporting3 = bundle.supporting3,
                    shortMantra = bundle.shortMantra,
                    morningText = bundle.morningText,
                    nightText = bundle.nightText,
                    whyExplanation = bundle.whyExplanation,
                    category = bundle.category,
                    isSaved = true
                )
            )
            _showGuestSavePrompt.value = false
            showToast("Saved temporary manifestation ✨")
        }
    }

    fun toggleFavorite(affirmation: AffirmationEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(affirmation)
        }
    }

    fun deleteAffirmation(id: Long) {
        viewModelScope.launch {
            repository.deleteAffirmation(id)
            showToast("Affirmation removed")
        }
    }

    fun deleteAiHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteAiHistory(id)
            showToast("Manifestation script removed")
        }
    }

    // ==========================================
    // 4. Manifestation Goals (CRUD)
    // ==========================================
    fun saveGoal(title: String, description: String, category: String, targetDate: Long? = null) {
        viewModelScope.launch {
            val current = _editingGoal.value
            if (current != null) {
                repository.updateGoal(
                    current.copy(
                        title = title,
                        description = description,
                        category = category,
                        targetDate = targetDate
                    )
                )
                showToast("Goal updated ✨")
            } else {
                repository.createGoal(title, description, category, targetDate)
                showToast("Goal created in your sanctuary ✨")
            }
            dismissGoalDialog()
        }
    }

    fun toggleGoalCompletion(goal: ManifestationGoalEntity) {
        viewModelScope.launch {
            repository.toggleGoalCompletion(goal.id, !goal.isCompleted)
            showToast(if (!goal.isCompleted) "Goal manifested! ✨" else "Goal marked active")
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteGoal(id)
            showToast("Goal deleted")
        }
    }

    // ==========================================
    // 5. Daily Rituals & Streak Mechanics
    // ==========================================
    fun completeRitual(ritualType: String, intention: String, reflection: String) {
        viewModelScope.launch {
            repository.logRitualCompletion(ritualType, intention, reflection)
            showToast("Ritual complete ✨ +1 day streak")
            _currentScreen.value = AppScreen.Main(ManifestaDestination.HOME)
        }
    }

    // ==========================================
    // 6. Premium Subscriptions & Entitlements
    // ==========================================
    fun activatePremium(tier: String = "PRO_ANNUAL") {
        viewModelScope.launch {
            repository.activatePremiumSubscription(tier)
            showToast("Manifesta Premium unlocked ✨")
            _currentScreen.value = AppScreen.Main(ManifestaDestination.PROFILE)
        }
    }

    // ==========================================
    // 7. Settings Management
    // ==========================================
    fun updateSettings(
        notificationsEnabled: Boolean,
        morningReminder: String,
        eveningReminder: String,
        categories: String,
        voicePitch: Float = 1.0f,
        speechRate: Float = 0.9f,
        defaultSoundscape: String = "Ambient 432Hz",
        backgroundVolume: Float = 0.4f,
        haptic: Boolean = true
    ) {
        viewModelScope.launch {
            repository.updateAppSettings(
                notificationsEnabled = notificationsEnabled,
                morningReminder = morningReminder,
                eveningReminder = eveningReminder,
                categories = categories,
                voicePitch = voicePitch,
                speechRate = speechRate,
                defaultSoundscape = defaultSoundscape,
                backgroundVolume = backgroundVolume,
                haptic = haptic
            )
            showToast("Preferences saved ✨")
            dismissSettingsDialog()
        }
    }

    // ==========================================
    // 8. Vision Cards
    // ==========================================
    fun addVisionCard(title: String, goal: String, category: String, affirmation: String) {
        viewModelScope.launch {
            repository.saveVisionCard(
                VisionCardEntity(
                    title = title,
                    goal = goal,
                    category = category,
                    affirmationText = affirmation,
                    gradientIndex = (0..3).random(),
                    orderIndex = visionCards.value.size
                )
            )
            showToast("Vision card added to your sanctuary ✨")
        }
    }

    fun deleteVisionCard(id: Long) {
        viewModelScope.launch {
            repository.deleteVisionCard(id)
            showToast("Vision card deleted")
        }
    }

    // ==========================================
    // 9. Firebase & Firestore Cloud Sync
    // ==========================================
    val isFirebaseConnected: Boolean
        get() = repository.firebaseService.isFirebaseInitialized

    fun syncAllToCloud() {
        val currentUser = user.value
        if (currentUser == null || currentUser.isGuest) {
            showToast("Sign in to sync your data to Firebase Cloud ✨")
            return
        }

        viewModelScope.launch {
            showToast("Syncing data to Firebase & Firestore...")
            var syncedGoals = 0
            var syncedAffirmations = 0

            val userSynced = repository.firebaseService.syncUserToFirestore(currentUser)

            goals.value.forEach { goal ->
                if (repository.firebaseService.syncGoalToFirestore(currentUser.id, goal)) {
                    syncedGoals++
                }
            }

            savedAffirmations.value.forEach { aff ->
                if (repository.firebaseService.syncAffirmationToFirestore(currentUser.id, aff)) {
                    syncedAffirmations++
                }
            }

            aiHistory.value.forEach { hist ->
                repository.firebaseService.syncAiHistoryToFirestore(currentUser.id, hist)
            }

            if (userSynced || syncedGoals > 0 || syncedAffirmations > 0) {
                showToast("Firebase Cloud sync complete! ✨")
            } else {
                showToast("Data saved locally. Firestore will sync when connected ✨")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
