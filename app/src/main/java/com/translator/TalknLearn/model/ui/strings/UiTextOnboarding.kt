package com.translator.TalknLearn.model.ui

// Auto-split from UiTextScreens.kt — order must match UiTextKey enum
val OnboardingExtrasScreenTexts: List<String> = listOf(
    // --- Chat Extra Labels ---
    // ChatUsernameLabel
    "Username: ",
    // ChatUserIdLabel
    "User ID: ",
    // ChatLearningLabel
    "Learning: ",
    // ChatBlockedMessage
    "You cannot send messages to this user.",

    // --- Custom Word Bank Extra ---
    // CustomWordsSearchPlaceholder
    "Search",
    // CustomWordsEmptyState
    "No custom words yet",
    // CustomWordsEmptyHint
    "Tap + to add your own words",
    // CustomWordsNoSearchResults
    "No words match your search",

    // --- Word Bank Records Count ---
    // WordBankRecordsCountTemplate
    "{count} records",

    // --- Blocked Users ID ---
    // BlockedUserIdTemplate
    "ID: {id}…",

    // --- Profile Extra ---
    // ProfileEmailTemplate
    "Email: {email}",
    // ProfileUsernameHintFull
    "Username for friends to find you (3-20 characters, letters/numbers/_)",

    // --- Voice Settings ---
    // VoiceSettingsNoOptions
    "No voice options available for this language",

    // --- Login ---
    // AuthUpdatedLoginAgain
    "App updated, please log in again",

    // --- Primary Language Cooldown ---
    // SettingsPrimaryLanguageCooldownTitle
    "Language Change Unavailable",
    // SettingsPrimaryLanguageCooldownMessage
    "Your primary language can only be changed once every 30 days. Please try again in {days} days.",
    // SettingsPrimaryLanguageCooldownMessageHours
    "Your primary language can only be changed once every 30 days. Please try again in {hours} hours.",
    // SettingsPrimaryLanguageConfirmTitle
    "Confirm Language Change",
    // SettingsPrimaryLanguageConfirmMessage
    "Changing your primary language will start a 30-day cooldown before you can change it again. Continue?",

    // --- Bottom Navigation ---
    // NavHome
    "Home",
    // NavTranslate
    "Translate",
    // NavLearn
    "Learn",
    // NavFriends
    "Friends",
    // NavSettings
    "Settings",

    // --- Permissions ---
    // CameraPermissionTitle
    "Camera Permission Required",
    // CameraPermissionMessage
    "This feature requires camera access to capture images for text recognition. Please grant camera permission to continue.",
    // CameraPermissionGrant
    "Grant Permission",
    // MicPermissionMessage
    "Microphone permission is needed for speech recognition. Please grant the permission.",

    // --- Delete Confirmations ---
    // FavoritesDeleteConfirm
    "Delete {count} selected item(s)? This cannot be undone.",
    // WordBankDeleteConfirm
    "Are you sure you want to delete \"{word}\"?",

    // --- Friends Extras ---
    // FriendsAcceptAllButton
    "Accept All",
    // FriendsRejectAllButton
    "Reject All",
    // ChatBlockedCannotSend
    "Cannot send messages",

    // --- Shop / Unlock ---
    // ShopUnlockConfirmTitle
    "Unlock {name}?",
    // ShopUnlockCost
    "Cost: {cost} coins",
    // ShopYourCoins
    "Your coins: {coins}",
    // ShopUnlockButton
    "Unlock",

    // --- Help: Primary Language Info ---
    // HelpPrimaryLanguageTitle
    "Primary Language",
    // HelpPrimaryLanguageBody
    "• Your primary language is used for learning sheet explanations and recommendations\n" +
            "• It can only be changed once every 30 days to maintain learning consistency\n" +
            "• You can change your primary language in Settings\n" +
            "• The primary language setting is global and applies to all screens",

    // --- Camera Language Hint ---
    // CameraLanguageHint
    "💡 Tip: For best accuracy, set the \"From\" language to match the text you are scanning.",

    // --- Username Change Cooldown ---
    // SettingsUsernameCooldownTitle
    "Username Change Unavailable",
    // SettingsUsernameCooldownMessage
    "Your username can only be changed once every 30 days. Please try again in {days} days.",
    // SettingsUsernameCooldownMessageHours
    "Your username can only be changed once every 30 days. Please try again in {hours} hours.",
    // SettingsUsernameConfirmTitle
    "Confirm Username Change",
    // SettingsUsernameConfirmMessage
    "Changing your username will start a 30-day cooldown before you can change it again. Continue?",

    // --- Phase 1 fixes ---
    // SettingsPasswordManagedByGoogle
    "Password managed by Google",
    // SpeechListeningLabel
    "Listening… speak now",
    // QuizLeaveTitle
    "Leave quiz?",
    // QuizLeaveMessage
    "Your answers will be lost.",
    // QuizLeaveConfirm
    "Leave",
    // QuizLeaveStay
    "Stay",

    // --- Phase 1 part 2: a11y labels and status text ---
    // A11yAddFavorite
    "Add to favorites",
    // A11yRemoveFavorite
    "Remove from favorites",
    // A11yViewFriendProfile
    "View friend profile",
    // A11yRefreshAutoDetect
    "Refresh auto-detect",
    // A11yInstructions
    "Instructions",
    // A11yClearText
    "Clear text",
    // A11yAddCustomWords
    "Add custom words",
    // A11yFilter
    "Filter",
    // A11ySpeakOriginal
    "Speak original",
    // A11ySpeakTranslation
    "Speak translation",
    // A11ySpeakExample
    "Speak example",
    // A11yCoinRules
    "Coin rules",
    // StatusWaiting
    "Waiting…",
    // ErrorNotLoggedIn
    "Not logged in",
    // HistoryErrorDeleteFailed
    "Delete failed. Please try again.",
    // HistoryErrorRenameFailed
    "Rename failed. Please try again.",
    // HistoryErrorDeleteSessionFailed
    "Could not delete the session. Please try again.",
    // HistoryErrorLoadMoreFailed
    "Could not load more records.",
    // HistoryErrorRemoveFavoriteFailed
    "Could not remove from favorites.",
    // HistoryErrorAddFavoriteFailed
    "Could not add to favorites.",
    // HistoryErrorFavoriteSessionFailed
    "Could not save the session to favorites.",
    // HistoryErrorUnfavoriteSessionFailed
    "Could not remove the session from favorites.",
    // TtsSpeaking
    "Speaking…",
    // TtsSpeakingTranslation
    "Speaking translation…",
    // TtsSpoken
    "✓ Spoken",
    // TtsError
    "✗ {detail}",
    // SpeechStatusRecognitionError
    "Recognition error: {detail}",
    // SpeechStatusAutoDetecting
    "Auto-detecting language…",
    // SpeechStatusDetected
    "Detected: {language}",
    // SpeechStatusDetectedConfidence
    "Detected: {language} ({confidence}% confidence)",
    // SpeechStatusEnterText
    "Please enter or speak text to translate.",
    // SpeechStatusLoginRequired
    "Login is required to use translation.",
    // SpeechStatusDetectFailed
    "Could not detect the source language. Please select it manually.",
    // SpeechStatusTranslationError
    "Translation error: {detail}",
    // SpeechStatusScanningImage
    "Scanning image for text…",
    // SpeechStatusTextExtracted
    "Text extracted successfully",
    // SpeechStatusOcrError
    "Image scan error: {detail}",
    // SpeechStatusEnterTextFirst
    "Please enter or speak text first.",
    // SpeechStatusDetectFailedForSpeech
    "Could not detect the source language for speech. Please select a source language.",
    // SpeechStatusRefreshed
    "Refreshed. Auto-detect will run again on your next translate or speak action.",
    // ContinuousStatusLoginRequired
    "Please log in to use continuous translation.",
    // ContinuousStatusPreparingMic
    "Preparing mic…",
    // ContinuousStatusTranslationError
    "Continuous translation error: {detail}",
    // ContinuousStatusRateLimited
    "{detail} Continuous mode stopped to avoid repeated failed requests.",
    // ContinuousStatusRecognitionError
    "Continuous recognition error: {detail}",
    // ContinuousStatusStartError
    "Could not start continuous mode: {detail}",

    // --- Phase 1 part 3: social area messages ---
    // StatusRaw
    "{detail}",
    // ErrGeneric
    "Something went wrong. Please try again.",
    // ErrNoInternet
    "No internet connection. Please check your network and try again.",
    // ErrPermissionDenied
    "You don't have permission to perform this action.",
    // ErrSessionExpired
    "Your session has expired. Please sign in again.",
    // ErrItemNotFound
    "The requested item was not found. It may have been deleted.",
    // ErrAccessDenied
    "Access denied.",
    // ErrAlreadyFriends
    "You're already friends with this user.",
    // ErrUserMaybeBlocked
    "Unable to complete this action. The user may be blocked.",
    // ErrRequestGone
    "This friend request no longer exists.",
    // ErrRequestHandled
    "This request has already been handled by someone else.",
    // ErrNotAuthorized
    "You're not authorized to perform this action.",
    // ErrTooManyRequests
    "Too many requests. Please wait a moment and try again.",
    // ErrInvalidInput
    "Invalid input. Please check and try again.",
    // ErrCannotCompleteNow
    "Operation cannot be completed right now.",
    // ErrTimedOut
    "The operation timed out. Please try again.",
    // FriendsMsgRemoved
    "Friend removed successfully.",
    // FriendsMsgRemoveFailed
    "Unable to remove friend. Please check your connection and try again.",
    // FriendsMsgRemovedMany
    "Friend(s) removed.",
    // FriendsMsgNeedUsername
    "Please set a username in your profile before managing friend requests.",
    // FriendsMsgMaxPending
    "You have reached the maximum of {limit} pending friend requests. Please wait for some to be accepted or " +
        "cancel them before sending more.",
    // FriendsMsgRateLimit
    "Rate limit exceeded. You can send up to {limit} friend requests per hour. Try again in about {minutes} minute(s).",
    // FriendsMsgNoteTooLong
    "Note must not exceed {max} characters",
    // FriendsMsgRequestSent
    "Friend request sent! They will be notified.",
    // FriendsMsgAlreadyFriends
    "You are already friends with this user.",
    // FriendsMsgRequestPending
    "You already have a pending request to this user. Please wait for their reply.",
    // FriendsMsgProfileMissing
    "Could not find the user's profile. They may have deleted their account.",
    // FriendsMsgYouBlocked
    "You have blocked this user. Unblock them first.",
    // FriendsMsgUnableToSend
    "Unable to send friend request.",
    // FriendsMsgSendFailed
    "Failed to send friend request. Please try again.",
    // FriendsMsgAccepted
    "Friend request accepted! You are now friends.",
    // FriendsMsgAcceptGone
    "This request no longer exists — it may have been cancelled.",
    // FriendsMsgAcceptNotAuthorized
    "You are not authorized to accept this request.",
    // FriendsMsgAcceptFailed
    "Failed to accept request. Please try again.",
    // FriendsMsgDeclined
    "Request declined.",
    // FriendsMsgDeclineGone
    "This request no longer exists.",
    // FriendsMsgDeclineFailed
    "Failed to decline request. Please try again.",
    // FriendsMsgCancelled
    "Friend request cancelled.",
    // FriendsMsgCancelGone
    "This request no longer exists — it may have already been accepted or declined.",
    // FriendsMsgCancelFailed
    "Failed to cancel request. Please try again.",
    // FriendsMsgBatchAcceptAll
    "All {count} friend requests accepted!",
    // FriendsMsgBatchAcceptFailed
    "Failed to accept requests. Please try again.",
    // FriendsMsgBatchAcceptMixed
    "{success} accepted, {failed} failed. Please retry for failed ones.",
    // FriendsMsgBatchAcceptStoppedAt
    "Batch accept cancelled after {processed}/{total} request(s).",
    // FriendsMsgBatchAcceptStopped
    "Batch accept cancelled.",
    // FriendsMsgBatchDeclined
    "Declined {count} request(s).",
    // FriendsMsgBatchDeclineStoppedAt
    "Batch reject cancelled after {processed}/{total} request(s).",
    // FriendsMsgBatchDeclineStopped
    "Batch reject cancelled.",
    // FriendsMsgBlockedRemoved
    "User blocked and removed from friends.",
    // FriendsMsgBlockFailed
    "Failed to block user. Please try again.",
    // FriendsMsgBlocked
    "User blocked.",
    // FriendsMsgUnblocked
    "User unblocked.",
    // FriendsMsgUnblockFailed
    "Failed to unblock user. Please try again.",
    // ChatMsgLoadFailed
    "Failed to load messages. Please try again.",
    // ChatMsgBlockFailed
    "Failed to block user.",
    // ChatMsgUnblockFailed
    "Failed to unblock user.",
    // ChatMsgTooLong
    "Message must not exceed {max} characters",
    // ChatMsgTooFast
    "You are sending messages too quickly. Please wait a moment.",
    // ChatMsgSendFailed
    "Failed to send message",
    // ChatMsgTranslateFailed
    "Translation failed. Please try again.",
    // MyProfileMsgLoadFailed
    "Failed to load profile. Please try again.",
    // MyProfileMsgNeedUsername
    "Set a username before making your profile public.",
    // MyProfileMsgPublic
    "Profile set to Public",
    // MyProfileMsgPrivate
    "Profile set to Private",
    // MyProfileMsgVisibilityFailed
    "Failed to update visibility. Please try again.",
    // SharedMsgAdded
    "Added to your collection",
    // SharedMsgDeleted
    "Item deleted",
    // SharedMsgDismissed
    "Item dismissed",
    // SharedMsgAcceptFailed
    "Failed to accept item",
    // SharedMsgDeleteFailed
    "Failed to delete item",
    // SharedMsgDismissFailed
    "Failed to dismiss item",
    // SharedMsgNoItemId
    "No item ID",
    // SharedMsgLoadFailed
    "Failed to load shared material. Please try again.",

    // --- Phase 1 part 4: settings, shop, profile, login and feedback messages ---
    // FeedbackMsgEmpty
    "Message cannot be empty",
    // FeedbackMsgTooShort
    "Feedback must be at least {min} characters",
    // FeedbackMsgTooLong
    "Feedback must not exceed {max} characters",
    // FeedbackMsgRateLimited
    "You have submitted too much feedback recently. Please try again later.",
    // FeedbackMsgSubmitFailed
    "Failed to submit feedback. Please check your internet connection.",
    // ShopMsgLoadFailed
    "Failed to load shop data",
    // ShopMsgPurchaseFailed
    "Purchase failed",
    // ShopMsgHistoryExpanded
    "History limit expanded to {limit} records!",
    // ShopMsgPaletteAlreadyUnlocked
    "Palette already unlocked",
    // ShopMsgUnlockFailed
    "Failed to unlock palette",
    // ShopMsgPaletteUnlocked
    "Palette unlocked!",
    // ShopMsgPaletteSelectFailed
    "Failed to change the color palette. Please try again.",
    // ProfileMsgLoadFailed
    "Failed to load profile",
    // ProfileMsgUsernameTooShort
    "Username must be at least {min} characters",
    // ProfileMsgUsernameTooLong
    "Username must not exceed {max} characters",
    // ProfileMsgUsernameInvalidChars
    "Username can only contain letters, numbers, and underscores",
    // ProfileMsgVerifyFailed
    "Unable to verify account status. Please try again.",
    // ProfileMsgUsernameTaken
    "Username already taken",
    // ProfileMsgUsernameUpdatedSyncing
    "Username updated. Your friends list may take a moment to refresh.",
    // ProfileMsgUsernameUpdated
    "Username updated successfully",
    // ProfileMsgUpdateFailed
    "Failed to update profile",
    // ProfileMsgSetUsernameFailed
    "Failed to set username",
    // ProfileMsgDeleteFailed
    "Failed to delete account",
    // ProfileMsgInvalidPassword
    "Invalid password. Please try again.",
    // ProfileMsgGoogleReauthFailed
    "Google re-authentication failed. Please try again.",
    // ProfileMsgGoogleNoToken
    "Google sign-in failed: no ID token.",
    // ProfileMsgGoogleSignInFailed
    "Google sign-in failed. Please try again.",
    // ProfileGoogleReauthHint
    "Re-authenticate with your Google account to confirm.",
    // ProfileContinueWithGoogle
    "Continue with Google",
    // SettingsMsgLanguageSaveFailed
    "Failed to save language setting. Please try again.",
    // SettingsMsgFontSaveFailed
    "Failed to save font size. Please try again.",
    // SettingsMsgThemeSaveFailed
    "Failed to save theme setting. Please try again.",
    // SettingsMsgAutoThemeFailed
    "Failed to disable auto theme. Please try again.",
    // SettingsMsgVoiceSaveFailed
    "Failed to save voice setting. Please try again.",
    // SettingsMsgNotificationSaveFailed
    "Failed to save notification setting. Please try again.",
    // AuthMsgEmailEmpty
    "Email cannot be empty",
    // AuthMsgEmailInvalid
    "Invalid email format",
    // AuthMsgTooManyLogins
    "Too many login attempts. Please try again later.",
    // AuthMsgTooManyResets
    "Too many password reset attempts. Please try again later.",
    // AuthMsgGoogleEmptyToken
    "Google sign-in failed: empty ID token.",
    // AuthMsgGoogleNoToken
    "Google sign-in failed: no ID token returned.",
    // AuthMsgGoogleFailedCode
    "Google sign-in failed (code {code}).",
    // AuthMsgGoogleNotConfigured
    "Google sign-in is not configured for this build.",
    // ErrUnknown
    "Unknown error occurred.",
    // ErrNetworkFailed
    "Network connection failed. Please check your internet connection.",
    // ErrOperationTimedOut
    "The operation timed out. Please try again.",
    // ErrCancelled
    "Operation was cancelled.",
    // ErrUnexpected
    "An unexpected error occurred. Please try again.",
    // ErrAuthInvalidEmail
    "The email address is badly formatted.",
    // ErrAuthWrongPassword
    "Invalid password.",
    // ErrAuthUserNotFound
    "No account found with this email.",
    // ErrAuthUserDisabled
    "This account has been disabled.",
    // ErrAuthTooManyRequests
    "Too many requests. Please try again later.",
    // ErrAuthEmailInUse
    "The email address is already in use by another account.",
    // ErrAuthFailed
    "Authentication failed. Please try again.",
    // ErrDbPermission
    "You do not have permission to access this data.",
    // ErrDbUnavailable
    "Service currently unavailable. Please try again later.",
    // ErrDbGeneric
    "Database error. Please try again.",

    // --- Phase 1 part 5: learning, word bank, custom words, favorites and empty states ---
    // LearningMsgLoadFailed
    "Failed to load learning sheets.",
    // LearningMsgSheetLoadFailed
    "Failed to load the learning sheet.",
    // LearningMsgSheetGenerateCancelled
    "Learning sheet generation cancelled.",
    // LearningMsgSheetGenerateFailed
    "Failed to generate the learning sheet. Please try again.",
    // LearningMsgQuizParseIncomplete
    "The quiz has only {parsed} of {total} questions. Please regenerate it.",
    // LearningMsgQuizGenerateCancelled
    "Quiz generation cancelled.",
    // LearningMsgQuizGenerateFailed
    "Failed to generate the quiz. Please try again.",
    // LearningMsgShareNotReady
    "Sheet content is not ready. Please wait for it to load.",
    // QuizMsgNotGenerated
    "No quiz generated yet.",
    // QuizMsgCorrupted
    "Stored quiz is corrupted. Please regenerate.",
    // QuizMsgLoadFailed
    "Failed to load quiz.",
    // QuizMsgSubmitFailed
    "Failed to submit quiz. Please try again.",
    // ShareMsgFailed
    "Failed to share. Please try again.",
    // WordBankMsgLoadFailed
    "Failed to load the word bank.",
    // WordBankMsgDeleteFailed
    "Failed to delete the word. Please try again.",
    // WordBankMsgNeedMoreRecords
    "Need {count} more records to refresh word bank",
    // WordBankMsgNoHistory
    "No translation history found for this language",
    // WordBankMsgGenerateFailed
    "Failed to generate word bank. Please try again.",
    // WordBankMsgShareIncomplete
    "Word data is incomplete. Refresh and try again.",
    // CustomWordsMsgOriginalRequired
    "Original word is required",
    // CustomWordsMsgOriginalTooLong
    "Original word must not exceed {max} characters",
    // CustomWordsMsgTranslatedRequired
    "Translated word is required",
    // CustomWordsMsgTranslatedTooLong
    "Translated word must not exceed {max} characters",
    // CustomWordsMsgPronunciationTooLong
    "Pronunciation must not exceed {max} characters",
    // CustomWordsMsgExampleTooLong
    "Example must not exceed {max} characters",
    // CustomWordsMsgDuplicate
    "This word already exists in your custom word bank",
    // CustomWordsMsgAddFailed
    "Failed to add word",
    // CustomWordsMsgInvalidLanguageUpdate
    "Invalid language update request",
    // CustomWordsMsgTranslateFailed
    "Translation failed: {detail}",
    // CustomWordsMsgUpdateLanguageFailed
    "Failed to update word language",
    // CustomWordsMsgLoadFailed
    "Failed to load custom words",
    // FavoritesMsgLoadFailed
    "Failed to load favorites",
    // FavoritesMsgLoadMoreFailed
    "Failed to load more favorites",
    // FavoritesMsgRefreshFailed
    "Failed to refresh favorites",
    // FavoritesMsgRemoveFailed
    "Failed to remove favorite",
    // FavoritesMsgDeleteSelectedFailed
    "Failed to delete selected items",
    // FavoritesLoadMore
    "Load More",
    // A11yConfirmDelete
    "Confirm delete",
    // A11yDeleteMode
    "Delete mode",
    // A11yExpand
    "Expand",
    // A11yCollapse
    "Collapse",
    // EmptyNoHistoryTitle
    "No History Yet",
    // EmptyNoFavoritesTitle
    "No Favorites",
    // EmptyNoLearningTitle
    "No Learning Materials",
    // EmptySharedItemsMessage
    "No items shared with you yet",
    // SharedMaterialLoadingContent
    "Loading content...",
    // HelpVersionTemplate
    "Version: {version}",
    // StartupTagline
    "Translate & Learn",

    // --- Extended Error Messages (localized) ---
)
