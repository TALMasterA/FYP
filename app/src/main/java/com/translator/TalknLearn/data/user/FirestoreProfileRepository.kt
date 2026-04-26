package com.translator.TalknLearn.data.user

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun docRef(uid: String) =
        db.collection("users").document(uid)
            .collection("profile").document("info")

    /**
     * Delete all user data and account
     */
    suspend fun deleteAccount(userId: String): Result<Unit> = try {
        // Delete user collections in Firestore
        deleteCollection(userId, "history")
        deleteCollection(userId, "word_banks")
        deleteCollection(userId, "learning_sheets")
        deleteCollection(userId, "quiz_attempts")
        deleteCollection(userId, "quiz_stats")
        deleteCollection(userId, "generated_quizzes")
        deleteCollection(userId, "quiz_versions")
        deleteCollection(userId, "favorites")
        deleteCollection(userId, "custom_words")
        deleteCollection(userId, "sessions")
        deleteCollection(userId, "coin_awards")
        deleteCollection(userId, "last_awarded_quiz")
        deleteCollection(userId, "user_stats")
        // Friends-related subcollections
        deleteCollection(userId, "friends")
        deleteCollection(userId, "shared_inbox")
        deleteCollection(userId, "favorite_sessions")
        deleteCollection(userId, "blocked_users")
        // Push notification tokens
        deleteCollection(userId, "fcm_tokens")

        // Delete profile subcollection (includes public profile)
        db.collection("users").document(userId)
            .collection("profile").document("settings").delete().await()
        db.collection("users").document(userId)
            .collection("profile").document("info").delete().await()
        db.collection("users").document(userId)
            .collection("profile").document("public").delete().await()

        // Delete username registry entry (best-effort)
        try {
            val usernameQuery = db.collection("usernames")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get().await()
            usernameQuery.documents.forEach { it.reference.delete().await() }
        } catch (e: Exception) {
            android.util.Log.d("ProfileRepo", "Username cleanup failed (best-effort)", e)
        }

        // Delete user_search index entry (best-effort)
        try {
            db.collection("user_search").document(userId).delete().await()
        } catch (e: Exception) {
            android.util.Log.d("ProfileRepo", "User search cleanup failed (best-effort)", e)
        }

        // Delete user document
        db.collection("users").document(userId).delete().await()

        // Delete Firebase Auth account
        auth.currentUser?.delete()?.await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun deleteCollection(userId: String, collectionName: String) {
        val col = db.collection("users").document(userId).collection(collectionName)
        while (true) {
            val snapshot = col.limit(500).get().await()
            if (snapshot.isEmpty) break

            val batch = db.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
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
}