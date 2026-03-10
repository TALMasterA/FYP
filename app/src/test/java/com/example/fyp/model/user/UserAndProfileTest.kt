package com.example.fyp.model.user

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for User and UserProfile data classes.
 *
 * These are simple data classes but we verify defaults, equality, and copy semantics.
 */
class UserAndProfileTest {

    // ── User ───────────────────────────────────────────────────────

    @Test
    fun `User has correct defaults`() {
        val user = User()
        assertEquals("", user.uid)
        assertNull(user.email)
    }

    @Test
    fun `User stores uid and email`() {
        val user = User(uid = "abc123", email = "test@example.com")
        assertEquals("abc123", user.uid)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun `User with null email`() {
        val user = User(uid = "abc123")
        assertNull(user.email)
    }

    @Test
    fun `User equality based on all fields`() {
        val a = User("uid1", "a@b.com")
        val b = User("uid1", "a@b.com")
        assertEquals(a, b)
    }

    @Test
    fun `User inequality when uid differs`() {
        val a = User("uid1", "a@b.com")
        val b = User("uid2", "a@b.com")
        assertNotEquals(a, b)
    }

    @Test
    fun `User inequality when email differs`() {
        val a = User("uid1", "a@b.com")
        val b = User("uid1", "c@d.com")
        assertNotEquals(a, b)
    }

    @Test
    fun `User copy preserves uid when updating email`() {
        val original = User("uid1", "old@b.com")
        val updated = original.copy(email = "new@b.com")
        assertEquals("uid1", updated.uid)
        assertEquals("new@b.com", updated.email)
    }

    // ── UserProfile ────────────────────────────────────────────────

    @Test
    fun `UserProfile has correct defaults`() {
        val profile = UserProfile()
        assertNull(profile.photoUrl)
        assertNull(profile.createdAt)
        assertNull(profile.updatedAt)
    }

    @Test
    fun `UserProfile stores all fields`() {
        val ts = com.google.firebase.Timestamp(1000, 0)
        val profile = UserProfile(
            photoUrl = "https://example.com/photo.jpg",
            createdAt = ts,
            updatedAt = ts
        )
        assertEquals("https://example.com/photo.jpg", profile.photoUrl)
        assertEquals(ts, profile.createdAt)
        assertEquals(ts, profile.updatedAt)
    }

    @Test
    fun `UserProfile equality based on all fields`() {
        val ts = com.google.firebase.Timestamp(1000, 0)
        val a = UserProfile("url", ts, ts)
        val b = UserProfile("url", ts, ts)
        assertEquals(a, b)
    }

    @Test
    fun `UserProfile inequality when photoUrl differs`() {
        val a = UserProfile(photoUrl = "url1")
        val b = UserProfile(photoUrl = "url2")
        assertNotEquals(a, b)
    }

    @Test
    fun `UserProfile with null photoUrl`() {
        val profile = UserProfile(photoUrl = null)
        assertNull(profile.photoUrl)
    }

    @Test
    fun `UserProfile copy updates updatedAt only`() {
        val created = com.google.firebase.Timestamp(1000, 0)
        val updated = com.google.firebase.Timestamp(2000, 0)
        val profile = UserProfile(
            photoUrl = "url",
            createdAt = created,
            updatedAt = created
        )
        val modified = profile.copy(updatedAt = updated)
        assertEquals(created, modified.createdAt)
        assertEquals(updated, modified.updatedAt)
        assertEquals("url", modified.photoUrl)
    }

    // ── AuthState sealed interface ─────────────────────────────────

    @Test
    fun `AuthState Loading is singleton`() {
        val a: AuthState = AuthState.Loading
        val b: AuthState = AuthState.Loading
        assertSame(a, b)
    }

    @Test
    fun `AuthState LoggedOut is singleton`() {
        val a: AuthState = AuthState.LoggedOut
        val b: AuthState = AuthState.LoggedOut
        assertSame(a, b)
    }

    @Test
    fun `AuthState LoggedIn wraps User`() {
        val user = User("uid1", "test@test.com")
        val state = AuthState.LoggedIn(user)
        assertEquals(user, state.user)
        assertEquals("uid1", state.user.uid)
    }

    @Test
    fun `AuthState LoggedIn equality based on User`() {
        val user = User("uid1", "test@test.com")
        val a = AuthState.LoggedIn(user)
        val b = AuthState.LoggedIn(user)
        assertEquals(a, b)
    }

    @Test
    fun `AuthState can be pattern matched`() {
        val states: List<AuthState> = listOf(
            AuthState.Loading,
            AuthState.LoggedOut,
            AuthState.LoggedIn(User("uid1", "a@b.com"))
        )

        val results = states.map { state ->
            when (state) {
                is AuthState.Loading -> "loading"
                is AuthState.LoggedOut -> "logged_out"
                is AuthState.LoggedIn -> "logged_in:${state.user.uid}"
            }
        }

        assertEquals("loading", results[0])
        assertEquals("logged_out", results[1])
        assertEquals("logged_in:uid1", results[2])
    }
}

