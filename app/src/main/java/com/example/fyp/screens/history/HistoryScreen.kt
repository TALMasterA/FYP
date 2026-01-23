@file:Suppress("AssignedValueIsNeverRead")

package com.example.fyp.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.fyp.core.PaginationRow
import com.example.fyp.core.pageCount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import com.example.fyp.core.LanguageDropdownField

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

    var showFilterDialog by remember { mutableStateOf(false) }
    var filterLanguageCode by remember { mutableStateOf("") }
    var filterKeyword by remember { mutableStateOf("") }

    val languageCounts = remember(uiState.records) {
        uiState.records
            .flatMap { listOf(it.sourceLang, it.targetLang) }
            .filter { it.isNotBlank() }
            .groupingBy { it }
            .eachCount()
    }

    val languageOptions = remember(languageCounts) {
        languageCounts.entries
            .sortedByDescending { it.value }
            .map { it.key }
    }

    val keyword = filterKeyword.trim()

    val filteredRecords = uiState.records.filter { rec ->
        val langOk = filterLanguageCode.isBlank() ||
                rec.sourceLang == filterLanguageCode || rec.targetLang == filterLanguageCode

        val keywordOk = keyword.isBlank() ||
                rec.sourceText.contains(keyword, ignoreCase = true) ||
                rec.targetText.contains(keyword, ignoreCase = true)

        langOk && keywordOk
    }

    val discreteRecords = filteredRecords
        .filter { it.mode == "discrete" }
        .sortedByDescending { it.timestamp }

    val sessions = groupContinuousSessions(filteredRecords)

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

    LaunchedEffect(sessions, selectedSessionId) {
        val sid = selectedSessionId
        if (sid != null && sessions.none { it.sessionId == sid }) {
            selectedSessionId = null
        }
    }

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

    if (showFilterDialog) {
        var draftLang by remember { mutableStateOf(filterLanguageCode) }
        var draftKeyword by remember { mutableStateOf(filterKeyword) }

        val nameForFilter: (String) -> String = { code ->
            if (code.isBlank()) t(UiTextKey.FilterDropdownDefault)
            else "${uiLanguageNameFor(code)} (${languageCounts[code] ?: 0})"
        }

        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text(t(UiTextKey.FilterTitle)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LanguageDropdownField(
                        label = t(UiTextKey.FilterLangDrop),
                        selectedCode = draftLang,
                        options = listOf("") + languageOptions,
                        nameFor = nameForFilter,
                        onSelected = { draftLang = it },
                        enabled = true
                    )

                    OutlinedTextField(
                        value = draftKeyword,
                        onValueChange = { draftKeyword = it },
                        label = { Text(t(UiTextKey.FilterKeyword)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    filterLanguageCode = draftLang
                    filterKeyword = draftKeyword
                    showFilterDialog = false

                    // reset pagination
                    discretePage = 0
                    sessionsPage = 0
                    sessionPage = 0
                }) { Text(t(UiTextKey.FilterApply)) }
            },
            dismissButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            draftLang = ""
                            draftKeyword = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(t(UiTextKey.FilterClear))
                    }

                    TextButton(
                        onClick = { showFilterDialog = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(t(UiTextKey.FilterCancel))
                    }
                }
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
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            TextButton(onClick = { showFilterDialog = true }) { Text(t(UiTextKey.FilterHistoryScreenTitle)) }
        },
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
                                noRecordsText = t(UiTextKey.HistoryNoDiscreteRecords),
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