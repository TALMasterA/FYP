package com.example.fyp.data.settings

import com.example.fyp.model.UserSettings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserSettingsRepository {

    private suspend fun ensureUserDocument(userId: String) {
        val doc = firestore.collection("users").document(userId).get().await()
        if (!doc.exists()) {
            firestore.collection("users")
                .document(userId)
                .set(mapOf(
                    "primaryLanguageCode" to "en-US",
                    "fontSizeScale" to 1.0f
                ))
                .await()
        }
    }

    override fun observeUserSettings(userId: String): Flow<UserSettings> = flow {
        try {
            val doc = firestore
                .collection("users")
                .document(userId)
                .get()
                .await()

            val settings = doc.toObject(UserSettings::class.java) ?: UserSettings()
            emit(settings)
        } catch (e: Exception) {
            emit(UserSettings())
        }
    }

    override suspend fun setFontSizeScale(userId: String, scale: Float) {
        ensureUserDocument(userId)  // Creates document if missing

        firestore
            .collection("users")
            .document(userId)
            .update("fontSizeScale", scale)  // Now safe to use update()
            .await()
    }

    override suspend fun setPrimaryLanguage(userId: String, languageCode: String) {
        ensureUserDocument(userId)  // Creates document if missing

        firestore
            .collection("users")
            .document(userId)
            .update("primaryLanguageCode", languageCode)  // Now safe to use update()
            .await()
    }
}