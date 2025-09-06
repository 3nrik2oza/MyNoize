package com.project.mynoize.activities.signin

import com.project.mynoize.core.presentation.UiText

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val loading: Boolean = false
)
