package com.project.mynoize.activities.signin.event

sealed class SignInEvent {
    data class OnEmailChange(val email: String) : SignInEvent()
    data class OnPasswordChange(val password: String) : SignInEvent()
    data class OnSignInClick(val onSuccess: () -> Unit) : SignInEvent()
    object OnDismissAlertDialog : SignInEvent()

}