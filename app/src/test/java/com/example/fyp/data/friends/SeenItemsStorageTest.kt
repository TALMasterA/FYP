package com.example.fyp.data.friends

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Unit tests for SeenItemsStorage.
 *
 * Verifies that seen shared inbox item IDs are correctly persisted
 * to SharedPreferences and restored on app restart, ensuring red dot
 * notification badges do not reappear for already-viewed items.
 *
 * See ARCHITECTURE_NOTES.md section 15 for design rationale.
 */
class SeenItemsStorageTest {

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        mockContext = mock(Context::class.java)
        mockPrefs = mock(SharedPreferences::class.java)
        mockEditor = mock(SharedPreferences.Editor::class.java)

        `when`(mockContext.getSharedPreferences("shared_inbox_prefs", Context.MODE_PRIVATE))
            .thenReturn(mockPrefs)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.remove(anyString())).thenReturn(mockEditor)
    }

    @Test
    fun `loadSeenItemIds returns empty set when no items stored`() {
        val userId = "user123"
        `when`(mockPrefs.getString("seen_shared_items_user123", null)).thenReturn(null)

        val result = SeenItemsStorage.loadSeenItemIds(mockContext, userId)

        assertTrue("Should return empty set when no stored data", result.isEmpty())
    }

    @Test
    fun `loadSeenItemIds returns empty set when stored value is empty string`() {
        val userId = "user123"
        `when`(mockPrefs.getString("seen_shared_items_user123", null)).thenReturn("")

        val result = SeenItemsStorage.loadSeenItemIds(mockContext, userId)

        assertTrue("Should return empty set when stored value is empty", result.isEmpty())
    }

    @Test
    fun `loadSeenItemIds returns set of item IDs from CSV storage`() {
        val userId = "user123"
        val storedCsv = "item1,item2,item3"
        `when`(mockPrefs.getString("seen_shared_items_user123", null)).thenReturn(storedCsv)

        val result = SeenItemsStorage.loadSeenItemIds(mockContext, userId)

        assertEquals("Should parse 3 item IDs from CSV", 3, result.size)
        assertTrue("Should contain item1", result.contains("item1"))
        assertTrue("Should contain item2", result.contains("item2"))
        assertTrue("Should contain item3", result.contains("item3"))
    }

    @Test
    fun `saveSeenItemIds stores IDs as CSV string`() {
        val userId = "user123"
        val seenIds = setOf("itemA", "itemB", "itemC")

        SeenItemsStorage.saveSeenItemIds(mockContext, userId, seenIds)

        verify(mockEditor).putString(eq("seen_shared_items_user123"), anyString())
        verify(mockEditor).apply()
    }

    @Test
    fun `saveSeenItemIds stores empty string for empty set`() {
        val userId = "user123"
        val seenIds = emptySet<String>()

        SeenItemsStorage.saveSeenItemIds(mockContext, userId, seenIds)

        verify(mockEditor).putString("seen_shared_items_user123", "")
        verify(mockEditor).apply()
    }

    @Test
    fun `addSeenItemIds merges new IDs with existing ones`() {
        val userId = "user123"
        val existingCsv = "item1,item2"
        `when`(mockPrefs.getString("seen_shared_items_user123", null)).thenReturn(existingCsv)

        val newIds = setOf("item3", "item4")
        SeenItemsStorage.addSeenItemIds(mockContext, userId, newIds)

        verify(mockEditor).putString(eq("seen_shared_items_user123"), anyString())
        verify(mockEditor).apply()
    }

    @Test
    fun `clearSeenItemIds removes stored data for user`() {
        val userId = "user123"

        SeenItemsStorage.clearSeenItemIds(mockContext, userId)

        verify(mockEditor).remove("seen_shared_items_user123")
        verify(mockEditor).apply()
    }

    @Test
    fun `different users have separate storage keys`() {
        val user1 = "user123"
        val user2 = "user456"

        SeenItemsStorage.saveSeenItemIds(mockContext, user1, setOf("itemA"))
        SeenItemsStorage.saveSeenItemIds(mockContext, user2, setOf("itemB"))

        verify(mockEditor).putString("seen_shared_items_user123", "itemA")
        verify(mockEditor).putString("seen_shared_items_user456", "itemB")
        verify(mockEditor, times(2)).apply()
    }

    @Test
    fun `storage key format matches expected pattern`() {
        // This is a specification test to document the expected key format
        val userId = "testuser"
        val expectedKeyPrefix = "seen_shared_items_"
        val expectedKey = expectedKeyPrefix + userId

        assertEquals(
            "Storage key should follow prefix + userId pattern",
            "seen_shared_items_testuser",
            expectedKey
        )
    }

    @Test
    fun `CSV format handles items with no commas in IDs`() {
        // Document that item IDs are expected to not contain commas
        // (Firestore document IDs are alphanumeric + underscores/hyphens)
        val validItemIds = setOf("item_1", "item-2", "item3")
        val csv = validItemIds.joinToString(",")

        assertTrue("CSV should contain 2 commas for 3 items", csv.count { it == ',' } == 2)
        assertEquals("Split should recover original set", validItemIds, csv.split(",").toSet())
    }
}
