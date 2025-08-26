package com.project.mynoize.activities.main.state

data class AlertDialogState (
    val show: Boolean = false,
    val loading: Boolean = false,
    val message: String = ""
    )