package com.gurumlab.aifriend.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpeechRequest(
    val model: String,
    val input: String,
    val voice: String
) : Parcelable