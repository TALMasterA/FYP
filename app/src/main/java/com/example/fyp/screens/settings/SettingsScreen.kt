package com.example.fyp.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import com.example.fyp.BuildConfig
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.core.validateScale
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import androidx.compose.material3.TextButton
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
    onOpenShop: () -> Unit = {},
    onOpenVoiceSettings: () -> Unit = {},
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

    // Auto-dismiss error after 3 seconds
    val settingsErrorText = uiState.errorKey?.let { t(it) } ?: uiState.errorRaw
    LaunchedEffect(settingsErrorText) {
        if (settingsErrorText != null) {
            delay(3000)
            viewModel.clearError()
        }
    }

    // Auto-dismiss unlock error after 3 seconds
    LaunchedEffect(uiState.unlockError) {
        if (uiState.unlockError != null) {
            delay(3000)
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

            TextButton(onClick = onOpenResetPassword) {
                Text(t(UiTextKey.SettingsResetPW))
            }

            // Quick Links for logged in users
            if (isLoggedIn) {
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
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextButton(
                                onClick = onOpenProfile,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(t(UiTextKey.ProfileTitle))
                            }
                            TextButton(
                                onClick = onOpenFavorites,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(t(UiTextKey.FavoritesTitle))
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
                                Text(t(UiTextKey.ShopEntry))
                            }
                            TextButton(
                                onClick = onOpenVoiceSettings,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(t(UiTextKey.VoiceSettingsTitle))
                            }
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateThemeMode("system") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentMode == "system")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = t(UiTextKey.SettingsThemeSystem),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateThemeMode("light") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentMode == "light")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = t(UiTextKey.SettingsThemeLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateThemeMode("dark") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentMode == "dark")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = t(UiTextKey.SettingsThemeDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // About
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    t(UiTextKey.SettingsAboutTitle),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "${t(UiTextKey.SettingsAppVersion)}${BuildConfig.VERSION_NAME}",
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