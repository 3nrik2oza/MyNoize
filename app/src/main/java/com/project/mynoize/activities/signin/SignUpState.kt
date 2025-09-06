package com.project.mynoize.activities.signin

import com.project.mynoize.core.presentation.UiText

data class SignUpState (
    val creatingAccount: Boolean = false,
    val username: String = "",
    val usernameError: UiText? = null,
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: UiText? = null
)