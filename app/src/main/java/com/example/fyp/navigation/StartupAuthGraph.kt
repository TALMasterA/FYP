package com.example.fyp

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.screens.login.LoginScreen
import com.example.fyp.screens.login.ResetPasswordScreen
import com.example.fyp.screens.onboarding.OnboardingScreen
import com.example.fyp.screens.startup.StartupScreen

/**
 * Navigation sub-graph: Startup splash, onboarding, login, and password reset.
 */
internal fun NavGraphBuilder.startupAuthGraph(
    navController: NavController,
    isOnboardingDone: Boolean,
    appLanguageState: AppLanguageState,
    updateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiLanguages: List<Pair<String, String>>,
) {
    composable(AppScreen.Startup.route) {
        StartupScreen(
            onFinished = {
                val destination = if (isOnboardingDone) AppScreen.Home.route
                                  else AppScreen.Onboarding.route
                navController.navigate(destination) {
                    popUpTo(AppScreen.Startup.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    composable(AppScreen.Onboarding.route) {
        OnboardingScreen(
            appLanguageState = appLanguageState,
            onComplete = {
                navController.navigate(AppScreen.Home.route) {
                    popUpTo(AppScreen.Onboarding.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    composable(AppScreen.Login.route) {
        LoginScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = { navController.popBackStack() },
            onLoginSuccess = { navController.popBackStack() },
            onOpenResetPassword = { navController.navigate(AppScreen.ResetPassword.route) }
        )
    }

    composable(AppScreen.ResetPassword.route) {
        ResetPasswordScreen(
            uiLanguages = uiLanguages,
            appLanguageState = appLanguageState,
            onUpdateAppLanguage = updateAppLanguage,
            onBack = { navController.popBackStack() }
        )
    }
}
