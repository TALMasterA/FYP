package com.example.fyp

/**
 * All navigation route destinations in the app.
 * Kept in a separate file from AppNavigation.kt so routes can be referenced
 * or inspected without pulling in the full nav-graph composable.
 */
sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object Speech : AppScreen("speech")
    object Help : AppScreen("help")
    object Continuous : AppScreen("continuous")
    object Login : AppScreen("login")
    object History : AppScreen("history")
    object ResetPassword : AppScreen("reset_password")
    object Learning : AppScreen("learning")
    object Settings : AppScreen("settings")
    object WordBank : AppScreen("word_bank")
    object Profile : AppScreen("profile")
    object Favorites : AppScreen("favorites")
    object Shop : AppScreen("shop")
    object VoiceSettings : AppScreen("voice_settings")
    object Feedback : AppScreen("feedback")
    object Friends : AppScreen("friends")
    object MyProfile : AppScreen("my_profile")
    object SharedInbox : AppScreen("shared_inbox")
    object BlockedUsers : AppScreen("blocked_users")
    object Onboarding : AppScreen("onboarding")
    object NotificationSettings : AppScreen("notification_settings")
    object Startup : AppScreen("startup")

    object SharedMaterialDetail : AppScreen("shared_material_detail/{itemId}") {
        fun routeFor(itemId: String) =
            "shared_material_detail/${java.net.URLEncoder.encode(itemId, "UTF-8")}"
    }

    object Chat : AppScreen("chat/{friendId}/{friendUsername}/{friendDisplayName}") {
        fun routeFor(friendId: String, friendUsername: String, friendDisplayName: String = "") =
            "chat/${java.net.URLEncoder.encode(friendId, "UTF-8")}/${java.net.URLEncoder.encode(friendUsername, "UTF-8")}/${java.net.URLEncoder.encode(friendDisplayName, "UTF-8")}"
    }

    object LearningSheet : AppScreen("learning_sheet/{primaryCode}/{targetCode}") {
        fun routeFor(primaryCode: String, targetCode: String) =
            "learning_sheet/$primaryCode/$targetCode"
    }

    object Quiz : AppScreen("quiz/{primaryCode}/{targetCode}") {
        fun routeFor(primaryCode: String, targetCode: String) =
            "quiz/$primaryCode/$targetCode"
    }
}
