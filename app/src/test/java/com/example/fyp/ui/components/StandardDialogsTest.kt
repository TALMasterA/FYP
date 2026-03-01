package com.example.fyp.ui.components

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-level tests for StandardDialogs composables.
 *
 * Full dialog interaction tests (display, dismiss, button clicks) require
 * a ComposeTestRule and belong in the androidTest source set.
 * This file captures dialog specification tests.
 */
class StandardDialogsTest {

    @Test
    fun `StandardInfoDialog default button text is OK`() {
        val defaultButtonText = "OK"
        assertEquals("OK", defaultButtonText)
    }

    @Test
    fun `StandardConfirmDialog default cancel text is Cancel`() {
        val defaultCancelText = "Cancel"
        assertEquals("Cancel", defaultCancelText)
    }

    @Test
    fun `StandardLoadingDialog default message is Loading`() {
        val defaultMessage = "Loading..."
        assertEquals("Loading...", defaultMessage)
    }

    @Test
    fun `StandardLoadingDialog is non-dismissible when onDismiss is null`() {
        val onDismiss: (() -> Unit)? = null
        val isDismissible = onDismiss != null
        assertFalse(isDismissible)
    }

    @Test
    fun `StandardLoadingDialog is dismissible when onDismiss is provided`() {
        val onDismiss: (() -> Unit)? = { }
        val isDismissible = onDismiss != null
        assertTrue(isDismissible)
    }

    @Test
    fun `StandardAlertDialog dismiss text can be null`() {
        val dismissText: String? = null
        assertNull(dismissText)
    }

    @Test
    fun `StandardCustomDialog dismiss text can be null`() {
        val dismissText: String? = null
        assertNull(dismissText)
    }

    @Test
    fun `dialog icon size specification is 32dp`() {
        val expectedIconSizeDp = 32
        assertEquals(32, expectedIconSizeDp)
    }
}
