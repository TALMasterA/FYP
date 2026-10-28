package com.translator.TalknLearn.screens.friends

import com.translator.TalknLearn.domain.friends.SearchUsersUseCase
import com.translator.TalknLearn.model.UserId
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Handles user-search logic for the Friends screen.
 * Extracted from [FriendsViewModel] for maintainability.
 */
internal class FriendSearchDelegate(
    private val uiState: MutableStateFlow<FriendsUiState>,
    private val scope: CoroutineScope,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val friendsRepository: com.translator.TalknLearn.data.friends.FriendsRepository,
    private val getCurrentUserId: () -> UserId?
) {
    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        uiState.value = uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        val trimmed = query.trim()

        if (trimmed.length >= 3) {
            searchJob = scope.launch {
                delay(500)
                performCombinedSearch(trimmed)
            }
        } else {
            uiState.value = uiState.value.copy(searchResults = emptyList(), isSearching = false)
        }
    }

    private suspend fun performCombinedSearch(query: String) {
        uiState.value = uiState.value.copy(isSearching = true)

        val caller = getCurrentUserId()

        val (usernameResults, idResult) = coroutineScope {
            val usernameSearchDeferred = async {
                searchUsersUseCase(query, callerUserId = caller).getOrElse { emptyList() }
            }

            val idSearchDeferred = if (!query.contains(' ')) {
                async {
                    try {
                        friendsRepository.findByUserId(UserId(query), callerUserId = caller)
                    } catch (_: Exception) {
                        null
                    }
                }
            } else null

            Pair(usernameSearchDeferred.await(), idSearchDeferred?.await())
        }

        val currentUid = getCurrentUserId()?.value
        val currentFriendIds = uiState.value.friends.map { it.friendId }.toSet()
        val blockedIds = uiState.value.blockedUserIds
        val distinctResults = (listOfNotNull(idResult) + usernameResults)
            .distinctBy { it.uid }
            .filter { profile ->
                profile.uid != currentUid &&
                    profile.uid !in currentFriendIds &&
                    profile.uid !in blockedIds
            }

        uiState.value = uiState.value.copy(
            searchResults = distinctResults,
            isSearching = false,
            error = null
        )
    }

    fun cancelJob() {
        searchJob?.cancel()
    }
}
