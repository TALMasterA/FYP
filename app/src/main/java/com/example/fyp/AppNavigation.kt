package com.example.fyp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.data.config.LanguageDisplayNames
import com.example.fyp.data.ui.rememberUiLanguageState
import com.example.fyp.screens.help.HelpScreen
import com.example.fyp.screens.history.HistoryScreen
import com.example.fyp.screens.home.HomeScreen
import com.example.fyp.screens.login.LoginScreen
import com.example.fyp.screens.login.ResetPasswordScreen
import com.example.fyp.screens.speech.ContinuousConversationScreen
import com.example.fyp.screens.speech.SpeechRecognitionScreen
import com.example.fyp.core.RequireLoginGate
import com.example.fyp.screens.learning.LearningScreen
import com.example.fyp.screens.settings.SettingsScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.fyp.screens.learning.LearningSheetScreen

sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object Speech : AppScreen("speech")
    object Help : AppScreen("help")
    object Continuous : AppScreen("continuous")
    object Login : AppScreen("login")
    object History : AppScreen("history")
    object ResetPassword : AppScreen("reset_password")
    object Learning : AppScreen("learning")
    object Settings : AppScreen("settings")
    object LearningSheet : AppScreen("learning_sheet/{languageCode}") {
        fun routeFor(languageCode: String) = "learning_sheet/$languageCode"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val supported = remember { AzureLanguageConfig.loadSupportedLanguages(context) }
    val uiLanguages = remember(supported) {
        supported.distinct().map { code ->
            code to LanguageDisplayNames.displayName(code)
        }
    }

    val (appLanguageState, updateAppLanguage) = rememberUiLanguageState(uiLanguages)

    Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.route
        ) {
            composable(AppScreen.Home.route) {
                HomeScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = updateAppLanguage,
                    onStartSpeech = { navController.navigate(AppScreen.Speech.route) },
                    onOpenHelp = { navController.navigate(AppScreen.Help.route) },
                    onStartContinuous = { navController.navigate(AppScreen.Continuous.route) },
                    onOpenHistory = {
                        navController.navigate(AppScreen.History.route) { launchSingleTop = true }
                    },
                    onOpenLogin = {
                        navController.navigate(AppScreen.Login.route) { launchSingleTop = true }
                    },
                    onOpenResetPassword = {
                        navController.navigate(AppScreen.ResetPassword.route) { launchSingleTop = true }
                    },
                    onOpenLearning = {
                        navController.navigate(AppScreen.Learning.route) { launchSingleTop = true }
                    },
                    onOpenSettings = {
                        navController.navigate(AppScreen.Settings.route) { launchSingleTop = true }
                    },
                )
            }

            composable(AppScreen.Speech.route) {
                SpeechRecognitionScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = updateAppLanguage,
                    onBack = { navController.popBackStack() }
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

            composable(AppScreen.Continuous.route) {
                ContinuousConversationScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = updateAppLanguage,
                    onBack = { navController.popBackStack() }
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

            composable(AppScreen.History.route) {
                RequireLoginGate(
                    content = {
                        HistoryScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = updateAppLanguage,
                            onBack = { navController.popBackStack() }
                        )
                    },
                    onNeedLogin = {
                        navController.navigate(AppScreen.Login.route) { launchSingleTop = true }
                    }
                )
            }

            composable(AppScreen.Learning.route) {
                RequireLoginGate(
                    content = {
                        LearningScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = updateAppLanguage,
                            onBack = { navController.popBackStack() },
                            onOpenSheet = { lang -> navController.navigate(AppScreen.LearningSheet.routeFor(lang)) }
                        )
                    },
                    onNeedLogin = {
                        navController.navigate(AppScreen.Login.route) { launchSingleTop = true }
                    }
                )
            }

            composable(AppScreen.Settings.route) {
                SettingsScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = updateAppLanguage,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppScreen.LearningSheet.route,
                arguments = listOf(navArgument("languageCode") { type = NavType.StringType })
            ) { backStackEntry ->
                val languageCode = backStackEntry.arguments?.getString("languageCode").orEmpty()

                RequireLoginGate(
                    content = {
                        LearningSheetScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = updateAppLanguage,
                            languageCode = languageCode,
                            onBack = { navController.popBackStack() }
                        )
                    },
                    onNeedLogin = {
                        navController.navigate(AppScreen.Login.route) { launchSingleTop = true }
                    }
                )
            }
        }
    }
}