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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
    onBack: () -> Unit
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
    var sessionPage by remember { mutableIntStateOf(0) }

    // Cleanup transient UI state when leaving screen
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
            onDismiss = { pendingDeleteRecord = null }
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
            onDismiss = { pendingDeleteSessionId = null }
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
            onDismiss = { pendingRenameSessionId = null }
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

                            // reset both pagers when switching tabs
                            discretePage = 0
                            sessionsPage = 0
                        },
                        text = { Text(label) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    uiState.isLoading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    uiState.error != null -> Text(
                        text = uiState.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )

                    selectedTab == 0 -> { // Discrete tab
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
                                modifier = Modifier.weight(1f)  // Takes all remaining space
                            )
                        }
                    }

                    else -> { // Continuous tab
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
                                    onOpenSession = { sid -> selectedSessionId = sid },
                                    onRequestRename = { sid ->
                                        renameText = uiState.sessionNames[sid].orEmpty()
                                        pendingRenameSessionId = sid
                                    },
                                    onRequestDelete = { sid -> pendingDeleteSessionId = sid },
                                    modifier = Modifier.weight(1f)  // Takes all remaining space
                                )
                            }
                        } else {
                            // Session details view
                            val sid = selectedSessionId.orEmpty()
                            val allSessionRecords = sessions
                                .firstOrNull { it.sessionId == sid }
                                ?.records
                                ?.sortedBy { it.timestamp }
                                .orEmpty()

                            val displayName = uiState.sessionNames[sid].orEmpty()
                            val title = displayName.ifBlank {
                                formatSessionTitle(
                                    template = t(UiTextKey.HistorySessionTitleTemplate),
                                    sessionId = sid
                                )
                            }

                            val pageSize = 10
                            val sessionTotalPages = pageCount(allSessionRecords.size, pageSize)
                            val sessionPageRecords = allSessionRecords.drop(sessionPage * pageSize).take(pageSize)

                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(title, style = MaterialTheme.typography.titleMedium)
                                    TextButton(onClick = { selectedSessionId = null }) {
                                        Text(t(UiTextKey.NavBack))
                                    }
                                }
                                HistoryList(
                                    records = sessionPageRecords,
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
                                    modifier = Modifier.weight(1f)
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
                    else -> sessionPage  // ⭐ NEW: session detail
                },
                totalPages = when {
                    selectedTab == 0 -> discreteTotalPages
                    selectedSessionId == null -> sessionsTotalPages
                    else -> pageCount(  // ⭐ NEW: calculate session total
                        sessions.firstOrNull { it.sessionId == selectedSessionId }?.records?.size ?: 0, pageSize
                    )
                },
                onPrev = {
                    when {
                        selectedTab == 0 && discretePage > 0 -> discretePage--
                        selectedSessionId == null && sessionsPage > 0 -> sessionsPage--
                        else -> sessionPage--  // ⭐ NEW: session detail prev
                    }
                },
                onNext = {
                    when {
                        selectedTab == 0 && discretePage < discreteTotalPages - 1 -> discretePage++
                        selectedSessionId == null && sessionsPage < sessionsTotalPages - 1 -> sessionsPage++
                        else -> sessionPage++  // ⭐ NEW: session detail next
                    }
                }
            )
        }
    }
}

private fun pageCount(total: Int, pageSize: Int): Int =
    if (total <= 0) 1 else ((total - 1) / pageSize) + 1

@Composable
private fun PaginationRow(
    page: Int,
    totalPages: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onPrev, enabled = page > 0) { Text("Prev") }
        Text("Page ${page + 1} / $totalPages")
        TextButton(onClick = onNext, enabled = page < totalPages - 1) { Text("Next") }
    }
}