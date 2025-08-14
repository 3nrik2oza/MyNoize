package com.project.mynoize.activities.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.project.mynoize.activities.signin.event.SignUpEvent
import kotlinx.coroutines.tasks.await

class SignUpViewModel (

): ViewModel(){

    val auth = FirebaseAuth.getInstance()

    var creatingAccount by mutableStateOf(false)

    var showAlertDialog by mutableStateOf(false)
        private set
    var messageText by mutableStateOf("")
        private set

    var username by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var repeatedPassword by mutableStateOf("")
        private set


    fun onEvent(event: SignUpEvent){
        when(event){
            is SignUpEvent.OnUsernameChange -> {
                username = event.username
            }
            is SignUpEvent.OnEmailChange -> {
                email = event.email.lowercase()
            }
            is SignUpEvent.OnPasswordChange -> {
                password = event.password
            }
            is SignUpEvent.OnRepeatedPasswordChange -> {
                repeatedPassword = event.repeatedPassword
            }
            is SignUpEvent.OnSignUpClick -> {
                createAccount()
            }
            is SignUpEvent.OnDismissAlertDialog -> {
                showAlertDialog = false
            }
        }
    }

    fun createAccount(){
        username = username.trim()
        email = email.trim()
        password = password.trim()
        repeatedPassword = repeatedPassword.trim()
        if(!checkInput().isEmpty()){
            messageText = checkInput()
            showAlertDialog = true
            return
        }
        creatingAccount = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val user = auth.currentUser
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user!!.updateProfile(profileUpdates)

                messageText = "Account has been created successfully"
                showAlertDialog = true
                auth.signOut()

            }
            .addOnFailureListener {
                creatingAccount = false
                messageText = "Something went wrong"
                showAlertDialog = true
            }

    }


    fun checkInput(): String{
        if(username.isEmpty()) return "Account has been created successfully"
        if(username.length > 30) return "Username is too long"

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Invalid email"

        if(password.isEmpty() || password.length < 8 || password.length > 30) return "Invalid password"

        if(repeatedPassword != password) return "Passwords don't match"

        return ""
    }



}