package com.example.fyp.screens.learning

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import com.example.fyp.core.rememberTranslator
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey

@Composable
fun QuizTakingScreen(
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
    val t = rememberTranslator(appLanguageState)

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val currentQuestion = attempt.questions.getOrNull(currentQuestionIndex)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp), // Space for bottom navigation
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!errorMessage.isNullOrBlank()) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
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
        }

        // Navigation buttons - anchored at bottom, just above device navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                enabled = currentQuestionIndex > 0,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = t(UiTextKey.QuizPreviousButton),
                )
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
            ) {
                Text(
                    if (currentQuestionIndex == attempt.questions.size - 1) t(UiTextKey.QuizSubmitButton) else t(UiTextKey.QuizNextButton)
                )
            }

            IconButton(
                onClick = { if (currentQuestionIndex < attempt.questions.size - 1) currentQuestionIndex++ },
                enabled = currentQuestionIndex < attempt.questions.size - 1,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = t(UiTextKey.QuizNextButton),
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
