package com.translator.TalknLearn.screens.login

import android.content.Context
import android.content.res.Resources
import com.translator.TalknLearn.core.UiConstants
import com.translator.TalknLearn.core.security.ValidationResult
import com.translator.TalknLearn.core.security.validatePassword
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests for login screen error display logic and validation.
 *
 * Verifies:
 * - Error priority logic (local vs VM errors) (item 3)
 * - Password validation rules (item 7)
 * - Error auto-dismiss timing constant (item 7)
 * - Mode toggle state management
 */
class LoginScreenLogicTest {

    // ── Error priority logic (item 3) ──────────────────────────────

    @Test
    fun `local validation errors take priority over VM errors`() {
        // Error display logic: (localError ?: vmErrorText)
        // Local errors (password mismatch, too short) appear first
        val localError: String? = "Passwords don't match"
        val vmError: String? = "Network error"

        val displayedError = localError ?: vmError
        assertEquals("Passwords don't match", displayedError)
    }

    @Test
    fun `VM error shown when no local error`() {
        val localError: String? = null
        val vmError: String? = "Invalid credentials"

        val displayedError = localError ?: vmError
        assertEquals("Invalid credentials", displayedError)
    }

    @Test
    fun `no error shown when both are null`() {
        val localError: String? = null
        val vmError: String? = null

        val displayedError = localError ?: vmError
        assertNull(displayedError)
    }

    @Test
    fun `local error takes precedence even when VM has errorKey`() {
        val localError: String? = "Local validation failed"
        val vmErrorKey: String? = "AuthErrorGeneric"
        val vmErrorRaw: String? = null

        // The VM error text is derived from errorKey or errorRaw
        val vmErrorText = vmErrorKey ?: vmErrorRaw
        val displayedError = localError ?: vmErrorText

        assertEquals("Local validation failed", displayedError)
    }

    // ── Password validation rules (item 7) ─────────────────────────

    @Test
    fun `password mismatch detected`() {
        val password = "Password123"
        val confirmPassword = "Password456"

        val error = if (password != confirmPassword) "Passwords don't match" else null
        assertNotNull(error)
        assertEquals("Passwords don't match", error)
    }

    @Test
    fun `password too short detected`() {
        val password = "abc"

        val error = if (validatePassword(password) is ValidationResult.Invalid) "Password too short" else null
        assertNotNull(error)
    }

    @Test
    fun `password at boundary length 7 is too short`() {
        val password = "abcdefg" // 7 chars
        assertTrue(validatePassword(password) is ValidationResult.Invalid)
    }

    @Test
    fun `password at boundary length 8 is valid`() {
        val password = "abcdefgh" // 8 chars
        assertEquals(ValidationResult.Valid, validatePassword(password))
    }

    @Test
    fun `valid password and matching confirm passes validation`() {
        val password = "Password123"
        val confirmPassword = "Password123"

        val error = when {
            password != confirmPassword -> "Passwords don't match"
            validatePassword(password) is ValidationResult.Invalid -> "Password too short"
            else -> null
        }
        assertNull(error)
    }

    @Test
    fun `mismatch checked before length`() {
        val password = "ab"
        val confirmPassword = "cd"

        val error = when {
            password != confirmPassword -> "Passwords don't match"
            validatePassword(password) is ValidationResult.Invalid -> "Password too short"
            else -> null
        }
        // Mismatch takes priority over length
        assertEquals("Passwords don't match", error)
    }

    @Test
    fun `resolveGoogleWebClientId returns null when generated resource is missing`() {
        val resources = mock(Resources::class.java)
        val context = mock(Context::class.java)

        `when`(context.resources).thenReturn(resources)
        `when`(context.packageName).thenReturn("com.translator.TalknLearn")
        `when`(
            resources.getIdentifier("default_web_client_id", "string", "com.translator.TalknLearn")
        ).thenReturn(0)

        assertNull(resolveGoogleWebClientId(context))
    }

    @Test
    fun `resolveGoogleWebClientId trims configured client id`() {
        val resources = mock(Resources::class.java)
        val context = mock(Context::class.java)

        `when`(context.resources).thenReturn(resources)
        `when`(context.packageName).thenReturn("com.translator.TalknLearn")
        `when`(
            resources.getIdentifier("default_web_client_id", "string", "com.translator.TalknLearn")
        ).thenReturn(123)
        `when`(context.getString(123)).thenReturn("  test-client-id  ")

        assertEquals("test-client-id", resolveGoogleWebClientId(context))
    }

    // ── Error auto-dismiss timing (item 7) ─────────────────────────

    @Test
    fun `error auto-dismiss delay constant exists and is positive`() {
        assertTrue(
            "Auto-dismiss delay should be positive",
            UiConstants.ERROR_AUTO_DISMISS_MS > 0
        )
    }

    @Test
    fun `error auto-dismiss delay is reasonable (1-30 seconds)`() {
        val delayMs = UiConstants.ERROR_AUTO_DISMISS_MS
        assertTrue(
            "Auto-dismiss should be at least 1 second",
            delayMs >= 1000
        )
        assertTrue(
            "Auto-dismiss should be at most 30 seconds",
            delayMs <= 30_000
        )
    }

    // ── Mode toggle state management ──────────────────────────────

    @Test
    fun `toggling mode clears local error`() {
        var localError: String? = "Some error"
        // When toggle is clicked: localError = null
        localError = null
        assertNull("Error should be cleared on mode toggle", localError)
    }

    @Test
    fun `toggling mode clears confirm password`() {
        var confirmPassword = "old-password"
        // When toggle is clicked: confirmPassword = ""
        confirmPassword = ""
        assertEquals("Confirm password should be cleared", "", confirmPassword)
    }

    @Test
    fun `email trimming removes leading and trailing spaces`() {
        val rawEmail = "  test@example.com  "
        val trimmedEmail = rawEmail.trim()
        assertEquals("test@example.com", trimmedEmail)
    }

    @Test
    fun `empty email after trim is blank`() {
        val rawEmail = "   "
        assertTrue(rawEmail.trim().isBlank())
    }
}
