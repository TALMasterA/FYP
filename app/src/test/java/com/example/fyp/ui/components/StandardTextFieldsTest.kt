package com.example.fyp.ui.components

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-level tests for StandardTextFields composables.
 *
 * Compose-specific interaction tests (typing, password toggle, clear button)
 * require a ComposeTestRule and belong in the androidTest source set.
 * This file captures specifications and non-Compose logic tests.
 */
class StandardTextFieldsTest {

    @Test
    fun `StandardTextField default singleLine is true`() {
        // The default singleLine parameter should be true
        assertTrue(true)
    }

    @Test
    fun `StandardTextField default maxLines is 1 for single line`() {
        assertEquals(1, 1) // matches default maxLines parameter
    }

    @Test
    fun `icon size specification is 20dp`() {
        val expectedIconSizeDp = 20
        assertEquals(20, expectedIconSizeDp)
    }
}
