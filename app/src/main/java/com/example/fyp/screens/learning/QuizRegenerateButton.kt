package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.core.rememberTranslator
import com.example.fyp.model.AppLanguageState
import com.example.fyp.model.UiTextKey

@Composable
fun QuizRegenerateButton(
    isGeneratingQuiz: Boolean,
    isAnyGenerationOngoing: Boolean,
    lastQuizCount: Int?,
    sheetHistoryCount: Int?,
    sheetLowerThanQuiz: Boolean,
    quizGenEnabled: Boolean,
    canEarnCoinsOnRegen: Boolean,
    onShowCoinRules: () -> Unit,
    onShowRegenConfirm: () -> Unit,
    onCancelGenerate: () -> Unit,
    appLanguageState: AppLanguageState,
) {
    val t = rememberTranslator(appLanguageState)

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
                    t(UiTextKey.QuizMaterialsQuizTemplate)
                        .replace("{materials}", sheetHistoryCount?.toString() ?: "-")
                        .replace("{quiz}", lastQuizCount?.toString() ?: "-"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Coin eligibility hint
                if (quizGenEnabled) {
                    val needed = (lastQuizCount ?: 0) + 10 - (sheetHistoryCount ?: 0)
                    Text(
                        if (canEarnCoinsOnRegen)
                            t(UiTextKey.QuizCanEarnCoins)
                        else
                            t(UiTextKey.QuizNeedMoreRecordsTemplate).replace("{count}", needed.toString()),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (canEarnCoinsOnRegen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Coin info button
            IconButton(onClick = onShowCoinRules) {
                Icon(Icons.Default.Info, contentDescription = "Coin Rules", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Warning if sheet count lower than quiz count (anti-cheat)
        if (sheetLowerThanQuiz) {
            Text(
                t(UiTextKey.QuizCannotRegenTemplate)
                    .replace("{materials}", sheetHistoryCount.toString())
                    .replace("{quiz}", lastQuizCount.toString()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        // Warning if any other generation is ongoing
        if (isAnyGenerationOngoing && !isGeneratingQuiz) {
            Text(
                t(UiTextKey.QuizAnotherGenInProgress),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onShowRegenConfirm,
                enabled = quizGenEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    when {
                        isGeneratingQuiz -> t(UiTextKey.QuizGenerating)
                        isAnyGenerationOngoing -> t(UiTextKey.QuizWait)
                        sheetLowerThanQuiz -> t(UiTextKey.QuizBlocked)
                        lastQuizCount == sheetHistoryCount && lastQuizCount != null -> t(UiTextKey.QuizUpToDate)
                        else -> t(UiTextKey.QuizGenerateButton)
                    }
                )
            }

            Button(
                onClick = onCancelGenerate,
                enabled = isGeneratingQuiz,
                modifier = Modifier.weight(1f)
            ) {
                Text(t(UiTextKey.QuizCancelButton))
            }
        }
    }
}
