package com.example.fyp.data.settings

import com.example.fyp.model.user.UserSettings
import com.google.firebase.firestore.FieldValue
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

    /**
     * Parse a Firestore document snapshot into UserSettings.
     * Shared by both observeUserSettings and fetchUserSettings to avoid duplication.
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseSettings(snap: com.google.firebase.firestore.DocumentSnapshot?): UserSettings {
        val code = snap?.getString("primaryLanguageCode").orEmpty()
        val scale = snap?.getDouble("fontSizeScale")?.toFloat() ?: 1.0f
        val themeMode = snap?.getString("themeMode") ?: "system"
        val colorPaletteId = snap?.getString("colorPaletteId") ?: "default"
        val unlockedPalettes = snap?.get("unlockedPalettes") as? List<String> ?: listOf("default")
        val voiceSettings = snap?.get("voiceSettings") as? Map<String, String> ?: emptyMap()
        val historyViewLimit = snap?.getLong("historyViewLimit")?.toInt() ?: UserSettings.BASE_HISTORY_LIMIT

        return UserSettings(
            primaryLanguageCode = code.ifBlank { "en-US" },
            fontSizeScale = scale,
            themeMode = themeMode,
            colorPaletteId = colorPaletteId,
            unlockedPalettes = unlockedPalettes,
            voiceSettings = voiceSettings,
            historyViewLimit = historyViewLimit
        )
    }

    override fun observeUserSettings(userId: String): Flow<UserSettings> = callbackFlow {
        val reg = docRef(userId).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            trySend(parseSettings(snap))
        }

        awaitClose { reg.remove() }
    }

    override suspend fun fetchUserSettings(userId: String): UserSettings {
        val snap = docRef(userId).get().await()
        return parseSettings(snap)
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

    override suspend fun setThemeMode(userId: String, themeMode: String) {
        docRef(userId)
            .set(mapOf("themeMode" to themeMode), SetOptions.merge())
            .await()
    }

    override suspend fun setColorPalette(userId: String, paletteId: String) {
        docRef(userId)
            .set(mapOf("colorPaletteId" to paletteId), SetOptions.merge())
            .await()
    }

    override suspend fun unlockColorPalette(userId: String, paletteId: String) {
        // Use arrayUnion to atomically add palette without reading current list first.
        // This eliminates one Firestore read per palette unlock.
        docRef(userId)
            .set(mapOf("unlockedPalettes" to FieldValue.arrayUnion(paletteId)), SetOptions.merge())
            .await()
    }

    override suspend fun setVoiceForLanguage(userId: String, languageCode: String, voiceName: String) {
        // Use dot-notation to update a single key in the voiceSettings map
        // without reading the entire document first.
        // This eliminates one Firestore read per voice change.
        docRef(userId)
            .set(mapOf("voiceSettings" to mapOf(languageCode to voiceName)), SetOptions.merge())
            .await()
    }

    override suspend fun expandHistoryViewLimit(userId: String, newLimit: Int) {
        val clampedLimit = newLimit.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        docRef(userId)
            .set(mapOf("historyViewLimit" to clampedLimit), SetOptions.merge())
            .await()
    }
}