package com.example.fyp.feature.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.AudioRecorder
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.RecordAudioPermissionRequest
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import androidx.compose.foundation.layout.*

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

    val supportedLanguages by remember {
        mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context))
    }

    var selectedLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var selectedTargetLanguage by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK") }

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
                    uiText = uiText
                )

                Text(text = t(UiTextKey.SpeechInstructions))

                LanguageDropdownField(
                    label = t(UiTextKey.DetectLanguageLabel),
                    selectedCode = selectedLanguage,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { selectedLanguage = it }
                )

                LanguageDropdownField(
                    label = t(UiTextKey.TranslateToLabel),
                    selectedCode = selectedTargetLanguage,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { selectedTargetLanguage = it }
                )

                Button(
                    onClick = { viewModel.recognize(selectedLanguage) },
                    enabled = !AudioRecorder.isRecording,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t(UiTextKey.AzureRecognizeButton))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = recognizedText)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { clipboardManager.setText(AnnotatedString(recognizedText)) },
                        enabled = recognizedText.isNotBlank()
                    ) { Text(t(UiTextKey.CopyButton)) }

                    Button(
                        onClick = { viewModel.speakOriginal(selectedLanguage) },
                        enabled = recognizedText.isNotBlank() && !isTtsRunning
                    ) {
                        Text(
                            if (isTtsRunning) t(UiTextKey.SpeakingLabel)
                            else t(UiTextKey.SpeakScriptButton)
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.translate(
                            fromLanguage = selectedLanguage,
                            toLanguage = selectedTargetLanguage
                        )
                    },
                    enabled = recognizedText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t(UiTextKey.TranslateButton))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = translatedText)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { clipboardManager.setText(AnnotatedString(translatedText)) },
                        enabled = translatedText.isNotBlank()
                    ) { Text(t(UiTextKey.CopyTranslationButton)) }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.speakTranslation(selectedTargetLanguage) },
                        enabled = translatedText.isNotBlank() && !isTtsRunning
                    ) {
                        Text(
                            if (isTtsRunning) t(UiTextKey.SpeakingLabel)
                            else t(UiTextKey.SpeakTranslationButton)
                        )
                    }
                }

                if (ttsStatus.isNotBlank()) {
                    Text(ttsStatus)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}