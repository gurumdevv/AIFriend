package com.gurumlab.aifriend

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gurumlab.aifriend.Destinations.CHAT_ROUTE
import com.gurumlab.aifriend.Destinations.HOME_ROUTE
import com.gurumlab.aifriend.Destinations.SETTINGS_ROUTE
import com.gurumlab.aifriend.Destinations.VIDEO_CALL_ROUTE
import com.gurumlab.aifriend.ui.chat.ChatRoute
import com.gurumlab.aifriend.ui.home.HomeRoute
import com.gurumlab.aifriend.ui.settings.SettingsRoute
import com.gurumlab.aifriend.ui.videocall.VideoChatRoute

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
        composable(
            route = HOME_ROUTE
        ) {
            HomeRoute(
                onNavigateToChat = { navController.navigate(CHAT_ROUTE) },
                onNavigateToVideoCall = { navController.navigate(VIDEO_CALL_ROUTE) },
                onNavigateToSettings = { navController.navigate(SETTINGS_ROUTE) }
            )
        }

        composable(
            route = CHAT_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            ChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(
            route = VIDEO_CALL_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            VideoChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(
            route = SETTINGS_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            SettingsRoute(
                onNavUp = { navController.navigateUp() }
            )
        }
    }
}