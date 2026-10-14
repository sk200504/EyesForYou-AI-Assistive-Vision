package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnalysisResult
import com.example.model.DepthCategory
import com.example.model.DetectedObject
import com.example.ui.VisionMode
import com.example.ui.theme.*

@Composable
fun SceneDetailsCard(
    result: AnalysisResult,
    activeMode: VisionMode,
    showBoundingBoxes: Boolean,
    showDepthHeatmap: Boolean,
    onToggleBoxes: () -> Unit,
    onToggleHeatmap: () -> Unit,
    onSpeakText: (String) -> Unit,
    onObjectClick: (DetectedObject) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("scene_details_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row with Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (activeMode) {
                            VisionMode.ALL_SCENE -> Icons.Default.ChatBubbleOutline
                            VisionMode.OBJECTS_SPATIAL -> Icons.Default.ViewInAr
                            VisionMode.DEPTH_ESTIMATION -> Icons.Default.Radar
                            VisionMode.OCR_TEXT -> Icons.Default.Description
                            VisionMode.HAZARDS -> Icons.Default.Warning
                        },
                        contentDescription = activeMode.title,
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = activeMode.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                    )
                }

                // Overlay Controls
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = showBoundingBoxes,
                        onClick = onToggleBoxes,
                        label = { Text("YOLO Boxes", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanAccent,
                            selectedLabelColor = SlateDarker
                        ),
                        modifier = Modifier.testTag("toggle_yolo_boxes")
                    )

                    FilterChip(
                        selected = showDepthHeatmap,
                        onClick = onToggleHeatmap,
                        label = { Text("MiDaS Depth", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberWarning,
                            selectedLabelColor = SlateDarker
                        ),
                        modifier = Modifier.testTag("toggle_midas_depth")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Content Body
            when (activeMode) {
                VisionMode.ALL_SCENE -> {
                    // BLIP Scene Caption
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = result.sceneDescription.caption,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = TextPrimary
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoBadge(
                                icon = Icons.Default.LocationOn,
                                label = result.sceneDescription.indoorOutdoor,
                                badgeColor = CyanAccent
                            )
                            InfoBadge(
                                icon = Icons.Default.WbSunny,
                                label = result.sceneDescription.lighting,
                                badgeColor = AmberWarning
                            )
                        }

                        if (result.sceneDescription.safetyAdvice.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(RedHazard.copy(alpha = 0.15f))
                                    .border(1.dp, RedHazard.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Safety Tip",
                                        tint = RedHazard,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = result.sceneDescription.safetyAdvice,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                VisionMode.OBJECTS_SPATIAL -> {
                    // YOLOv8 Objects List
                    if (result.detectedObjects.isEmpty()) {
                        Text("No spatial objects detected.", color = TextSecondary)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            result.detectedObjects.forEach { obj ->
                                ObjectRowItem(obj = obj, onClick = { onObjectClick(obj) })
                            }
                        }
                    }
                }

                VisionMode.DEPTH_ESTIMATION -> {
                    // MiDaS Relative Depth Summary
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "MiDaS Monocular Depth Estimation",
                            style = MaterialTheme.typography.titleSmall.copy(color = CyanAccent)
                        )

                        val foregrounds = result.detectedObjects.filter { it.depth == DepthCategory.FOREGROUND }
                        val midgrounds = result.detectedObjects.filter { it.depth == DepthCategory.MIDGROUND }
                        val backgrounds = result.detectedObjects.filter { it.depth == DepthCategory.BACKGROUND }

                        DepthSectionBadge("Foreground / Immediate (< 1.5m)", foregrounds, RedHazard)
                        DepthSectionBadge("Midground (1.5m - 3.0m)", midgrounds, AmberWarning)
                        DepthSectionBadge("Background (> 3.0m)", backgrounds, EmeraldSuccess)
                    }
                }

                VisionMode.OCR_TEXT -> {
                    // Text / Signboard OCR
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (result.sceneDescription.ocrText.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SlateDarker,
                                modifier = Modifier.fillMaxWidth().padding(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "EXTRACTED SIGN / LABEL TEXT",
                                        style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = result.sceneDescription.ocrText,
                                        style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            Button(
                                onClick = { onSpeakText(result.sceneDescription.ocrText) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                modifier = Modifier.fillMaxWidth().testTag("read_ocr_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Read Text Aloud",
                                    tint = SlateDarker
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Read Text Aloud", color = SlateDarker, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(
                                text = "No signboard or document text detected in this scene.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                            )
                        }
                    }
                }

                VisionMode.HAZARDS -> {
                    // Hazard Warning
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val hazardObjs = result.detectedObjects.filter { it.isHazard }
                        if (hazardObjs.isNotEmpty()) {
                            Text(
                                text = "⚠️ Active Hazard Warning",
                                style = MaterialTheme.typography.titleMedium.copy(color = RedHazard, fontWeight = FontWeight.Bold)
                            )
                            hazardObjs.forEach { h ->
                                ObjectRowItem(obj = h, onClick = { onObjectClick(h) })
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Safe", tint = EmeraldSuccess)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("No immediate hazards or step drop-offs detected.", color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    badgeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = badgeColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = badgeColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary, fontSize = 12.sp))
        }
    }
}

@Composable
fun ObjectRowItem(
    obj: DetectedObject,
    onClick: () -> Unit
) {
    val color = when {
        obj.isHazard || obj.depth == DepthCategory.FOREGROUND -> RedHazard
        obj.depth == DepthCategory.MIDGROUND -> AmberWarning
        else -> EmeraldSuccess
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SlateDarker,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("object_row_${obj.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(color)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = obj.label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "${obj.position.displayName} • ${obj.depth.displayName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "${obj.distanceMeters}m",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = color,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DepthSectionBadge(
    title: String,
    objects: List<DetectedObject>,
    color: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge.copy(color = color, fontWeight = FontWeight.Bold))
        }
        if (objects.isEmpty()) {
            Text(text = "  None in this distance zone", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
        } else {
            Text(
                text = "  " + objects.joinToString(", ") { "${it.label} (${it.distanceMeters}m)" },
                style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
            )
        }
    }
}
