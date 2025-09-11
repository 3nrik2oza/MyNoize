package com.project.mynoize.activities.main.presentation.main_screen

sealed class MainActivityUiEvent {
    object NavigateToSignIn : MainActivityUiEvent()
    object ShowNotification: MainActivityUiEvent()
}