package com.example.data

import android.graphics.Bitmap
import com.example.model.AnalysisResult
import com.example.model.PresetData
import com.example.model.PresetScene
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VisionRepository {

    private val geminiService = GeminiVisionService()

    private val _history = MutableStateFlow<List<AnalysisResult>>(emptyList())
    val history: StateFlow<List<AnalysisResult>> = _history.asStateFlow()

    private val _savedBookmarkedResults = MutableStateFlow<List<AnalysisResult>>(emptyList())
    val savedBookmarkedResults: StateFlow<List<AnalysisResult>> = _savedBookmarkedResults.asStateFlow()

    fun getPresets(): List<PresetScene> = PresetData.sampleScenes

    suspend fun analyzeImage(bitmap: Bitmap): AnalysisResult {
        val result = geminiService.analyzeImage(bitmap)
        addToHistory(result)
        return result
    }

    private fun addToHistory(result: AnalysisResult) {
        val updated = mutableListOf(result)
        updated.addAll(_history.value.filter { it.id != result.id })
        _history.value = updated.take(20) // Keep last 20 analyses
    }

    fun toggleBookmark(result: AnalysisResult) {
        val current = _savedBookmarkedResults.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == result.id }
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
        } else {
            current.add(0, result)
        }
        _savedBookmarkedResults.value = current
    }

    fun isBookmarked(id: String): Boolean {
        return _savedBookmarkedResults.value.any { it.id == id }
    }

    fun clearHistory() {
        _history.value = emptyList()
    }
}
