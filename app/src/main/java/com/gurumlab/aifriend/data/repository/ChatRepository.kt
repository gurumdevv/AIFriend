package com.gurumlab.aifriend.data.repository

import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.model.ChatRequest
import com.gurumlab.aifriend.data.model.ChatResponse
import com.gurumlab.aifriend.data.source.local.ChatDao
import com.gurumlab.aifriend.data.source.remote.ApiClient
import com.gurumlab.aifriend.data.source.remote.onError
import com.gurumlab.aifriend.data.source.remote.onException
import com.gurumlab.aifriend.data.source.remote.onSuccess
import com.gurumlab.aifriend.di.GPTVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val chatDao: ChatDao
) {

    fun getResponse(
        messages: List<ChatMessage>,
        onCompletion: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<ChatResponse> = flow {
        val response = apiClient.getResponse(
            ChatRequest(GPTVersion.CURRENT_VERSION, messages)
        )
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code message: $message")
        }.onException {
            onException(it.message)
        }
    }.onCompletion { onCompletion() }
        .flowOn(Dispatchers.IO)

    fun getLastThreeMessages(): Flow<List<ChatMessage>> = flow {
        emit(chatDao.getLastFiveMessages())
    }.flowOn(Dispatchers.IO)

    suspend fun insertMessage(message: ChatMessage) {
        chatDao.insertMessage(message)
    }
}