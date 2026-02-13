package com.example.fyp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fyp.core.LocalAppLanguageState
import com.example.fyp.core.LocalFontSizeScale
import com.example.fyp.core.LocalUiLanguages
import com.example.fyp.core.LocalUpdateAppLanguage
import com.example.fyp.core.OfflineBanner
import com.example.fyp.core.composableRequireLogin
import com.example.fyp.core.composableRequireLoginWithArgs
import com.example.fyp.core.createScaledTypography
import com.example.fyp.core.rememberConnectivityState
import com.example.fyp.core.validateScale
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.data.ui.rememberUiLanguageState
import com.example.fyp.screens.help.HelpScreen
import com.example.fyp.screens.history.HistoryScreen
import com.example.fyp.screens.home.HomeScreen
import com.example.fyp.screens.favorites.FavoritesScreen
import com.example.fyp.screens.learning.LearningScreen
import com.example.fyp.screens.learning.LearningSheetScreen
import com.example.fyp.screens.learning.LearningViewModel
import com.example.fyp.screens.learning.QuizScreen
import com.example.fyp.screens.login.LoginScreen
import com.example.fyp.screens.login.ResetPasswordScreen
import com.example.fyp.screens.settings.ProfileScreen
import com.example.fyp.screens.settings.SettingsScreen
import com.example.fyp.screens.settings.SettingsViewModel
import com.example.fyp.screens.settings.ShopScreen
import com.example.fyp.screens.settings.VoiceSettingsScreen
import com.example.fyp.screens.speech.ContinuousConversationScreen
import com.example.fyp.screens.speech.SpeechRecognitionScreen
import com.example.fyp.screens.wordbank.WordBankScreen
import com.example.fyp.screens.wordbank.WordBankViewModel
import com.example.fyp.ui.theme.FYPTheme
import com.example.fyp.ui.theme.Typography as AppTypography

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
    object WordBank : AppScreen("word_bank")
    object Profile : AppScreen("profile")
    object Favorites : AppScreen("favorites")
    object Shop : AppScreen("shop")
    object VoiceSettings : AppScreen("voice_settings")

    object LearningSheet : AppScreen("learning_sheet/{primaryCode}/{targetCode}") {
        fun routeFor(primaryCode: String, targetCode: String) =
            "learning_sheet/$primaryCode/$targetCode"
    }

    object Quiz : AppScreen("quiz/{primaryCode}/{targetCode}") {
        fun routeFor(primaryCode: String, targetCode: String) =
            "quiz/$primaryCode/$targetCode"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val supported by produceState(initialValue = listOf("en-US")) {
        value = AzureLanguageConfig.loadSupportedLanguagesSuspend(context)
    }
    
    val uiLanguages = remember(supported) {
        supported.distinct().map { code ->
            code to LanguageDisplayNames.displayName(code)
        }
    }

    val (appLanguageState, updateAppLanguage) = rememberUiLanguageState(uiLanguages)

    // One SettingsViewModel shared across app
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    // One LearningViewModel shared for Learning + Sheet
    val learningViewModel: LearningViewModel = hiltViewModel()

    // One WordBankViewModel shared across app (so generation continues when leaving page)
    val wordBankViewModel: WordBankViewModel = hiltViewModel()

    val fontSizeScale = validateScale(settingsUiState.settings.fontSizeScale)
    val scaledTypography = createScaledTypography(AppTypography, fontSizeScale)

    val themeMode = settingsUiState.settings.themeMode
    val darkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    val colorPaletteId = settingsUiState.settings.colorPaletteId

    // Observe network connectivity for offline indicator
    val isConnected by rememberConnectivityState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        CompositionLocalProvider(
            LocalFontSizeScale provides fontSizeScale,
            LocalAppLanguageState provides appLanguageState,
            LocalUiLanguages provides uiLanguages,
            LocalUpdateAppLanguage provides updateAppLanguage
        ) {
            FYPTheme(darkTheme = darkTheme, colorPaletteId = colorPaletteId, typography = scaledTypography) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Offline banner shown at the top when no internet
                    OfflineBanner(isConnected = isConnected)

                    // Helper for navigating to login
                    val navigateToLogin: () -> Unit = {
                        navController.navigate(AppScreen.Login.route) { launchSingleTop = true }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = AppScreen.Home.route,
                        modifier = Modifier.weight(1f)
                    ) {
                    composable(AppScreen.Home.route) {
                        HomeScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = updateAppLanguage,
                            onStartSpeech = { navController.navigate(AppScreen.Speech.route) },
                            onOpenHelp = { navController.navigate(AppScreen.Help.route) },
                            onStartContinuous = { navController.navigate(AppScreen.Continuous.route) },
                            onOpenHistory = { navController.navigate(AppScreen.History.route) { launchSingleTop = true } },
                            onOpenLogin = { navController.navigate(AppScreen.Login.route) { launchSingleTop = true } },
                            onOpenLearning = { navController.navigate(AppScreen.Learning.route) { launchSingleTop = true } },
                            onOpenSettings = { navController.navigate(AppScreen.Settings.route) { launchSingleTop = true } },
                            onOpenWordBank = { navController.navigate(AppScreen.WordBank.route) { launchSingleTop = true } },
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

                    composableRequireLogin(
                        route = AppScreen.Learning.route,
                        onNeedLogin = navigateToLogin
                    ) {
                        LearningScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = updateAppLanguage,
                            onBack = { navController.popBackStack() },
                            viewModel = learningViewModel,
                            onOpenSheet = { primary, target ->
                                navController.navigate(AppScreen.LearningSheet.routeFor(primary, target))
                            }
                        )
                    }

                    composable(AppScreen.Settings.route) {
                        SettingsScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = updateAppLanguage,
                            onBack = { navController.popBackStack() },
                            onOpenResetPassword = { navController.navigate(AppScreen.ResetPassword.route) },
                            onOpenProfile = { navController.navigate(AppScreen.Profile.route) },
                            onOpenFavorites = { navController.navigate(AppScreen.Favorites.route) },
                            onOpenShop = { navController.navigate(AppScreen.Shop.route) },
                            onOpenVoiceSettings = { navController.navigate(AppScreen.VoiceSettings.route) },
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

                    composableRequireLoginWithArgs(
                        route = AppScreen.LearningSheet.route,
                        argNames = listOf("primaryCode", "targetCode"),
                        onNeedLogin = navigateToLogin
                    ) { backStackEntry ->
                        val primaryCode = backStackEntry.arguments?.getString("primaryCode").orEmpty()
                        val targetCode = backStackEntry.arguments?.getString("targetCode").orEmpty()

                        LearningSheetScreen(
                            uiLanguages = uiLanguages,
                            appLanguageState = appLanguageState,
                            onUpdateAppLanguage = updateAppLanguage,
                            primaryCode = primaryCode,
                            targetCode = targetCode,
                            onBack = { navController.popBackStack() },
                            learningViewModel = learningViewModel,
                            onOpenQuiz = {
                                navController.navigate(AppScreen.Quiz.routeFor(primaryCode, targetCode))
                            }
                        )
                    }

                    composableRequireLoginWithArgs(
                        route = AppScreen.Quiz.route,
                        argNames = listOf("primaryCode", "targetCode"),
                        onNeedLogin = navigateToLogin
                    ) { backStackEntry ->
                        val primaryCode = backStackEntry.arguments?.getString("primaryCode").orEmpty()
                        val targetCode = backStackEntry.arguments?.getString("targetCode").orEmpty()

                        QuizScreen(
                            appLanguageState = appLanguageState,
                            primaryCode = primaryCode,
                            targetCode = targetCode,
                            onBack = { navController.popBackStack() },
                            learningViewModel = learningViewModel
                        )
                    }

                    composableRequireLogin(
                        route = AppScreen.WordBank.route,
                        onNeedLogin = navigateToLogin
                    ) {
                        WordBankScreen(
                            viewModel = wordBankViewModel,
                            appLanguageState = appLanguageState,
                            primaryLanguageCode = settingsUiState.settings.primaryLanguageCode,
                            onBack = { navController.popBackStack() }
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
                                    popUpTo(AppScreen.Home.route) { inclusive = true }
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
                }
                }
            }
        }
    }
}