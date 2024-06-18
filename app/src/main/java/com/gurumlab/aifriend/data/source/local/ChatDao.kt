package com.gurumlab.aifriend.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gurumlab.aifriend.data.model.ChatMessage

@Dao
interface ChatDao {

    @Insert
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM ChatMessage ORDER BY timestamp DESC")
    suspend fun getAllMessages(): List<ChatMessage>

    @Query("SELECT * FROM ChatMessage ORDER BY timestamp DESC LIMIT 5")
    suspend fun getLastFiveMessages(): List<ChatMessage>
}