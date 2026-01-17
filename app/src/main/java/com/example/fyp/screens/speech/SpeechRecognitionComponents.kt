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
import androidx.compose.ui.unit.sp

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
                Text("⇄", fontSize = 16.sp)
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
fun LabeledTextBlock(
    text: String,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.bodyMedium)
}