package com.gurumlab.aifriend.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.repository.ChatRepository
import com.gurumlab.aifriend.util.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    val chatMessages: Flow<PagingData<ChatMessage>> =
        repository.getAllMessages().cachedIn(viewModelScope)

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun getResponse(content: String, loadingMessage: String) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val newMessage = listOf(ChatMessage(role = Role.USER, content = content))
            val lastMessage = getLastMessage()
            addMessage(Role.USER, content)
            addMessage(Role.ASSISTANT, loadingMessage)
            val response = repository.getResponse(
                messages = (newMessage + lastMessage).reversed(),
                onError = {
                    Log.d("Chat", "onError: $it")
                },
                onException = {
                    Log.d("Chat", "onException: $it")
                }
            ).firstOrNull()
            val responseMessage = response?.choices?.firstOrNull()?.message?.content ?: ""
            repository.deleteLoadingMessage()
            addMessage(Role.ASSISTANT, responseMessage)
            _isLoading.emit(false)
        }
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
        return repository.getLastFourMessages().firstOrNull() ?: emptyList()
    }
}