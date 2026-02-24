package com.example.fyp

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fyp.core.LocalAppLanguageState
import com.example.fyp.core.LocalFontSizeScale
import com.example.fyp.core.LocalUiLanguages
import com.example.fyp.core.LocalUpdateAppLanguage
import com.example.fyp.core.OfflineBanner
import com.example.fyp.core.UiLanguageList
import com.example.fyp.core.composableRequireLogin
import com.example.fyp.core.composableRequireLoginWithArgs
import com.example.fyp.core.createScaledTypography
import com.example.fyp.core.rememberConnectivityState
import com.example.fyp.core.validateScale
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.data.ui.rememberUiLanguageState
import com.example.fyp.screens.help.HelpScreen
import com.example.fyp.screens.onboarding.OnboardingScreen
import com.example.fyp.screens.onboarding.isOnboardingComplete
import com.example.fyp.screens.history.HistoryScreen
import com.example.fyp.screens.home.HomeScreen
import com.example.fyp.screens.favorites.FavoritesScreen
import com.example.fyp.screens.feedback.FeedbackScreen
import com.example.fyp.screens.friends.ChatScreen
import com.example.fyp.screens.friends.FriendsScreen
import com.example.fyp.screens.friends.MyProfileScreen
import com.example.fyp.screens.friends.SharedInboxScreen
import com.example.fyp.screens.friends.SharedMaterialDetailScreen
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
import com.example.fyp.screens.settings.SystemNotesScreen
import com.example.fyp.screens.speech.ContinuousConversationScreen
import com.example.fyp.screens.speech.SpeechRecognitionScreen
import com.example.fyp.screens.wordbank.WordBankScreen
import com.example.fyp.screens.wordbank.WordBankViewModel
import com.example.fyp.ui.theme.FYPTheme
import com.example.fyp.ui.theme.ThemeHelper
import com.example.fyp.ui.theme.Typography as AppTypography

private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

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
    object Feedback : AppScreen("feedback")
    object SystemNotes : AppScreen("system_notes")
    object Friends : AppScreen("friends")
    object MyProfile : AppScreen("my_profile")
    object SharedInbox : AppScreen("shared_inbox")
    object Onboarding : AppScreen("onboarding")

    object SharedMaterialDetail : AppScreen("shared_material_detail/{itemId}") {
        fun routeFor(itemId: String) =
            "shared_material_detail/${java.net.URLEncoder.encode(itemId, "UTF-8")}"
    }

    object Chat : AppScreen("chat/{friendId}/{friendUsername}/{friendDisplayName}") {
        fun routeFor(friendId: String, friendUsername: String, friendDisplayName: String = "") =
            "chat/${java.net.URLEncoder.encode(friendId, "UTF-8")}/${java.net.URLEncoder.encode(friendUsername, "UTF-8")}/${java.net.URLEncoder.encode(friendDisplayName, "UTF-8")}"
    }

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

    val isOnboardingDone = remember {
        isOnboardingComplete(context)
    }

    val supported by produceState(initialValue = listOf("en-US")) {
        value = AzureLanguageConfig.loadSupportedLanguagesSuspend(context)
    }
    
    val uiLanguages = remember(supported) {
        supported.distinct().map { code ->
            code to LanguageDisplayNames.displayName(code)
        }
    }

    val (appLanguageState, updateAppLanguage) = rememberUiLanguageState(uiLanguages)

    // Application-level ViewModel for cross-cutting concerns (used for side effects)
    val appViewModel: AppViewModel = hiltViewModel()
    val pendingFriendRequestCount by appViewModel.pendingFriendRequestCount.collectAsStateWithLifecycle()
    val hasUnreadMessages by appViewModel.hasUnreadMessages.collectAsStateWithLifecycle()
    val hasUnseenSharedItems by appViewModel.hasUnseenSharedItems.collectAsStateWithLifecycle()

    // One SettingsViewModel shared across app
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    // Request POST_NOTIFICATIONS permission on Android 13+ after the user logs in,
    // so the prompt appears in a meaningful context (they are about to use social features).
    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result handled by the system; no action needed */ }
    val settingsUidForPerm = settingsUiState.uid
    LaunchedEffect(settingsUidForPerm) {
        if (settingsUidForPerm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // One LearningViewModel shared for Learning + Sheet
    val learningViewModel: LearningViewModel = hiltViewModel()

    // One WordBankViewModel shared across app (so generation continues when leaving page)
    val wordBankViewModel: WordBankViewModel = hiltViewModel()

    val fontSizeScale = validateScale(settingsUiState.settings.fontSizeScale)
    val scaledTypography = createScaledTypography(AppTypography, fontSizeScale)

    // Use ThemeHelper to determine dark theme based on settings and time
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = ThemeHelper.shouldUseDarkTheme(settingsUiState.settings, systemDarkTheme)
    val colorPaletteId = settingsUiState.settings.colorPaletteId

    // Observe network connectivity for offline indicator
    val isConnected by rememberConnectivityState()

    val bottomNavItems = listOf(
        BottomNavItem(AppScreen.Home.route, Icons.Default.Home, "Home"),
        BottomNavItem(AppScreen.Speech.route, Icons.Default.Mic, "Translate"),
        BottomNavItem(AppScreen.Learning.route, Icons.AutoMirrored.Filled.MenuBook, "Learn"),
        BottomNavItem(AppScreen.Friends.route, Icons.Default.People, "Friends"),
        BottomNavItem(AppScreen.Settings.route, Icons.Default.Settings, "Settings"),
    )

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
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomNav = currentRoute in setOf(
                    AppScreen.Home.route,
                    AppScreen.Speech.route,
                    AppScreen.Learning.route,
                    AppScreen.Friends.route,
                    AppScreen.Settings.route
                )

                // Helper for navigating to login
                val navigateToLogin: () -> Unit = {
                    navController.navigate(AppScreen.Login.route) { launchSingleTop = true }
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar {
                                bottomNavItems.forEach { item ->
                                    val isSelected = currentRoute == item.route
                                    val badgeCount = when (item.route) {
                                        AppScreen.Friends.route -> pendingFriendRequestCount + (if (hasUnreadMessages) 1 else 0) + (if (hasUnseenSharedItems) 1 else 0)
                                        else -> 0
                                    }
                                    NavigationBarItem(
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (badgeCount > 0) {
                                                        Badge {
                                                            Text(if (badgeCount > 99) "99+" else "$badgeCount")
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(item.icon, contentDescription = item.label)
                                            }
                                        },
                                        label = { Text(item.label) },
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(AppScreen.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { scaffoldPadding ->
                Column(modifier = Modifier.padding(scaffoldPadding).fillMaxSize()) {
                    // Offline banner shown at the top when no internet
                    OfflineBanner(isConnected = isConnected)

                    NavHost(
                        navController = navController,
                        startDestination = if (isOnboardingDone) AppScreen.Home.route else AppScreen.Onboarding.route,
                        enterTransition = { fadeIn(animationSpec = tween(300)) },
                        exitTransition = { fadeOut(animationSpec = tween(300)) },
                        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                        popExitTransition = { fadeOut(animationSpec = tween(300)) },
                        modifier = Modifier.weight(1f)
                    ) {
                    composable(AppScreen.Onboarding.route) {
                        OnboardingScreen(
                            onComplete = {
                                navController.navigate(AppScreen.Home.route) {
                                    popUpTo(AppScreen.Onboarding.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

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
                            totalNotificationCount = pendingFriendRequestCount + (if (hasUnreadMessages) 1 else 0) + (if (hasUnseenSharedItems) 1 else 0),
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
                            onBack = {
                                // Use navigateUp() which is safer than popBackStack():
                                // navigateUp() falls back to popBackStack() and handles edge cases
                                if (!navController.navigateUp()) {
                                    navController.navigate(AppScreen.Home.route) {
                                        popUpTo(AppScreen.Home.route) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            viewModel = learningViewModel,
                            onOpenSheet = { primary, target ->
                                navController.navigate(AppScreen.LearningSheet.routeFor(primary, target)) { launchSingleTop = true }
                            }
                        )
                    }

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
                            onOpenFriends = { navController.navigate(AppScreen.Friends.route) { launchSingleTop = true } },
                            onOpenSharedInbox = { navController.navigate(AppScreen.SharedInbox.route) { launchSingleTop = true } },
                            onOpenShop = { navController.navigate(AppScreen.Shop.route) { launchSingleTop = true } },
                            onOpenVoiceSettings = { navController.navigate(AppScreen.VoiceSettings.route) { launchSingleTop = true } },
                            onOpenFeedback = { navController.navigate(AppScreen.Feedback.route) { launchSingleTop = true } },
                            onOpenSystemNotes = { navController.navigate(AppScreen.SystemNotes.route) { launchSingleTop = true } },
                            pendingFriendRequestCount = pendingFriendRequestCount,
                            hasUnreadMessages = hasUnreadMessages,
                            hasUnseenSharedItems = hasUnseenSharedItems,
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
                                navController.navigate(AppScreen.Quiz.routeFor(primaryCode, targetCode)) { launchSingleTop = true }
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
                                // Navigate to Home, clearing the entire back stack
                                // Use inclusive=false so Home itself remains
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
                    ) { backStackEntry ->
                        ChatScreen(
                            appLanguageState = appLanguageState,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(AppScreen.SystemNotes.route) {
                        SystemNotesScreen(
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
}