@file:Suppress("unused")

package com.translator.TalknLearn.data.user

import com.translator.TalknLearn.model.user.AuthState
import com.translator.TalknLearn.model.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val secureStorage: com.translator.TalknLearn.core.security.SecureStorage
) {
    val currentUser: User?
        get() = firebaseAuth.currentUser?.toUser()

    val currentUserState: Flow<AuthState> = callbackFlow {
        val initial = firebaseAuth.currentUser
        trySend(if (initial != null) AuthState.LoggedIn(initial.toUser()) else AuthState.LoggedOut)

        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(if (user != null) AuthState.LoggedIn(user.toUser()) else AuthState.LoggedOut)
        }

        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun login(email: String, password: String): Result<User> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user
            ?: return Result.failure(Exception("Authentication failed: No user returned"))
        Result.success(user.toUser())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun register(email: String, password: String): Result<User> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
            ?: return Result.failure(Exception("Authentication failed: No user returned"))
        Result.success(user.toUser())
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Sign in to Firebase using a Google ID token obtained from Google Sign-In.
     * If the user does not yet exist, Firebase will provision a new account.
     */
    suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = result.user
            ?: return Result.failure(Exception("Authentication failed: No user returned"))
        Result.success(user.toUser())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun logout() {
        // FIX 5.5: Remove FCM token before signing out so the device
        // stops receiving push notifications for this account.
        runCatching {
            secureStorage.remove(com.translator.TalknLearn.core.security.SecureStorage.KEY_FCM_TOKEN)
        }
        com.translator.TalknLearn.core.FcmNotificationService.removeTokenOnSignOut()
        firebaseAuth.signOut()
    }

    /**
     * Returns true if the currently signed-in user authenticated via Google Sign-In,
     * false for email/password users or when no user is signed in.
     */
    fun isGoogleUser(): Boolean =
        firebaseAuth.currentUser?.providerData
            ?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true

    private fun FirebaseUser.toUser() = User(
        uid = uid,
        email = email
    )
}