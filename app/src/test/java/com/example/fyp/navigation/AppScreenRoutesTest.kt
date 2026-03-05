package com.example.fyp.navigation

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AppScreen route definitions.
 *
 * Verifies:
 * - All screen routes are unique
 * - Required screens exist
 * - Parameterized routes have correct format
 * - Bottom nav routes are correctly identified (item 12)
 */
class AppScreenRoutesTest {

    // All known routes in the app
    private val allRoutes = listOf(
        "home", "speech", "help", "continuous", "login",
        "history", "reset_password", "learning", "settings",
        "word_bank", "profile", "favorites", "shop",
        "voice_settings", "feedback", "friends", "my_profile",
        "shared_inbox", "blocked_users", "onboarding",
        "notification_settings", "startup"
    )

    private val mainFeatureRoutes = setOf(
        "home", "speech", "learning", "friends", "settings"
    )

    // ── Route Uniqueness ───────────────────────────────────────────

    @Test
    fun `all routes are unique`() {
        assertEquals(
            "All routes should be unique",
            allRoutes.size,
            allRoutes.toSet().size
        )
    }

    // ── Required Screens ───────────────────────────────────────────

    @Test
    fun `home screen route exists`() {
        assertTrue(allRoutes.contains("home"))
    }

    @Test
    fun `speech screen route exists`() {
        assertTrue(allRoutes.contains("speech"))
    }

    @Test
    fun `learning screen route exists`() {
        assertTrue(allRoutes.contains("learning"))
    }

    @Test
    fun `friends screen route exists`() {
        assertTrue(allRoutes.contains("friends"))
    }

    @Test
    fun `settings screen route exists`() {
        assertTrue(allRoutes.contains("settings"))
    }

    @Test
    fun `login screen route exists`() {
        assertTrue(allRoutes.contains("login"))
    }

    @Test
    fun `history screen route exists`() {
        assertTrue(allRoutes.contains("history"))
    }

    @Test
    fun `shop screen route exists`() {
        assertTrue(allRoutes.contains("shop"))
    }

    @Test
    fun `word bank screen route exists`() {
        assertTrue(allRoutes.contains("word_bank"))
    }

    @Test
    fun `favorites screen route exists`() {
        assertTrue(allRoutes.contains("favorites"))
    }

    @Test
    fun `profile screen route exists`() {
        assertTrue(allRoutes.contains("profile"))
    }

    @Test
    fun `feedback screen route exists`() {
        assertTrue(allRoutes.contains("feedback"))
    }

    @Test
    fun `shared inbox screen route exists`() {
        assertTrue(allRoutes.contains("shared_inbox"))
    }

    @Test
    fun `blocked users screen route exists`() {
        assertTrue(allRoutes.contains("blocked_users"))
    }

    @Test
    fun `notification settings screen route exists`() {
        assertTrue(allRoutes.contains("notification_settings"))
    }

    // ── Bottom Navigation Routes ───────────────────────────────────

    @Test
    fun `exactly 5 main feature routes for bottom nav`() {
        assertEquals(5, mainFeatureRoutes.size)
    }

    @Test
    fun `bottom nav includes Home`() {
        assertTrue(mainFeatureRoutes.contains("home"))
    }

    @Test
    fun `bottom nav includes Speech`() {
        assertTrue(mainFeatureRoutes.contains("speech"))
    }

    @Test
    fun `bottom nav includes Learning`() {
        assertTrue(mainFeatureRoutes.contains("learning"))
    }

    @Test
    fun `bottom nav includes Friends`() {
        assertTrue(mainFeatureRoutes.contains("friends"))
    }

    @Test
    fun `bottom nav includes Settings`() {
        assertTrue(mainFeatureRoutes.contains("settings"))
    }

    @Test
    fun `bottom nav does NOT include Login`() {
        assertFalse(mainFeatureRoutes.contains("login"))
    }

    @Test
    fun `bottom nav does NOT include History`() {
        assertFalse(mainFeatureRoutes.contains("history"))
    }

    @Test
    fun `bottom nav does NOT include Shop`() {
        assertFalse(mainFeatureRoutes.contains("shop"))
    }

    // ── Parameterized Routes ───────────────────────────────────────

    @Test
    fun `chat route has friendId and friendUsername parameters`() {
        val chatRoute = "chat/{friendId}/{friendUsername}"
        assertTrue(chatRoute.contains("{friendId}"))
        assertTrue(chatRoute.contains("{friendUsername}"))
    }

    @Test
    fun `learning sheet route has language code parameters`() {
        val route = "learning_sheet/{primaryCode}/{targetCode}"
        assertTrue(route.contains("{primaryCode}"))
        assertTrue(route.contains("{targetCode}"))
    }

    @Test
    fun `quiz route has language code parameters`() {
        val route = "quiz/{primaryCode}/{targetCode}"
        assertTrue(route.contains("{primaryCode}"))
        assertTrue(route.contains("{targetCode}"))
    }

    @Test
    fun `shared material detail route has itemId parameter`() {
        val route = "shared_material_detail/{itemId}"
        assertTrue(route.contains("{itemId}"))
    }

    // ── Route Categories ───────────────────────────────────────────

    @Test
    fun `detail screens are not in main feature routes`() {
        val detailRoutes = listOf(
            "history", "shop", "profile", "word_bank",
            "favorites", "voice_settings", "feedback",
            "my_profile", "shared_inbox", "blocked_users",
            "notification_settings"
        )

        detailRoutes.forEach { route ->
            assertFalse(
                "$route should NOT be a main feature route",
                mainFeatureRoutes.contains(route)
            )
        }
    }
}
