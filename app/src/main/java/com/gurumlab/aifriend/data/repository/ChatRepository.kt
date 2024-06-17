package com.gurumlab.aifriend.data.repository

import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.model.ChatRequest
import com.gurumlab.aifriend.data.model.ChatResponse
import com.gurumlab.aifriend.data.source.remote.ApiClient
import com.gurumlab.aifriend.data.source.remote.onError
import com.gurumlab.aifriend.data.source.remote.onException
import com.gurumlab.aifriend.data.source.remote.onSuccess
import com.gurumlab.aifriend.di.GPTVersion
import com.gurumlab.aifriend.util.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val apiClient: ApiClient
) {

    suspend fun getResponse(
        message: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<ChatResponse> = flow {
        val response = apiClient.getResponse(
            ChatRequest(
                GPTVersion.CURRENT_VERSION, listOf(
                    ChatMessage(Role.ASSISTANT, message)
                )
            )
        )
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)
}