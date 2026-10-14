package com.example.data

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiVisionService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeImage(bitmap: Bitmap): AnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiVisionService", "Using local fallback analyzer (No Gemini API key provided)")
            return@withContext LocalVisionAnalyzer.analyze(bitmap)
        }

        try {
            val base64Image = bitmap.toBase64()
            val prompt = """
                You are VisionScene AI, an assistive visual AI designed to assist visually impaired individuals with scene understanding, object detection, depth estimation, and text reading.
                
                Analyze the provided image and respond strictly with a valid JSON object matching this structure (no markdown code blocks, no trailing comments):
                {
                  "caption": "Clear natural language description of the entire scene, surroundings, key elements and atmosphere.",
                  "indoorOutdoor": "e.g. Indoor Living Room / Outdoor Sidewalk / Office Corridor",
                  "lighting": "e.g. Well lit daylight / Dim indoor illumination",
                  "safetyAdvice": "Crucial hazard warnings or spatial navigation tips (e.g., Obstacle 1m ahead on left)",
                  "ocrText": "Text read from signs, posters, or documents in view (leave empty string if none)",
                  "audioNarration": "Concise 2-3 sentence narration spoken clearly to a blind user summarizing the scene and hazards.",
                  "detectedObjects": [
                    {
                      "label": "Object class (e.g. Chair, Door, Person, Table, Stairs, Car, Cup)",
                      "confidence": 0.95,
                      "left": 0.20,
                      "top": 0.40,
                      "right": 0.60,
                      "bottom": 0.85,
                      "distanceMeters": 1.2,
                      "position": "CENTER",
                      "depth": "FOREGROUND",
                      "isHazard": false,
                      "description": "Brief description"
                    }
                  ]
                }
                
                Note on bounding box coordinates: 'left', 'top', 'right', 'bottom' MUST be normalized floats between 0.0 and 1.0.
                'position' MUST be one of: "LEFT", "CENTER", "RIGHT".
                'depth' MUST be one of: "FOREGROUND", "MIDGROUND", "BACKGROUND".
                'isHazard' MUST be true if the object is close (< 1.5m) or represents a step/dropoff/doorway/obstacle in the direct walking path.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().put(
                    JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                            put(JSONObject().put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Image)
                            }))
                        }
                        put("parts", parts)
                    }
                )
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isBlank()) {
                Log.e("GeminiVisionService", "Gemini API HTTP Error ${response.code}: $responseString")
                return@withContext LocalVisionAnalyzer.analyze(bitmap)
            }

            val parsedResult = parseGeminiResponse(responseString)
            parsedResult ?: LocalVisionAnalyzer.analyze(bitmap)
        } catch (e: Exception) {
            Log.e("GeminiVisionService", "Exception calling Gemini API: ${e.message}", e)
            LocalVisionAnalyzer.analyze(bitmap)
        }
    }

    private fun parseGeminiResponse(jsonString: String): AnalysisResult? {
        return try {
            val rootObj = JSONObject(jsonString)
            val candidates = rootObj.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null

            val text = parts.getJSONObject(0).optString("text", "")
            if (text.isBlank()) return null

            // Clean json format
            val cleanJson = text.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val aiObj = JSONObject(cleanJson)

            val caption = aiObj.optString("caption", "Scene analyzed.")
            val indoorOutdoor = aiObj.optString("indoorOutdoor", "Environment detected")
            val lighting = aiObj.optString("lighting", "Standard lighting")
            val safetyAdvice = aiObj.optString("safetyAdvice", "Clear path ahead")
            val ocrText = aiObj.optString("ocrText", "")
            val audioNarration = aiObj.optString("audioNarration", caption)

            val detectedObjects = mutableListOf<DetectedObject>()
            val objectsArray = aiObj.optJSONArray("detectedObjects")
            if (objectsArray != null) {
                for (i in 0 until objectsArray.length()) {
                    val obj = objectsArray.getJSONObject(i)
                    val label = obj.optString("label", "Object")
                    val confidence = obj.optDouble("confidence", 0.90).toFloat()
                    val left = obj.optDouble("left", 0.2).toFloat().coerceIn(0f, 1f)
                    val top = obj.optDouble("top", 0.2).toFloat().coerceIn(0f, 1f)
                    val right = obj.optDouble("right", 0.8).toFloat().coerceIn(left + 0.05f, 1f)
                    val bottom = obj.optDouble("bottom", 0.8).toFloat().coerceIn(top + 0.05f, 1f)
                    val distanceMeters = obj.optDouble("distanceMeters", 1.5).toFloat()
                    
                    val posStr = obj.optString("position", "CENTER").uppercase()
                    val position = try {
                        SpatialPosition.valueOf(posStr)
                    } catch (e: Exception) {
                        SpatialPosition.CENTER
                    }

                    val depthStr = obj.optString("depth", "MIDGROUND").uppercase()
                    val depth = try {
                        DepthCategory.valueOf(depthStr)
                    } catch (e: Exception) {
                        if (distanceMeters < 1.5f) DepthCategory.FOREGROUND
                        else if (distanceMeters < 3.0f) DepthCategory.MIDGROUND
                        else DepthCategory.BACKGROUND
                    }

                    val isHazard = obj.optBoolean("isHazard", distanceMeters < 1.5f)
                    val description = obj.optString("description", "$label at ${distanceMeters}m")

                    detectedObjects.add(
                        DetectedObject(
                            id = "ai_obj_$i",
                            label = label,
                            confidence = confidence,
                            boundingBox = NormalizedRect(left, top, right, bottom),
                            distanceMeters = distanceMeters,
                            position = position,
                            depth = depth,
                            isHazard = isHazard,
                            description = description
                        )
                    )
                }
            }

            AnalysisResult(
                sceneDescription = SceneDescription(
                    caption = caption,
                    indoorOutdoor = indoorOutdoor,
                    lighting = lighting,
                    safetyAdvice = safetyAdvice,
                    ocrText = ocrText
                ),
                detectedObjects = detectedObjects,
                audioNarrationText = audioNarration
            )
        } catch (e: Exception) {
            Log.e("GeminiVisionService", "Error parsing Gemini JSON response", e)
            null
        }
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        // Resize bitmap if very large to optimize bandwidth
        val maxDim = 1024
        val scaledBitmap = if (width > maxDim || height > maxDim) {
            val scale = maxDim.toFloat() / Math.max(width, height)
            Bitmap.createScaledBitmap(this, (width * scale).toInt(), (height * scale).toInt(), true)
        } else {
            this
        }
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
