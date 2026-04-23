package com.example.fishaudiotts.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

/**
 * Audio player utility for voice preview playback
 * Manages MediaPlayer lifecycle and audio file cleanup
 */
class AudioPlayer(private val context: Context) {
    companion object {
        private const val TAG = "AudioPlayer"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var currentPreviewFile: File? = null

    /**
     * Play audio from an InputStream (e.g., TTS response)
     * Saves to temp file, plays, then cleans up
     */
    suspend fun playPreview(audioStream: InputStream, onComplete: (() -> Unit)? = null) {
        withContext(Dispatchers.IO) {
            try {
                // Stop any currently playing audio
                stop()

                // Save to temp file
                val tempFile = File.createTempFile("voice_preview", ".mp3", context.cacheDir)
                currentPreviewFile = tempFile

                tempFile.outputStream().use { output ->
                    audioStream.copyTo(output)
                }

                // Play the audio
                withContext(Dispatchers.Main) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(tempFile.absolutePath)
                        setOnPreparedListener { start() }
                        setOnCompletionListener {
                            onComplete?.invoke()
                            cleanup()
                        }
                        setOnErrorListener { _, what, extra ->
                            Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                            cleanup()
                            true
                        }
                        prepareAsync()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play preview: ${e.message}", e)
                cleanup()
            }
        }
    }

    /**
     * Play audio from a file path
     */
    fun playFromFile(filePath: String, onComplete: (() -> Unit)? = null) {
        try {
            stop()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                setOnPreparedListener { start() }
                setOnCompletionListener {
                    onComplete?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play from file: ${e.message}", e)
        }
    }

    /**
     * Stop playback and cleanup
     */
    fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping playback: ${e.message}")
        }
        cleanup()
    }

    /**
     * Check if audio is currently playing
     */
    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    private fun cleanup() {
        try {
            currentPreviewFile?.let { file ->
                if (file.exists()) {
                    file.delete()
                }
            }
            currentPreviewFile = null
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up temp file: ${e.message}")
        }
    }

    /**
     * Release resources - call when done with player
     */
    fun release() {
        stop()
    }
}
