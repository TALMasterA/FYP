package com.example.fyp.model.friends

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SharedItem model, SharedItemType, and SharedItemStatus enums.
 */
class SharedItemTest {

    @Test
    fun `default SharedItem has WORD type and PENDING status`() {
        val item = SharedItem()
        assertEquals(SharedItemType.WORD, item.type)
        assertEquals(SharedItemStatus.PENDING, item.status)
    }

    @Test
    fun `default SharedItem has empty string fields`() {
        val item = SharedItem()
        assertEquals("", item.itemId)
        assertEquals("", item.fromUserId)
        assertEquals("", item.fromUsername)
        assertEquals("", item.toUserId)
        assertTrue(item.content.isEmpty())
    }

    @Test
    fun `SharedItem stores word content correctly`() {
        val wordContent = mapOf(
            "sourceText" to "hello" as Any,
            "targetText" to "hola" as Any,
            "sourceLang" to "en-US" as Any,
            "targetLang" to "es-ES" as Any
        )
        val item = SharedItem(
            itemId = "item1",
            fromUserId = "user1",
            fromUsername = "sender",
            toUserId = "user2",
            type = SharedItemType.WORD,
            content = wordContent
        )
        assertEquals("hello", item.content["sourceText"])
        assertEquals("hola", item.content["targetText"])
    }

    @Test
    fun `SharedItemType has all expected values`() {
        val types = SharedItemType.entries
        assertEquals(3, types.size)
        assertTrue(types.contains(SharedItemType.WORD))
        assertTrue(types.contains(SharedItemType.LEARNING_SHEET))
        assertTrue(types.contains(SharedItemType.QUIZ))
    }

    @Test
    fun `SharedItemStatus has all expected values`() {
        val statuses = SharedItemStatus.entries
        assertEquals(3, statuses.size)
        assertTrue(statuses.contains(SharedItemStatus.PENDING))
        assertTrue(statuses.contains(SharedItemStatus.ACCEPTED))
        assertTrue(statuses.contains(SharedItemStatus.DISMISSED))
    }

    @Test
    fun `SharedItem copy allows status change`() {
        val original = SharedItem(itemId = "item1", status = SharedItemStatus.PENDING)
        val accepted = original.copy(status = SharedItemStatus.ACCEPTED)
        assertEquals(SharedItemStatus.ACCEPTED, accepted.status)
        assertEquals("item1", accepted.itemId)
    }

    @Test
    fun `learning sheet SharedItem has correct type`() {
        val item = SharedItem(
            type = SharedItemType.LEARNING_SHEET,
            content = mapOf("title" to "English Basics" as Any)
        )
        assertEquals(SharedItemType.LEARNING_SHEET, item.type)
        assertEquals("English Basics", item.content["title"])
    }

    @Test
    fun `quiz SharedItem has correct type`() {
        val item = SharedItem(
            type = SharedItemType.QUIZ,
            content = mapOf("quizTitle" to "Vocabulary Test" as Any)
        )
        assertEquals(SharedItemType.QUIZ, item.type)
    }
}
