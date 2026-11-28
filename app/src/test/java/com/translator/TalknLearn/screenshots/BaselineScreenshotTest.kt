package com.translator.TalknLearn.screenshots

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.translator.TalknLearn.screens.learning.QuizResultsScreen
import com.translator.TalknLearn.screens.learning.QuizTakingScreen
import com.translator.TalknLearn.screens.onboarding.OnboardingScreen
import com.translator.TalknLearn.screens.startup.StartupScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * Baseline screenshots of the screens that can render without Hilt.
 *
 * Phase 0 of docs/ROADMAP.md records these so later redesign PRs have a
 * clear "before" picture. Re-record with `./gradlew :app:recordRoborazziDebug`
 * after an intended UI change; check with `./gradlew :app:verifyRoborazziDebug`.
 *
 * Robolectric settings (SDK, plain Application, Pixel 7-sized screen) live in
 * `src/test/resources/robolectric.properties`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BaselineScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun capture(name: String) {
        composeRule.onRoot().captureRoboImage("$SCREENSHOT_DIR/$name.png")
    }

    @Test
    fun startup() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { ScreenshotTheme(darkTheme = false) { StartupScreen(onFinished = {}) } }
        composeRule.mainClock.advanceTimeBy(FRAME_MS)
        capture("startup_light")
    }

    @Test
    fun onboardingLight() {
        composeRule.setContent {
            ScreenshotTheme(darkTheme = false) {
                OnboardingScreen(appLanguageState = englishLanguageState, onComplete = {})
            }
        }
        capture("onboarding_light")
    }

    @Test
    fun onboardingDark() {
        composeRule.setContent {
            ScreenshotTheme(darkTheme = true) {
                OnboardingScreen(appLanguageState = englishLanguageState, onComplete = {})
            }
        }
        capture("onboarding_dark")
    }

    @Test
    fun quizTakingLight() {
        composeRule.setContent { ScreenshotTheme(darkTheme = false) { QuizTaking() } }
        capture("quiz_taking_light")
    }

    @Test
    fun quizTakingDark() {
        composeRule.setContent { ScreenshotTheme(darkTheme = true) { QuizTaking() } }
        capture("quiz_taking_dark")
    }

    @Test
    fun quizResultsLight() {
        composeRule.setContent { ScreenshotTheme(darkTheme = false) { QuizResults() } }
        capture("quiz_results_light")
    }

    @Test
    fun quizResultsDark() {
        composeRule.setContent { ScreenshotTheme(darkTheme = true) { QuizResults() } }
        capture("quiz_results_dark")
    }

    @androidx.compose.runtime.Composable
    private fun QuizTaking() {
        QuizTakingScreen(
            attempt = SampleData.quizInProgress,
            onAnswerSelected = { _, _ -> },
            onSubmit = {},
            isLoading = false,
            appLanguageState = englishLanguageState,
            isQuizOutdated = false,
            onRegenerate = {},
        )
    }

    @androidx.compose.runtime.Composable
    private fun QuizResults() {
        QuizResultsScreen(
            attempt = SampleData.quizCompleted,
            onRetake = {},
            onBack = {},
            appLanguageState = englishLanguageState,
        )
    }

    private companion object {
        const val FRAME_MS = 500L
    }
}
