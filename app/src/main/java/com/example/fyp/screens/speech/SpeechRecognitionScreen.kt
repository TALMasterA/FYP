package com.example.fyp.screens.speech

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.fyp.core.RequestCameraPermission
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.user.AuthState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import kotlinx.coroutines.delay
import com.example.fyp.core.UiConstants
import com.example.fyp.core.rememberHapticFeedback

/** Maximum number of candidate languages for auto-detection (Azure SDK limit) */
private const val MAX_AUTO_DETECT_LANGUAGES = 4

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
    val haptic = rememberHapticFeedback()

    val recognizedText = viewModel.recognizedText
    val translatedText = viewModel.translatedText

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(translatedText) {
        // Only scroll when translation becomes available
        if (translatedText.isNotBlank()) {
            // small delay helps after recomposition/layout
            delay(UiConstants.SPEECH_LISTENING_DEBOUNCE_MS.toLong())
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

    // Add "auto" option only for source language detection
    val sourceLanguageOptions = remember(supportedLanguages) {
        listOf("auto") + supportedLanguages
    }

    var selectedLanguage by remember {
        mutableStateOf("auto") // Default to auto-detect
    }

    var selectedTargetLanguage by remember {
        mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK")
    }

    // Track detected source language for history
    var detectedSourceLanguage by remember { mutableStateOf<String?>(null) }

    // Custom name function that handles "auto"
    val sourceLanguageNameFor: (String) -> String = { code ->
        when {
            code == "auto" -> t(UiTextKey.LanguageDetectAuto)
            else -> uiLanguageNameFor(code)
        }
    }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn = authState is AuthState.LoggedIn

    // Image capture state
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var requestCameraPermission by remember { mutableStateOf(false) }
    
    // Image picker launcher
    val launchImagePicker = rememberImagePickerLauncher { uri ->
        uri?.let { viewModel.recognizeTextFromImage(it, selectedLanguage) }
    }

    // Handle camera permission
    if (requestCameraPermission) {
        RequestCameraPermission(
            onPermissionGranted = {
                requestCameraPermission = false
                showCamera = true
            },
            onPermissionDenied = {
                requestCameraPermission = false
            }
        )
    }

    // Show image source selection dialog
    if (showImageSourceDialog) {
        ImageSourceDialog(
            onCamera = {
                requestCameraPermission = true
            },
            onGallery = {
                launchImagePicker()
            },
            onDismiss = {
                showImageSourceDialog = false
            },
            accuracyWarning = t(UiTextKey.ImageRecognitionAccuracyWarning),
            title = t(UiTextKey.ImageSourceTitle),
            cameraLabel = t(UiTextKey.ImageSourceCamera),
            galleryLabel = t(UiTextKey.ImageSourceGallery),
            cancelLabel = t(UiTextKey.ImageSourceCancel)
        )
    }

    // Show camera capture screen
    if (showCamera) {
        CameraCaptureScreen(
            onImageCaptured = { uri ->
                showCamera = false
                viewModel.recognizeTextFromImage(uri, selectedLanguage)
            },
            onError = { error ->
                showCamera = false
                // Error handling could be improved with a toast/snackbar
            },
            onCancel = {
                showCamera = false
            },
            captureContentDesc = t(UiTextKey.CameraCaptureContentDesc),
            cancelLabel = t(UiTextKey.ImageSourceCancel)
        )
        return // Don't show the regular UI when camera is active
    }

    // Info dialog state
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(t(UiTextKey.SpeechTitle)) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(t(UiTextKey.SpeechInstructions))
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
        title = t(UiTextKey.SpeechTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            // Language swap button (circle button with ⇄)
            IconButton(
                onClick = {
                    // Only swap if source is not auto
                    if (selectedLanguage != "auto") {
                        val tmp = selectedLanguage
                        selectedLanguage = selectedTargetLanguage
                        selectedTargetLanguage = tmp
                    }
                },
                enabled = selectedLanguage != "auto"
            ) {
                Text(
                    text = "⇄",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
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
                        sourceLanguageOptions = sourceLanguageOptions,
                        targetLanguageOptions = supportedLanguages,
                        sourceLanguageNameFor = sourceLanguageNameFor,
                        targetLanguageNameFor = uiLanguageNameFor,
                        onSelectedLanguage = {
                            selectedLanguage = it
                            if (it != "auto") detectedSourceLanguage = null
                        },
                        onSelectedTargetLanguage = { selectedTargetLanguage = it },
                    )

                    val isRecognizing = recognizePhase != RecognizePhase.Idle

                    // Default candidate languages for auto-detect (Azure supports max 4)
                    // Include the target language and common languages
                    val autoDetectCandidates = remember(selectedTargetLanguage, supportedLanguages) {
                        // Build candidates: target language + common languages (English, Chinese, Japanese, Spanish)
                        val commonLanguages = listOf("en-US", "zh-CN", "ja-JP", "ko-KR")
                        val candidates = mutableListOf<String>()

                        // Add languages that differ from target (to detect what user is speaking)
                        commonLanguages.forEach { lang ->
                            if (candidates.size < MAX_AUTO_DETECT_LANGUAGES && lang != selectedTargetLanguage) {
                                candidates.add(lang)
                            }
                        }

                        candidates.take(MAX_AUTO_DETECT_LANGUAGES)
                    }

                    RecognizeButton(
                        recognizePhase = recognizePhase,
                        idleLabel = t(UiTextKey.AzureRecognizeButton),
                        preparingLabel = t(UiTextKey.StatusRecognizePreparing),
                        listeningLabel = t(UiTextKey.StatusRecognizeListening),
                        onClick = {
                            haptic.click()
                            if (selectedLanguage == "auto") {
                                // Use auto-detect recognition
                                viewModel.recognizeWithAutoDetect(
                                    candidateLanguages = autoDetectCandidates,
                                    onDetectedLanguage = { detected ->
                                        detectedSourceLanguage = detected
                                    }
                                )
                            } else {
                                // Use specific language recognition
                                viewModel.recognize(selectedLanguage)
                            }
                        },
                        enabled = !isRecognizing && !AudioRecorder.isRecording,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Camera/Image button for OCR
                    OutlinedButton(
                        onClick = { showImageSourceDialog = true },
                        enabled = !isRecognizing,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = t(UiTextKey.ImageRecognitionButton)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t(UiTextKey.ImageRecognitionButton))
                    }


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
                        onSpeak = {
                            // Use detected language for speaking if auto was selected
                            val speakLang = if (selectedLanguage == "auto" && detectedSourceLanguage != null) {
                                detectedSourceLanguage!!
                            } else {
                                selectedLanguage
                            }
                            viewModel.speakOriginal(speakLang)
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TranslateButton(
                        label = t(UiTextKey.TranslateButton),
                        enabled = isLoggedIn && recognizedText.isNotBlank(),
                        onClick = {
                            haptic.click()
                            viewModel.translate(
                                fromLanguage = selectedLanguage,
                                toLanguage = selectedTargetLanguage,
                                onDetectedSourceLanguage = { detected ->
                                    detectedSourceLanguage = detected
                                }
                            )
                        },
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