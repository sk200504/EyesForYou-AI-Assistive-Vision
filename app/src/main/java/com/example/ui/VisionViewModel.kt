package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VisionRepository
import com.example.model.AnalysisResult
import com.example.model.PresetScene
import com.example.service.SpeechEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnalysisUiState {
    object Idle : AnalysisUiState
    object Analyzing : AnalysisUiState
    data class Success(val result: AnalysisResult) : AnalysisUiState
    data class Error(val message: String) : AnalysisUiState
}

enum class VisionMode(val title: String, val subtitle: String, val iconName: String) {
    ALL_SCENE("Scene Caption", "BLIP natural description", "ic_scene"),
    OBJECTS_SPATIAL("Spatial Layout", "YOLOv8 object positions", "ic_spatial"),
    DEPTH_ESTIMATION("Depth & Distance", "MiDaS relative distance", "ic_depth"),
    OCR_TEXT("Sign & Text Read", "OCR text-to-speech", "ic_text"),
    HAZARDS("Hazard Detect", "Obstacle & step warning", "ic_hazard")
}

class VisionViewModel(application: Application) : AndroidViewModel(application) {

    val repository = VisionRepository()
    val speechEngine = SpeechEngine(application)

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    private val _currentBitmap = MutableStateFlow<Bitmap?>(null)
    val currentBitmap: StateFlow<Bitmap?> = _currentBitmap.asStateFlow()

    private val _currentResult = MutableStateFlow<AnalysisResult?>(null)
    val currentResult: StateFlow<AnalysisResult?> = _currentResult.asStateFlow()

    private val _activeMode = MutableStateFlow(VisionMode.ALL_SCENE)
    val activeMode: StateFlow<VisionMode> = _activeMode.asStateFlow()

    private val _showBoundingBoxes = MutableStateFlow(true)
    val showBoundingBoxes: StateFlow<Boolean> = _showBoundingBoxes.asStateFlow()

    private val _showDepthHeatmap = MutableStateFlow(false)
    val showDepthHeatmap: StateFlow<Boolean> = _showDepthHeatmap.asStateFlow()

    private val _autoNarrateOnAnalyze = MutableStateFlow(true)
    val autoNarrateOnAnalyze: StateFlow<Boolean> = _autoNarrateOnAnalyze.asStateFlow()

    val history = repository.history
    val bookmarks = repository.savedBookmarkedResults
    val isSpeaking = speechEngine.isSpeaking
    val speechRate = speechEngine.speechRate

    init {
        // Load initial preset living room scene for immediate interactive demo
        val defaultPreset = repository.getPresets().first()
        selectPreset(defaultPreset)
    }

    fun selectPreset(preset: PresetScene) {
        speechEngine.stop()
        _currentBitmap.value = null
        _currentResult.value = preset.result
        _uiState.value = AnalysisUiState.Success(preset.result)

        if (_autoNarrateOnAnalyze.value) {
            speakNarration(preset.result)
        }
    }

    fun analyzeBitmap(bitmap: Bitmap) {
        speechEngine.stop()
        _currentBitmap.value = bitmap
        _uiState.value = AnalysisUiState.Analyzing

        viewModelScope.launch {
            try {
                val result = repository.analyzeImage(bitmap)
                _currentResult.value = result
                _uiState.value = AnalysisUiState.Success(result)

                if (_autoNarrateOnAnalyze.value) {
                    speakNarration(result)
                }
            } catch (e: Exception) {
                _uiState.value = AnalysisUiState.Error(e.message ?: "Failed to analyze image.")
            }
        }
    }

    fun speakNarration(result: AnalysisResult? = _currentResult.value) {
        result?.let { res ->
            val hasHazard = res.detectedObjects.any { it.isHazard && it.distanceMeters < 1.5f }
            val textToSpeak = when (_activeMode.value) {
                VisionMode.ALL_SCENE -> res.audioNarrationText
                VisionMode.OBJECTS_SPATIAL -> {
                    val objSummary = res.detectedObjects.joinToString("; ") {
                        "${it.label} ${it.position.displayName} at ${it.distanceMeters} meters"
                    }
                    if (objSummary.isNotBlank()) "Detected objects: $objSummary" else res.sceneDescription.caption
                }
                VisionMode.DEPTH_ESTIMATION -> {
                    val foregrounds = res.detectedObjects.filter { it.distanceMeters < 1.5f }
                    if (foregrounds.isNotEmpty()) {
                        "Nearest objects within 1.5 meters: " + foregrounds.joinToString(", ") { "${it.label} at ${it.distanceMeters}m ${it.position.displayName}" }
                    } else "No objects detected in immediate 1.5 meter range."
                }
                VisionMode.OCR_TEXT -> {
                    if (res.sceneDescription.ocrText.isNotBlank()) "Extracted text says: ${res.sceneDescription.ocrText}"
                    else "No clear text or signboards detected in this scene."
                }
                VisionMode.HAZARDS -> {
                    "Safety Alert: ${res.sceneDescription.safetyAdvice}"
                }
            }
            speechEngine.speak(textToSpeak, isHazard = hasHazard)
        }
    }

    fun stopNarration() {
        speechEngine.stop()
    }

    fun setMode(mode: VisionMode) {
        _activeMode.value = mode
        if (mode == VisionMode.DEPTH_ESTIMATION) {
            _showDepthHeatmap.value = true
        }
    }

    fun toggleBoundingBoxes() {
        _showBoundingBoxes.value = !_showBoundingBoxes.value
    }

    fun toggleDepthHeatmap() {
        _showDepthHeatmap.value = !_showDepthHeatmap.value
    }

    fun toggleAutoNarrate() {
        _autoNarrateOnAnalyze.value = !_autoNarrateOnAnalyze.value
    }

    fun setSpeechRate(rate: Float) {
        speechEngine.setSpeechRate(rate)
    }

    fun toggleBookmark(result: AnalysisResult) {
        repository.toggleBookmark(result)
    }

    fun isBookmarked(result: AnalysisResult): Boolean {
        return repository.isBookmarked(result.id)
    }

    override fun onCleared() {
        super.onCleared()
        speechEngine.shutdown()
    }
}
