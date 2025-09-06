package com.project.mynoize.activities.signin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.R
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.activities.signin.domain.SignUpValidation
import com.project.mynoize.activities.signin.event.SignUpEvent
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.toErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel (
    val validation: SignUpValidation,
    val authRepository: AuthRepository
): ViewModel(){



    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    fun onEvent(event: SignUpEvent){
        when(event){
            is SignUpEvent.OnUsernameChange -> {
                if(event.username.length < 30){
                    _state.update { it.copy(username = event.username) }
                }
            }
            is SignUpEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email.lowercase()) }
            }
            is SignUpEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.password) }
            }
            is SignUpEvent.OnRepeatedPasswordChange -> {
                _state.update { it.copy(repeatedPassword = event.repeatedPassword) }
            }
            is SignUpEvent.OnSignUpClick -> {
                createAccount()
            }
            is SignUpEvent.OnDismissAlertDialog -> {
                _alertDialogState.update { it.copy(show = false) }
            }
            is SignUpEvent.OnBackClick -> Unit
        }
    }

    fun createAccount(){
        _state.update {
            it.copy(
                username = it.username.trim(),
                usernameError = null,
                email = it.email.trim(),
                emailError = null,
                password = it.password.trim(),
                passwordError = null,
                repeatedPassword = it.repeatedPassword.trim(),
                repeatedPasswordError = null
            )
        }

        validation.execute(
            username = state.value.username,
            email = state.value.email,
            password = state.value.password,
            repeatedPassword = state.value.repeatedPassword
        ).onError { error ->

            when(error){
                InputError.SignUp.ENTER_USERNAME ->{
                    _state.update { it.copy(usernameError = error.toErrorMessage()) }
                }
                InputError.SignUp.USERNAME_TOO_LONG -> {
                    _state.update { it.copy(usernameError = error.toErrorMessage()) }
                }
                InputError.SignUp.ENTER_EMAIL -> {
                    _state.update { it.copy(emailError = error.toErrorMessage()) }
                }
                InputError.SignUp.INCORRECT_EMAIL -> {
                    _state.update { it.copy(emailError = error.toErrorMessage()) }
                }
                InputError.SignUp.ENTER_PASSWORD -> {
                    _state.update { it.copy(passwordError = error.toErrorMessage()) }
                }
                InputError.SignUp.PASSWORD_TOO_SHORT -> {
                    _state.update { it.copy(passwordError = error.toErrorMessage()) }
                }
                InputError.SignUp.PASSWORD_TOO_LONG -> {
                    _state.update { it.copy(passwordError = error.toErrorMessage()) }
                }
                InputError.SignUp.PASSWORD_NO_NUMBERS -> {
                    _state.update { it.copy(passwordError = error.toErrorMessage()) }
                }
                InputError.SignUp.PASSWORD_NO_LETTERS -> {
                    _state.update { it.copy(passwordError = error.toErrorMessage()) }
                }
                InputError.SignUp.ENTER_REPEATED_PASSWORD -> {
                    _state.update { it.copy(repeatedPasswordError = error.toErrorMessage()) }
                }
                InputError.SignUp.PASSWORDS_DO_NOT_MATCH -> {
                    _state.update { it.copy(repeatedPasswordError = error.toErrorMessage()) }
                }

            }
            _alertDialogState.update {
                it.copy(show = true, message = error.toErrorMessage(), warning = true)
            }
            return
        }

        _state.update { it.copy(creatingAccount = true) }

        viewModelScope.launch {
            authRepository.createUserWithEmailAndPassword(state.value.username, state.value.email, state.value.password)
                .onError { error ->
                    _state.update { it.copy(creatingAccount = false) }
                    _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage(), warning = true) }
                }
                .onSuccess {
                    _alertDialogState.update { it.copy(show = true, message = UiText.StringResource(R.string.successfully_account_created), warning = false) }
                }
        }

    }


}