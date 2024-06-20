package com.gurumlab.aifriend.data.source.remote

import com.gurumlab.aifriend.data.model.ChatRequest
import com.gurumlab.aifriend.data.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiClient {

    @POST("v1/chat/completions")
    suspend fun getResponse(@Body requestBody: ChatRequest): ApiResponse<ChatResponse>
}