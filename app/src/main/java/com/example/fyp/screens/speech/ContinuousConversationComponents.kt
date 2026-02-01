package com.example.fyp.screens.speech

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment

@Composable
fun ContinuousStartStopButton(
    isLoggedIn: Boolean,
    isRunning: Boolean,
    isPreparing: Boolean,
    isProcessing: Boolean,
    t: (UiTextKey) -> String,
    uiText: (UiTextKey, String) -> String,
    onStartStop: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonIsGrey = isPreparing || isProcessing
    val buttonColors =
        if (buttonIsGrey) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        else ButtonDefaults.buttonColors()

    val buttonText = when {
        isPreparing ->
            uiText(UiTextKey.ContinuousPreparingMicText, BaseUiTexts[UiTextKey.ContinuousPreparingMicText.ordinal])

        isProcessing ->
            uiText(UiTextKey.ContinuousTranslatingText, BaseUiTexts[UiTextKey.ContinuousTranslatingText.ordinal])

        isRunning ->
            t(UiTextKey.ContinuousStopButton)

        else ->
            t(UiTextKey.ContinuousStartButton)
    }

    Button(
        onClick = { onStartStop(!isRunning) },
        enabled = isLoggedIn && (!isPreparing && !isProcessing),
        modifier = modifier,
        colors = buttonColors,
    ) {
        Text(buttonText)
    }
}

@Composable
fun ContinuousStatusBlock(
    currentStringLabel: String,
    partial: String,
    isTtsRunning: Boolean,
    ttsStatus: String,
) {
    Text(
        text = "$currentStringLabel $partial",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    if (isTtsRunning || ttsStatus.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = ttsStatus.ifBlank { "Speaking..." },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun ContinuousMessageBubble(
    msg: ChatMessage,
    speakerAName: String,
    speakerBName: String,
    translationSuffix: String,
    isTtsRunning: Boolean,
    onSpeakMessage: (ChatMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isFromPersonA) Arrangement.End else Arrangement.Start,
    ) {
        OutlinedCard(modifier = Modifier.fillMaxWidth(0.92f)) {
            Column(modifier = Modifier.padding(10.dp)) {
                val speakerName = if (msg.isFromPersonA) speakerAName else speakerBName
                val label = if (msg.isTranslation) speakerName + translationSuffix else speakerName

                Text(text = label, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(4.dp))
                Text(text = msg.text)
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = { onSpeakMessage(msg) },
                        enabled = !isTtsRunning, // ⭐ global TTS lock
                    ) {
                        Text(if (isTtsRunning) "..." else "🔊")
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationHeaderRow(
    speakerLabel: String,
    isPersonATalking: Boolean,
    isLoggedIn: Boolean,
    isPreparing: Boolean,
    isProcessing: Boolean,
    onPersonToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = speakerLabel)

        Switch(
            checked = isPersonATalking,
            onCheckedChange = onPersonToggle,
            enabled = isLoggedIn && !isPreparing && !isProcessing,
        )
    }
}