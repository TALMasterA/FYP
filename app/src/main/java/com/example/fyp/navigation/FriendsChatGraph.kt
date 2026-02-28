package com.example.fyp

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.fyp.core.composableRequireLogin
import com.example.fyp.core.composableRequireLoginWithArgs
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.screens.friends.BlockedUsersScreen
import com.example.fyp.screens.friends.ChatScreen
import com.example.fyp.screens.friends.FriendsScreen
import com.example.fyp.screens.friends.MyProfileScreen
import com.example.fyp.screens.friends.SharedInboxScreen
import com.example.fyp.screens.friends.SharedMaterialDetailScreen

/**
 * Navigation sub-graph: Friends list, chat, shared inbox, blocked users, and my profile.
 */
internal fun NavGraphBuilder.friendsChatGraph(
    navController: NavController,
    navigateToLogin: () -> Unit,
    appLanguageState: AppLanguageState,
    hasUnseenSharedItems: Boolean,
    hasUnreadMessages: Boolean,
) {
    composableRequireLogin(
        route = AppScreen.Friends.route,
        onNeedLogin = navigateToLogin
    ) {
        FriendsScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() },
            onOpenChat = { friendId, friendUsername, friendDisplayName ->
                navController.navigate(
                    AppScreen.Chat.routeFor(friendId, friendUsername, friendDisplayName)
                ) { launchSingleTop = true }
            },
            onOpenSharedInbox = {
                navController.navigate(AppScreen.SharedInbox.route) { launchSingleTop = true }
            },
            onOpenBlockedUsers = {
                navController.navigate(AppScreen.BlockedUsers.route) { launchSingleTop = true }
            },
            onOpenNotifSettings = {
                navController.navigate(AppScreen.NotificationSettings.route) { launchSingleTop = true }
            },
            hasUnseenSharedItems = hasUnseenSharedItems,
            hasUnreadMessages = hasUnreadMessages
        )
    }

    composableRequireLogin(
        route = AppScreen.SharedInbox.route,
        onNeedLogin = navigateToLogin
    ) {
        SharedInboxScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() },
            onViewMaterial = { itemId ->
                navController.navigate(
                    AppScreen.SharedMaterialDetail.routeFor(itemId)
                ) { launchSingleTop = true }
            }
        )
    }

    composableRequireLoginWithArgs(
        route = AppScreen.SharedMaterialDetail.route,
        argNames = listOf("itemId"),
        onNeedLogin = navigateToLogin
    ) { backStackEntry ->
        val itemId = java.net.URLDecoder.decode(
            backStackEntry.arguments?.getString("itemId").orEmpty(), "UTF-8"
        )
        SharedMaterialDetailScreen(
            itemId = itemId,
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }

    composableRequireLogin(
        route = AppScreen.MyProfile.route,
        onNeedLogin = navigateToLogin
    ) {
        MyProfileScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }

    composableRequireLoginWithArgs(
        route = AppScreen.Chat.route,
        argNames = listOf("friendId", "friendUsername", "friendDisplayName"),
        onNeedLogin = navigateToLogin
    ) {
        ChatScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }

    composableRequireLogin(
        route = AppScreen.BlockedUsers.route,
        onNeedLogin = navigateToLogin
    ) {
        BlockedUsersScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }
}
