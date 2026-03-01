package com.example.fyp.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.ui.components.EmptyStateView
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton

@Composable
fun HistoryContinuousTab(
    sessions: List<HistorySessionUi>,
    selectedSessionId: String?,
    sessionNames: Map<String, String>,
    noSessionsText: String,
    openLabel: String,
    nameLabel: String,
    deleteLabel: String,
    sessionTitleTemplate: String,
    itemsCountTemplate: String,
    onOpenSession: (String) -> Unit,
    onRequestRename: (String) -> Unit,
    onRequestDelete: (String) -> Unit,
    onFavouriteSession: (String, List<com.example.fyp.model.TranslationRecord>) -> Unit = { _, _ -> },
    onUnfavouriteSession: (String, List<com.example.fyp.model.TranslationRecord>) -> Unit = { _, _ -> },
    isSessionFavourited: (List<com.example.fyp.model.TranslationRecord>) -> Boolean = { false },
    favouritingSessionId: String? = null,
    modifier: Modifier = Modifier,
) {
    // Session details view stays in HistoryScreen.
    if (selectedSessionId != null) return

    if (sessions.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyStateView(
                icon = Icons.Filled.Forum,
                title = "No Conversations Yet",
                message = noSessionsText,
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(sessions, key = { it.sessionId }) { session ->
            val sid = session.sessionId
            val records = session.records

            val displayName = sessionNames[sid].orEmpty()
            val title = displayName.ifBlank {
                formatSessionTitle(template = sessionTitleTemplate, sessionId = sid)
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                formatItemsCount(template = itemsCountTemplate, count = records.size),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Session favourite button
                        val isFavouriting = favouritingSessionId == sid
                        val isFav = isSessionFavourited(records)
                        if (isFavouriting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = {
                                if (isFav) onUnfavouriteSession(sid, records)
                                else onFavouriteSession(sid, records)
                            }) {
                                Icon(
                                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = if (isFav) "Unfavourite session" else "Favourite session",
                                    tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { onOpenSession(sid) }) { Text(openLabel) }
                        OutlinedButton(onClick = { onRequestRename(sid) }) { Text(nameLabel) }
                        OutlinedButton(
                            onClick = { onRequestDelete(sid) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text(deleteLabel) }
                    }
                }
            }
        }
    }
}