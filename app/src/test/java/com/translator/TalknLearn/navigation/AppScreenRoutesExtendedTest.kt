package com.translator.TalknLearn.navigation

import com.translator.TalknLearn.AppScreen
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AppScreen route formatting.
 *
 * Tests parameterized route generation for screens that take arguments.
 */
class AppScreenRoutesExtendedTest {

    // ── SharedMaterialDetail routes ─────────────────────────────────

    @Test
    fun `SharedMaterialDetail routeFor encodes simple itemId`() {
        val route = AppScreen.SharedMaterialDetail.routeFor("item123")
        assertEquals("shared_material_detail/item123", route)
    }

    @Test
    fun `SharedMaterialDetail routeFor encodes special characters`() {
        val route = AppScreen.SharedMaterialDetail.routeFor("item/with spaces")
        assertTrue(route.startsWith("shared_material_detail/"))
        assertFalse(route.contains(" ")) // spaces should be encoded
    }

    // ── Chat routes ─────────────────────────────────────────────────

    @Test
    fun `Chat routeFor encodes friendId and username`() {
        val route = AppScreen.Chat.routeFor("friend123", "Alice")
        assertEquals("chat/friend123/Alice", route)
    }

    @Test
    fun `Chat routeFor encodes special characters in username`() {
        val route = AppScreen.Chat.routeFor("f1", "user with spaces")
        assertTrue(route.startsWith("chat/f1/"))
        assertFalse(route.contains(" ")) // spaces should be encoded
    }

    // ── LearningSheet routes ────────────────────────────────────────

    @Test
    fun `LearningSheet routeFor constructs correct path`() {
        val route = AppScreen.LearningSheet.routeFor("en-US", "ja-JP")
        assertEquals("learning_sheet/en-US/ja-JP", route)
    }

    // ── Quiz routes ─────────────────────────────────────────────────

    @Test
    fun `Quiz routeFor constructs correct path`() {
        val route = AppScreen.Quiz.routeFor("en-US", "zh-TW")
        assertEquals("quiz/en-US/zh-TW", route)
    }

    // ── Static routes ───────────────────────────────────────────────

    @Test
    fun `all static routes are unique`() {
        val routes = listOf(
            AppScreen.Home.route,
            AppScreen.Speech.route,
            AppScreen.Help.route,
            AppScreen.Continuous.route,
            AppScreen.Login.route,
            AppScreen.History.route,
            AppScreen.ResetPassword.route,
            AppScreen.Learning.route,
            AppScreen.Settings.route,
            AppScreen.WordBank.route,
            AppScreen.Profile.route,
            AppScreen.Favorites.route,
            AppScreen.Shop.route,
            AppScreen.VoiceSettings.route,
            AppScreen.Feedback.route,
            AppScreen.Friends.route,
            AppScreen.MyProfile.route,
            AppScreen.SharedInbox.route,
            AppScreen.BlockedUsers.route,
            AppScreen.Onboarding.route,
            AppScreen.NotificationSettings.route,
            AppScreen.Startup.route,
        )

        assertEquals("All static routes should be unique", routes.size, routes.toSet().size)
    }

    @Test
    fun `static routes are not blank`() {
        val routes = listOf(
            AppScreen.Home.route,
            AppScreen.Speech.route,
            AppScreen.Login.route,
            AppScreen.Settings.route,
            AppScreen.Friends.route,
            AppScreen.Onboarding.route,
            AppScreen.Startup.route,
        )

        routes.forEach { route ->
            assertTrue("Route should not be blank: $route", route.isNotBlank())
        }
    }
}
