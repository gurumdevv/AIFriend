package com.gurumlab.aifriend

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(durationMillis = 300, easing = Ease),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 300, easing = EaseOut
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(durationMillis = 300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            }
        ) {
            ChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(
            route = AIFriendScreen.VIDEO_CALL.name,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(durationMillis = 300, easing = Ease),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 300, easing = EaseOut
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(durationMillis = 300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            }
        ) {
            VideoChatRoute(
                onNavUp = { navController.navigateUp() }
            )
        }

        composable(
            route = AIFriendScreen.SETTINGS.name,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            SettingsRoute(
                onNavUp = { navController.navigateUp() }
            )
        }
    }
}