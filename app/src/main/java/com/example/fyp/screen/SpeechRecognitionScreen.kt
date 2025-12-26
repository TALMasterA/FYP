package com.example.fyp.screen

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
import com.example.fyp.SpeechViewModel
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechRecognitionScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SpeechViewModel = viewModel()

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
                        UiTextKey.Instructions,
                        "Select User Interface language on top, then the detect and translate languages. " +
                                "Support languages: English, Cantonese, Japanese, Mandarin..."
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LanguageDropdownField(
                    label = uiText(UiTextKey.DetectLanguageLabel, "Detect language"),
                    selectedCode = selectedLanguage,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { selectedLanguage = it }
                )

                LanguageDropdownField(
                    label = uiText(UiTextKey.TranslateToLabel, "Translate to"),
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
                            "Use Azure Recognize (from Mic)"
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
                    Text(uiText(UiTextKey.CopyButton, "Copy"))
                }

                Button(
                    onClick = { viewModel.speakOriginal(selectedLanguage) },
                    enabled = recognizedText.isNotBlank() && !isTtsRunning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isTtsRunning)
                            uiText(UiTextKey.SpeakingLabel, "Speaking...")
                        else
                            uiText(UiTextKey.SpeakScriptButton, "Speak script")
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
                    Text(uiText(UiTextKey.TranslateButton, "Translate"))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = translatedText, modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(translatedText))
                    },
                    enabled = translatedText.isNotEmpty()
                ) {
                    Text(uiText(UiTextKey.CopyTranslationButton, "Copy Translation"))
                }

                Button(
                    onClick = { viewModel.speakTranslation(selectedTargetLanguage) },
                    enabled = translatedText.isNotBlank() && !isTtsRunning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isTtsRunning)
                            uiText(UiTextKey.SpeakingLabel, "Speaking...")
                        else
                            uiText(UiTextKey.SpeakTranslationButton, "Speak Translation")
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