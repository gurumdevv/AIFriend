package com.gurumlab.aifriend

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gurumlab.aifriend.ui.chat.ChatRoute
import com.gurumlab.aifriend.ui.home.HomeRoute
import com.gurumlab.aifriend.ui.settings.SettingsRoute
import com.gurumlab.aifriend.ui.videocall.VideoChatRoute

enum class AIFriendScreen {
    HOME,
    CHAT,
    VIDEO_CALL,
    SETTINGS
}

@Composable
fun AIFriendNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AIFriendScreen.HOME.name,
    ) {
        composable(
            route = AIFriendScreen.HOME.name
        ) {
            HomeRoute(
                onNavigateToChat = { navController.navigate(AIFriendScreen.CHAT.name) },
                onNavigateToVideoCall = { navController.navigate(AIFriendScreen.VIDEO_CALL.name) },
                onNavigateToSettings = { navController.navigate(AIFriendScreen.SETTINGS.name) }
            )
        }

        composable(
            route = AIFriendScreen.CHAT.name,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            ChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(
            route = AIFriendScreen.VIDEO_CALL.name,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            VideoChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(
            route = AIFriendScreen.SETTINGS.name,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            SettingsRoute(
                onNavUp = { navController.navigateUp() }
            )
        }
    }
}