package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface ManifestaDao {

    // ==========================================
    // 1. User & Account
    // ==========================================
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserSync(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("SELECT * FROM accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getAccountByEmail(email: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE userId = :userId LIMIT 1")
    suspend fun getAccountById(userId: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    // ==========================================
    // 2. Sessions (Persistent Login)
    // ==========================================
    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY lastUsedAt DESC LIMIT 1")
    fun getActiveSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY lastUsedAt DESC LIMIT 1")
    suspend fun getActiveSessionSync(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("UPDATE sessions SET isActive = 0")
    suspend fun deactivateAllSessions()

    @Query("UPDATE sessions SET isActive = 0 WHERE sessionId = :sessionId")
    suspend fun deactivateSession(sessionId: String)

    // ==========================================
    // 3. Manifestation Goals (CRUD & User Isolation)
    // ==========================================
    @Query("SELECT * FROM manifestation_goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getGoalsForUser(userId: String): Flow<List<ManifestationGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: ManifestationGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: ManifestationGoalEntity)

    @Query("DELETE FROM manifestation_goals WHERE userId = :userId AND id = :id")
    suspend fun deleteGoal(userId: String, id: Long)

    @Query("UPDATE manifestation_goals SET isCompleted = :isCompleted, completedAt = :completedAt, updatedAt = :updatedAt WHERE userId = :userId AND id = :id")
    suspend fun setGoalCompletion(userId: String, id: Long, isCompleted: Boolean, completedAt: Long?, updatedAt: Long)

    // ==========================================
    // 4. AI Manifestation History & Scripts
    // ==========================================
    @Query("SELECT * FROM ai_manifestation_history WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAiHistoryForUser(userId: String): Flow<List<AiManifestationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiHistory(history: AiManifestationHistoryEntity): Long

    @Query("DELETE FROM ai_manifestation_history WHERE userId = :userId AND id = :id")
    suspend fun deleteAiHistory(userId: String, id: Long)

    @Query("DELETE FROM ai_manifestation_history WHERE userId = :userId")
    suspend fun clearAiHistory(userId: String)

    // ==========================================
    // 5. Audio Affirmations & Playback Progress
    // ==========================================
    @Query("SELECT * FROM audio_affirmations WHERE userId = :userId OR userId = 'global' ORDER BY createdAt DESC")
    fun getAudioAffirmations(userId: String): Flow<List<AudioAffirmationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioAffirmation(audio: AudioAffirmationEntity): Long

    @Update
    suspend fun updateAudioAffirmation(audio: AudioAffirmationEntity)

    @Query("UPDATE audio_affirmations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleAudioFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM audio_playback_progress WHERE userId = :userId AND audioId = :audioId LIMIT 1")
    suspend fun getAudioProgress(userId: String, audioId: Long): AudioPlaybackProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAudioProgress(progress: AudioPlaybackProgressEntity)

    @Query("SELECT * FROM audio_playback_progress WHERE userId = :userId ORDER BY lastPlayedAt DESC")
    fun getAllAudioProgressForUser(userId: String): Flow<List<AudioPlaybackProgressEntity>>

    // ==========================================
    // 6. Daily Progress & Sessions
    // ==========================================
    @Query("SELECT * FROM daily_progress WHERE userId = :userId ORDER BY completedAt DESC")
    fun getDailyProgressForUser(userId: String): Flow<List<DailyProgressEntity>>

    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND dateKey = :dateKey LIMIT 1")
    suspend fun getTodayProgress(userId: String, dateKey: String): DailyProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyProgress(progress: DailyProgressEntity): Long

    @Update
    suspend fun updateDailyProgress(progress: DailyProgressEntity)

    // ==========================================
    // 7. App Settings
    // ==========================================
    @Query("SELECT * FROM app_settings WHERE userId = :userId LIMIT 1")
    fun getSettings(userId: String): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE userId = :userId LIMIT 1")
    suspend fun getSettingsSync(userId: String): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AppSettingsEntity)

    // ==========================================
    // 8. Affirmations (User-isolated)
    // ==========================================
    @Query("SELECT * FROM affirmations WHERE userId = :userId AND isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedAffirmations(userId: String): Flow<List<AffirmationEntity>>

    @Query("SELECT * FROM affirmations WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteAffirmations(userId: String): Flow<List<AffirmationEntity>>

    @Query("SELECT * FROM affirmations WHERE userId = :userId ORDER BY createdAt DESC LIMIT 10")
    fun getRecentAffirmations(userId: String): Flow<List<AffirmationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAffirmation(affirmation: AffirmationEntity): Long

    @Update
    suspend fun updateAffirmation(affirmation: AffirmationEntity)

    @Query("DELETE FROM affirmations WHERE userId = :userId AND id = :id")
    suspend fun deleteAffirmation(userId: String, id: Long)

    // Legacy / fallback delete
    @Query("DELETE FROM affirmations WHERE id = :id")
    suspend fun deleteAffirmation(id: Long)

    // ==========================================
    // 9. Vision Cards (User-isolated)
    // ==========================================
    @Query("SELECT * FROM vision_cards WHERE userId = :userId ORDER BY orderIndex ASC, createdAt DESC")
    fun getAllVisionCards(userId: String): Flow<List<VisionCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisionCard(card: VisionCardEntity): Long

    @Query("DELETE FROM vision_cards WHERE userId = :userId AND id = :id")
    suspend fun deleteVisionCard(userId: String, id: Long)

    @Query("DELETE FROM vision_cards WHERE id = :id")
    suspend fun deleteVisionCard(id: Long)

    // ==========================================
    // 10. Journal & Legacy Audio Sessions
    // ==========================================
    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY dateTimestamp DESC")
    fun getAllJournalEntries(userId: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Query("SELECT * FROM audio_sessions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllAudioSessions(userId: String): Flow<List<AudioSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioSession(session: AudioSessionEntity): Long

    @Query("SELECT * FROM manifestations WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllManifestations(userId: String): Flow<List<ManifestationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManifestation(manifestation: ManifestationEntity): Long
}
