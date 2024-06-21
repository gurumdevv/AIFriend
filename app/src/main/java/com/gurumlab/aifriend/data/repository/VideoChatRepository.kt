package com.gurumlab.aifriend.data.repository

import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.model.ChatRequest
import com.gurumlab.aifriend.data.model.ChatResponse
import com.gurumlab.aifriend.data.model.SpeechRequest
import com.gurumlab.aifriend.data.model.TranscriptionResponse
import com.gurumlab.aifriend.data.source.local.ChatDao
import com.gurumlab.aifriend.data.source.remote.ChatApiClient
import com.gurumlab.aifriend.data.source.remote.SpeechApiClient
import com.gurumlab.aifriend.data.source.remote.TranscriptionApiClient
import com.gurumlab.aifriend.data.source.remote.onError
import com.gurumlab.aifriend.data.source.remote.onException
import com.gurumlab.aifriend.data.source.remote.onSuccess
import com.gurumlab.aifriend.util.GPTConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class VideoChatRepository @Inject constructor(
    private val chatApiClient: ChatApiClient,
    private val transcriptionApiClient: TranscriptionApiClient,
    private val speechApiClient: SpeechApiClient,
    private val chatDao: ChatDao
) {

    fun getTranscription(
        file: File,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<TranscriptionResponse> = flow {
        val response = transcriptionApiClient.getResponse(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody()),
            MultipartBody.Part.createFormData("model", GPTConstants.TRANSCRIPTION_MODEL),
            MultipartBody.Part.createFormData("language", GPTConstants.TRANSCRIPTION_LANGUAGE)
        )
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    fun getChatResponse(
        messages: List<ChatMessage>,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<ChatResponse> = flow {
        val response = chatApiClient.getResponse(
            ChatRequest(GPTConstants.CURRENT_VERSION, messages)
        )
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            onError("code: $code message: $message")
        }.onException {
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    fun getSpeech(
        message: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<InputStream> = flow {
        val response = speechApiClient.getResponse(
            SpeechRequest(
                model = GPTConstants.TTS_MODEL,
                input = message,
                voice = GPTConstants.VOICE
            )
        )
        response.onSuccess {
            emit(it.byteStream())
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
}