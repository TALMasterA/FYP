package com.example.fyp.feature.history

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    modifier: Modifier = Modifier,
) {
    // Session details view stays in HistoryScreen (same behavior as before).
    if (selectedSessionId != null) return

    if (sessions.isEmpty()) {
        Text(noSessionsText, modifier = Modifier.padding(8.dp))
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
            val title =
                if (displayName.isNotBlank()) displayName
                else formatSessionTitle(template = sessionTitleTemplate, sessionId = sid)

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        formatItemsCount(template = itemsCountTemplate, count = records.size),
                        style = MaterialTheme.typography.bodySmall
                    )

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