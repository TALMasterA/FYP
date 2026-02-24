package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey

@Composable
fun NotificationSettingsScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings
    val (uiText) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    StandardScreenScaffold(
        title = t(UiTextKey.FriendsNotifSettingsTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = t(UiTextKey.FriendsNotifSettingsTitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            NotifToggle(
                label = t(UiTextKey.FriendsNotifNewMessages),
                checked = settings.notifyNewMessages,
                onCheckedChange = {
                    viewModel.updateNotificationPref("notifyNewMessages", it)
                }
            )
            NotifToggle(
                label = t(UiTextKey.FriendsNotifFriendRequests),
                checked = settings.notifyFriendRequests,
                onCheckedChange = {
                    viewModel.updateNotificationPref("notifyFriendRequests", it)
                }
            )
            NotifToggle(
                label = t(UiTextKey.FriendsNotifRequestAccepted),
                checked = settings.notifyRequestAccepted,
                onCheckedChange = {
                    viewModel.updateNotificationPref("notifyRequestAccepted", it)
                }
            )
            NotifToggle(
                label = t(UiTextKey.FriendsNotifSharedInbox),
                checked = settings.notifySharedInbox,
                onCheckedChange = {
                    viewModel.updateNotificationPref("notifySharedInbox", it)
                }
            )

            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = t(UiTextKey.InAppBadgeSectionTitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            NotifToggle(
                label = t(UiTextKey.InAppBadgeMessages),
                checked = settings.inAppBadgeMessages,
                onCheckedChange = {
                    viewModel.updateNotificationPref("inAppBadgeMessages", it)
                }
            )
            NotifToggle(
                label = t(UiTextKey.InAppBadgeFriendRequests),
                checked = settings.inAppBadgeFriendRequests,
                onCheckedChange = {
                    viewModel.updateNotificationPref("inAppBadgeFriendRequests", it)
                }
            )
            NotifToggle(
                label = t(UiTextKey.InAppBadgeSharedInbox),
                checked = settings.inAppBadgeSharedInbox,
                onCheckedChange = {
                    viewModel.updateNotificationPref("inAppBadgeSharedInbox", it)
                }
            )
        }
    }
}

@Composable
private fun NotifToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
