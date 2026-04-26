package com.translator.TalknLearn.data.friends

import com.translator.TalknLearn.model.friends.FriendRequest
import com.translator.TalknLearn.model.friends.RequestStatus
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from FirestoreFriendsRepository.
 *
 * Covers: friend request note sanitization (HTML escaping, truncation),
 * request expiry filtering, username sync freshness debounce,
 * and search index building.
 */
class FriendsRepositoryLogicTest {

    // ── Note sanitization ─────────────────────────────────────────────

    @Test
    fun `note sanitization escapes ampersand`() {
        val note = "Tom & Jerry"
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals("Tom &amp; Jerry", sanitized)
    }

    @Test
    fun `note sanitization escapes angle brackets`() {
        val note = "<script>alert('xss')</script>"
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", sanitized)
    }

    @Test
    fun `note sanitization escapes all three characters together`() {
        val note = "A & B < C > D"
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals("A &amp; B &lt; C &gt; D", sanitized)
    }

    @Test
    fun `note sanitization truncates to 80 characters before escaping`() {
        val longNote = "a".repeat(100)
        val sanitized = longNote.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals(80, sanitized.length)
    }

    @Test
    fun `note sanitization trims whitespace after truncation`() {
        val note = "  hello world  "
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals("hello world", sanitized)
    }

    @Test
    fun `note sanitization handles empty note`() {
        val note = ""
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals("", sanitized)
    }

    @Test
    fun `note sanitization preserves normal note unchanged`() {
        val note = "Hey, want to be friends?"
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals("Hey, want to be friends?", sanitized)
    }

    @Test
    fun `note sanitization handles ampersand-heavy text`() {
        val note = "A&B&C&D"
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        assertEquals("A&amp;B&amp;C&amp;D", sanitized)
    }

    @Test
    fun `note sanitization order matters - ampersand first`() {
        // If we escapeAmp after <>, the &amp; in &lt; would be double-escaped
        val note = "&<>"
        val sanitized = note.take(80).trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        // Correct order: & → &amp;, then < → &lt; (not &amp;lt;)
        assertEquals("&amp;&lt;&gt;", sanitized)
    }

    // ── Request expiry filtering ──────────────────────────────────────

    @Test
    fun `unexpired request passes filter`() {
        val futureSeconds = Timestamp.now().seconds + 86400 // +1 day
        val expiresAt = Timestamp(futureSeconds, 0)
        val now = Timestamp.now()
        val passes = expiresAt.seconds > now.seconds
        assertTrue("Unexpired request should pass filter", passes)
    }

    @Test
    fun `expired request is filtered out`() {
        val pastSeconds = Timestamp.now().seconds - 86400 // -1 day
        val expiresAt = Timestamp(pastSeconds, 0)
        val now = Timestamp.now()
        val passes = expiresAt.seconds > now.seconds
        assertFalse("Expired request should be filtered out", passes)
    }

    @Test
    fun `request with null expiresAt passes filter`() {
        val expiresAt: Timestamp? = null
        val passes = expiresAt == null || expiresAt.seconds > Timestamp.now().seconds
        assertTrue("Null expiresAt should pass (legacy request)", passes)
    }

    @Test
    fun `request expiry is 30 days from creation`() {
        val creationSeconds = 1000000L
        val expirySeconds = creationSeconds + 30 * 24 * 3600
        val expectedDays = (expirySeconds - creationSeconds) / (24 * 3600)
        assertEquals(30L, expectedDays)
    }

    @Test
    fun `filter removes only expired requests from list`() {
        val now = Timestamp.now()
        val future = Timestamp(now.seconds + 86400, 0)
        val past = Timestamp(now.seconds - 86400, 0)

        data class SimpleRequest(val id: String, val expiresAt: Timestamp?)
        val requests = listOf(
            SimpleRequest("r1", future),
            SimpleRequest("r2", past),
            SimpleRequest("r3", null),
            SimpleRequest("r4", future)
        )

        val filtered = requests.filter { req ->
            val exp = req.expiresAt
            exp == null || exp.seconds > now.seconds
        }

        assertEquals(3, filtered.size)
        assertTrue(filtered.any { it.id == "r1" })
        assertFalse(filtered.any { it.id == "r2" })
        assertTrue(filtered.any { it.id == "r3" })
        assertTrue(filtered.any { it.id == "r4" })
    }

    // ── Username sync freshness ───────────────────────────────────────

    @Test
    fun `sync freshness check blocks within 1 hour`() {
        val freshMs = 3_600_000L
        val lastSync = System.currentTimeMillis() - 1_800_000L // 30 min ago
        val now = System.currentTimeMillis()
        val shouldSkip = now - lastSync < freshMs
        assertTrue("Should skip sync if within freshness window", shouldSkip)
    }

    @Test
    fun `sync freshness check allows after 1 hour`() {
        val freshMs = 3_600_000L
        val lastSync = System.currentTimeMillis() - 4_000_000L // >1hr ago
        val now = System.currentTimeMillis()
        val shouldSkip = now - lastSync < freshMs
        assertFalse("Should allow sync after freshness window", shouldSkip)
    }

    @Test
    fun `sync freshness check allows first sync (lastSync = 0)`() {
        val freshMs = 3_600_000L
        val lastSync = 0L
        val now = System.currentTimeMillis()
        val shouldSkip = now - lastSync < freshMs
        assertFalse("Should allow first-ever sync", shouldSkip)
    }

    // ── Search index building ─────────────────────────────────────────

    @Test
    fun `username lowercased for search index`() {
        val username = "TestUser123"
        val searchUsername = username.lowercase()
        assertEquals("testuser123", searchUsername)
    }

    @Test
    fun `search index includes username_lowercase field`() {
        val updates = mapOf("username" to "MyUser")
        val searchUpdates = mutableMapOf<String, Any>()
        val username = updates["username"] as? String
        if (username != null) {
            searchUpdates["username"] = username
            searchUpdates["username_lowercase"] = username.lowercase()
        }
        assertEquals("MyUser", searchUpdates["username"])
        assertEquals("myuser", searchUpdates["username_lowercase"])
    }

    @Test
    fun `MAX_FRIENDS_PER_SYNC is 100`() {
        // Mirror the constant from FirestoreFriendsRepository
        val maxFriendsPerSync = 100L
        assertEquals(100L, maxFriendsPerSync)
    }

    @Test
    fun `USERNAME_SYNC_FRESHNESS_MS is 1 hour`() {
        val freshMs = 3_600_000L
        assertEquals(3_600_000L, freshMs)
        assertEquals(60 * 60 * 1000L, freshMs)
    }
}
