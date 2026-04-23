package com.example.fishaudiotts.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * File-based logging utility for debugging crashes
 * Writes logs to app's cache directory for easy retrieval
 */
class FileLogger(private val context: Context) {
    companion object {
        private const val LOG_FILE_NAME = "app_logs.txt"
        private const val CRASH_FILE_NAME = "crash_logs.txt"
        private const val MAX_LOG_SIZE = 1024 * 1024 // 1MB
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        
        @Volatile
        private var instance: FileLogger? = null
        
        fun getInstance(context: Context): FileLogger {
            return instance ?: synchronized(this) {
                instance ?: FileLogger(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private val logFile: File by lazy {
        File(context.cacheDir, LOG_FILE_NAME).also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }
    }
    
    private val crashFile: File by lazy {
        File(context.cacheDir, CRASH_FILE_NAME).also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }
    }
    
    /**
     * Log a debug message
     */
    fun d(tag: String, message: String) {
        log("DEBUG", tag, message)
        Log.d(tag, message)
    }
    
    /**
     * Log an info message
     */
    fun i(tag: String, message: String) {
        log("INFO", tag, message)
        Log.i(tag, message)
    }
    
    /**
     * Log a warning message
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        log("WARN", tag, message, throwable)
        Log.w(tag, message, throwable)
    }
    
    /**
     * Log an error message
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log("ERROR", tag, message, throwable)
        Log.e(tag, message, throwable)
    }
    
    /**
     * Log a crash with full stack trace
     */
    fun logCrash(throwable: Throwable) {
        val timestamp = dateFormat.format(Date())
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        
        pw.println("\n========================================")
        pw.println("CRASH at $timestamp")
        pw.println("========================================")
        throwable.printStackTrace(pw)
        pw.println()
        
        val crashLog = sw.toString()
        
        // Write to crash file
        try {
            crashFile.appendText(crashLog)
        } catch (e: Exception) {
            Log.e("FileLogger", "Failed to write crash log", e)
        }
        
        // Also log to regular log
        log("CRASH", "AppCrash", "Application crashed", throwable)
    }
    
    private fun log(level: String, tag: String, message: String, throwable: Throwable? = null) {
        try {
            val timestamp = dateFormat.format(Date())
            val logLine = "$timestamp [$level] $tag: $message\n"
            
            logFile.appendText(logLine)
            
            throwable?.let {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                it.printStackTrace(pw)
                logFile.appendText(sw.toString())
            }
            
            // Rotate log if too large
            rotateLogIfNeeded()
        } catch (e: Exception) {
            Log.e("FileLogger", "Failed to write log", e)
        }
    }
    
    private fun rotateLogIfNeeded() {
        if (logFile.length() > MAX_LOG_SIZE) {
            try {
                val content = logFile.readText()
                val halfSize = content.length / 2
                logFile.writeText(content.substring(halfSize))
            } catch (e: Exception) {
                Log.e("FileLogger", "Failed to rotate log", e)
            }
        }
    }
    
    /**
     * Get all logs as a string
     */
    fun getLogs(): String {
        return try {
            if (logFile.exists()) {
                logFile.readText()
            } else {
                "No logs found"
            }
        } catch (e: Exception) {
            "Error reading logs: ${e.message}"
        }
    }
    
    /**
     * Get all crash logs as a string
     */
    fun getCrashLogs(): String {
        return try {
            if (crashFile.exists()) {
                crashFile.readText()
            } else {
                "No crash logs found"
            }
        } catch (e: Exception) {
            "Error reading crash logs: ${e.message}"
        }
    }
    
    /**
     * Clear all logs
     */
    fun clearLogs() {
        try {
            logFile.writeText("")
            crashFile.writeText("")
        } catch (e: Exception) {
            Log.e("FileLogger", "Failed to clear logs", e)
        }
    }
    
    /**
     * Share logs via system share dialog
     */
    suspend fun getLogsForSharing(): String = withContext(Dispatchers.IO) {
        val sb = StringBuilder()
        sb.append("=== FISH AUDIO TTS LOGS ===\n")
        sb.append("Generated: ${dateFormat.format(Date())}\n")
        sb.append("App Version: ${getAppVersion()}\n")
        sb.append("Android Version: ${android.os.Build.VERSION.RELEASE}\n")
        sb.append("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}\n")
        sb.append("\n=== CRASH LOGS ===\n")
        sb.append(getCrashLogs())
        sb.append("\n\n=== APP LOGS ===\n")
        sb.append(getLogs())
        sb.toString()
    }
    
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }
}

/**
 * Global exception handler for uncaught exceptions
 */
class CrashHandler(
    private val context: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {
    
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Log the crash
        FileLogger.getInstance(context).logCrash(throwable)
        
        // Call the default handler
        defaultHandler?.uncaughtException(thread, throwable)
    }
    
    companion object {
        fun install(context: Context) {
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(
                CrashHandler(context.applicationContext, defaultHandler)
            )
        }
    }
}
