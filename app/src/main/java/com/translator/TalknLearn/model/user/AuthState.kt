package com.translator.TalknLearn.model.user

sealed interface AuthState {
    data object Loading : AuthState
    data object LoggedOut : AuthState
    data class LoggedIn(val user: User) : AuthState
}