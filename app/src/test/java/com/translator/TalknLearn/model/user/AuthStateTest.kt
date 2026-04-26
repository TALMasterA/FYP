package com.translator.TalknLearn.model.user

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for AuthState sealed interface and User data class.
 * Verifies structural correctness and pattern matching behavior.
 */
class AuthStateTest {

    @Test
    fun `Loading is a singleton object`() {
        assertSame(AuthState.Loading, AuthState.Loading)
    }

    @Test
    fun `LoggedOut is a singleton object`() {
        assertSame(AuthState.LoggedOut, AuthState.LoggedOut)
    }

    @Test
    fun `LoggedIn wraps a User`() {
        val user = User(uid = "abc", email = "a@b.com")
        val state = AuthState.LoggedIn(user)
        assertEquals("abc", state.user.uid)
        assertEquals("a@b.com", state.user.email)
    }

    @Test
    fun `LoggedIn data class equality`() {
        val a = AuthState.LoggedIn(User("1", "x@y.com"))
        val b = AuthState.LoggedIn(User("1", "x@y.com"))
        assertEquals(a, b)
    }

    @Test
    fun `LoggedIn data class inequality on different uid`() {
        val a = AuthState.LoggedIn(User("1", "x@y.com"))
        val b = AuthState.LoggedIn(User("2", "x@y.com"))
        assertNotEquals(a, b)
    }

    @Test
    fun `exhaustive when matches all auth states`() {
        val states: List<AuthState> = listOf(
            AuthState.Loading,
            AuthState.LoggedOut,
            AuthState.LoggedIn(User("u1", "e@e.com"))
        )

        states.forEach { state ->
            val label = when (state) {
                AuthState.Loading -> "loading"
                AuthState.LoggedOut -> "loggedOut"
                is AuthState.LoggedIn -> "loggedIn:${state.user.uid}"
            }
            assertTrue(label.isNotBlank())
        }
    }

    @Test
    fun `User default values`() {
        val user = User()
        assertEquals("", user.uid)
        assertNull(user.email)
    }

    @Test
    fun `User copy changes only specified fields`() {
        val original = User(uid = "1", email = "a@b.com")
        val copied = original.copy(email = "new@b.com")
        assertEquals("1", copied.uid)
        assertEquals("new@b.com", copied.email)
    }

    @Test
    fun `User with null email`() {
        val user = User(uid = "1", email = null)
        assertNull(user.email)
    }
}
