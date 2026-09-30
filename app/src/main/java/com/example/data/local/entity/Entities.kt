package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User Profile & Account Data
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "local_user",
    val name: String = "Creator",
    val email: String = "",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isGuest: Boolean = true,
    val isPremium: Boolean = false,
    val premiumTier: String = "FREE", // "FREE", "PRO_ANNUAL", "PRO_LIFETIME"
    val subscriptionId: String = "",
    val subscriptionStartDate: Long? = null,
    val subscriptionEndDate: Long? = null,
    val currentStreak: Int = 7,
    val longestStreak: Int = 14,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val ritualsCompleted: Int = 12,
    val audioSessionsCount: Int = 18,
    val affirmationsCreatedCount: Int = 24
)

/**
 * Account Security & Authentication Credentials (Private to Auth layer)
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val passwordHash: String, // SHA-256 salted hash
    val salt: String,
    val authProvider: String = "EMAIL", // "EMAIL", "GOOGLE", "GUEST"
    val resetToken: String? = null,
    val resetTokenExpires: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Persistent Login Session
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val sessionId: String,
    val userId: String,
    val token: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * User Manifestation Goals (Full CRUD & Isolation)
 */
@Entity(tableName = "manifestation_goals")
data class ManifestationGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val title: String,
    val description: String,
    val category: String = "Self Growth",
    val targetDate: Long? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * AI Manifestation Generation History & Scripts
 */
@Entity(tableName = "ai_manifestation_history")
data class AiManifestationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val goal: String,
    val category: String,
    val emotion: String,
    val primaryAffirmation: String,
    val supporting1: String = "",
    val supporting2: String = "",
    val supporting3: String = "",
    val shortMantra: String = "",
    val morningText: String = "",
    val nightText: String = "",
    val manifestationScript: String = "",
    val whyExplanation: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Audio Affirmations Catalog & User Metadata
 */
@Entity(tableName = "audio_affirmations")
data class AudioAffirmationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val title: String,
    val affirmationText: String,
    val category: String,
    val voiceType: String = "Calm Female",
    val soundscape: String = "Ambient 432Hz",
    val durationSeconds: Int = 180,
    val audioFilePath: String = "",
    val isFavorite: Boolean = false,
    val isPremiumOnly: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Audio Playback Progress & History
 */
@Entity(tableName = "audio_playback_progress")
data class AudioPlaybackProgressEntity(
    @PrimaryKey val key: String, // "${userId}_${audioId}"
    val userId: String,
    val audioId: Long,
    val lastPositionMs: Long = 0,
    val playCount: Int = 1,
    val hasPlayed: Boolean = true,
    val lastPlayedAt: Long = System.currentTimeMillis()
)

/**
 * Daily Progress & Completed Sessions
 */
@Entity(tableName = "daily_progress")
data class DailyProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val dateKey: String, // "yyyy-MM-dd"
    val completedAffirmationText: String = "",
    val sessionsCount: Int = 1,
    val ritualsCompleted: String = "Morning",
    val totalSeconds: Int = 180,
    val completedAt: Long = System.currentTimeMillis()
)

/**
 * App Settings & Personal Preferences
 */
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val userId: String = "local_user",
    val notificationsEnabled: Boolean = true,
    val morningReminderTime: String = "08:00 AM",
    val eveningReminderTime: String = "09:30 PM",
    val selectedCategoriesJson: String = "Self Growth,Wealth,Peace,Career,Confidence",
    val voicePitch: Float = 1.0f,
    val speechRate: Float = 0.9f,
    val defaultSoundscape: String = "Ambient 432Hz",
    val backgroundVolume: Float = 0.4f,
    val hapticFeedbackEnabled: Boolean = true,
    val autoPlayAffirmations: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "manifestations")
data class ManifestationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val title: String,
    val goal: String,
    val category: String,
    val emotion: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "affirmations")
data class AffirmationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val manifestationId: Long? = null,
    val primaryText: String,
    val supporting1: String = "",
    val supporting2: String = "",
    val supporting3: String = "",
    val shortMantra: String = "",
    val morningText: String = "",
    val nightText: String = "",
    val whyExplanation: String = "",
    val category: String = "Self Growth",
    val isFavorite: Boolean = false,
    val isSaved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audio_sessions")
data class AudioSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val title: String,
    val affirmationText: String,
    val category: String,
    val voiceType: String = "Calm Female",
    val soundscape: String = "Ambient",
    val durationSeconds: Int = 180,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vision_cards")
data class VisionCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val title: String,
    val goal: String,
    val category: String,
    val affirmationText: String,
    val gradientIndex: Int = 0,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "local_user",
    val ritualType: String, // Morning, Afternoon, Night
    val intention: String,
    val reflection: String,
    val dateTimestamp: Long = System.currentTimeMillis()
)
