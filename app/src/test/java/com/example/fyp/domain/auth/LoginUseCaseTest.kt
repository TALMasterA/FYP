package com.example.fyp.domain.auth

import com.example.fyp.data.user.FirebaseAuthRepository
import com.example.fyp.model.user.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LoginUseCaseTest {

    private lateinit var authRepo: FirebaseAuthRepository
    private lateinit var useCase: LoginUseCase

    @Before
    fun setup() {
        authRepo = mock()
        useCase = LoginUseCase(authRepo)
    }

    @Test
    fun `invoke delegates to auth repository with credentials`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val expected = Result.success(User(uid = "user-id-123"))
        whenever(authRepo.login(email, password))
            .thenReturn(expected)

        // Act
        val result = useCase(email, password)

        // Assert
        assertEquals(expected, result)
        verify(authRepo).login(email, password)
    }

    @Test
    fun `invoke returns error when login fails`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "wrong-password"
        val expected = Result.failure<User>(Exception("Invalid credentials"))
        whenever(authRepo.login(email, password))
            .thenReturn(expected)

        // Act
        val result = useCase(email, password)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke handles empty credentials`() = runTest {
        // Arrange
        val email = ""
        val password = ""
        val expected = Result.failure<User>(Exception("Email and password required"))
        whenever(authRepo.login(email, password))
            .thenReturn(expected)

        // Act
        val result = useCase(email, password)

        // Assert
        assertEquals(expected, result)
    }
}
