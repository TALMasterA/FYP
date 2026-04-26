package com.translator.TalknLearn.screens.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.translator.TalknLearn.HiltTestActivity
import com.translator.TalknLearn.model.ui.AppLanguageState
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.ui.theme.FYPTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented Compose UI smoke tests for the LoginScreen.
 *
 * Verifies that key UI elements render, text input works, and
 * the login/register toggle switches modes correctly.
 *
 * Note: These tests use a standalone composable without Hilt injection,
 * so they exercise the UI layer only (no real ViewModel or Firebase).
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginScreenSmokeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun injectHilt() {
        hiltRule.inject()
    }

    private fun setContent() {
        composeTestRule.setContent {
            FYPTheme {
                LoginScreen(
                    uiLanguages = listOf("en-US" to "English"),
                    appLanguageState = AppLanguageState(
                        selectedUiLanguage = "en-US",
                        uiTexts = emptyMap()
                    ),
                    onUpdateAppLanguage = { _, _ -> },
                    onBack = {}
                )
            }
        }
    }

    @Test
    fun loginScreen_displaysEmailAndPasswordFields() {
        setContent()
        composeTestRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
    }

    @Test
    fun loginScreen_loginButtonIsDisplayedAndEnabled() {
        setContent()
        composeTestRule.onNodeWithTag("loginButton")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun loginScreen_canTypeInEmailField() {
        setContent()
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("test@example.com").assertIsDisplayed()
    }

    @Test
    fun loginScreen_toggleToRegisterShowsConfirmPassword() {
        setContent()
        composeTestRule.onNodeWithTag("toggleAuthMode").performClick()
        composeTestRule.waitForIdle()
        // In register mode, the confirm password field should appear
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()
    }
}
