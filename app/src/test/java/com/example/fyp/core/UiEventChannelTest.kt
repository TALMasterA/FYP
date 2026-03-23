package com.example.fyp.core

import com.example.fyp.model.ui.UiTextKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiEventChannelTest {

    @Test
    fun `send emits snackbar event to flow`() = runTest {
        val channel = UiEventChannel()
        val event = UiEvent.ShowSnackbar(message = "Saved", actionLabel = "Undo")

        channel.send(event)
        val received = channel.flow.first()

        assertEquals(event, received)
    }

    @Test
    fun `send preserves typed key snackbar payload`() = runTest {
        val channel = UiEventChannel()
        val event = UiEvent.ShowSnackbarKey(key = UiTextKey.ToastCopied, actionLabel = null)

        channel.send(event)
        val received = channel.flow.first()

        assertTrue(received is UiEvent.ShowSnackbarKey)
        val typed = received as UiEvent.ShowSnackbarKey
        assertEquals(UiTextKey.ToastCopied, typed.key)
        assertEquals(null, typed.actionLabel)
    }

    @Test
    fun `events are received in send order`() = runTest {
        val channel = UiEventChannel()
        val first = UiEvent.Navigate("home")
        val second = UiEvent.ShowToast("Done")

        channel.send(first)
        channel.send(second)

        val received = channel.flow.take(2).toList()

        assertEquals(listOf(first, second), received)
    }
}
