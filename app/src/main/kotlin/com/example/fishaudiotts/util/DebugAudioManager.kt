package com.example.fishaudiotts.util

import android.content.Context
import android.util.Log
import com.example.fishaudiotts.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages retention of the last N generated audio files for debugging.
 * Files are only saved in debug builds to avoid consuming storage in release.
 */
class DebugAudioManager(private val context: Context) {

    companion object {
        private const val TAG = "DebugAudioManager"
        private val timestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US)
    }

    private val debugDir: File by lazy {
        File(context.cacheDir, Constants.DEBUG_AUDIO_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Saves a copy of the generated audio bytes to the debug directory.
     * Only performs I/O in debug builds.
     *
     * @param bytes Raw audio bytes
     * @param prefix Filename prefix, e.g. "tts" or "preview"
     * @return The saved [File] or null if saving is disabled/failed
     */
    fun saveAudioBytes(bytes: ByteArray, prefix: String = "tts", extension: String = "mp3"): File? {
        if (!BuildConfig.DEBUG) return null

        return try {
            val timestamp = timestampFormat.format(Date())
            val file = File(debugDir, "${prefix}_${timestamp}.${extension}")
            file.writeBytes(bytes)
            cleanupOldFiles()
            Log.d(TAG, "Saved debug audio: ${file.name} (${bytes.size} bytes)")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save debug audio", e)
            null
        }
    }

    /**
     * Returns the saved debug audio files sorted newest-first.
     */
    fun getDebugAudioFiles(): List<File> {
        return debugDir.listFiles()
            ?.filter { it.isFile }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    /**
     * Deletes all saved debug audio files.
     */
    fun clearDebugAudioFiles() {
        try {
            debugDir.listFiles()?.forEach { it.delete() }
            Log.d(TAG, "Cleared debug audio files")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear debug audio files", e)
        }
    }

    private fun cleanupOldFiles() {
        val files = debugDir.listFiles()?.filter { it.isFile }?.sortedBy { it.lastModified() }
            ?: return

        if (files.size > Constants.MAX_DEBUG_AUDIO_FILES) {
            val toDelete = files.take(files.size - Constants.MAX_DEBUG_AUDIO_FILES)
            toDelete.forEach {
                try {
                    it.delete()
                    Log.d(TAG, "Pruned old debug audio: ${it.name}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to delete old debug audio: ${it.name}", e)
                }
            }
        }
    }
}
