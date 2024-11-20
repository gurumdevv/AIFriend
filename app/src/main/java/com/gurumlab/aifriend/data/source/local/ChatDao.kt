package com.gurumlab.aifriend.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gurumlab.aifriend.data.model.ChatMessage

@Dao
interface ChatDao {

    @Insert
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM ChatMessage ORDER BY id ASC")
    fun getAllMessages(): PagingSource<Int, ChatMessage>

    @Query("SELECT * FROM ChatMessage ORDER BY id DESC LIMIT 4")
    suspend fun getFourMessages(): List<ChatMessage>

    @Query("DELETE FROM ChatMessage WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM ChatMessage ORDER BY id DESC LIMIT 1")
    suspend fun getLastMessage(): ChatMessage
}