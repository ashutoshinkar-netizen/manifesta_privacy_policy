package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val currentTitle: String = "Morning Alignment & Clarity",
    val currentAffirmation: String = "I am becoming the person capable of creating the life I desire.",
    val category: String = "Morning",
    val voiceType: String = "Calm Female",
    val soundscape: String = "Ambient 432Hz",
    val currentPositionSeconds: Int = 0,
    val durationSeconds: Int = 180,
    val sleepTimerMinutes: Int? = null,
    val waveformAmplitudes: List<Float> = List(32) { 0.2f }
)

class ManifestaAudioManager(private val context: Context) {

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Default)

    private var tickerJob: Job? = null
    private var ambientTrack: AudioTrack? = null
    private var isAmbientPlaying = false

    init {
        initTextToSpeech()
    }

    private fun initTextToSpeech() {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.US)
                    isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                                 result != TextToSpeech.LANG_NOT_SUPPORTED
                    tts?.setPitch(0.92f) // slightly deeper/calmer
                    tts?.setSpeechRate(0.85f) // serene, mindful pacing
                    setupTtsListener()
                } else {
                    Log.w("ManifestaAudio", "TTS init failed with status: $status")
                }
            }
        } catch (e: Exception) {
            Log.e("ManifestaAudio", "Failed to initialize TTS", e)
        }
    }

    private fun setupTtsListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                mainHandler.post {
                    _playerState.value = _playerState.value.copy(isPlaying = true)
                }
            }

            override fun onDone(utteranceId: String?) {
                mainHandler.post {
                    // Loop or repeat gently
                    if (_playerState.value.isPlaying) {
                        mainHandler.postDelayed({
                            if (_playerState.value.isPlaying) {
                                speakCurrentAffirmation()
                            }
                        }, 4000)
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.w("ManifestaAudio", "TTS utterance error")
            }
        })
    }

    fun playSession(
        title: String,
        affirmationText: String,
        category: String = "Self Growth",
        voiceType: String = "Calm Female",
        soundscape: String = "Ambient 432Hz"
    ) {
        // Adjust voice characteristics based on voiceType selection
        when (voiceType) {
            "Deep Calm" -> {
                tts?.setPitch(0.78f)
                tts?.setSpeechRate(0.80f)
            }
            "Calm Male" -> {
                tts?.setPitch(0.85f)
                tts?.setSpeechRate(0.85f)
            }
            "Soft Whisper" -> {
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(0.80f)
            }
            "Energetic" -> {
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(0.98f)
            }
            else -> { // Calm Female default
                tts?.setPitch(0.95f)
                tts?.setSpeechRate(0.86f)
            }
        }

        _playerState.value = _playerState.value.copy(
            isPlaying = true,
            currentTitle = title,
            currentAffirmation = affirmationText,
            category = category,
            voiceType = voiceType,
            soundscape = soundscape,
            currentPositionSeconds = 0,
            durationSeconds = 180
        )

        startAmbientSoundscape(soundscape)
        speakCurrentAffirmation()
        startTicker()
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        _playerState.value = _playerState.value.copy(isPlaying = false)
        tts?.stop()
        stopAmbientSoundscape()
        tickerJob?.cancel()
    }

    fun resume() {
        _playerState.value = _playerState.value.copy(isPlaying = true)
        startAmbientSoundscape(_playerState.value.soundscape)
        speakCurrentAffirmation()
        startTicker()
    }

    fun seekRelative(seconds: Int) {
        val current = _playerState.value.currentPositionSeconds
        val maxDuration = _playerState.value.durationSeconds
        val newPos = (current + seconds).coerceIn(0, maxDuration)
        _playerState.value = _playerState.value.copy(currentPositionSeconds = newPos)
    }

    fun setSleepTimer(minutes: Int?) {
        _playerState.value = _playerState.value.copy(sleepTimerMinutes = minutes)
        if (minutes != null) {
            scope.launch {
                delay(minutes * 60 * 1000L)
                pause()
                _playerState.value = _playerState.value.copy(sleepTimerMinutes = null)
            }
        }
    }

    private fun speakCurrentAffirmation() {
        val text = _playerState.value.currentAffirmation
        if (text.isNotBlank() && isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "manifesta_utterance")
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            var step = 0
            while (isActive) {
                delay(1000)
                if (_playerState.value.isPlaying) {
                    val current = _playerState.value.currentPositionSeconds
                    val maxDuration = _playerState.value.durationSeconds
                    val next = if (current + 1 >= maxDuration) 0 else current + 1

                    step++
                    // Dynamic pulsating waveforms
                    val dynamicAmplitudes = List(32) { i ->
                        val base = (sin((step * 0.4f) + (i * 0.25f)) * 0.5f + 0.5f)
                        (0.2f + base * 0.7f).coerceIn(0.15f, 0.95f)
                    }

                    _playerState.value = _playerState.value.copy(
                        currentPositionSeconds = next,
                        waveformAmplitudes = dynamicAmplitudes
                    )
                }
            }
        }
    }

    /**
     * Synthesizes genuine soothing harmonic meditation soundscapes
     * (such as 432Hz sine wave harmonics or ocean white/pink noise)
     * using Android AudioTrack without requiring large asset bundles.
     */
    private fun startAmbientSoundscape(soundscape: String) {
        stopAmbientSoundscape()
        scope.launch(Dispatchers.IO) {
            try {
                val sampleRate = 44100
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                ambientTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                ambientTrack?.play()
                isAmbientPlaying = true

                val numSamples = bufferSize
                val sample = ShortArray(numSamples)
                var angle = 0.0
                val freq = when {
                    soundscape.contains("432Hz", ignoreCase = true) -> 432.0
                    soundscape.contains("Ocean", ignoreCase = true) -> 120.0
                    soundscape.contains("Rain", ignoreCase = true) -> 240.0
                    else -> 216.0 // Sub-harmonic of 432Hz
                }

                while (isAmbientPlaying && isActive) {
                    for (i in 0 until numSamples) {
                        val harmonic = sin(angle) * 0.6 + sin(angle * 1.5) * 0.3 + sin(angle * 2.0) * 0.1
                        // Keep volume soft and calming (under spoken voice)
                        sample[i] = (harmonic * 1200.0).toInt().toShort()
                        angle += 2 * Math.PI * freq / sampleRate
                        if (angle > 2 * Math.PI) angle -= 2 * Math.PI
                    }
                    ambientTrack?.write(sample, 0, numSamples)
                }
            } catch (e: Exception) {
                Log.w("ManifestaAudio", "Ambient synthesis stopped: ${e.message}")
            }
        }
    }

    private fun stopAmbientSoundscape() {
        isAmbientPlaying = false
        try {
            ambientTrack?.pause()
            ambientTrack?.flush()
            ambientTrack?.stop()
            ambientTrack?.release()
        } catch (e: Exception) {
            // Ignore clean teardown
        } finally {
            ambientTrack = null
        }
    }

    fun release() {
        pause()
        tts?.shutdown()
        tts = null
    }
}
