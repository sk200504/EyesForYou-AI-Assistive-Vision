package com.example.model

enum class DepthCategory(val displayName: String, val colorHex: Long) {
    FOREGROUND("Near (< 1.5m)", 0xFFEF4444),   // Red
    MIDGROUND("Mid (1.5 - 3m)", 0xFFF59E0B),    // Amber
    BACKGROUND("Far (> 3m)", 0xFF10B981)      // Emerald Green
}

enum class SpatialPosition(val displayName: String) {
    LEFT("Left"),
    CENTER("Center"),
    RIGHT("Right")
}

data class NormalizedRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = (right - left).coerceAtLeast(0.01f)
    val height: Float get() = (bottom - top).coerceAtLeast(0.01f)
    val centerX: Float get() = (left + right) / 2f
    val centerY: Float get() = (top + bottom) / 2f
}

data class DetectedObject(
    val id: String,
    val label: String,
    val confidence: Float,
    val boundingBox: NormalizedRect,
    val distanceMeters: Float,
    val position: SpatialPosition,
    val depth: DepthCategory,
    val isHazard: Boolean = false,
    val description: String = ""
)

data class SceneDescription(
    val caption: String,
    val indoorOutdoor: String,
    val lighting: String,
    val safetyAdvice: String,
    val ocrText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class AnalysisResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sceneDescription: SceneDescription,
    val detectedObjects: List<DetectedObject>,
    val imageUri: String? = null,
    val audioNarrationText: String,
    val isSample: Boolean = false
)

data class PresetScene(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val result: AnalysisResult
)
