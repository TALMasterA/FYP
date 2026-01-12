package com.example.fyp.feature.history

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.AppLanguageDropdown
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.feature.speech.SpeechViewModel
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val speechVm: SpeechViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(t(UiTextKey.HistoryTabDiscrete), t(UiTextKey.HistoryTabContinuous))

    val discreteRecords = uiState.records.filter { it.mode == "discrete" }
    val continuousRecords = uiState.records.filter { it.mode == "continuous" }

    val sessions = continuousRecords
        .filter { it.sessionId.isNotBlank() }
        .groupBy { it.sessionId }
        .toList()
        .sortedByDescending { (_, records) -> records.lastOrNull()?.timestamp }

    var selectedSessionId by remember { mutableStateOf<String?>(null) }

    var pendingDeleteRecord by remember { mutableStateOf<TranslationRecord?>(null) }
    var pendingDeleteSessionId by remember { mutableStateOf<String?>(null) }
    var pendingRenameSessionId by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            pendingDeleteRecord = null
            pendingDeleteSessionId = null
            pendingRenameSessionId = null
            renameText = ""
            selectedSessionId = null
        }
    }

    // ----- dialogs -----
    if (pendingDeleteRecord != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteRecord = null },
            title = { Text(t(UiTextKey.DialogDeleteRecordTitle)) },
            text = { Text(t(UiTextKey.DialogDeleteRecordMessage)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRecord(pendingDeleteRecord!!)
                        pendingDeleteRecord = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text(t(UiTextKey.ActionDelete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteRecord = null }) { Text(t(UiTextKey.ActionCancel)) }
            }
        )
    }

    if (pendingDeleteSessionId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteSessionId = null },
            title = { Text(t(UiTextKey.DialogDeleteSessionTitle)) },
            text = { Text(t(UiTextKey.DialogDeleteSessionMessage)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSession(pendingDeleteSessionId!!)
                        if (selectedSessionId == pendingDeleteSessionId) selectedSessionId = null
                        pendingDeleteSessionId = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text(t(UiTextKey.HistoryDeleteSessionButton)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSessionId = null }) { Text(t(UiTextKey.ActionCancel)) }
            }
        )
    }

    if (pendingRenameSessionId != null) {
        AlertDialog(
            onDismissRequest = { pendingRenameSessionId = null },
            title = { Text(t(UiTextKey.HistoryNameSessionTitle)) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text(t(UiTextKey.HistorySessionNameLabel)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameSession(pendingRenameSessionId!!, renameText.trim())
                        pendingRenameSessionId = null
                    }
                ) { Text(t(UiTextKey.ActionSave)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingRenameSessionId = null }) { Text(t(UiTextKey.ActionCancel)) }
            }
        )
    }

    val showUiDropdown = (selectedTab == 0) || (selectedTab == 1 && selectedSessionId == null)

    StandardScreenScaffold(
        title = t(UiTextKey.HistoryTitle),
        onBack = {
            if (selectedTab == 1 && selectedSessionId != null) selectedSessionId = null
            else onBack()
        },
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        StandardScreenBody(
            innerPadding = innerPadding,
            scrollable = false,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showUiDropdown) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            if (index == 0) selectedSessionId = null
                        },
                        text = { Text(label) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.error != null -> {
                        Text(
                            text = uiState.error.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    selectedTab == 0 -> {
                        HistoryList(
                            records = discreteRecords,
                            languageNameFor = uiLanguageNameFor,
                            onSpeakOriginal = { rec -> speechVm.speakTextOriginal(rec.sourceLang, rec.sourceText) },
                            onSpeakTranslation = { rec -> speechVm.speakText(rec.targetLang, rec.targetText) },
                            onDelete = { rec -> pendingDeleteRecord = rec },
                            deleteLabel = t(UiTextKey.ActionDelete),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        if (selectedSessionId == null) {
                            if (sessions.isEmpty()) {
                                Text(t(UiTextKey.HistoryNoContinuousSessions), modifier = Modifier.padding(8.dp))
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(sessions, key = { (sid, _) -> sid }) { (sid, records) ->
                                        val displayName = uiState.sessionNames[sid].orEmpty()
                                        val title =
                                            if (displayName.isNotBlank()) displayName
                                            else formatSessionTitle(
                                                template = t(UiTextKey.HistorySessionTitleTemplate),
                                                sessionId = sid
                                            )

                                        OutlinedCard(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = MaterialTheme.colorScheme.background
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(title, style = MaterialTheme.typography.titleMedium)
                                                Text(
                                                    formatItemsCount(
                                                        template = t(UiTextKey.HistoryItemsCountTemplate),
                                                        count = records.size
                                                    )
                                                )

                                                Spacer(Modifier.height(10.dp))

                                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                    Button(onClick = { selectedSessionId = sid }) {
                                                        Text(t(UiTextKey.ActionOpen))
                                                    }

                                                    OutlinedButton(onClick = {
                                                        renameText = uiState.sessionNames[sid].orEmpty()
                                                        pendingRenameSessionId = sid
                                                    }) {
                                                        Text(t(UiTextKey.ActionName))
                                                    }

                                                    Button(
                                                        onClick = { pendingDeleteSessionId = sid },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.error,
                                                            contentColor = MaterialTheme.colorScheme.onError
                                                        )
                                                    ) {
                                                        Text(t(UiTextKey.ActionDelete))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            val sessionRecords = sessions
                                .firstOrNull { (sid, _) -> sid == selectedSessionId }
                                ?.second
                                ?.sortedBy { it.timestamp }
                                .orEmpty()

                            val displayName = uiState.sessionNames[selectedSessionId!!].orEmpty()
                            val title =
                                if (displayName.isNotBlank()) displayName
                                else formatSessionTitle(
                                    template = t(UiTextKey.HistorySessionTitleTemplate),
                                    sessionId = selectedSessionId!!
                                )

                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(title, style = MaterialTheme.typography.titleMedium)
                                    TextButton(onClick = { selectedSessionId = null }) { Text(t(UiTextKey.NavBack)) }
                                }

                                Spacer(Modifier.height(8.dp))

                                HistoryList(
                                    records = sessionRecords,
                                    languageNameFor = uiLanguageNameFor,
                                    onSpeakOriginal = { rec -> speechVm.speakTextOriginal(rec.sourceLang, rec.sourceText) },
                                    onSpeakTranslation = { rec -> speechVm.speakText(rec.targetLang, rec.targetText) },
                                    onDelete = { rec -> pendingDeleteRecord = rec },
                                    deleteLabel = t(UiTextKey.ActionDelete),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryList(
    records: List<TranslationRecord>,
    languageNameFor: (String) -> String,
    onSpeakOriginal: (TranslationRecord) -> Unit,
    onSpeakTranslation: (TranslationRecord) -> Unit,
    onDelete: (TranslationRecord) -> Unit,
    deleteLabel: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(records, key = { it.id }) { rec ->
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
                        Button(onClick = { onSpeakOriginal(rec) }) { Text("🗣️O") }
                        Button(onClick = { onSpeakTranslation(rec) }) { Text("🔊T") }
                        Button(
                            onClick = { onDelete(rec) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) { Text(deleteLabel) }
                    }
                }
            }
        }
    }
}

/**
 * Supports both styles:
 * - New: "Session {id}"
 * - Old: "Session %s" (or any translated variant that still uses %s)
 */
private fun formatSessionTitle(template: String, sessionId: String): String {
    val shortId = sessionId.take(8)

    return when {
        template.contains("{id}") -> template.replace("{id}", shortId)
        else -> safeFormat(template, shortId)
    }
}

/**
 * Supports both styles:
 * - New: "{count} items"
 * - Old: "%d items" (or any translated variant that still uses %d)
 */
private fun formatItemsCount(template: String, count: Int): String {
    return when {
        template.contains("{count}") -> template.replace("{count}", count.toString())
        else -> safeFormat(template, count)
    }
}

private fun safeFormat(template: String, vararg args: Any): String {
    return try {
        String.format(template, *args)
    } catch (_: Exception) {
        // Escape any '%' that is not a valid placeholder so it won't crash.
        val escaped = template.replace(Regex("%(?!([0-9]+\\$)?[sd%])"), "%%")
        try {
            String.format(escaped, *args)
        } catch (_: Exception) {
            // Last resort: show the template without formatting
            template
        }
    }
}