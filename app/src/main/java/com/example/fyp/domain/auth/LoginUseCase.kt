package com.example.fyp.domain.auth

import com.example.fyp.data.auth.FirebaseAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepo: FirebaseAuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authRepo.login(email, password)
}