package com.translator.TalknLearn.core.security

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AuditLogger.
 *
 * Tests the EventType enum and message building logic.
 * Note: log() calls AppLogger and Crashlytics which are Android-dependent,
 * so we test the EventType enum values and helper method contracts.
 */
class AuditLoggerTest {

    // ── EventType enum values ───────────────────────────────────────

    @Test
    fun `EventType contains all expected security events`() {
        val expectedEvents = listOf(
            "LOGIN_FAILED",
            "LOGIN_SUCCESS",
            "RATE_LIMIT_EXCEEDED",
            "INVALID_INPUT",
            "ACCOUNT_DELETED",
            "PASSWORD_RESET_REQUESTED",
            "FRIEND_REQUEST_BLOCKED",
            "SUSPICIOUS_ACTIVITY",
            "SEND_FRIEND_REQUEST",
            "ACCEPT_FRIEND_REQUEST",
            "REJECT_FRIEND_REQUEST",
            "CANCEL_FRIEND_REQUEST",
            "REMOVE_FRIEND",
            "BLOCK_USER",
            "UNBLOCK_USER",
            "SEND_MESSAGE",
            "DELETE_CHAT"
        )

        val actualEvents = AuditLogger.EventType.entries.map { it.name }
        assertEquals(expectedEvents.sorted(), actualEvents.sorted())
    }

    @Test
    fun `EventType has 17 entries`() {
        assertEquals(17, AuditLogger.EventType.entries.size)
    }

    @Test
    fun `EventType LOGIN_FAILED valueOf works`() {
        assertEquals(AuditLogger.EventType.LOGIN_FAILED, AuditLogger.EventType.valueOf("LOGIN_FAILED"))
    }

    @Test
    fun `EventType LOGIN_SUCCESS valueOf works`() {
        assertEquals(AuditLogger.EventType.LOGIN_SUCCESS, AuditLogger.EventType.valueOf("LOGIN_SUCCESS"))
    }

    @Test
    fun `EventType RATE_LIMIT_EXCEEDED valueOf works`() {
        assertEquals(AuditLogger.EventType.RATE_LIMIT_EXCEEDED, AuditLogger.EventType.valueOf("RATE_LIMIT_EXCEEDED"))
    }

    @Test
    fun `EventType friend system events exist`() {
        assertNotNull(AuditLogger.EventType.valueOf("SEND_FRIEND_REQUEST"))
        assertNotNull(AuditLogger.EventType.valueOf("ACCEPT_FRIEND_REQUEST"))
        assertNotNull(AuditLogger.EventType.valueOf("REJECT_FRIEND_REQUEST"))
        assertNotNull(AuditLogger.EventType.valueOf("CANCEL_FRIEND_REQUEST"))
        assertNotNull(AuditLogger.EventType.valueOf("REMOVE_FRIEND"))
        assertNotNull(AuditLogger.EventType.valueOf("BLOCK_USER"))
        assertNotNull(AuditLogger.EventType.valueOf("UNBLOCK_USER"))
    }

    @Test
    fun `EventType security events exist`() {
        assertNotNull(AuditLogger.EventType.valueOf("SUSPICIOUS_ACTIVITY"))
        assertNotNull(AuditLogger.EventType.valueOf("FRIEND_REQUEST_BLOCKED"))
    }

    @Test
    fun `EventType chat events exist`() {
        assertNotNull(AuditLogger.EventType.valueOf("SEND_MESSAGE"))
        assertNotNull(AuditLogger.EventType.valueOf("DELETE_CHAT"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `EventType valueOf throws for invalid name`() {
        AuditLogger.EventType.valueOf("NONEXISTENT_EVENT")
    }
}
