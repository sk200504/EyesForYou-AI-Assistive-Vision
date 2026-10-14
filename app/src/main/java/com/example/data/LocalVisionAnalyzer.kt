package com.example.data

import android.graphics.Bitmap
import android.graphics.Color
import com.example.model.*

object LocalVisionAnalyzer {

    fun analyze(bitmap: Bitmap): AnalysisResult {
        val width = bitmap.width
        val height = bitmap.height

        // Calculate average brightness and spatial light distribution
        var totalLum = 0L
        val sampleStep = (width * height / 1000).coerceAtLeast(1)
        var count = 0

        for (y in 0 until height step (sampleStep / width).coerceAtLeast(1)) {
            for (x in 0 until width step 20) {
                if (x < width && y < height) {
                    val pixel = bitmap.getPixel(x, y)
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)
                    val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                    totalLum += lum
                    count++
                }
            }
        }

        val avgLum = if (count > 0) (totalLum / count) else 128
        val lightingStr = when {
            avgLum > 180 -> "Bright daylight illumination"
            avgLum > 100 -> "Moderate indoor lighting"
            else -> "Low light ambient environment"
        }

        // Object layout generator simulating COCO object detection + MiDaS depth scale
        val detectedObjects = listOf(
            DetectedObject(
                id = "loc_obj_1",
                label = "Chair / Seating",
                confidence = 0.94f,
                boundingBox = NormalizedRect(0.20f, 0.45f, 0.55f, 0.88f),
                distanceMeters = 1.2f,
                position = SpatialPosition.LEFT,
                depth = DepthCategory.FOREGROUND,
                isHazard = true,
                description = "Armchair positioned 1.2 meters to your left"
            ),
            DetectedObject(
                id = "loc_obj_2",
                label = "Work Desk & Laptop",
                confidence = 0.96f,
                boundingBox = NormalizedRect(0.40f, 0.35f, 0.85f, 0.75f),
                distanceMeters = 1.8f,
                position = SpatialPosition.CENTER,
                depth = DepthCategory.MIDGROUND,
                isHazard = false,
                description = "Flat surface table with open laptop computer"
            ),
            DetectedObject(
                id = "loc_obj_3",
                label = "Doorway Entrance",
                confidence = 0.89f,
                boundingBox = NormalizedRect(0.70f, 0.15f, 0.95f, 0.70f),
                distanceMeters = 3.2f,
                position = SpatialPosition.RIGHT,
                depth = DepthCategory.BACKGROUND,
                isHazard = false,
                description = "Open doorway leading into neighboring hallway"
            ),
            DetectedObject(
                id = "loc_obj_4",
                label = "Water Bottle",
                confidence = 0.91f,
                boundingBox = NormalizedRect(0.48f, 0.42f, 0.58f, 0.58f),
                distanceMeters = 1.0f,
                position = SpatialPosition.CENTER,
                depth = DepthCategory.FOREGROUND,
                isHazard = false,
                description = "Clear bottle standing upright on table"
            )
        )

        val caption = "Indoor workspace environment featuring an armchair on the left, a work desk with laptop directly ahead, and an open doorway on the far right under $lightingStr."
        val safetyAdvice = "Chair obstacle located 1.2 meters to your left. Straight path ahead towards the table is clear."
        val ocrText = "ROOM 204 - VISUAL RESEARCH LAB"
        val narration = "Scene analysis: Indoor room with a chair 1.2 meters on your left and a desk directly ahead at 1.8 meters. Clear path straight ahead."

        return AnalysisResult(
            sceneDescription = SceneDescription(
                caption = caption,
                indoorOutdoor = "Indoor Room / Workspace",
                lighting = lightingStr,
                safetyAdvice = safetyAdvice,
                ocrText = ocrText
            ),
            detectedObjects = detectedObjects,
            audioNarrationText = narration
        )
    }
}
