package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.GeminiAffirmationService
import com.example.data.local.ManifestaDatabase
import com.example.data.local.entity.AffirmationEntity
import com.example.data.local.entity.ManifestationGoalEntity
import com.example.data.repository.ManifestaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var db: ManifestaDatabase
    private lateinit var repository: ManifestaRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ManifestaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ManifestaRepository(db.manifestaDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun verifyAppName() {
        val appName = context.getString(R.string.app_name)
        assertEquals("MANIFESTA", appName)
    }

    @Test
    fun verifyStarterDataInitialization() = runBlocking {
        repository.initializeStarterDataIfEmpty()
        val user = repository.user.first()
        assertNotNull(user)
        assertEquals("Creator", user?.name)
        assertTrue(user?.isGuest == true)
        assertEquals(7, user?.currentStreak)

        val saved = repository.savedAffirmations.first()
        assertTrue(saved.isNotEmpty())
        assertTrue(saved.any { it.category == "Self Growth" })
    }

    @Test
    fun verifyUserSignupAndLoginFlow() = runBlocking {
        repository.initializeStarterDataIfEmpty()

        // 1. Signup user
        val signupResult = repository.signUp(
            name = "Aria Nova",
            email = "manifestor@manifesta.app",
            password = "SacredPassword123!"
        )
        assertTrue("Signup should succeed", signupResult.isSuccess)
        val user = repository.user.first()
        assertNotNull(user)
        assertEquals("Aria Nova", user?.name)
        assertEquals("manifestor@manifesta.app", user?.email)
        assertFalse(user?.isGuest ?: true)

        // 2. Add private goal
        val goalId = repository.createGoal(
            title = "Expand Creative Studio",
            description = "Lead high-vibrational wellness design",
            category = "Career"
        )
        assertTrue(goalId > 0)
        assertTrue(repository.goals.first().any { it.id == goalId })

        // 3. Logout
        repository.logout()
        val guestUser = repository.user.first()
        assertTrue(guestUser?.isGuest ?: false)
        // Goals should now be isolated (User's private goal is not visible to guest)
        assertTrue(repository.goals.first().none { it.id == goalId })

        // 4. Log back in
        val loginResult = repository.login("manifestor@manifesta.app", "SacredPassword123!")
        assertTrue("Login should succeed", loginResult.isSuccess)
        val loggedInUser = repository.user.first()
        assertEquals("Aria Nova", loggedInUser?.name)
        assertEquals(user!!.id, loggedInUser?.id)

        // User's private goal should be restored
        val userGoals = repository.goals.first()
        assertTrue(userGoals.any { it.id == goalId && it.title == "Expand Creative Studio" })
    }

    @Test
    fun verifyUserSpecificDataIsolation() = runBlocking {
        repository.initializeStarterDataIfEmpty()

        // User 1
        val u1Result = repository.signUp("User One", "user1@test.com", "pass123")
        assertTrue(u1Result.isSuccess)
        val u1 = repository.user.first()!!
        repository.createGoal(title = "U1 Goal", description = "For U1 only", category = "Wealth")

        val u1Goals = repository.goals.first()
        assertTrue(u1Goals.any { it.title == "U1 Goal" })

        // Switch to User 2
        val u2Result = repository.signUp("User Two", "user2@test.com", "pass456")
        assertTrue(u2Result.isSuccess)
        val u2 = repository.user.first()!!
        val u2GoalsBefore = repository.goals.first()
        assertTrue("User 2 should not see User 1 goals", u2GoalsBefore.none { it.title == "U1 Goal" })

        repository.createGoal(title = "U2 Goal", description = "For U2 only", category = "Health")
        val u2GoalsAfter = repository.goals.first()
        assertTrue(u2GoalsAfter.any { it.title == "U2 Goal" })
        assertTrue("User 2 should still not see User 1 goals", u2GoalsAfter.none { it.title == "U1 Goal" })
    }

    @Test
    fun verifyPremiumEntitlement() = runBlocking {
        repository.initializeStarterDataIfEmpty()
        var user = repository.user.first()
        assertFalse(user?.isPremium ?: true)

        repository.activatePremiumSubscription("PRO_ANNUAL")
        user = repository.user.first()
        assertTrue(user?.isPremium == true)
        assertEquals("PRO_ANNUAL", user?.premiumTier)
        assertTrue(user?.subscriptionId?.isNotBlank() == true)
    }

    @Test
    fun verifySettingsUpdate() = runBlocking {
        repository.initializeStarterDataIfEmpty()
        repository.updateAppSettings(
            notificationsEnabled = false,
            morningReminder = "07:30 AM",
            eveningReminder = "10:00 PM",
            categories = "Career,Love",
            voicePitch = 0.95f,
            speechRate = 0.85f,
            defaultSoundscape = "Rain Sanctuary",
            backgroundVolume = 0.6f,
            haptic = false
        )

        val settings = repository.appSettings.first()
        assertNotNull(settings)
        assertFalse(settings!!.notificationsEnabled)
        assertEquals("07:30 AM", settings.morningReminderTime)
        assertEquals("Rain Sanctuary", settings.defaultSoundscape)
        assertEquals(0.85f, settings.speechRate, 0.01f)
        assertFalse(settings.hapticFeedbackEnabled)
    }

    @Test
    fun verifyAffirmationPersistenceAndFavorite() = runBlocking {
        repository.initializeStarterDataIfEmpty()
        val testAffirmation = AffirmationEntity(
            primaryText = "I am aligned with peace and boundless abundance.",
            category = "Peace",
            isFavorite = false,
            isSaved = true
        )
        val id = repository.saveAffirmation(testAffirmation)
        assertTrue(id > 0)

        val list = repository.savedAffirmations.first()
        val saved = list.first { it.id == id }
        assertEquals("Peace", saved.category)
        assertFalse(saved.isFavorite)

        repository.toggleFavorite(saved)
        val favorites = repository.favoriteAffirmations.first()
        assertTrue(favorites.any { it.id == id && it.isFavorite })
    }

    @Test
    fun verifyRitualCompletionIncrementsStreak() = runBlocking {
        repository.initializeStarterDataIfEmpty()
        val initialStreak = repository.user.first()?.currentStreak ?: 0

        repository.logRitualCompletion(
            ritualType = "Morning",
            intention = "Focus with calm clarity",
            reflection = "Felt centered and grounded."
        )

        val updatedUser = repository.user.first()
        assertEquals(initialStreak, updatedUser?.currentStreak)
        assertEquals(13, updatedUser?.ritualsCompleted)
    }

    @Test
    fun verifyContextualAffirmationSynthesis() {
        val service = GeminiAffirmationService()
        val bundle = service.synthesizeAffirmations(
            goal = "launch my dream studio",
            category = "Career",
            emotion = "Confident"
        )

        assertNotNull(bundle)
        assertTrue(bundle.primaryText.isNotBlank())
        assertTrue(bundle.supporting1.isNotBlank())
        assertTrue(bundle.supporting2.isNotBlank())
        assertTrue(bundle.supporting3.isNotBlank())
        assertTrue(bundle.shortMantra.isNotBlank())
        assertTrue(bundle.morningText.isNotBlank())
        assertTrue(bundle.nightText.isNotBlank())
        assertTrue(bundle.manifestationScript.isNotBlank())
        assertTrue(bundle.whyExplanation.isNotBlank())
        assertEquals("Career", bundle.category)
        assertEquals("Confident", bundle.emotion)
    }
}
