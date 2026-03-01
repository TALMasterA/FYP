package com.example.fyp.screens.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.PaginationRow
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.pageCount
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.core.UiConstants
import com.example.fyp.core.rememberHapticFeedback
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.FavoriteRecord
import com.example.fyp.model.FavoriteSession
import com.example.fyp.model.FavoriteSessionRecord
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.components.EmptyStates
import com.example.fyp.ui.components.EmptyStateView
import com.example.fyp.ui.components.TranslationCardSkeleton
import kotlinx.coroutines.delay

@Composable
fun FavoritesScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }
    val haptic = rememberHapticFeedback()

    // Tab state
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(t(UiTextKey.FavoritesTabRecords), t(UiTextKey.FavoritesTabSessions))

    // Records pagination
    var recordsPage by remember { mutableIntStateOf(0) }
    val pageSize = 10
    val recordsTotalPages = pageCount(uiState.favorites.size, pageSize)
    val pagedFavorites = uiState.favorites
        .drop(recordsPage * pageSize)
        .take(pageSize)

    // Sessions pagination
    var sessionsPage by remember { mutableIntStateOf(0) }
    val sessionsTotalPages = pageCount(uiState.sessions.size, pageSize)
    val pagedSessions = uiState.sessions
        .drop(sessionsPage * pageSize)
        .take(pageSize)

    // Delete mode confirmation dialog
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val totalSelected = uiState.selectedRecordIds.size + uiState.selectedSessionIds.size

    // Auto-dismiss error after delay
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            delay(UiConstants.ERROR_AUTO_DISMISS_MS)
            viewModel.clearError()
        }
    }

    // Delete confirm dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(t(UiTextKey.ActionDelete)) },
            text = { Text("Delete $totalSelected selected item(s)? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSelected()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(t(UiTextKey.ActionDelete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(t(UiTextKey.ActionCancel))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.FavoritesTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            if (uiState.isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = {
                    haptic.click()
                    when {
                        !uiState.isDeleteMode -> viewModel.toggleDeleteMode()
                        totalSelected > 0 -> showDeleteConfirmDialog = true
                        else -> viewModel.exitDeleteMode()
                    }
                }) {
                    Icon(
                        imageVector = if (uiState.isDeleteMode) Icons.Default.DeleteForever else Icons.Default.Delete,
                        contentDescription = if (uiState.isDeleteMode) "Confirm delete" else "Delete mode",
                        tint = if (uiState.isDeleteMode) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Error message card (auto-dismisses after 3 seconds)
            uiState.error?.let { errorMsg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            viewModel.exitDeleteMode()
                        },
                        text = { Text(label) },
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        repeat(3) {
                            TranslationCardSkeleton()
                        }
                    }
                }

                selectedTab == 0 -> {
                    // Records tab
                    if (uiState.favorites.isEmpty()) {
                        EmptyStates.NoFavorites(
                            message = t(UiTextKey.FavoritesEmpty),
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(pagedFavorites, key = { it.id }) { favorite ->
                                FavoriteCard(
                                    favorite = favorite,
                                    languageNameFor = uiLanguageNameFor,
                                    onSpeakSource = {
                                        haptic.click()
                                        viewModel.speak(favorite.sourceText, favorite.sourceLang, favorite.id + "_source")
                                    },
                                    onSpeakTarget = {
                                        haptic.click()
                                        viewModel.speak(favorite.targetText, favorite.targetLang, favorite.id + "_target")
                                    },
                                    isSpeakingSource = uiState.speakingId == (favorite.id + "_source"),
                                    isSpeakingTarget = uiState.speakingId == (favorite.id + "_target"),
                                    isDeleteMode = uiState.isDeleteMode,
                                    isSelected = favorite.id in uiState.selectedRecordIds,
                                    onToggleSelect = {
                                        haptic.click()
                                        viewModel.toggleRecordSelection(favorite.id)
                                    },
                                    t = t
                                )
                            }
                        }

                        if (uiState.hasMore && recordsPage == recordsTotalPages - 1) {
                            Button(
                                onClick = {
                                    viewModel.loadMoreFavorites()
                                    recordsPage = 0
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(text = "Load More")
                            }
                        }
                    }
                }

                else -> {
                    // Sessions tab (view-only)
                    if (uiState.sessions.isEmpty()) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyStateView(
                                icon = Icons.Filled.Forum,
                                title = t(UiTextKey.FavoritesSessionsEmpty),
                                message = t(UiTextKey.FavoritesSessionsEmpty),
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pagedSessions, key = { it.id }) { session ->
                                FavoriteSessionCard(
                                    session = session,
                                    itemsTemplate = t(UiTextKey.FavoritesSessionItemsTemplate),
                                    openLabel = t(UiTextKey.ActionOpen),
                                    speakingId = uiState.speakingId,
                                    isDeleteMode = uiState.isDeleteMode,
                                    isSelected = session.id in uiState.selectedSessionIds,
                                    onToggleSelect = {
                                        haptic.click()
                                        viewModel.toggleSessionSelection(session.id)
                                    },
                                    onSpeak = { text, lang, id ->
                                        haptic.click()
                                        viewModel.speak(text, lang, id)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Pagination controls
            val page = if (selectedTab == 0) recordsPage else sessionsPage
            val totalPages = if (selectedTab == 0) recordsTotalPages else sessionsTotalPages
            if (totalPages > 1) {
                PaginationRow(
                    page = page,
                    totalPages = totalPages,
                    prevLabel = uiText(UiTextKey.PaginationPrevLabel, BaseUiTexts[UiTextKey.PaginationPrevLabel.ordinal]),
                    nextLabel = uiText(UiTextKey.PaginationNextLabel, BaseUiTexts[UiTextKey.PaginationNextLabel.ordinal]),
                    pageLabelTemplate = uiText(
                        UiTextKey.PaginationPageLabelTemplate,
                        BaseUiTexts[UiTextKey.PaginationPageLabelTemplate.ordinal]
                    ),
                    onPrev = {
                        if (selectedTab == 0 && recordsPage > 0) recordsPage--
                        if (selectedTab == 1 && sessionsPage > 0) sessionsPage--
                    },
                    onNext = {
                        if (selectedTab == 0 && recordsPage < recordsTotalPages - 1) recordsPage++
                        if (selectedTab == 1 && sessionsPage < sessionsTotalPages - 1) sessionsPage++
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun FavoriteSessionCard(
    session: FavoriteSession,
    itemsTemplate: String,
    openLabel: String,
    speakingId: String?,
    isDeleteMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onSpeak: (text: String, lang: String, id: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                             else MaterialTheme.colorScheme.background
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Session header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox in delete mode
                if (isDeleteMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect() },
                        modifier = Modifier.size(40.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.sessionName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = itemsTemplate.replace("{count}", session.records.size.toString()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Open/Close toggle (hidden in delete mode)
                if (!isDeleteMode) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = openLabel
                        )
                    }
                }
            }

            // Expand to show conversation bubbles (view-only)
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    session.records.sortedBy { it.sequence }.forEachIndexed { index, record ->
                        FavoriteSessionBubble(
                            record = record,
                            sessionId = session.id,
                            index = index,
                            speakingId = speakingId,
                            onSpeak = onSpeak
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteSessionBubble(
    record: FavoriteSessionRecord,
    sessionId: String,
    index: Int,
    speakingId: String?,
    onSpeak: (text: String, lang: String, id: String) -> Unit
) {
    val isPersonA = record.speaker == "A"
    val alignment = if (isPersonA) Alignment.Start else Alignment.End
    val bubbleColor = if (isPersonA)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.tertiaryContainer

    val speakSourceId = "${sessionId}_${index}_source"
    val speakTargetId = "${sessionId}_${index}_target"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Speaker label
        Text(
            text = if (isPersonA) "Person A" else "Person B",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Source text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.sourceText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (record.sourceLang.isNotBlank()) {
                        IconButton(
                            onClick = { onSpeak(record.sourceText, record.sourceLang, speakSourceId) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak",
                                modifier = Modifier.size(16.dp),
                                tint = if (speakingId == speakSourceId) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Target text (translation)
                if (record.targetText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = record.targetText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        if (record.targetLang.isNotBlank()) {
                            IconButton(
                                onClick = { onSpeak(record.targetText, record.targetLang, speakTargetId) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speak translation",
                                    modifier = Modifier.size(16.dp),
                                    tint = if (speakingId == speakTargetId) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
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
fun FavoriteCard(
    favorite: FavoriteRecord,
    languageNameFor: (String) -> String,
    onSpeakSource: () -> Unit,
    onSpeakTarget: () -> Unit,
    isSpeakingSource: Boolean,
    isSpeakingTarget: Boolean,
    isDeleteMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    t: (UiTextKey) -> String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                             else MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row with language info and selection checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${languageNameFor(favorite.sourceLang)} → ${languageNameFor(favorite.targetLang)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                if (isDeleteMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect() },
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Source text with speak button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = favorite.sourceText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSpeakSource) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak source",
                        tint = if (isSpeakingSource) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Target text with speak button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = favorite.targetText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSpeakTarget) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak translation",
                        tint = if (isSpeakingTarget) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Note if present
            if (favorite.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📝 ${favorite.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
