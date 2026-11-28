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
import com.translator.TalknLearn.model.ui.StatusText
import com.translator.TalknLearn.model.ui.UiTextKey
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

/** Maximum length of the optional note sent with a friend request. */
private const val MAX_NOTE_LENGTH = 200

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
    private val showSuccess: (StatusText) -> Unit
) {
    private var batchJob: Job? = null

    fun sendFriendRequest(toUserId: String, note: String = "") {
        val fromUserId = getCurrentUserId() ?: return
        if (!requireUsername()) return
        if (uiState.value.outgoingRequests.size >= MAX_PENDING_REQUESTS) {
            uiState.value = uiState.value.copy(
                error = StatusText(
                    UiTextKey.FriendsMsgMaxPending,
                    mapOf("limit" to MAX_PENDING_REQUESTS.toString()),
                ),
            )
            return
        }

        val rateLimitStatus = friendRequestRateLimiter.canSend(fromUserId.value)
        if (!rateLimitStatus.allowed) {
            val retryAfterMinutes = ceil(rateLimitStatus.retryAfterMillis / 60000.0)
                .toInt()
                .coerceAtLeast(1)
            uiState.value = uiState.value.copy(
                error = StatusText(
                    UiTextKey.FriendsMsgRateLimit,
                    mapOf(
                        "limit" to MAX_FRIEND_REQUESTS_PER_HOUR.toString(),
                        "minutes" to retryAfterMinutes.toString(),
                    ),
                ),
            )
            return
        }

        val noteValidation = validateTextLength(note, minLength = 0, maxLength = MAX_NOTE_LENGTH, fieldName = "Note")
        if (noteValidation is ValidationResult.Invalid) {
            uiState.value = uiState.value.copy(
                error = StatusText(UiTextKey.FriendsMsgNoteTooLong, mapOf("max" to MAX_NOTE_LENGTH.toString())),
            )
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
                    showSuccess(StatusText(UiTextKey.FriendsMsgRequestSent))
                },
                onFailure = { e ->
                    val key = when {
                        e.message?.contains("Already friends", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgAlreadyFriends
                        e.message?.contains("already sent", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgRequestPending
                        e.message?.contains("profile not found", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgProfileMissing
                        e.message?.contains("blocked this user", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgYouBlocked
                        e.message?.contains("Unable to send", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgUnableToSend
                        else -> UiTextKey.FriendsMsgSendFailed
                    }
                    uiState.value = uiState.value.copy(error = StatusText(key))
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
                    showSuccess(StatusText(UiTextKey.FriendsMsgAccepted))
                },
                onFailure = { e ->
                    val key = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgAcceptGone
                        e.message?.contains("not authorized", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgAcceptNotAuthorized
                        else -> UiTextKey.FriendsMsgAcceptFailed
                    }
                    uiState.value = uiState.value.copy(error = StatusText(key))
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
                    showSuccess(StatusText(UiTextKey.FriendsMsgDeclined))
                },
                onFailure = { e ->
                    val key = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgDeclineGone
                        else -> UiTextKey.FriendsMsgDeclineFailed
                    }
                    uiState.value = uiState.value.copy(error = StatusText(key))
                }
            )
        }
    }

    fun cancelFriendRequest(requestId: String) {
        scope.launch {
            cancelFriendRequestUseCase(requestId).fold(
                onSuccess = {
                    uiState.value = uiState.value.copy(error = null)
                    showSuccess(StatusText(UiTextKey.FriendsMsgCancelled))
                },
                onFailure = { e ->
                    val key = when {
                        e.message?.contains("not found", ignoreCase = true) == true ->
                            UiTextKey.FriendsMsgCancelGone
                        else -> UiTextKey.FriendsMsgCancelFailed
                    }
                    uiState.value = uiState.value.copy(error = StatusText(key))
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
                    failCount == 0 -> StatusText(
                        UiTextKey.FriendsMsgBatchAcceptAll,
                        mapOf("count" to successCount.toString()),
                    )
                    successCount == 0 -> StatusText(UiTextKey.FriendsMsgBatchAcceptFailed)
                    else -> StatusText(
                        UiTextKey.FriendsMsgBatchAcceptMixed,
                        mapOf("success" to successCount.toString(), "failed" to failCount.toString()),
                    )
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
                        StatusText(
                            UiTextKey.FriendsMsgBatchAcceptStoppedAt,
                            mapOf("processed" to processedCount.toString(), "total" to total.toString()),
                        )
                    } else {
                        StatusText(UiTextKey.FriendsMsgBatchAcceptStopped)
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
                showSuccess(
                    StatusText(UiTextKey.FriendsMsgBatchDeclined, mapOf("count" to successCount.toString())),
                )
            } catch (_: CancellationException) {
                uiState.value = uiState.value.copy(error = null)
                showSuccess(
                    if (processedCount > 0) {
                        StatusText(
                            UiTextKey.FriendsMsgBatchDeclineStoppedAt,
                            mapOf("processed" to processedCount.toString(), "total" to total.toString()),
                        )
                    } else {
                        StatusText(UiTextKey.FriendsMsgBatchDeclineStopped)
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
