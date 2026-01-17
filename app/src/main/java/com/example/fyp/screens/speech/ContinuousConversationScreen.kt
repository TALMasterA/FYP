@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.fyp.screens.speech

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.RecordAudioPermissionRequest
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.AzureLanguageConfig
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
    val supportedLanguages by remember {
        mutableStateOf(AzureLanguageConfig.loadSupportedLanguages(context))
    }

    var fromLanguage by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var toLanguage by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-HK") }
    var isPersonATalking by remember { mutableStateOf(true) }

    val isRunning = viewModel.isContinuousRunning
    val isPreparing = viewModel.isContinuousPreparing
    val isProcessing = viewModel.isContinuousProcessing
    val partial = viewModel.livePartialText
    val messages = viewModel.continuousMessages

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn = authState is AuthState.LoggedIn

    // Restart recognizer on person switch while running (but never during preparing/processing)
    LaunchedEffect(isPersonATalking, isLoggedIn) {
        if (isRunning && isLoggedIn && !isPreparing && !isProcessing) {
            viewModel.stopContinuous()
            val speakLang = if (isPersonATalking) fromLanguage else toLanguage
            val otherLang = if (isPersonATalking) toLanguage else fromLanguage
            viewModel.startContinuous(
                speakingLang = speakLang,
                targetLang = otherLang,
                isFromPersonA = isPersonATalking,
                resetSession = false,
            )
        }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(outerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText,
                    enabled = isLoggedIn,
                )

                Text(text = t(UiTextKey.ContinuousInstructions), style = MaterialTheme.typography.bodyMedium)

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isPersonATalking) {
                            t(UiTextKey.ContinuousPersonALabel)
                        } else {
                            t(UiTextKey.ContinuousPersonBLabel)
                        },
                    )

                    Switch(
                        checked = isPersonATalking,
                        onCheckedChange = { isPersonATalking = it },
                        enabled = isLoggedIn && !isPreparing && !isProcessing,
                    )
                }

                val buttonIsGrey = isPreparing || isProcessing
                val buttonColors =
                    if (buttonIsGrey) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    else ButtonDefaults.buttonColors()

                val buttonText = when {
                    isPreparing -> uiText(UiTextKey.ContinuousPreparingMicText, BaseUiTexts[UiTextKey.ContinuousPreparingMicText.ordinal])
                    isProcessing && isRunning -> t(UiTextKey.ContinuousStopButton) + " (" +
                            uiText(UiTextKey.ContinuousTranslatingText, BaseUiTexts[UiTextKey.ContinuousTranslatingText.ordinal]) + ")"
                    isProcessing -> uiText(UiTextKey.ContinuousTranslatingText, BaseUiTexts[UiTextKey.ContinuousTranslatingText.ordinal])
                    isRunning -> t(UiTextKey.ContinuousStopButton)
                    else -> t(UiTextKey.ContinuousStartButton)
                }

                Button(
                    onClick = {
                        if (isRunning) {
                            viewModel.stopContinuous()
                        } else {
                            val speakLang = if (isPersonATalking) fromLanguage else toLanguage
                            val otherLang = if (isPersonATalking) toLanguage else fromLanguage
                            viewModel.startContinuous(
                                speakingLang = speakLang,
                                targetLang = otherLang,
                                isFromPersonA = isPersonATalking,
                                resetSession = true,
                            )
                        }
                    },
                    // Stop must always be allowed; Start must be blocked while preparing/processing
                    enabled = isLoggedIn && (isRunning || (!isPreparing && !isProcessing)),
                    modifier = Modifier.fillMaxWidth(),
                    colors = buttonColors,
                ) {
                    Text(buttonText)
                }

                Text(
                    text = t(UiTextKey.ContinuousCurrentStringLabel) + ": " + partial,
                    style = MaterialTheme.typography.bodySmall,
                )

                Spacer(Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isFromA = msg.isFromPersonA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isFromA) Arrangement.End else Arrangement.Start,
                        ) {
                            OutlinedCard(modifier = Modifier.fillMaxWidth(0.92f)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    val speakerName = if (isFromA) {
                                        t(UiTextKey.ContinuousSpeakerAName)
                                    } else {
                                        t(UiTextKey.ContinuousSpeakerBName)
                                    }

                                    val label = if (msg.isTranslation) {
                                        speakerName + t(UiTextKey.ContinuousTranslationSuffix)
                                    } else {
                                        speakerName
                                    }

                                    Text(text = label, style = MaterialTheme.typography.labelSmall)
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = msg.text)
                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                    ) {
                                        Button(
                                            onClick = { viewModel.speakText(languageCode = msg.lang, text = msg.text) },
                                        ) {
                                            Text("🔊")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}