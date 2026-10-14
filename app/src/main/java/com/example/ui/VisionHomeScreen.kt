package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisionHomeScreen(
    viewModel: VisionViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentBitmap by viewModel.currentBitmap.collectAsStateWithLifecycle()
    val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
    val activeMode by viewModel.activeMode.collectAsStateWithLifecycle()
    val showBoxes by viewModel.showBoundingBoxes.collectAsStateWithLifecycle()
    val showHeatmap by viewModel.showDepthHeatmap.collectAsStateWithLifecycle()
    val autoNarrate by viewModel.autoNarrateOnAnalyze.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val speechRate by viewModel.speechRate.collectAsStateWithLifecycle()

    val history by viewModel.history.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    var showCameraView by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyanAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "VisionScene AI",
                                tint = SlateDarker,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VisionScene AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Spatial Assist & Narration",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.testTag("history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (history.isNotEmpty()) {
                                    Badge(containerColor = CyanAccent, contentColor = SlateDarker) {
                                        Text("${history.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.History, contentDescription = "History", tint = TextPrimary)
                        }
                    }

                    currentResult?.let { res ->
                        val isBookmarked = viewModel.isBookmarked(res)
                        IconButton(
                            onClick = { viewModel.toggleBookmark(res) },
                            modifier = Modifier.testTag("bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) CyanAccent else TextPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateDarker)
            )
        },
        containerColor = SlateDarker
    ) { innerPadding ->
        if (showCameraView) {
            CameraViewFinder(
                onPhotoCaptured = { bitmap ->
                    showCameraView = false
                    viewModel.analyzeBitmap(bitmap)
                },
                onClose = { showCameraView = false },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Sample Scene & Input Picker
                SampleScenePicker(
                    onPresetSelected = { preset -> viewModel.selectPreset(preset) },
                    onImageBitmapPicked = { bitmap -> viewModel.analyzeBitmap(bitmap) },
                    onCameraClick = { showCameraView = true }
                )

                // Main Image Preview Canvas with Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateSurface)
                        .testTag("image_preview_box"),
                    contentAlignment = Alignment.Center
                ) {
                    currentBitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Analyzed Scene View",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: run {
                        // Preset visual backdrop placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(SlateSurfaceVariant, SlateDarker)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CenterFocusWeak,
                                    contentDescription = "Scene Reticle",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Scene Camera Viewfinder",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }
                    }

                    // Bounding Box / Depth Canvas Overlay
                    currentResult?.let { res ->
                        BoundingBoxOverlay(
                            detectedObjects = res.detectedObjects,
                            showBoundingBoxes = showBoxes,
                            showDepthHeatmap = showHeatmap,
                            onObjectClick = { obj ->
                                viewModel.speechEngine.speak("${obj.label}, ${obj.position.displayName}, ${obj.distanceMeters} meters away.")
                            }
                        )
                    }

                    if (uiState is AnalysisUiState.Analyzing) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SlateDarker.copy(alpha = 0.85f),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = CyanAccent,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "AI Analyzing Scene & Objects...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                // Voice Narration & Audio Control Bar
                SpeechControlBar(
                    isSpeaking = isSpeaking,
                    speechRate = speechRate,
                    autoNarrate = autoNarrate,
                    onPlayPauseClick = {
                        if (isSpeaking) viewModel.stopNarration()
                        else viewModel.speakNarration()
                    },
                    onSpeechRateChange = { rate -> viewModel.setSpeechRate(rate) },
                    onAutoNarrateToggle = { viewModel.toggleAutoNarrate() }
                )

                // Vision Mode Selector Pills
                VisionModeSelector(
                    activeMode = activeMode,
                    onModeSelected = { mode -> viewModel.setMode(mode) }
                )

                // Scene Breakdown Details Card
                currentResult?.let { res ->
                    SceneDetailsCard(
                        result = res,
                        activeMode = activeMode,
                        showBoundingBoxes = showBoxes,
                        showDepthHeatmap = showHeatmap,
                        onToggleBoxes = { viewModel.toggleBoundingBoxes() },
                        onToggleHeatmap = { viewModel.toggleDepthHeatmap() },
                        onSpeakText = { text -> viewModel.speechEngine.speak(text) },
                        onObjectClick = { obj ->
                            viewModel.speechEngine.speak("${obj.label}, ${obj.position.displayName}, ${obj.distanceMeters} meters away.")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showHistorySheet) {
            HistoryDrawerSheet(
                history = history,
                bookmarks = bookmarks,
                onSelectResult = { res ->
                    viewModel.stopNarration()
                    viewModel.speakNarration(res)
                },
                onToggleBookmark = { res -> viewModel.toggleBookmark(res) },
                onClearHistory = { viewModel.repository.clearHistory() },
                onDismiss = { showHistorySheet = false }
            )
        }
    }
}
