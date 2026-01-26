package com.example.fyp.screens.learning

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.BaseUiTexts
import com.example.fyp.model.UiTextKey

@Composable
fun QuizScreen(
    appLanguageState: AppLanguageState,
    primaryCode: String,
    targetCode: String,
    onBack: () -> Unit,
    viewModel: LearningSheetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    val targetName = targetCode

    // Debug-only: allow expanding to view full saved content
    var debugShowFullContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initializeQuiz()
    }

    StandardScreenScaffold(
        title = "Quiz: $targetName",
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        when {
            uiState.quizLoading && uiState.currentAttempt == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.quizError != null && uiState.quizQuestions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "\u26A0\uFE0F Quiz Error",
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
                        "Suggestion: Go back and try re-generating the learning materials.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                Log.d("QuizDebug", "User tapped Retry")
                                viewModel.initializeQuiz()
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }

                        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Go Back")
                        }
                    }
                }
            }

            uiState.isQuizTaken && uiState.currentAttempt != null -> {
                QuizResultsScreen(
                    attempt = uiState.currentAttempt!!,
                    onRetake = { viewModel.resetQuiz(); viewModel.initializeQuiz() },
                    onBack = onBack
                )
            }

            uiState.currentAttempt != null -> {
                QuizTakingScreen(
                    attempt = uiState.currentAttempt!!,
                    onAnswerSelected = viewModel::recordQuizAnswer,
                    onSubmit = viewModel::submitQuiz,
                    isLoading = uiState.quizLoading,
                    appLanguageState = appLanguageState
                )
            }

            // If the sheet content is missing (and not currently loading), show a clear message
            uiState.content.isNullOrBlank() && !uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No learning materials found",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        "Please go back and generate the learning materials before viewing the quiz.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Back")
                        }
                    }
                }
            }

            // Default loading state (content is being loaded)
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Loading quiz... (You may need to re-generate the materials, if there is no response for a long time.)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizTakingScreen(
    attempt: com.example.fyp.model.QuizAttempt,
    onAnswerSelected: (String, Int) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    appLanguageState: AppLanguageState,
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val currentQuestion = attempt.questions.getOrNull(currentQuestionIndex)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                "Question ${currentQuestionIndex + 1} of ${attempt.questions.size}",
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

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                enabled = currentQuestionIndex > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text("Previous")
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
                    if (currentQuestionIndex == attempt.questions.size - 1) "Submit" else "Next"
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
    attempt: com.example.fyp.model.QuizAttempt,
    onRetake: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Score display
        Text(
            "Quiz Completed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Score card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "${attempt.totalScore}/${attempt.maxScore}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    "%.1f%%".format(attempt.percentage),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Review answers
        Text(
            "Answer Review",
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
                            "Your answer: ${question.options[selectedIndex]}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }

                    if (!isCorrect && question.correctOptionIndex < question.options.size) {
                        Text(
                            "Correct: ${question.options[question.correctOptionIndex]}",
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
                Text("Back")
            }

            Button(
                onClick = onRetake,
                modifier = Modifier.weight(1f)
            ) {
                Text("Retake Quiz")
            }
        }
    }
}