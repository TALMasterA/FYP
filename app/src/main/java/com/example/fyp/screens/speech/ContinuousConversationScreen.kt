@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH", "AssignedValueIsNeverRead")

package com.example.fyp.screens.speech

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.RecordAudioPermissionRequest
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.AuthState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinuousConversationScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: SpeechViewModel = hiltViewModel()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val context = LocalContext.current
    val supportedLanguages by remember { mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context)) }

    var fromLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var toLanguage by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK") }
    var isPersonATalking by remember { mutableStateOf(true) }


    val isRunning = viewModel.isContinuousRunning
    val isPreparing = viewModel.isContinuousPreparing
    val isProcessing = viewModel.isContinuousProcessing
    val partial = viewModel.livePartialText
    val messages = viewModel.continuousMessages
    val listState = rememberLazyListState()

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn = authState is AuthState.LoggedIn

    ContinuousConversationEffects(
        messagesCount = messages.size,
        listState = listState,
        isPersonATalking = isPersonATalking,
        isLoggedIn = isLoggedIn,
        isRunning = isRunning,
        isPreparing = isPreparing,
        isProcessing = isProcessing,
        fromLanguage = fromLanguage,
        toLanguage = toLanguage,
        onRestartRecognizer = { speakingLang, targetLang, isFromPersonA ->
            viewModel.startContinuous(
                speakingLang = speakingLang,
                targetLang = targetLang,
                isFromPersonA = isFromPersonA,
                resetSession = false,
            )
        },
        onStopRecognizer = {
            viewModel.stopContinuous()
        }
    )

    DisposableEffect(Unit) {
        onDispose { viewModel.endContinuousSession() }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.ContinuousTitle),
        onBack = {
            viewModel.endContinuousSession()
            onBack()
        },
        backContentDescription = t(UiTextKey.NavBack),
    ) { outerPadding ->
        RecordAudioPermissionRequest {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(outerPadding),
            ) {
                val density = LocalDensity.current
                val pagePadding = 16.dp
                val gap = 12.dp

                // Measure the non-slide controls height so the sheet peek height = remaining space
                var controlsHeightPx by remember { mutableIntStateOf(0) }
                val controlsHeightDp = with(density) { controlsHeightPx.toDp() }
                val controlsBottomDp = pagePadding + controlsHeightDp
                val peek = if (controlsHeightPx == 0) 0.dp
                else (maxHeight - (controlsBottomDp + gap)).coerceAtLeast(0.dp)

                BottomSheetScaffold(
                    topBar = {},
                    sheetPeekHeight = peek,
                    sheetTonalElevation = 2.dp,
                    sheetDragHandle = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .size(width = 44.dp, height = 5.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(100),
                                    ),
                            )
                        }
                    },
                    sheetContent = {
                        ConversationSheet(
                            t = t,
                            uiText = uiText,
                            isLoggedIn = isLoggedIn,
                            isPersonATalking = isPersonATalking,
                            isRunning = isRunning,
                            isPreparing = isPreparing,
                            isProcessing = isProcessing,
                            isTtsRunning = viewModel.isTtsRunning,
                            ttsStatus = viewModel.ttsStatus,
                            partial = partial,
                            onPersonToggle = { isPersonATalking = it },
                            onStartStop = { start ->
                                if (start) {
                                    val speakLang = if (isPersonATalking) fromLanguage else toLanguage
                                    val otherLang = if (isPersonATalking) toLanguage else fromLanguage
                                    viewModel.startContinuous(
                                        speakingLang = speakLang,
                                        targetLang = otherLang,
                                        isFromPersonA = isPersonATalking,
                                        resetSession = true,
                                    )
                                } else {
                                    viewModel.stopContinuous()
                                }
                            },
                            messages = messages,
                            listState = listState,
                            onSpeakMessage = { msg ->
                                // Use original/translation TTS status correctly
                                if (msg.isTranslation) viewModel.speakText(languageCode = msg.lang, text = msg.text)
                                else viewModel.speakTextOriginal(languageCode = msg.lang, text = msg.text)
                            },
                        )
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pagePadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Controls area (non-slide)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coords -> controlsHeightPx = coords.size.height },
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

                            Text(
                                text = t(UiTextKey.ContinuousInstructions),
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    LanguageDropdownField(
                                        label = t(UiTextKey.ContinuousSpeakerAName),
                                        selectedCode = fromLanguage,
                                        options = supportedLanguages,
                                        nameFor = uiLanguageNameFor,
                                        onSelected = { fromLanguage = it },
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    LanguageDropdownField(
                                        label = t(UiTextKey.ContinuousSpeakerBName),
                                        selectedCode = toLanguage,
                                        options = supportedLanguages,
                                        nameFor = uiLanguageNameFor,
                                        onSelected = { toLanguage = it },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationSheet(
    t: (UiTextKey) -> String,
    uiText: (UiTextKey, String) -> String,
    isLoggedIn: Boolean,
    isPersonATalking: Boolean,
    isRunning: Boolean,
    isPreparing: Boolean,
    isProcessing: Boolean,
    isTtsRunning: Boolean,
    ttsStatus: String,
    partial: String,
    onPersonToggle: (Boolean) -> Unit,
    onStartStop: (Boolean) -> Unit,
    messages: List<ChatMessage>,
    listState: LazyListState,
    onSpeakMessage: (ChatMessage) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 240.dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ConversationHeaderRow(
            speakerLabel = if (isPersonATalking) t(UiTextKey.ContinuousPersonALabel) else t(UiTextKey.ContinuousPersonBLabel),
            isPersonATalking = isPersonATalking,
            isLoggedIn = isLoggedIn,
            isPreparing = isPreparing,
            isProcessing = isProcessing,
            onPersonToggle = onPersonToggle,
        )

        ContinuousStartStopButton(
            isLoggedIn = isLoggedIn,
            isRunning = isRunning,
            isPreparing = isPreparing,
            isProcessing = isProcessing,
            t = t,
            uiText = uiText,
            onStartStop = onStartStop,
            modifier = Modifier.fillMaxWidth(),
        )

        ContinuousStatusBlock(
            currentStringLabel = t(UiTextKey.ContinuousCurrentStringLabel),
            partial = partial,
            isTtsRunning = isTtsRunning,
            ttsStatus = ttsStatus,
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp),
        ) {
            items(messages, key = { it.id }) { msg ->
                ContinuousMessageBubble(
                    msg = msg,
                    speakerAName = t(UiTextKey.ContinuousSpeakerAName),
                    speakerBName = t(UiTextKey.ContinuousSpeakerBName),
                    translationSuffix = t(UiTextKey.ContinuousTranslationSuffix),
                    isTtsRunning = isTtsRunning,
                    onSpeakMessage = onSpeakMessage,
                )
            }
        }
    }
}