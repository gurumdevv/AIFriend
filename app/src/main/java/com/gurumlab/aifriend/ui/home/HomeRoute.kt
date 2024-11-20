package com.gurumlab.aifriend.ui.home

import androidx.compose.runtime.Composable

@Composable
fun HomeRoute(
    onNavigateToChat: () -> Unit,
    onNavigateToVideoCall: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    HomeScreen(
        onNavigateToChat = onNavigateToChat,
        onNavigateToVideoCall = onNavigateToVideoCall,
        onNavigateToSettings = onNavigateToSettings,
    )
}