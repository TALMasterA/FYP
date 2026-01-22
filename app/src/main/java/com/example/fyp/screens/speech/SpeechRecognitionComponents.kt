package com.example.fyp.screens.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.core.LanguageDropdownField
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard

@Composable
fun SpeechLanguagePickers(
    detectLabel: String,
    translateToLabel: String,
    selectedLanguage: String,
    selectedTargetLanguage: String,
    supportedLanguages: List<String>,
    languageNameFor: (String) -> String,
    onSelectedLanguage: (String) -> Unit,
    onSelectedTargetLanguage: (String) -> Unit,
    onSwapLanguages: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSwapLanguages,
                modifier = Modifier.size(28.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text("⇄", style = MaterialTheme.typography.labelMedium)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LanguageDropdownField(
                    label = detectLabel,
                    selectedCode = selectedLanguage,
                    options = supportedLanguages,
                    nameFor = languageNameFor,
                    onSelected = onSelectedLanguage,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                LanguageDropdownField(
                    label = translateToLabel,
                    selectedCode = selectedTargetLanguage,
                    options = supportedLanguages,
                    nameFor = languageNameFor,
                    onSelected = onSelectedTargetLanguage,
                )
            }
        }
    }
}

@Composable
fun TextActionsRow(
    leftText: String,
    leftEnabled: Boolean,
    onLeft: () -> Unit,
    rightText: String,
    rightEnabled: Boolean,
    onRight: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = onLeft, enabled = leftEnabled) { Text(leftText) }
        Button(onClick = onRight, enabled = rightEnabled) { Text(rightText) }
    }
}

@Composable
fun TranslatedResultBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun RecognizeButton(
    recognizePhase: RecognizePhase,
    idleLabel: String,
    preparingLabel: String,
    listeningLabel: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = when (recognizePhase) {
        RecognizePhase.Preparing ->
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

        RecognizePhase.Listening,
        RecognizePhase.Idle,
            -> ButtonDefaults.buttonColors()
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = colors,
    ) {
        val label = when (recognizePhase) {
            RecognizePhase.Preparing -> preparingLabel
            RecognizePhase.Listening -> listeningLabel
            RecognizePhase.Idle -> idleLabel
        }
        Text(label)
    }
}

@Composable
fun BottomStatusText(
    statusMessage: String,
    ttsStatus: String,
    modifier: Modifier = Modifier,
) {
    val bottomStatus = when {
        statusMessage.isNotBlank() && ttsStatus.isNotBlank() -> statusMessage + "" + ttsStatus
        statusMessage.isNotBlank() -> statusMessage
        ttsStatus.isNotBlank() -> ttsStatus
        else -> ""
    }

    if (bottomStatus.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = bottomStatus,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun TranslateButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(label)
    }
}

@Composable
fun TranslationActionsRow(
    copyLabel: String,
    speakLabel: String,
    isTtsRunning: Boolean,
    enableCopy: Boolean,
    enableSpeak: Boolean,
    onCopy: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Button(
            onClick = onCopy,
            enabled = enableCopy,
        ) {
            Text(copyLabel)
        }

        Button(
            onClick = onSpeak,
            enabled = enableSpeak,
        ) {
            Text(if (isTtsRunning) "..." else speakLabel)
        }
    }
}

@Composable
fun SourceTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    copyLabel: String,
    speakLabel: String,
    isTtsRunning: Boolean,
    enableCopy: Boolean,
    enableSpeak: Boolean,
    onCopy: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            minLines = 3,
        )

        TextActionsRow(
            leftText = copyLabel,
            leftEnabled = enableCopy,
            onLeft = onCopy,
            rightText = if (isTtsRunning) "..." else speakLabel,
            rightEnabled = enableSpeak,
            onRight = onSpeak,
        )
    }
}