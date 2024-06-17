package com.gurumlab.aifriend.ui.chat

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatRoute(
    onNavUp: () -> Unit,
) {
    val viewModel = hiltViewModel<ChatViewModel>()
    ChatScreen(onNavUp = onNavUp, viewModel = viewModel)
}