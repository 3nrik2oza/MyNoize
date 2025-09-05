package com.project.mynoize.activities.main.presentation.profile_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.mynoize.activities.main.presentation.main_screen.MainActivityUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ProfileScreenViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<MainActivityUiEvent>()
    val uiEvent = _uiEvent

    val auth = FirebaseAuth.getInstance()

    var username by mutableStateOf("")
        private set

    init{
        username = auth.currentUser?.displayName ?: "Greska"
    }

    fun signOut() {
        auth.signOut()

    }
    fun onEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.OnSignOutClick -> {
                signOut()

                viewModelScope.launch {
                    _uiEvent.emit(MainActivityUiEvent.NavigateToSignIn)
                }
            }
        }
    }
}