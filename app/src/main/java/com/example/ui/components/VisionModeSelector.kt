package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VisionMode
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary

@Composable
fun VisionModeSelector(
    activeMode: VisionMode,
    onModeSelected: (VisionMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("vision_mode_selector"),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VisionMode.values().forEach { mode ->
            val isSelected = mode == activeMode
            val icon: ImageVector = when (mode) {
                VisionMode.ALL_SCENE -> Icons.Default.RemoveRedEye
                VisionMode.OBJECTS_SPATIAL -> Icons.Default.GridView
                VisionMode.DEPTH_ESTIMATION -> Icons.Default.Layers
                VisionMode.OCR_TEXT -> Icons.Default.TextFields
                VisionMode.HAZARDS -> Icons.Default.Warning
            }

            FilterChip(
                selected = isSelected,
                onClick = { onModeSelected(mode) },
                label = {
                    Text(
                        text = mode.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            color = if (isSelected) SlateDarker else TextPrimary
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = mode.title,
                        tint = if (isSelected) SlateDarker else CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanAccent,
                    containerColor = SlateSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) CyanAccent else SlateSurfaceVariant,
                    enabled = true,
                    selected = isSelected
                ),
                modifier = Modifier
                    .height(40.dp)
                    .testTag("mode_chip_${mode.name}")
            )
        }
    }
}
