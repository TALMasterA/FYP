package com.example.fyp.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.R
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.core.validateScale
import com.example.fyp.core.UiConstants
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.google.firebase.appdistribution.FirebaseAppDistribution
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    onOpenResetPassword: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenFavorites: () -> Unit = {},
    onOpenMyProfile: () -> Unit = {},
    onOpenFriends: () -> Unit = {},
    onOpenSharedInbox: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onOpenVoiceSettings: () -> Unit = {},
    onOpenFeedback: () -> Unit = {},
    onOpenSystemNotes: () -> Unit = {},
    pendingFriendRequestCount: Int = 0,
    hasUnreadMessages: Boolean = false,
    hasUnseenSharedItems: Boolean = false,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn = uiState.uid != null

    val context = LocalContext.current
    val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context) }

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var selected by remember(uiState.settings.primaryLanguageCode) {
        mutableStateOf(uiState.settings.primaryLanguageCode.ifBlank { "en-US" })
    }

    var sliderValue by remember { mutableFloatStateOf(validateScale(uiState.settings.fontSizeScale)) }

    LaunchedEffect(uiState.settings.fontSizeScale) {
        sliderValue = validateScale(uiState.settings.fontSizeScale)
    }

    // Auto-dismiss error after delay
    val settingsErrorText = uiState.errorKey?.let { t(it) } ?: uiState.errorRaw
    LaunchedEffect(settingsErrorText) {
        if (settingsErrorText != null) {
            delay(UiConstants.ERROR_AUTO_DISMISS_MS)
            viewModel.clearError()
        }
    }

    // Auto-dismiss unlock error after delay
    LaunchedEffect(uiState.unlockError) {
        if (uiState.unlockError != null) {
            delay(UiConstants.COIN_UNLOCK_SUCCESS_DURATION_MS)
            viewModel.clearUnlockError()
        }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.SettingsTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText,
                enabled = true,
                isLoggedIn = isLoggedIn
            )

            // Reset Password Button with larger text
            TextButton(onClick = onOpenResetPassword) {
                Text(
                    text = t(UiTextKey.SettingsResetPW),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }

            // Quick Links for logged in users
            if (isLoggedIn) {
                // Details Settings Header
                Text(
                    text = t(UiTextKey.SettingsQuickLinks),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextButton(
                                onClick = onOpenProfile,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = t(UiTextKey.ProfileTitle),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            TextButton(
                                onClick = onOpenFavorites,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = t(UiTextKey.FavoritesTitle),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        // My Profile button (full width)
                        TextButton(
                            onClick = onOpenMyProfile,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = t(UiTextKey.MyProfileTitle),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Friends button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Friends button with red dot for pending requests or unread messages
                            val showFriendsBadge = pendingFriendRequestCount > 0 || hasUnreadMessages
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                TextButton(
                                    onClick = onOpenFriends,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = t(UiTextKey.FriendsMenuButton),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                                // Badge positioned at top-end of the button
                                if (showFriendsBadge) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .align(androidx.compose.ui.Alignment.TopEnd)
                                            .padding(top = 4.dp, end = 4.dp)
                                    )
                                }
                            }
                            
                            // Shared Inbox button with red dot for unseen shared items
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                TextButton(
                                    onClick = onOpenSharedInbox,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = t(UiTextKey.ShareInboxTitle),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                                // Badge positioned at top-end of the button
                                if (hasUnseenSharedItems) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .align(androidx.compose.ui.Alignment.TopEnd)
                                            .padding(top = 4.dp, end = 4.dp)
                                    )
                                }
                            }
                        }

                        // Shop and Voice Settings buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextButton(
                                onClick = onOpenShop,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = t(UiTextKey.ShopEntry),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            TextButton(
                                onClick = onOpenVoiceSettings,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = t(UiTextKey.VoiceSettingsTitle),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        // Feedback buttons - Two separate options
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Tester Feedback (Firebase App Distribution)
                            TextButton(
                                onClick = {
                                    FirebaseAppDistribution.getInstance().startFeedback(R.string.feedback_additional_info)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = t(UiTextKey.SettingsTesterFeedback),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            // General Feedback (Custom Form)
                            TextButton(
                                onClick = onOpenFeedback,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = t(UiTextKey.FeedbackTitle),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        // System Notes Button
                        TextButton(
                            onClick = onOpenSystemNotes,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = t(UiTextKey.SettingsSystemNotesButton),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }


            // Color Palette (only for logged in users)
            if (isLoggedIn) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        t(UiTextKey.SettingsColorPaletteTitle),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = t(UiTextKey.SettingsColorPaletteDesc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ColorPaletteSelector(
                        currentPaletteId = uiState.settings.colorPaletteId,
                        unlockedPalettes = uiState.settings.unlockedPalettes,
                        coinBalance = uiState.coinStats.coinTotal,
                        onPaletteSelected = { viewModel.updateColorPalette(it) },
                        onUnlockClicked = { paletteId, cost -> viewModel.unlockPaletteWithCoins(paletteId, cost) },
                        unlockError = uiState.unlockError,
                        t = t
                    )
                }
            }


            val settingsErrorText = uiState.errorKey?.let { t(it) } ?: uiState.errorRaw
            settingsErrorText?.let { errorMsg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Primary Language
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    t(UiTextKey.SettingsPrimaryLanguageTitle),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = t(UiTextKey.SettingsPrimaryLanguageDesc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LanguageDropdownField(
                    label = t(UiTextKey.SettingsPrimaryLanguageLabel),
                    selectedCode = selected,
                    options = supportedLanguages,
                    nameFor = { code -> uiLanguageNameFor(code) },
                    onSelected = { code ->
                        selected = code
                        viewModel.updatePrimaryLanguage(code)
                    }
                )
            }

            // Font size
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    t(UiTextKey.SettingsFontSizeTitle),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = t(UiTextKey.SettingsFontSizeDesc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    t(UiTextKey.SettingsScaleTemplate)
                        .replace("{pct}", (sliderValue * 100).toInt().toString())
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = t(UiTextKey.SettingsPreviewHeadline),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize * sliderValue
                            )
                        )
                        Text(
                            text = t(UiTextKey.SettingsPreviewBody),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize * sliderValue
                            )
                        )
                        Text(
                            text = t(UiTextKey.SettingsPreviewLabel),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize * sliderValue
                            )
                        )
                    }
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { newScale -> sliderValue = validateScale(newScale) },
                    onValueChangeFinished = { viewModel.updateFontSizeScale(sliderValue) },
                    valueRange = 0.8f..1.5f,
                    steps = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "80%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "100%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "120%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "150%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Theme
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    t(UiTextKey.SettingsThemeTitle),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = t(UiTextKey.SettingsThemeDesc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val currentMode = uiState.settings.themeMode
                val isAuto = uiState.settings.autoThemeEnabled

                // Helper for Theme Option
                @Composable
                fun ThemeOption(
                    title: String,
                    selected: Boolean,
                    onClick: () -> Unit,
                    modifier: Modifier = Modifier
                ) {
                    Card(
                        modifier = modifier
                            .clickable(onClick = onClick),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1: System & Scheduled
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ThemeOption(
                            title = t(UiTextKey.SettingsThemeSystem),
                            selected = !isAuto && currentMode == "system",
                            onClick = { viewModel.updateThemeMode("system") },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            title = t(UiTextKey.SettingsThemeScheduled),
                            selected = isAuto,
                            onClick = { viewModel.updateThemeMode("scheduled") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Light & Dark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ThemeOption(
                            title = t(UiTextKey.SettingsThemeLight),
                            selected = !isAuto && currentMode == "light",
                            onClick = { viewModel.updateThemeMode("light") },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            title = t(UiTextKey.SettingsThemeDark),
                            selected = !isAuto && currentMode == "dark",
                            onClick = { viewModel.updateThemeMode("dark") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Show schedule info when enabled
            if (uiState.settings.autoThemeEnabled) {
                Text(
                    text = t(UiTextKey.SettingsAutoThemePreview),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // About
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    t(UiTextKey.SettingsAboutTitle),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val versionName = remember {
                    try {
                        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
                    } catch (_: Exception) { "?" }
                }
                Text(
                    text = "${t(UiTextKey.SettingsAppVersion)}$versionName",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = t(UiTextKey.SettingsSyncInfo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}