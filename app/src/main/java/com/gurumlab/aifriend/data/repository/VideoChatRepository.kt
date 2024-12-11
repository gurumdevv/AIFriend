package com.gurumlab.aifriend.data.repository

import com.gurumlab.aifriend.data.model.ChatMessage
import com.gurumlab.aifriend.data.model.ChatRequest
import com.gurumlab.aifriend.data.model.ChatResponse
import com.gurumlab.aifriend.data.model.SpeechRequest
import com.gurumlab.aifriend.data.model.TranscriptionResponse
import com.gurumlab.aifriend.data.source.local.AppDataStore
import com.gurumlab.aifriend.data.source.local.ChatDao
import com.gurumlab.aifriend.data.source.remote.ChatApiClient
import com.gurumlab.aifriend.data.source.remote.SpeechApiClient
import com.gurumlab.aifriend.data.source.remote.TranscriptionApiClient
import com.gurumlab.aifriend.data.source.remote.onError
import com.gurumlab.aifriend.data.source.remote.onException
import com.gurumlab.aifriend.data.source.remote.onSuccess
import com.gurumlab.aifriend.util.GPTConstants
import com.gurumlab.aifriend.util.MediaTypes
import com.gurumlab.aifriend.util.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class VideoChatRepository @Inject constructor(
    private val chatApiClient: ChatApiClient,
    private val transcriptionApiClient: TranscriptionApiClient,
    private val speechApiClient: SpeechApiClient,
    private val chatDao: ChatDao,
    private val dataStore: AppDataStore
) {

    fun getTranscription(
        file: File,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<TranscriptionResponse> = flow {
        val map = HashMap<String, RequestBody>()
        val requestFile = file.asRequestBody(MediaTypes.MP3.type.toMediaTypeOrNull())
        val multiPartBody = MultipartBody.Part.createFormData(FILE, file.name, requestFile)

        val model =
            GPTConstants.TRANSCRIPTION_MODEL.toRequestBody(MediaTypes.STRING.type.toMediaTypeOrNull())
        val language =
            GPTConstants.TRANSCRIPTION_LANGUAGE.toRequestBody(MediaTypes.STRING.type.toMediaTypeOrNull())

        map[MODEL] = model
        map[LANGUAGE] = language

        val response = transcriptionApiClient.getResponse(
            multiPartBody,
            map
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
        val chatCommand =
            listOf(ChatMessage(role = Role.SYSTEM, content = GPTConstants.CHARACTER_SETTING))
        val response = chatApiClient.getResponse(
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

    fun getGptApiKey(): Flow<String> {
        return dataStore.getGptApiKey()
    }

    companion object {
        private const val FILE = "file"
        private const val MODEL = "model"
        private const val LANGUAGE = "language"
    }
}