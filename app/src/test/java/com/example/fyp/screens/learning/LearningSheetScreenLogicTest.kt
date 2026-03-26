package com.example.fyp.screens.learning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LearningSheetScreenLogicTest {

    @Test
    fun `resolveDisplayedSavedCount prefers latest metadata from learning screen`() {
        val displayed = resolveDisplayedSavedCount(
            latestSheetCount = 42,
            loadedSheetCount = 30
        )

        assertEquals(42, displayed)
    }

    @Test
    fun `resolveDisplayedSavedCount falls back to loaded sheet when metadata is missing`() {
        val displayed = resolveDisplayedSavedCount(
            latestSheetCount = null,
            loadedSheetCount = 30
        )

        assertEquals(30, displayed)
    }

    @Test
    fun `resolveDisplayedSavedCount returns null when both counts are missing`() {
        val displayed = resolveDisplayedSavedCount(
            latestSheetCount = null,
            loadedSheetCount = null
        )

        assertNull(displayed)
    }
}

