package com.example.fyp.screens.settings

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.validateScale
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context) }
    val languageNameMap = remember(uiLanguages) { uiLanguages.toMap() }

    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    fun displayName(code: String) = languageNameMap[code] ?: code

    var selected by remember(uiState.settings.primaryLanguageCode) {
        mutableStateOf(uiState.settings.primaryLanguageCode.ifBlank { "en-US" })
    }

    // Local slider state for live preview
    var sliderValue by remember { mutableStateOf(validateScale(uiState.settings.fontSizeScale)) }

    // Keep local slider in sync with actual saved value (Firestore / VM updates)
    LaunchedEffect(uiState.settings.fontSizeScale) {
        sliderValue = validateScale(uiState.settings.fontSizeScale)
    }

    StandardScreenScaffold(
        title = t(UiTextKey.SettingsPrimaryLanguageTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            uiState.error?.let { errorMsg ->
                Text(
                    text = "Error: $errorMsg",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // ==================== SECTION 1: PRIMARY LANGUAGE ====================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(t(UiTextKey.SettingsPrimaryLanguageLabel), style = MaterialTheme.typography.titleMedium)
                Text(
                    t(UiTextKey.SettingsFontSizeDesc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LanguageDropdownField(
                    label = t(UiTextKey.SettingsPrimaryLanguageLabel),
                    selectedCode = selected,
                    options = supportedLanguages,
                    nameFor = { code -> displayName(code) },
                    onSelected = {
                        selected = it
                        viewModel.updatePrimaryLanguage(it)
                    }
                )
            }

            // ==================== SECTION 2: FONT SIZE ====================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(t(UiTextKey.SettingsFontSizeTitle), style = MaterialTheme.typography.titleMedium)

                Text(
                    t(UiTextKey.SettingsFontSizeDesc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    t(UiTextKey.SettingsScaleTemplate)
                        .replace("{pct}", (sliderValue * 100).toInt().toString())
                )

                // Preview card (local preview)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            t(UiTextKey.SettingsPreviewHeadline),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize * sliderValue
                            )
                        )
                        Text(
                            t(UiTextKey.SettingsPreviewBody),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize * sliderValue
                            )
                        )
                        Text(
                            t(UiTextKey.SettingsPreviewLabel),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize * sliderValue
                            )
                        )
                    }
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { newScale ->
                        sliderValue = validateScale(newScale)
                    },
                    onValueChangeFinished = {
                        viewModel.updateFontSizeScale(sliderValue)
                    },
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
                    Text("80%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("100%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("120%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("150%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // ==================== SECTION 3: APP INFO ====================
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(t(UiTextKey.SettingsAboutTitle), style = MaterialTheme.typography.titleMedium)
                Text(t(UiTextKey.SettingsAppVersion), style = MaterialTheme.typography.bodyMedium)
                Text(
                    t(UiTextKey.SettingsSyncInfo) + {BuildConfig.VERSION_NAME},
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}