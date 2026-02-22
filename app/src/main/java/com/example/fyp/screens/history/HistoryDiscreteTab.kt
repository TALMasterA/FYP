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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
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
import com.example.fyp.ui.components.EmptyStates

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
    onToggleFavorite: (TranslationRecord) -> Unit,
    favoritedTexts: Set<String>,
    addingFavoriteId: String?,
    deleteLabel: String,
    noRecordsText: String,
    modifier: Modifier = Modifier,
    hasMoreRecords: Boolean = false,
    isLoadingMore: Boolean = false,
    totalRecordsCount: Int = 0,
    onLoadMore: () -> Unit = {},
) {
    if (records.isEmpty()) {
        EmptyStates.NoHistory(
            message = noRecordsText,
            modifier = Modifier.fillMaxSize()
        )
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
        onToggleFavorite = onToggleFavorite,
        favoritedTexts = favoritedTexts,
        addingFavoriteId = addingFavoriteId,
        deleteLabel = deleteLabel,
        hasMoreRecords = hasMoreRecords,
        isLoadingMore = isLoadingMore,
        totalRecordsCount = totalRecordsCount,
        onLoadMore = onLoadMore,
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
    onToggleFavorite: (TranslationRecord) -> Unit,
    favoritedTexts: Set<String>,
    addingFavoriteId: String?,
    deleteLabel: String,
    modifier: Modifier = Modifier,
    hasMoreRecords: Boolean = false,
    isLoadingMore: Boolean = false,
    totalRecordsCount: Int = 0,
    onLoadMore: () -> Unit = {},
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
            val recordKey = "${rec.sourceText}|${rec.targetText}"
            val isFavorited = favoritedTexts.contains(recordKey)
            val isAddingThis = addingFavoriteId == rec.id

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${languageNameFor(rec.sourceLang)} → ${languageNameFor(rec.targetLang)}")

                        // Favorite toggle button (add or remove)
                        IconButton(
                            onClick = { onToggleFavorite(rec) },
                            enabled = !isAddingThis
                        ) {
                            when {
                                isAddingThis -> CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                isFavorited -> Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Remove from Favorites",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                else -> Icon(
                                    imageVector = Icons.Filled.FavoriteBorder,
                                    contentDescription = "Add to Favorites",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
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

        // No "Load More" button — history shows only recent records (50-100 limit)
    }
}