package com.example.fyp.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.fyp.BuildConfig
import com.example.fyp.core.StandardScreenBody
import com.example.fyp.core.StandardScreenScaffold
import com.example.fyp.core.rememberUiTextFunctions
import com.example.fyp.model.ui.AppLanguageState
import com.example.fyp.model.ui.BaseUiTexts
import com.example.fyp.model.ui.UiTextKey
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemNotesScreen(
    appLanguageState: AppLanguageState,
    onBack: () -> Unit
) {
    val (uiText, _) = rememberUiTextFunctions(appLanguageState)
    val t: (UiTextKey) -> String = { key -> uiText(key, BaseUiTexts[key.ordinal]) }

    StandardScreenScaffold(
        title = t(UiTextKey.SystemNotesTitle),
        onBack = onBack,
        backContentDescription = t(UiTextKey.NavBack)
    ) { innerPadding ->
        StandardScreenBody(
            innerPadding = innerPadding,
            scrollable = true,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Version Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "App Version",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Auto Theme Schedule
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Auto Theme Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "When enabled, the app automatically switches between light and dark themes based on time of day:\n\n" +
                                "• Light Mode: 6:00 AM - 6:00 PM\n" +
                                "• Dark Mode: 6:00 PM - 6:00 AM\n\n" +
                                "This helps reduce eye strain in different lighting conditions throughout the day.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Color Palettes Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Color Palettes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "The app offers 11 different color palettes:\n\n" +
                                "• 1 Free: Sky Blue (default)\n" +
                                "• 10 Unlockable: Ocean, Sunset, Lavender, Rose, Mint, Crimson, Amber, Indigo, Emerald, Coral\n\n" +
                                "Each unlockable palette costs 10 coins. Earn coins by completing quizzes to unlock more themes!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Friend System Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Friend System",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Search and add friends by username or User ID\n" +
                                "• Send, accept, or reject friend requests\n" +
                                "• Real-time chat with friends and translate conversations\n" +
                                "• Share words and learning materials with friends\n" +
                                "• Shared Inbox to receive and manage items from friends\n" +
                                "• A red dot (●) on friend cards or inbox indicates unread messages or new items\n" +
                                "• Pull down to refresh friend list and requests",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Profile Visibility Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Profile Visibility",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• You can set your profile to Public or Private in My Profile settings\n" +
                                "• Public: any user can search for you and send a friend request\n" +
                                "• Private: your profile will not appear in search results\n" +
                                "• You can still add friends by sharing your User ID even when set to Private",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Shared Inbox Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Shared Inbox",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Shared words can be accepted into your word bank or dismissed\n" +
                                "• Dismissing a shared word requires confirmation\n" +
                                "• Shared learning sheets show the language name (not the code)\n" +
                                "• Tap View to see the full content of shared learning materials\n" +
                                "• A red dot (●) on the inbox icon indicates new items",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Feature Notes
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Important Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• App requires internet connection for translation services\n" +
                                "• History is limited to 50-100 records (expandable with coins)\n" +
                                "• Auto sign-out occurs on app version updates for security\n" +
                                "• Offline mode available for cached data\n" +
                                "• All translations are powered by Azure AI services\n" +
                                "• Usernames must be unique across all users\n" +
                                "• Changing your username releases the old name for others to use",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Privacy & Data
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Privacy & Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "• Audio is only captured for recognition, never stored\n" +
                                "• All data synced securely via Firebase\n" +
                                "• OCR processing happens on-device (privacy-first)\n" +
                                "• You can delete your account and all data anytime\n" +
                                "• Set your profile to Private to prevent others from finding you via search",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
