package com.project.mynoize.activities.signin.event

sealed class SignInEvent {
    data class OnEmailChange(val email: String) : SignInEvent()
    data class OnPasswordChange(val password: String) : SignInEvent()
    object OnSignInClick : SignInEvent()
    object OnDismissAlertDialog : SignInEvent()
    object OnSignInWithGoogleClick : SignInEvent()
    object OnSuccessFulSignInWithEmail : SignInEvent()

}