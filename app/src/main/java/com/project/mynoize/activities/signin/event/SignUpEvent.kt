package com.project.mynoize.activities.signin.event

sealed class SignUpEvent {
    data class OnUsernameChange(val username: String): SignUpEvent()
    data class OnEmailChange(val email: String): SignUpEvent()
    data class OnPasswordChange(val password: String): SignUpEvent()
    data class OnRepeatedPasswordChange(val repeatedPassword: String): SignUpEvent()
    object OnSignUpClick: SignUpEvent()
    object OnDismissAlertDialog: SignUpEvent()

}