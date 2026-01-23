package com.example.fyp.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.model.TranslationRecord

@Composable
fun HistoryDiscreteTab(
    records: List<TranslationRecord>,
    speakingRecordId: String?,
    speakingType: String?,
    isTtsRunning: Boolean,
    ttsStatus: String,
    languageNameFor: (String) -> String,
    onDelete: (TranslationRecord) -> Unit,
    onSpeakOriginal: (TranslationRecord) -> Unit,
    onSpeakTranslation: (TranslationRecord) -> Unit,
    deleteLabel: String,
    noRecordsText: String,
    modifier: Modifier = Modifier,
) {
    if (records.isEmpty()) {
        Text(noRecordsText, modifier = Modifier.padding(8.dp))
        return
    }

    HistoryList(
        records = records,
        languageNameFor = languageNameFor,
        speakingRecordId = speakingRecordId,
        speakingType = speakingType,
        isTtsRunning = isTtsRunning,
        ttsStatus = ttsStatus,
        onSpeakOriginal = onSpeakOriginal,
        onSpeakTranslation = onSpeakTranslation,
        onDelete = onDelete,
        deleteLabel = deleteLabel,
        modifier = modifier
    )
}

@Composable
fun HistoryList(
    records: List<TranslationRecord>,
    languageNameFor: (String) -> String,
    speakingRecordId: String?,
    speakingType: String?,
    isTtsRunning: Boolean,
    ttsStatus: String,
    onSpeakOriginal: (TranslationRecord) -> Unit,
    onSpeakTranslation: (TranslationRecord) -> Unit,
    onDelete: (TranslationRecord) -> Unit,
    deleteLabel: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(records, key = { it.id }) { rec ->
            val busyOriginal = isTtsRunning && speakingRecordId == rec.id && speakingType == "O"
            val busyTranslation = isTtsRunning && speakingRecordId == rec.id && speakingType == "T"
            val busyAny = busyOriginal || busyTranslation

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "${languageNameFor(rec.sourceLang)} → ${languageNameFor(rec.targetLang)}")
                    Spacer(Modifier.height(6.dp))
                    Text(text = rec.sourceText)
                    Spacer(Modifier.height(4.dp))
                    Text(text = rec.targetText, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onSpeakOriginal(rec) },
                            enabled = !isTtsRunning
                        ) { Text(if (busyOriginal) "Waiting..." else "🗣️O") }

                        Button(
                            onClick = { onSpeakTranslation(rec) },
                            enabled = !isTtsRunning
                        ) { Text(if (busyTranslation) "Waiting..." else "🔊T") }

                        Button(
                            onClick = { onDelete(rec) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) { Text(deleteLabel) }
                    }

                    if (busyAny) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = ttsStatus.ifBlank { "Waiting..." },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}