package com.gurumlab.aifriend.ui.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsRoute(
    onNavUp: () -> Unit,
) {
    val viewModel = hiltViewModel<SettingViewModel>()
    SettingScreen(
        viewModel = viewModel,
        onNavUp = onNavUp
    )
}