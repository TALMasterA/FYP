package com.example.fyp.domain.settings

import com.example.fyp.data.learning.FirestoreQuizRepository
import com.example.fyp.data.settings.UserSettingsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for UnlockColorPaletteWithCoinsUseCase.
 *
 * Tests the business logic for unlocking color palettes with coins:
 * 1. Free palettes (cost=0) should unlock without coin deduction
 * 2. Paid palettes should deduct coins and unlock on success
 * 3. Insufficient coins should return error without unlocking
 * 4. Proper ordering: deduct coins THEN unlock (transaction safety)
 */
class UnlockColorPaletteWithCoinsUseCaseTest {

    private lateinit var settingsRepo: UserSettingsRepository
    private lateinit var quizRepo: FirestoreQuizRepository
    private lateinit var useCase: UnlockColorPaletteWithCoinsUseCase

    @Before
    fun setup() {
        settingsRepo = mock()
        quizRepo = mock()
        useCase = UnlockColorPaletteWithCoinsUseCase(settingsRepo, quizRepo)
    }

    // --- Free Palette Tests ---

    @Test
    fun `free palette unlocks without coin deduction`() = runBlocking {
        val userId = "user123"
        val paletteId = "default"
        val cost = 0

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
        verifyNoInteractions(quizRepo) // Should NOT deduct coins for free palette
    }

    // --- Paid Palette Success Tests ---

    @Test
    fun `paid palette unlocks when user has sufficient coins`() = runBlocking {
        val userId = "user123"
        val paletteId = "premium"
        val cost = 100

        whenever(quizRepo.deductCoins(userId, cost)).thenReturn(50) // 150 - 100 = 50 remaining

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    @Test
    fun `paid palette unlocks when user has exact coins needed`() = runBlocking {
        val userId = "user123"
        val paletteId = "premium"
        val cost = 100

        whenever(quizRepo.deductCoins(userId, cost)).thenReturn(0) // 100 - 100 = 0 remaining

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    // --- Insufficient Coins Tests ---

    @Test
    fun `paid palette fails when user has insufficient coins`() = runBlocking {
        val userId = "user123"
        val paletteId = "premium"
        val cost = 100

        whenever(quizRepo.deductCoins(userId, cost)).thenReturn(-1) // Insufficient

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
        verify(quizRepo).deductCoins(userId, cost)
        // Should NOT unlock palette when deduction fails
        verify(settingsRepo, never()).unlockColorPalette(any(), any())
    }

    @Test
    fun `paid palette fails when user has zero coins`() = runBlocking {
        val userId = "user123"
        val paletteId = "premium"
        val cost = 50

        whenever(quizRepo.deductCoins(userId, cost)).thenReturn(-1) // 0 - 50 = -50, insufficient

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
        verify(settingsRepo, never()).unlockColorPalette(any(), any())
    }

    // --- Edge Cases ---

    @Test
    fun `expensive palette unlocks with sufficient high balance`() = runBlocking {
        val userId = "user123"
        val paletteId = "luxury"
        val cost = 1000

        whenever(quizRepo.deductCoins(userId, cost)).thenReturn(500) // 1500 - 1000 = 500

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    @Test
    fun `cheap palette unlocks with minimal cost`() = runBlocking {
        val userId = "user123"
        val paletteId = "basic"
        val cost = 1

        whenever(quizRepo.deductCoins(userId, cost)).thenReturn(99) // 100 - 1 = 99

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    // --- Scenario Tests ---

    @Test
    fun `multiple unlocks deplete coin balance correctly`() = runBlocking {
        val userId = "user123"

        // First unlock: 500 - 100 = 400 remaining
        whenever(quizRepo.deductCoins(userId, 100)).thenReturn(400)
        val result1 = useCase(userId, "palette1", 100)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result1)

        // Second unlock: 400 - 200 = 200 remaining
        whenever(quizRepo.deductCoins(userId, 200)).thenReturn(200)
        val result2 = useCase(userId, "palette2", 200)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result2)

        // Third unlock fails: 200 - 300 = -100 insufficient
        whenever(quizRepo.deductCoins(userId, 300)).thenReturn(-1)
        val result3 = useCase(userId, "palette3", 300)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result3)

        verify(settingsRepo).unlockColorPalette(userId, "palette1")
        verify(settingsRepo).unlockColorPalette(userId, "palette2")
        verify(settingsRepo, never()).unlockColorPalette(userId, "palette3")
    }
}
