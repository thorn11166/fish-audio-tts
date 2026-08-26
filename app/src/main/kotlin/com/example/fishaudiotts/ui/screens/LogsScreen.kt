package com.example.fishaudiotts.ui.screens

import android.content.Intent
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.fishaudiotts.ui.theme.TorBoxCard
import com.example.fishaudiotts.ui.theme.TorBoxGreenLight
import com.example.fishaudiotts.ui.theme.TorBoxGreen
import com.example.fishaudiotts.ui.theme.TorBoxText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient
import com.example.fishaudiotts.util.DebugAudioManager
import com.example.fishaudiotts.util.FileLogger
import com.example.fishaudiotts.util.RequestLogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Logs screen for viewing request history, debug audio files, and text logs.
 */
@Composable
fun LogsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = remember { FileLogger.getInstance(context) }
    val debugAudioManager = remember { DebugAudioManager(context) }

    var selectedTab by remember { mutableStateOf(0) }
    var textLogs by remember { mutableStateOf("Loading logs...") }
    val requestLogs = remember { mutableStateListOf<RequestLogEntry>() }
    val audioFiles = remember { mutableStateListOf<File>() }

    LaunchedEffect(Unit) {
        refreshData(logger, debugAudioManager, { textLogs = it }, requestLogs, audioFiles)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vaporwaveGradient)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TorBoxGreen
                    )
                }

                Text(
                    text = "📋 Debug Logs",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TorBoxGreen,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        scope.launch {
                            shareLogs(context, logger)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share logs",
                        tint = TorBoxGreenLight
                    )
                }

                IconButton(
                    onClick = {
                        logger.clearLogs()
                        logger.clearRequestLogs()
                        debugAudioManager.clearDebugAudioFiles()
                        scope.launch {
                            refreshData(logger, debugAudioManager, { textLogs = it }, requestLogs, audioFiles)
                        }
                        textLogs = "Logs cleared"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear logs",
                        tint = TorBoxGreen
                    )
                }
            }
        }

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        ) {
            TabButton(
                label = "Requests (${requestLogs.size})",
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TabButton(
                label = "Audio (${audioFiles.size})",
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TabButton(
                label = "Text",
                isSelected = selectedTab == 2,
                onClick = { selectedTab = 2 }
            )
        }

        // Content
        when (selectedTab) {
            0 -> RequestLogsList(logs = requestLogs)
            1 -> AudioFilesList(files = audioFiles)
            2 -> TextLogsView(textLogs = textLogs)
        }
    }
}

@Composable
private fun TabButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val background = if (isSelected) TorBoxGreen.copy(alpha = 0.3f) else TorBoxCard.copy(alpha = 0.1f)
    val textColor = if (isSelected) TorBoxGreen else TorBoxText

    Row(
        modifier = Modifier
            .background(background, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
private fun RequestLogsList(logs: List<RequestLogEntry>) {
    if (logs.isEmpty()) {
        BoxedText(text = "No request logs yet.")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        items(logs) { entry ->
            RequestLogItem(entry = entry)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RequestLogItem(entry: RequestLogEntry) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = if (entry.success) TorBoxGreen else androidx.compose.ui.graphics.Color.Red
    val statusText = if (entry.success) "OK" else "FAIL"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TorBoxCard.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.timestamp,
                    fontSize = 11.sp,
                    color = TorBoxGreenLight
                )
                Text(
                    text = "${entry.model} · ${entry.text.take(40)}${if (entry.text.length > 40) "…" else ""}",
                    fontSize = 13.sp,
                    color = TorBoxText,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = TorBoxGreenLight
                )
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                DetailRow(label = "Model", value = entry.model)
                DetailRow(label = "Text", value = entry.text)
                DetailRow(label = "Voice ID", value = entry.referenceId ?: "default")
                DetailRow(label = "Format", value = entry.format)
                DetailRow(label = "Sample Rate", value = entry.sampleRate?.toString() ?: "default")
                DetailRow(label = "Response Size", value = entry.responseSize?.let { "$it bytes" } ?: "N/A")
                DetailRow(label = "Duration", value = "${entry.durationMs} ms")
                if (!entry.error.isNullOrBlank()) {
                    DetailRow(label = "Error", value = entry.error)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label:",
            fontSize = 11.sp,
            color = TorBoxGreenLight,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = TorBoxText
        )
    }
}

@Composable
private fun AudioFilesList(files: List<File>) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentlyPlaying by remember { mutableStateOf<MediaPlayer?>(null) }

    if (files.isEmpty()) {
        BoxedText(text = "No debug audio files saved yet.\n(Debug builds only, last 50 kept.)")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        items(files, key = { it.absolutePath }) { file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TorBoxCard.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        fontSize = 12.sp,
                        color = TorBoxText,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${file.length()} bytes",
                        fontSize = 11.sp,
                        color = TorBoxGreenLight
                    )
                }

                IconButton(
                    onClick = {
                        scope.launch {
                            try {
                                currentlyPlaying?.release()
                                val player = withContext(Dispatchers.IO) {
                                    MediaPlayer().apply {
                                        setDataSource(file.absolutePath)
                                        setOnCompletionListener { release() }
                                        setOnErrorListener { _, _, _ ->
                                            release()
                                            true
                                        }
                                        prepare()
                                    }
                                }
                                player.start()
                                currentlyPlaying = player
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = TorBoxGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TextLogsView(textLogs: String) {
    SelectionContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .background(TorBoxCard.copy(alpha = 0.3f))
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = textLogs,
                fontSize = 11.sp,
                color = TorBoxText,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun BoxedText(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(TorBoxCard.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = TorBoxText
        )
    }
}

private suspend fun refreshData(
    logger: FileLogger,
    debugAudioManager: DebugAudioManager,
    onTextLogs: (String) -> Unit,
    requestLogs: MutableList<RequestLogEntry>,
    audioFiles: MutableList<File>
) {
    withContext(Dispatchers.IO) {
        onTextLogs(logger.getLogs() + "\n\n=== CRASH LOGS ===\n\n" + logger.getCrashLogs())
        requestLogs.clear()
        requestLogs.addAll(logger.getRequestLogs())
        audioFiles.clear()
        audioFiles.addAll(debugAudioManager.getDebugAudioFiles())
    }
}

private suspend fun shareLogs(context: android.content.Context, logger: FileLogger) {
    val logContent = logger.getLogsForSharing()

    val shareFile = File(context.cacheDir, "fish_audio_tts_logs.txt")
    shareFile.writeText(logContent)

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        shareFile
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Fish Audio TTS Logs")
        putExtra(Intent.EXTRA_TEXT, "Debug logs from Fish Audio TTS app")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(intent, "Share Logs")
    )
}
