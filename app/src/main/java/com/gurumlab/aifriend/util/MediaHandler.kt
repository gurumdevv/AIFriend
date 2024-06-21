package com.gurumlab.aifriend.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.ui.videocall.VideoChatViewModel
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class MediaHandler @Inject constructor() {
    private var outputPath: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    fun startRecording(
        context: Context,
        viewModel: VideoChatViewModel,
        isStateChanged: (Boolean) -> Unit
    ) {
        val fileName = DateTimeConverter.getCurrentDateString() + ".mp3"
        val file = File(context.filesDir, fileName)
        outputPath = file.absolutePath

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputPath)

            try {
                prepare()
                start()
                isStateChanged(true)
            } catch (e: Exception) {
                handleException(viewModel, R.string.fail_recording, "${e.message}")
                releaseRecorder()
                isStateChanged(false)
            }
        }
    }

    fun stopRecording(
        viewModel: VideoChatViewModel,
        isStateChanged: (Boolean) -> Unit
    ) {
        mediaRecorder?.apply {
            try {
                stop()
            } catch (e: Exception) {
                handleException(viewModel, R.string.fail_recording, "${e.message}")
            } finally {
                releaseRecorder()
                isStateChanged(false)
            }
        }

        outputPath?.let {
            val audio = File(it)
            viewModel.getResponse(audio)
            outputPath = null
        } ?: {
            handleException(viewModel, R.string.fail_recording, "outputPath is null")
        }
    }

    fun playMediaPlayer(inputStream: InputStream, onStart: () -> Unit, onCompletion: () -> Unit) {
        onStart()
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

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun releaseRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun handleException(viewModel: VideoChatViewModel, messageRes: Int, message: String) {
        viewModel.setSnackbarMessage(messageRes)
        Log.e("MediaHandler", message)
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