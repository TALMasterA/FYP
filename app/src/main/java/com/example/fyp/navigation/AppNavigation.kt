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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fyp.core.LocalAppLanguageState
import com.example.fyp.core.LocalFontSizeScale
import com.example.fyp.core.LocalUiLanguages
import com.example.fyp.core.LocalUpdateAppLanguage
import com.example.fyp.core.OfflineBanner
import com.example.fyp.core.createScaledTypography
import com.example.fyp.core.rememberConnectivityState
import com.example.fyp.core.validateScale
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.data.azure.LanguageDisplayNames
import com.example.fyp.data.ui.rememberUiLanguageState
import com.example.fyp.screens.learning.LearningViewModel
import com.example.fyp.screens.onboarding.isOnboardingComplete
import com.example.fyp.screens.settings.SettingsViewModel
import com.example.fyp.screens.wordbank.WordBankViewModel
import com.example.fyp.ui.theme.FYPTheme
import com.example.fyp.ui.theme.ThemeHelper
import com.example.fyp.ui.theme.Typography as AppTypography

private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

// AppScreen route destinations are defined in AppScreens.kt

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
    val learningUiState by learningViewModel.uiState.collectAsStateWithLifecycle()

    // One WordBankViewModel shared across app (so generation continues when leaving page)
    val wordBankViewModel: WordBankViewModel = hiltViewModel()
    val wordBankUiState by wordBankViewModel.uiState.collectAsStateWithLifecycle()

    val fontSizeScale = validateScale(settingsUiState.settings.fontSizeScale)
    val scaledTypography = createScaledTypography(AppTypography, fontSizeScale)

    // Use ThemeHelper to determine dark theme based on settings and time
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = ThemeHelper.shouldUseDarkTheme(settingsUiState.settings, systemDarkTheme)
    val colorPaletteId = settingsUiState.settings.colorPaletteId

    // Observe network connectivity for offline indicator
    val isConnected by rememberConnectivityState()

    // Snackbar state for generation banners
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe generation completion events and show banners
    val sheetDone = learningUiState.sheetGenerationCompleted
    LaunchedEffect(sheetDone) {
        if (sheetDone != null) {
            val result = snackbarHostState.showSnackbar(
                message = appLanguageState.uiTexts[com.example.fyp.model.ui.UiTextKey.GenerationBannerSheet]
                    ?: "Learning sheet ready! Tap to open.",
                actionLabel = appLanguageState.uiTexts[com.example.fyp.model.ui.UiTextKey.ActionOpen] ?: "Open",
                duration = SnackbarDuration.Long
            )
            learningViewModel.consumeSheetGenerationCompleted()
            if (result == SnackbarResult.ActionPerformed) {
                navController.navigate(
                    AppScreen.LearningSheet.routeFor(
                        learningUiState.primaryLanguageCode, sheetDone
                    )
                ) { launchSingleTop = true }
            }
        }
    }

    val quizDone = learningUiState.quizGenerationCompleted
    LaunchedEffect(quizDone) {
        if (quizDone != null) {
            val result = snackbarHostState.showSnackbar(
                message = appLanguageState.uiTexts[com.example.fyp.model.ui.UiTextKey.GenerationBannerQuiz]
                    ?: "Quiz ready! Tap to start.",
                actionLabel = appLanguageState.uiTexts[com.example.fyp.model.ui.UiTextKey.ActionOpen] ?: "Open",
                duration = SnackbarDuration.Long
            )
            learningViewModel.consumeQuizGenerationCompleted()
            if (result == SnackbarResult.ActionPerformed) {
                navController.navigate(
                    AppScreen.Quiz.routeFor(
                        learningUiState.primaryLanguageCode, quizDone
                    )
                ) { launchSingleTop = true }
            }
        }
    }

    val wordBankDone = wordBankUiState.wordBankGenerationCompleted
    LaunchedEffect(wordBankDone) {
        if (wordBankDone != null) {
            val result = snackbarHostState.showSnackbar(
                message = appLanguageState.uiTexts[com.example.fyp.model.ui.UiTextKey.GenerationBannerWordBank]
                    ?: "Word bank ready! Tap to view.",
                actionLabel = appLanguageState.uiTexts[com.example.fyp.model.ui.UiTextKey.ActionOpen] ?: "Open",
                duration = SnackbarDuration.Long
            )
            wordBankViewModel.consumeWordBankGenerationCompleted()
            if (result == SnackbarResult.ActionPerformed) {
                navController.navigate(AppScreen.WordBank.route) { launchSingleTop = true }
            }
        }
    }

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
                    AppScreen.Settings.route,
                )

                val navigateToLogin: () -> Unit = {
                    navController.navigate(AppScreen.Login.route) { launchSingleTop = true }
                }

                Scaffold(
                    contentWindowInsets = WindowInsets.navigationBars,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar {
                                val friendsBadgeCount = pendingFriendRequestCount +
                                    (if (hasUnreadMessages) 1 else 0) +
                                    (if (hasUnseenSharedItems) 1 else 0)

                                val isUserLoggedIn = settingsUiState.uid != null

                                bottomNavItems.forEach { item ->
                                    val isSelected = currentRoute == item.route
                                    val badgeCount = when (item.route) {
                                        AppScreen.Friends.route -> friendsBadgeCount
                                        else -> 0
                                    }

                                    // Disable buttons except Home and Settings when not logged in
                                    val isDisabled = !isUserLoggedIn && item.route !in listOf(
                                        AppScreen.Home.route,
                                        AppScreen.Settings.route
                                    )

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
                                        label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                                        selected = isSelected,
                                        enabled = !isDisabled,
                                        onClick = {
                                            if (isSelected) return@NavigationBarItem
                                            if (item.route == AppScreen.Home.route) {
                                                navController.navigate(AppScreen.Home.route) {
                                                    popUpTo(0) { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            } else {
                                                navController.navigate(item.route) {
                                                    popUpTo(AppScreen.Home.route) {
                                                        saveState = true
                                                        inclusive = false
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { scaffoldPadding ->
                    Column(modifier = Modifier.padding(scaffoldPadding).fillMaxSize()) {
                        OfflineBanner(isConnected = isConnected)

                        NavHost(
                            navController = navController,
                            startDestination = AppScreen.Startup.route,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                            popExitTransition = { fadeOut(animationSpec = tween(300)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            startupAuthGraph(
                                navController = navController,
                                isOnboardingDone = isOnboardingDone,
                                appLanguageState = appLanguageState,
                                updateAppLanguage = updateAppLanguage,
                                uiLanguages = uiLanguages,
                            )
                            mainFeatureGraph(
                                navController = navController,
                                navigateToLogin = navigateToLogin,
                                appLanguageState = appLanguageState,
                                updateAppLanguage = updateAppLanguage,
                                uiLanguages = uiLanguages,
                                pendingFriendRequestCount = pendingFriendRequestCount,
                                hasUnreadMessages = hasUnreadMessages,
                                hasUnseenSharedItems = hasUnseenSharedItems,
                            )
                            learningWordBankGraph(
                                navController = navController,
                                navigateToLogin = navigateToLogin,
                                appLanguageState = appLanguageState,
                                updateAppLanguage = updateAppLanguage,
                                uiLanguages = uiLanguages,
                                learningViewModel = learningViewModel,
                                wordBankViewModel = wordBankViewModel,
                                primaryLanguageCode = settingsUiState.settings.primaryLanguageCode,
                            )
                            friendsChatGraph(
                                navController = navController,
                                navigateToLogin = navigateToLogin,
                                appLanguageState = appLanguageState,
                                hasUnseenSharedItems = hasUnseenSharedItems,
                                hasUnreadMessages = hasUnreadMessages,
                            )
                            settingsProfileGraph(
                                navController = navController,
                                navigateToLogin = navigateToLogin,
                                appLanguageState = appLanguageState,
                                updateAppLanguage = updateAppLanguage,
                                uiLanguages = uiLanguages,
                                settingsViewModel = settingsViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}
