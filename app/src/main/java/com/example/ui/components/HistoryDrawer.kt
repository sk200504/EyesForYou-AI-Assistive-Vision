package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnalysisResult
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDrawerSheet(
    history: List<AnalysisResult>,
    bookmarks: List<AnalysisResult>,
    onSelectResult: (AnalysisResult) -> Unit,
    onToggleBookmark: (AnalysisResult) -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SlateSurface,
        scrimColor = SlateDarker.copy(alpha = 0.8f),
        modifier = Modifier.testTag("history_drawer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Analysis History",
                        tint = CyanAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scene Analysis History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                if (history.isNotEmpty()) {
                    TextButton(onClick = onClearHistory) {
                        Text("Clear All", color = RedHazard, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No past scenes logged yet.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxHeight(0.75f)
                ) {
                    items(history) { result ->
                        val dateStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(result.sceneDescription.timestamp))
                        val isBookmarked = bookmarks.any { it.id == result.id }

                        Surface(
                            onClick = {
                                onSelectResult(result)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = SlateDarker,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = result.sceneDescription.caption,
                                        maxLines = 2,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$dateStr • ${result.detectedObjects.size} Objects • ${result.sceneDescription.indoorOutdoor}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                IconButton(onClick = { onToggleBookmark(result) }) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark Scene",
                                        tint = if (isBookmarked) CyanAccent else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
