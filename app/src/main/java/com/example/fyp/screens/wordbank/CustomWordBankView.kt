package com.example.fyp.screens.wordbank

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.PaginationRow
import com.example.fyp.core.pageCount
import com.example.fyp.model.ui.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWordBankView(
    customWords: List<WordBankItem>,
    isSpeaking: Boolean,
    speakingItemId: String?,
    speakingType: SpeakingType?,
    isTranslating: Boolean,
    onSpeakWord: (WordBankItem, SpeakingType) -> Unit,
    onDeleteWord: (WordBankItem) -> Unit,
    onAddWord: (original: String, translated: String, pronunciation: String, example: String, sourceLang: String, targetLang: String) -> Unit,
    onTranslate: (text: String, sourceLang: String, targetLang: String, onResult: (String) -> Unit) -> Unit,
    supportedLanguages: List<String>,
    uiLanguageNameFor: (String) -> String,
    t: (UiTextKey) -> String
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(0) }
    val pageSize = 10

    // Filter state
    var filterKeyword by remember { mutableStateOf("") }

    // Filter words
    val filteredWords = remember(customWords, filterKeyword) {
        if (filterKeyword.isBlank()) {
            customWords
        } else {
            customWords.filter { word ->
                word.originalWord.contains(filterKeyword, ignoreCase = true) ||
                word.translatedWord.contains(filterKeyword, ignoreCase = true) ||
                word.category.contains(filterKeyword, ignoreCase = true)
            }
        }
    }

    // Paginate
    val totalPages = pageCount(filteredWords.size, pageSize)
    val paginatedWords = remember(filteredWords, currentPage) {
        filteredWords.drop(currentPage * pageSize).take(pageSize)
    }

    // Reset page when filter changes
    LaunchedEffect(filterKeyword) {
        currentPage = 0
    }

    if (showAddDialog) {
        AddCustomWordFullDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { original, translated, pronunciation, example, sourceLang, targetLang ->
                onAddWord(original, translated, pronunciation, example, sourceLang, targetLang)
                showAddDialog = false
            },
            onTranslate = onTranslate,
            isTranslating = isTranslating,
            supportedLanguages = supportedLanguages,
            uiLanguageNameFor = uiLanguageNameFor,
            t = t
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = t(UiTextKey.CustomWordsTitle),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = t(UiTextKey.CustomWordsAdd),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = "${customWords.size} ${t(UiTextKey.WordBankWordsCount)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search filter
        OutlinedTextField(
            value = filterKeyword,
            onValueChange = { filterKeyword = it },
            label = { Text(t(UiTextKey.CustomWordsSearchPlaceholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (filterKeyword.isNotBlank()) {
                    IconButton(onClick = { filterKeyword = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = t(UiTextKey.ActionCancel))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (customWords.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = t(UiTextKey.CustomWordsEmptyState),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = t(UiTextKey.CustomWordsEmptyHint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t(UiTextKey.CustomWordsAdd))
                    }
                }
            }
        } else if (filteredWords.isEmpty()) {
            // No results
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = t(UiTextKey.CustomWordsNoSearchResults),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Word list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(paginatedWords, key = { it.id }) { word ->
                    CustomWordCard(
                        word = word,
                        isSpeaking = isSpeaking && speakingItemId == word.id,
                        speakingType = if (speakingItemId == word.id) speakingType else null,
                        onSpeakOriginal = { onSpeakWord(word, SpeakingType.ORIGINAL) },
                        onSpeakTranslated = { onSpeakWord(word, SpeakingType.TRANSLATED) },
                        onDelete = { onDeleteWord(word) },
                        uiLanguageNameFor = uiLanguageNameFor,
                        t = t
                    )
                }
            }

            // Pagination
            if (totalPages > 1) {
                PaginationRow(
                    page = currentPage,
                    totalPages = totalPages,
                    prevLabel = t(UiTextKey.PaginationPrevLabel),
                    nextLabel = t(UiTextKey.PaginationNextLabel),
                    pageLabelTemplate = t(UiTextKey.PaginationPageLabelTemplate),
                    onPrev = { if (currentPage > 0) currentPage-- },
                    onNext = { if (currentPage < totalPages - 1) currentPage++ },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomWordCard(
    word: WordBankItem,
    isSpeaking: Boolean,
    speakingType: SpeakingType?,
    onSpeakOriginal: () -> Unit,
    onSpeakTranslated: () -> Unit,
    onDelete: () -> Unit,
    uiLanguageNameFor: (String) -> String,
    t: (UiTextKey) -> String
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Parse language codes from category and convert to names
    val languagePairDisplay = remember(word.category) {
        val parts = word.category.split(" → ")
        if (parts.size == 2) {
            val sourceName = uiLanguageNameFor(parts[0].trim())
            val targetName = uiLanguageNameFor(parts[1].trim())
            "$sourceName → $targetName"
        } else {
            word.category // Fallback to original if format is unexpected
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(t(UiTextKey.CustomWordsDelete)) },
            text = { Text(t(UiTextKey.DialogDeleteRecordMessage)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(t(UiTextKey.ActionDelete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(t(UiTextKey.ActionCancel))
                }
            }
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Language pair indicator
                    Text(
                        text = languagePairDisplay,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Original word
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = word.originalWord,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onSpeakOriginal,
                            enabled = !isSpeaking || speakingType != SpeakingType.ORIGINAL
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak original",
                                tint = if (isSpeaking && speakingType == SpeakingType.ORIGINAL)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Translated word
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = word.translatedWord,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onSpeakTranslated,
                            enabled = !isSpeaking || speakingType != SpeakingType.TRANSLATED
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak translated",
                                tint = if (isSpeaking && speakingType == SpeakingType.TRANSLATED)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Pronunciation if available
                    if (word.pronunciation.isNotBlank()) {
                        Text(
                            text = "[${word.pronunciation}]",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Example if available
                    if (word.example.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = word.example,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Delete icon button at top-right
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomWordFullDialog(
    onDismiss: () -> Unit,
    onConfirm: (original: String, translated: String, pronunciation: String, example: String, sourceLang: String, targetLang: String) -> Unit,
    onTranslate: (text: String, sourceLang: String, targetLang: String, onResult: (String) -> Unit) -> Unit,
    isTranslating: Boolean,
    supportedLanguages: List<String>,
    uiLanguageNameFor: (String) -> String,
    t: (UiTextKey) -> String
) {
    var originalWord by remember { mutableStateOf("") }
    var translatedWord by remember { mutableStateOf("") }
    var pronunciation by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var sourceLang by remember { mutableStateOf(supportedLanguages.firstOrNull() ?: "en-US") }
    var targetLang by remember { mutableStateOf(supportedLanguages.getOrNull(1) ?: "zh-CN") }

    val canSave = originalWord.isNotBlank() && translatedWord.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(UiTextKey.CustomWordsAdd)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Source language
                LanguageDropdownField(
                    label = t(UiTextKey.CustomWordsOriginalLanguageLabel),
                    selectedCode = sourceLang,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { sourceLang = it },
                    enabled = true
                )

                // Original word input
                OutlinedTextField(
                    value = originalWord,
                    onValueChange = { originalWord = it },
                    label = { Text(t(UiTextKey.CustomWordsOriginalLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (originalWord.isNotBlank() && !isTranslating) {
                            IconButton(
                                onClick = {
                                    onTranslate(originalWord, sourceLang, targetLang) { result ->
                                        translatedWord = result
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Translate, contentDescription = "Translate")
                            }
                        } else if (isTranslating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                )

                // Target language
                LanguageDropdownField(
                    label = t(UiTextKey.CustomWordsTranslationLanguageLabel),
                    selectedCode = targetLang,
                    options = supportedLanguages,
                    nameFor = uiLanguageNameFor,
                    onSelected = { targetLang = it },
                    enabled = true
                )

                // Translated word input
                OutlinedTextField(
                    value = translatedWord,
                    onValueChange = { translatedWord = it },
                    label = { Text(t(UiTextKey.CustomWordsTranslatedLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Pronunciation (optional)
                OutlinedTextField(
                    value = pronunciation,
                    onValueChange = { pronunciation = it },
                    label = { Text(t(UiTextKey.CustomWordsPronunciationLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Example (optional)
                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text(t(UiTextKey.CustomWordsExampleLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(originalWord.trim(), translatedWord.trim(), pronunciation.trim(), example.trim(), sourceLang, targetLang)
                },
                enabled = canSave
            ) {
                Text(t(UiTextKey.CustomWordsSaveButton))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t(UiTextKey.CustomWordsCancelButton))
            }
        }
    )
}
