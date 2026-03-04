package com.example.fyp.domain.settings

import com.example.fyp.data.settings.FirestoreUserSettingsRepository
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.UserId
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Unit tests for SetPrimaryLanguageUseCase.
 * This use case directly delegates to FirestoreUserSettingsRepository.
 */
class SetPrimaryLanguageUseCaseTest {

    private lateinit var repo: FirestoreUserSettingsRepository
    private lateinit var useCase: SetPrimaryLanguageUseCase

    @Before
    fun setup() {
        repo = mock()
        useCase = SetPrimaryLanguageUseCase(repo)
    }

    @Test
    fun `invoke delegates to repository with correct parameters`() = runTest {
        val uid = UserId("user1")
        val code = LanguageCode("en-US")

        useCase(uid, code)

        verify(repo).setPrimaryLanguage(uid, code)
    }

    @Test
    fun `invoke works with language-only code`() = runTest {
        val uid = UserId("user1")
        val code = LanguageCode("ja")

        useCase(uid, code)

        verify(repo).setPrimaryLanguage(uid, code)
    }

    @Test
    fun `invoke works with different users`() = runTest {
        val uid1 = UserId("user1")
        val uid2 = UserId("user2")
        val code = LanguageCode("zh-CN")

        useCase(uid1, code)
        useCase(uid2, code)

        verify(repo).setPrimaryLanguage(uid1, code)
        verify(repo).setPrimaryLanguage(uid2, code)
    }
}
