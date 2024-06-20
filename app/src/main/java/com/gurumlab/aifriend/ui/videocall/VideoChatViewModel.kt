package com.gurumlab.aifriend.ui.videocall

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.repository.VideoChatRepository
import com.gurumlab.aifriend.util.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class VideoChatViewModel @Inject constructor(
    private val repository: VideoChatRepository
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun getResponse(file: File) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val transcriptionResponse = repository.getTranscription(
                file = file,
                onError = {
                    Log.d("VideoChat", "onError: $it")
                },
                onException = {
                    Log.d("VideoChat", "onException: $it")
                }
            ).firstOrNull()
            val transcriptionText = transcriptionResponse?.text ?: ""

            if (transcriptionText.isEmpty()) {
                Log.d("VideoChat", "transcriptionText is empty")
                return@launch
            }

            val newMessage = ChatMessage(
                content = transcriptionText,
                role = Role.USER,
            )

            val chatResponse = repository.getChatResponse(
                messages = listOf(newMessage),
                onError = {
                    Log.d("VideoChat", "onError: $it")
                },
                onException = {
                    Log.d("VideoChat", "onException: $it")
                }
            ).firstOrNull()

            val chatText = chatResponse?.choices?.firstOrNull()?.message?.content ?: ""

            if (chatText.isEmpty()) {
                Log.d("VideoChat", "chatText is empty")
                return@launch
            }

            val speechResponse = repository.getSpeech(
                chatText,
                onCompletion = {
                    viewModelScope.launch {
                        _isLoading.emit(false)
                    }
                },
                onError = {
                    Log.d("VideoChat", "onError: $it")
                },
                onException = {
                    Log.d("VideoChat", "onException: $it")
                }
            ).firstOrNull()

            if (speechResponse == null) {
                Log.d("VideoChat", "speechResponse is null")
                return@launch
            }

            mediaPlayer?.reset()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(speechResponse)
                setOnPreparedListener {
                    it.start()
                }
                setOnErrorListener { _, what, extra ->
                    Log.d("VideoChat", "MediaPlayer error: what=$what, extra=$extra")
                    true
                }
                prepareAsync()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
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