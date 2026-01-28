@file:Suppress("AssignedValueIsNeverRead")

package com.example.fyp.screens.wordbank

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.LanguageDropdownField
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.config.AzureLanguageConfig
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBankScreen(
    viewModel: WordBankViewModel,
    appLanguageState: AppLanguageState,
    primaryLanguageCode: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val context = LocalContext.current
    val supportedLanguages = remember { AzureLanguageConfig.loadSupportedLanguages(context).toList() }

    // Track current primary language in viewModel
    var currentPrimaryCode by remember { mutableStateOf(primaryLanguageCode) }

    // Set primary language code on first load
    LaunchedEffect(primaryLanguageCode) {
        viewModel.setPrimaryLanguageCode(primaryLanguageCode)
        currentPrimaryCode = primaryLanguageCode
    }

    val selectedLanguage = uiState.selectedLanguageCode
    val currentWordBank = uiState.currentWordBank

    StandardScreenScaffold(
        title = t(UiTextKey.WordBankTitle),
        onBack = {
            if (selectedLanguage != null) {
                viewModel.clearSelection()
            } else {
                onBack()
            }
        },
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && !uiState.isGenerating -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null && selectedLanguage == null && !uiState.isGenerating -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                selectedLanguage != null -> {
                    val canRegen = viewModel.canRegenerate(selectedLanguage)
                    val newRecordCount = viewModel.getNewRecordCount(selectedLanguage)

                    WordBankDetailView(
                        languageCode = selectedLanguage,
                        languageName = uiLanguageNameFor(selectedLanguage),
                        wordBank = currentWordBank,
                        isGenerating = uiState.isGenerating,
                        canRegenerate = canRegen,
                        newRecordCount = newRecordCount,
                        minRecordsForRegen = WordBankViewModel.MIN_RECORDS_FOR_REGEN,
                        isSpeaking = uiState.isSpeaking,
                        speakingItemId = uiState.speakingItemId,
                        speakingType = uiState.speakingType,
                        error = uiState.error,
                        onGenerate = { viewModel.generateWordBank(selectedLanguage) },
                        onCancel = { viewModel.cancelGeneration() },
                        onSpeakWord = { word, type -> viewModel.speakWord(word, type) },
                        onSpeakExample = { word -> viewModel.speakExample(word) },
                        t = t
                    )
                }
                else -> {
                    LanguageSelectionView(
                        clusters = uiState.languageClusters,
                        uiLanguageNameFor = uiLanguageNameFor,
                        onSelectLanguage = { viewModel.selectLanguage(it) },
                        currentPrimaryCode = currentPrimaryCode,
                        supportedLanguages = supportedLanguages,
                        onPrimaryLanguageChange = { newCode ->
                            currentPrimaryCode = newCode
                            viewModel.setPrimaryLanguageCode(newCode)
                        },
                        t = t
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSelectionView(
    clusters: List<WordBankLanguageCluster>,
    uiLanguageNameFor: (String) -> String,
    onSelectLanguage: (String) -> Unit,
    currentPrimaryCode: String,
    supportedLanguages: List<String>,
    onPrimaryLanguageChange: (String) -> Unit,
    t: (UiTextKey) -> String
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Primary language dropdown
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LanguageDropdownField(
                label = t(UiTextKey.SettingsPrimaryLanguageLabel),
                selectedCode = currentPrimaryCode,
                options = supportedLanguages,
                nameFor = uiLanguageNameFor,
                onSelected = onPrimaryLanguageChange,
                enabled = true
            )
        }

        if (clusters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = t(UiTextKey.WordBankNoHistory),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = t(UiTextKey.WordBankNoHistoryHint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = t(UiTextKey.WordBankSelectLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(clusters) { cluster ->
                    LanguageClusterCard(
                        cluster = cluster,
                        languageName = uiLanguageNameFor(cluster.languageCode),
                        onClick = { onSelectLanguage(cluster.languageCode) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageClusterCard(
    cluster: WordBankLanguageCluster,
    languageName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = languageName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${cluster.recordCount} records",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (cluster.hasWordBank) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Word bank exists",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WordBankDetailView(
    languageCode: String,
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
    t: (UiTextKey) -> String
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = languageName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

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
                            text = "✅ +$newRecordCount new records - refresh available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "📊 +$newRecordCount / $minRecordsForRegen records needed to refresh",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Generate/Refresh button
                if (isGenerating) {
                    // Show generating state with cancel button
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
                    // Show generate or refresh button
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

        // Word list
        if (wordBank == null || wordBank.words.isEmpty()) {
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
                        imageVector = Icons.Default.LibraryBooks,
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wordBank.words, key = { it.id }) { word ->
                    WordBankItemCard(
                        word = word,
                        isSpeaking = isSpeaking && speakingItemId == word.id,
                        speakingType = if (speakingItemId == word.id) speakingType else null,
                        onSpeakOriginal = { onSpeakWord(word, SpeakingType.ORIGINAL) },
                        onSpeakTranslated = { onSpeakWord(word, SpeakingType.TRANSLATED) },
                        onSpeakExample = { onSpeakExample(word) },
                        t = t
                    )
                }
            }
        }
    }
}

@Composable
private fun WordBankItemCard(
    word: WordBankItem,
    isSpeaking: Boolean,
    speakingType: SpeakingType?,
    onSpeakOriginal: () -> Unit,
    onSpeakTranslated: () -> Unit,
    onSpeakExample: () -> Unit,
    t: (UiTextKey) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Main content row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Original word
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.originalWord,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        IconButton(
                            onClick = onSpeakOriginal,
                            enabled = !isSpeaking,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak original",
                                modifier = Modifier.size(20.dp),
                                tint = if (speakingType == SpeakingType.ORIGINAL)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Pronunciation
                    if (word.pronunciation.isNotBlank()) {
                        Text(
                            text = "[${word.pronunciation}]",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Translated word
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.translatedWord,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        IconButton(
                            onClick = onSpeakTranslated,
                            enabled = !isSpeaking,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak translation",
                                modifier = Modifier.size(20.dp),
                                tint = if (speakingType == SpeakingType.TRANSLATED)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Category badge
                if (word.category.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = word.category,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expanded content
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Example sentence
                    if (word.example.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = t(UiTextKey.WordBankExample),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onSpeakExample,
                                enabled = !isSpeaking,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speak example",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = word.example,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Difficulty
                    if (word.difficulty.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = t(UiTextKey.WordBankDifficulty),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DifficultyBadge(difficulty = word.difficulty)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (color, text) = when (difficulty.lowercase()) {
        "beginner" -> MaterialTheme.colorScheme.tertiary to difficulty
        "intermediate" -> MaterialTheme.colorScheme.secondary to difficulty
        "advanced" -> MaterialTheme.colorScheme.error to difficulty
        else -> MaterialTheme.colorScheme.outline to difficulty
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            color = color
        )
    }
}
