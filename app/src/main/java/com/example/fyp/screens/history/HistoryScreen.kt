package com.example.fyp.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.TranslationRecord
import com.example.fyp.model.ui.UiTextKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.PaginationRow
import com.example.fyp.core.pageCount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.rememberHapticFeedback
import com.example.fyp.core.UiConstants
import com.example.fyp.ui.components.TranslationCardSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiLanguages: List<Pair<String, String>>,
    appLanguageState: AppLanguageState,
    onUpdateAppLanguage: (String, Map<UiTextKey, String>) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = rememberHapticFeedback()

    // Refresh coin stats when screen becomes visible (on-demand instead of real-time listener)
    LaunchedEffect(Unit) {
        viewModel.refreshCoinStats()
    }

    // Auto-dismiss error after 3 seconds
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            kotlinx.coroutines.delay(UiConstants.ERROR_AUTO_DISMISS_MS)
            viewModel.clearError()
        }
    }

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(t(UiTextKey.HistoryTabDiscrete), t(UiTextKey.HistoryTabContinuous))

    var showFilterDialog by remember { mutableStateOf(false) }
    var filterLanguageCode by remember { mutableStateOf("") }
    var filterKeyword by remember { mutableStateOf("") }
    var showCoinRulesDialog by remember { mutableStateOf(false) }
    var showHistoryInfoDialog by remember { mutableStateOf(false) }

    // History limit from user settings (default if not available)
    val historyLimit = uiState.historyViewLimit

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
                haptic.reject()
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
                haptic.confirm()
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
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

    StandardScreenScaffold(
        title = t(UiTextKey.HistoryTitle),
        onBack = {
            if (selectedTab == 1 && selectedSessionId != null) selectedSessionId = null
            else onBack()
        },
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Info button - shows history limit info
                IconButton(onClick = { showHistoryInfoDialog = true }) {
                    Icon(Icons.Default.Info, contentDescription = "History Info")
                }

                // Coin button - opens dialog with coin count and rules
                IconButton(onClick = { showCoinRulesDialog = true }) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = "Coins")
                }

                TextButton(onClick = { showFilterDialog = true }) { Text(t(UiTextKey.FilterHistoryScreenTitle)) }
            }
        },
    ) { innerPadding ->
        // History Info Dialog - shows limit and bookmark reminder
        if (showHistoryInfoDialog) {
            AlertDialog(
                onDismissRequest = { showHistoryInfoDialog = false },
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                title = { Text(t(UiTextKey.HistoryInfoTitle)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = t(UiTextKey.HistoryInfoLimitMessage).replace("{limit}", historyLimit.toString()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = t(UiTextKey.HistoryInfoOlderRecordsMessage),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = t(UiTextKey.HistoryInfoFavoritesMessage),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = t(UiTextKey.HistoryInfoViewFavoritesMessage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = t(UiTextKey.HistoryInfoFilterMessage).replace("{limit}", historyLimit.toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHistoryInfoDialog = false }) {
                        Text(t(UiTextKey.HistoryInfoGotItButton))
                    }
                }
            )
        }

        // Coin Rules Dialog - extracted to separate component
        CoinRulesDialog(
            isVisible = showCoinRulesDialog,
            onDismiss = { showCoinRulesDialog = false },
            coinTotal = uiState.coinStats.coinTotal,
            t = t
        )

        StandardScreenBody(
            innerPadding = innerPadding,
            scrollable = false,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
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
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(5) {
                                TranslationCardSkeleton()
                            }
                        }
                    }

                    uiState.error != null -> Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = uiState.error.orEmpty(),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.retryLoad() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(t(UiTextKey.ErrorRetryButton))
                            }
                        }
                    }

                    selectedTab == 0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            HistoryDiscreteTab(
                                records = discretePageRecords,
                                languageNameFor = uiLanguageNameFor,
                                speakingRecordId = speakingRecordId,
                                speakingType = speakingType,
                                isTtsRunning = uiState.isTtsRunning,
                                ttsStatus = uiState.ttsStatus,
                                noRecordsText = t(UiTextKey.HistoryNoDiscreteRecords),
                                onSpeakOriginal = { rec ->
                                    speakingRecordId = rec.id
                                    speakingType = "O"
                                    viewModel.speakTextOriginal(rec.sourceLang, rec.sourceText)
                                },
                                onSpeakTranslation = { rec ->
                                    speakingRecordId = rec.id
                                    speakingType = "T"
                                    viewModel.speakText(rec.targetLang, rec.targetText)
                                },
                                onDelete = { rec -> pendingDeleteRecord = rec },
                                onToggleFavorite = { rec -> viewModel.toggleFavorite(rec) },
                                favoritedTexts = uiState.favoritedTexts,
                                addingFavoriteId = uiState.addingFavoriteId,
                                deleteLabel = t(UiTextKey.ActionDelete),
                                hasMoreRecords = false,
                                isLoadingMore = false,
                                totalRecordsCount = uiState.totalRecordsCount,
                                onLoadMore = { /* No load more — shows recent records only */ },
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
                                    isTtsRunning = uiState.isTtsRunning,
                                    ttsStatus = uiState.ttsStatus,
                                    onSpeakOriginal = { rec ->
                                        speakingRecordId = rec.id
                                        speakingType = "O"
                                        viewModel.speakTextOriginal(rec.sourceLang, rec.sourceText)
                                    },
                                    onSpeakTranslation = { rec ->
                                        speakingRecordId = rec.id
                                        speakingType = "T"
                                        viewModel.speakText(rec.targetLang, rec.targetText)
                                    },
                                    onDelete = { rec -> pendingDeleteRecord = rec },
                                    onToggleFavorite = { rec -> viewModel.toggleFavorite(rec) },
                                    favoritedTexts = uiState.favoritedTexts,
                                    addingFavoriteId = uiState.addingFavoriteId,
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