package com.gurumlab.aifriend.ui.chat

import androidx.compose.runtime.Composable

@Composable
fun ChatRoute(
    onNavUp: () -> Unit,
) {
    ChatScreen(onNavUp = onNavUp)
}