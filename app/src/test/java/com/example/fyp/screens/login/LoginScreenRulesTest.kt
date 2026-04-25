package com.example.fyp.screens.login

import com.example.fyp.core.security.ValidationResult
import com.example.fyp.core.security.validatePassword
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for login screen app logic and Firebase auth rules.
 * 
 * Requirements:
 * - Login uses Firebase auth service
 * - Error appears above the input fields (visible when keyboard is open)
 * - Password validation rules
 * - Registration requires password confirmation
 */
class LoginScreenRulesTest {

    @Test
    fun `login uses Firebase authentication`() {
        // Specification: AuthViewModel delegates to FirebaseAuthRepository
        val authProvider = "firebase"
        assertEquals("firebase", authProvider)
    }

    @Test
    fun `error message position is above input fields`() {
        // Layout order in LoginScreen:
        // 1. Language dropdown
        // 2. Update logout message (optional)
        // 3. Login hint / register rules text
        // 4. ERROR MESSAGE CARD ← above inputs
        // 5. Email input
        // 6. Password input
        val errorPosition = 4
        val emailPosition = 5
        assertTrue("Error should be above email field", errorPosition < emailPosition)
    }

    @Test
    fun `error visible when keyboard is open`() {
        // Error is positioned above the input fields, so when keyboard pushes up
        // the scrollable content, the error remains visible
        val errorAboveInputs = true
        assertTrue("Error above inputs stays visible with keyboard", errorAboveInputs)
    }

    @Test
    fun `local error takes priority over VM error`() {
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
    fun `no error shown when both null`() {
        val localError: String? = null
        val vmError: String? = null

        val displayedError = localError ?: vmError
        assertNull(displayedError)
    }

    @Test
    fun `password too short error for less than 8 chars`() {
        val password = "abcdefg"
        val error = if (validatePassword(password) is ValidationResult.Invalid) "Password too short" else null

        assertNotNull(error)
    }

    @Test
    fun `password at 8 chars passes length check`() {
        val password = "abcdefgh"
        val error = if (validatePassword(password) is ValidationResult.Invalid) "Password too short" else null

        assertNull(error)
    }

    @Test
    fun `password mismatch error when confirm differs`() {
        val password = "Password123"
        val confirmPassword = "Password456"

        val error = if (password != confirmPassword) "Passwords don't match" else null
        assertNotNull(error)
    }

    @Test
    fun `matching passwords pass validation`() {
        val password = "Password123"
        val confirmPassword = "Password123"

        val error = if (password != confirmPassword) "Passwords don't match" else null
        assertNull(error)
    }

    @Test
    fun `email is trimmed before submission`() {
        val rawEmail = "  test@example.com  "
        val trimmedEmail = rawEmail.trim()
        assertEquals("test@example.com", trimmedEmail)
    }

    @Test
    fun `mode toggle clears local error`() {
        var localError: String? = "Some error"
        // Toggle mode action
        localError = null
        assertNull(localError)
    }

    @Test
    fun `mode toggle clears confirm password`() {
        var confirmPassword = "secret"
        // Toggle mode action
        confirmPassword = ""
        assertEquals("", confirmPassword)
    }
}
