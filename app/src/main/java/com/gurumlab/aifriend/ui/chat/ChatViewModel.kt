package com.gurumlab.aifriend.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.aifriend.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _response = MutableSharedFlow<String>()
    val response = _response.asSharedFlow()

    fun getResponse(message: String) {
        viewModelScope.launch {
            val response = repository.getResponse(
                message,
                onError = {
                    Log.d("Chat", "onError: $it")
                },
                onException = {
                    Log.d("Chat", "onException: $it")
                }
            ).firstOrNull()

            val responseMessage = response?.choices?.firstOrNull()?.message?.content ?: ""
            _response.emit(responseMessage)
        }
    }
}