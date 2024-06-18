package com.gurumlab.aifriend.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gurumlab.aifriend.util.DateTimeConverter
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String,
    val content: String,
    val timeStamp: String = DateTimeConverter.getCurrentDateString(),
) : Parcelable