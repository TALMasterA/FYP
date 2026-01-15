package com.example.fyp.feature.history

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
    val sessions = groupContinuousSessions(uiState.records)

    var selectedSessionId by remember { mutableStateOf<String?>(null) }
    var pendingDeleteRecord by remember { mutableStateOf<TranslationRecord?>(null) }
    var pendingDeleteSessionId by remember { mutableStateOf<String?>(null) }
    var pendingRenameSessionId by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }

    var speakingRecordId by remember { mutableStateOf<String?>(null) }
    var speakingType by remember { mutableStateOf<String?>(null) } // "O" or "T"

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
                        },
                        text = { Text(label) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                    uiState.error != null -> Text(
                        text = uiState.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )

                    selectedTab == 0 -> {
                        HistoryDiscreteTab(
                            records = discreteRecords,
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
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        if (selectedSessionId == null) {
                            HistoryContinuousTab(
                                sessions = sessions,
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
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val sid = selectedSessionId.orEmpty()
                            val sessionRecords = sessions
                                .firstOrNull { it.sessionId == sid }
                                ?.records
                                ?.sortedBy { it.timestamp }
                                .orEmpty()

                            val displayName = uiState.sessionNames[sid].orEmpty()
                            val title =
                                if (displayName.isNotBlank()) displayName
                                else formatSessionTitle(
                                    template = t(UiTextKey.HistorySessionTitleTemplate),
                                    sessionId = sid
                                )

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

                                Spacer(Modifier.height(8.dp))

                                HistoryList(
                                    records = sessionRecords,
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