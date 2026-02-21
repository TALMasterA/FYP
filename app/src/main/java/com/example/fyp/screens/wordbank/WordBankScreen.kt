package com.example.fyp.screens.wordbank

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.components.FriendSelectorDialog
import com.example.fyp.ui.components.WordBankItemSkeleton

// Word Bank Screen - Main entry point for word bank feature
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

    // Use cached languages from ViewModel instead of loading on composition
    val supportedLanguages = viewModel.supportedLanguages

    // Track current primary language in viewModel
    var currentPrimaryCode by remember { mutableStateOf(primaryLanguageCode) }

    // Info dialog state
    var showInfoDialog by remember { mutableStateOf(false) }

    // Set primary language code on first load
    LaunchedEffect(primaryLanguageCode) {
        viewModel.setPrimaryLanguageCode(primaryLanguageCode)
        currentPrimaryCode = primaryLanguageCode
    }

    val selectedLanguage = uiState.selectedLanguageCode
    val currentWordBank = uiState.currentWordBank

    // Info dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(t(UiTextKey.WordBankInfoTitle)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(t(UiTextKey.WordBankInfoMessage))
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(t(UiTextKey.WordBankInfoGotItButton))
                }
            }
        )
    }

    StandardScreenScaffold(
        title = t(UiTextKey.WordBankTitle),
        onBack = {
            if (selectedLanguage != null || uiState.isCustomWordBankSelected) {
                viewModel.clearSelection()
            } else {
                onBack()
            }
        },
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            IconButton(onClick = { showInfoDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = t(UiTextKey.WordBankInfoTitle),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && !uiState.isGenerating -> {
                    // Show loading skeletons
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(5) {
                            WordBankItemSkeleton()
                        }
                    }
                }
                uiState.error != null && selectedLanguage == null && !uiState.isCustomWordBankSelected && !uiState.isGenerating -> {
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
                uiState.isCustomWordBankSelected -> {
                    // Show custom word bank view
                    CustomWordBankView(
                        customWords = uiState.customWords,
                        isSpeaking = uiState.isSpeaking,
                        speakingItemId = uiState.speakingItemId,
                        speakingType = uiState.speakingType,
                        isTranslating = uiState.isTranslatingCustomWord,
                        onSpeakWord = { word, type -> viewModel.speakWord(word, type) },
                        onDeleteWord = { word ->
                            val realId = word.id.removePrefix("custom_")
                            viewModel.deleteCustomWord(realId)
                        },
                        onAddWord = { original, translated, pronunciation, example, sourceLang, targetLang ->
                            viewModel.addCustomWord(
                                originalWord = original,
                                translatedWord = translated,
                                pronunciation = pronunciation,
                                example = example,
                                sourceLang = sourceLang,
                                targetLang = targetLang
                            )
                        },
                        onTranslate = { text, sourceLang, targetLang, onResult ->
                            viewModel.translateForCustomWord(text, sourceLang, targetLang, onResult)
                        },
                        supportedLanguages = supportedLanguages,
                        uiLanguageNameFor = uiLanguageNameFor,
                        t = t
                    )
                }
                selectedLanguage != null -> {
                    val canRegen = viewModel.canRegenerate(selectedLanguage)
                    val newRecordCount = viewModel.getNewRecordCount(selectedLanguage)
                    val currentHistoryCount = viewModel.getCurrentHistoryCount(selectedLanguage)

                    // Filter and pagination state
                    var filterKeyword by remember { mutableStateOf("") }
                    var filterCategory by remember { mutableStateOf("") }
                    var filterDifficulty by remember { mutableStateOf("") }
                    var wordBankPage by remember { mutableIntStateOf(0) }
                    val pageSize = 10

                    // Share: show friend selector when a word is pending share
                    if (uiState.pendingShareWord != null) {
                        FriendSelectorDialog(
                            friends = uiState.friends,
                            isLoading = uiState.isSharing,
                            t = t,
                            onFriendSelected = { friendId ->
                                viewModel.shareWord(uiState.pendingShareWord!!, friendId)
                            },
                            onDismiss = { viewModel.setPendingShareWord(null) }
                        )
                    }

                    // Share feedback
                    uiState.shareSuccess?.let { msg ->
                        LaunchedEffect(msg) {
                            kotlinx.coroutines.delay(3000)
                            viewModel.clearShareMessages()
                        }
                        Snackbar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) { Text(msg) }
                    }
                    uiState.shareError?.let { err ->
                        LaunchedEffect(err) {
                            kotlinx.coroutines.delay(3000)
                            viewModel.clearShareMessages()
                        }
                        Snackbar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ) { Text(err) }
                    }

                    WordBankDetailView(
                        languageName = uiLanguageNameFor(selectedLanguage),
                        wordBank = currentWordBank,
                        isGenerating = uiState.isGenerating,
                        canRegenerate = canRegen,
                        newRecordCount = newRecordCount,
                        minRecordsForRegen = GenerationEligibility.MIN_RECORDS_FOR_REGEN,
                        currentHistoryCount = currentHistoryCount,
                        isSpeaking = uiState.isSpeaking,
                        speakingItemId = uiState.speakingItemId,
                        speakingType = uiState.speakingType,
                        error = uiState.error,
                        onGenerate = { viewModel.generateWordBank(selectedLanguage) },
                        onCancel = { viewModel.cancelGeneration() },
                        onSpeakWord = { word, type -> viewModel.speakWord(word, type) },
                        onSpeakExample = { word -> viewModel.speakExample(word) },
                        onDeleteWord = { word ->
                            viewModel.deleteWordFromBank(word.id, selectedLanguage)
                        },
                        onShareWord = { word -> viewModel.setPendingShareWord(word) },
                        t = t,
                        filterKeyword = filterKeyword,
                        onFilterKeywordChange = { filterKeyword = it },
                        filterCategory = filterCategory,
                        onFilterCategoryChange = { filterCategory = it },
                        filterDifficulty = filterDifficulty,
                        onFilterDifficultyChange = { filterDifficulty = it },
                        currentPage = wordBankPage,
                        onPageChange = { wordBankPage = it },
                        pageSize = pageSize
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
                        customWordsCount = uiState.customWordsCount,
                        onSelectCustomWordBank = { viewModel.selectCustomWordBank() },
                        onRefresh = { viewModel.refreshLanguageCounts() },
                        t = t
                    )
                }
            }
        }
    }
}
