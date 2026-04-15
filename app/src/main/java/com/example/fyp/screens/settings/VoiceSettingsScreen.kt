package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val supportedLanguages = remember {
        val voiceSupportedSet = AzureVoiceConfig.getSupportedLanguages()
        AzureLanguageConfig.loadSupportedLanguages(context)
            .filter { it in voiceSupportedSet }
    }

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(t(UiTextKey.VoiceSettingsTitle)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(t(UiTextKey.VoiceSettingsDesc))
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
        title = t(UiTextKey.VoiceSettingsTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            IconButton(onClick = { showInfoDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = t(UiTextKey.VoiceSettingsTitle),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Per-language voice selection (report §4.5.1)
            VoiceSettingsSelector(
                voiceSettings = uiState.settings.voiceSettings,
                supportedLanguages = supportedLanguages,
                onVoiceSelected = { languageCode, voice ->
                    viewModel.updateVoiceForLanguage(languageCode, voice)
                },
                languageNameFor = uiLanguageNameFor,
                t = t
            )
        }
    }
}

