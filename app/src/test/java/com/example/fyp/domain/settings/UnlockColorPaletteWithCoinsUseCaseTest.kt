package com.example.fyp.domain.settings

import com.example.fyp.data.cloud.CloudQuizClient
import com.example.fyp.data.cloud.SpendCoinsResult
import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.*

/**
 * Fake UserSettingsRepository that records calls to unlockColorPalette.
 * Avoids Mockito, which cannot handle @JvmInline value class parameters.
 */
private class FakeUserSettingsRepository : UserSettingsRepository {
    val unlockedPalettes = mutableListOf<Pair<UserId, PaletteId>>()

    override suspend fun unlockColorPalette(userId: UserId, paletteId: PaletteId) {
        unlockedPalettes.add(userId to paletteId)
    }

    // Unused stubs
    override fun observeUserSettings(userId: UserId): Flow<UserSettings> = emptyFlow()
    override suspend fun fetchUserSettings(userId: UserId): UserSettings = error("not used")
    override suspend fun setFontSizeScale(userId: UserId, scale: Float) {}
    override suspend fun setPrimaryLanguage(userId: UserId, languageCode: LanguageCode) {}
    override suspend fun setThemeMode(userId: UserId, themeMode: String) {}
    override suspend fun setColorPalette(userId: UserId, paletteId: PaletteId) {}
    override suspend fun setVoiceForLanguage(userId: UserId, languageCode: LanguageCode, voiceName: VoiceName) {}
    override suspend fun setAutoThemeEnabled(userId: UserId, enabled: Boolean) {}
    override suspend fun setNotificationPref(userId: UserId, field: String, enabled: Boolean) {}
    override suspend fun expandHistoryViewLimit(userId: UserId, newLimit: Int) {}
    override suspend fun setLastUsernameChangeMs(userId: UserId, timestampMs: Long) {}
}

/**
 * Unit tests for UnlockColorPaletteWithCoinsUseCase.
 *
 * The use case delegates paid palette unlocks to the server-side spendCoins
 * Cloud Function via CloudQuizClient. Free palettes are unlocked directly.
 */
class UnlockColorPaletteWithCoinsUseCaseTest {

    private lateinit var settingsRepo: FakeUserSettingsRepository
    private lateinit var cloudClient: CloudQuizClient
    private lateinit var useCase: UnlockColorPaletteWithCoinsUseCase

    @Before
    fun setup() {
        settingsRepo = FakeUserSettingsRepository()
        cloudClient = mock()
        useCase = UnlockColorPaletteWithCoinsUseCase(settingsRepo, cloudClient)
    }

    // --- Free Palette Tests ---

    @Test
    fun `free palette unlocks directly without cloud call`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("default")

        val result = useCase(userId, paletteId, cost = 0)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        assertEquals(1, settingsRepo.unlockedPalettes.size)
        assertEquals(userId to paletteId, settingsRepo.unlockedPalettes[0])
        verifyNoInteractions(cloudClient)
    }

    @Test
    fun `free palette with zero cost never calls cloud client`() = runBlocking {
        useCase(UserId("user123"), PaletteId("freebie"), cost = 0)

        verifyNoInteractions(cloudClient)
    }

    // --- Paid Palette Success Tests ---

    @Test
    fun `paid palette returns success when server confirms`() = runBlocking {
        val paletteId = PaletteId("ocean")
        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = true, newBalance = 90
            )
        }

        val result = useCase(UserId("user123"), paletteId, cost = 10)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        // Server handles the unlock — client settingsRepo must NOT be called for paid palettes
        assertTrue("settingsRepo must NOT be called for paid palettes", settingsRepo.unlockedPalettes.isEmpty())
    }

    @Test
    fun `paid palette passes paletteId to cloud client`() {
        runBlocking {
            cloudClient.stub {
                onBlocking { spendCoinsForPaletteUnlock("sunset") } doReturn SpendCoinsResult(
                    success = true, newBalance = 0
                )
            }

            useCase(UserId("user123"), PaletteId("sunset"), cost = 10)

            verify(cloudClient).spendCoinsForPaletteUnlock("sunset")
        }
    }

    // --- Insufficient Coins Tests ---

    @Test
    fun `paid palette returns insufficient when server reports insufficient_coins`() = runBlocking {
        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = false, reason = "insufficient_coins"
            )
        }

        val result = useCase(UserId("user123"), PaletteId("ocean"), cost = 10)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
        assertTrue("settingsRepo must NOT be called on failure", settingsRepo.unlockedPalettes.isEmpty())
    }

    // --- Already Unlocked ---

    @Test
    fun `paid palette returns success when server reports already_unlocked`() = runBlocking {
        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = false, reason = "already_unlocked"
            )
        }

        val result = useCase(UserId("user123"), PaletteId("ocean"), cost = 10)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
    }

    // --- Unknown Error Fallback ---

    @Test
    fun `paid palette returns insufficient for unknown server error reason`() = runBlocking {
        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = false, reason = "unknown_error"
            )
        }

        val result = useCase(UserId("user123"), PaletteId("ocean"), cost = 10)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
    }

    @Test
    fun `paid palette returns insufficient when server reason is null`() = runBlocking {
        cloudClient.stub {
            onBlocking { spendCoinsForPaletteUnlock("ocean") } doReturn SpendCoinsResult(
                success = false, reason = null
            )
        }

        val result = useCase(UserId("user123"), PaletteId("ocean"), cost = 10)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
    }

    // --- Scenario Tests ---

    @Test
    fun `multiple free palettes unlock directly each time`() = runBlocking {
        val userId = UserId("user123")

        useCase(userId, PaletteId("palette1"), cost = 0)
        useCase(userId, PaletteId("palette2"), cost = 0)

        assertEquals(2, settingsRepo.unlockedPalettes.size)
        verifyNoInteractions(cloudClient)
    }
}
