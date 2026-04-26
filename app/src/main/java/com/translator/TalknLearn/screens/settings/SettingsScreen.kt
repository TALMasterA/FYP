package com.translator.TalknLearn.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.translator.TalknLearn.R
import com.translator.TalknLearn.core.AppLanguageDropdown
import com.translator.TalknLearn.core.LanguageDropdownField
import com.translator.TalknLearn.core.StandardScreenScaffold
import com.translator.TalknLearn.core.rememberUiTextFunctions
import com.translator.TalknLearn.core.validateScale
import com.translator.TalknLearn.core.UiConstants
import com.translator.TalknLearn.data.azure.AzureLanguageConfig
import com.translator.TalknLearn.data.azure.AzureVoiceConfig
import com.translator.TalknLearn.model.ui.AppLanguageState
import com.translator.TalknLearn.model.ui.BaseUiTexts
import com.translator.TalknLearn.model.ui.UiTextKey
import kotlinx.coroutines.delay
import com.translator.TalknLearn.ui.theme.AppSpacing
import com.translator.TalknLearn.ui.theme.AppCorners

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    onOpenResetPassword: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenMyProfile: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onOpenFeedback: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn = uiState.uid != null

    val context = LocalContext.current
    val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context) }
    val supportedVoiceLanguages = remember {
        val voiceSupportedSet = AzureVoiceConfig.getSupportedLanguages()
        supportedLanguages.filter { it in voiceSupportedSet }
    }

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

    // Pending language code for the confirmation dialog (set when user selects a new language)
    var pendingLanguageCode by remember { mutableStateOf<String?>(null) }
    var showInfoDialog by remember { mutableStateOf(false) }

    val settingsInfoMessage = if (isLoggedIn) {
        t(UiTextKey.SettingsSyncInfo)
    } else {
        "${t(UiTextKey.SettingsNotLoggedInWarning)}\n\n${t(UiTextKey.SettingsSyncInfo)}"
    }

    // ── Primary language change confirmation dialog ──
    pendingLanguageCode?.let { pendingCode ->
        AlertDialog(
            onDismissRequest = {
                selected = uiState.settings.primaryLanguageCode.ifBlank { "en-US" }
                pendingLanguageCode = null
            },
            title = { Text(t(UiTextKey.SettingsPrimaryLanguageConfirmTitle)) },
            text = { Text(t(UiTextKey.SettingsPrimaryLanguageConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updatePrimaryLanguage(pendingCode)
                    pendingLanguageCode = null
                }) {
                    Text(t(UiTextKey.ActionConfirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selected = uiState.settings.primaryLanguageCode.ifBlank { "en-US" }
                    pendingLanguageCode = null
                }) {
                    Text(t(UiTextKey.ActionCancel))
                }
            }
        )
    }

    // ── Primary language cooldown alert dialog ──
    uiState.primaryLanguageCooldownDays?.let { days ->
        val hours = uiState.primaryLanguageCooldownHours ?: 0
        // Reset dropdown to current saved language since the change was rejected
        selected = uiState.settings.primaryLanguageCode.ifBlank { "en-US" }
        AlertDialog(
            onDismissRequest = { viewModel.dismissCooldownDialog() },
            title = { Text(t(UiTextKey.SettingsPrimaryLanguageCooldownTitle)) },
            text = {
                Text(
                    if (days > 0) {
                        t(UiTextKey.SettingsPrimaryLanguageCooldownMessage)
                            .replace("{days}", days.toString())
                    } else {
                        t(UiTextKey.SettingsPrimaryLanguageCooldownMessageHours)
                            .replace("{hours}", hours.toString())
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissCooldownDialog() }) {
                    Text(t(UiTextKey.ActionConfirm))
                }
            }
        )
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(t(UiTextKey.SettingsTitle)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(settingsInfoMessage)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(t(UiTextKey.ActionConfirm))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.SettingsTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            IconButton(onClick = { showInfoDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = t(UiTextKey.SettingsTitle),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        hasBottomNav = true
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.large)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.extraLarge)
        ) {
            AppLanguageDropdown(
                uiLanguages = uiLanguages,
                appLanguageState = appLanguageState,
                onUpdateAppLanguage = onUpdateAppLanguage,
                uiText = uiText,
                enabled = true,
                isLoggedIn = isLoggedIn
            )

            // Primary Language — only for logged-in users
            if (isLoggedIn) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
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
                            if (code != uiState.settings.primaryLanguageCode) {
                                selected = code
                                pendingLanguageCode = code
                            }
                        }
                    )
                }
            }

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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(AppCorners.medium)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                    ) {

                        // Profile and Shop buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
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
                                onClick = onOpenShop,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = t(UiTextKey.ShopEntry),
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

                        // Feedback buttons - Two separate options
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                        ) {
                            // Tester Feedback (Firebase App Distribution — debug builds only)
                            TextButton(
                                onClick = {
                                    try {
                                        // Use reflection so this compiles in release builds where
                                        // the firebase-appdistribution SDK is not included.
                                        val cls = Class.forName("com.google.firebase.appdistribution.FirebaseAppDistribution")
                                        val getInstance = cls.getMethod("getInstance")
                                        val instance = getInstance.invoke(null)
                                        val startFeedback = cls.getMethod("startFeedback", Int::class.javaPrimitiveType)
                                        startFeedback.invoke(instance, R.string.feedback_additional_info)
                                    } catch (e: Exception) {
                                        android.util.Log.e("SettingsScreen", "Firebase App Distribution not available: ${e.message}")
                                        // Silently fail - this feature is only available in debug/tester builds
                                    }
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
                    }
                }
            }


            if (isLoggedIn) {
                // Error card for actual errors (suppress the generic "not logged in" warning key)
                val actualError = if (uiState.errorKey == UiTextKey.SettingsNotLoggedInWarning) null
                                  else settingsErrorText
                actualError?.let { errorMsg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppCorners.medium)
                    ) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(AppSpacing.large)
                        )
                    }
                }
            } else {
                // Sign-in prompt
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(AppCorners.medium)
                ) {
                    Text(
                        text = t(UiTextKey.SettingsNotLoggedInWarning),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(AppSpacing.large)
                    )
                }
            }

            // Font size — only for logged-in users
            if (isLoggedIn) Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
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
                            .padding(AppSpacing.medium),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
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
                        .padding(horizontal = AppSpacing.small),
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

            // Voice settings — only for logged-in users (moved from separate screen)
            if (isLoggedIn) Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                Text(
                    t(UiTextKey.VoiceSettingsTitle),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = t(UiTextKey.VoiceSettingsDesc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                VoiceSettingsSelector(
                    voiceSettings = uiState.settings.voiceSettings,
                    supportedLanguages = supportedVoiceLanguages,
                    onVoiceSelected = { languageCode, voice ->
                        viewModel.updateVoiceForLanguage(languageCode, voice)
                    },
                    languageNameFor = uiLanguageNameFor,
                    t = t
                )
            }

            // Theme — only for logged-in users
            if (isLoggedIn) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
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
                                .padding(AppSpacing.medium),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                    // Row 1: System & Scheduled
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
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
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
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
                    modifier = Modifier.padding(start = AppSpacing.extraSmall)
                )
            }
            } // end if (isLoggedIn) for Theme

            // About
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
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

            }
        }
    }
}
