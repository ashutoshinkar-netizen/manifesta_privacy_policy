package com.example.data.firebase

import android.util.Log
import com.example.data.local.entity.AffirmationEntity
import com.example.data.local.entity.AiManifestationHistoryEntity
import com.example.data.local.entity.ManifestationGoalEntity
import com.example.data.local.entity.UserEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Service managing Firebase Authentication & Cloud Firestore synchronization for MANIFESTA.
 * Reuses singleton instances of FirebaseAuth and FirebaseFirestore initialized with FirebaseApp.
 */
class FirebaseSyncService {

    companion object {
        private const val TAG = "FirebaseSyncService"
        const val USERS_COLLECTION = "users"
        const val GOALS_COLLECTION = "goals"
        const val AFFIRMATIONS_COLLECTION = "affirmations"
        const val AI_HISTORY_COLLECTION = "ai_history"

        /**
         * Formats Firebase Auth and Cloud Firestore errors into clear, user-friendly messages.
         */
        fun formatFirebaseError(throwable: Throwable): String {
            val rawMessage = throwable.message ?: ""
            val lower = rawMessage.lowercase()

            return when {
                throwable is FirebaseAuthUserCollisionException ||
                        lower.contains("email-already-in-use") ||
                        lower.contains("already in use") ||
                        lower.contains("error_email_already_in_use") -> {
                    "An account already exists with this email address. Please log in instead."
                }

                throwable is FirebaseAuthWeakPasswordException ||
                        lower.contains("weak-password") ||
                        lower.contains("weak_password") ||
                        lower.contains("at least 6 characters") -> {
                    (throwable as? FirebaseAuthWeakPasswordException)?.reason
                        ?: "The password is too weak. Please use at least 6 characters."
                }

                throwable is FirebaseAuthInvalidCredentialsException ||
                        lower.contains("invalid-email") ||
                        lower.contains("error_invalid_email") ||
                        lower.contains("badly formatted") -> {
                    if (lower.contains("email")) {
                        "The email address is invalid or badly formatted."
                    } else {
                        "Invalid credentials. Please check your email and password."
                    }
                }

                throwable is FirebaseAuthInvalidUserException ||
                        lower.contains("user-not-found") ||
                        lower.contains("error_user_not_found") ||
                        lower.contains("user-disabled") -> {
                    "No account found with this email, or the account has been disabled."
                }

                lower.contains("wrong-password") ||
                        lower.contains("error_wrong_password") -> {
                    "Incorrect password. Please verify and try again."
                }

                throwable is FirebaseNetworkException ||
                        lower.contains("network-request-failed") ||
                        lower.contains("network_error") ||
                        lower.contains("unable to resolve host") ||
                        lower.contains("connection refused") -> {
                    "Network error. Please check your internet connection and try again."
                }

                throwable is FirebaseFirestoreException &&
                        throwable.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ||
                        lower.contains("permission-denied") ||
                        lower.contains("permission_denied") -> {
                    "Cloud Firestore permission denied. Please verify your Firestore security rules in the Firebase Console."
                }

                throwable is FirebaseFirestoreException &&
                        throwable.code == FirebaseFirestoreException.Code.UNAVAILABLE ||
                        lower.contains("unavailable") -> {
                    "Cloud Firestore is temporarily unavailable. Please try again shortly."
                }

                throwable is FirebaseAuthException -> {
                    when (throwable.errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email address. Please log in instead."
                        "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
                        "ERROR_USER_NOT_FOUND" -> "No account found with this email address."
                        "ERROR_INVALID_EMAIL" -> "The email address is invalid."
                        "ERROR_WEAK_PASSWORD" -> "The password must be at least 6 characters."
                        else -> throwable.message ?: "Authentication failed. Please try again."
                    }
                }

                rawMessage.isNotBlank() -> rawMessage
                else -> "Authentication failed. Please verify your credentials and try again."
            }
        }
    }

    val isFirebaseInitialized: Boolean
        get() = try {
            FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()
        } catch (e: Exception) {
            false
        }

    val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseAuth initialization error: ${e.message}")
            null
        }
    }

    val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseFirestore initialization error: ${e.message}")
            null
        }
    }

    val currentFirebaseUser: FirebaseUser?
        get() = auth?.currentUser

    /**
     * Creates or updates the user profile document in Cloud Firestore at users/{uid}.
     * Must be called only after Firebase Authentication succeeds.
     * Guaranteed never to store the user's password.
     *
     * @param uid The Firebase Authentication UID (used as document ID and stored as 'uid')
     * @param email The user's registered email
     * @param displayName The user's display name if provided
     * @throws Exception if Firestore write fails (e.g., PERMISSION_DENIED or network error)
     */
    suspend fun createFirestoreUserDocument(
        uid: String,
        email: String,
        displayName: String?,
        isGuest: Boolean = false,
        isPremium: Boolean = false,
        premiumTier: String = "FREE"
    ) {
        val db = firestore ?: throw IllegalStateException("Cloud Firestore is not initialized. Please verify Firebase configuration.")

        val now = System.currentTimeMillis()
        val userMap = hashMapOf<String, Any?>(
            "uid" to uid,
            "id" to uid,
            "email" to email,
            "displayName" to displayName?.ifBlank { null },
            "name" to (displayName?.ifBlank { null } ?: email.substringBefore("@")),
            "createdAt" to now,
            "updatedAt" to now,
            "isGuest" to isGuest,
            "isPremium" to isPremium,
            "premiumTier" to premiumTier,
            "currentStreak" to 1,
            "longestStreak" to 1,
            "ritualsCompleted" to 0
        )

        // Document ID is strictly the Firebase Authentication UID
        db.collection(USERS_COLLECTION)
            .document(uid)
            .set(userMap, SetOptions.merge())
            .await()

        Log.d(TAG, "Firestore user document created at $USERS_COLLECTION/$uid")
    }

    /**
     * Fetches user profile data from Firestore at users/{uid}.
     */
    suspend fun fetchFirestoreUserDocument(uid: String): Map<String, Any?>? {
        val db = firestore ?: return null
        return try {
            val doc = db.collection(USERS_COLLECTION).document(uid).get().await()
            if (doc.exists()) doc.data else null
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching user document for $uid: ${e.message}")
            throw e
        }
    }

    /**
     * Synchronizes the user profile to Firestore at users/{user.id}
     */
    suspend fun syncUserToFirestore(user: UserEntity): Boolean {
        val db = firestore ?: return false
        return try {
            val userMap = hashMapOf(
                "uid" to user.id,
                "id" to user.id,
                "name" to user.name,
                "displayName" to user.name,
                "email" to user.email,
                "photoUrl" to user.photoUrl,
                "isGuest" to user.isGuest,
                "isPremium" to user.isPremium,
                "premiumTier" to user.premiumTier,
                "currentStreak" to user.currentStreak,
                "longestStreak" to user.longestStreak,
                "ritualsCompleted" to user.ritualsCompleted,
                "lastActiveTimestamp" to user.lastActiveTimestamp,
                "createdAt" to user.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection(USERS_COLLECTION)
                .document(user.id)
                .set(userMap, SetOptions.merge())
                .await()
            Log.d(TAG, "User profile synchronized to Firestore: ${user.id}")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Firestore sync skipped or offline: ${e.message}")
            false
        }
    }

    /**
     * Synchronizes a manifestation goal to Firestore under user subcollection: users/{userId}/goals/{goalId}
     */
    suspend fun syncGoalToFirestore(userId: String, goal: ManifestationGoalEntity): Boolean {
        val db = firestore ?: return false
        return try {
            val goalMap = hashMapOf(
                "id" to goal.id,
                "userId" to userId,
                "title" to goal.title,
                "description" to goal.description,
                "category" to goal.category,
                "targetDate" to goal.targetDate,
                "isCompleted" to goal.isCompleted,
                "completedAt" to goal.completedAt,
                "createdAt" to goal.createdAt,
                "updatedAt" to goal.updatedAt
            )
            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(GOALS_COLLECTION)
                .document(goal.id.toString())
                .set(goalMap, SetOptions.merge())
                .await()
            Log.d(TAG, "Goal synced to Firestore: ${goal.id}")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Goal sync skipped: ${e.message}")
            false
        }
    }

    /**
     * Synchronizes a saved affirmation to Firestore: users/{userId}/affirmations/{affirmationId}
     */
    suspend fun syncAffirmationToFirestore(userId: String, affirmation: AffirmationEntity): Boolean {
        val db = firestore ?: return false
        return try {
            val affirmationMap = hashMapOf(
                "id" to affirmation.id,
                "userId" to userId,
                "manifestationId" to affirmation.manifestationId,
                "primaryText" to affirmation.primaryText,
                "supporting1" to affirmation.supporting1,
                "supporting2" to affirmation.supporting2,
                "supporting3" to affirmation.supporting3,
                "shortMantra" to affirmation.shortMantra,
                "morningText" to affirmation.morningText,
                "nightText" to affirmation.nightText,
                "whyExplanation" to affirmation.whyExplanation,
                "category" to affirmation.category,
                "isFavorite" to affirmation.isFavorite,
                "isSaved" to affirmation.isSaved,
                "createdAt" to affirmation.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(AFFIRMATIONS_COLLECTION)
                .document(affirmation.id.toString())
                .set(affirmationMap, SetOptions.merge())
                .await()
            Log.d(TAG, "Affirmation synced to Firestore: ${affirmation.id}")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Affirmation sync skipped: ${e.message}")
            false
        }
    }

    /**
     * Synchronizes an AI synthesized ritual history to Firestore: users/{userId}/ai_history/{historyId}
     */
    suspend fun syncAiHistoryToFirestore(userId: String, history: AiManifestationHistoryEntity): Boolean {
        val db = firestore ?: return false
        return try {
            val historyMap = hashMapOf(
                "id" to history.id,
                "userId" to userId,
                "goal" to history.goal,
                "category" to history.category,
                "emotion" to history.emotion,
                "primaryAffirmation" to history.primaryAffirmation,
                "supporting1" to history.supporting1,
                "supporting2" to history.supporting2,
                "supporting3" to history.supporting3,
                "shortMantra" to history.shortMantra,
                "morningText" to history.morningText,
                "nightText" to history.nightText,
                "manifestationScript" to history.manifestationScript,
                "whyExplanation" to history.whyExplanation,
                "createdAt" to history.createdAt,
                "syncedAt" to System.currentTimeMillis()
            )
            db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(AI_HISTORY_COLLECTION)
                .document(history.id.toString())
                .set(historyMap, SetOptions.merge())
                .await()
            Log.d(TAG, "AI history synced to Firestore: ${history.id}")
            true
        } catch (e: Exception) {
            Log.w(TAG, "AI history sync skipped: ${e.message}")
            false
        }
    }
}
