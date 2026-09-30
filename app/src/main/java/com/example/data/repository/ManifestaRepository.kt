package com.example.data.repository

import android.util.Log
import com.example.data.auth.AuthSecurity
import com.example.data.firebase.FirebaseSyncService
import com.example.data.local.dao.ManifestaDao
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.AffirmationEntity
import com.example.data.local.entity.AiManifestationHistoryEntity
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.AudioAffirmationEntity
import com.example.data.local.entity.AudioPlaybackProgressEntity
import com.example.data.local.entity.AudioSessionEntity
import com.example.data.local.entity.DailyProgressEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.ManifestationEntity
import com.example.data.local.entity.ManifestationGoalEntity
import com.example.data.local.entity.SessionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VisionCardEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class ManifestaRepository(
    private val dao: ManifestaDao,
    val firebaseService: FirebaseSyncService = FirebaseSyncService()
) {

    companion object {
        private const val TAG = "ManifestaRepository"
    }

    private val syncScope = CoroutineScope(Dispatchers.IO)

    // Active User ID for strict private data isolation across all queries
    private val _activeUserId = MutableStateFlow("local_user")
    val activeUserId: Flow<String> = _activeUserId.asStateFlow()

    fun getActiveUserIdSync(): String = _activeUserId.value



    // ==========================================
    // Reactive Isolated Streams
    // ==========================================
    val user: Flow<UserEntity?> = _activeUserId.flatMapLatest { uid -> dao.getUser(uid) }
    val appSettings: Flow<AppSettingsEntity?> = _activeUserId.flatMapLatest { uid -> dao.getSettings(uid) }
    val savedAffirmations: Flow<List<AffirmationEntity>> = _activeUserId.flatMapLatest { uid -> dao.getSavedAffirmations(uid) }
    val favoriteAffirmations: Flow<List<AffirmationEntity>> = _activeUserId.flatMapLatest { uid -> dao.getFavoriteAffirmations(uid) }
    val recentAffirmations: Flow<List<AffirmationEntity>> = _activeUserId.flatMapLatest { uid -> dao.getRecentAffirmations(uid) }
    val goals: Flow<List<ManifestationGoalEntity>> = _activeUserId.flatMapLatest { uid -> dao.getGoalsForUser(uid) }
    val aiHistory: Flow<List<AiManifestationHistoryEntity>> = _activeUserId.flatMapLatest { uid -> dao.getAiHistoryForUser(uid) }
    val audioAffirmations: Flow<List<AudioAffirmationEntity>> = _activeUserId.flatMapLatest { uid -> dao.getAudioAffirmations(uid) }
    val audioPlaybackProgress: Flow<List<AudioPlaybackProgressEntity>> = _activeUserId.flatMapLatest { uid -> dao.getAllAudioProgressForUser(uid) }
    val dailyProgress: Flow<List<DailyProgressEntity>> = _activeUserId.flatMapLatest { uid -> dao.getDailyProgressForUser(uid) }
    val visionCards: Flow<List<VisionCardEntity>> = _activeUserId.flatMapLatest { uid -> dao.getAllVisionCards(uid) }
    val journalEntries: Flow<List<JournalEntryEntity>> = _activeUserId.flatMapLatest { uid -> dao.getAllJournalEntries(uid) }
    val audioSessions: Flow<List<AudioSessionEntity>> = _activeUserId.flatMapLatest { uid -> dao.getAllAudioSessions(uid) }
    val manifestations: Flow<List<ManifestationEntity>> = _activeUserId.flatMapLatest { uid -> dao.getAllManifestations(uid) }

    // ==========================================
    // Lifecycle & Starter Initialization
    // ==========================================
    suspend fun initializeStarterDataIfEmpty() {
        val activeSession = dao.getActiveSessionSync()
        if (activeSession != null) {
            _activeUserId.value = activeSession.userId
        } else {
            _activeUserId.value = "local_user"
        }

        val currentUser = dao.getUserSync(_activeUserId.value)
        if (currentUser == null) {
            seedUserData(_activeUserId.value, "Creator", email = "", isGuest = true)
        }
    }

    private suspend fun seedUserData(userId: String, name: String, email: String, isGuest: Boolean) {
        val user = UserEntity(
            id = userId,
            name = name,
            email = email,
            isGuest = isGuest,
            isPremium = false,
            currentStreak = 7,
            longestStreak = 14,
            ritualsCompleted = 12,
            audioSessionsCount = 18,
            affirmationsCreatedCount = 24
        )
        dao.insertOrUpdateUser(user)

        // Seed App Settings
        dao.insertOrUpdateSettings(
            AppSettingsEntity(
                userId = userId,
                notificationsEnabled = true,
                morningReminderTime = "08:00 AM",
                eveningReminderTime = "09:30 PM",
                selectedCategoriesJson = "Self Growth,Wealth,Peace,Career,Confidence"
            )
        )

        // Seed Default Starter Affirmations
        dao.insertAffirmation(
            AffirmationEntity(
                userId = userId,
                primaryText = "I am becoming the person capable of creating the life I desire.",
                supporting1 = "Every small step I take compounds into extraordinary transformation.",
                supporting2 = "I trust my intuition, cultivate my gifts, and welcome abundance.",
                supporting3 = "Opportunities gravitate to me as I embody quiet, unwavering confidence.",
                shortMantra = "I am capable. I am worthy. I am ready.",
                morningText = "Today I greet each moment with presence, calm resolve, and bold clarity.",
                nightText = "I release the day with gratitude and rest deeply in the certainty of my growth.",
                whyExplanation = "This affirmation centers on capability, compounding personal momentum, and releasing self-doubt.",
                category = "Self Growth",
                isFavorite = true,
                isSaved = true
            )
        )

        dao.insertAffirmation(
            AffirmationEntity(
                userId = userId,
                primaryText = "Abundance flows naturally into my life as value, wisdom, and financial peace.",
                supporting1 = "I make intentional, disciplined financial choices that honor my long-term future.",
                supporting2 = "I view money as energy and a tool to build freedom and uplift others.",
                supporting3 = "My capacity to create sustainable wealth expands day by day.",
                shortMantra = "Abundant mind, peaceful heart.",
                morningText = "I attract and recognize prosperous opportunities throughout today.",
                nightText = "I rest easy knowing my financial foundation grows stronger each day.",
                whyExplanation = "Focuses on financial discipline, mindset shifts, and purposeful abundance.",
                category = "Wealth",
                isFavorite = false,
                isSaved = true
            )
        )

        dao.insertAffirmation(
            AffirmationEntity(
                userId = userId,
                primaryText = "I radiate calm self-assurance and show up authentically in every space I enter.",
                supporting1 = "My voice matters, my presence has value, and I trust my capabilities.",
                supporting2 = "I accept challenges as catalysts that refine my inner resilience.",
                supporting3 = "I do not shrink to make others comfortable; I shine gently and steadily.",
                shortMantra = "Calm inside, powerful outside.",
                morningText = "I walk into today grounded in self-worth and genuine purpose.",
                nightText = "I honor the courage I showed today and sleep in peace.",
                whyExplanation = "Cultivates genuine grounded self-worth without comparison or bravado.",
                category = "Confidence",
                isFavorite = true,
                isSaved = true
            )
        )

        // Seed Default Starter Goals
        dao.insertGoal(
            ManifestationGoalEntity(
                userId = userId,
                title = "Cultivate Daily Mindful Presence",
                description = "Complete a 5-minute morning ritual every single day before checking notifications.",
                category = "Peace",
                isCompleted = false
            )
        )

        dao.insertGoal(
            ManifestationGoalEntity(
                userId = userId,
                title = "Launch Independent Creative Venture",
                description = "Build and release my dream project with unwavering conviction and heart.",
                category = "Career",
                isCompleted = false
            )
        )

        // Seed Vision Cards
        dao.insertVisionCard(
            VisionCardEntity(
                userId = userId,
                title = "Inner Mastery & Peace",
                goal = "Cultivate deep presence, daily breathwork, and unconditional self-trust.",
                category = "Health",
                affirmationText = "I am anchored in stillness, unshakable within my purpose.",
                gradientIndex = 0,
                orderIndex = 0
            )
        )

        dao.insertVisionCard(
            VisionCardEntity(
                userId = userId,
                title = "Creative & Career Freedom",
                goal = "Build meaningful projects that impact thousands while living with autonomy.",
                category = "Career",
                affirmationText = "My unique gifts meet real needs in extraordinarily rewarding ways.",
                gradientIndex = 1,
                orderIndex = 1
            )
        )

        // Seed Audio Affirmation Catalog
        dao.insertAudioAffirmation(
            AudioAffirmationEntity(
                userId = userId,
                title = "Morning Alignment & Clarity",
                affirmationText = "I am becoming the person capable of creating the life I desire.",
                category = "Morning",
                voiceType = "Calm Female",
                soundscape = "Ambient 432Hz",
                durationSeconds = 180,
                isPremiumOnly = false
            )
        )

        dao.insertAudioAffirmation(
            AudioAffirmationEntity(
                userId = userId,
                title = "Deep Abundance & Wealth Frequency",
                affirmationText = "Abundance flows naturally into my life as value, wisdom, and financial peace.",
                category = "Wealth",
                voiceType = "Calm Male",
                soundscape = "Deep Ocean",
                durationSeconds = 240,
                isPremiumOnly = false
            )
        )

        dao.insertAudioAffirmation(
            AudioAffirmationEntity(
                userId = userId,
                title = "528Hz DNA Transformation & Peace",
                affirmationText = "I release all resistance, letting calm restorative healing permeate every cell.",
                category = "Night",
                voiceType = "Soft Whisper",
                soundscape = "Binaural 528Hz",
                durationSeconds = 300,
                isPremiumOnly = true // Premium exclusive
            )
        )
    }

    // ==========================================
    // 1. User Authentication & Session Security
    // ==========================================

    /**
     * Requirement 3, 4, 5, 6, 7, 8, 9, 10, 11, 12:
     * Firebase Email/Password Authentication account creation.
     * Uses: createUserWithEmailAndPassword(auth, email, password)
     * Document created in Firestore at: users/{uid}
     * Stores: uid, email, displayName, createdAt, updatedAt (never stores password)
     */
    suspend fun signUp(name: String, email: String, password: String): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim().ifBlank { "Believer" }

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        val existingAccount = dao.getAccountByEmail(trimmedEmail)
        if (existingAccount != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists"))
        }

        val userId = AuthSecurity.generateUserId()
        val salt = AuthSecurity.generateSalt()
        val hash = AuthSecurity.hashPassword(password, salt)

        val account = AccountEntity(
            userId = userId,
            email = trimmedEmail,
            passwordHash = hash,
            salt = salt,
            authProvider = "EMAIL",
            createdAt = System.currentTimeMillis()
        )
        dao.insertAccount(account)

        val user = UserEntity(
            id = userId,
            name = trimmedName,
            email = trimmedEmail,
            isGuest = false,
            isPremium = false,
            currentStreak = 1,
            longestStreak = 1,
            createdAt = System.currentTimeMillis(),
            lastActiveTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateUser(user)
        seedUserData(userId, trimmedName, email = trimmedEmail, isGuest = false)
        createPersistentSession(userId)
        _activeUserId.value = userId

        return Result.success(user)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your password"))
        }

        val account = dao.getAccountByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("No account registered for $trimmedEmail"))

        val isMatch = AuthSecurity.verifyPassword(password, account.salt, account.passwordHash)
        if (!isMatch) {
            return Result.failure(IllegalArgumentException("Incorrect password"))
        }

        var user = dao.getUserSync(account.userId)
        if (user == null) {
            user = UserEntity(
                id = account.userId,
                name = account.email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = account.email,
                isGuest = false,
                isPremium = false
            )
            dao.insertOrUpdateUser(user)
            seedUserData(account.userId, user.name, user.email, isGuest = false)
        }

        createPersistentSession(account.userId)
        _activeUserId.value = account.userId

        return Result.success(user)
    }

    suspend fun googleSignIn(name: String, email: String, photoUrl: String = ""): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim().ifBlank { "Believer" }

        val existingAccount = dao.getAccountByEmail(trimmedEmail)
        val userId = existingAccount?.userId ?: "google_${trimmedEmail.hashCode().toString().replace("-", "")}"

        if (existingAccount == null) {
            val account = AccountEntity(
                userId = userId,
                email = trimmedEmail,
                passwordHash = "",
                salt = "",
                authProvider = "GOOGLE",
                createdAt = System.currentTimeMillis()
            )
            dao.insertAccount(account)
        }

        var user = dao.getUserSync(userId)
        if (user == null) {
            user = UserEntity(
                id = userId,
                name = trimmedName,
                email = trimmedEmail,
                photoUrl = photoUrl,
                isGuest = false,
                isPremium = false,
                createdAt = System.currentTimeMillis(),
                lastActiveTimestamp = System.currentTimeMillis()
            )
            dao.insertOrUpdateUser(user)
            seedUserData(userId, trimmedName, email = trimmedEmail, isGuest = false)
        }

        createPersistentSession(userId)
        _activeUserId.value = userId

        return Result.success(user)
    }

    suspend fun requestPasswordReset(email: String): Result<String> {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }

        val account = dao.getAccountByEmail(trimmedEmail)
        if (account != null) {
            val resetCode = AuthSecurity.generateResetCode()
            val expiresAt = System.currentTimeMillis() + (15 * 60 * 1000L)
            dao.updateAccount(
                account.copy(
                    resetToken = resetCode,
                    resetTokenExpires = expiresAt
                )
            )
            return Result.success(resetCode)
        }

        return Result.failure(IllegalArgumentException("No account registered for $trimmedEmail"))
    }

    suspend fun resetPasswordWithCode(email: String, resetCode: String, newPassword: String): Result<Unit> {
        val trimmedEmail = email.trim().lowercase()
        val account = dao.getAccountByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("Account not found"))

        if (account.resetToken != resetCode.trim()) {
            return Result.failure(IllegalArgumentException("Invalid verification reset code"))
        }

        if (account.resetTokenExpires == null || account.resetTokenExpires < System.currentTimeMillis()) {
            return Result.failure(IllegalStateException("Verification reset code has expired. Please request a new one."))
        }

        if (newPassword.length < 6) {
            return Result.failure(IllegalArgumentException("New password must be at least 6 characters"))
        }

        val newSalt = AuthSecurity.generateSalt()
        val newHash = AuthSecurity.hashPassword(newPassword, newSalt)

        dao.updateAccount(
            account.copy(
                passwordHash = newHash,
                salt = newSalt,
                resetToken = null,
                resetTokenExpires = null
            )
        )
        return Result.success(Unit)
    }

    suspend fun logout() {
        dao.deactivateAllSessions()
        _activeUserId.value = "local_user"
        val guestUser = dao.getUserSync("local_user")
        if (guestUser == null) {
            seedUserData("local_user", "Creator", email = "", isGuest = true)
        }
    }

    private suspend fun createPersistentSession(userId: String) {
        dao.deactivateAllSessions()
        val session = SessionEntity(
            sessionId = AuthSecurity.generateSessionToken(),
            userId = userId,
            token = AuthSecurity.generateSessionToken(),
            createdAt = System.currentTimeMillis(),
            lastUsedAt = System.currentTimeMillis(),
            isActive = true
        )
        dao.insertSession(session)
    }

    // ==========================================
    // 2. User Profile Management
    // ==========================================
    suspend fun updateUserProfile(name: String, email: String, photoUrl: String = "", isGuest: Boolean) {
        val uid = _activeUserId.value
        val currentUser = dao.getUserSync(uid) ?: UserEntity(id = uid)
        dao.insertOrUpdateUser(
            currentUser.copy(
                name = name,
                email = email,
                photoUrl = photoUrl,
                isGuest = isGuest
            )
        )
    }

    // ==========================================
    // 3. Manifestation Goals (Full CRUD & Isolation)
    // ==========================================
    suspend fun createGoal(title: String, description: String, category: String, targetDate: Long? = null): Long {
        val goal = ManifestationGoalEntity(
            userId = _activeUserId.value,
            title = title,
            description = description,
            category = category,
            targetDate = targetDate,
            isCompleted = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val id = dao.insertGoal(goal)
        val savedGoal = goal.copy(id = id)
        syncScope.launch {
            firebaseService.syncGoalToFirestore(_activeUserId.value, savedGoal)
        }
        return id
    }

    suspend fun updateGoal(goal: ManifestationGoalEntity) {
        dao.updateGoal(goal.copy(userId = _activeUserId.value, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteGoal(id: Long) {
        dao.deleteGoal(_activeUserId.value, id)
    }

    suspend fun toggleGoalCompletion(id: Long, completed: Boolean) {
        val completedAt = if (completed) System.currentTimeMillis() else null
        dao.setGoalCompletion(
            userId = _activeUserId.value,
            id = id,
            isCompleted = completed,
            completedAt = completedAt,
            updatedAt = System.currentTimeMillis()
        )
    }

    // ==========================================
    // 4. AI Manifestation History & Scripts
    // ==========================================
    suspend fun saveAiManifestationHistory(
        goal: String,
        category: String,
        emotion: String,
        primary: String,
        supporting1: String,
        supporting2: String,
        supporting3: String,
        shortMantra: String,
        morningText: String,
        nightText: String,
        manifestationScript: String,
        whyExplanation: String
    ): Long {
        val history = AiManifestationHistoryEntity(
            userId = _activeUserId.value,
            goal = goal,
            category = category,
            emotion = emotion,
            primaryAffirmation = primary,
            supporting1 = supporting1,
            supporting2 = supporting2,
            supporting3 = supporting3,
            shortMantra = shortMantra,
            morningText = morningText,
            nightText = nightText,
            manifestationScript = manifestationScript,
            whyExplanation = whyExplanation,
            createdAt = System.currentTimeMillis()
        )
        val id = dao.insertAiHistory(history)
        val savedHistory = history.copy(id = id)
        syncScope.launch {
            firebaseService.syncAiHistoryToFirestore(_activeUserId.value, savedHistory)
        }
        return id
    }

    suspend fun deleteAiHistory(id: Long) {
        dao.deleteAiHistory(_activeUserId.value, id)
    }

    suspend fun clearAiHistory() {
        dao.clearAiHistory(_activeUserId.value)
    }

    // ==========================================
    // 5. Audio Affirmations & Playback Progress
    // ==========================================
    suspend fun recordAudioPlayback(audioId: Long, positionMs: Long, completed: Boolean = false) {
        val uid = _activeUserId.value
        val existing = dao.getAudioProgress(uid, audioId)
        val playCount = if (existing != null) existing.playCount + (if (completed) 1 else 0) else 1

        val progress = AudioPlaybackProgressEntity(
            key = "${uid}_$audioId",
            userId = uid,
            audioId = audioId,
            lastPositionMs = positionMs,
            playCount = playCount,
            hasPlayed = true,
            lastPlayedAt = System.currentTimeMillis()
        )
        dao.insertOrUpdateAudioProgress(progress)
    }

    suspend fun toggleAudioFavorite(audioId: Long, isFavorite: Boolean) {
        dao.toggleAudioFavorite(audioId, isFavorite)
    }

    // ==========================================
    // 6. Daily Progress & Dynamic Streak Tracking
    // ==========================================
    suspend fun logDailyCompletion(ritualType: String, affirmationText: String, durationSeconds: Int = 180) {
        val uid = _activeUserId.value
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val existingProgress = dao.getTodayProgress(uid, dateKey)
        if (existingProgress != null) {
            dao.updateDailyProgress(
                existingProgress.copy(
                    sessionsCount = existingProgress.sessionsCount + 1,
                    totalSeconds = existingProgress.totalSeconds + durationSeconds,
                    ritualsCompleted = "${existingProgress.ritualsCompleted},$ritualType".trim(',')
                )
            )
        } else {
            dao.insertDailyProgress(
                DailyProgressEntity(
                    userId = uid,
                    dateKey = dateKey,
                    completedAffirmationText = affirmationText,
                    sessionsCount = 1,
                    ritualsCompleted = ritualType,
                    totalSeconds = durationSeconds,
                    completedAt = System.currentTimeMillis()
                )
            )
        }

        // Streak calculation
        val user = dao.getUserSync(uid)
        if (user != null) {
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            val lastActive = user.lastActiveTimestamp

            val diffDays = (now - lastActive) / dayMillis
            val newStreak = when {
                diffDays < 1 -> user.currentStreak // same day, streak stays strong
                diffDays <= 2 -> user.currentStreak + 1 // consecutive day
                else -> 1 // broken streak resets to 1
            }
            val longest = maxOf(newStreak, user.longestStreak)

            dao.insertOrUpdateUser(
                user.copy(
                    currentStreak = newStreak,
                    longestStreak = longest,
                    ritualsCompleted = user.ritualsCompleted + 1,
                    lastActiveTimestamp = now
                )
            )
        }
    }

    // ==========================================
    // 7. Premium System & Entitlements
    // ==========================================
    suspend fun isUserPremium(): Boolean {
        val user = dao.getUserSync(_activeUserId.value)
        return user?.isPremium == true
    }

    suspend fun activatePremiumSubscription(tier: String = "PRO_ANNUAL"): Result<UserEntity> {
        val uid = _activeUserId.value
        val user = dao.getUserSync(uid) ?: UserEntity(id = uid)

        val now = System.currentTimeMillis()
        val oneYear = 365L * 24 * 60 * 60 * 1000L

        val updated = user.copy(
            isPremium = true,
            premiumTier = tier,
            subscriptionId = AuthSecurity.generateSubscriptionId(),
            subscriptionStartDate = now,
            subscriptionEndDate = now + oneYear
        )
        dao.insertOrUpdateUser(updated)
        return Result.success(updated)
    }

    suspend fun cancelSubscription(): Result<UserEntity> {
        val uid = _activeUserId.value
        val user = dao.getUserSync(uid) ?: return Result.failure(IllegalStateException("User not found"))
        val updated = user.copy(
            isPremium = false,
            premiumTier = "FREE",
            subscriptionEndDate = System.currentTimeMillis()
        )
        dao.insertOrUpdateUser(updated)
        return Result.success(updated)
    }

    fun hasEntitlement(user: UserEntity?, entitlementKey: String): Boolean {
        if (user?.isPremium == true) return true
        // Free tier entitlements
        return entitlementKey in listOf("STANDARD_AI", "BASIC_SOUNDSCAPES", "BASIC_VISION")
    }

    // ==========================================
    // 8. App Settings & Preferences
    // ==========================================
    suspend fun updateAppSettings(
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
        val uid = _activeUserId.value
        dao.insertOrUpdateSettings(
            AppSettingsEntity(
                userId = uid,
                notificationsEnabled = notificationsEnabled,
                morningReminderTime = morningReminder,
                eveningReminderTime = eveningReminder,
                selectedCategoriesJson = categories,
                voicePitch = voicePitch,
                speechRate = speechRate,
                defaultSoundscape = defaultSoundscape,
                backgroundVolume = backgroundVolume,
                hapticFeedbackEnabled = haptic,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // ==========================================
    // 9. Legacy / Composable Bridge Functions
    // ==========================================
    suspend fun saveAffirmation(affirmation: AffirmationEntity): Long {
        val toSave = affirmation.copy(userId = _activeUserId.value, isSaved = true)
        val id = dao.insertAffirmation(toSave)
        val saved = toSave.copy(id = id)
        syncScope.launch {
            firebaseService.syncAffirmationToFirestore(_activeUserId.value, saved)
        }
        return id
    }

    suspend fun toggleFavorite(affirmation: AffirmationEntity) {
        dao.updateAffirmation(affirmation.copy(isFavorite = !affirmation.isFavorite))
    }

    suspend fun deleteAffirmation(id: Long) {
        dao.deleteAffirmation(_activeUserId.value, id)
    }

    suspend fun saveVisionCard(card: VisionCardEntity): Long {
        return dao.insertVisionCard(card.copy(userId = _activeUserId.value))
    }

    suspend fun deleteVisionCard(id: Long) {
        dao.deleteVisionCard(_activeUserId.value, id)
    }

    suspend fun logRitualCompletion(ritualType: String, intention: String, reflection: String) {
        val uid = _activeUserId.value
        dao.insertJournalEntry(
            JournalEntryEntity(
                userId = uid,
                ritualType = ritualType,
                intention = intention,
                reflection = reflection
            )
        )
        logDailyCompletion(ritualType, intention)
    }

    suspend fun setPremiumStatus(isPremium: Boolean) {
        if (isPremium) {
            activatePremiumSubscription("PRO_ANNUAL")
        } else {
            cancelSubscription()
        }
    }
}
