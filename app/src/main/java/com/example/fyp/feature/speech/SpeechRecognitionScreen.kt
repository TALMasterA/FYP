package com.example.fyp.feature.speech

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fyp.model.AppLanguageState
import com.example.fyp.core.AudioRecorder
import com.example.fyp.data.AzureLanguageConfig
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.core.RecordAudioPermissionRequest
import com.example.fyp.model.UiTextKey
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.rememberUiTextFunctions
import androidx.hilt.navigation.compose.hiltViewModel

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

    val recognizedText = viewModel.recognizedText
    val translatedText = viewModel.translatedText
    val ttsStatus = viewModel.ttsStatus
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiText(
                            UiTextKey.SpeechTitle,
                            BaseUiTexts[UiTextKey.SpeechTitle.ordinal]
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        RecordAudioPermissionRequest {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText
                )

                Text(
                    text = uiText(
                        UiTextKey.SpeechInstructions,
                        BaseUiTexts[UiTextKey.SpeechInstructions.ordinal]
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LanguageDropdownField(
                    label = uiText(UiTextKey.DetectLanguageLabel, BaseUiTexts[UiTextKey.DetectLanguageLabel.ordinal]),
                    selectedCode = selectedLanguage,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { selectedLanguage = it }
                )

                LanguageDropdownField(
                    label = uiText(UiTextKey.TranslateToLabel, BaseUiTexts[UiTextKey.TranslateToLabel.ordinal]),
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
                    Text(
                        uiText(
                            UiTextKey.AzureRecognizeButton,
                            BaseUiTexts[UiTextKey.AzureRecognizeButton.ordinal]
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = recognizedText, modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(recognizedText))
                    },
                    enabled = recognizedText.isNotEmpty()
                ) {
                    Text(uiText(UiTextKey.CopyButton, BaseUiTexts[UiTextKey.CopyButton.ordinal]))
                }

                Button(
                    onClick = { viewModel.speakOriginal(selectedLanguage) },
                    enabled = recognizedText.isNotBlank() && !isTtsRunning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isTtsRunning)
                            uiText(UiTextKey.SpeakingLabel, BaseUiTexts[UiTextKey.SpeakingLabel.ordinal])
                        else
                            uiText(UiTextKey.SpeakScriptButton, BaseUiTexts[UiTextKey.SpeakScriptButton.ordinal])
                    )
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
                    Text(uiText(UiTextKey.TranslateButton, BaseUiTexts[UiTextKey.TranslateButton.ordinal]))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = translatedText, modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(translatedText))
                    },
                    enabled = translatedText.isNotEmpty()
                ) {
                    Text(uiText(UiTextKey.CopyTranslationButton, BaseUiTexts[UiTextKey.CopyTranslationButton.ordinal]))
                }

                Button(
                    onClick = { viewModel.speakTranslation(selectedTargetLanguage) },
                    enabled = translatedText.isNotBlank() && !isTtsRunning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isTtsRunning)
                            uiText(UiTextKey.SpeakingLabel, BaseUiTexts[UiTextKey.SpeakingLabel.ordinal])
                        else
                            uiText(UiTextKey.SpeakTranslationButton, BaseUiTexts[UiTextKey.SpeakTranslationButton.ordinal])
                    )
                }

                if (ttsStatus.isNotBlank()) {
                    Text(ttsStatus)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}