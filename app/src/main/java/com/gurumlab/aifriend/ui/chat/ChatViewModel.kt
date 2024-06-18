package com.gurumlab.aifriend.ui.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.aifriend.R
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.repository.ChatRepository
import com.gurumlab.aifriend.util.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val application: Application,
    private val repository: ChatRepository
) : AndroidViewModel(application) {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    fun getResponse() {
        viewModelScope.launch {
            val lastMessage = getLastMessage()
            val response = repository.getResponse(
                messages = lastMessage,
                onCompletion = {
                    deleteLoadingMessage()
                },
                onError = {
                    Log.d("Chat", "onError: $it")
                },
                onException = {
                    Log.d("Chat", "onException: $it")
                }
            ).firstOrNull()

            val responseMessage = response?.choices?.firstOrNull()?.message?.content
            addMessage(Role.ASSISTANT, responseMessage)
        }
    }

    fun addMessage(role: String, content: String?) {
        val newMessage = ChatMessage(
            role = role,
            content = content ?: application.getString(R.string.fail_response)
        )

        viewModelScope.launch {
            _chatMessages.value += newMessage
            content?.let {
                repository.insertMessage(newMessage)
            }
        }
    }

    private suspend fun getLastMessage(): List<ChatMessage> {
        return repository.getLastThreeMessages().firstOrNull() ?: emptyList()
    }

    fun insertLoadingMessage() {
        val loadingMessage = ChatMessage(
            role = Role.ASSISTANT,
            content = application.getString(R.string.loading),
        )

        viewModelScope.launch {
            _chatMessages.value += loadingMessage
        }
    }

    private fun deleteLoadingMessage() {
        viewModelScope.launch {
            _chatMessages.value -= _chatMessages.value[_chatMessages.value.size - 2]
        }
    }
}