package com.example.fyp.screens.wordbank

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fyp.core.PaginationRow
import com.example.fyp.core.pageCount
import com.example.fyp.model.ui.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBankDetailView(
    languageName: String,
    wordBank: WordBank?,
    isGenerating: Boolean,
    canRegenerate: Boolean,
    newRecordCount: Int,
    minRecordsForRegen: Int,
    isSpeaking: Boolean,
    speakingItemId: String?,
    speakingType: SpeakingType?,
    error: String?,
    onGenerate: () -> Unit,
    onCancel: () -> Unit,
    onSpeakWord: (WordBankItem, SpeakingType) -> Unit,
    onSpeakExample: (WordBankItem) -> Unit,
    onDeleteWord: (WordBankItem) -> Unit,
    t: (UiTextKey) -> String,
    filterKeyword: String,
    onFilterKeywordChange: (String) -> Unit,
    filterCategory: String,
    onFilterCategoryChange: (String) -> Unit,
    filterDifficulty: String,
    onFilterDifficultyChange: (String) -> Unit,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    pageSize: Int
) {
    var showFilterDialog by remember { mutableStateOf(false) }

    // Filter dialog
    if (showFilterDialog && wordBank != null) {
        WordBankFilterDialog(
            wordBank = wordBank,
            filterKeyword = filterKeyword,
            filterCategory = filterCategory,
            filterDifficulty = filterDifficulty,
            onFilterKeywordChange = onFilterKeywordChange,
            onFilterCategoryChange = onFilterCategoryChange,
            onFilterDifficultyChange = onFilterDifficultyChange,
            onApply = {
                showFilterDialog = false
                onPageChange(0)
            },
            onDismiss = { showFilterDialog = false },
            t = t
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with filters
        WordBankHeader(
            languageName = languageName,
            wordBank = wordBank,
            isGenerating = isGenerating,
            canRegenerate = canRegenerate,
            newRecordCount = newRecordCount,
            minRecordsForRegen = minRecordsForRegen,
            error = error,
            onGenerate = onGenerate,
            onCancel = onCancel,
            t = t,
            onShowFilterDialog = { showFilterDialog = true },
            hasActiveFilters = filterKeyword.isNotBlank() || filterCategory.isNotBlank() || filterDifficulty.isNotBlank()
        )

        // Word list with filter button
        if (wordBank == null || wordBank.words.isEmpty()) {
            WordBankEmptyState(t)
        } else {
            WordBankList(
                wordBank = wordBank,
                filterKeyword = filterKeyword,
                filterCategory = filterCategory,
                filterDifficulty = filterDifficulty,
                currentPage = currentPage,
                onPageChange = onPageChange,
                pageSize = pageSize,
                isSpeaking = isSpeaking,
                speakingItemId = speakingItemId,
                speakingType = speakingType,
                onSpeakWord = onSpeakWord,
                onSpeakExample = onSpeakExample,
                onDeleteWord = onDeleteWord,
                t = t
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordBankHeader(
    languageName: String,
    wordBank: WordBank?,
    isGenerating: Boolean,
    canRegenerate: Boolean,
    newRecordCount: Int,
    minRecordsForRegen: Int,
    error: String?,
    onGenerate: () -> Unit,
    onCancel: () -> Unit,
    t: (UiTextKey) -> String,
    onShowFilterDialog: () -> Unit = {},
    hasActiveFilters: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = if (isExpanded) 16.dp else 8.dp)
        ) {
            // Always visible: Language name with filter button and expand/collapse toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language name and word count (clickable to toggle expand)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = languageName,
                        style = if (isExpanded) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Show word count in collapsed state
                    if (!isExpanded && wordBank != null) {
                        Text(
                            text = " (${wordBank.words.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Filter and Refresh buttons (always visible)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (wordBank != null && wordBank.words.isNotEmpty()) {
                        IconButton(onClick = onShowFilterDialog) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = if (hasActiveFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Compact refresh/generate button when collapsed
                    if (!isExpanded && !isGenerating) {
                        val isFirstGeneration = wordBank == null
                        val buttonEnabled = isFirstGeneration || canRegenerate
                        IconButton(
                            onClick = onGenerate,
                            enabled = buttonEnabled
                        ) {
                            Icon(
                                imageVector = if (isFirstGeneration) Icons.Default.AutoAwesome else Icons.Default.Refresh,
                                contentDescription = if (isFirstGeneration) t(UiTextKey.WordBankGenerate) else t(UiTextKey.WordBankRefresh),
                                tint = if (buttonEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    } else if (!isExpanded && isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            // Expanded content
            if (isExpanded) {
                if (wordBank != null) {
                    Text(
                        text = "${wordBank.words.size} ${t(UiTextKey.WordBankWordsCount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Show regeneration status for existing word banks
                if (wordBank != null && !isGenerating) {
                    if (canRegenerate) {
                        Text(
                            text = "+$newRecordCount new records - ${t(UiTextKey.WordBankRefreshAvailable)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "+$newRecordCount / $minRecordsForRegen ${t(UiTextKey.WordBankRecordsNeeded)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Generate/Refresh button (full width when expanded)
                if (isGenerating) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(t(UiTextKey.WordBankGenerating))
                        }

                        OutlinedButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    val isFirstGeneration = wordBank == null
                    val buttonEnabled = isFirstGeneration || canRegenerate

                    Button(
                        onClick = onGenerate,
                        enabled = buttonEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (isFirstGeneration) Icons.Default.AutoAwesome else Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isFirstGeneration) t(UiTextKey.WordBankGenerate)
                            else t(UiTextKey.WordBankRefresh)
                        )
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun WordBankEmptyState(t: (UiTextKey) -> String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = t(UiTextKey.WordBankEmpty),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = t(UiTextKey.WordBankEmptyHint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WordBankList(
    wordBank: WordBank,
    filterKeyword: String,
    filterCategory: String,
    filterDifficulty: String,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    pageSize: Int,
    isSpeaking: Boolean,
    speakingItemId: String?,
    speakingType: SpeakingType?,
    onSpeakWord: (WordBankItem, SpeakingType) -> Unit,
    onSpeakExample: (WordBankItem) -> Unit,
    onDeleteWord: (WordBankItem) -> Unit,
    t: (UiTextKey) -> String
) {
    // Apply filters
    val filteredWords = wordBank.words.filter { word ->
        val keywordMatch = filterKeyword.isBlank() ||
                word.originalWord.contains(filterKeyword, ignoreCase = true) ||
                word.translatedWord.contains(filterKeyword, ignoreCase = true) ||
                word.example.contains(filterKeyword, ignoreCase = true)

        val categoryMatch = filterCategory.isBlank() ||
                word.category.equals(filterCategory, ignoreCase = true)

        val difficultyMatch = filterDifficulty.isBlank() ||
                word.difficulty.equals(filterDifficulty, ignoreCase = true)

        keywordMatch && categoryMatch && difficultyMatch
    }

    val totalPages = pageCount(filteredWords.size, pageSize)
    val pageWords = filteredWords.drop(currentPage * pageSize).take(pageSize)


    if (filteredWords.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = t(UiTextKey.WordBankFilterNoResults),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pageWords, key = { it.id }) { word ->
                    WordBankItemCard(
                        word = word,
                        isSpeaking = isSpeaking && speakingItemId == word.id,
                        speakingType = if (speakingItemId == word.id) speakingType else null,
                        onSpeakOriginal = { onSpeakWord(word, SpeakingType.ORIGINAL) },
                        onSpeakTranslated = { onSpeakWord(word, SpeakingType.TRANSLATED) },
                        onSpeakExample = { onSpeakExample(word) },
                        onDelete = { onDeleteWord(word) },
                        t = t
                    )
                }
            }

            // Pagination
            if (totalPages > 1) {
                PaginationRow(
                    page = currentPage,
                    totalPages = totalPages,
                    prevLabel = "< Prev",
                    nextLabel = "Next >",
                    pageLabelTemplate = "Page {page} of {total}",
                    onPrev = { if (currentPage > 0) onPageChange(currentPage - 1) },
                    onNext = { if (currentPage < totalPages - 1) onPageChange(currentPage + 1) },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordBankFilterDialog(
    wordBank: WordBank,
    filterKeyword: String,
    filterCategory: String,
    filterDifficulty: String,
    onFilterKeywordChange: (String) -> Unit,
    onFilterCategoryChange: (String) -> Unit,
    onFilterDifficultyChange: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    t: (UiTextKey) -> String
) {
    val categories = remember(wordBank) {
        wordBank.words
            .map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    val difficulties = remember(wordBank) {
        wordBank.words
            .map { it.difficulty }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    var draftKeyword by remember { mutableStateOf(filterKeyword) }
    var draftCategory by remember { mutableStateOf(filterCategory) }
    var draftDifficulty by remember { mutableStateOf(filterDifficulty) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.FilterTitle)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Category dropdown
                if (categories.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = if (draftCategory.isBlank()) t(UiTextKey.WordBankFilterCategoryAll) else draftCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(t(UiTextKey.WordBankFilterCategory)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(t(UiTextKey.WordBankFilterCategoryAll)) },
                                onClick = {
                                    draftCategory = ""
                                    expanded = false
                                }
                            )
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        draftCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Difficulty level filter chips
                if (difficulties.isNotEmpty()) {
                    Column {
                        Text(
                            text = t(UiTextKey.WordBankFilterDifficultyLabel),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            difficulties.forEach { difficulty ->
                                FilterChip(
                                    selected = draftDifficulty.equals(difficulty, ignoreCase = true),
                                    onClick = {
                                        draftDifficulty = if (draftDifficulty.equals(difficulty, ignoreCase = true)) "" else difficulty
                                    },
                                    label = { Text(difficulty.replaceFirstChar { it.uppercase() }) }
                                )
                            }

                            // Clear button if filter is active
                            if (draftDifficulty.isNotBlank()) {
                                FilterChip(
                                    selected = false,
                                    onClick = { draftDifficulty = "" },
                                    label = { Text("All") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Keyword filter
                OutlinedTextField(
                    value = draftKeyword,
                    onValueChange = { draftKeyword = it },
                    label = { Text(t(UiTextKey.FilterKeyword)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onFilterKeywordChange(draftKeyword)
                onFilterCategoryChange(draftCategory)
                onFilterDifficultyChange(draftDifficulty)
                onApply()
            }) {
                Text(t(UiTextKey.FilterApply))
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        draftKeyword = ""
                        draftCategory = ""
                        draftDifficulty = ""
                        onFilterKeywordChange("")
                        onFilterCategoryChange("")
                        onFilterDifficultyChange("")
                        onApply()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(t(UiTextKey.FilterClear))
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(t(UiTextKey.FilterCancel))
                }
            }
        }
    )
}
