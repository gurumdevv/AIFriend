package com.gurumlab.aifriend.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TranscriptionResponse(
    val text: String
) : Parcelable