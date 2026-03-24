package com.example.fyp.data.settings

import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.example.fyp.model.user.UserSettings
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserSettingsRepository @Inject constructor(
    private val db: FirebaseFirestore
) : UserSettingsRepository {

    private companion object {
        const val MIN_FONT_SIZE_SCALE = 0.5f
        const val MAX_FONT_SIZE_SCALE = 2.0f
        const val VOICE_SETTINGS_FIELD_PREFIX = "voiceSettings."
    }

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
        val scale = (snap?.getDouble("fontSizeScale")?.toFloat() ?: 1.0f)
            .coerceIn(MIN_FONT_SIZE_SCALE, MAX_FONT_SIZE_SCALE)
        val themeMode = snap?.getString("themeMode") ?: "system"
        val colorPaletteId = snap?.getString("colorPaletteId") ?: "default"
        val unlockedPalettes = snap?.get("unlockedPalettes") as? List<String> ?: listOf("default")
        val voiceSettings = snap?.get("voiceSettings") as? Map<String, String> ?: emptyMap()
        val historyViewLimit = (snap?.getLong("historyViewLimit")?.toInt() ?: UserSettings.BASE_HISTORY_LIMIT)
            .coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        val autoThemeEnabled = snap?.getBoolean("autoThemeEnabled") ?: false
        val lastPrimaryLanguageChangeMs = snap?.getLong("lastPrimaryLanguageChangeMs") ?: 0L
        val lastUsernameChangeMs = snap?.getLong("lastUsernameChangeMs") ?: 0L
        val notifyNewMessages = snap?.getBoolean("notifyNewMessages") ?: false
        val notifyFriendRequests = snap?.getBoolean("notifyFriendRequests") ?: false
        val notifyRequestAccepted = snap?.getBoolean("notifyRequestAccepted") ?: false
        val notifySharedInbox = snap?.getBoolean("notifySharedInbox") ?: false
        val inAppBadgeMessages = snap?.getBoolean("inAppBadgeMessages") ?: true
        val inAppBadgeFriendRequests = snap?.getBoolean("inAppBadgeFriendRequests") ?: true
        val inAppBadgeSharedInbox = snap?.getBoolean("inAppBadgeSharedInbox") ?: true

        return UserSettings(
            primaryLanguageCode = code.ifBlank { "en-US" },
            fontSizeScale = scale,
            themeMode = themeMode,
            colorPaletteId = colorPaletteId,
            unlockedPalettes = unlockedPalettes,
            voiceSettings = voiceSettings,
            historyViewLimit = historyViewLimit,
            autoThemeEnabled = autoThemeEnabled,
            lastPrimaryLanguageChangeMs = lastPrimaryLanguageChangeMs,
            lastUsernameChangeMs = lastUsernameChangeMs,
            notifyNewMessages = notifyNewMessages,
            notifyFriendRequests = notifyFriendRequests,
            notifyRequestAccepted = notifyRequestAccepted,
            notifySharedInbox = notifySharedInbox,
            inAppBadgeMessages = inAppBadgeMessages,
            inAppBadgeFriendRequests = inAppBadgeFriendRequests,
            inAppBadgeSharedInbox = inAppBadgeSharedInbox,
        )
    }

    override fun observeUserSettings(userId: UserId): Flow<UserSettings> = callbackFlow {
        val reg = docRef(userId.value).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            trySend(parseSettings(snap))
        }

        awaitClose { reg.remove() }
    }

    override suspend fun fetchUserSettings(userId: UserId): UserSettings {
        val ref = docRef(userId.value)
        // Prefer server for cross-device sync; fall back to default on network error.
        // The real-time listener (observeUserSettings) handles updates after initial load.
        val snap = try {
            ref.get(Source.SERVER).await()
        } catch (_: Exception) {
            // Offline or server error: fall back to cache-then-server default
            ref.get().await()
        }
        return parseSettings(snap)
    }

    override suspend fun setPrimaryLanguage(userId: UserId, languageCode: LanguageCode) {
        docRef(userId.value)
            .set(
                mapOf(
                    "primaryLanguageCode" to languageCode.value,
                    "lastPrimaryLanguageChangeMs" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
            .await()
    }

    override suspend fun setFontSizeScale(userId: UserId, scale: Float) {
        val clampedScale = scale.coerceIn(MIN_FONT_SIZE_SCALE, MAX_FONT_SIZE_SCALE)
        docRef(userId.value)
            .set(mapOf("fontSizeScale" to clampedScale), SetOptions.merge())
            .await()
    }

    override suspend fun setThemeMode(userId: UserId, themeMode: String) {
        docRef(userId.value)
            .set(mapOf("themeMode" to themeMode), SetOptions.merge())
            .await()
    }

    override suspend fun setColorPalette(userId: UserId, paletteId: PaletteId) {
        docRef(userId.value)
            .set(mapOf("colorPaletteId" to paletteId.value), SetOptions.merge())
            .await()
    }

    override suspend fun unlockColorPalette(userId: UserId, paletteId: PaletteId) {
        // Use arrayUnion to atomically add palette without reading current list first.
        // This eliminates one Firestore read per palette unlock.
        docRef(userId.value)
            .set(mapOf("unlockedPalettes" to FieldValue.arrayUnion(paletteId.value)), SetOptions.merge())
            .await()
    }

    override suspend fun setVoiceForLanguage(userId: UserId, languageCode: LanguageCode, voiceName: VoiceName) {
        val fieldPath = "$VOICE_SETTINGS_FIELD_PREFIX${languageCode.value}"
        try {
            docRef(userId.value)
                .update(fieldPath, voiceName.value)
                .await()
        } catch (e: FirebaseFirestoreException) {
            if (e.code != FirebaseFirestoreException.Code.NOT_FOUND) throw e
            docRef(userId.value)
                .set(
                    mapOf("voiceSettings" to mapOf(languageCode.value to voiceName.value)),
                    SetOptions.merge()
                )
                .await()
        }
    }

    override suspend fun setAutoThemeEnabled(userId: UserId, enabled: Boolean) {
        docRef(userId.value)
            .set(mapOf("autoThemeEnabled" to enabled), SetOptions.merge())
            .await()
    }

    /** Allowed field names for notification preferences. */
    private val notificationFields = setOf(
        "notifyNewMessages", "notifyFriendRequests", "notifyRequestAccepted", "notifySharedInbox",
        "inAppBadgeMessages", "inAppBadgeFriendRequests", "inAppBadgeSharedInbox"
    )

    override suspend fun setNotificationPref(userId: UserId, field: String, enabled: Boolean) {
        require(field in notificationFields) {
            "Unknown notification preference field: $field"
        }
        docRef(userId.value)
            .set(mapOf(field to enabled), SetOptions.merge())
            .await()
    }


    override suspend fun expandHistoryViewLimit(userId: UserId, newLimit: Int) {
        val clampedLimit = newLimit.coerceIn(UserSettings.BASE_HISTORY_LIMIT, UserSettings.MAX_HISTORY_LIMIT)
        docRef(userId.value)
            .set(mapOf("historyViewLimit" to clampedLimit), SetOptions.merge())
            .await()
    }

    override suspend fun setLastUsernameChangeMs(userId: UserId, timestampMs: Long) {
        docRef(userId.value)
            .set(mapOf("lastUsernameChangeMs" to timestampMs), SetOptions.merge())
            .await()
    }
}
