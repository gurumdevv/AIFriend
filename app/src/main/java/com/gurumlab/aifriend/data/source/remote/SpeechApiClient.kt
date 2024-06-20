package com.gurumlab.aifriend.data.source.remote

import com.gurumlab.aifriend.data.model.SpeechRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface SpeechApiClient {

    @Streaming
    @POST("v1/audio/speech")
    suspend fun getResponse(@Body requestBody: SpeechRequest): ApiResponse<ResponseBody>
}