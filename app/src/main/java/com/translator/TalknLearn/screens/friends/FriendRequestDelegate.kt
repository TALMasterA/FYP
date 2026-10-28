package com.translator.TalknLearn.screens.friends

import com.translator.TalknLearn.core.security.AuditLogger
import com.translator.TalknLearn.core.security.ValidationResult
import com.translator.TalknLearn.core.security.sanitizeInput
import com.translator.TalknLearn.core.security.validateTextLength
import com.translator.TalknLearn.data.friends.FriendRequestRateLimiter
import com.translator.TalknLearn.data.friends.MAX_FRIEND_REQUESTS_PER_HOUR
import com.translator.TalknLearn.domain.friends.AcceptFriendRequestUseCase
import com.translator.TalknLearn.domain.friends.CancelFriendRequestUseCase
import com.translator.TalknLearn.domain.friends.RejectFriendRequestUseCase
import com.translator.TalknLearn.domain.friends.SendFriendRequestUseCase
import com.translator.TalknLearn.model.UserId
import com.translator.TalknLearn.observability.FunnelAnalyticsTracker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

/** Maximum pending outgoing friend requests a user can have at any one time. */
private const val MAX_PENDING_REQUESTS = 20

/**
 * Handles friend-request send / accept / reject / cancel and batch operations.
 * Extracted from [FriendsViewModel] for maintainability.
 */
internal class FriendRequestDelegate(
    private val uiState: MutableStateFlow<FriendsUiState>,
    private val scope: CoroutineScope,
    private val friendRequestRateLimiter: FriendRequestRateLimiter,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val funnelTracker: FunnelAnalyticsTracker,
    private val getCurrentUserId: () -> UserId?,
    private val requireUsername: () -> Boolean,
    private val showSuccess: (String) -> Unit
) {
    private var batchJob: Job? = null

    fun sendFriendRequest(toUserId: String, note: String = "") {
        val fromUserId = getCurrentUserId() ?: return
        if (!requireUsername()) return
        if (uiState.value.outgoingRequests.size >= MAX_PENDING_REQUESTS) {
            uiState.value = uiState.value.copy(
                error = "You have reached the maximum of 20 pending friend requests. " +
                        "Please wait for some to be accepted or cancel them before sending more."
            )
            return
        }

        val rateLimitStatus = friendRequestRateLimiter.canSend(fromUserId.value)
        if (!rateLimitStatus.allowed) {
            val retryAfterMinutes = ceil(rateLimitStatus.retryAfterMillis / 60000.0)
                .toInt()
                .coerceAtLeast(1)
            uiState.value = uiState.value.copy(
                error = "Rate limit exceeded. You can send up to $MAX_FRIEND_REQUESTS_PER_HOUR friend requests per hour. Try again in about $retryAfterMinutes minute(s)."
            )
            return
        }

        val noteValidation = validateTextLength(note, minLength = 0, maxLength = 200, fieldName = "Note")
        if (noteValidation is ValidationResult.Invalid) {
            uiState.value = uiState.value.copy(error = noteValidation.message)
            return
        }

        val sanitizedNote = sanitizeInput(note)

        scope.launch {
            sendFriendRequestUseCase(fromUserId, UserId(toUserId), sanitizedNote).fold(
                onSuccess = {
                    friendRequestRateLimiter.recordSend(fromUserId.value)
                    AuditLogger.logFriendRequestSent(fromUserId.value, toUserId)
                    uiState.value = uiState.value.copy(
                        error = null,
                        searchResults = emptyList(),
                        searchQuery = ""
                    )
                    showSuccess("Friend request sent! They will be notified.")
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("Already friends", ignoreCase = true) == true ->
                            "You are already friends with this user."
                        e.message?.contains("already sent", ignoreCase = true) == true ->
                            "You already have a pending request to this user. Please wait for their reply."
                        e.message?.contains("profile not found", ignoreCase = true) == true ->
                            "Could not find the user's profile. They may have deleted their account."
                        e.message?.contains("blocked this user", ignoreCase = true) == true ->
                            "You have blocked this user. Unblock them first."
                        e.message?.contains("Unable to send", ignoreCase = true) == true ->
                            "Unable to send friend request."
                        else -> "Failed to send friend request. Please try again."
                    }
                    uiState.value = uiState.value.copy(error = message)
                }
            )
        }
    }

    fun acceptFriendRequest(requestId: String) {
        val userId = getCurrentUserId() ?: return
        if (!requireUsername()) return
        val friendUserId = uiState.value.incomingRequests
            .firstOrNull { it.requestId == requestId }
            ?.fromUserId
            ?.let { UserId(it) } ?: return
        scope.launch {
            acceptFriendRequestUseCase(requestId, userId, friendUserId).fold(
                onSuccess = {
                    AuditLogger.logFriendRequestAccepted(userId.value, friendUserId.value)
                    funnelTracker.logFirstFriendAdd()
                    uiState.value = uiState.value.copy(error = null)
                    showSuccess("Friend request accepted! You are now friends.")
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            "This request no longer exists — it may have been cancelled."
                        e.message?.contains("not authorized", ignoreCase = true) == true ->
                            "You are not authorized to accept this request."
                        else -> "Failed to accept request. Please try again."
                    }
                    uiState.value = uiState.value.copy(error = message)
                }
            )
        }
    }

    fun rejectFriendRequest(requestId: String) {
        val uid = getCurrentUserId()?.value ?: ""
        val fromUser = uiState.value.incomingRequests
            .firstOrNull { it.requestId == requestId }?.fromUserId ?: ""
        scope.launch {
            rejectFriendRequestUseCase(requestId).fold(
                onSuccess = {
                    AuditLogger.logFriendRequestRejected(uid, fromUser)
                    uiState.value = uiState.value.copy(error = null)
                    showSuccess("Request declined.")
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            "This request no longer exists."
                        else -> "Failed to decline request. Please try again."
                    }
                    uiState.value = uiState.value.copy(error = message)
                }
            )
        }
    }

    fun cancelFriendRequest(requestId: String) {
        scope.launch {
            cancelFriendRequestUseCase(requestId).fold(
                onSuccess = {
                    uiState.value = uiState.value.copy(error = null)
                    showSuccess("Friend request cancelled.")
                },
                onFailure = { e ->
                    val message = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            "This request no longer exists — it may have already been accepted or declined."
                        else -> "Failed to cancel request. Please try again."
                    }
                    uiState.value = uiState.value.copy(error = message)
                }
            )
        }
    }

    fun acceptAllRequests() {
        val userId = getCurrentUserId() ?: return
        if (!requireUsername()) return
        val requests = uiState.value.incomingRequests
        if (requests.isEmpty() || batchJob?.isActive == true) return

        batchJob = scope.launch {
            val total = requests.size
            var successCount = 0
            var failCount = 0
            var processedCount = 0

            try {
                requests.forEachIndexed { index, request ->
                    ensureActive()
                    uiState.value = uiState.value.copy(batchProgress = "${index + 1}/$total")
                    val friendUserId = UserId(request.fromUserId)
                    acceptFriendRequestUseCase(request.requestId, userId, friendUserId).fold(
                        onSuccess = {
                            funnelTracker.logFirstFriendAdd()
                            successCount++
                        },
                        onFailure = { failCount++ }
                    )
                    processedCount = index + 1
                }

                val message = when {
                    failCount == 0 -> "All $successCount friend requests accepted!"
                    successCount == 0 -> "Failed to accept requests. Please try again."
                    else -> "$successCount accepted, $failCount failed. Please retry for failed ones."
                }

                if (successCount == 0 && failCount > 0) {
                    uiState.value = uiState.value.copy(error = message)
                } else if (successCount > 0) {
                    uiState.value = uiState.value.copy(error = null)
                    showSuccess(message)
                }
            } catch (_: CancellationException) {
                uiState.value = uiState.value.copy(error = null)
                showSuccess(
                    if (processedCount > 0) {
                        "Batch accept cancelled after $processedCount/$total request(s)."
                    } else {
                        "Batch accept cancelled."
                    }
                )
            } finally {
                uiState.value = uiState.value.copy(batchProgress = null)
                batchJob = null
            }
        }
    }

    fun rejectAllRequests() {
        val requests = uiState.value.incomingRequests
        if (requests.isEmpty() || batchJob?.isActive == true) return

        batchJob = scope.launch {
            val total = requests.size
            var successCount = 0
            var processedCount = 0
            try {
                requests.forEachIndexed { index, request ->
                    ensureActive()
                    uiState.value = uiState.value.copy(batchProgress = "${index + 1}/$total")
                    rejectFriendRequestUseCase(request.requestId).fold(
                        onSuccess = { successCount++ },
                        onFailure = { /* count silently */ }
                    )
                    processedCount = index + 1
                }
                uiState.value = uiState.value.copy(error = null)
                showSuccess("Declined $successCount request(s).")
            } catch (_: CancellationException) {
                uiState.value = uiState.value.copy(error = null)
                showSuccess(
                    if (processedCount > 0) {
                        "Batch reject cancelled after $processedCount/$total request(s)."
                    } else {
                        "Batch reject cancelled."
                    }
                )
            } finally {
                uiState.value = uiState.value.copy(batchProgress = null)
                batchJob = null
            }
        }
    }

    fun cancelBatchOperation() {
        batchJob?.cancel()
    }

    fun cancelJobs() {
        batchJob?.cancel()
    }
}
