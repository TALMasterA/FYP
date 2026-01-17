package com.example.fyp.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.UiTextKey
import com.example.fyp.screens.speech.SpeechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val speechVm: SpeechViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(t(UiTextKey.HistoryTabDiscrete), t(UiTextKey.HistoryTabContinuous))

    val discreteRecords = uiState.records
        .filter { it.mode == "discrete" }
        .sortedByDescending { it.timestamp }

    val sessions = groupContinuousSessions(uiState.records)

    var selectedSessionId by remember { mutableStateOf<String?>(null) }

    var pendingDeleteRecord by remember { mutableStateOf<TranslationRecord?>(null) }
    var pendingDeleteSessionId by remember { mutableStateOf<String?>(null) }
    var pendingRenameSessionId by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }

    var speakingRecordId by remember { mutableStateOf<String?>(null) }
    var speakingType by remember { mutableStateOf<String?>(null) } // "O" or "T"

    val pageSize = 10

    // Discrete pagination
    var discretePage by remember { mutableIntStateOf(0) }
    val discreteTotalPages = pageCount(discreteRecords.size, pageSize)
    val discretePageRecords = discreteRecords.drop(discretePage * pageSize).take(pageSize)

    // Continuous sessions pagination (session list only)
    var sessionsPage by remember { mutableIntStateOf(0) }
    val sessionsTotalPages = pageCount(sessions.size, pageSize)
    val sessionsPageItems = sessions.drop(sessionsPage * pageSize).take(pageSize)

    // Session detail pagination
    var sessionPage by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            pendingDeleteRecord = null
            pendingDeleteSessionId = null
            pendingRenameSessionId = null
            renameText = ""
            selectedSessionId = null
            speakingRecordId = null
            speakingType = null
        }
    }

    // ----- dialogs -----
    pendingDeleteRecord?.let { rec ->
        DeleteRecordDialog(
            title = t(UiTextKey.DialogDeleteRecordTitle),
            message = t(UiTextKey.DialogDeleteRecordMessage),
            confirmText = t(UiTextKey.ActionDelete),
            cancelText = t(UiTextKey.ActionCancel),
            onConfirm = {
                viewModel.deleteRecord(rec)
                pendingDeleteRecord = null
            },
            onDismiss = { pendingDeleteRecord = null },
        )
    }

    pendingDeleteSessionId?.let { sid ->
        DeleteSessionDialog(
            title = t(UiTextKey.DialogDeleteSessionTitle),
            message = t(UiTextKey.DialogDeleteSessionMessage),
            confirmText = t(UiTextKey.HistoryDeleteSessionButton),
            cancelText = t(UiTextKey.ActionCancel),
            onConfirm = {
                viewModel.deleteSession(sid)
                if (selectedSessionId == sid) selectedSessionId = null
                pendingDeleteSessionId = null
            },
            onDismiss = { pendingDeleteSessionId = null },
        )
    }

    pendingRenameSessionId?.let { sid ->
        RenameSessionDialog(
            title = t(UiTextKey.HistoryNameSessionTitle),
            label = t(UiTextKey.HistorySessionNameLabel),
            value = renameText,
            confirmText = t(UiTextKey.ActionSave),
            cancelText = t(UiTextKey.ActionCancel),
            onValueChange = { renameText = it },
            onConfirm = {
                viewModel.renameSession(sid, renameText.trim())
                pendingRenameSessionId = null
            },
            onDismiss = { pendingRenameSessionId = null },
        )
    }

    val showUiDropdown = (selectedTab == 0) || (selectedTab == 1 && selectedSessionId == null)

    StandardScreenScaffold(
        title = t(UiTextKey.HistoryTitle),
        onBack = {
            if (selectedTab == 1 && selectedSessionId != null) selectedSessionId = null
            else onBack()
        },
        backContentDescription = t(UiTextKey.NavBack),
    ) { innerPadding ->
        StandardScreenBody(
            innerPadding = innerPadding,
            scrollable = false,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (showUiDropdown) {
                AppLanguageDropdown(
                    uiLanguages = uiLanguages,
                    appLanguageState = appLanguageState,
                    onUpdateAppLanguage = onUpdateAppLanguage,
                    uiText = uiText,
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            if (index == 0) selectedSessionId = null
                            discretePage = 0
                            sessionsPage = 0
                            sessionPage = 0
                        },
                        text = { Text(label) },
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    uiState.isLoading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator() }

                    uiState.error != null -> Text(
                        text = uiState.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp),
                    )

                    selectedTab == 0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            HistoryDiscreteTab(
                                records = discretePageRecords,
                                languageNameFor = uiLanguageNameFor,
                                speakingRecordId = speakingRecordId,
                                speakingType = speakingType,
                                isTtsRunning = speechVm.isTtsRunning,
                                ttsStatus = speechVm.ttsStatus,
                                onSpeakOriginal = { rec ->
                                    speakingRecordId = rec.id
                                    speakingType = "O"
                                    speechVm.speakTextOriginal(rec.sourceLang, rec.sourceText)
                                },
                                onSpeakTranslation = { rec ->
                                    speakingRecordId = rec.id
                                    speakingType = "T"
                                    speechVm.speakText(rec.targetLang, rec.targetText)
                                },
                                onDelete = { rec -> pendingDeleteRecord = rec },
                                deleteLabel = t(UiTextKey.ActionDelete),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    else -> {
                        // Continuous tab
                        if (selectedSessionId == null) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                HistoryContinuousTab(
                                    sessions = sessionsPageItems,
                                    selectedSessionId = selectedSessionId,
                                    sessionNames = uiState.sessionNames,
                                    noSessionsText = t(UiTextKey.HistoryNoContinuousSessions),
                                    openLabel = t(UiTextKey.ActionOpen),
                                    nameLabel = t(UiTextKey.ActionName),
                                    deleteLabel = t(UiTextKey.ActionDelete),
                                    sessionTitleTemplate = t(UiTextKey.HistorySessionTitleTemplate),
                                    itemsCountTemplate = t(UiTextKey.HistoryItemsCountTemplate),
                                    onOpenSession = { sid ->
                                        selectedSessionId = sid
                                        sessionPage = 0
                                    },
                                    onRequestRename = { sid ->
                                        renameText = uiState.sessionNames[sid].orEmpty()
                                        pendingRenameSessionId = sid
                                    },
                                    onRequestDelete = { sid ->
                                        pendingDeleteSessionId = sid
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        } else {
                            val sid = selectedSessionId.orEmpty()
                            val allSessionRecords = sessions
                                .firstOrNull { it.sessionId == sid }
                                ?.records
                                ?.sortedWith(
                                    compareBy<TranslationRecord> { it.sequence ?: Long.MAX_VALUE }
                                        .thenBy { it.timestamp },
                                )
                                .orEmpty()

                            val displayName = uiState.sessionNames[sid].orEmpty()
                            val title = displayName.ifBlank {
                                formatSessionTitle(
                                    template = t(UiTextKey.HistorySessionTitleTemplate),
                                    sessionId = sid,
                                )
                            }

                            val sessionTotalPages = pageCount(allSessionRecords.size, pageSize)
                            val sessionPageRecords = allSessionRecords.drop(sessionPage * pageSize).take(pageSize)

                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(title, style = MaterialTheme.typography.titleMedium)
                                    TextButton(onClick = { selectedSessionId = null }) {
                                        Text(t(UiTextKey.NavBack))
                                    }
                                }

                                HistoryContinuousSessionBubbles(
                                    records = sessionPageRecords,
                                    speakerAName = t(UiTextKey.ContinuousSpeakerAName),
                                    speakerBName = t(UiTextKey.ContinuousSpeakerBName),
                                    speakingRecordId = speakingRecordId,
                                    speakingType = speakingType,
                                    isTtsRunning = speechVm.isTtsRunning,
                                    ttsStatus = speechVm.ttsStatus,
                                    onSpeakOriginal = { rec ->
                                        speakingRecordId = rec.id
                                        speakingType = "O"
                                        speechVm.speakTextOriginal(rec.sourceLang, rec.sourceText)
                                    },
                                    onSpeakTranslation = { rec ->
                                        speakingRecordId = rec.id
                                        speakingType = "T"
                                        speechVm.speakText(rec.targetLang, rec.targetText)
                                    },
                                    onDelete = { rec -> pendingDeleteRecord = rec },
                                    deleteLabel = t(UiTextKey.ActionDelete),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            PaginationRow(
                page = when {
                    selectedTab == 0 -> discretePage
                    selectedSessionId == null -> sessionsPage
                    else -> sessionPage
                },
                totalPages = when {
                    selectedTab == 0 -> discreteTotalPages
                    selectedSessionId == null -> sessionsTotalPages
                    else -> {
                        val sid = selectedSessionId.orEmpty()
                        val total = sessions.firstOrNull { it.sessionId == sid }?.records?.size ?: 0
                        pageCount(total, pageSize)
                    }
                },
                prevLabel = uiText(UiTextKey.PaginationPrevLabel, BaseUiTexts[UiTextKey.PaginationPrevLabel.ordinal]),
                nextLabel = uiText(UiTextKey.PaginationNextLabel, BaseUiTexts[UiTextKey.PaginationNextLabel.ordinal]),
                pageLabelTemplate = uiText(
                    UiTextKey.PaginationPageLabelTemplate,
                    BaseUiTexts[UiTextKey.PaginationPageLabelTemplate.ordinal],
                ),
                onPrev = {
                    when {
                        selectedTab == 0 && discretePage > 0 -> discretePage--
                        selectedSessionId == null && sessionsPage > 0 -> sessionsPage--
                        selectedSessionId != null && sessionPage > 0 -> sessionPage--
                    }
                },
                onNext = {
                    when {
                        selectedTab == 0 && discretePage < discreteTotalPages - 1 -> discretePage++
                        selectedSessionId == null && sessionsPage < sessionsTotalPages - 1 -> sessionsPage++
                        selectedSessionId != null -> sessionPage++
                    }
                },
            )
        }
    }
}

@Composable
private fun HistoryContinuousSessionBubbles(
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isFromA) Arrangement.End else Arrangement.Start,
            ) {
                OutlinedCard(modifier = Modifier.fillMaxWidth(0.92f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isFromA) speakerAName else speakerBName,
                            style = MaterialTheme.typography.labelSmall,
                        )

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

private fun pageCount(total: Int, pageSize: Int): Int =
    if (total <= 0) 1 else ((total - 1) / pageSize) + 1

@Composable
private fun PaginationRow(
    page: Int,
    totalPages: Int,
    prevLabel: String,
    nextLabel: String,
    pageLabelTemplate: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pageText = pageLabelTemplate
        .replace("{page}", (page + 1).toString())
        .replace("{total}", totalPages.toString())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onPrev, enabled = page > 0) { Text(prevLabel) }
        Text(pageText)
        TextButton(onClick = onNext, enabled = page < totalPages - 1) { Text(nextLabel) }
    }
}