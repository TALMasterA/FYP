package com.example.fyp.model

sealed class AuthState {
    object LoggedOut : AuthState()
    data class LoggedIn(val user: User) : AuthState()
}