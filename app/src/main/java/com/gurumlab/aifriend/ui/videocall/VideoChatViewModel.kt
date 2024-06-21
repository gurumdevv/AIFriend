package com.gurumlab.aifriend.ui.videocall

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.model.Emotion
import com.gurumlab.aifriend.data.repository.VideoChatRepository
import com.gurumlab.aifriend.util.GPTConstants
import com.gurumlab.aifriend.util.MediaHandler
import com.gurumlab.aifriend.util.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoChatViewModel @Inject constructor(
    private val repository: VideoChatRepository,
    private val mediaHandler: MediaHandler
) : ViewModel() {

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    private val _characterEmotion = MutableStateFlow(Emotion.NORMAL.drawableRes)
    val characterEmotion = _characterEmotion.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<Int>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun getResponse(file: File) {
        viewModelScope.launch {
            setLoadingState(true)

            val transcription = getTranscription(file)
            if (transcription.checkAndHandleError("transcriptionText is empty")) return@launch

            val chatText = getChatResponse(transcription)
            if (chatText.checkAndHandleError("chatText is empty")) return@launch

            val emotionText = getEmotion(chatText)
            if (!setCharacter(
                    chatText,
                    emotionText
                ).also { success -> if (!success) handleError("speechResponse is null") }
            ) return@launch
        }
    }

    private suspend fun getTranscription(file: File): String {
        val transcriptionResponse = repository.getTranscription(
            file = file,
            onError = { handleError("onError: $it") },
            onException = { handleError("onException: $it") }
        ).firstOrNull()

        return transcriptionResponse?.text ?: ""
    }

    private suspend fun getChatResponse(transcription: String): String {
        val newMessage = listOf(ChatMessage(content = transcription, role = Role.USER))
        val lastMessage = getLastMessage()
        addMessage(content = transcription, role = Role.USER)
        val chatResponse = repository.getChatResponse(
            messages = (newMessage + lastMessage).reversed(),
            onCompletion = { setLoadingState(false) },
            onError = { handleError("onError: $it") },
            onException = { handleError("onException: $it") }
        ).firstOrNull()

        val chatText = chatResponse?.choices?.firstOrNull()?.message?.content ?: ""
        addMessage(content = chatText, role = Role.ASSISTANT)
        return chatText
    }

    private suspend fun getEmotion(text: String): String {
        val message = ChatMessage(content = text, role = Role.USER)
        val emotionCommand = ChatMessage(
            content = GPTConstants.EMOTION_COMMAND,
            role = Role.SYSTEM
        )

        val emotionResponse = repository.getChatResponse(
            messages = listOf(emotionCommand, message),
            onCompletion = {},
            onError = { handleError("onError: $it") },
            onException = { handleError("onException: $it") }
        ).firstOrNull()

        return emotionResponse?.choices?.firstOrNull()?.message?.content ?: ""
    }

    private suspend fun setCharacter(chatText: String, emotionText: String): Boolean {
        val emotion = when {
            emotionText.contains("기쁨") -> Emotion.HAPPY
            emotionText.contains("슬픔") -> Emotion.SAD
            emotionText.contains("화남") -> Emotion.ANGRY
            else -> Emotion.SAYING
        }

        val speechResponse = repository.getSpeech(
            chatText,
            onError = { handleError("onError: $it") },
            onException = { handleError("onException: $it") }
        ).firstOrNull()

        if (speechResponse == null) {
            return false
        }

        mediaHandler.playMediaPlayer(
            inputStream = speechResponse,
            onStart = {
                _characterEmotion.value = emotion.drawableRes
            },
            onCompletion = {
                _characterEmotion.value = Emotion.NORMAL.drawableRes
                mediaHandler.releaseMediaPlayer()
            }
        )

        return true
    }

    private suspend fun addMessage(role: String, content: String) {
        repository.insertMessage(
            ChatMessage(
                role = role,
                content = content
            )
        )
    }

    private suspend fun getLastMessage(): List<ChatMessage> {
        return repository.getLastThreeMessages().firstOrNull() ?: emptyList()
    }

    private fun setLoadingState(state: Boolean) {
        viewModelScope.launch {
            _isLoading.emit(state)
        }
    }

    private fun String.checkAndHandleError(errorMessage: String): Boolean {
        return if (this.isEmpty()) {
            handleError(errorMessage)
            viewModelScope.launch { _snackbarMessage.emit(R.string.fail_response) }
            true
        } else {
            false
        }
    }

    private fun handleError(message: String) {
        Log.d("VideoChat", message)
        setLoadingState(false)
    }

    fun setSnackbarMessage(messageRes: Int) {
        viewModelScope.launch {
            _snackbarMessage.emit(messageRes)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaHandler.releaseMediaPlayer()
    }
}