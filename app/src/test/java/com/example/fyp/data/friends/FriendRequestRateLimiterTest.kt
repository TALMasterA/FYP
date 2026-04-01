package com.example.fyp.data.friends

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock

class FriendRequestRateLimiterTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var storage: MutableMap<String, String>
    private lateinit var limiter: FriendRequestRateLimiter

    @Before
    fun setup() {
        context = mock()
        prefs = mock()
        editor = mock()
        storage = mutableMapOf()

        `when`(context.getSharedPreferences("friend_request_rate_limit_prefs", Context.MODE_PRIVATE))
            .thenReturn(prefs)
        `when`(prefs.edit()).thenReturn(editor)
        `when`(editor.putString(any(), anyOrNull())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<String?>(1)
            if (value == null) storage.remove(key) else storage[key] = value
            editor
        }
        `when`(editor.remove(any())).thenAnswer { invocation ->
            storage.remove(invocation.getArgument(0))
            editor
        }
        `when`(prefs.getString(any(), anyOrNull())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            storage[key]
        }

        limiter = SharedPreferencesFriendRequestRateLimiter(context)
    }

    @Test
    fun `allows send before limit is reached`() {
        limiter.recordSend("user1", nowMillis = 1_000L)

        val result = limiter.canSend("user1", nowMillis = 2_000L)

        assertTrue(result.allowed)
        assertEquals(4, result.remainingSends)
    }

    @Test
    fun `blocks send after five requests in window`() {
        repeat(5) { index ->
            limiter.recordSend("user1", nowMillis = 1_000L + index)
        }

        val result = limiter.canSend("user1", nowMillis = 2_000L)

        assertFalse(result.allowed)
        assertTrue(result.retryAfterMillis > 0L)
        assertEquals(0, result.remainingSends)
    }

    @Test
    fun `expired timestamps are pruned`() {
        limiter.recordSend("user1", nowMillis = 0L)
        limiter.recordSend("user1", nowMillis = 1_000L)

        val result = limiter.canSend("user1", nowMillis = 3_700_000L)

        assertTrue(result.allowed)
        assertEquals(MAX_FRIEND_REQUESTS_PER_HOUR, result.remainingSends)
    }
}
