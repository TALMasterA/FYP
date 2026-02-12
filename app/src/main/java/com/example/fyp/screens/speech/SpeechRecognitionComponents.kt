package com.example.fyp.screens.speech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.core.LanguageDropdownField

@Composable
fun SpeechLanguagePickers(
    detectLabel: String,
    translateToLabel: String,
    selectedLanguage: String,
    selectedTargetLanguage: String,
    sourceLanguageOptions: List<String>,
    targetLanguageOptions: List<String>,
    sourceLanguageNameFor: (String) -> String,
    targetLanguageNameFor: (String) -> String,
    onSelectedLanguage: (String) -> Unit,
    onSelectedTargetLanguage: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            LanguageDropdownField(
                label = detectLabel,
                selectedCode = selectedLanguage,
                options = sourceLanguageOptions,
                nameFor = sourceLanguageNameFor,
                onSelected = onSelectedLanguage,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            LanguageDropdownField(
                label = translateToLabel,
                selectedCode = selectedTargetLanguage,
                options = targetLanguageOptions,
                nameFor = targetLanguageNameFor,
                onSelected = onSelectedTargetLanguage,
            )
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
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.height(8.dp))

    val showPlaceholder = text.isBlank()
    val displayText = if (showPlaceholder) placeholder else text

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Text(
            text = displayText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (showPlaceholder)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.onSurface,
            minLines = 3
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
        // Use Box to overlay the clear button on top-right corner
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                minLines = 3,
                // Add padding to the right to prevent text from going under the clear button
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            )

            // Clear button positioned at top-right corner, always visible when there's content
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

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