package com.gurumlab.aifriend.data.source.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AppDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val gptApiKey = stringPreferencesKey("gpt_api_key")

    fun getGptApiKey(): Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[gptApiKey] ?: ""
        }

    suspend fun setGptApiKey(apiKey: String) {
        Log.d("DataStore", "setGptApiKey: $apiKey")
        dataStore.edit { preferences ->
            preferences[gptApiKey] = apiKey
            Log.d("DataStore", "API Key set to DataStore: ${preferences[gptApiKey]}")
        }
    }
}