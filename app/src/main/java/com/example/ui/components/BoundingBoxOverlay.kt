package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.model.DepthCategory
import com.example.model.DetectedObject
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RedHazard

@Composable
fun BoundingBoxOverlay(
    detectedObjects: List<DetectedObject>,
    showBoundingBoxes: Boolean,
    showDepthHeatmap: Boolean,
    onObjectClick: (DetectedObject) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("bounding_box_canvas")
            .pointerInput(detectedObjects, showBoundingBoxes) {
                detectTapGestures { tapOffset ->
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Find tapped object
                    val tappedObj = detectedObjects.firstOrNull { obj ->
                        val leftPx = obj.boundingBox.left * canvasWidth
                        val topPx = obj.boundingBox.top * canvasHeight
                        val rightPx = obj.boundingBox.right * canvasWidth
                        val bottomPx = obj.boundingBox.bottom * canvasHeight

                        tapOffset.x in leftPx..rightPx && tapOffset.y in topPx..bottomPx
                    }

                    tappedObj?.let { onObjectClick(it) }
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (detectedObjects.isEmpty()) return@Canvas

        detectedObjects.forEach { obj ->
            val leftPx = obj.boundingBox.left * canvasWidth
            val topPx = obj.boundingBox.top * canvasHeight
            val widthPx = obj.boundingBox.width * canvasWidth
            val heightPx = obj.boundingBox.height * canvasHeight

            val color = when {
                obj.isHazard || obj.depth == DepthCategory.FOREGROUND -> RedHazard
                obj.depth == DepthCategory.MIDGROUND -> AmberWarning
                else -> EmeraldSuccess
            }

            // Depth Heatmap Overlay fill
            if (showDepthHeatmap) {
                val alpha = when (obj.depth) {
                    DepthCategory.FOREGROUND -> 0.45f
                    DepthCategory.MIDGROUND -> 0.30f
                    DepthCategory.BACKGROUND -> 0.15f
                }
                drawRect(
                    color = color.copy(alpha = alpha),
                    topLeft = Offset(leftPx, topPx),
                    size = Size(widthPx, heightPx)
                )
            }

            // Draw YOLOv8 style Bounding Boxes
            if (showBoundingBoxes) {
                // Dashed outer box
                drawRect(
                    color = color.copy(alpha = 0.8f),
                    topLeft = Offset(leftPx, topPx),
                    size = Size(widthPx, heightPx),
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                    )
                )

                // Reticle Corner Brackets
                val cornerLen = (widthPx * 0.2f).coerceIn(16f, 48f)
                val strokeW = 8f

                // Top-Left
                drawLine(color, Offset(leftPx, topPx), Offset(leftPx + cornerLen, topPx), strokeW)
                drawLine(color, Offset(leftPx, topPx), Offset(leftPx, topPx + cornerLen), strokeW)

                // Top-Right
                drawLine(color, Offset(leftPx + widthPx, topPx), Offset(leftPx + widthPx - cornerLen, topPx), strokeW)
                drawLine(color, Offset(leftPx + widthPx, topPx), Offset(leftPx + widthPx, topPx + cornerLen), strokeW)

                // Bottom-Left
                drawLine(color, Offset(leftPx, topPx + heightPx), Offset(leftPx + cornerLen, topPx + heightPx), strokeW)
                drawLine(color, Offset(leftPx, topPx + heightPx), Offset(leftPx, topPx + heightPx - cornerLen), strokeW)

                // Bottom-Right
                drawLine(color, Offset(leftPx + widthPx, topPx + heightPx), Offset(leftPx + widthPx - cornerLen, topPx + heightPx), strokeW)
                drawLine(color, Offset(leftPx + widthPx, topPx + heightPx), Offset(leftPx + widthPx, topPx + heightPx - cornerLen), strokeW)

                // Object Label Tag Background & Text
                val labelText = "${obj.label} • ${obj.distanceMeters}m (${obj.position.displayName})"
                val textPaint = android.graphics.Paint().apply {
                    this.color = android.graphics.Color.WHITE
                    textSize = 34f
                    isAntiAlias = true
                    isFakeBoldText = true
                }

                val textBounds = android.graphics.Rect()
                textPaint.getTextBounds(labelText, 0, labelText.length, textBounds)

                val tagPadding = 12f
                val tagWidth = textBounds.width() + (tagPadding * 2)
                val tagHeight = textBounds.height() + (tagPadding * 2)

                val tagLeft = leftPx.coerceIn(0f, (canvasWidth - tagWidth).coerceAtLeast(0f))
                val tagTop = (topPx - tagHeight - 4f).coerceAtLeast(0f)

                drawRect(
                    color = color,
                    topLeft = Offset(tagLeft, tagTop),
                    size = Size(tagWidth, tagHeight)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    tagLeft + tagPadding,
                    tagTop + tagHeight - tagPadding - 2f,
                    textPaint
                )
            }
        }
    }
}
