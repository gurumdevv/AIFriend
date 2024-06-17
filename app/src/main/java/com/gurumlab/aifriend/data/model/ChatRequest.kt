package com.gurumlab.aifriend.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>
) : Parcelable