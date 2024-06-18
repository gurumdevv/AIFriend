package com.gurumlab.aifriend.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gurumlab.aifriend.data.model.ChatMessage

@Database(entities = [ChatMessage::class], version = 1)
@TypeConverters(
    ChatMessageConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}