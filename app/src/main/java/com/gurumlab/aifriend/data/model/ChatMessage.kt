package com.gurumlab.aifriend.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val role: String,
    val content: String
) : Parcelable