package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeneratedAffirmationBundle(
    val primaryText: String,
    val supporting1: String,
    val supporting2: String,
    val supporting3: String,
    val shortMantra: String,
    val morningText: String,
    val nightText: String,
    val manifestationScript: String = "",
    val whyExplanation: String,
    val category: String,
    val emotion: String
)

class GeminiAffirmationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateAffirmations(
        goal: String,
        category: String,
        emotion: String
    ): Result<GeneratedAffirmationBundle> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val result = callGeminiApi(apiKey, goal, category, emotion)
                if (result != null) {
                    return@withContext Result.success(result)
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "API call failed, switching to contextual generator", e)
            }
        }

        // Contextual generator fallback
        val synthesized = synthesizeAffirmations(goal, category, emotion)
        Result.success(synthesized)
    }

    private fun callGeminiApi(
        apiKey: String,
        goal: String,
        category: String,
        emotion: String
    ): GeneratedAffirmationBundle? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val systemPrompt = """
            You are MANIFESTA, an elite AI manifestation and affirmation companion.
            You create grounded, powerful, and deeply personal affirmations and manifestation scripts.
            Frame manifestation around intention, mindset, reflection, visualization, and consistent action.
            Avoid guaranteed claims about future events or supernatural promises.
            The tone must be calm, elevated, modern, encouraging, and natural.
            Output ONLY valid JSON adhering strictly to this schema:
            {
              "primaryText": "string",
              "supporting1": "string",
              "supporting2": "string",
              "supporting3": "string",
              "shortMantra": "string",
              "morningText": "string",
              "nightText": "string",
              "manifestationScript": "string",
              "whyExplanation": "string"
            }
        """.trimIndent()

        val userPrompt = """
            User Goal: "$goal"
            Category: "$category"
            Desired Feeling: "$emotion"
            
            Generate:
            1. primaryText: The main personal affirmation in first person present tense.
            2. supporting1, supporting2, supporting3: Three supporting affirmations exploring facets of this intention.
            3. shortMantra: A concise 3-5 word anchor mantra.
            4. morningText: A morning affirmation to start the day aligned with this intention.
            5. nightText: An evening reflection affirmation to release tension and anchor progress.
            6. manifestationScript: A 3-paragraph visualization and embodiment script guiding the user to feel this reality right now.
            7. whyExplanation: A thoughtful explanation of why this affirmation works for this goal and emotion.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
            })
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
            }))
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            Log.w("GeminiService", "API response error code: ${response.code}")
            return null
        }

        val responseBody = response.body?.string() ?: return null
        val rootJson = JSONObject(responseBody)
        val candidates = rootJson.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null

        val textContent = candidates.getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")

        val parsed = JSONObject(textContent)
        val defaultScript = buildDefaultScript(goal, category, emotion)
        return GeneratedAffirmationBundle(
            primaryText = parsed.optString("primaryText", "").ifEmpty { "I am becoming aligned with my highest aspirations." },
            supporting1 = parsed.optString("supporting1", "I take steady, disciplined action toward my goals."),
            supporting2 = parsed.optString("supporting2", "I cultivate deep self-trust and inner clarity."),
            supporting3 = parsed.optString("supporting3", "I welcome expansion and growth in every circumstance."),
            shortMantra = parsed.optString("shortMantra", "Anchored in purpose."),
            morningText = parsed.optString("morningText", "Today I show up with clear intention and peaceful focus."),
            nightText = parsed.optString("nightText", "I release the day with gratitude and rest in my progress."),
            manifestationScript = parsed.optString("manifestationScript", defaultScript),
            whyExplanation = parsed.optString("whyExplanation", "This affirmation aligns your mindset with positive intention and daily action."),
            category = category,
            emotion = emotion
        )
    }

    private fun buildDefaultScript(goal: String, category: String, emotion: String): String {
        return """
            Close your eyes, breathe in deeply for four counts, and gently release. Picture yourself standing in a serene sanctuary of your own design. The energy in the room feels light, warm, and distinctly peaceful.
            
            Notice the version of yourself who has already achieved $goal. You stand tall, your shoulders relaxed, radiating a calm, unmistakable aura of $emotion. Everything you sought is now woven effortlessly into your daily rhythm. The self-doubt has fallen away, replaced by steady mastery and deep gratitude.
            
            Place a hand over your heart. Feel the truth of this vibration vibrating through your chest. Breathe in the certainty that this reality begins with the thoughts, words, and small choices you make in this exact present moment. You are ready.
        """.trimIndent()
    }

    /**
     * Contextual affirmation generator that provides tailored affirmations
     * dynamically based on user input, category, and desired emotion.
     */
    fun synthesizeAffirmations(
        goal: String,
        category: String,
        emotion: String
    ): GeneratedAffirmationBundle {
        val cleanGoal = goal.trim().removePrefix("I want to ").removePrefix("I want ").removePrefix("to ")
        val goalPhrase = if (cleanGoal.isNotBlank()) cleanGoal else "cultivate inner peace and expansion"
        val script = buildDefaultScript(goalPhrase, category, emotion)

        val (primary, s1, s2, s3, mantra, morning, night, why) = when (category.lowercase()) {
            "wealth" -> {
                AffirmationTemplate(
                    primary = "I am becoming financially confident, disciplined, and capable of creating sustainable abundance as I $goalPhrase.",
                    s1 = "I make intentional financial decisions that expand my autonomy and peace of mind.",
                    s2 = "Value flows naturally from the energy, integrity, and focus I invest every day.",
                    s3 = "I transform limiting beliefs around money into responsible, empowering stewardship.",
                    mantra = "Abundance, discipline, freedom.",
                    morning = "Today I see and seize opportunities to create real value and prosper.",
                    night = "I rest with deep gratitude for my resources and trust in my financial future.",
                    why = "This affirmation pairs your desire to feel $emotion with steady discipline and practical money mindset."
                )
            }
            "love" -> {
                AffirmationTemplate(
                    primary = "I radiate warmth, authenticity, and healthy boundaries as I $goalPhrase.",
                    s1 = "I am deeply worthy of reciprocal, compassionate, and uplifting relationships.",
                    s2 = "By honoring my own heart first, I invite genuine connection and mutual respect.",
                    s3 = "I communicate my needs with gentle courage and listen with an open spirit.",
                    mantra = "Open heart, grounded soul.",
                    morning = "Today I share presence, kindness, and love with everyone I encounter.",
                    night = "I close this day anchored in self-love and cherished connections.",
                    why = "This affirmation roots your wish to feel $emotion in healthy relational boundaries and unconditional self-worth."
                )
            }
            "career" -> {
                AffirmationTemplate(
                    primary = "I step into my professional strength with calm competence as I $goalPhrase.",
                    s1 = "My unique perspectives and skills bring tangible value to every collaboration.",
                    s2 = "I meet high-stakes opportunities with preparation, poise, and steady execution.",
                    s3 = "I am not intimidated by expansion; I am equipped to lead and grow.",
                    mantra = "Focused, capable, unstoppable.",
                    morning = "Today I approach every project with sharp clarity and intentional effort.",
                    night = "I celebrate the meaningful strides I made today toward lasting mastery.",
                    why = "This affirmation aligns your aim to feel $emotion with concrete professional composure and self-efficacy."
                )
            }
            "peace" -> {
                AffirmationTemplate(
                    primary = "I preserve my inner serenity and respond with grace as I $goalPhrase.",
                    s1 = "I let go of what I cannot control and channel my energy into what nurtures me.",
                    s2 = "Stillness is my sanctuary, and from stillness comes my greatest wisdom.",
                    s3 = "I breathe out restlessness and welcome steady, quiet equilibrium.",
                    mantra = "Still within, calm throughout.",
                    morning = "Today I protect my peace and move through each moment with quiet poise.",
                    night = "I surrender all tension and allow deep, rejuvenating rest to restore me.",
                    why = "Focuses on emotional regulation, mindful surrender, and conscious stillness to embody $emotion."
                )
            }
            "confidence" -> {
                AffirmationTemplate(
                    primary = "I trust my capabilities and take decisive action as I $goalPhrase.",
                    s1 = "I speak with conviction, knowing my voice holds distinct importance and merit.",
                    s2 = "Failure is not my identity; it is simply guidance that refines my path.",
                    s3 = "I stand tall in who I am without seeking external validation.",
                    mantra = "Bold vision, quiet courage.",
                    morning = "Today I step forward boldly, unfazed by doubt and anchored in purpose.",
                    night = "I honor my courage today and rest in full certainty of my strength.",
                    why = "Builds internal validation and active courage tailored to help you feel genuinely $emotion."
                )
            }
            "lifestyle" -> {
                AffirmationTemplate(
                    primary = "I design a daily life filled with beauty, intention, and joy as I $goalPhrase.",
                    s1 = "My surroundings reflect the harmony, order, and inspiration I nurture within.",
                    s2 = "I choose quality, simplicity, and meaningful rituals over hurry and clutter.",
                    s3 = "Every environment I create fuels my vitality and creativity.",
                    mantra = "Curated ease, intentional joy.",
                    morning = "Today I savor the small blessings and craft a day that feels truly mine.",
                    night = "I look back on today with satisfaction and welcome peaceful slumber.",
                    why = "Encourages intentional environment curation and daily rhythm design to cultivate $emotion."
                )
            }
            else -> {
                AffirmationTemplate(
                    primary = "I am evolving into the person capable of achieving what I envision as I $goalPhrase.",
                    s1 = "Small, consistent daily practices shape my highest reality.",
                    s2 = "I honor both my current progress and my limitless potential for renewal.",
                    s3 = "I am ready for the opportunities that match the energy I cultivate.",
                    mantra = "Think it. Feel it. Become it.",
                    morning = "Today I align my thoughts and actions with the future I am building.",
                    night = "I rest peacefully knowing every day brings growth and fresh clarity.",
                    why = "This affirmation focuses on compounding personal momentum and intentional self-actualization to cultivate $emotion."
                )
            }
        }

        return GeneratedAffirmationBundle(
            primaryText = primary,
            supporting1 = s1,
            supporting2 = s2,
            supporting3 = s3,
            shortMantra = mantra,
            morningText = morning,
            nightText = night,
            manifestationScript = script,
            whyExplanation = why,
            category = category,
            emotion = emotion
        )
    }

    private data class AffirmationTemplate(
        val primary: String,
        val s1: String,
        val s2: String,
        val s3: String,
        val mantra: String,
        val morning: String,
        val night: String,
        val why: String
    )
}
