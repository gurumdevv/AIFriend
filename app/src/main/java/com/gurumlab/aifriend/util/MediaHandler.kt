package com.gurumlab.aifriend.util

import android.media.MediaPlayer
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class MediaHandler @Inject constructor() {
    private var mediaPlayer: MediaPlayer? = null

    fun play(inputStream: InputStream, onStart: () -> Unit, onCompletion: () -> Unit) {
        onStart()
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(inputStream)
            setOnPreparedListener {
                it.start()
            }
            setOnCompletionListener {
                onCompletion()
            }
            prepareAsync()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun MediaPlayer.setDataSource(inputStream: InputStream) {
        val tempFile = File.createTempFile("tempMedia", "mp3")
        tempFile.deleteOnExit()
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        setDataSource(tempFile.absolutePath)
    }
}