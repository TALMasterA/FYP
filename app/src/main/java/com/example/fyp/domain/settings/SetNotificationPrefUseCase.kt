package com.example.fyp.domain.settings

import com.example.fyp.data.settings.UserSettingsRepository
import com.example.fyp.model.UserId
import javax.inject.Inject

class SetNotificationPrefUseCase @Inject constructor(
    private val repo: UserSettingsRepository
) {
    /**
     * @param field  One of "notifyNewMessages", "notifyFriendRequests",
     *               "notifyRequestAccepted", "notifySharedInbox"
     */
    suspend operator fun invoke(uid: UserId, field: String, enabled: Boolean) {
        repo.setNotificationPref(uid, field, enabled)
    }
}
