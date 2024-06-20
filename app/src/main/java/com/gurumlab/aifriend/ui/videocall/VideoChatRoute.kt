package com.gurumlab.aifriend.ui.videocall

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VideoChatRoute(
    onNavUp: () -> Unit,
) {
    val viewModel = hiltViewModel<VideoChatViewModel>()
    VideoCallScreen(
        onNavUp = onNavUp,
        viewModel = viewModel
    )
}