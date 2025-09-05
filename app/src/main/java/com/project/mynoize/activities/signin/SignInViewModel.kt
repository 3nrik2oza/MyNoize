package com.project.mynoize.activities.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.project.mynoize.core.data.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.project.mynoize.activities.signin.event.SignInEvent

class SignInViewModel: ViewModel() {

    val auth = FirebaseAuth.getInstance()

    var loading by mutableStateOf(false)
    var showAlertDialog by mutableStateOf(false)
    var messageText by mutableStateOf("")

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }
/*
    fun resetState(){
        _state.update { SignInState() }
    }*/

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.OnEmailChange -> {
                email = event.email.lowercase()
            }

            is SignInEvent.OnPasswordChange -> {
                password = event.password
            }
            is SignInEvent.OnSignInClick -> {
                signIn(event.onSuccess)
            }
            is SignInEvent.OnDismissAlertDialog -> {
                showAlertDialog = false
            }
        }
    }

    fun signIn(onSuccess: () -> Unit){
        loading = true
        email = email.trim()
        password = password.trim()
        val message = checkInput()

        if(message.isNotEmpty()){
            messageText = message
            showAlertDialog = true
            loading = false
            return
        }

        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                loading = false
                messageText = "Something went wrong"
                showAlertDialog = true
            }

    }

    fun checkInput(): String{
        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Invalid email"
        if(password.isEmpty() || password.length < 8 || password.length > 30) return "Invalid password"
        return ""

    }

}