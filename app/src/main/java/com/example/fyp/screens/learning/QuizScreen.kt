package com.example.fyp.screens.learning

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey
import androidx.compose.foundation.layout.navigationBarsPadding

@Suppress("UNUSED_PARAMETER")
@Composable
fun QuizScreen(
    appLanguageState: AppLanguageState,
    primaryCode: String,
    targetCode: String,
    onBack: () -> Unit,
    learningViewModel: LearningViewModel,
    viewModel: LearningSheetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val learningUiState by learningViewModel.uiState.collectAsState()
    val (uiText, uiLanguageNameFor) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val targetName = uiLanguageNameFor(targetCode)

    // Quiz generation state from LearningViewModel (survives navigation)
    val isGeneratingQuiz = learningUiState.generatingQuizLanguageCode == targetCode
    val isGeneratingAnyQuiz = learningUiState.generatingQuizLanguageCode != null
    val isGeneratingMaterials = learningUiState.generatingLanguageCode != null
    val isAnyGenerationOngoing = isGeneratingAnyQuiz || isGeneratingMaterials

    val lastQuizCount = learningUiState.quizCountByLanguage[targetCode]
    val sheetHistoryCount = uiState.historyCountAtGenerate

    // Check if can earn coins (materials > quiz by 10+, or first quiz)
    val canEarnCoinsOnRegen = lastQuizCount == null ||
        (sheetHistoryCount != null && sheetHistoryCount >= lastQuizCount + 10)

    // Anti-cheat: Disable generate button if:
    // 1. Any generation is ongoing (materials or quiz)
    // 2. Sheet not loaded
    // 3. Quiz count matches sheet count (no new content)
    // 4. Sheet count is LOWER than quiz count (user deleted history - cheating attempt)
    val sheetLowerThanQuiz = lastQuizCount != null && sheetHistoryCount != null && sheetHistoryCount < lastQuizCount
    val quizGenEnabled = !isAnyGenerationOngoing &&
            sheetHistoryCount != null &&
            (lastQuizCount == null || lastQuizCount != sheetHistoryCount) &&
            !sheetLowerThanQuiz &&
            !uiState.content.isNullOrBlank()

    // Show coins alert when quiz is taken and has coin message
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

    val regenerateButton: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show counts info with coin eligibility
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Materials: ${sheetHistoryCount ?: "-"} | Quiz: ${lastQuizCount ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Coin eligibility hint
                    if (quizGenEnabled) {
                        Text(
                            if (canEarnCoinsOnRegen) "🪙 Can earn coins!" else "🪙 Need ${(lastQuizCount ?: 0) + 10 - (sheetHistoryCount ?: 0)} more records for coins",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (canEarnCoinsOnRegen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Coin info button
                IconButton(onClick = { showCoinRulesDialog = true }) {
                    Icon(Icons.Default.Info, contentDescription = "Coin Rules", tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Warning if sheet count lower than quiz count (anti-cheat)
            if (sheetLowerThanQuiz) {
                Text(
                    "⚠️ Cannot regenerate: Materials ($sheetHistoryCount) < Quiz ($lastQuizCount). Add more translations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Warning if any other generation is ongoing
            if (isAnyGenerationOngoing && !isGeneratingQuiz) {
                Text(
                    "⏳ Another generation is in progress. Please wait.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        // Show coin policy alert before regenerating
                        showRegenCoinAlert = true
                    },
                    enabled = quizGenEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        when {
                            isGeneratingQuiz -> "⏳ Generating..."
                            isAnyGenerationOngoing -> "⏳ Wait..."
                            sheetLowerThanQuiz -> "🚫 Blocked"
                            lastQuizCount == sheetHistoryCount && lastQuizCount != null -> "✓ Up-to-date"
                            else -> "🔄 Generate Quiz"
                        }
                    )
                }

                Button(
                    onClick = { learningViewModel.cancelQuizGenerate() },
                    enabled = isGeneratingQuiz,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        }
    }

    StandardScreenScaffold(
        title = "Quiz: $targetName",
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        // Coin Rules Dialog
        if (showCoinRulesDialog) {
            AlertDialog(
                onDismissRequest = { showCoinRulesDialog = false },
                title = { Text(t(UiTextKey.QuizCoinRulesTitle)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(t(UiTextKey.QuizCoinRulesHowToEarn), style = MaterialTheme.typography.titleSmall)
                        Text(t(UiTextKey.QuizCoinRule1Coin), style = MaterialTheme.typography.bodySmall)
                        Text(t(UiTextKey.QuizCoinRuleFirstAttempt), style = MaterialTheme.typography.bodySmall)

                        Text(t(UiTextKey.QuizCoinRulesRequirements), style = MaterialTheme.typography.titleSmall)
                        Text(t(UiTextKey.QuizCoinRuleMatchMaterials), style = MaterialTheme.typography.bodySmall)
                        Text(t(UiTextKey.QuizCoinRulePlus10), style = MaterialTheme.typography.bodySmall)
                        Text(t(UiTextKey.QuizCoinRuleNoDelete), style = MaterialTheme.typography.bodySmall)

                        Text(t(UiTextKey.QuizCoinRulesCurrentStatus), style = MaterialTheme.typography.titleSmall)
                        Text(
                            t(UiTextKey.QuizCoinRuleMaterialsTemplate).replace("{count}", sheetHistoryCount?.toString() ?: "-"),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            t(UiTextKey.QuizCoinRuleQuizTemplate).replace("{count}", lastQuizCount?.toString() ?: t(UiTextKey.QuizRecordsLabel)),
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (canEarnCoinsOnRegen && quizGenEnabled) {
                            Text(t(UiTextKey.QuizCoinRulesCanEarn), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        } else if (lastQuizCount != null) {
                            val needed = (lastQuizCount + 10) - (sheetHistoryCount ?: 0)
                            if (needed > 0) {
                                Text(
                                    t(UiTextKey.QuizCoinRulesNeedMoreTemplate).replace("{count}", needed.toString()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showCoinRulesDialog = false }) {
                        Text(t(UiTextKey.QuizCoinRuleGotIt))
                    }
                }
            )
        }

        // Regen confirmation with coin policy
        if (showRegenCoinAlert) {
            AlertDialog(
                onDismissRequest = { showRegenCoinAlert = false },
                title = { Text(t(UiTextKey.QuizRegenConfirmTitle)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (canEarnCoinsOnRegen)
                                t(UiTextKey.QuizRegenCanEarnCoins)
                            else
                                t(UiTextKey.QuizRegenCannotEarnCoins),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (canEarnCoinsOnRegen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )

                        if (!canEarnCoinsOnRegen && lastQuizCount != null) {
                            val needed = (lastQuizCount + 10) - (sheetHistoryCount ?: 0)
                            Text(
                                t(UiTextKey.QuizRegenNeedMoreTemplate).replace("{count}", needed.toString()),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            t(UiTextKey.QuizRegenReminder),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showRegenCoinAlert = false
                        val content = uiState.content.orEmpty()
                        val historyCount = uiState.historyCountAtGenerate ?: 0
                        learningViewModel.generateQuizFor(targetCode, content, historyCount)
                    }) {
                        Text(t(UiTextKey.QuizRegenGenerateButton))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRegenCoinAlert = false }) {
                        Text(t(UiTextKey.QuizCancelButton))
                    }
                }
            )
        }

        // Coins earned alert - only shows when coins were actually awarded
        if (showCoinsAlert && uiState.currentAttempt != null) {
            AlertDialog(
                onDismissRequest = { showCoinsAlert = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(t(UiTextKey.QuizCoinsEarnedTitle))
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            t(UiTextKey.QuizCoinsEarnedMessageTemplate).replace("{coins}", uiState.currentAttempt?.totalScore.toString()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text("\n📜 " + t(UiTextKey.QuizCoinRulesTitle), style = MaterialTheme.typography.labelMedium)
                        Text(t(UiTextKey.QuizCoinsRule1), style = MaterialTheme.typography.bodySmall)
                        Text(t(UiTextKey.QuizCoinsRule2), style = MaterialTheme.typography.bodySmall)
                        Text(t(UiTextKey.QuizCoinsRule3), style = MaterialTheme.typography.bodySmall)
                        Text(t(UiTextKey.QuizCoinsRule4), style = MaterialTheme.typography.bodySmall)
                        Text(t(UiTextKey.QuizCoinsRule5), style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = {
                    Button(onClick = { showCoinsAlert = false }) {
                        Text(t(UiTextKey.QuizCoinsGreatButton))
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            regenerateButton()

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

                uiState.currentAttempt != null -> {
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

                uiState.quizError != null && uiState.quizQuestions.isEmpty() -> {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            t(UiTextKey.QuizErrorTitle),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            uiState.quizError ?: "Unknown error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Text(
                            t(UiTextKey.QuizErrorSuggestion),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
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
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            t(UiTextKey.QuizNoMaterialsMessage),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }
                }

                // Default loading state (content is being loaded)
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

@Composable
private fun QuizTakingScreen(
    modifier: Modifier = Modifier,
    attempt: com.example.fyp.model.QuizAttempt,
    onAnswerSelected: (String, Int) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    appLanguageState: AppLanguageState,
    errorMessage: String? = null,
    isQuizOutdated: Boolean,
    onRegenerate: () -> Unit,
    regenEnabled: Boolean = true,
) {
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val currentQuestion = attempt.questions.getOrNull(currentQuestionIndex)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!errorMessage.isNullOrBlank()) {
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (isQuizOutdated) {
            Text(
                t(UiTextKey.QuizOutdatedMessage),
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Progress bar
        if (attempt.questions.isNotEmpty()) {
            val progress = (currentQuestionIndex + 1).toFloat() / attempt.questions.size.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Text(
                t(UiTextKey.QuizQuestionTemplate)
                    .replace("{current}", (currentQuestionIndex + 1).toString())
                    .replace("{total}", attempt.questions.size.toString()),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }

        // Question content
        if (currentQuestion != null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        currentQuestion.question,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                itemsIndexed(currentQuestion.options) { index, option ->
                    QuestionOptionButton(
                        option = option,
                        isSelected = attempt.answers.find { it.questionId == currentQuestion.id }
                            ?.selectedOptionIndex == index,
                        isCorrect = null,
                        onClick = { onAnswerSelected(currentQuestion.id, index) }
                    )
                }
            }
        }

        // Navigation buttons - at bottom with safe area padding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                enabled = currentQuestionIndex > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text(t(UiTextKey.QuizPreviousButton))
            }

            Button(
                onClick = {
                    if (currentQuestionIndex < attempt.questions.size - 1) {
                        currentQuestionIndex++
                    } else {
                        onSubmit()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (currentQuestionIndex == attempt.questions.size - 1) t(UiTextKey.QuizSubmitButton) else t(UiTextKey.QuizNextButton)
                )
            }
        }
    }
}

@Composable
private fun QuestionOptionButton(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
) {
    val backgroundColor = when {
        isCorrect == true -> MaterialTheme.colorScheme.primaryContainer
        isCorrect == false -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isCorrect == true -> MaterialTheme.colorScheme.primary
        isCorrect == false -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isCorrect == null) { onClick() }
            .animateContentSize()
            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                option,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            if (isCorrect == true) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Correct",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else if (isCorrect == false) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Incorrect",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun QuizResultsScreen(
    modifier: Modifier = Modifier,
    attempt: com.example.fyp.model.QuizAttempt,
    onRetake: () -> Unit,
    onBack: () -> Unit,
    appLanguageState: AppLanguageState,
) {
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Score display
        Text(
            t(UiTextKey.QuizCompletedTitle),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // ...existing score card...

        // Review answers
        Text(
            t(UiTextKey.QuizAnswerReviewTitle),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        attempt.questions.forEach { question ->
            val answer = attempt.answers.find { it.questionId == question.id }
            val selectedIndex = answer?.selectedOptionIndex ?: -1
            val isCorrect = answer?.isCorrect ?: false

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        question.question,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (selectedIndex >= 0 && selectedIndex < question.options.size) {
                        Text(
                            t(UiTextKey.QuizYourAnswerTemplate).replace("{answer}", question.options[selectedIndex]),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }

                    if (!isCorrect && question.correctOptionIndex < question.options.size) {
                        Text(
                            t(UiTextKey.QuizCorrectAnswerTemplate).replace("{answer}", question.options[question.correctOptionIndex]),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (question.explanation.isNotEmpty()) {
                        Text(
                            question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(t(UiTextKey.QuizBackButton))
            }

            Button(
                onClick = onRetake,
                modifier = Modifier.weight(1f)
            ) {
                Text(t(UiTextKey.QuizRetakeButton))
            }
        }
    }
}