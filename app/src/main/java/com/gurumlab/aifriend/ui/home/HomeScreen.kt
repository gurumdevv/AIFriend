package com.gurumlab.aifriend.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToVideoCall: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    Scaffold { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            HomeBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SettingButton { onNavigateToSettings() }

                ProfileAndButtonsArea(
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToVideoCall = onNavigateToVideoCall
                )
            }
        }
    }
}