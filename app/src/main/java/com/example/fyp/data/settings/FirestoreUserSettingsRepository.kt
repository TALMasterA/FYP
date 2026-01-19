package com.example.fyp.data.settings

import com.example.fyp.model.UserSettings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserSettingsRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun docRef(uid: String) =
        db.collection("users").document(uid)
            .collection("profile").document("settings")

    fun observe(uid: String): Flow<UserSettings> = callbackFlow {
        val reg = docRef(uid).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val code = snap?.getString("primaryLanguageCode") ?: ""
            trySend(UserSettings(primaryLanguageCode = code.ifBlank { "en-US" }))
        }
        awaitClose { reg.remove() }
    }

    suspend fun setPrimaryLanguage(uid: String, code: String) {
        docRef(uid).set(mapOf("primaryLanguageCode" to code), com.google.firebase.firestore.SetOptions.merge()).await()
    }
}