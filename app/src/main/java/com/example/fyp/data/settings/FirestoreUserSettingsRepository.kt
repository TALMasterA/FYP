package com.example.fyp.data.settings

import com.example.fyp.model.UserSettings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserSettingsRepository @Inject constructor(
    private val db: FirebaseFirestore
) : UserSettingsRepository {

    private fun docRef(uid: String) =
        db.collection("users").document(uid)
            .collection("profile").document("settings")

    override fun observeUserSettings(userId: String): Flow<UserSettings> = callbackFlow {
        val reg = docRef(userId).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }

            val code = snap?.getString("primaryLanguageCode").orEmpty()
            val scale = snap?.getDouble("fontSizeScale")?.toFloat() ?: 1.0f

            trySend(
                UserSettings(
                    primaryLanguageCode = code.ifBlank { "en-US" },
                    fontSizeScale = scale
                )
            )
        }

        awaitClose { reg.remove() }
    }

    override suspend fun setPrimaryLanguage(userId: String, languageCode: String) {
        docRef(userId)
            .set(mapOf("primaryLanguageCode" to languageCode), SetOptions.merge())
            .await()
    }

    override suspend fun setFontSizeScale(userId: String, scale: Float) {
        docRef(userId)
            .set(mapOf("fontSizeScale" to scale), SetOptions.merge())
            .await()
    }
}