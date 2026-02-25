package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.data.azure.AzureVoiceConfig
import com.example.fyp.model.ui.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsSelector(
    voiceSettings: Map<String, String>,
    supportedLanguages: List<String>,
    onVoiceSelected: (languageCode: String, voiceName: String) -> Unit,
    languageNameFor: (String) -> String,
    t: (UiTextKey) -> String
) {
    var selectedLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var languageDropdownExpanded by remember { mutableStateOf(false) }
    var voiceDropdownExpanded by remember { mutableStateOf(false) }

    val voicesForLanguage = remember(selectedLanguage) {
        AzureVoiceConfig.getVoicesForLanguage(selectedLanguage)
    }

    val currentVoiceName = voiceSettings[selectedLanguage]
    val currentVoice = remember(selectedLanguage, currentVoiceName) {
        AzureVoiceConfig.getVoiceOrDefault(selectedLanguage, currentVoiceName)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Language selector
        ExposedDropdownMenuBox(
            expanded = languageDropdownExpanded,
            onExpandedChange = { languageDropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = languageNameFor(selectedLanguage),
                onValueChange = {},
                readOnly = true,
                label = { Text(t(UiTextKey.SettingsVoiceLanguageLabel)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )

            ExposedDropdownMenu(
                expanded = languageDropdownExpanded,
                onDismissRequest = { languageDropdownExpanded = false }
            ) {
                supportedLanguages.forEach { code ->
                    DropdownMenuItem(
                        text = { Text(languageNameFor(code)) },
                        onClick = {
                            selectedLanguage = code
                            languageDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Voice selector for the selected language
        if (voicesForLanguage.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = voiceDropdownExpanded,
                onExpandedChange = { voiceDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = currentVoice?.displayName ?: t(UiTextKey.SettingsVoiceDefault),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(t(UiTextKey.SettingsVoiceSelectLabel)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = voiceDropdownExpanded,
                    onDismissRequest = { voiceDropdownExpanded = false }
                ) {
                    voicesForLanguage.forEach { voice ->
                        DropdownMenuItem(
                            text = { Text(voice.displayName) },
                            onClick = {
                                onVoiceSelected(selectedLanguage, voice.name)
                                voiceDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        } else {
            Text(
                text = t(UiTextKey.VoiceSettingsNoOptions),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
