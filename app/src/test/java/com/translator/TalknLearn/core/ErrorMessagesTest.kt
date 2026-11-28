package com.translator.TalknLearn.core

import com.translator.TalknLearn.model.ui.StatusText
import com.translator.TalknLearn.model.ui.UiTextKey
import com.translator.TalknLearn.model.ui.english
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMessagesTest {

    // ── classify: null and fallback ─────────────────────────────

    @Test
    fun `classify - null exception returns default fallback`() {
        val msg = ErrorMessages.statusFromException(null).english()
        assertEquals("Something went wrong. Please try again.", msg)
    }

    @Test
    fun `classify - null exception returns custom fallback key`() {
        val status = ErrorMessages.statusFromException(null, UiTextKey.ErrTimedOut)
        assertEquals(StatusText(UiTextKey.ErrTimedOut), status)
    }

    @Test
    fun `classify - unknown exception returns default fallback`() {
        val msg = ErrorMessages.statusFromException(RuntimeException("some random error")).english()
        assertEquals("Something went wrong. Please try again.", msg)
    }

    @Test
    fun `classify - unknown exception returns custom fallback key`() {
        val status = ErrorMessages.statusFromException(RuntimeException("random"), UiTextKey.ErrTimedOut)
        assertEquals(StatusText(UiTextKey.ErrTimedOut), status)
    }

    // ── classify: network errors ───────────────────────────────

    @Test
    fun `classify - UnknownHostException returns network message`() {
        val msg = ErrorMessages.statusFromException(UnknownHostException("host not found")).english()
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    @Test
    fun `classify - SocketTimeoutException returns network message`() {
        val msg = ErrorMessages.statusFromException(SocketTimeoutException("timed out")).english()
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    @Test
    fun `classify - ConnectException returns network message`() {
        val msg = ErrorMessages.statusFromException(ConnectException("connection refused")).english()
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    @Test
    fun `classify - IOException with network in message returns network message`() {
        val msg = ErrorMessages.statusFromException(IOException("network is unreachable")).english()
        assertEquals("No internet connection. Please check your network and try again.", msg)
    }

    // ── classify: Firebase auth errors ─────────────────────────

    @Test
    fun `classify - PERMISSION_DENIED returns permission message`() {
        val msg = ErrorMessages.statusFromException(Exception("PERMISSION_DENIED: insufficient permissions")).english()
        assertEquals("You don't have permission to perform this action.", msg)
    }

    @Test
    fun `classify - UNAUTHENTICATED returns session expired message`() {
        val msg = ErrorMessages.statusFromException(Exception("UNAUTHENTICATED")).english()
        assertEquals("Your session has expired. Please sign in again.", msg)
    }

    @Test
    fun `classify - NOT_FOUND returns not found message`() {
        val msg = ErrorMessages.statusFromException(Exception("NOT_FOUND: document missing")).english()
        assertEquals("The requested item was not found. It may have been deleted.", msg)
    }

    // ── classify: SecurityException ────────────────────────────

    @Test
    fun `classify - SecurityException uses its message`() {
        val msg = ErrorMessages.statusFromException(SecurityException("Custom security error")).english()
        assertEquals("Custom security error", msg)
    }

    @Test
    fun `classify - SecurityException with null message returns Access denied`() {
        val msg = ErrorMessages.statusFromException(SecurityException()).english()
        assertEquals("Access denied.", msg)
    }

    // ── classify: friend-specific errors ───────────────────────

    @Test
    fun `classify - Already friends message`() {
        val msg = ErrorMessages.statusFromException(Exception("Already friends with user")).english()
        assertEquals("You're already friends with this user.", msg)
    }

    @Test
    fun `classify - blocked message`() {
        val msg = ErrorMessages.statusFromException(Exception("User is blocked")).english()
        assertEquals("Unable to complete this action. The user may be blocked.", msg)
    }

    @Test
    fun `classify - Request not found message`() {
        val msg = ErrorMessages.statusFromException(Exception("Request not found in database")).english()
        assertEquals("This friend request no longer exists.", msg)
    }

    @Test
    fun `classify - already been handled message`() {
        val msg = ErrorMessages.statusFromException(Exception("Request has already been handled")).english()
        assertEquals("This request has already been handled by someone else.", msg)
    }

    @Test
    fun `classify - Not authorized message`() {
        val msg = ErrorMessages.statusFromException(Exception("Not authorized to do this")).english()
        assertEquals("You're not authorized to perform this action.", msg)
    }

    // ── classify: rate limiting ────────────────────────────────

    @Test
    fun `classify - RESOURCE_EXHAUSTED returns rate limit message`() {
        val msg = ErrorMessages.statusFromException(Exception("RESOURCE_EXHAUSTED")).english()
        assertEquals("Too many requests. Please wait a moment and try again.", msg)
    }

    @Test
    fun `classify - rate limit message`() {
        val msg = ErrorMessages.statusFromException(Exception("rate limit exceeded")).english()
        assertEquals("Too many requests. Please wait a moment and try again.", msg)
    }

    // ── classify: validation errors ────────────────────────────

    @Test
    fun `classify - IllegalArgumentException uses its message`() {
        val msg = ErrorMessages.statusFromException(IllegalArgumentException("Bad input")).english()
        assertEquals("Bad input", msg)
    }

    @Test
    fun `classify - IllegalArgumentException with null message`() {
        val msg = ErrorMessages.statusFromException(IllegalArgumentException()).english()
        assertEquals("Invalid input. Please check and try again.", msg)
    }

    @Test
    fun `classify - IllegalStateException uses its message`() {
        val msg = ErrorMessages.statusFromException(IllegalStateException("Not ready")).english()
        assertEquals("Not ready", msg)
    }

    @Test
    fun `classify - IllegalStateException with null message`() {
        val msg = ErrorMessages.statusFromException(IllegalStateException()).english()
        assertEquals("Operation cannot be completed right now.", msg)
    }

    // ── classify: deadline ─────────────────────────────────────

    @Test
    fun `classify - DEADLINE_EXCEEDED returns timeout message`() {
        val msg = ErrorMessages.statusFromException(Exception("DEADLINE_EXCEEDED")).english()
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

    // ── Default fallback ────────────────────────────────────────────

    @Test
    fun `default fallback is ErrGeneric`() {
        assertEquals(StatusText(UiTextKey.ErrGeneric), ErrorMessages.statusFromException(null))
    }

    // ── statusFromException: localizable form ──────────────────────

    @Test
    fun `statusFromException - null exception returns fallback key`() {
        assertEquals(
            StatusText(UiTextKey.FriendsMsgRemoveFailed),
            ErrorMessages.statusFromException(null, UiTextKey.FriendsMsgRemoveFailed),
        )
    }

    @Test
    fun `statusFromException - default fallback is ErrGeneric`() {
        assertEquals(StatusText(UiTextKey.ErrGeneric), ErrorMessages.statusFromException(RuntimeException("x")))
    }

    @Test
    fun `statusFromException - network error maps to ErrNoInternet`() {
        assertEquals(
            StatusText(UiTextKey.ErrNoInternet),
            ErrorMessages.statusFromException(UnknownHostException("host")),
        )
    }

    @Test
    fun `statusFromException - SecurityException keeps its detail as raw text`() {
        val status = ErrorMessages.statusFromException(SecurityException("Cannot share with this user"))
        assertEquals(UiTextKey.StatusRaw, status.key)
        assertEquals("Cannot share with this user", status.english())
    }

    @Test
    fun `statusFromException - SecurityException without message maps to ErrAccessDenied`() {
        assertEquals(StatusText(UiTextKey.ErrAccessDenied), ErrorMessages.statusFromException(SecurityException()))
    }

    @Test
    fun `statusFromException classifies every known case without the fallback`() {
        val cases = listOf(
            UnknownHostException("host"),
            RuntimeException("PERMISSION_DENIED"),
            RuntimeException("UNAUTHENTICATED"),
            RuntimeException("NOT_FOUND"),
            SecurityException("custom"),
            RuntimeException("Already friends"),
            RuntimeException("user blocked"),
            RuntimeException("Request not found"),
            RuntimeException("already been handled"),
            RuntimeException("Not authorized"),
            RuntimeException("RESOURCE_EXHAUSTED"),
            IllegalArgumentException("bad"),
            IllegalStateException(),
            RuntimeException("DEADLINE_EXCEEDED"),
        )
        cases.forEach { e ->
            assertNotEquals(e.toString(), StatusText(UiTextKey.ErrGeneric), ErrorMessages.statusFromException(e))
        }
    }

    @Test
    fun `StatusText raw resolves to the text unchanged`() {
        assertEquals("Item not found", StatusText.raw("Item not found").english())
    }
}
