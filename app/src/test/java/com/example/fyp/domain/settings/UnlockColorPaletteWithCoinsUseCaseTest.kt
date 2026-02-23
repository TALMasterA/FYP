package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.example.fyp.model.user.UserSettings
import com.example.fyp.domain.learning.GeneratedQuizDoc
import com.example.fyp.domain.learning.QuizMetadata
import com.example.fyp.domain.learning.QuizRepository
import com.example.fyp.model.LanguageCode as LC
import com.example.fyp.model.QuizAttempt
import com.example.fyp.model.QuizQuestion
import com.example.fyp.model.QuizStats
import com.example.fyp.model.UserCoinStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

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
}

/**
 * Fake QuizRepository that returns a configurable deductCoins result.
 * Avoids Mockito, which cannot handle @JvmInline value class parameters.
 */
private class FakeQuizRepository : QuizRepository {
    var deductCoinsResult: Int = 0
    var deductCoinsCalled = false

    override suspend fun deductCoins(uid: UserId, amount: Int): Int {
        deductCoinsCalled = true
        return deductCoinsResult
    }

    // Unused stubs
    override suspend fun saveAttempt(uid: UserId, attempt: QuizAttempt): String = ""
    override suspend fun getAttempt(uid: UserId, attemptId: String): QuizAttempt? = null
    override suspend fun getAttemptsByLanguagePair(uid: UserId, primaryCode: LC, targetCode: LC, limit: Long): List<QuizAttempt> = emptyList()
    override suspend fun getQuizStats(uid: UserId, primaryCode: LC, targetCode: LC): QuizStats? = null
    override suspend fun getRecentAttempts(uid: UserId, limit: Long): List<QuizAttempt> = emptyList()
    override suspend fun getGeneratedQuizDoc(uid: UserId, primaryCode: LC, targetCode: LC): GeneratedQuizDoc? = null
    override suspend fun getBatchQuizMetadata(uid: UserId, primary: LC, targets: List<String>): Map<String, QuizMetadata> = emptyMap()
    override suspend fun upsertGeneratedQuiz(uid: UserId, primaryCode: LC, targetCode: LC, quizData: String, historyCountAtGenerate: Int) {}
    override suspend fun getGeneratedQuizQuestions(uid: UserId, primaryCode: LC, targetCode: LC): List<QuizQuestion> = emptyList()
    override fun observeUserCoinStats(uid: UserId): Flow<UserCoinStats> = emptyFlow()
    override suspend fun fetchUserCoinStats(uid: UserId): UserCoinStats? = null
    override suspend fun getLastAwardedQuizCount(uid: UserId, primaryCode: LC, targetCode: LC): Int? = null
    override suspend fun awardCoinsIfEligible(uid: UserId, attempt: QuizAttempt, latestHistoryCount: Int?): Boolean = false
}

/**
 * Unit tests for UnlockColorPaletteWithCoinsUseCase.
 */
class UnlockColorPaletteWithCoinsUseCaseTest {

    private lateinit var settingsRepo: FakeUserSettingsRepository
    private lateinit var quizRepo: FakeQuizRepository
    private lateinit var useCase: UnlockColorPaletteWithCoinsUseCase

    @Before
    fun setup() {
        settingsRepo = FakeUserSettingsRepository()
        quizRepo = FakeQuizRepository()
        useCase = UnlockColorPaletteWithCoinsUseCase(settingsRepo, quizRepo)
    }

    // --- Free Palette Tests ---

    @Test
    fun `free palette unlocks without coin deduction`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("default")

        val result = useCase(userId, paletteId, cost = 0)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        assertEquals(1, settingsRepo.unlockedPalettes.size)
        assertEquals(userId to paletteId, settingsRepo.unlockedPalettes[0])
        assertFalse("Coins should NOT be deducted for free palette", quizRepo.deductCoinsCalled)
    }

    // --- Paid Palette Success Tests ---

    @Test
    fun `paid palette unlocks when user has sufficient coins`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        quizRepo.deductCoinsResult = 50 // 150 - 100 = 50 remaining

        val result = useCase(userId, paletteId, cost = 100)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        assertTrue(quizRepo.deductCoinsCalled)
        assertEquals(1, settingsRepo.unlockedPalettes.size)
        assertEquals(userId to paletteId, settingsRepo.unlockedPalettes[0])
    }

    @Test
    fun `paid palette unlocks when user has exact coins needed`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        quizRepo.deductCoinsResult = 0 // 100 - 100 = 0 remaining

        val result = useCase(userId, paletteId, cost = 100)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        assertTrue(quizRepo.deductCoinsCalled)
        assertEquals(1, settingsRepo.unlockedPalettes.size)
    }

    // --- Insufficient Coins Tests ---

    @Test
    fun `paid palette fails when user has insufficient coins`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        quizRepo.deductCoinsResult = -1 // Insufficient

        val result = useCase(userId, paletteId, cost = 100)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
        assertTrue(quizRepo.deductCoinsCalled)
        assertTrue("Palette must NOT be unlocked on failure", settingsRepo.unlockedPalettes.isEmpty())
    }

    @Test
    fun `paid palette fails when user has zero coins`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("premium")
        quizRepo.deductCoinsResult = -1 // 0 - 50 = insufficient

        val result = useCase(userId, paletteId, cost = 50)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result)
        assertTrue("Palette must NOT be unlocked on failure", settingsRepo.unlockedPalettes.isEmpty())
    }

    // --- Edge Cases ---

    @Test
    fun `expensive palette unlocks with sufficient high balance`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("luxury")
        quizRepo.deductCoinsResult = 500 // 1500 - 1000 = 500

        val result = useCase(userId, paletteId, cost = 1000)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        assertTrue(quizRepo.deductCoinsCalled)
        assertEquals(1, settingsRepo.unlockedPalettes.size)
    }

    @Test
    fun `cheap palette unlocks with minimal cost`() = runBlocking {
        val userId = UserId("user123")
        val paletteId = PaletteId("basic")
        quizRepo.deductCoinsResult = 99 // 100 - 1 = 99

        val result = useCase(userId, paletteId, cost = 1)

        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result)
        assertTrue(quizRepo.deductCoinsCalled)
        assertEquals(1, settingsRepo.unlockedPalettes.size)
    }

    // --- Scenario Tests ---

    @Test
    fun `multiple unlocks deplete coin balance correctly`() = runBlocking {
        val userId = UserId("user123")

        // First unlock: 500 - 100 = 400 remaining
        quizRepo.deductCoinsResult = 400
        val result1 = useCase(userId, PaletteId("palette1"), 100)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result1)

        // Second unlock: 400 - 200 = 200 remaining
        quizRepo.deductCoinsResult = 200
        val result2 = useCase(userId, PaletteId("palette2"), 200)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.Success, result2)

        // Third unlock fails: 200 - 300 = insufficient
        quizRepo.deductCoinsResult = -1
        val result3 = useCase(userId, PaletteId("palette3"), 300)
        assertEquals(UnlockColorPaletteWithCoinsUseCase.Result.InsufficientCoins, result3)

        // Exactly 2 palettes unlocked (palette1 + palette2), palette3 was NOT unlocked
        assertEquals(2, settingsRepo.unlockedPalettes.size)
        assertEquals(userId to PaletteId("palette1"), settingsRepo.unlockedPalettes[0])
        assertEquals(userId to PaletteId("palette2"), settingsRepo.unlockedPalettes[1])
    }
}
