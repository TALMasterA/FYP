package com.example.fyp.domain.settings

import com.example.fyp.data.settings.FirestoreUserSettingsRepository
import com.example.fyp.model.UserId
import com.example.fyp.model.user.UserSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for ObserveUserSettingsUseCase.
 *
 * Tests:
 *  1. Delegates to repository observeUserSettings
 *  2. Emits settings from repository flow
 *  3. Emits default settings when repository returns defaults
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObserveUserSettingsUseCaseTest {

    private lateinit var repo: FirestoreUserSettingsRepository
    private lateinit var useCase: ObserveUserSettingsUseCase

    @Before
    fun setup() {
        repo = mock()
        useCase = ObserveUserSettingsUseCase(repo)
    }

    @Test
    fun `delegates to repository observeUserSettings`() = runTest {
        val settings = UserSettings(primaryLanguageCode = "zh-TW", fontSizeScale = 1.5f)
        whenever(repo.observeUserSettings(UserId("user1"))).thenReturn(flowOf(settings))

        val result = useCase(UserId("user1")).first()

        assertEquals("zh-TW", result.primaryLanguageCode)
        assertEquals(1.5f, result.fontSizeScale)
        verify(repo).observeUserSettings(UserId("user1"))
    }

    @Test
    fun `emits default settings when repository returns defaults`() = runTest {
        whenever(repo.observeUserSettings(UserId("user1"))).thenReturn(flowOf(UserSettings()))

        val result = useCase(UserId("user1")).first()

        assertEquals("en-US", result.primaryLanguageCode)
        assertEquals(1.0f, result.fontSizeScale)
        assertEquals("system", result.themeMode)
    }

    @Test
    fun `emits custom notification settings`() = runTest {
        val settings = UserSettings(
            notifyNewMessages = true,
            notifyFriendRequests = true,
            inAppBadgeMessages = false
        )
        whenever(repo.observeUserSettings(UserId("user1"))).thenReturn(flowOf(settings))

        val result = useCase(UserId("user1")).first()

        assertTrue(result.notifyNewMessages)
        assertTrue(result.notifyFriendRequests)
        assertFalse(result.inAppBadgeMessages)
    }
}
