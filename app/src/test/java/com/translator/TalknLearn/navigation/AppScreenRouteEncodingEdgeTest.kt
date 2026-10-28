package com.translator.TalknLearn.navigation

import com.translator.TalknLearn.AppScreen
import java.net.URLEncoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppScreenRouteEncodingEdgeTest {

    @Test
    fun `shared material routeFor percent-encodes reserved characters`() {
        val rawItemId = "id/with spaces?and#symbols"
        val expectedEncoded = URLEncoder.encode(rawItemId, Charsets.UTF_8.name())

        val route = AppScreen.SharedMaterialDetail.routeFor(rawItemId)

        assertEquals("shared_material_detail/$expectedEncoded", route)
        assertTrue(route.startsWith("shared_material_detail/"))
    }

    @Test
    fun `chat routeFor encodes both friend id and username independently`() {
        val rawFriendId = "user/alpha beta"
        val rawUsername = "A&B?Name#Z"
        val expectedFriendId = URLEncoder.encode(rawFriendId, Charsets.UTF_8.name())
        val expectedUsername = URLEncoder.encode(rawUsername, Charsets.UTF_8.name())

        val route = AppScreen.Chat.routeFor(rawFriendId, rawUsername)

        assertEquals("chat/$expectedFriendId/$expectedUsername", route)
    }

    @Test
    fun `parameterized route templates keep expected placeholders`() {
        assertEquals("chat/{friendId}/{friendUsername}", AppScreen.Chat.route)
        assertEquals("shared_material_detail/{itemId}", AppScreen.SharedMaterialDetail.route)
        assertEquals("learning_sheet/{primaryCode}/{targetCode}", AppScreen.LearningSheet.route)
        assertEquals("quiz/{primaryCode}/{targetCode}", AppScreen.Quiz.route)
    }
}
