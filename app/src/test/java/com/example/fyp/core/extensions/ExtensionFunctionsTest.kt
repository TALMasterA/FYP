package com.example.fyp.core.extensions

import org.junit.Assert.*
import org.junit.Test

class ExtensionFunctionsTest {

    // ── String.truncate ─────────────────────────────────────────────

    @Test
    fun `truncate - short string unchanged`() {
        assertEquals("hello", "hello".truncate(10))
    }

    @Test
    fun `truncate - exact length unchanged`() {
        assertEquals("hello", "hello".truncate(5))
    }

    @Test
    fun `truncate - long string is shortened with ellipsis`() {
        val result = "hello world".truncate(5)
        assertEquals("hello\u2026", result)
    }

    @Test
    fun `truncate - maxLength 0 returns only ellipsis`() {
        assertEquals("\u2026", "hello".truncate(0))
    }

    @Test
    fun `truncate - empty string unchanged`() {
        assertEquals("", "".truncate(5))
    }

    @Test
    fun `truncate - maxLength 1 takes first char plus ellipsis`() {
        assertEquals("h\u2026", "hello".truncate(1))
    }

    // ── String.capitalizeFirst ──────────────────────────────────────

    @Test
    fun `capitalizeFirst - lowercase first char`() {
        assertEquals("Hello", "hello".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst - already capitalised`() {
        assertEquals("Hello", "Hello".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst - single char`() {
        assertEquals("A", "a".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst - empty string`() {
        assertEquals("", "".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst - number first`() {
        assertEquals("1hello", "1hello".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst - all caps stays all caps`() {
        assertEquals("HELLO", "HELLO".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst - unicode character`() {
        val result = "über".capitalizeFirst()
        assertEquals("Über", result)
    }

    // ── List.orNullIfEmpty ──────────────────────────────────────────

    @Test
    fun `orNullIfEmpty - non-empty list returns itself`() {
        val list = listOf(1, 2, 3)
        assertSame(list, list.orNullIfEmpty())
    }

    @Test
    fun `orNullIfEmpty - empty list returns null`() {
        val list = emptyList<Int>()
        assertNull(list.orNullIfEmpty())
    }

    @Test
    fun `orNullIfEmpty - single element list returns itself`() {
        val list = listOf("a")
        assertNotNull(list.orNullIfEmpty())
        assertEquals(listOf("a"), list.orNullIfEmpty())
    }
}
