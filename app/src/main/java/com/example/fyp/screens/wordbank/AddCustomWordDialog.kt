package com.example.fyp.screens.wordbank

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.model.ui.UiTextKey

@Composable
fun AddCustomWordDialog(
    primaryLanguageName: String,
    targetLanguageName: String,
    onDismiss: () -> Unit,
    onConfirm: (originalWord: String, translatedWord: String, pronunciation: String, example: String) -> Unit,
    onTranslate: ((String, (String) -> Unit) -> Unit)? = null,
    isTranslating: Boolean = false,
    t: (UiTextKey) -> String
) {
    var originalWord by remember { mutableStateOf("") }
    var translatedWord by remember { mutableStateOf("") }
    var pronunciation by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }

    val maxWordLength = 200
    val maxExampleLength = 500

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.CustomWordsAdd)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hint text
                Text(
                    text = "Enter word in $primaryLanguageName and its translation in $targetLanguageName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Original word field with language label
                OutlinedTextField(
                    value = originalWord,
                    onValueChange = { if (it.length <= maxWordLength) originalWord = it },
                    label = { Text("${t(UiTextKey.CustomWordsOriginalLabel)} ($primaryLanguageName)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = if (originalWord.length >= maxWordLength * 4 / 5) {
                        {
                            Text(
                                "${originalWord.length}/$maxWordLength",
                                color = if (originalWord.length >= maxWordLength) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else null
                )

                // Translated word field with translate button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = translatedWord,
                        onValueChange = { if (it.length <= maxWordLength) translatedWord = it },
                        label = { Text("${t(UiTextKey.CustomWordsTranslatedLabel)} ($targetLanguageName)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        supportingText = if (translatedWord.length >= maxWordLength * 4 / 5) {
                            {
                                Text(
                                    "${translatedWord.length}/$maxWordLength",
                                    color = if (translatedWord.length >= maxWordLength) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else null
                    )

                    // Translate button
                    if (onTranslate != null) {
                        IconButton(
                            onClick = {
                                if (originalWord.isNotBlank()) {
                                    onTranslate(originalWord) { result ->
                                        translatedWord = result
                                    }
                                }
                            },
                            enabled = originalWord.isNotBlank() && !isTranslating
                        ) {
                            if (isTranslating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Auto-translate",
                                    tint = if (originalWord.isNotBlank())
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = pronunciation,
                    onValueChange = { if (it.length <= maxWordLength) pronunciation = it },
                    label = { Text(t(UiTextKey.CustomWordsPronunciationLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = example,
                    onValueChange = { if (it.length <= maxExampleLength) example = it },
                    label = { Text(t(UiTextKey.CustomWordsExampleLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    supportingText = if (example.length >= maxExampleLength * 4 / 5) {
                        {
                            Text(
                                "${example.length}/$maxExampleLength",
                                color = if (example.length >= maxExampleLength) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else null
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (originalWord.isNotBlank() && translatedWord.isNotBlank()) {
                        onConfirm(originalWord.trim(), translatedWord.trim(), pronunciation.trim(), example.trim())
                    }
                },
                enabled = originalWord.isNotBlank() && translatedWord.isNotBlank() && !isTranslating
            ) {
                Text(t(UiTextKey.ActionSave))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t(UiTextKey.ActionCancel))
            }
        }
    )
}
