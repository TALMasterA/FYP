package com.example.fyp.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fyp.data.auth.FirebaseAuthRepository
import com.example.fyp.domain.history.DeleteHistoryRecordUseCase
import com.example.fyp.domain.history.DeleteSessionUseCase
import com.example.fyp.domain.history.ObserveSessionNamesUseCase
import com.example.fyp.domain.history.ObserveUserHistoryUseCase
import com.example.fyp.domain.history.RenameSessionUseCase
import com.example.fyp.model.AuthState
import com.example.fyp.model.TranslationRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val records: List<TranslationRecord> = emptyList(),
    val sessionNames: Map<String, String> = emptyMap()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepository,
    private val observeUserHistory: ObserveUserHistoryUseCase,
    private val observeSessionNames: ObserveSessionNamesUseCase,
    private val deleteHistoryRecord: DeleteHistoryRecordUseCase,
    private val renameSession: RenameSessionUseCase,
    private val deleteSession: DeleteSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var historyJob: Job? = null
    private var sessionsJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.currentUserState.collect { auth ->
                when (auth) {
                    is AuthState.LoggedIn -> {
                        currentUserId = auth.user.uid
                        startListening(auth.user.uid)
                    }

                    AuthState.LoggedOut -> {
                        currentUserId = null
                        historyJob?.cancel()
                        sessionsJob?.cancel()
                        _uiState.value = HistoryUiState(
                            isLoading = false,
                            error = "Not logged in",
                            records = emptyList(),
                            sessionNames = emptyMap()
                        )
                    }

                    AuthState.Loading -> {
                        currentUserId = null
                        historyJob?.cancel()
                        sessionsJob?.cancel()
                        _uiState.value = HistoryUiState(
                            isLoading = true,
                            error = null,
                            records = emptyList(),
                            sessionNames = emptyMap()
                        )
                    }
                }
            }
        }
    }

    fun deleteRecord(record: TranslationRecord) {
        val uid = record.userId.ifBlank { currentUserId.orEmpty() }
        if (uid.isBlank() || record.id.isBlank()) return

        viewModelScope.launch {
            runCatching { deleteHistoryRecord(uid, record.id) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "Delete failed")
                }
        }
    }

    fun renameSession(sessionId: String, name: String) {
        val uid = currentUserId.orEmpty()
        if (uid.isBlank() || sessionId.isBlank()) return

        viewModelScope.launch {
            runCatching { renameSession(uid, sessionId, name) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "Rename failed")
                }
        }
    }

    fun deleteSession(sessionId: String) {
        val uid = currentUserId.orEmpty()
        if (uid.isBlank() || sessionId.isBlank()) return

        viewModelScope.launch {
            runCatching { deleteSession(uid, sessionId) }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "Delete session failed")
                }
        }
    }

    private fun startListening(userId: String) {
        historyJob?.cancel()
        sessionsJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        historyJob = viewModelScope.launch {
            observeUserHistory(userId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message,
                        records = emptyList()
                    )
                }
                .collect { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null,
                        records = list
                    )
                }
        }

        sessionsJob = viewModelScope.launch {
            observeSessionNames(userId)
                .catch { /* optional: ignore */ }
                .collect { map ->
                    _uiState.value = _uiState.value.copy(sessionNames = map)
                }
        }
    }
}