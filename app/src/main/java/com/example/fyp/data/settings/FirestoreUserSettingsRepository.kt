package com.example.fyp.data.settings

import com.example.fyp.model.LanguageCode
import com.example.fyp.model.PaletteId
import com.example.fyp.model.UserId
import com.example.fyp.model.VoiceName
import com.example.fyp.model.user.UserSettings
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
        val autoThemeEnabled = snap?.getBoolean("autoThemeEnabled") ?: false
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
        // Cache-first: show saved settings instantly, server sync via real-time listener
        val snap = try {
            val cached = ref.get(Source.CACHE).await()
            if (cached.exists()) cached else ref.get(Source.SERVER).await()
        } catch (_: Exception) {
            ref.get().await()
        }
        return parseSettings(snap)
    }

    override suspend fun setPrimaryLanguage(userId: UserId, languageCode: LanguageCode) {
        docRef(userId.value)
            .set(mapOf("primaryLanguageCode" to languageCode.value), SetOptions.merge())
            .await()
    }

    override suspend fun setFontSizeScale(userId: UserId, scale: Float) {
        docRef(userId.value)
            .set(mapOf("fontSizeScale" to scale), SetOptions.merge())
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
        // Use dot-notation field path to update only the specific language key
        // within the voiceSettings map, without reading the entire document first.
        // This eliminates one Firestore read per voice change.
        docRef(userId.value)
            .update("voiceSettings.${languageCode.value}", voiceName.value)
            .await()
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
}