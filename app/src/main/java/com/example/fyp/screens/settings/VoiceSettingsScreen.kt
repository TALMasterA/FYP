package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.data.azure.AzureVoiceConfig
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context) }

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    StandardScreenScaffold(
        title = t(UiTextKey.VoiceSettingsTitle),
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
            Text(
                text = t(UiTextKey.VoiceSettingsDesc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Full voice settings for each language
            supportedLanguages.forEach { languageCode ->
                VoiceSettingsCard(
                    languageCode = languageCode,
                    languageName = uiLanguageNameFor(languageCode),
                    currentVoice = uiState.settings.voiceSettings[languageCode],
                    onVoiceSelected = { voice -> viewModel.updateVoiceForLanguage(languageCode, voice) },
                    t = t
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceSettingsCard(
    languageCode: String,
    languageName: String,
    currentVoice: String?,
    onVoiceSelected: (String) -> Unit,
    t: (UiTextKey) -> String
) {
    val voiceOptions = remember(languageCode) {
        AzureVoiceConfig.getVoicesForLanguage(languageCode)
    }

    if (voiceOptions.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = languageName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            var expanded by remember { mutableStateOf(false) }
            val selectedVoice = currentVoice ?: voiceOptions.firstOrNull()?.name ?: ""
            val selectedDisplayName = voiceOptions.find { it.name == selectedVoice }?.displayName
                ?: t(UiTextKey.SettingsVoiceDefault)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDisplayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(t(UiTextKey.SettingsVoiceSelectLabel)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    voiceOptions.forEach { voice ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = voice.displayName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = voice.gender,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onVoiceSelected(voice.name)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

