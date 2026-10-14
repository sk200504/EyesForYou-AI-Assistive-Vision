package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PresetData
import com.example.model.PresetScene
import com.example.ui.theme.*

@Composable
fun SampleScenePicker(
    onPresetSelected: (PresetScene) -> Unit,
    onImageBitmapPicked: (Bitmap) -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let { b -> onImageBitmapPicked(b) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("sample_scene_picker")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Scene Input Source",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Live Camera Button
                Button(
                    onClick = onCameraClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(34.dp).testTag("camera_capture_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Capture",
                        tint = SlateDarker,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Camera", color = SlateDarker, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Gallery Button
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(34.dp).testTag("gallery_pick_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Upload Photo",
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload", color = TextPrimary, fontSize = 12.sp)
                }
            }
        }

        // Preset Scenes Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PresetData.sampleScenes.forEach { preset ->
                Surface(
                    onClick = { onPresetSelected(preset) },
                    shape = RoundedCornerShape(14.dp),
                    color = SlateSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.testTag("preset_${preset.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (preset.id) {
                                "preset_living_room" -> Icons.Default.Weekend
                                "preset_crosswalk" -> Icons.Default.DirectionsWalk
                                "preset_staircase" -> Icons.Default.Stairs
                                else -> Icons.Default.Image
                            },
                            contentDescription = preset.title,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = preset.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
