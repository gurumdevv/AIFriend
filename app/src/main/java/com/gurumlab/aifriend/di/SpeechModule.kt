package com.gurumlab.aifriend.di

import com.gurumlab.aifriend.data.source.local.AppDataStore
import com.gurumlab.aifriend.data.source.remote.ApiCallAdapterFactory
import com.gurumlab.aifriend.data.source.remote.SpeechApiClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Singleton
    @SpeechOkhttpClient
    @Provides
    fun provideOkHttpClient(dataStore: AppDataStore): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val header = Interceptor { chain ->
            val apiKey = runBlocking { dataStore.getGptApiKey().firstOrNull() } ?: ""
            val newRequest = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .addInterceptor(header)
            .build()
    }

    @Singleton
    @SpeechRetrofit
    @Provides
    fun provideRetrofit(@SpeechOkhttpClient client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(@SpeechRetrofit retrofit: Retrofit): SpeechApiClient {
        return retrofit.create(SpeechApiClient::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SpeechOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SpeechRetrofit