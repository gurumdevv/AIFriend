package com.gurumlab.aifriend.ui.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsRoute(
    onNavUp: () -> Unit,
) {
    SettingScreen(onNavUp = onNavUp)
}