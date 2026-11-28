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

    // --- Extended Error Messages (localized) ---
)
