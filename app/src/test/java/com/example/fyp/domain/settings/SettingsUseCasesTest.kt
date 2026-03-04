package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Fake UserSettingsRepository that records calls to all settings methods.
 * Avoids Mockito which cannot handle @JvmInline value class parameters.
 */
private class RecordingSettingsRepository : UserSettingsRepository {
    // Record all calls for verification
    val fontSizeCalls = mutableListOf<Pair<UserId, Float>>()
    val primaryLanguageCalls = mutableListOf<Pair<UserId, LanguageCode>>()
    val themeModeCalls = mutableListOf<Pair<UserId, String>>()
    val colorPaletteCalls = mutableListOf<Pair<UserId, PaletteId>>()
    val unlockPaletteCalls = mutableListOf<Pair<UserId, PaletteId>>()
    val voiceCalls = mutableListOf<Triple<UserId, LanguageCode, VoiceName>>()
    val autoThemeCalls = mutableListOf<Pair<UserId, Boolean>>()
    val notifPrefCalls = mutableListOf<Triple<UserId, String, Boolean>>()

    override fun observeUserSettings(userId: UserId): Flow<UserSettings> = emptyFlow()
    override suspend fun fetchUserSettings(userId: UserId): UserSettings = UserSettings()
    override suspend fun setFontSizeScale(userId: UserId, scale: Float) {
        fontSizeCalls.add(userId to scale)
    }
    override suspend fun setPrimaryLanguage(userId: UserId, languageCode: LanguageCode) {
        primaryLanguageCalls.add(userId to languageCode)
    }
    override suspend fun setThemeMode(userId: UserId, themeMode: String) {
        themeModeCalls.add(userId to themeMode)
    }
    override suspend fun setColorPalette(userId: UserId, paletteId: PaletteId) {
        colorPaletteCalls.add(userId to paletteId)
    }
    override suspend fun unlockColorPalette(userId: UserId, paletteId: PaletteId) {
        unlockPaletteCalls.add(userId to paletteId)
    }
    override suspend fun setVoiceForLanguage(userId: UserId, languageCode: LanguageCode, voiceName: VoiceName) {
        voiceCalls.add(Triple(userId, languageCode, voiceName))
    }
    override suspend fun setAutoThemeEnabled(userId: UserId, enabled: Boolean) {
        autoThemeCalls.add(userId to enabled)
    }
    override suspend fun setNotificationPref(userId: UserId, field: String, enabled: Boolean) {
        notifPrefCalls.add(Triple(userId, field, enabled))
    }
    override suspend fun expandHistoryViewLimit(userId: UserId, newLimit: Int) {}
}

/**
 * Unit tests for all settings use cases.
 * Each use case is a thin wrapper around UserSettingsRepository.
 */
class SettingsUseCasesTest {

    private lateinit var repo: RecordingSettingsRepository
    private val testUserId = UserId("testUser123")

    @Before
    fun setup() {
        repo = RecordingSettingsRepository()
    }

    // ── SetFontSizeScaleUseCase ──────────────────────────────────────

    @Test
    fun `SetFontSizeScale delegates to repository with validated scale`() = runBlocking {
        val useCase = SetFontSizeScaleUseCase(repo)
        useCase(testUserId, 1.2f)

        assertEquals(1, repo.fontSizeCalls.size)
        assertEquals(testUserId, repo.fontSizeCalls[0].first)
        assertEquals(1.2f, repo.fontSizeCalls[0].second)
    }

    @Test
    fun `SetFontSizeScale clamps scale below minimum to 0_8`() = runBlocking {
        val useCase = SetFontSizeScaleUseCase(repo)
        useCase(testUserId, 0.3f)

        assertEquals(0.8f, repo.fontSizeCalls[0].second)
    }

    @Test
    fun `SetFontSizeScale clamps scale above maximum to 1_5`() = runBlocking {
        val useCase = SetFontSizeScaleUseCase(repo)
        useCase(testUserId, 5.0f)

        assertEquals(1.5f, repo.fontSizeCalls[0].second)
    }

    @Test
    fun `SetFontSizeScale accepts boundary value 0_8`() = runBlocking {
        val useCase = SetFontSizeScaleUseCase(repo)
        useCase(testUserId, 0.8f)

        assertEquals(0.8f, repo.fontSizeCalls[0].second)
    }

    @Test
    fun `SetFontSizeScale accepts boundary value 1_5`() = runBlocking {
        val useCase = SetFontSizeScaleUseCase(repo)
        useCase(testUserId, 1.5f)

        assertEquals(1.5f, repo.fontSizeCalls[0].second)
    }

    // ── SetThemeModeUseCase ─────────────────────────────────────────

    @Test
    fun `SetThemeMode delegates to repository`() = runBlocking {
        val useCase = SetThemeModeUseCase(repo)
        useCase(testUserId, "dark")

        assertEquals(1, repo.themeModeCalls.size)
        assertEquals(testUserId, repo.themeModeCalls[0].first)
        assertEquals("dark", repo.themeModeCalls[0].second)
    }

    @Test
    fun `SetThemeMode handles system mode`() = runBlocking {
        val useCase = SetThemeModeUseCase(repo)
        useCase(testUserId, "system")

        assertEquals("system", repo.themeModeCalls[0].second)
    }

    @Test
    fun `SetThemeMode handles light mode`() = runBlocking {
        val useCase = SetThemeModeUseCase(repo)
        useCase(testUserId, "light")

        assertEquals("light", repo.themeModeCalls[0].second)
    }

    // ── SetColorPaletteUseCase ──────────────────────────────────────

    @Test
    fun `SetColorPalette delegates to repository`() = runBlocking {
        val useCase = SetColorPaletteUseCase(repo)
        val paletteId = PaletteId("ocean")
        useCase(testUserId, paletteId)

        assertEquals(1, repo.colorPaletteCalls.size)
        assertEquals(testUserId, repo.colorPaletteCalls[0].first)
        assertEquals(paletteId, repo.colorPaletteCalls[0].second)
    }

    // ── UnlockColorPaletteUseCase ───────────────────────────────────

    @Test
    fun `UnlockColorPalette delegates to repository`() = runBlocking {
        val useCase = UnlockColorPaletteUseCase(repo)
        val paletteId = PaletteId("premium")
        useCase(testUserId, paletteId)

        assertEquals(1, repo.unlockPaletteCalls.size)
        assertEquals(testUserId, repo.unlockPaletteCalls[0].first)
        assertEquals(paletteId, repo.unlockPaletteCalls[0].second)
    }

    // ── SetVoiceForLanguageUseCase ──────────────────────────────────

    @Test
    fun `SetVoiceForLanguage delegates to repository`() = runBlocking {
        val useCase = SetVoiceForLanguageUseCase(repo)
        val langCode = LanguageCode("en-US")
        val voiceName = VoiceName("en-US-JennyNeural")
        useCase(testUserId, langCode, voiceName)

        assertEquals(1, repo.voiceCalls.size)
        assertEquals(testUserId, repo.voiceCalls[0].first)
        assertEquals(langCode, repo.voiceCalls[0].second)
        assertEquals(voiceName, repo.voiceCalls[0].third)
    }

    // ── SetAutoThemeEnabledUseCase ──────────────────────────────────

    @Test
    fun `SetAutoThemeEnabled enables auto theme`() = runBlocking {
        val useCase = SetAutoThemeEnabledUseCase(repo)
        useCase(testUserId, true)

        assertEquals(1, repo.autoThemeCalls.size)
        assertEquals(testUserId, repo.autoThemeCalls[0].first)
        assertTrue(repo.autoThemeCalls[0].second)
    }

    @Test
    fun `SetAutoThemeEnabled disables auto theme`() = runBlocking {
        val useCase = SetAutoThemeEnabledUseCase(repo)
        useCase(testUserId, false)

        assertEquals(1, repo.autoThemeCalls.size)
        assertFalse(repo.autoThemeCalls[0].second)
    }

    // ── SetNotificationPrefUseCase ──────────────────────────────────

    @Test
    fun `SetNotificationPref enables notification for specific field`() = runBlocking {
        val useCase = SetNotificationPrefUseCase(repo)
        useCase(testUserId, "notifyNewMessages", true)

        assertEquals(1, repo.notifPrefCalls.size)
        assertEquals(testUserId, repo.notifPrefCalls[0].first)
        assertEquals("notifyNewMessages", repo.notifPrefCalls[0].second)
        assertTrue(repo.notifPrefCalls[0].third)
    }

    @Test
    fun `SetNotificationPref disables notification for specific field`() = runBlocking {
        val useCase = SetNotificationPrefUseCase(repo)
        useCase(testUserId, "notifyFriendRequests", false)

        assertEquals("notifyFriendRequests", repo.notifPrefCalls[0].second)
        assertFalse(repo.notifPrefCalls[0].third)
    }

    @Test
    fun `SetNotificationPref works for all notification types`() = runBlocking {
        val useCase = SetNotificationPrefUseCase(repo)
        val fields = listOf("notifyNewMessages", "notifyFriendRequests", "notifyRequestAccepted", "notifySharedInbox")

        fields.forEach { field ->
            useCase(testUserId, field, true)
        }

        assertEquals(4, repo.notifPrefCalls.size)
        assertEquals(fields, repo.notifPrefCalls.map { it.second })
    }
}
