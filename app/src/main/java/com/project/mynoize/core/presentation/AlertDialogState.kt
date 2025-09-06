package com.project.mynoize.core.presentation

data class AlertDialogState (
    val show: Boolean = false,
    val loading: Boolean = false,
    val message: UiText? = null,
    val warning: Boolean = true
    )