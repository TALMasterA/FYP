package com.translator.TalknLearn.domain.settings

import com.translator.TalknLearn.data.settings.FirestoreUserSettingsRepository
import com.translator.TalknLearn.model.LanguageCode
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.model.user.UserSettings
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever

/**
 * Unit tests for SetPrimaryLanguageUseCase.
 *
 * Tests:
 * 1. First change allowed when lastChangeMs is 0 (never changed)
 * 2. Change allowed after cooldown expired (31+ days)
 * 3. Change rejected within cooldown period (< 30 days)
 * 4. Same language returns Success without checking cooldown
 * 5. Cooldown returns correct remaining days
 * 6. Change at exact 30-day boundary is allowed
 * 7. Change rejected 1 day before cooldown expires
 * 8. Different users have independent cooldowns
 */
class SetPrimaryLanguageUseCaseTest {

    private lateinit var repo: FirestoreUserSettingsRepository
    private lateinit var useCase: SetPrimaryLanguageUseCase

    private val uid = UserId("user1")

    @Before
    fun setup() {
        repo = mock()
        useCase = SetPrimaryLanguageUseCase(repo)
    }

    @Test
    fun `first change allowed when lastChangeMs is 0`() = runTest {
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = 0L)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.Success)
        verify(repo).setPrimaryLanguage(uid, LanguageCode("ja"))
    }

    @Test
    fun `change allowed after cooldown expired`() = runTest {
        val thirtyOneDaysAgo = System.currentTimeMillis() - (31L * 24 * 60 * 60 * 1000)
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = thirtyOneDaysAgo)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.Success)
        verify(repo).setPrimaryLanguage(uid, LanguageCode("ja"))
    }

    @Test
    fun `change rejected within cooldown period`() = runTest {
        val fiveDaysAgo = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000)
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = fiveDaysAgo)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.CooldownActive)
        verify(repo, never()).setPrimaryLanguage(uid, LanguageCode("ja"))
    }

    @Test
    fun `same language returns Success without calling setPrimaryLanguage`() = runTest {
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = System.currentTimeMillis())
        )

        val result = useCase(uid, LanguageCode("en-US"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.Success)
        verify(repo, never()).setPrimaryLanguage(uid, LanguageCode("en-US"))
    }

    @Test
    fun `cooldown returns correct remaining days`() = runTest {
        // Changed 10 days ago -> ~20 days remaining -> remainingDays should be 21 (ceiling)
        val tenDaysAgo = System.currentTimeMillis() - (10L * 24 * 60 * 60 * 1000)
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = tenDaysAgo)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.CooldownActive)
        val days = (result as SetPrimaryLanguageUseCase.Result.CooldownActive).remainingDays
        // ~20 days remaining, ceiling rounds to 21
        assertTrue("Expected ~21 remaining days, got $days", days in 20..21)
    }

    @Test
    fun `change at exact 30-day boundary is allowed`() = runTest {
        val exactlyThirtyDaysAgo = System.currentTimeMillis() - UserSettings.PRIMARY_LANGUAGE_CHANGE_COOLDOWN_MS
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = exactlyThirtyDaysAgo)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.Success)
        verify(repo).setPrimaryLanguage(uid, LanguageCode("ja"))
    }

    @Test
    fun `change rejected 1 day before cooldown expires`() = runTest {
        val twentyNineDaysAgo = System.currentTimeMillis() - (29L * 24 * 60 * 60 * 1000)
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = twentyNineDaysAgo)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.CooldownActive)
        val days = (result as SetPrimaryLanguageUseCase.Result.CooldownActive).remainingDays
        assertTrue("Expected 1-2 remaining days, got $days", days in 1..2)
    }

    @Test
    fun `different users have independent cooldowns`() = runTest {
        val uid2 = UserId("user2")
        val recentChange = System.currentTimeMillis() - (1L * 24 * 60 * 60 * 1000)

        // user1: never changed
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = 0L)
        )
        // user2: changed yesterday
        whenever(repo.fetchUserSettings(uid2)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = recentChange)
        )

        val result1 = useCase(uid, LanguageCode("ja"))
        val result2 = useCase(uid2, LanguageCode("ja"))

        assertTrue(result1 is SetPrimaryLanguageUseCase.Result.Success)
        assertTrue(result2 is SetPrimaryLanguageUseCase.Result.CooldownActive)
    }

    @Test
    fun `cooldown returns correct remaining hours for sub-day duration`() = runTest {
        // Changed 29 days + 20 hours ago -> ~4 hours remaining
        val changeMs = System.currentTimeMillis() - (29L * 24 * 60 * 60 * 1000 + 20L * 60 * 60 * 1000)
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = changeMs)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.CooldownActive)
        val cooldown = result as SetPrimaryLanguageUseCase.Result.CooldownActive
        assertEquals(0, cooldown.remainingDays)
        assertTrue("Expected remainingHours in 3..5, got ${cooldown.remainingHours}", cooldown.remainingHours in 3..5)
    }

    @Test
    fun `cooldown returns both days and hours`() = runTest {
        // Changed 10 days ago -> ~20 days remaining
        val tenDaysAgo = System.currentTimeMillis() - (10L * 24 * 60 * 60 * 1000)
        whenever(repo.fetchUserSettings(uid)).thenReturn(
            UserSettings(primaryLanguageCode = "en-US", lastPrimaryLanguageChangeMs = tenDaysAgo)
        )

        val result = useCase(uid, LanguageCode("ja"))

        assertTrue(result is SetPrimaryLanguageUseCase.Result.CooldownActive)
        val cooldown = result as SetPrimaryLanguageUseCase.Result.CooldownActive
        assertTrue("Expected ~20 remaining days, got ${cooldown.remainingDays}", cooldown.remainingDays in 19..20)
        assertTrue("Expected remainingHours >= 0, got ${cooldown.remainingHours}", cooldown.remainingHours >= 0)
    }
}
