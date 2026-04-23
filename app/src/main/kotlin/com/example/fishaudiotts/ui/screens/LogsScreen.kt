package com.example.fishaudiotts.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.fishaudiotts.util.FileLogger
import com.example.fishaudiotts.ui.theme.CyberPurple
import com.example.fishaudiotts.ui.theme.DarkCyan
import com.example.fishaudiotts.ui.theme.NeonPink
import com.example.fishaudiotts.ui.theme.VapText
import com.example.fishaudiotts.ui.theme.vaporwaveGradient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Logs screen for viewing and sharing debug logs
 */
@Composable
fun LogsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = remember { FileLogger.getInstance(context) }
    
    var logs by remember { mutableStateOf("Loading logs...") }
    
    LaunchedEffect(Unit) {
        logs = logger.getLogs() + "\n\n=== CRASH LOGS ===\n\n" + logger.getCrashLogs()
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
                        tint = NeonPink
                    )
                }
                
                Text(
                    text = "📋 Debug Logs",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPink,
                    modifier = Modifier.weight(1f)
                )
                
                // Share button
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
                        tint = DarkCyan
                    )
                }
                
                // Clear button
                IconButton(
                    onClick = {
                        logger.clearLogs()
                        logs = "Logs cleared"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear logs",
                        tint = NeonPink
                    )
                }
            }
        }
        
        // Logs content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .background(CyberPurple.copy(alpha = 0.3f))
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = logs,
                fontSize = 11.sp,
                color = VapText,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

private suspend fun shareLogs(context: android.content.Context, logger: FileLogger) {
    val logContent = logger.getLogsForSharing()
    
    // Write to a temporary file for sharing
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
