package com.example.fyp.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.model.TranslationRecord

@Composable
fun HistoryContinuousSessionBubbles(
    records: List<TranslationRecord>,
    speakerAName: String,
    speakerBName: String,
    speakingRecordId: String?,
    speakingType: String?,
    isTtsRunning: Boolean,
    ttsStatus: String,
    onSpeakOriginal: (TranslationRecord) -> Unit,
    onSpeakTranslation: (TranslationRecord) -> Unit,
    onDelete: (TranslationRecord) -> Unit,
    onToggleFavorite: (TranslationRecord) -> Unit,
    favoritedTexts: Set<String>,
    addingFavoriteId: String?,
    deleteLabel: String,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(records, key = { it.id }) { rec ->
            val speaker = rec.speaker ?: if (rec.direction?.startsWith("A") == true) "A" else "B"
            val isFromA = speaker == "A"

            val busyOriginal = isTtsRunning && speakingRecordId == rec.id && speakingType == "O"
            val busyTranslation = isTtsRunning && speakingRecordId == rec.id && speakingType == "T"
            val busyAny = busyOriginal || busyTranslation
            val recordKey = "${rec.sourceText}|${rec.targetText}"
            val isFavorited = favoritedTexts.contains(recordKey)
            val isAddingThis = addingFavoriteId == rec.id

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isFromA) Arrangement.End else Arrangement.Start,
            ) {
                OutlinedCard(modifier = Modifier.fillMaxWidth(0.92f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isFromA) speakerAName else speakerBName,
                                style = MaterialTheme.typography.labelSmall,
                            )

                            // Favorite toggle button
                            IconButton(
                                onClick = { onToggleFavorite(rec) },
                                enabled = !isAddingThis,
                                modifier = Modifier.size(32.dp)
                            ) {
                                when {
                                    isAddingThis -> CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    isFavorited -> Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Remove from Favorites",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    else -> Icon(
                                        imageVector = Icons.Filled.FavoriteBorder,
                                        contentDescription = "Add to Favorites",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(text = rec.sourceText)

                        Spacer(Modifier.height(6.dp))
                        Text(text = rec.targetText, style = MaterialTheme.typography.bodyMedium)

                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = { onSpeakOriginal(rec) },
                                enabled = !isTtsRunning,
                            ) { Text(if (busyOriginal) "Waiting..." else "🗣️O") }

                            Button(
                                onClick = { onSpeakTranslation(rec) },
                                enabled = !isTtsRunning,
                            ) { Text(if (busyTranslation) "Waiting..." else "🔊T") }

                            Button(
                                onClick = { onDelete(rec) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError,
                                ),
                            ) { Text(deleteLabel) }
                        }

                        if (busyAny) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = ttsStatus.ifBlank { "Waiting..." },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}