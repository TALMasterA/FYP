package com.example.fyp.screens.wordbank

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.data.azure.AzureLanguageConfig
import com.example.fyp.domain.learning.GenerationEligibility
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

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
            if (selectedLanguage != null || uiState.isCustomWordBankSelected) {
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
                        t = t
                    )
                }
            }
        }
    }
}
