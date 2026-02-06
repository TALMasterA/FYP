package com.example.fyp.screens.learning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fyp.core.rememberTranslator
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey

@Composable
fun QuizRegenerateButton(
    isGeneratingQuiz: Boolean,
    isAnyGenerationOngoing: Boolean,
    sheetLowerThanQuiz: Boolean,
    quizGenEnabled: Boolean,
    canEarnCoinsOnRegen: Boolean,
    onShowRegenConfirm: () -> Unit,
    onCancelGenerate: () -> Unit,
    appLanguageState: AppLanguageState,
) {
    val t = rememberTranslator(appLanguageState)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Coin eligibility hint (compact)
        if (quizGenEnabled) {
            Text(
                if (canEarnCoinsOnRegen)
                    t(UiTextKey.QuizCanEarnCoins)
                else
                    t(UiTextKey.QuizRegenCannotEarnCoins),
                style = MaterialTheme.typography.labelSmall,
                color = if (canEarnCoinsOnRegen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Warning if sheet count lower than quiz count (anti-cheat)
        if (sheetLowerThanQuiz) {
            Text(
                t(UiTextKey.QuizBlocked),
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
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    when {
                        isGeneratingQuiz -> t(UiTextKey.QuizGenerating)
                        isAnyGenerationOngoing -> t(UiTextKey.QuizWait)
                        sheetLowerThanQuiz -> t(UiTextKey.QuizBlocked)
                        else -> t(UiTextKey.QuizGenerateButton)
                    }
                )
            }

            Button(
                onClick = onCancelGenerate,
                enabled = isGeneratingQuiz,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(t(UiTextKey.QuizCancelButton))
            }
        }
    }
}
