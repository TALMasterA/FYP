package com.translator.TalknLearn.domain.settings

import com.translator.TalknLearn.data.settings.UserSettingsRepository
import com.translator.TalknLearn.model.UserId
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
