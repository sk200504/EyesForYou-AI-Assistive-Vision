package com.example.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SpeechEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("SpeechEngine", "Failed to initialize TextToSpeech: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("SpeechEngine", "English language not supported or missing")
            } else {
                isInitialized = true
                setupProgressListener()
            }
        } else {
            Log.e("SpeechEngine", "TTS initialization failed with status $status")
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    fun speak(text: String, isHazard: Boolean = false) {
        if (text.isBlank()) return

        if (isHazard) {
            triggerHazardHaptics()
        }

        if (isInitialized) {
            tts?.setSpeechRate(_speechRate.value)
            tts?.setPitch(_pitch.value)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "VisionSpeechId_${System.currentTimeMillis()}")
            _isSpeaking.value = true
        }
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
        _isSpeaking.value = false
    }

    fun setSpeechRate(rate: Float) {
        val clampedRate = rate.coerceIn(0.5f, 2.0f)
        _speechRate.value = clampedRate
        if (isInitialized) {
            tts?.setSpeechRate(clampedRate)
        }
    }

    fun setPitch(pitch: Float) {
        val clampedPitch = pitch.coerceIn(0.5f, 1.8f)
        _pitch.value = clampedPitch
        if (isInitialized) {
            tts?.setPitch(clampedPitch)
        }
    }

    fun triggerHazardHaptics() {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val pattern = longArrayOf(0, 150, 100, 200)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(300)
                }
            }
        } catch (e: Exception) {
            Log.e("SpeechEngine", "Vibration failed: ${e.message}")
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("SpeechEngine", "Shutdown error: ${e.message}")
        }
    }
}
