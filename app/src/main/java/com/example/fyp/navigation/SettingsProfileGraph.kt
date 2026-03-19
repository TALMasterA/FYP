package com.example.fyp

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.fyp.core.composableRequireLogin
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.screens.favorites.FavoritesScreen
import com.example.fyp.screens.feedback.FeedbackScreen
import com.example.fyp.screens.settings.NotificationSettingsScreen
import com.example.fyp.screens.settings.ProfileScreen
import com.example.fyp.screens.settings.SettingsScreen
import com.example.fyp.screens.settings.SettingsViewModel
import com.example.fyp.screens.settings.ShopScreen
import com.example.fyp.screens.settings.VoiceSettingsScreen

/**
 * Navigation sub-graph: Settings hub, profile, shop, voice, notifications,
 * favorites, and feedback.
 */
internal fun NavGraphBuilder.settingsProfileGraph(
    navController: NavController,
    navigateToLogin: () -> Unit,
    appLanguageState: AppLanguageState,
    updateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiLanguages: List<Pair<String, String>>,
    settingsViewModel: SettingsViewModel,
) {
    composable(AppScreen.Settings.route) {
        SettingsScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = { navController.popBackStack() },
            onOpenResetPassword = { navController.navigate(AppScreen.ResetPassword.route) { launchSingleTop = true } },
            onOpenProfile = { navController.navigate(AppScreen.Profile.route) { launchSingleTop = true } },
            onOpenFavorites = { navController.navigate(AppScreen.Favorites.route) { launchSingleTop = true } },
            onOpenMyProfile = { navController.navigate(AppScreen.MyProfile.route) { launchSingleTop = true } },
            onOpenShop = { navController.navigate(AppScreen.Shop.route) { launchSingleTop = true } },
            onOpenVoiceSettings = { navController.navigate(AppScreen.VoiceSettings.route) { launchSingleTop = true } },
            onOpenFeedback = { navController.navigate(AppScreen.Feedback.route) { launchSingleTop = true } },
            onOpenNotificationSettings = { navController.navigate(AppScreen.NotificationSettings.route) { launchSingleTop = true } },
            viewModel = settingsViewModel
        )
    }

    composableRequireLogin(
        route = AppScreen.Shop.route,
        onNeedLogin = navigateToLogin
    ) {
        ShopScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }

    composableRequireLogin(
        route = AppScreen.VoiceSettings.route,
        onNeedLogin = navigateToLogin
    ) {
        VoiceSettingsScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() },
            viewModel = settingsViewModel
        )
    }

    composableRequireLogin(
        route = AppScreen.Profile.route,
        onNeedLogin = navigateToLogin
    ) {
        ProfileScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() },
            onAccountDeleted = {
                navController.navigate(AppScreen.Home.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    composableRequireLogin(
        route = AppScreen.Favorites.route,
        onNeedLogin = navigateToLogin
    ) {
        FavoritesScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }

    composable(AppScreen.Feedback.route) {
        FeedbackScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() }
        )
    }

    composableRequireLogin(
        route = AppScreen.NotificationSettings.route,
        onNeedLogin = navigateToLogin
    ) {
        NotificationSettingsScreen(
            appLanguageState = appLanguageState,
            onBack = { navController.popBackStack() },
            viewModel = settingsViewModel
        )
    }
}
