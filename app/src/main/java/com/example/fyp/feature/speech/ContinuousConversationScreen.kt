package com.example.fyp.feature.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fyp.model.AppLanguageState
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

    // conversation languages
    var fromLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var toLanguage by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK") }

    // true = person A speaking (fromLanguage), false = person B speaking (toLanguage)
    var isPersonATalking by remember { mutableStateOf(true) }

    val isRunning = viewModel.isContinuousRunning
    val partial = viewModel.livePartialText
    val lastTranslation = viewModel.lastSegmentTranslation
    val messages = viewModel.continuousMessages

    val listState = rememberLazyListState()

    // auto‑restart when speaker switches
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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.endContinuousSession()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        RecordAudioPermissionRequest {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                ConversationControls(
                    innerPadding = innerPadding,
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText,
                    uiLanguageNameFor = uiLanguageNameFor,
                    supportedLanguages = supportedLanguages,
                    fromLanguage = fromLanguage,
                    toLanguage = toLanguage,
                    isPersonATalking = isPersonATalking,
                    isRunning = isRunning,
                    partial = partial,
                    lastTranslation = lastTranslation,
                    onFromLanguageChange = { fromLanguage = it },
                    onToLanguageChange = { toLanguage = it },
                    onPersonToggle = { isPersonATalking = it },
                    onStartStop = { start ->
                        if (start) {
                            val speakLang =
                                if (isPersonATalking) fromLanguage else toLanguage
                            val otherLang =
                                if (isPersonATalking) toLanguage else fromLanguage
                            viewModel.startContinuous(
                                speakingLang = speakLang,
                                targetLang = otherLang,
                                isFromPersonA = isPersonATalking
                            )
                        } else {
                            viewModel.endContinuousSession()
                        }
                    }
                )

                ConversationMessageList(
                    messages = messages,
                    listState = listState,
                    uiText = uiText,
                    onSpeakMessage = { msg ->
                        viewModel.speakText(languageCode = msg.lang, text = msg.text)
                    }
                )
            }
        }
    }
}

@Composable
private fun ConversationControls(
    innerPadding: PaddingValues,
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    uiText: (UiTextKey, String) -> String,
    uiLanguageNameFor: (String) -> String,
    supportedLanguages: List<String>,
    fromLanguage: String,
    toLanguage: String,
    isPersonATalking: Boolean,
    isRunning: Boolean,
    partial: String,
    lastTranslation: String,
    onFromLanguageChange: (String) -> Unit,
    onToLanguageChange: (String) -> Unit,
    onPersonToggle: (Boolean) -> Unit,
    onStartStop: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
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
            onSelected = onFromLanguageChange
        )

        LanguageDropdownField(
            label = uiText(
                UiTextKey.ContinuousSpeakerBName,
                BaseUiTexts[UiTextKey.ContinuousSpeakerBName.ordinal]
            ),
            selectedCode = toLanguage,
            options = supportedLanguages,
            nameFor = uiLanguageNameFor,
            onSelected = onToLanguageChange
        )

        SpeakerToggle(
            isPersonATalking = isPersonATalking,
            uiText = uiText,
            onToggle = onPersonToggle
        )

        Button(
            onClick = { onStartStop(!isRunning) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isRunning)
                    uiText(
                        UiTextKey.ContinuousStopButton,
                        BaseUiTexts[UiTextKey.ContinuousStopButton.ordinal]
                    )
                else
                    uiText(
                        UiTextKey.ContinuousStartButton,
                        BaseUiTexts[UiTextKey.ContinuousStartButton.ordinal]
                    )
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = uiText(
                UiTextKey.ContinuousCurrentStringLabel,
                BaseUiTexts[UiTextKey.ContinuousCurrentStringLabel.ordinal]
            ) + ": " + partial
        )
        if (lastTranslation.isNotBlank()) {
            Text("Last translation: $lastTranslation")
        }
    }
}

@Composable
private fun SpeakerToggle(
    isPersonATalking: Boolean,
    uiText: (UiTextKey, String) -> String,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isPersonATalking)
                uiText(
                    UiTextKey.ContinuousPersonALabel,
                    BaseUiTexts[UiTextKey.ContinuousPersonALabel.ordinal]
                )
            else
                uiText(
                    UiTextKey.ContinuousPersonBLabel,
                    BaseUiTexts[UiTextKey.ContinuousPersonBLabel.ordinal]
                )
        )

        Switch(
            checked = isPersonATalking,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun ConversationMessageList(
    messages: List<SpeechViewModel.ChatMessage>,
    listState: LazyListState,
    uiText: (UiTextKey, String) -> String,
    onSpeakMessage: (SpeechViewModel.ChatMessage) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        state = listState
    ) {
        items(messages, key = { it.id }) { msg ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = if (msg.isFromPersonA) {
                    Arrangement.End       // Person A on the right
                } else {
                    Arrangement.Start      // Person B on the left
                }
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    // speaker + type
                    Text(
                        text = buildString {
                            val speakerName = if (msg.isFromPersonA) {
                                uiText(
                                    UiTextKey.ContinuousSpeakerAName,
                                    BaseUiTexts[UiTextKey.ContinuousSpeakerAName.ordinal]
                                )
                            } else {
                                uiText(
                                    UiTextKey.ContinuousSpeakerBName,
                                    BaseUiTexts[UiTextKey.ContinuousSpeakerBName.ordinal]
                                )
                            }
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

                    // message text
                    Text(
                        text = msg.text,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // speak button
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onSpeakMessage(msg) }
                        ) {
                            Text("🔊")
                        }
                    }
                }
            }
        }
    }
}