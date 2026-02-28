package com.example.fyp

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.fyp.core.UiLanguageList
import com.example.fyp.core.composableRequireLogin
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.screens.help.HelpScreen
import com.example.fyp.screens.history.HistoryScreen
import com.example.fyp.screens.home.HomeScreen
import com.example.fyp.screens.speech.ContinuousConversationScreen
import com.example.fyp.screens.speech.SpeechRecognitionScreen

/**
 * Navigation sub-graph: Home, speech/translation, continuous conversation, help, and history.
 */
internal fun NavGraphBuilder.mainFeatureGraph(
    navController: NavController,
    navigateToLogin: () -> Unit,
    appLanguageState: AppLanguageState,
    updateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiLanguages: List<Pair<String, String>>,
    pendingFriendRequestCount: Int,
    unreadMessageCount: Int,
    unseenSharedItemsCount: Int,
) {
    composable(AppScreen.Home.route) {
        HomeScreen(
            uiLanguages = UiLanguageList(uiLanguages),
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onStartSpeech = { navController.navigate(AppScreen.Speech.route) { launchSingleTop = true } },
            onOpenHelp = { navController.navigate(AppScreen.Help.route) { launchSingleTop = true } },
            onStartContinuous = { navController.navigate(AppScreen.Continuous.route) { launchSingleTop = true } },
            onOpenHistory = { navController.navigate(AppScreen.History.route) { launchSingleTop = true } },
            onOpenLogin = { navController.navigate(AppScreen.Login.route) { launchSingleTop = true } },
            onOpenLearning = { navController.navigate(AppScreen.Learning.route) { launchSingleTop = true } },
            onOpenSettings = { navController.navigate(AppScreen.Settings.route) { launchSingleTop = true } },
            onOpenWordBank = { navController.navigate(AppScreen.WordBank.route) { launchSingleTop = true } },
            totalNotificationCount = pendingFriendRequestCount +
                unreadMessageCount +
                unseenSharedItemsCount,
        )
    }

    composable(AppScreen.Help.route) {
        HelpScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = { navController.popBackStack() }
        )
    }

    composableRequireLogin(
        route = AppScreen.Speech.route,
        onNeedLogin = navigateToLogin
    ) {
        SpeechRecognitionScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = { navController.popBackStack() },
            onOpenConversation = {
                navController.navigate(AppScreen.Continuous.route) { launchSingleTop = true }
            }
        )
    }

    composableRequireLogin(
        route = AppScreen.Continuous.route,
        onNeedLogin = navigateToLogin
    ) {
        ContinuousConversationScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = { navController.popBackStack() }
        )
    }

    composableRequireLogin(
        route = AppScreen.History.route,
        onNeedLogin = navigateToLogin
    ) {
        HistoryScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = { navController.popBackStack() }
        )
    }
}
