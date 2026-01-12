package com.example.fyp.feature.speech

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.RecordAudioPermissionRequest
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinuousConversationScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SpeechViewModel = hiltViewModel()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)

    val context = LocalContext.current
    val supportedLanguages by remember {
        mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context))
    }

    var fromLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var toLanguage by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK") }
    var isPersonATalking by remember { mutableStateOf(true) }

    val isRunning = viewModel.isContinuousRunning
    val partial = viewModel.livePartialText
    val lastTranslation = viewModel.lastSegmentTranslation
    val messages = viewModel.continuousMessages
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    // Switch speaker while running: stop and restart recognizer, keep session/messages.
    LaunchedEffect(isPersonATalking) {
        if (isRunning) {
            viewModel.stopContinuous()
            val speakLang = if (isPersonATalking) fromLanguage else toLanguage
            val otherLang = if (isPersonATalking) toLanguage else fromLanguage
            viewModel.startContinuous(
                speakingLang = speakLang,
                targetLang = otherLang,
                isFromPersonA = isPersonATalking
            )
        }
    }

    // Leaving screen: end session and clear messages.
    DisposableEffect(Unit) {
        onDispose { viewModel.endContinuousSession() }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Stable peek height to avoid flashing/jumping.
        val estimatedControlsHeight = 380.dp
        val peek = (maxHeight - estimatedControlsHeight).coerceIn(180.dp, maxHeight * 0.85f)

        BottomSheetScaffold(
            sheetPeekHeight = peek,
            sheetTonalElevation = 2.dp,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            uiText(
                                UiTextKey.ContinuousTitle,
                                BaseUiTexts[UiTextKey.ContinuousTitle.ordinal]
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.endContinuousSession()
                            onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            sheetContent = {
                ConversationSheet(
                    uiText = uiText,
                    isPersonATalking = isPersonATalking,
                    isRunning = isRunning,
                    partial = partial,
                    lastTranslation = lastTranslation,
                    onPersonToggle = { isPersonATalking = it },
                    onStartStop = { start ->
                        if (start) {
                            val speakLang = if (isPersonATalking) fromLanguage else toLanguage
                            val otherLang = if (isPersonATalking) toLanguage else fromLanguage
                            viewModel.startContinuous(
                                speakingLang = speakLang,
                                targetLang = otherLang,
                                isFromPersonA = isPersonATalking,
                                resetSession = true
                            )
                        } else {
                            viewModel.stopContinuous() // keep messages for 🔊
                        }
                    },
                    messages = messages,
                    listState = listState,
                    onSpeakMessage = { msg ->
                        viewModel.speakText(languageCode = msg.lang, text = msg.text)
                    }
                )
            }
        ) { innerPadding ->
            RecordAudioPermissionRequest {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxWidth(),
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
                            UiTextKey.ContinuousInstructions,
                            BaseUiTexts[UiTextKey.ContinuousInstructions.ordinal]
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    LanguageDropdownField(
                        label = uiText(
                            UiTextKey.ContinuousSpeakerAName,
                            BaseUiTexts[UiTextKey.ContinuousSpeakerAName.ordinal]
                        ),
                        selectedCode = fromLanguage,
                        options = supportedLanguages,
                        nameFor = uiLanguageNameFor,
                        onSelected = { fromLanguage = it }
                    )

                    LanguageDropdownField(
                        label = uiText(
                            UiTextKey.ContinuousSpeakerBName,
                            BaseUiTexts[UiTextKey.ContinuousSpeakerBName.ordinal]
                        ),
                        selectedCode = toLanguage,
                        options = supportedLanguages,
                        nameFor = uiLanguageNameFor,
                        onSelected = { toLanguage = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationSheet(
    uiText: (UiTextKey, String) -> String,
    isPersonATalking: Boolean,
    isRunning: Boolean,
    partial: String,
    lastTranslation: String,
    onPersonToggle: (Boolean) -> Unit,
    onStartStop: (Boolean) -> Unit,
    messages: List<SpeechViewModel.ChatMessage>,
    listState: LazyListState,
    onSpeakMessage: (SpeechViewModel.ChatMessage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 240.dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersonATalking)
                    uiText(UiTextKey.ContinuousPersonALabel, BaseUiTexts[UiTextKey.ContinuousPersonALabel.ordinal])
                else
                    uiText(UiTextKey.ContinuousPersonBLabel, BaseUiTexts[UiTextKey.ContinuousPersonBLabel.ordinal])
            )
            Switch(checked = isPersonATalking, onCheckedChange = onPersonToggle)
        }

        Button(
            onClick = { onStartStop(!isRunning) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isRunning)
                    uiText(UiTextKey.ContinuousStopButton, BaseUiTexts[UiTextKey.ContinuousStopButton.ordinal])
                else
                    uiText(UiTextKey.ContinuousStartButton, BaseUiTexts[UiTextKey.ContinuousStartButton.ordinal])
            )
        }

        Text(
            text = uiText(
                UiTextKey.ContinuousCurrentStringLabel,
                BaseUiTexts[UiTextKey.ContinuousCurrentStringLabel.ordinal]
            ) + ": " + partial,
            style = MaterialTheme.typography.bodySmall
        )

        if (lastTranslation.isNotBlank()) {
            Text(text = "Last translation: $lastTranslation", style = MaterialTheme.typography.bodySmall)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isFromPersonA) Arrangement.End else Arrangement.Start
                ) {
                    OutlinedCard(modifier = Modifier.fillMaxWidth(0.92f)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = buildString {
                                    val speakerName =
                                        if (msg.isFromPersonA)
                                            uiText(UiTextKey.ContinuousSpeakerAName, BaseUiTexts[UiTextKey.ContinuousSpeakerAName.ordinal])
                                        else
                                            uiText(UiTextKey.ContinuousSpeakerBName, BaseUiTexts[UiTextKey.ContinuousSpeakerBName.ordinal])

                                    append(speakerName)
                                    if (msg.isTranslation) {
                                        append(
                                            uiText(
                                                UiTextKey.ContinuousTranslationSuffix,
                                                BaseUiTexts[UiTextKey.ContinuousTranslationSuffix.ordinal]
                                            )
                                        )
                                    }
                                },
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(Modifier.height(4.dp))
                            Text(text = msg.text)

                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = { onSpeakMessage(msg) }) { Text("🔊") }
                            }
                        }
                    }
                }
            }
        }
    }
}