package com.example.fyp.domain.auth

import com.example.fyp.data.user.FirebaseAuthRepository
import javax.inject.Inject

/**
 * Use case for user authentication via email and password.
 * Integrates with Firebase Authentication.
 *
 * @param authRepo Repository handling Firebase authentication
 */
class LoginUseCase @Inject constructor(
    private val authRepo: FirebaseAuthRepository
) {
    /**
     * Authenticates user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return AuthResult.Success with user ID or AuthResult.Error with error message
     */
    suspend operator fun invoke(email: String, password: String) =
        authRepo.login(email, password)
}