package com.gurumlab.aifriend.data.source.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.gurumlab.aifriend.data.model.ChatMessage

class ChatMessageConverter{

    @TypeConverter
    fun fromChatMessage(value: ChatMessage): String{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toChatMessage(value: String): ChatMessage{
        return Gson().fromJson(value, ChatMessage::class.java)
    }
}