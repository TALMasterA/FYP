package com.translator.TalknLearn.data.user

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.translator.TalknLearn.data.cloud.CloudAccountDeletionClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val accountDeletionClient: CloudAccountDeletionClient
) {
    /**
     * Delete all user data and the Firebase Auth account through the protected
     * Cloud Function. The client keeps only the local password re-auth step.
     */
    suspend fun deleteAccount(userId: String): Result<Unit> {
        return try {
            if (auth.currentUser?.uid != userId) {
                Result.failure(IllegalStateException("Signed-in user changed. Please sign in again."))
            } else {
                accountDeletionClient.deleteAccountAndData().onSuccess {
                    auth.signOut()
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Re-authenticate user before sensitive operations like account deletion
     */
    suspend fun reauthenticate(email: String, password: String): Result<Unit> = try {
        val credential = EmailAuthProvider
            .getCredential(email, password)
        auth.currentUser?.reauthenticate(credential)?.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Returns true when the current user signed in via Google (not email/password). */
    fun isGoogleUser(): Boolean =
        auth.currentUser?.providerData
            ?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true

    /** Re-authenticate a Google user before account deletion. */
    suspend fun reauthenticateWithGoogle(idToken: String): Result<Unit> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.currentUser?.reauthenticate(credential)?.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}