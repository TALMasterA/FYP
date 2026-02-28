package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

@Composable
fun QuizScreen(
    appLanguageState: AppLanguageState,
    primaryCode: String,
    targetCode: String,
    onBack: () -> Unit,
    learningViewModel: LearningViewModel,
    viewModel: LearningSheetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val learningUiState by learningViewModel.uiState.collectAsStateWithLifecycle()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val targetName = uiLanguageNameFor(targetCode)

    // Quiz generation state from LearningViewModel (survives navigation)
    val isGeneratingQuiz = learningUiState.generatingQuizLanguageCode == targetCode
    val isGeneratingAnyQuiz = learningUiState.generatingQuizLanguageCode != null
    val isGeneratingMaterials = learningUiState.generatingLanguageCode != null
    val isAnyGenerationOngoing = isGeneratingAnyQuiz || isGeneratingMaterials

    val currentQuizCount = learningUiState.quizCountByLanguage[targetCode]
    val lastAwardedQuizCount = learningUiState.lastAwardedQuizCountByLanguage[targetCode]
    val sheetHistoryCount = uiState.historyCountAtGenerate

    // Check if can earn coins: need 10+ more materials than last awarded quiz, or first quiz ever
    // This uses lastAwardedQuizCount (the count when coins were last awarded), NOT currentQuizCount
    val canEarnCoinsOnRegen = lastAwardedQuizCount == null ||
        (sheetHistoryCount != null && sheetHistoryCount >= lastAwardedQuizCount + 10)

    // Anti-cheat: Disable generate button based on multiple conditions
    val sheetLowerThanQuiz = currentQuizCount != null && sheetHistoryCount != null && sheetHistoryCount < currentQuizCount
    val quizGenEnabled = !isAnyGenerationOngoing &&
            sheetHistoryCount != null &&
            (currentQuizCount == null || currentQuizCount != sheetHistoryCount) &&
            !sheetLowerThanQuiz &&
            !uiState.content.isNullOrBlank()

    // Dialog visibility states
    var showCoinsAlert by remember { mutableStateOf(false) }
    var showCoinRulesDialog by remember { mutableStateOf(false) }
    var showRegenCoinAlert by remember { mutableStateOf(false) }

    // Show coins alert when quiz is taken and has coin message
    LaunchedEffect(uiState.isQuizTaken) {
        if (uiState.isQuizTaken && uiState.quizError?.contains("✨") == true) {
            showCoinsAlert = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSheet()
        viewModel.initializeQuiz()
    }

    // Reload quiz when generation finishes
    LaunchedEffect(learningUiState.quizCountByLanguage[targetCode]) {
        viewModel.initializeQuiz()
    }

    // Dialogs
    CoinRulesDialog(
        isVisible = showCoinRulesDialog,
        onDismiss = { showCoinRulesDialog = false },
        sheetHistoryCount = sheetHistoryCount,
        lastQuizCount = lastAwardedQuizCount,
        canEarnCoins = canEarnCoinsOnRegen && quizGenEnabled,
        appLanguageState = appLanguageState
    )

    QuizRegenConfirmDialog(
        isVisible = showRegenCoinAlert,
        onConfirm = {
            showRegenCoinAlert = false
            val content = uiState.content.orEmpty()
            val historyCount = uiState.historyCountAtGenerate ?: 0
            learningViewModel.generateQuizFor(targetCode, content, historyCount)
        },
        onDismiss = { showRegenCoinAlert = false },
        canEarnCoins = canEarnCoinsOnRegen,
        lastQuizCount = lastAwardedQuizCount,
        sheetHistoryCount = sheetHistoryCount,
        appLanguageState = appLanguageState
    )

    CoinsEarnedDialog(
        isVisible = showCoinsAlert,
        onDismiss = { showCoinsAlert = false },
        coinsEarned = uiState.currentAttempt?.totalScore ?: 0,
        appLanguageState = appLanguageState
    )

    StandardScreenScaffold(
        title = t(UiTextKey.QuizTitleTemplate).replace("{language}", targetName),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack),
        actions = {
            // Info icon in top right corner for coin rules
            IconButton(onClick = { showCoinRulesDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Coin Rules",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        // For QuizTakingScreen, we need it to fill the entire area so navigation can be at true bottom
        if (uiState.currentAttempt != null && !uiState.isQuizTaken) {
            // Only apply top padding from scaffold, let QuizTakingScreen handle bottom with navigationBarsPadding
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        PaddingValues(
                            top = padding.calculateTopPadding(),
                            start = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
            ) {
                // Regenerate button at top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    QuizRegenerateButton(
                        isGeneratingQuiz = isGeneratingQuiz,
                        isAnyGenerationOngoing = isAnyGenerationOngoing,
                        sheetLowerThanQuiz = sheetLowerThanQuiz,
                        quizGenEnabled = quizGenEnabled,
                        canEarnCoinsOnRegen = canEarnCoinsOnRegen,
                        onShowRegenConfirm = { showRegenCoinAlert = true },
                        onCancelGenerate = { learningViewModel.cancelQuizGenerate() },
                        appLanguageState = appLanguageState
                    )
                }

                QuizTakingScreen(
                    modifier = Modifier.weight(1f),
                    attempt = uiState.currentAttempt!!,
                    onAnswerSelected = viewModel::recordQuizAnswer,
                    onSubmit = viewModel::submitQuiz,
                    isLoading = uiState.quizLoading || isGeneratingQuiz,
                    appLanguageState = appLanguageState,
                    errorMessage = uiState.quizError,
                    isQuizOutdated = uiState.isQuizOutdated,
                    onRegenerate = {
                        val content = uiState.content.orEmpty()
                        val historyCount = uiState.historyCountAtGenerate ?: 0
                        learningViewModel.generateQuizFor(targetCode, content, historyCount)
                    },
                    regenEnabled = quizGenEnabled
                )
            }
        } else {
            // Other states use the original layout with padding
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Regenerate button (simplified - info moved to top bar)
                QuizRegenerateButton(
                    isGeneratingQuiz = isGeneratingQuiz,
                    isAnyGenerationOngoing = isAnyGenerationOngoing,
                    sheetLowerThanQuiz = sheetLowerThanQuiz,
                    quizGenEnabled = quizGenEnabled,
                    canEarnCoinsOnRegen = canEarnCoinsOnRegen,
                    onShowRegenConfirm = { showRegenCoinAlert = true },
                    onCancelGenerate = { learningViewModel.cancelQuizGenerate() },
                    appLanguageState = appLanguageState
                )

                // Quiz content based on state
                when {
                    uiState.isQuizTaken && uiState.currentAttempt != null -> {
                        QuizResultsScreen(
                            modifier = Modifier.weight(1f),
                            attempt = uiState.currentAttempt!!,
                            onRetake = { viewModel.resetQuiz(); viewModel.initializeQuiz() },
                            onBack = onBack,
                            appLanguageState = appLanguageState
                        )
                    }

                    uiState.quizError != null && uiState.quizQuestions.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    t(UiTextKey.QuizErrorTitle),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Text(
                                    uiState.quizError ?: "Unknown error",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                if (uiState.quizError?.contains("No quiz") == true) {
                                    Text(
                                        t(UiTextKey.QuizErrorSuggestion),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    uiState.content.isNullOrBlank() && !uiState.isLoading -> {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                t(UiTextKey.QuizNoMaterialsTitle),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Text(
                                t(UiTextKey.QuizNoMaterialsMessage),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                if (uiState.quizLoading) t(UiTextKey.QuizGeneratingText) else t(UiTextKey.QuizLoadingText),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
