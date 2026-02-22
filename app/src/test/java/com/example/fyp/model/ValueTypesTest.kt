package com.example.fyp.model

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for value type classes defined in ValueTypes.kt.
 * Tests validation logic and type safety of inline value classes.
 */
class ValueTypesTest {

    // ── UserId ──────────────────────────────────────────────────────────────

    @Test
    fun `UserId accepts valid non-blank strings`() {
        val uid = UserId("abc123")
        assertEquals("abc123", uid.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `UserId rejects blank string`() {
        UserId("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `UserId rejects whitespace-only string`() {
        UserId("   ")
    }

    // ── Username ────────────────────────────────────────────────────────────

    @Test
    fun `Username accepts valid alphanumeric with underscores`() {
        val name = Username("test_user1")
        assertEquals("test_user1", name.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Username rejects too short`() {
        Username("ab")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Username rejects too long`() {
        Username("a".repeat(21))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Username rejects special characters`() {
        Username("user@name")
    }

    @Test
    fun `Username allows exactly 3 chars`() {
        val name = Username("abc")
        assertEquals("abc", name.value)
    }

    @Test
    fun `Username allows exactly 20 chars`() {
        val name = Username("a".repeat(20))
        assertEquals(20, name.value.length)
    }

    // ── LanguageCode ────────────────────────────────────────────────────────

    @Test
    fun `LanguageCode accepts valid format`() {
        val code = LanguageCode("en-US")
        assertEquals("en-US", code.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `LanguageCode rejects invalid format`() {
        LanguageCode("english")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `LanguageCode rejects blank string`() {
        LanguageCode("")
    }

    // ── RecordId ────────────────────────────────────────────────────────────

    @Test
    fun `RecordId accepts valid string`() {
        val id = RecordId("record-123")
        assertEquals("record-123", id.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `RecordId rejects blank string`() {
        RecordId("")
    }

    // ── PaletteId ───────────────────────────────────────────────────────────

    @Test
    fun `PaletteId accepts valid string`() {
        val id = PaletteId("default")
        assertEquals("default", id.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `PaletteId rejects blank string`() {
        PaletteId("")
    }

    // ── VoiceName ───────────────────────────────────────────────────────────

    @Test
    fun `VoiceName accepts valid string`() {
        val name = VoiceName("en-US-JennyNeural")
        assertEquals("en-US-JennyNeural", name.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `VoiceName rejects blank string`() {
        VoiceName("")
    }

    // ── Extension functions ─────────────────────────────────────────────────

    @Test
    fun `toUserId extension creates valid UserId`() {
        val uid = "user123".toUserId()
        assertEquals("user123", uid.value)
    }

    @Test
    fun `toLanguageCode extension creates valid LanguageCode`() {
        val code = "ja-JP".toLanguageCode()
        assertEquals("ja-JP", code.value)
    }

    @Test
    fun `toUsername extension creates valid Username`() {
        val name = "test_user".toUsername()
        assertEquals("test_user", name.value)
    }
}
