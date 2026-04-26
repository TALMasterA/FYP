package com.translator.TalknLearn.screens.settings

import com.translator.TalknLearn.model.user.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the ShopUiState data class defaults and state transitions.
 * Tests shop screen UI state management including coin balance display,
 * purchase flow states, and error handling.
 */
class ShopUiStateTest {

    // ── Default State ──────────────────────────────────────────────

    @Test
    fun `default state is loading`() {
        val state = ShopUiState()
        assertTrue(state.isLoading)
    }

    @Test
    fun `default coin balance is 0`() {
        val state = ShopUiState()
        assertEquals(0, state.coinBalance)
    }

    @Test
    fun `default history limit is BASE_HISTORY_LIMIT`() {
        val state = ShopUiState()
        assertEquals(UserSettings.BASE_HISTORY_LIMIT, state.currentHistoryLimit)
    }

    @Test
    fun `default palette is default`() {
        val state = ShopUiState()
        assertEquals("default", state.currentPaletteId)
    }

    @Test
    fun `default unlocked palettes contains only default`() {
        val state = ShopUiState()
        assertEquals(listOf("default"), state.unlockedPalettes)
    }

    @Test
    fun `default is not purchasing`() {
        val state = ShopUiState()
        assertFalse(state.isPurchasing)
    }

    @Test
    fun `default errors are null`() {
        val state = ShopUiState()
        assertNull(state.purchaseError)
        assertNull(state.purchaseSuccess)
        assertNull(state.unlockError)
    }

    // ── State Transitions ──────────────────────────────────────────

    @Test
    fun `purchasing state can be toggled`() {
        val state = ShopUiState(isPurchasing = true)
        assertTrue(state.isPurchasing)
    }

    @Test
    fun `coin balance updates correctly`() {
        val state = ShopUiState(coinBalance = 5000)
        assertEquals(5000, state.coinBalance)
    }

    @Test
    fun `history limit can be set to max`() {
        val state = ShopUiState(currentHistoryLimit = UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(UserSettings.MAX_HISTORY_LIMIT, state.currentHistoryLimit)
    }

    @Test
    fun `multiple palettes can be unlocked`() {
        val state = ShopUiState(
            unlockedPalettes = listOf("default", "ocean", "sunset", "forest")
        )
        assertEquals(4, state.unlockedPalettes.size)
        assertTrue(state.unlockedPalettes.contains("ocean"))
    }

    @Test
    fun `purchase error and success can coexist with purchase state`() {
        val state = ShopUiState(
            isPurchasing = false,
            purchaseError = null,
            purchaseSuccess = "Expanded!"
        )
        assertFalse(state.isPurchasing)
        assertNull(state.purchaseError)
        assertEquals("Expanded!", state.purchaseSuccess)
    }

    // ── Error States ──────────────────────────────────────────────

    @Test
    fun `purchase error preserved in state`() {
        val state = ShopUiState(purchaseError = "Insufficient coins")
        assertEquals("Insufficient coins", state.purchaseError)
    }

    @Test
    fun `unlock error preserved in state`() {
        val state = ShopUiState(unlockError = "Insufficient coins")
        assertEquals("Insufficient coins", state.unlockError)
    }

    @Test
    fun `purchase success preserved in state`() {
        val state = ShopUiState(purchaseSuccess = "History limit expanded to 40 records!")
        assertNotNull(state.purchaseSuccess)
        assertTrue(state.purchaseSuccess!!.contains("expanded"))
    }

    // ── Copy Semantics ────────────────────────────────────────────

    @Test
    fun `copy preserves unchanged fields`() {
        val original = ShopUiState(
            isLoading = false,
            coinBalance = 3000,
            currentHistoryLimit = 40,
            currentPaletteId = "ocean",
            unlockedPalettes = listOf("default", "ocean")
        )
        val updated = original.copy(coinBalance = 2000)

        assertEquals(2000, updated.coinBalance)
        assertEquals(40, updated.currentHistoryLimit) // Preserved
        assertEquals("ocean", updated.currentPaletteId) // Preserved
        assertEquals(2, updated.unlockedPalettes.size) // Preserved
    }

    @Test
    fun `copy can update multiple fields at once`() {
        val state = ShopUiState()
        val updated = state.copy(
            isLoading = false,
            coinBalance = 5000,
            currentHistoryLimit = 50,
            currentPaletteId = "sunset"
        )

        assertFalse(updated.isLoading)
        assertEquals(5000, updated.coinBalance)
        assertEquals(50, updated.currentHistoryLimit)
        assertEquals("sunset", updated.currentPaletteId)
    }
}
