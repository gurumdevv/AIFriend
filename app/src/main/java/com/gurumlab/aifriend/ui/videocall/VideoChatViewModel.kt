package com.gurumlab.aifriend.ui.videocall

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.repository.VideoChatRepository
import com.gurumlab.aifriend.util.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _characterEmotion = MutableStateFlow(R.drawable.character_normal)
    val characterEmotion = _characterEmotion.asStateFlow()

    fun getResponse(file: File) {
        viewModelScope.launch {
            setLoadingState(true)
            val transcriptionResponse = repository.getTranscription(
                file = file,
                onError = {
                    setLoadingState(false)
                    Log.d("VideoChat", "onError: $it")
                },
                onException = {
                    setLoadingState(false)
                    Log.d("VideoChat", "onException: $it")
                }
            ).firstOrNull()
            val transcriptionText = transcriptionResponse?.text ?: ""

            if (transcriptionText.isEmpty()) {
                Log.d("VideoChat", "transcriptionText is empty")
                setLoadingState(false)
                return@launch
            }

            val newMessage = ChatMessage(
                content = transcriptionText,
                role = Role.USER,
            )

            val chatResponse = repository.getChatResponse(
                messages = listOf(newMessage),
                onCompletion = {
                    setLoadingState(false)
                },
                onError = {
                    Log.d("VideoChat", "onError: $it")
                    setLoadingState(false)
                },
                onException = {
                    Log.d("VideoChat", "onException: $it")
                    setLoadingState(false)
                }
            ).firstOrNull()

            val chatText = chatResponse?.choices?.firstOrNull()?.message?.content ?: ""

            if (chatText.isEmpty()) {
                Log.d("VideoChat", "chatText is empty")
                setLoadingState(false)
                return@launch
            }
            val emotionCommand = ChatMessage(
                content = "메세지에서 기쁨/슬픔/화남/보통으로 감정을 분류해주세요. (기쁨/슬픔/화남/보통) 단어로만 답변해야합니다.",
                role = Role.SYSTEM
            )

            val emotionResponse = repository.getChatResponse(
                messages = listOf(emotionCommand, newMessage),
                onCompletion = {},
                onError = {
                    setLoadingState(false)
                    Log.d("VideoChat", "onError: $it")
                },
                onException = {
                    setLoadingState(false)
                    Log.d("VideoChat", "onException: $it")
                }
            ).firstOrNull()

            val emotionText = emotionResponse?.choices?.firstOrNull()?.message?.content ?: ""

            val emotion = if (emotionText.contains("기쁨")) R.drawable.character_happy
            else if (emotionText.contains("슬픔")) R.drawable.character_sad
            else if (emotionText.contains("화남")) R.drawable.characeter_angry
            else R.drawable.character_saying

            val speechResponse = repository.getSpeech(
                chatText,
                onError = {
                    Log.d("VideoChat", "onError: $it")
                },
                onException = {
                    Log.d("VideoChat", "onException: $it")
                }
            ).firstOrNull()

            if (speechResponse == null) {
                Log.d("VideoChat", "speechResponse is null")
                setLoadingState(false)
                return@launch
            }

            mediaPlayer?.reset()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(speechResponse)
                setOnPreparedListener {
                    it.start()
                    _characterEmotion.value = emotion
                }
                setOnErrorListener { _, what, extra ->
                    Log.d("VideoChat", "MediaPlayer error: what=$what, extra=$extra")
                    true
                }
                setOnCompletionListener {
                    _characterEmotion.value = R.drawable.character_normal
                }
                prepareAsync()
            }
        }
    }

    private fun setLoadingState(state: Boolean) {
        viewModelScope.launch {
            _isLoading.emit(state)
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