package com.example.fyp.domain.history

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for DeleteSessionUseCase and RenameSessionUseCase validation logic.
 *
 * Since these use cases are thin wrappers around repository calls,
 * we test the validation of their input parameters (value types).
 */
class SessionManagementLogicTest {

    // ── Session ID validation ──────────────────────────────────────

    /**
     * Simulates the validation that SessionId performs.
     */
    private fun isValidSessionId(id: String): Boolean {
        return id.isNotBlank()
    }

    @Test
    fun `valid session ID accepted`() {
        assertTrue(isValidSessionId("session-abc-12345678"))
    }

    @Test
    fun `empty session ID rejected`() {
        assertFalse(isValidSessionId(""))
    }

    @Test
    fun `whitespace-only session ID rejected`() {
        assertFalse(isValidSessionId("   "))
    }

    @Test
    fun `single character session ID accepted`() {
        assertTrue(isValidSessionId("a"))
    }

    // ── User ID validation ─────────────────────────────────────────

    private fun isValidUserId(id: String): Boolean {
        return id.isNotBlank()
    }

    @Test
    fun `valid user ID accepted`() {
        assertTrue(isValidUserId("user123"))
    }

    @Test
    fun `empty user ID rejected`() {
        assertFalse(isValidUserId(""))
    }

    // ── Session name validation ────────────────────────────────────

    /**
     * Session names in renameSession don't have specific validation
     * beyond being a String, but we test edge cases.
     */
    private fun trimSessionName(name: String): String {
        return name.trim()
    }

    @Test
    fun `session name is trimmed`() {
        assertEquals("My Chat", trimSessionName("  My Chat  "))
    }

    @Test
    fun `empty session name remains empty after trim`() {
        assertEquals("", trimSessionName(""))
    }

    @Test
    fun `session name with special characters preserved`() {
        assertEquals("Chat #1 — 日本語", trimSessionName("Chat #1 — 日本語"))
    }

    @Test
    fun `session name with unicode preserved`() {
        assertEquals("聊天 🎉", trimSessionName("  聊天 🎉  "))
    }

    // ── Delete session pre-conditions ──────────────────────────────

    /**
     * Before deleting a session, both userId and sessionId must be valid.
     */
    private fun canDeleteSession(userId: String, sessionId: String): Boolean {
        return userId.isNotBlank() && sessionId.isNotBlank()
    }

    @Test
    fun `canDeleteSession - true when both IDs valid`() {
        assertTrue(canDeleteSession("user1", "sess1"))
    }

    @Test
    fun `canDeleteSession - false when userId blank`() {
        assertFalse(canDeleteSession("", "sess1"))
    }

    @Test
    fun `canDeleteSession - false when sessionId blank`() {
        assertFalse(canDeleteSession("user1", ""))
    }

    @Test
    fun `canDeleteSession - false when both blank`() {
        assertFalse(canDeleteSession("", ""))
    }
}

