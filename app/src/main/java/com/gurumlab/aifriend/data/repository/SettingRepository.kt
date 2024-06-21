package com.gurumlab.aifriend.data.repository

import com.gurumlab.aifriend.data.source.local.AppDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val dataStore: AppDataStore
) {

    fun getGptApiKey(): Flow<String> {
        return dataStore.getGptApiKey()
    }

    suspend fun setGptApiKey(gptApiKey: String) {
        dataStore.setGptApiKey(gptApiKey)
    }
}