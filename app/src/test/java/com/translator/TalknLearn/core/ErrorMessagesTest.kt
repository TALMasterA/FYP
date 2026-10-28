package com.translator.TalknLearn.core

import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMessagesTest {

    // ── fromException: null and fallback ─────────────────────────────

    @Test
    fun `fromException - null exception returns default fallback`() {
        val msg = ErrorMessages.fromException(null)
        assertEquals("Something went wrong. Please try again.", msg)
    }

    @Test
    fun `fromException - null exception returns custom fallback`() {
        val msg = ErrorMessages.fromException(null, "Custom fallback")
        assertEquals("Custom fallback", msg)
    }

    @Test
    fun `fromException - unknown exception returns default fallback`() {
        val msg = ErrorMessages.fromException(RuntimeException("some random error"))
        assertEquals("Something went wrong. Please try again.", msg)
    }

    @Test
    fun `fromException - unknown exception returns custom fallback`() {
        val msg = ErrorMessages.fromException(RuntimeException("random"), "Try later")
        assertEquals("Try later", msg)
    }

    // ── fromException: network errors ───────────────────────────────

    @Test
    fun `fromException - UnknownHostException returns network message`() {
        val msg = ErrorMessages.fromException(UnknownHostException("host not found"))
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    @Test
    fun `fromException - SocketTimeoutException returns network message`() {
        val msg = ErrorMessages.fromException(SocketTimeoutException("timed out"))
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    @Test
    fun `fromException - ConnectException returns network message`() {
        val msg = ErrorMessages.fromException(ConnectException("connection refused"))
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    @Test
    fun `fromException - IOException with network in message returns network message`() {
        val msg = ErrorMessages.fromException(IOException("network is unreachable"))
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    // ── fromException: Firebase auth errors ─────────────────────────

    @Test
    fun `fromException - PERMISSION_DENIED returns permission message`() {
        val msg = ErrorMessages.fromException(Exception("PERMISSION_DENIED: insufficient permissions"))
        assertEquals("You don't have permission to perform this action.", msg)
    }

    @Test
    fun `fromException - UNAUTHENTICATED returns session expired message`() {
        val msg = ErrorMessages.fromException(Exception("UNAUTHENTICATED"))
        assertEquals("Your session has expired. Please sign in again.", msg)
    }

    @Test
    fun `fromException - NOT_FOUND returns not found message`() {
        val msg = ErrorMessages.fromException(Exception("NOT_FOUND: document missing"))
        assertEquals("The requested item was not found. It may have been deleted.", msg)
    }

    // ── fromException: SecurityException ────────────────────────────

    @Test
    fun `fromException - SecurityException uses its message`() {
        val msg = ErrorMessages.fromException(SecurityException("Custom security error"))
        assertEquals("Custom security error", msg)
    }

    @Test
    fun `fromException - SecurityException with null message returns Access denied`() {
        val msg = ErrorMessages.fromException(SecurityException())
        assertEquals("Access denied.", msg)
    }

    // ── fromException: friend-specific errors ───────────────────────

    @Test
    fun `fromException - Already friends message`() {
        val msg = ErrorMessages.fromException(Exception("Already friends with user"))
        assertEquals("You're already friends with this user.", msg)
    }

    @Test
    fun `fromException - blocked message`() {
        val msg = ErrorMessages.fromException(Exception("User is blocked"))
        assertEquals("Unable to complete this action. The user may be blocked.", msg)
    }

    @Test
    fun `fromException - Request not found message`() {
        val msg = ErrorMessages.fromException(Exception("Request not found in database"))
        assertEquals("This friend request no longer exists.", msg)
    }

    @Test
    fun `fromException - already been handled message`() {
        val msg = ErrorMessages.fromException(Exception("Request has already been handled"))
        assertEquals("This request has already been handled by someone else.", msg)
    }

    @Test
    fun `fromException - Not authorized message`() {
        val msg = ErrorMessages.fromException(Exception("Not authorized to do this"))
        assertEquals("You're not authorized to perform this action.", msg)
    }

    // ── fromException: rate limiting ────────────────────────────────

    @Test
    fun `fromException - RESOURCE_EXHAUSTED returns rate limit message`() {
        val msg = ErrorMessages.fromException(Exception("RESOURCE_EXHAUSTED"))
        assertEquals("Too many requests. Please wait a moment and try again.", msg)
    }

    @Test
    fun `fromException - rate limit message`() {
        val msg = ErrorMessages.fromException(Exception("rate limit exceeded"))
        assertEquals("Too many requests. Please wait a moment and try again.", msg)
    }

    // ── fromException: validation errors ────────────────────────────

    @Test
    fun `fromException - IllegalArgumentException uses its message`() {
        val msg = ErrorMessages.fromException(IllegalArgumentException("Bad input"))
        assertEquals("Bad input", msg)
    }

    @Test
    fun `fromException - IllegalArgumentException with null message`() {
        val msg = ErrorMessages.fromException(IllegalArgumentException())
        assertEquals("Invalid input. Please check and try again.", msg)
    }

    @Test
    fun `fromException - IllegalStateException uses its message`() {
        val msg = ErrorMessages.fromException(IllegalStateException("Not ready"))
        assertEquals("Not ready", msg)
    }

    @Test
    fun `fromException - IllegalStateException with null message`() {
        val msg = ErrorMessages.fromException(IllegalStateException())
        assertEquals("Operation cannot be completed right now.", msg)
    }

    // ── fromException: deadline ─────────────────────────────────────

    @Test
    fun `fromException - DEADLINE_EXCEEDED returns timeout message`() {
        val msg = ErrorMessages.fromException(Exception("DEADLINE_EXCEEDED"))
        assertEquals("The operation timed out. Please try again.", msg)
    }

    // ── isNetworkError ──────────────────────────────────────────────

    @Test
    fun `isNetworkError - UnknownHostException is a network error`() {
        assertTrue(ErrorMessages.isNetworkError(UnknownHostException()))
    }

    @Test
    fun `isNetworkError - SocketTimeoutException is a network error`() {
        assertTrue(ErrorMessages.isNetworkError(SocketTimeoutException()))
    }

    @Test
    fun `isNetworkError - ConnectException is a network error`() {
        assertTrue(ErrorMessages.isNetworkError(ConnectException()))
    }

    @Test
    fun `isNetworkError - IOException with network keyword is a network error`() {
        assertTrue(ErrorMessages.isNetworkError(IOException("network unreachable")))
    }

    @Test
    fun `isNetworkError - exception with unavailable keyword`() {
        assertTrue(ErrorMessages.isNetworkError(RuntimeException("service unavailable")))
    }

    @Test
    fun `isNetworkError - exception with UNAVAILABLE keyword`() {
        assertTrue(ErrorMessages.isNetworkError(RuntimeException("UNAVAILABLE")))
    }

    @Test
    fun `isNetworkError - exception with timeout keyword`() {
        assertTrue(ErrorMessages.isNetworkError(RuntimeException("request timeout")))
    }

    @Test
    fun `isNetworkError - generic RuntimeException is not a network error`() {
        assertFalse(ErrorMessages.isNetworkError(RuntimeException("some random error")))
    }

    @Test
    fun `isNetworkError - IllegalArgumentException is not a network error`() {
        assertFalse(ErrorMessages.isNetworkError(IllegalArgumentException("bad input")))
    }

    // ── Pre-built messages are non-empty ────────────────────────────

    @Test
    fun `pre-built messages are not blank`() {
        assertTrue(ErrorMessages.FRIEND_REMOVED.isNotBlank())
        assertTrue(ErrorMessages.FRIEND_REMOVE_FAILED.isNotBlank())
        assertTrue(ErrorMessages.GENERIC_RETRY.isNotBlank())
    }
}
