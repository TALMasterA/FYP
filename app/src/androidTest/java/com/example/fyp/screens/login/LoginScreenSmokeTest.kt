package com.example.fyp.screens.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.UiTextKey
import com.example.fyp.ui.theme.FYPTheme
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
@RunWith(AndroidJUnit4::class)
class LoginScreenSmokeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent() {
        composeTestRule.setContent {
            FYPTheme {
                LoginScreen(
                    uiLanguages = listOf("en-US" to "English"),
                    appLanguageState = AppLanguageState(),
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
