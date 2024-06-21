package com.gurumlab.aifriend.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.model.ChatRequest
import com.gurumlab.aifriend.data.model.ChatResponse
import com.gurumlab.aifriend.data.source.local.ChatDao
import com.gurumlab.aifriend.data.source.remote.ChatApiClient
import com.gurumlab.aifriend.data.source.remote.onError
import com.gurumlab.aifriend.data.source.remote.onException
import com.gurumlab.aifriend.data.source.remote.onSuccess
import com.gurumlab.aifriend.util.GPTConstants
import com.gurumlab.aifriend.util.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val apiClient: ChatApiClient,
    private val chatDao: ChatDao
) {

    fun getResponse(
        messages: List<ChatMessage>,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<ChatResponse> = flow {
        val chatCommand =
            listOf(ChatMessage(role = Role.SYSTEM, content = GPTConstants.CHARACTER_SETTING))
        val response = apiClient.getResponse(
            ChatRequest(GPTConstants.CURRENT_VERSION, chatCommand + messages)
        )
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    fun getLastFourMessages(): Flow<List<ChatMessage>> = flow {
        emit(chatDao.getFourMessages())
    }.flowOn(Dispatchers.IO)

    suspend fun insertMessage(message: ChatMessage) {
        chatDao.insertMessage(message)
    }

    fun getAllMessages(): Flow<PagingData<ChatMessage>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 30),
            pagingSourceFactory = { chatDao.getAllMessages() }
        ).flow
    }

    suspend fun deleteLoadingMessage() {
        val loadingMessage = chatDao.getLastMessage()
        chatDao.deleteById(loadingMessage.id)
    }
}