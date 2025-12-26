package com.example.fyp

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey
import com.example.fyp.screen.ContinuousConversationScreen
import com.example.fyp.screen.HelpScreen
import com.example.fyp.screen.HomeScreen
import com.example.fyp.screen.SpeechRecognitionScreen

sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object Speech : AppScreen("speech")
    object Help : AppScreen("help")
    object Continuous : AppScreen("continuous")
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
                onStartContinuous = { navController.navigate(AppScreen.Continuous.route) }
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
    }
}