package com.example.fyp.screens.speech

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.AudioRecorder
import com.example.fyp.core.RecordAudioPermissionRequest
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.AuthState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SpeechRecognitionScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: SpeechViewModel = hiltViewModel()

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val recognizedText = viewModel.recognizedText
    val translatedText = viewModel.translatedText

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(translatedText) {
        // Only scroll when translation becomes available
        if (translatedText.isNotBlank()) {
            // small delay helps after recomposition/layout
            delay(150)
            bringIntoViewRequester.bringIntoView()
        }
    }

    val ttsStatus = viewModel.ttsStatus
    val statusMessage = viewModel.statusMessage
    val recognizePhase = viewModel.recognizePhase
    val isTtsRunning = viewModel.isTtsRunning

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val supportedLanguages by remember {
        mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context))
    }

    var selectedLanguage by remember {
        mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US")
    }

    var selectedTargetLanguage by remember {
        mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK")
    }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn = authState is AuthState.LoggedIn

    // Info dialog state
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(t(UiTextKey.SpeechTitle)) },
            text = { Text(t(UiTextKey.SpeechInstructions)) },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(t(UiTextKey.ActionConfirm))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.SpeechTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            IconButton(onClick = { showInfoDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Instructions"
                )
            }
        }
    ) { innerPadding ->
        RecordAudioPermissionRequest {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Scrollable main content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppLanguageDropdown(
                        uiLanguages = uiLanguages,
                        appLanguageState = appLanguageState,
                        onUpdateAppLanguage = onUpdateAppLanguage,
                        uiText = uiText,
                        enabled = isLoggedIn,
                        isLoggedIn = isLoggedIn
                    )

                    SpeechLanguagePickers(
                        detectLabel = t(UiTextKey.DetectLanguageLabel),
                        translateToLabel = t(UiTextKey.TranslateToLabel),
                        selectedLanguage = selectedLanguage,
                        selectedTargetLanguage = selectedTargetLanguage,
                        supportedLanguages = supportedLanguages,
                        languageNameFor = uiLanguageNameFor,
                        onSelectedLanguage = { selectedLanguage = it },
                        onSelectedTargetLanguage = { selectedTargetLanguage = it },
                        onSwapLanguages = {
                            val tmp = selectedLanguage
                            selectedLanguage = selectedTargetLanguage
                            selectedTargetLanguage = tmp
                        },
                    )

                    val isRecognizing = recognizePhase != RecognizePhase.Idle

                    RecognizeButton(
                        recognizePhase = recognizePhase,
                        idleLabel = t(UiTextKey.AzureRecognizeButton),
                        preparingLabel = t(UiTextKey.StatusRecognizePreparing),
                        listeningLabel = t(UiTextKey.StatusRecognizeListening),
                        onClick = { viewModel.recognize(selectedLanguage) },
                        enabled = !isRecognizing && !AudioRecorder.isRecording,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    SourceTextEditor(
                        value = recognizedText,
                        onValueChange = viewModel::updateSourceText,
                        placeholder = uiText(UiTextKey.SpeechInputPlaceholder, BaseUiTexts[UiTextKey.SpeechInputPlaceholder.ordinal]),
                        copyLabel = t(UiTextKey.CopyButton),
                        speakLabel = t(UiTextKey.SpeakScriptButton),
                        isTtsRunning = isTtsRunning,
                        enableCopy = recognizedText.isNotBlank(),
                        enableSpeak = recognizedText.isNotBlank() && !isTtsRunning,
                        onCopy = { clipboardManager.setText(AnnotatedString(recognizedText)) },
                        onSpeak = { viewModel.speakOriginal(selectedLanguage) },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TranslateButton(
                        label = t(UiTextKey.TranslateButton),
                        enabled = isLoggedIn && recognizedText.isNotBlank(),
                        onClick = { viewModel.translate(fromLanguage = selectedLanguage, toLanguage = selectedTargetLanguage) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Column(
                        modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
                    ) {
                        TranslatedResultBox(
                            text = translatedText,
                            placeholder = t(UiTextKey.SpeechTranslatedPlaceholder),
                            modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester),
                        )

                        TranslationActionsRow(
                            copyLabel = t(UiTextKey.CopyTranslationButton),
                            speakLabel = t(UiTextKey.SpeakTranslationButton),
                            isTtsRunning = isTtsRunning,
                            enableCopy = translatedText.isNotBlank(),
                            enableSpeak = isLoggedIn && translatedText.isNotBlank() && !isTtsRunning,
                            onCopy = { clipboardManager.setText(AnnotatedString(translatedText)) },
                            onSpeak = { viewModel.speakTranslation(selectedTargetLanguage) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                BottomStatusText(
                    statusMessage = statusMessage,
                    ttsStatus = ttsStatus,
                )
            }
        }
    }
}