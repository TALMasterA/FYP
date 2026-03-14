package com.example.fyp.core

import com.example.fyp.model.ui.UiTextKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Standardised one-shot UI events emitted by ViewModels.
 *
 * Unlike state (which persists in [MutableStateFlow] and is re-emitted on
 * resubscription), these events are consumed exactly once — ideal for
 * snackbar messages, toasts, and navigation commands that should not
 * replay on configuration change.
 *
 * Usage in a ViewModel:
 * ```
 * private val _events = UiEventChannel()
 * val events: Flow<UiEvent> = _events.flow
 *
 * fun doSomething() {
 *     viewModelScope.launch {
 *         _events.send(UiEvent.ShowSnackbar("Done!"))
 *     }
 * }
 * ```
 *
 * Consumption in a Composable:
 * ```
 * LaunchedEffect(Unit) {
 *     viewModel.events.collect { event ->
 *         when (event) {
 *             is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
 *             is UiEvent.ShowSnackbarKey -> { /* resolve key then show */ }
 *             is UiEvent.Navigate -> navController.navigate(event.route)
 *             is UiEvent.ShowToast -> Toast.makeText(ctx, event.message, Toast.LENGTH_SHORT).show()
 *         }
 *     }
 * }
 * ```
 */
sealed class UiEvent {
    /** Show a snackbar with a raw string message. */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null
    ) : UiEvent()

    /** Show a snackbar using a translatable [UiTextKey]. */
    data class ShowSnackbarKey(
        val key: UiTextKey,
        val actionLabel: String? = null
    ) : UiEvent()

    /** Trigger navigation to a route. */
    data class Navigate(val route: String) : UiEvent()

    /** Show a short toast message. */
    data class ShowToast(val message: String) : UiEvent()
}

/**
 * Convenience wrapper around [Channel] for emitting [UiEvent]s.
 *
 * Uses [Channel.BUFFERED] to avoid suspending the producer when the
 * collector is briefly unavailable (e.g. during configuration change).
 */
class UiEventChannel {
    private val channel = Channel<UiEvent>(Channel.BUFFERED)

    /** Expose as a cold [Flow] for collectors. */
    val flow: Flow<UiEvent> = channel.receiveAsFlow()

    /** Send a single event. Safe to call from any coroutine. */
    suspend fun send(event: UiEvent) {
        channel.send(event)
    }
}
