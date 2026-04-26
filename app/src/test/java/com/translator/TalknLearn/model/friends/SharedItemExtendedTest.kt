package com.translator.TalknLearn.model.friends

import org.junit.Assert.*
import org.junit.Test

/**
 * Extended unit tests for SharedItem model and related enums.
 *
 * Tests:
 * 1. SharedItemType enum values
 * 2. SharedItemStatus enum values
 * 3. SharedItem default values
 * 4. SharedItem copy preserves fields
 */
class SharedItemExtendedTest {

    // ── SharedItemType ──────────────────────────────────────────────

    @Test
    fun `SharedItemType has 2 types`() {
        assertEquals(2, SharedItemType.entries.size)
    }

    @Test
    fun `SharedItemType contains WORD, LEARNING_SHEET`() {
        val types = SharedItemType.entries.map { it.name }
        assertTrue(types.contains("WORD"))
        assertTrue(types.contains("LEARNING_SHEET"))
    }

    // ── SharedItemStatus ────────────────────────────────────────────

    @Test
    fun `SharedItemStatus has 3 statuses`() {
        assertEquals(3, SharedItemStatus.entries.size)
    }

    @Test
    fun `SharedItemStatus contains PENDING, ACCEPTED, DISMISSED`() {
        val statuses = SharedItemStatus.entries.map { it.name }
        assertTrue(statuses.contains("PENDING"))
        assertTrue(statuses.contains("ACCEPTED"))
        assertTrue(statuses.contains("DISMISSED"))
    }

    // ── SharedItem defaults ─────────────────────────────────────────

    @Test
    fun `SharedItem defaults are correct`() {
        val item = SharedItem()

        assertEquals("", item.itemId)
        assertEquals("", item.fromUserId)
        assertEquals("", item.fromUsername)
        assertEquals("", item.toUserId)
        assertEquals(SharedItemType.WORD, item.type)
        assertTrue(item.content.isEmpty())
        assertEquals(SharedItemStatus.PENDING, item.status)
    }

    @Test
    fun `SharedItem with custom values`() {
        val content = mapOf<String, Any>("sourceText" to "hello", "targetText" to "hola")
        val item = SharedItem(
            itemId = "item1",
            fromUserId = "user1",
            fromUsername = "Alice",
            toUserId = "user2",
            type = SharedItemType.LEARNING_SHEET,
            content = content,
            status = SharedItemStatus.ACCEPTED
        )

        assertEquals("item1", item.itemId)
        assertEquals("Alice", item.fromUsername)
        assertEquals(SharedItemType.LEARNING_SHEET, item.type)
        assertEquals(SharedItemStatus.ACCEPTED, item.status)
        assertEquals("hello", item.content["sourceText"])
    }

    @Test
    fun `SharedItem copy changes only specified fields`() {
        val original = SharedItem(
            itemId = "item1",
            fromUserId = "user1",
            toUserId = "user2",
            type = SharedItemType.WORD,
            status = SharedItemStatus.PENDING
        )

        val updated = original.copy(status = SharedItemStatus.ACCEPTED)

        assertEquals("item1", updated.itemId)
        assertEquals("user1", updated.fromUserId)
        assertEquals(SharedItemType.WORD, updated.type)
        assertEquals(SharedItemStatus.ACCEPTED, updated.status)
    }
}
