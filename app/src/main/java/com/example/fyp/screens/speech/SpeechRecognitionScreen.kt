package com.example.fyp.screens.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.AuthState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechRecognitionScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SpeechViewModel = hiltViewModel()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val recognizedText = viewModel.recognizedText
    val translatedText = viewModel.translatedText
    val ttsStatus = viewModel.ttsStatus
    val isTtsRunning = viewModel.isTtsRunning

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val supportedLanguages by remember { mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context)) }

    var selectedLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var selectedTargetLanguage by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK") }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn = authState is AuthState.LoggedIn

    StandardScreenScaffold(
        title = t(UiTextKey.SpeechTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        RecordAudioPermissionRequest {
            StandardScreenBody(
                innerPadding = innerPadding,
                scrollable = true,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText,
                    enabled = isLoggedIn
                )

                Text(text = t(UiTextKey.SpeechInstructions))

                SpeechLanguagePickers(
                    detectLabel = t(UiTextKey.DetectLanguageLabel),
                    translateToLabel = t(UiTextKey.TranslateToLabel),
                    selectedLanguage = selectedLanguage,
                    selectedTargetLanguage = selectedTargetLanguage,
                    supportedLanguages = supportedLanguages,
                    languageNameFor = uiLanguageNameFor,
                    onSelectedLanguage = { selectedLanguage = it },
                    onSelectedTargetLanguage = { selectedTargetLanguage = it }
                )

                Button(
                    onClick = { viewModel.recognize(selectedLanguage) },
                    enabled = !AudioRecorder.isRecording,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t(UiTextKey.AzureRecognizeButton))
                }

                // Step 1: typed text + mic share same source, and mic "replace" is done in VM.
                OutlinedTextField(
                    value = recognizedText,
                    onValueChange = { viewModel.updateSourceText(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type here or use microphone...") },
                    minLines = 3
                )

                TextActionsRow(
                    leftText = t(UiTextKey.CopyButton),
                    leftEnabled = recognizedText.isNotBlank(),
                    onLeft = { clipboardManager.setText(AnnotatedString(recognizedText)) },
                    rightText = if (isTtsRunning) t(UiTextKey.SpeakingLabel) else t(UiTextKey.SpeakScriptButton),
                    rightEnabled = recognizedText.isNotBlank() && !isTtsRunning,
                    onRight = { viewModel.speakOriginal(selectedLanguage) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.translate(
                            fromLanguage = selectedLanguage,
                            toLanguage = selectedTargetLanguage
                        )
                    },
                    enabled = isLoggedIn && recognizedText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t(UiTextKey.TranslateButton))
                }

                LabeledTextBlock(text = translatedText)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { clipboardManager.setText(AnnotatedString(translatedText)) },
                        enabled = translatedText.isNotBlank()
                    ) { Text(t(UiTextKey.CopyTranslationButton)) }

                    Button(
                        onClick = { viewModel.speakTranslation(selectedTargetLanguage) },
                        enabled = isLoggedIn && translatedText.isNotBlank() && !isTtsRunning
                    ) {
                        Text(
                            if (isTtsRunning) t(UiTextKey.SpeakingLabel)
                            else t(UiTextKey.SpeakTranslationButton)
                        )
                    }
                }

                if (ttsStatus.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(ttsStatus)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}