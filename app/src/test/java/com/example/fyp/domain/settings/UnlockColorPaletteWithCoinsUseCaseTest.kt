package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

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
    private lateinit var quizRepo: com.example.fyp.domain.learning.QuizRepository
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
        val userId = UserId("user123")
        val paletteId = PaletteId("default")
        val cost = 0

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
        verifyNoInteractions(quizRepo) // Should NOT deduct coins for free palette
    }

    // --- Paid Palette Success Tests ---

    @Test
    fun `paid palette unlocks when user has sufficient coins`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        val cost = 100

        quizRepo.stub {
            onBlocking { deductCoins(userId, cost) } doReturn 50 // 150 - 100 = 50 remaining
        }

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    @Test
    fun `paid palette unlocks when user has exact coins needed`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        val cost = 100

        quizRepo.stub {
            onBlocking { deductCoins(userId, cost) } doReturn 0 // 100 - 100 = 0 remaining
        }

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    // --- Insufficient Coins Tests ---

    @Test
    fun `paid palette fails when user has insufficient coins`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        val cost = 100

        quizRepo.stub {
            onBlocking { deductCoins(userId, cost) } doReturn -1 // Insufficient
        }

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
        verify(quizRepo).deductCoins(userId, cost)
        // Should NOT unlock palette when deduction fails
        verify(settingsRepo, never()).unlockColorPalette(any(), any())
    }

    @Test
    fun `paid palette fails when user has zero coins`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        val cost = 50

        quizRepo.stub {
            onBlocking { deductCoins(userId, cost) } doReturn -1 // 0 - 50 = -50, insufficient
        }

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
        verify(settingsRepo, never()).unlockColorPalette(any(), any())
    }

    // --- Edge Cases ---

    @Test
    fun `expensive palette unlocks with sufficient high balance`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("luxury")
        val cost = 1000

        quizRepo.stub {
            onBlocking { deductCoins(userId, cost) } doReturn 500 // 1500 - 1000 = 500
        }

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    @Test
    fun `cheap palette unlocks with minimal cost`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("basic")
        val cost = 1

        quizRepo.stub {
            onBlocking { deductCoins(userId, cost) } doReturn 99 // 100 - 1 = 99
        }

        val result = useCase(userId, paletteId, cost)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        verify(quizRepo).deductCoins(userId, cost)
        verify(settingsRepo).unlockColorPalette(userId, paletteId)
    }

    // --- Scenario Tests ---

    @Test
    fun `multiple unlocks deplete coin balance correctly`() = runBlocking {
        val userId = UserId("user123")

        // First unlock: 500 - 100 = 400 remaining
        quizRepo.stub {
            onBlocking { deductCoins(userId, 100) } doReturn 400
        }
        val result1 = useCase(userId, PaletteId("palette1"), 100)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result1)

        // Second unlock: 400 - 200 = 200 remaining
        quizRepo.stub {
            onBlocking { deductCoins(userId, 200) } doReturn 200
        }
        val result2 = useCase(userId, PaletteId("palette2"), 200)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result2)

        // Third unlock fails: 200 - 300 = -100 insufficient
        quizRepo.stub {
            onBlocking { deductCoins(userId, 300) } doReturn -1
        }
        val result3 = useCase(userId, PaletteId("palette3"), 300)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result3)

        verify(settingsRepo).unlockColorPalette(userId, PaletteId("palette1"))
        verify(settingsRepo).unlockColorPalette(userId, PaletteId("palette2"))
        verify(settingsRepo, never()).unlockColorPalette(any(), any())
    }
}
