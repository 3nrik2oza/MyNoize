package com.project.mynoize.activities.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.project.mynoize.core.data.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.mynoize.activities.main.state.AlertDialogState
import com.project.mynoize.activities.signin.domain.SignInValidation
import com.project.mynoize.activities.signin.event.SignInEvent
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.toErrorMessage
import kotlinx.coroutines.launch

class SignInViewModel(
    private val validation: SignInValidation,
    private val auth: AuthRepository
): ViewModel() {



    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }


    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email.lowercase()) }
            }

            is SignInEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.password) }
            }
            is SignInEvent.OnSignInClick -> {
                signIn(event.onSuccess)
            }
            is SignInEvent.OnDismissAlertDialog -> {
                _alertDialogState.update { it.copy(show = false) }
            }
        }
    }

    fun signIn(onSuccess: () -> Unit){
        _alertDialogState.update { it.copy(show = false) }
        _state.update { it.copy(loading = true, email = it.email.trim(),
            password = it.password.trim(), emailError = null, passwordError = null) }


        validation.execute(email = _state.value.email, password = _state.value.password).onError { error->
            when (error) {
                InputError.SingIn.ENTER_EMAIL -> {
                    _state.update { it.copy(loading = false, emailError = error.toErrorMessage()) }
                }
                InputError.SingIn.INCORRECT_EMAIL -> {
                    _state.update { it.copy(loading = false, emailError = error.toErrorMessage()) }
                }
                InputError.SingIn.ENTER_PASSWORD -> {
                    _state.update { it.copy(loading = false, passwordError = error.toErrorMessage()) }
                }
            }

            _alertDialogState.update { it.copy(
                show = true,
                message = error.toErrorMessage()
            ) }

            return
        }

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(_state.value.email, _state.value.password)
                .onError { error ->
                    _state.update { it.copy(loading = false) }
                    _alertDialogState.update {
                        it.copy(show = true, message = error.toErrorMessage())
                    }
                }
                .onSuccess {
                    onSuccess()
                }
        }


    }


}