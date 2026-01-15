package com.example.fyp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fyp.data.AzureLanguageConfig
import com.example.fyp.data.LanguageDisplayNames
import com.example.fyp.feature.help.HelpScreen
import com.example.fyp.feature.history.HistoryScreen
import com.example.fyp.feature.home.HomeScreen
import com.example.fyp.feature.login.AuthViewModel
import com.example.fyp.feature.login.LoginScreen
import com.example.fyp.feature.speech.ContinuousConversationScreen
import com.example.fyp.feature.speech.SpeechRecognitionScreen
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.AuthState
import com.example.fyp.model.UiTextKey
import com.example.fyp.data.UiLanguageCacheStore
import com.example.fyp.model.baseUiTextsHash
import com.example.fyp.feature.login.ResetPasswordScreen

sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object Speech : AppScreen("speech")
    object Help : AppScreen("help")
    object Continuous : AppScreen("continuous")
    object Login : AppScreen("login")
    object History : AppScreen("history")
    object ResetPassword : AppScreen("reset_password")
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

    val languageSaver = mapSaver(
        save = { state ->
            mapOf(
                "code" to state.selectedUiLanguage,
                "texts" to state.uiTexts.map { it.key.name to it.value }
            )
        },
        restore = { map ->
            val code = map["code"] as? String ?: uiLanguages[0].first

            val textsList: List<Pair<String, String>> =
                (map["texts"] as? List<*>)?.mapNotNull { it as? Pair<String, String> } ?: emptyList()

            AppLanguageState(
                selectedUiLanguage = code,
                uiTexts = textsList.associate { (k, v) -> UiTextKey.valueOf(k) to v }
            )
        }
    )

    /*var appLanguageState by rememberSaveable(stateSaver = languageSaver) {
        mutableStateOf(
            AppLanguageState(
                selectedUiLanguage = uiLanguages[0].first,
                uiTexts = emptyMap()
            )
        )
    }*/

    val cache = remember { UiLanguageCacheStore(context) }

    var appLanguageState by remember {
        mutableStateOf(AppLanguageState(selectedUiLanguage = uiLanguages[0].first, uiTexts = emptyMap()))
    }

    LaunchedEffect(Unit) {
        val defaultCode = uiLanguages[0].first
        val selected = cache.getSelectedLanguage(defaultCode)
        val currentHash = baseUiTextsHash()
        val cachedHash = cache.getBaseHash()

        if (selected.startsWith("en")) {
            appLanguageState = appLanguageState.copy(selectedUiLanguage = selected, uiTexts = emptyMap())
            return@LaunchedEffect
        }

        if (cachedHash == currentHash) {
            val cachedMap = cache.loadUiTexts(selected)
            if (!cachedMap.isNullOrEmpty()) {
                appLanguageState = appLanguageState.copy(
                    selectedUiLanguage = selected,
                    uiTexts = cachedMap
                )
            } else {
                // Keep selected language, but no translations yet
                appLanguageState = appLanguageState.copy(
                    selectedUiLanguage = selected,
                    uiTexts = emptyMap()
                )
            }
        } else {
            // Base texts changed => invalidate cache, but keep the selected language
            cache.clearUiTexts(selected)
            cache.setBaseHash(currentHash)
            appLanguageState = appLanguageState.copy(
                selectedUiLanguage = selected,
                uiTexts = emptyMap()
            )
        }
    }

    var pendingSave by remember {
        mutableStateOf<Pair<String, Map<UiTextKey, String>>?>(null)
    }

    fun updateAppLanguage(code: String, uiTexts: Map<UiTextKey, String>) {
        appLanguageState = appLanguageState.copy(
            selectedUiLanguage = code,
            uiTexts = uiTexts
        )
        pendingSave = code to uiTexts
    }

    LaunchedEffect(pendingSave) {
        val pair = pendingSave ?: return@LaunchedEffect
        val (code, uiTexts) = pair

        cache.setSelectedLanguage(code)

        if (code.startsWith("en")) {
            cache.setBaseHash(baseUiTextsHash())
            // optional: clear stored english map (usually not needed)
            return@LaunchedEffect
        }

        cache.saveUiTexts(code, uiTexts)
        cache.setBaseHash(baseUiTextsHash())
    }

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
                    onUpdateAppLanguage = ::updateAppLanguage,
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
                    }
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
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = ::updateAppLanguage,
                    onBack = { navController.popBackStack() },
                    onLoginSuccess = { navController.popBackStack() },
                    onOpenResetPassword = { navController.navigate(AppScreen.ResetPassword.route) }
                )
            }

            composable(AppScreen.ResetPassword.route) {
                ResetPasswordScreen(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = ::updateAppLanguage,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(AppScreen.History.route) {
                RequireLogin(
                    content = {
                        HistoryScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = ::updateAppLanguage,
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

@Composable
private fun RequireLogin(
    content: @Composable () -> Unit,
    onNeedLogin: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        is AuthState.LoggedIn -> content()
        AuthState.Loading -> { /* show a progress indicator UI */ }
        AuthState.LoggedOut -> LaunchedEffect(authState) { onNeedLogin() }
    }
}