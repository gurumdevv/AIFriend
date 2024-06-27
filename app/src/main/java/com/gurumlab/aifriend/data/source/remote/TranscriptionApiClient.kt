package com.gurumlab.aifriend.data.source.remote

import com.gurumlab.aifriend.data.model.TranscriptionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface TranscriptionApiClient {

    @POST("v1/audio/transcriptions")
    @Multipart
    suspend fun getResponse(
        @Part file: MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    ): ApiResponse<TranscriptionResponse>
}