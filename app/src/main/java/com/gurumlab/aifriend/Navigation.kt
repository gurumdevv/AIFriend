package com.gurumlab.aifriend

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gurumlab.aifriend.Destinations.CHAT_ROUTE
import com.gurumlab.aifriend.Destinations.HOME_ROUTE
import com.gurumlab.aifriend.Destinations.SETTINGS_ROUTE
import com.gurumlab.aifriend.Destinations.VIDEO_CALL_ROUTE
import com.gurumlab.aifriend.chat.ChatRoute
import com.gurumlab.aifriend.home.HomeRoute
import com.gurumlab.aifriend.settings.SettingsRoute
import com.gurumlab.aifriend.videocall.VideoChatRoute

object Destinations {
    const val HOME_ROUTE = "home"
    const val CHAT_ROUTE = "chat"
    const val VIDEO_CALL_ROUTE = "video_call"
    const val SETTINGS_ROUTE = "settings"
}

@Composable
fun AIFriendNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
    ) {
        composable(HOME_ROUTE) {
            HomeRoute(
                onNavigateToChat = { navController.navigate(CHAT_ROUTE) },
                onNavigateToVideoCall = { navController.navigate(VIDEO_CALL_ROUTE) },
                onNavigateToSettings = { navController.navigate(SETTINGS_ROUTE) }
            )
        }

        composable(CHAT_ROUTE) {
            ChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(VIDEO_CALL_ROUTE) {
            VideoChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsRoute(
                onNavUp = { navController.navigateUp() }
            )
        }
    }
}