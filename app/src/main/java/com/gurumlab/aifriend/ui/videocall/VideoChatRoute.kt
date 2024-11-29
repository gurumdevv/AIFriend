package com.gurumlab.aifriend.ui.videocall

import androidx.compose.runtime.Composable

@Composable
fun VideoChatRoute(
    onNavUp: () -> Unit,
) {
    VideoCallScreen(
        onNavUp = onNavUp,
    )
}