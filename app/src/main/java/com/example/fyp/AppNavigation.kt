package com.example.fyp

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey
import com.example.fyp.feature.speech.ContinuousConversationScreen
import com.example.fyp.feature.help.HelpScreen
import com.example.fyp.feature.home.HomeScreen
import com.example.fyp.feature.speech.SpeechRecognitionScreen
import com.example.fyp.feature.login.LoginScreen
import com.example.fyp.feature.history.HistoryScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.fyp.feature.login.AuthViewModel
import com.example.fyp.model.AuthState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object Speech : AppScreen("speech")
    object Help : AppScreen("help")
    object Continuous : AppScreen("continuous")
    object Login : AppScreen("login")
    object History : AppScreen("history")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val uiLanguages = listOf(
        "en" to "English UI",
        "zh-HK" to "中文（香港）UI",
        "ja-JP" to "日本語 UI"
    )

    val languageSaver = mapSaver(
        save = { state ->
            mapOf(
                "code" to state.selectedUiLanguage,
                "texts" to state.uiTexts.map { it.key.name to it.value }
            )
        },
        restore = { map ->
            val code = map["code"] as String
            val textsList = map["texts"] as List<Pair<String, String>>
            AppLanguageState(
                selectedUiLanguage = code,
                uiTexts = textsList.associate { (k, v) ->
                    UiTextKey.valueOf(k) to v
                }
            )
        }
    )

    var appLanguageState by rememberSaveable(stateSaver = languageSaver) {
        mutableStateOf(
            AppLanguageState(
                selectedUiLanguage = uiLanguages[0].first,
                uiTexts = emptyMap()
            )
        )
    }

    fun updateAppLanguage(code: String, uiTexts: Map<UiTextKey, String>) {
        appLanguageState = appLanguageState.copy(
            selectedUiLanguage = code,
            uiTexts = uiTexts
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
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
                    onUpdateAppLanguage = ::updateAppLanguage,
                    onStartSpeech = { navController.navigate(AppScreen.Speech.route) },
                    onOpenHelp = { navController.navigate(AppScreen.Help.route) },
                    onStartContinuous = { navController.navigate(AppScreen.Continuous.route) },
                    onOpenLogin = { navController.navigate(AppScreen.Login.route) },
                    onOpenHistory = { navController.navigate(AppScreen.History.route) }
                )
            }
            composable(AppScreen.Speech.route) {
                SpeechRecognitionScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = ::updateAppLanguage,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(AppScreen.Help.route) {
                HelpScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = ::updateAppLanguage,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(AppScreen.Continuous.route) {
                ContinuousConversationScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = ::updateAppLanguage,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(AppScreen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.popBackStack() // go back to previous screen (Home / History)
                    }
                )
            }
            composable(AppScreen.History.route) {
                RequireLogin(
                    content = {
                        HistoryScreen(
                            appLanguageState = appLanguageState,
                            onBack = { navController.popBackStack() }
                        )
                    },
                    onNeedLogin = {
                        navController.navigate(AppScreen.Login.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun RequireLogin(
    content: @Composable () -> Unit,
    onNeedLogin: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        is AuthState.LoggedIn -> content()
        AuthState.LoggedOut -> LaunchedEffect(Unit) { onNeedLogin() }
    }
}