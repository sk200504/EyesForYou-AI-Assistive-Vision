package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SpeechControlBar(
    isSpeaking: Boolean,
    speechRate: Float,
    autoNarrate: Boolean,
    onPlayPauseClick: () -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onAutoNarrateToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("speech_control_bar"),
        shape = RoundedCornerShape(20.dp),
        color = SlateSurface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Audio Status & Animated Visualizer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSpeaking) CyanAccent else SlateSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onPlayPauseClick,
                            modifier = Modifier.testTag("narration_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Pause else Icons.Default.VolumeUp,
                                contentDescription = if (isSpeaking) "Pause Voice Narration" else "Play Voice Narration",
                                tint = if (isSpeaking) SlateDarker else TextPrimary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (isSpeaking) "Speaking Narration..." else "Voice Guide Ready",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Speed: ${"%.1f".format(speechRate)}x • TTS Audio Engine",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        )
                    }
                }

                // Audio Equalizer Waveform Animation
                if (isSpeaking) {
                    EqualizerWaveform()
                } else {
                    OutlinedButton(
                        onClick = onAutoNarrateToggle,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (autoNarrate) CyanAccent else TextSecondary
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("auto_narrate_toggle")
                    ) {
                        Icon(
                            imageVector = if (autoNarrate) Icons.Default.AutoAwesome else Icons.Default.VolumeOff,
                            contentDescription = "Auto Narrate Toggle",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (autoNarrate) "Auto Voice ON" else "Auto Voice OFF",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Speed Control Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "0.5x",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )

                Slider(
                    value = speechRate,
                    onValueChange = onSpeechRateChange,
                    valueRange = 0.5f..2.0f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanAccent,
                        activeTrackColor = CyanAccent,
                        inactiveTrackColor = SlateSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("speech_rate_slider")
                )

                Text(
                    text = "2.0x",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }
    }
}

@Composable
fun EqualizerWaveform() {
    val transition = rememberInfiniteTransition(label = "Equalizer")
    
    val height1 by transition.animateFloat(
        initialValue = 8f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h1"
    )

    val height2 by transition.animateFloat(
        initialValue = 24f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h2"
    )

    val height3 by transition.animateFloat(
        initialValue = 12f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(32.dp)
    ) {
        Box(modifier = Modifier.width(4.dp).height(height1.dp).clip(RoundedCornerShape(2.dp)).background(CyanAccent))
        Box(modifier = Modifier.width(4.dp).height(height2.dp).clip(RoundedCornerShape(2.dp)).background(CyanAccentGlow))
        Box(modifier = Modifier.width(4.dp).height(height3.dp).clip(RoundedCornerShape(2.dp)).background(CyanAccent))
        Box(modifier = Modifier.width(4.dp).height(height1.dp).clip(RoundedCornerShape(2.dp)).background(CyanAccentGlow))
    }
}
