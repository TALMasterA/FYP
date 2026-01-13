package com.example.fyp.feature.speech

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.RecordAudioPermissionRequest
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.model.AuthState

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
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn = authState is AuthState.LoggedIn

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    // Switch speaker while running: stop and restart recognizer, keep session/messages.
    LaunchedEffect(isPersonATalking, isLoggedIn) {
        if (isRunning && isLoggedIn) {
            viewModel.stopContinuous()
            val speakLang = if (isPersonATalking) fromLanguage else toLanguage
            val otherLang = if (isPersonATalking) toLanguage else fromLanguage
            viewModel.startContinuous(
                speakingLang = speakLang,
                targetLang = otherLang,
                isFromPersonA = isPersonATalking,
                resetSession = false
            )
        }
    }

    // Leaving screen: end session and clear messages.
    DisposableEffect(Unit) {
        onDispose { viewModel.endContinuousSession() }
    }

    StandardScreenScaffold(
        title = t(UiTextKey.ContinuousTitle),
        onBack = {
            viewModel.endContinuousSession()
            onBack()
        },
        backContentDescription = t(UiTextKey.NavBack)
    ) { outerPadding ->

        // Put everything inside the same padded area so measurements match maxHeight.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding)
        ) {
            val density = LocalDensity.current
            val pagePadding = 16.dp // matches your app's StandardScreenBody rule. [file:107]
            val gap = 12.dp

            var controlsHeightPx by remember { mutableIntStateOf(0) }
            val controlsHeightDp = with(density) { controlsHeightPx.toDp() }
            val controlsBottomDp = pagePadding + controlsHeightDp

            // Sheet height so its top starts right below Person B dropdown (+gap).
            val peek = if (controlsHeightPx == 0) {
                // Avoid first-frame overlap/jump before measurement is available.
                0.dp
            } else {
                (maxHeight - (controlsBottomDp + gap)).coerceAtLeast(0.dp)
            }

            BottomSheetScaffold(
                topBar = {}, // top bar is handled by StandardScreenScaffold
                sheetPeekHeight = peek,
                sheetTonalElevation = 2.dp,
                sheetDragHandle = {
                    // Custom drag handle (no dependency on BottomSheetDefaults/SheetDefaults).
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(width = 44.dp, height = 5.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(100)
                            )
                    )
                },
                sheetContent = {
                    ConversationSheet(
                        uiText = uiText,
                        isLoggedIn = isLoggedIn,
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
                                viewModel.stopContinuous()
                            }
                        },
                        messages = messages,
                        listState = listState,
                        onSpeakMessage = { msg ->
                            viewModel.speakText(languageCode = msg.lang, text = msg.text)
                        }
                    )
                }
            ) {
                RecordAudioPermissionRequest {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pagePadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Measure the whole controls block (App UI + instructions + A + B).
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coords ->
                                    controlsHeightPx = coords.size.height
                                },
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppLanguageDropdown(
                                uiLanguages = uiLanguages,
                                appLanguageState = appLanguageState,
                                onUpdateAppLanguage = onUpdateAppLanguage,
                                uiText = uiText,
                                enabled = isLoggedIn
                            )

                            Text(
                                text = t(UiTextKey.ContinuousInstructions),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            LanguageDropdownField(
                                label = t(UiTextKey.ContinuousSpeakerAName),
                                selectedCode = fromLanguage,
                                options = supportedLanguages,
                                nameFor = uiLanguageNameFor,
                                onSelected = { fromLanguage = it }
                            )

                            LanguageDropdownField(
                                label = t(UiTextKey.ContinuousSpeakerBName),
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
    }
}

@Composable
private fun ConversationSheet(
    uiText: (UiTextKey, String) -> String,
    isLoggedIn: Boolean,
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
            enabled = isLoggedIn,
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
                                            uiText(
                                                UiTextKey.ContinuousSpeakerAName,
                                                BaseUiTexts[UiTextKey.ContinuousSpeakerAName.ordinal]
                                            )
                                        else
                                            uiText(
                                                UiTextKey.ContinuousSpeakerBName,
                                                BaseUiTexts[UiTextKey.ContinuousSpeakerBName.ordinal]
                                            )

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