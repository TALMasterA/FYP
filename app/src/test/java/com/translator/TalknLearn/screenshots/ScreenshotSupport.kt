package com.translator.TalknLearn.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.firebase.Timestamp
import com.translator.TalknLearn.model.QuizAnswer
import com.translator.TalknLearn.model.QuizAttempt
import com.translator.TalknLearn.model.QuizQuestion
import com.translator.TalknLearn.model.ui.AppLanguageState
import com.translator.TalknLearn.ui.theme.FYPTheme

/** Reference images are committed here (relative to the `app/` module). */
internal const val SCREENSHOT_DIR = "src/test/screenshots"

/** English UI: an empty map makes every lookup fall back to `BaseUiTexts`. */
internal val englishLanguageState = AppLanguageState(selectedUiLanguage = "en-US", uiTexts = emptyMap())

/**
 * The app theme with fixed colours. Dynamic colour is off so the images do
 * not depend on the wallpaper of the machine that renders them.
 */
@Composable
internal fun ScreenshotTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    FYPTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

/** Fixed sample data so screenshots never change between runs. */
internal object SampleData {
    private val fixedTime = Timestamp(1_764_300_000L, 0)

    private val questions = listOf(
        QuizQuestion(
            id = "q1",
            question = "What does \"ありがとう\" mean?",
            options = listOf("Thank you", "Good morning", "Excuse me", "Goodbye"),
            correctOptionIndex = 0,
            explanation = "\"ありがとう\" (arigatou) is the everyday way to say thank you.",
        ),
        QuizQuestion(
            id = "q2",
            question = "Which word means \"water\"?",
            options = listOf("火 (hi)", "水 (mizu)", "木 (ki)", "山 (yama)"),
            correctOptionIndex = 1,
            explanation = "水 (mizu) means water.",
        ),
        QuizQuestion(
            id = "q3",
            question = "How do you say \"Where is the station?\"",
            options = listOf("駅はどこですか", "これは何ですか", "いくらですか", "トイレはどこですか"),
            correctOptionIndex = 0,
            explanation = "駅 (eki) is station; どこですか asks where something is.",
        ),
    )

    val quizInProgress = QuizAttempt(
        id = "attempt-sample",
        primaryLanguageCode = "en-US",
        targetLanguageCode = "ja-JP",
        questions = questions,
        answers = listOf(QuizAnswer(questionId = "q1", selectedOptionIndex = 0, isCorrect = true)),
        startedAt = fixedTime,
        maxScore = questions.size,
    )

    val quizCompleted = quizInProgress.copy(
        answers = listOf(
            QuizAnswer(questionId = "q1", selectedOptionIndex = 0, isCorrect = true),
            QuizAnswer(questionId = "q2", selectedOptionIndex = 2, isCorrect = false),
            QuizAnswer(questionId = "q3", selectedOptionIndex = 0, isCorrect = true),
        ),
        completedAt = fixedTime,
        totalScore = 2,
        percentage = 66.7f,
    )
}
