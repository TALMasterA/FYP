package com.example.fyp.model.friends

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for PublicUserProfile model.
 */
class PublicUserProfileTest {

    @Test
    fun `default profile has empty fields`() {
        val profile = PublicUserProfile()
        assertEquals("", profile.uid)
        assertEquals("", profile.username)
        assertEquals("", profile.displayName)
        assertEquals("", profile.avatarUrl)
        assertEquals("", profile.primaryLanguage)
        assertTrue(profile.isDiscoverable)
    }

    @Test
    fun `profile stores all fields correctly`() {
        val profile = PublicUserProfile(
            uid = "user123",
            username = "test_user",
            displayName = "Test User",
            avatarUrl = "https://example.com/avatar.jpg",
            primaryLanguage = "en-US",
            learningLanguages = listOf("ja-JP", "es-ES"),
            isDiscoverable = false
        )
        assertEquals("user123", profile.uid)
        assertEquals("test_user", profile.username)
        assertEquals("Test User", profile.displayName)
        assertEquals("https://example.com/avatar.jpg", profile.avatarUrl)
        assertEquals("en-US", profile.primaryLanguage)
        assertEquals(2, profile.learningLanguages?.size)
        assertFalse(profile.isDiscoverable)
    }

    @Test
    fun `learningLanguages defaults to empty list`() {
        val profile = PublicUserProfile()
        assertTrue(profile.learningLanguages.orEmpty().isEmpty())
    }

    @Test
    fun `learningLanguages handles null safely`() {
        // Firestore may return null for list fields
        val profile = PublicUserProfile(learningLanguages = null)
        assertNull(profile.learningLanguages)
        assertTrue(profile.learningLanguages.orEmpty().isEmpty())
    }

    @Test
    fun `copy updates fields correctly`() {
        val original = PublicUserProfile(uid = "user1", username = "old_name")
        val updated = original.copy(username = "new_name", isDiscoverable = false)
        assertEquals("user1", updated.uid)
        assertEquals("new_name", updated.username)
        assertFalse(updated.isDiscoverable)
    }
}
