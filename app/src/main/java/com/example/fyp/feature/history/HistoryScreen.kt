package com.example.fyp.feature.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.feature.speech.SpeechViewModel
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.TranslationRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val speechVm: SpeechViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsState()
    val (_, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Discrete", "Continuous")

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
            title = { Text("Delete record?") },
            text = { Text("This action cannot be undone.") },
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
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteRecord = null }) { Text("Cancel") }
            }
        )
    }

    if (pendingDeleteSessionId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteSessionId = null },
            title = { Text("Delete session?") },
            text = { Text("All records in this session will be deleted. This action cannot be undone.") },
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
                ) { Text("Delete session") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSessionId = null }) { Text("Cancel") }
            }
        )
    }

    if (pendingRenameSessionId != null) {
        AlertDialog(
            onDismissRequest = { pendingRenameSessionId = null },
            title = { Text("Name this session") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Session name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameSession(pendingRenameSessionId!!, renameText.trim())
                        pendingRenameSessionId = null
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRenameSessionId = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedTab == 1 && selectedSessionId != null) {
                            selectedSessionId = null
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
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
                        modifier = Modifier.padding(16.dp)
                    )
                }

                selectedTab == 0 -> {
                    HistoryList(
                        records = discreteRecords,
                        languageNameFor = uiLanguageNameFor,
                        onSpeakOriginal = { rec -> speechVm.speakTextOriginal(rec.sourceLang, rec.sourceText) },
                        onSpeakTranslation = { rec -> speechVm.speakText(rec.targetLang, rec.targetText) },
                        onDelete = { rec -> pendingDeleteRecord = rec }
                    )
                }

                else -> {
                    if (selectedSessionId == null) {
                        if (sessions.isEmpty()) {
                            Text("No continuous sessions yet.", modifier = Modifier.padding(16.dp))
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(sessions, key = { (sid, _) -> sid }) { (sid, records) ->
                                    val displayName = uiState.sessionNames[sid].orEmpty()
                                    val title = if (displayName.isNotBlank()) displayName else "Session ${sid.take(8)}"

                                    OutlinedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.outlinedCardColors(
                                            containerColor = MaterialTheme.colorScheme.background
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(title, style = MaterialTheme.typography.titleMedium)
                                            Text("${records.size} items")
                                            Spacer(Modifier.height(10.dp))

                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Button(onClick = { selectedSessionId = sid }) { Text("Open") }

                                                OutlinedButton(onClick = {
                                                    renameText = uiState.sessionNames[sid].orEmpty()
                                                    pendingRenameSessionId = sid
                                                }) { Text("Name") }

                                                Button(
                                                    onClick = { pendingDeleteSessionId = sid },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.error,
                                                        contentColor = MaterialTheme.colorScheme.onError
                                                    )
                                                ) { Text("Delete") }
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
                        val title = if (displayName.isNotBlank()) displayName else "Session ${selectedSessionId!!.take(8)}"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = { selectedSessionId = null }) { Text("Back") }
                        }

                        HistoryList(
                            records = sessionRecords,
                            languageNameFor = uiLanguageNameFor,
                            onSpeakOriginal = { rec -> speechVm.speakTextOriginal(rec.sourceLang, rec.sourceText) },
                            onSpeakTranslation = { rec -> speechVm.speakText(rec.targetLang, rec.targetText) },
                            onDelete = { rec -> pendingDeleteRecord = rec }
                        )
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
    onDelete: (TranslationRecord) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(records, key = { it.id }) { rec ->
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
                        Button(onClick = { onSpeakOriginal(rec) }) { Text("🗣️O") }      // original
                        Button(onClick = { onSpeakTranslation(rec) }) { Text("🔊T") }   // translation

                        Button(
                            onClick = { onDelete(rec) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) { Text("Delete") }
                    }

                }
            }
        }
    }
}